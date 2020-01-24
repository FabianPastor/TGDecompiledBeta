package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import org.telegram.messenger.MediaController.VideoConvertorListener;

public class MediaCodecVideoConvertor {
    private static final int MEDIACODEC_TIMEOUT_DEFAULT = 2500;
    private static final int MEDIACODEC_TIMEOUT_INCREASED = 22000;
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    VideoConvertorListener callback;
    MediaExtractor extractor;
    MP4Builder mediaMuxer;

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, long j, long j2, boolean z2, long j3, VideoConvertorListener videoConvertorListener) {
        String str2 = str;
        File file2 = file;
        int i6 = i;
        boolean z3 = z;
        int i7 = i2;
        int i8 = i3;
        int i9 = i4;
        int i10 = i5;
        long j4 = j;
        long j5 = j2;
        boolean z4 = z2;
        long j6 = j3;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file2, i6, z3, i7, i8, i9, i10, j4, j5, j6, z4, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:77:0x0147 A:{Catch:{ Exception -> 0x017d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0147 A:{Catch:{ Exception -> 0x017d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x034e  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x034c  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0a5c A:{Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0a20 A:{Catch:{ Exception -> 0x0a63, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0a20 A:{Catch:{ Exception -> 0x0a63, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0a5c A:{Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0902 A:{Catch:{ Exception -> 0x0888, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x08de A:{Catch:{ Exception -> 0x0888, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x090b A:{SYNTHETIC, Splitter:B:558:0x090b} */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0a5c A:{Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0a20 A:{Catch:{ Exception -> 0x0a63, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x081e  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0221  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x021a A:{SYNTHETIC, Splitter:B:124:0x021a} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0252 A:{SYNTHETIC, Splitter:B:136:0x0252} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0272 A:{SYNTHETIC, Splitter:B:149:0x0272} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0295 A:{SYNTHETIC, Splitter:B:160:0x0295} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02d8 A:{SYNTHETIC, Splitter:B:182:0x02d8} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02cb A:{SYNTHETIC, Splitter:B:178:0x02cb} */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x02f5 A:{SYNTHETIC, Splitter:B:191:0x02f5} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0432  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x031a A:{SYNTHETIC, Splitter:B:201:0x031a} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0465 A:{Catch:{ Exception -> 0x0b39, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x05ef  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0499 A:{SYNTHETIC, Splitter:B:287:0x0499} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0632  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0627  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x064f  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x081e  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x021a A:{SYNTHETIC, Splitter:B:124:0x021a} */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0221  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0252 A:{SYNTHETIC, Splitter:B:136:0x0252} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0272 A:{SYNTHETIC, Splitter:B:149:0x0272} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0295 A:{SYNTHETIC, Splitter:B:160:0x0295} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02cb A:{SYNTHETIC, Splitter:B:178:0x02cb} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02d8 A:{SYNTHETIC, Splitter:B:182:0x02d8} */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x02f5 A:{SYNTHETIC, Splitter:B:191:0x02f5} */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x031a A:{SYNTHETIC, Splitter:B:201:0x031a} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0432  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0465 A:{Catch:{ Exception -> 0x0b39, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x047c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0499 A:{SYNTHETIC, Splitter:B:287:0x0499} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x05ef  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0608 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0627  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0632  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x064f  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x081e  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0221  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x021a A:{SYNTHETIC, Splitter:B:124:0x021a} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x022b  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0252 A:{SYNTHETIC, Splitter:B:136:0x0252} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0272 A:{SYNTHETIC, Splitter:B:149:0x0272} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0295 A:{SYNTHETIC, Splitter:B:160:0x0295} */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02d8 A:{SYNTHETIC, Splitter:B:182:0x02d8} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02cb A:{SYNTHETIC, Splitter:B:178:0x02cb} */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0315  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x02f5 A:{SYNTHETIC, Splitter:B:191:0x02f5} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0432  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x031a A:{SYNTHETIC, Splitter:B:201:0x031a} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0465 A:{Catch:{ Exception -> 0x0b39, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x047c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x05ef  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0499 A:{SYNTHETIC, Splitter:B:287:0x0499} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0608 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0632  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0627  */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x064f  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x081e  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:700:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00d8 A:{SYNTHETIC, Splitter:B:43:0x00d8} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x0ccd  */
    /* JADX WARNING: Removed duplicated region for block: B:743:0x0cd4 A:{SYNTHETIC, Splitter:B:743:0x0cd4} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:727:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:727:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0c4d A:{ExcHandler: all (r0_75 'th' java.lang.Throwable), Splitter:B:91:0x0196} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bb8 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0bfa A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0bff A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0c0c A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }} */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0c3b A:{SYNTHETIC, Splitter:B:706:0x0c3b} */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0cc4 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0ca4  */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:27:0x0091, B:91:0x0196] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:34:0x00b2, B:91:0x0196] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:37:0x00bf, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:38:0x00c0, code skipped:
            r9 = r79;
            r4 = r80;
            r1 = r0;
            r8 = r13;
     */
    /* JADX WARNING: Missing block: B:39:0x00c8, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:40:0x00c9, code skipped:
            r15 = r77;
            r9 = r79;
            r4 = r80;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:143:0x0268, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:151:0x027f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:152:0x0280, code skipped:
            r10 = r1;
            r4 = r11;
     */
    /* JADX WARNING: Missing block: B:165:0x02a3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:166:0x02a4, code skipped:
            r10 = r1;
            r11 = r4;
            r8 = r13;
            r6 = null;
            r47 = null;
     */
    /* JADX WARNING: Missing block: B:167:0x02aa, code skipped:
            r56 = null;
     */
    /* JADX WARNING: Missing block: B:168:0x02ac, code skipped:
            r4 = r80;
     */
    /* JADX WARNING: Missing block: B:169:0x02b2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:170:0x02b3, code skipped:
            r4 = r80;
            r10 = r1;
            r8 = r13;
     */
    /* JADX WARNING: Missing block: B:180:0x02d1, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:181:0x02d2, code skipped:
            r4 = r80;
            r10 = r1;
            r8 = r13;
     */
    /* JADX WARNING: Missing block: B:197:0x030d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:198:0x030e, code skipped:
            r10 = r1;
            r47 = r4;
            r8 = r13;
            r11 = r19;
     */
    /* JADX WARNING: Missing block: B:231:0x03a4, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:232:0x03a7, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:233:0x03a8, code skipped:
            r11 = r81;
     */
    /* JADX WARNING: Missing block: B:243:0x03d0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:254:0x03f8, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:255:0x03fa, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:256:0x03fb, code skipped:
            r8 = r83;
     */
    /* JADX WARNING: Missing block: B:257:0x03fd, code skipped:
            r4 = r80;
            r10 = r1;
            r56 = r7;
            r8 = r13;
            r11 = r19;
     */
    /* JADX WARNING: Missing block: B:258:0x0407, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:259:0x0408, code skipped:
            r8 = r83;
     */
    /* JADX WARNING: Missing block: B:261:0x0423, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:262:0x0424, code skipped:
            r11 = r81;
            r8 = r83;
     */
    /* JADX WARNING: Missing block: B:263:0x0428, code skipped:
            r47 = r4;
     */
    /* JADX WARNING: Missing block: B:264:0x042a, code skipped:
            r4 = r80;
            r10 = r1;
            r8 = r13;
            r11 = r19;
     */
    /* JADX WARNING: Missing block: B:282:0x0487, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:283:0x0488, code skipped:
            r8 = r78;
            r10 = r1;
            r56 = r4;
            r11 = r19;
     */
    /* JADX WARNING: Missing block: B:303:0x04fa, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:329:0x0542, code skipped:
            if (r11.presentationTimeUs < r8) goto L_0x0544;
     */
    /* JADX WARNING: Missing block: B:348:0x059d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:349:0x059e, code skipped:
            r2 = r81;
     */
    /* JADX WARNING: Missing block: B:359:0x05d2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:360:0x05d3, code skipped:
            r8 = r78;
     */
    /* JADX WARNING: Missing block: B:361:0x05d5, code skipped:
            r4 = r80;
            r1 = r0;
            r11 = r19;
            r5 = r30;
     */
    /* JADX WARNING: Missing block: B:363:0x05e2, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:364:0x05e3, code skipped:
            r2 = r11;
     */
    /* JADX WARNING: Missing block: B:390:0x0669, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:391:0x066a, code skipped:
            r8 = r78;
     */
    /* JADX WARNING: Missing block: B:486:0x07fa, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:514:0x0888, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:576:0x093e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:577:0x0940, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:578:0x0941, code skipped:
            r7 = r19;
     */
    /* JADX WARNING: Missing block: B:579:0x0943, code skipped:
            r1 = r0;
            r47 = r4;
            r11 = r7;
            r5 = r30;
            r10 = r61;
            r4 = r80;
     */
    /* JADX WARNING: Missing block: B:591:0x09d7, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:592:0x09d8, code skipped:
            r64 = r3;
            r22 = r4;
            r68 = r6;
            r69 = r7;
            r10 = r61;
            r4 = r80;
            r1 = r0;
            r47 = r22;
     */
    /* JADX WARNING: Missing block: B:593:0x09ea, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:594:0x09eb, code skipped:
            r68 = r6;
            r69 = r19;
            r64 = r30;
            r22 = r47;
            r10 = r61;
            r4 = r80;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:595:0x09fa, code skipped:
            r5 = r64;
     */
    /* JADX WARNING: Missing block: B:605:0x0a2a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:606:0x0a2b, code skipped:
            r4 = r80;
            r1 = r0;
            r47 = r22;
            r5 = r64;
     */
    /* JADX WARNING: Missing block: B:621:0x0a63, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:622:0x0a64, code skipped:
            r3 = r64;
     */
    /* JADX WARNING: Missing block: B:625:0x0a8b, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:630:0x0abf, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:636:0x0b0e, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:637:0x0b0f, code skipped:
            r4 = r80;
            r1 = r0;
            r5 = r3;
            r47 = r22;
     */
    /* JADX WARNING: Missing block: B:638:0x0b15, code skipped:
            r6 = r68;
     */
    /* JADX WARNING: Missing block: B:639:0x0b18, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:640:0x0b19, code skipped:
            r8 = r78;
     */
    /* JADX WARNING: Missing block: B:641:0x0b1b, code skipped:
            r3 = r5;
            r68 = r6;
            r69 = r19;
            r22 = r47;
            r10 = r61;
     */
    /* JADX WARNING: Missing block: B:642:0x0b25, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:643:0x0b26, code skipped:
            r8 = r78;
     */
    /* JADX WARNING: Missing block: B:644:0x0b28, code skipped:
            r68 = r6;
            r69 = r19;
            r22 = r47;
            r10 = r61;
            r4 = r80;
            r1 = r0;
            r5 = r30;
     */
    /* JADX WARNING: Missing block: B:645:0x0b39, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:646:0x0b3a, code skipped:
            r10 = r1;
            r56 = r4;
            r3 = r5;
            r68 = r6;
            r8 = r13;
            r69 = r19;
            r22 = r47;
     */
    /* JADX WARNING: Missing block: B:647:0x0b45, code skipped:
            r4 = r80;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:648:0x0b4a, code skipped:
            r11 = r69;
     */
    /* JADX WARNING: Missing block: B:649:0x0b4e, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:650:0x0b4f, code skipped:
            r10 = r1;
            r22 = r4;
            r3 = r5;
            r68 = r6;
            r8 = r13;
            r4 = r80;
            r1 = r0;
            r47 = r22;
            r11 = r19;
     */
    /* JADX WARNING: Missing block: B:651:0x0b63, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:652:0x0b64, code skipped:
            r10 = r1;
            r22 = r4;
            r3 = r5;
            r68 = r6;
            r69 = r11;
            r8 = r13;
            r4 = r80;
            r1 = r0;
            r47 = r22;
     */
    /* JADX WARNING: Missing block: B:653:0x0b75, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:654:0x0b76, code skipped:
            r10 = r1;
            r3 = r5;
            r68 = r6;
            r69 = r11;
            r8 = r13;
            r4 = r80;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:655:0x0b83, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:656:0x0b84, code skipped:
            r10 = r1;
            r3 = r5;
            r69 = r11;
            r8 = r13;
            r4 = r80;
            r1 = r0;
            r6 = null;
     */
    /* JADX WARNING: Missing block: B:657:0x0b90, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:658:0x0b91, code skipped:
            r10 = r1;
            r3 = r5;
            r8 = r13;
            r4 = r80;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:659:0x0b9a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:660:0x0b9b, code skipped:
            r10 = r1;
            r80 = r11;
     */
    /* JADX WARNING: Missing block: B:661:0x0b9f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:662:0x0ba0, code skipped:
            r10 = r1;
            r8 = r13;
     */
    /* JADX WARNING: Missing block: B:663:0x0ba5, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:664:0x0ba6, code skipped:
            r10 = r1;
     */
    /* JADX WARNING: Missing block: B:665:0x0ba7, code skipped:
            r8 = r13;
            r4 = r80;
     */
    /* JADX WARNING: Missing block: B:666:0x0bac, code skipped:
            r1 = r0;
            r5 = null;
     */
    /* JADX WARNING: Missing block: B:667:0x0bae, code skipped:
            r6 = null;
            r11 = null;
     */
    /* JADX WARNING: Missing block: B:668:0x0bb0, code skipped:
            r47 = null;
     */
    /* JADX WARNING: Missing block: B:669:0x0bb2, code skipped:
            r56 = null;
     */
    /* JADX WARNING: Missing block: B:674:0x0bba, code skipped:
            r3 = true;
     */
    /* JADX WARNING: Missing block: B:694:0x0c1d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:695:0x0c1f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:696:0x0CLASSNAME, code skipped:
            r9 = r79;
     */
    /* JADX WARNING: Missing block: B:697:0x0CLASSNAME, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:698:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:699:0x0CLASSNAME, code skipped:
            r9 = r79;
     */
    /* JADX WARNING: Missing block: B:713:0x0c4d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:714:0x0c4e, code skipped:
            r2 = r0;
            r1 = r14;
     */
    /* JADX WARNING: Missing block: B:724:0x0c8f, code skipped:
            r1.release();
     */
    /* JADX WARNING: Missing block: B:728:?, code skipped:
            r1.finishMovie();
     */
    /* JADX WARNING: Missing block: B:729:0x0c9a, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:730:0x0c9b, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:740:0x0ccd, code skipped:
            r3.release();
     */
    /* JADX WARNING: Missing block: B:744:?, code skipped:
            r3.finishMovie();
     */
    /* JADX WARNING: Missing block: B:745:0x0cd8, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:746:0x0cd9, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    private boolean convertVideoInternal(java.lang.String r73, java.io.File r74, int r75, boolean r76, int r77, int r78, int r79, int r80, long r81, long r83, long r85, boolean r87, boolean r88) {
        /*
        r72 = this;
        r14 = r72;
        r13 = r73;
        r15 = r75;
        r12 = r77;
        r11 = r78;
        r9 = r79;
        r10 = r80;
        r7 = r81;
        r5 = r83;
        r2 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r2.<init>();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1.<init>();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r4 = r74;
        r1.setCacheFile(r4);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1.setRotation(r15);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1.setSize(r12, r11);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r3 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r3.<init>();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r15 = r76;
        r1 = r3.createMovie(r1, r15);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r14.mediaMuxer = r1;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1.<init>();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r14.extractor = r1;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1.setDataSource(r13);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r12 = r85;
        r1 = (float) r12;
        r3 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r18 = r1 / r3;
        r72.checkConversionCanceled();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r3 = 0;
        r1 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        r8 = -1;
        if (r10 == r8) goto L_0x0064;
    L_0x0054:
        r7 = r14.extractor;	 Catch:{ Exception -> 0x005c, all -> 0x0c4d }
        r8 = 1;
        r7 = org.telegram.messenger.MediaController.findTrack(r7, r8);	 Catch:{ Exception -> 0x005c, all -> 0x0c4d }
        goto L_0x0066;
    L_0x005c:
        r0 = move-exception;
        r15 = r77;
        r1 = r0;
        r4 = r10;
        r8 = r11;
        goto L_0x0c5b;
    L_0x0064:
        r8 = 1;
        r7 = -1;
    L_0x0066:
        r3 = "mime";
        r8 = "video/avc";
        if (r1 < 0) goto L_0x0089;
    L_0x006d:
        r20 = r2;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0081, all -> 0x0c4d }
        r2 = r2.getTrackFormat(r1);	 Catch:{ Exception -> 0x0081, all -> 0x0c4d }
        r2 = r2.getString(r3);	 Catch:{ Exception -> 0x0081, all -> 0x0c4d }
        r2 = r2.equals(r8);	 Catch:{ Exception -> 0x0081, all -> 0x0c4d }
        if (r2 != 0) goto L_0x008b;
    L_0x007f:
        r2 = 1;
        goto L_0x008c;
    L_0x0081:
        r0 = move-exception;
        r15 = r77;
        r1 = r0;
        r4 = r10;
    L_0x0086:
        r8 = r11;
        goto L_0x0c5a;
    L_0x0089:
        r20 = r2;
    L_0x008b:
        r2 = 0;
    L_0x008c:
        if (r87 != 0) goto L_0x00d1;
    L_0x008e:
        if (r2 == 0) goto L_0x0091;
    L_0x0090:
        goto L_0x00d1;
    L_0x0091:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x00c8, all -> 0x0c4d }
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x00c8, all -> 0x0c4d }
        r1 = -1;
        if (r10 == r1) goto L_0x009b;
    L_0x0098:
        r17 = 1;
        goto L_0x009d;
    L_0x009b:
        r17 = 0;
    L_0x009d:
        r1 = r72;
        r7 = r20;
        r8 = 0;
        r13 = 1;
        r4 = r7;
        r5 = r81;
        r12 = 0;
        r7 = r83;
        r9 = r85;
        r13 = r11;
        r11 = r74;
        r15 = r77;
        r12 = r17;
        r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12);	 Catch:{ Exception -> 0x00bf, all -> 0x0c4d }
        r9 = r79;
        r4 = r80;
        r8 = r13;
        r3 = 0;
        r67 = 0;
        goto L_0x0CLASSNAME;
    L_0x00bf:
        r0 = move-exception;
        r9 = r79;
        r4 = r80;
        r1 = r0;
        r8 = r13;
        goto L_0x0c5a;
    L_0x00c8:
        r0 = move-exception;
        r15 = r77;
        r9 = r79;
        r4 = r80;
        r1 = r0;
        goto L_0x0086;
    L_0x00d1:
        r15 = r77;
        r13 = r11;
        r9 = r20;
        if (r1 < 0) goto L_0x0CLASSNAME;
    L_0x00d8:
        r2 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r2 = r2.toLowerCase();	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r11 = 16;
        r12 = 2;
        r20 = 4;
        r10 = 18;
        if (r6 >= r10) goto L_0x018e;
    L_0x00e9:
        r6 = org.telegram.messenger.MediaController.selectCodec(r8);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r23 = org.telegram.messenger.MediaController.selectColorFormat(r6, r8);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r23 == 0) goto L_0x0175;
    L_0x00f3:
        r4 = r6.getName();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r5 = "OMX.qcom.";
        r5 = r4.contains(r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r5 == 0) goto L_0x0119;
    L_0x00ff:
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r4 != r11) goto L_0x0116;
    L_0x0103:
        r4 = "lge";
        r4 = r2.equals(r4);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r4 != 0) goto L_0x0113;
    L_0x010b:
        r4 = "nokia";
        r4 = r2.equals(r4);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r4 == 0) goto L_0x0116;
    L_0x0113:
        r4 = 1;
    L_0x0114:
        r5 = 1;
        goto L_0x0143;
    L_0x0116:
        r4 = 1;
    L_0x0117:
        r5 = 0;
        goto L_0x0143;
    L_0x0119:
        r5 = "OMX.Intel.";
        r5 = r4.contains(r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r5 == 0) goto L_0x0123;
    L_0x0121:
        r4 = 2;
        goto L_0x0117;
    L_0x0123:
        r5 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r5 == 0) goto L_0x012d;
    L_0x012b:
        r4 = 3;
        goto L_0x0117;
    L_0x012d:
        r5 = "OMX.SEC.AVC.Encoder";
        r5 = r4.equals(r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r5 == 0) goto L_0x0137;
    L_0x0135:
        r4 = 4;
        goto L_0x0114;
    L_0x0137:
        r5 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r4 = r4.equals(r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r4 == 0) goto L_0x0141;
    L_0x013f:
        r4 = 5;
        goto L_0x0117;
    L_0x0141:
        r4 = 0;
        goto L_0x0117;
    L_0x0143:
        r26 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r26 == 0) goto L_0x0171;
    L_0x0147:
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r10.<init>();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r11 = "codec = ";
        r10.append(r11);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r6 = r6.getName();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r10.append(r6);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r6 = " manufacturer = ";
        r10.append(r6);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r10.append(r2);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r6 = "device = ";
        r10.append(r6);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r6 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r10.append(r6);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r6 = r10.toString();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        org.telegram.messenger.FileLog.d(r6);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
    L_0x0171:
        r11 = r5;
        r10 = r23;
        goto L_0x0196;
    L_0x0175:
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r3 = "no supported color format";
        r2.<init>(r3);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        throw r2;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
    L_0x017d:
        r0 = move-exception;
        r4 = r80;
    L_0x0180:
        r10 = r1;
    L_0x0181:
        r8 = r13;
        r5 = 0;
    L_0x0183:
        r6 = 0;
        r11 = 0;
    L_0x0185:
        r47 = 0;
    L_0x0187:
        r56 = 0;
    L_0x0189:
        r67 = 0;
    L_0x018b:
        r1 = r0;
        goto L_0x0bb4;
    L_0x018e:
        r4 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r4 = 0;
        r10 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r11 = 0;
    L_0x0196:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        if (r5 == 0) goto L_0x01ae;
    L_0x019a:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r5.<init>();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r6 = "colorFormat = ";
        r5.append(r6);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r5.append(r10);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r5 = r5.toString();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        org.telegram.messenger.FileLog.d(r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
    L_0x01ae:
        r5 = r15 * r13;
        r6 = r5 * 3;
        r6 = r6 / r12;
        if (r4 != 0) goto L_0x01cc;
    L_0x01b5:
        r2 = r13 % 16;
        if (r2 == 0) goto L_0x0200;
    L_0x01b9:
        r2 = r13 % 16;
        r4 = 16;
        r2 = 16 - r2;
        r2 = r2 + r13;
        r2 = r2 - r13;
        r2 = r2 * r15;
        r4 = r2 * 5;
        r4 = r4 / 4;
    L_0x01c7:
        r6 = r6 + r4;
    L_0x01c8:
        r12 = r2;
        r34 = r6;
        goto L_0x0203;
    L_0x01cc:
        r12 = 1;
        if (r4 != r12) goto L_0x01e2;
    L_0x01cf:
        r2 = r2.toLowerCase();	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r4 = "lge";
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r2 != 0) goto L_0x0200;
    L_0x01db:
        r2 = r5 + 2047;
        r2 = r2 & -2048;
        r2 = r2 - r5;
        r6 = r6 + r2;
        goto L_0x01c8;
    L_0x01e2:
        r5 = 5;
        if (r4 != r5) goto L_0x01e6;
    L_0x01e5:
        goto L_0x0200;
    L_0x01e6:
        r5 = 3;
        if (r4 != r5) goto L_0x0200;
    L_0x01e9:
        r4 = "baidu";
        r2 = r2.equals(r4);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        if (r2 == 0) goto L_0x0200;
    L_0x01f1:
        r2 = r13 % 16;
        r4 = 16;
        r2 = 16 - r2;
        r2 = r2 + r13;
        r2 = r2 - r13;
        r2 = r2 * r15;
        r4 = r2 * 5;
        r4 = r4 / 4;
        goto L_0x01c7;
    L_0x0200:
        r34 = r6;
        r12 = 0;
    L_0x0203:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r2.selectTrack(r1);	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r2 = r2.getTrackFormat(r1);	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r5 = 0;
        r35 = r11;
        r36 = r12;
        r11 = r81;
        r4 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r4 <= 0) goto L_0x0221;
    L_0x021a:
        r4 = r14.extractor;	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        r5 = 0;
        r4.seekTo(r11, r5);	 Catch:{ Exception -> 0x017d, all -> 0x0c4d }
        goto L_0x0229;
    L_0x0221:
        r5 = 0;
        r4 = r14.extractor;	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
        r11 = 0;
        r4.seekTo(r11, r5);	 Catch:{ Exception -> 0x0ba5, all -> 0x0c4d }
    L_0x0229:
        if (r80 > 0) goto L_0x022f;
    L_0x022b:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x0231;
    L_0x022f:
        r4 = r80;
    L_0x0231:
        r6 = android.media.MediaFormat.createVideoFormat(r8, r15, r13);	 Catch:{ Exception -> 0x0b9f, all -> 0x0c4d }
        r11 = "color-format";
        r6.setInteger(r11, r10);	 Catch:{ Exception -> 0x0b9f, all -> 0x0c4d }
        r11 = "bitrate";
        r6.setInteger(r11, r4);	 Catch:{ Exception -> 0x0b9f, all -> 0x0c4d }
        r11 = "frame-rate";
        r12 = r79;
        r6.setInteger(r11, r12);	 Catch:{ Exception -> 0x0b9f, all -> 0x0c4d }
        r11 = "i-frame-interval";
        r5 = 2;
        r6.setInteger(r11, r5);	 Catch:{ Exception -> 0x0b9f, all -> 0x0c4d }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b9f, all -> 0x0c4d }
        r11 = 23;
        if (r5 >= r11) goto L_0x026b;
    L_0x0252:
        r5 = java.lang.Math.min(r13, r15);	 Catch:{ Exception -> 0x0268, all -> 0x0c4d }
        r11 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r5 > r11) goto L_0x026b;
    L_0x025a:
        r5 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        if (r4 <= r5) goto L_0x0262;
    L_0x025f:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x0262:
        r5 = "bitrate";
        r6.setInteger(r5, r4);	 Catch:{ Exception -> 0x0268, all -> 0x0c4d }
        goto L_0x026b;
    L_0x0268:
        r0 = move-exception;
        goto L_0x0180;
    L_0x026b:
        r11 = r4;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b9a, all -> 0x0c4d }
        r5 = 18;
        if (r4 >= r5) goto L_0x0284;
    L_0x0272:
        r4 = "stride";
        r5 = r15 + 32;
        r6.setInteger(r4, r5);	 Catch:{ Exception -> 0x027f, all -> 0x0c4d }
        r4 = "slice-height";
        r6.setInteger(r4, r13);	 Catch:{ Exception -> 0x027f, all -> 0x0c4d }
        goto L_0x0284;
    L_0x027f:
        r0 = move-exception;
        r10 = r1;
        r4 = r11;
        goto L_0x0181;
    L_0x0284:
        r5 = android.media.MediaCodec.createEncoderByType(r8);	 Catch:{ Exception -> 0x0b9a, all -> 0x0c4d }
        r80 = r11;
        r4 = 0;
        r11 = 1;
        r5.configure(r6, r4, r4, r11);	 Catch:{ Exception -> 0x0b90, all -> 0x0c4d }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b90, all -> 0x0c4d }
        r6 = 18;
        if (r4 < r6) goto L_0x02b9;
    L_0x0295:
        r4 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x02b2, all -> 0x0c4d }
        r6 = r5.createInputSurface();	 Catch:{ Exception -> 0x02b2, all -> 0x0c4d }
        r4.<init>(r6);	 Catch:{ Exception -> 0x02b2, all -> 0x0c4d }
        r4.makeCurrent();	 Catch:{ Exception -> 0x02a3, all -> 0x0c4d }
        r11 = r4;
        goto L_0x02ba;
    L_0x02a3:
        r0 = move-exception;
        r10 = r1;
        r11 = r4;
        r8 = r13;
        r6 = 0;
        r47 = 0;
    L_0x02aa:
        r56 = 0;
    L_0x02ac:
        r67 = 0;
        r4 = r80;
        goto L_0x018b;
    L_0x02b2:
        r0 = move-exception;
        r4 = r80;
        r10 = r1;
        r8 = r13;
        goto L_0x0183;
    L_0x02b9:
        r11 = 0;
    L_0x02ba:
        r5.start();	 Catch:{ Exception -> 0x0b83, all -> 0x0c4d }
        r4 = r2.getString(r3);	 Catch:{ Exception -> 0x0b83, all -> 0x0c4d }
        r6 = android.media.MediaCodec.createDecoderByType(r4);	 Catch:{ Exception -> 0x0b83, all -> 0x0c4d }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b75, all -> 0x0c4d }
        r12 = 18;
        if (r4 < r12) goto L_0x02d8;
    L_0x02cb:
        r4 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x02d1, all -> 0x0c4d }
        r4.<init>();	 Catch:{ Exception -> 0x02d1, all -> 0x0c4d }
        goto L_0x02df;
    L_0x02d1:
        r0 = move-exception;
        r4 = r80;
        r10 = r1;
        r8 = r13;
        goto L_0x0185;
    L_0x02d8:
        r4 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0b75, all -> 0x0c4d }
        r12 = r75;
        r4.<init>(r15, r13, r12);	 Catch:{ Exception -> 0x0b75, all -> 0x0c4d }
    L_0x02df:
        r12 = r4.getSurface();	 Catch:{ Exception -> 0x0b63, all -> 0x0c4d }
        r44 = r10;
        r19 = r11;
        r10 = 0;
        r11 = 0;
        r6.configure(r2, r12, r10, r11);	 Catch:{ Exception -> 0x0b4e, all -> 0x0c4d }
        r6.start();	 Catch:{ Exception -> 0x0b4e, all -> 0x0c4d }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b4e, all -> 0x0c4d }
        r11 = 21;
        if (r2 >= r11) goto L_0x0315;
    L_0x02f5:
        r2 = r6.getInputBuffers();	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r12 = r5.getOutputBuffers();	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r11 = 18;
        if (r10 >= r11) goto L_0x030a;
    L_0x0303:
        r10 = r5.getInputBuffers();	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r11 = r10;
        r10 = r2;
        goto L_0x0318;
    L_0x030a:
        r10 = r2;
        r11 = 0;
        goto L_0x0318;
    L_0x030d:
        r0 = move-exception;
        r10 = r1;
        r47 = r4;
        r8 = r13;
        r11 = r19;
        goto L_0x02aa;
    L_0x0315:
        r10 = 0;
        r11 = 0;
        r12 = 0;
    L_0x0318:
        if (r7 < 0) goto L_0x0432;
    L_0x031a:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0423, all -> 0x0c4d }
        r2 = r2.getTrackFormat(r7);	 Catch:{ Exception -> 0x0423, all -> 0x0c4d }
        r30 = r7;
        r7 = r2.getString(r3);	 Catch:{ Exception -> 0x0423, all -> 0x0c4d }
        r31 = r12;
        r12 = "audio/mp4a-latm";
        r7 = r7.equals(r12);	 Catch:{ Exception -> 0x0423, all -> 0x0c4d }
        if (r7 != 0) goto L_0x033f;
    L_0x0330:
        r7 = r2.getString(r3);	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r12 = "audio/mpeg";
        r7 = r7.equals(r12);	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        if (r7 == 0) goto L_0x033d;
    L_0x033c:
        goto L_0x033f;
    L_0x033d:
        r7 = 0;
        goto L_0x0340;
    L_0x033f:
        r7 = 1;
    L_0x0340:
        r3 = r2.getString(r3);	 Catch:{ Exception -> 0x0423, all -> 0x0c4d }
        r12 = "audio/unknown";
        r3 = r3.equals(r12);	 Catch:{ Exception -> 0x0423, all -> 0x0c4d }
        if (r3 == 0) goto L_0x034e;
    L_0x034c:
        r3 = -1;
        goto L_0x0350;
    L_0x034e:
        r3 = r30;
    L_0x0350:
        if (r3 < 0) goto L_0x040b;
    L_0x0352:
        if (r7 == 0) goto L_0x03ac;
    L_0x0354:
        r12 = r14.mediaMuxer;	 Catch:{ Exception -> 0x03a7, all -> 0x0c4d }
        r32 = r7;
        r7 = 1;
        r12 = r12.addTrack(r2, r7);	 Catch:{ Exception -> 0x03a7, all -> 0x0c4d }
        r7 = r14.extractor;	 Catch:{ Exception -> 0x03a7, all -> 0x0c4d }
        r7.selectTrack(r3);	 Catch:{ Exception -> 0x03a7, all -> 0x0c4d }
        r7 = "max-input-size";
        r2 = r2.getInteger(r7);	 Catch:{ Exception -> 0x03a7, all -> 0x0c4d }
        r2 = java.nio.ByteBuffer.allocateDirect(r2);	 Catch:{ Exception -> 0x03a7, all -> 0x0c4d }
        r45 = r11;
        r7 = r12;
        r27 = 0;
        r11 = r81;
        r30 = (r11 > r27 ? 1 : (r11 == r27 ? 0 : -1));
        if (r30 <= 0) goto L_0x0386;
    L_0x0377:
        r30 = r2;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r33 = r7;
        r7 = 0;
        r2.seekTo(r11, r7);	 Catch:{ Exception -> 0x030d, all -> 0x0c4d }
        r47 = r4;
        r46 = r8;
        goto L_0x0396;
    L_0x0386:
        r30 = r2;
        r33 = r7;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x03a4, all -> 0x0c4d }
        r47 = r4;
        r46 = r8;
        r4 = 0;
        r7 = 0;
        r2.seekTo(r7, r4);	 Catch:{ Exception -> 0x03d0, all -> 0x0c4d }
    L_0x0396:
        r7 = r3;
        r48 = r9;
        r2 = r30;
        r49 = r32;
        r3 = r33;
        r4 = 0;
        r8 = r83;
        goto L_0x0447;
    L_0x03a4:
        r0 = move-exception;
        goto L_0x0428;
    L_0x03a7:
        r0 = move-exception;
        r11 = r81;
        goto L_0x0428;
    L_0x03ac:
        r47 = r4;
        r32 = r7;
        r46 = r8;
        r45 = r11;
        r11 = r81;
        r4 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
        r4.<init>();	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
        r8 = r73;
        r4.setDataSource(r8);	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
        r4.selectTrack(r3);	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
        r7 = 0;
        r27 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1));
        if (r27 <= 0) goto L_0x03d3;
    L_0x03c9:
        r7 = 0;
        r4.seekTo(r11, r7);	 Catch:{ Exception -> 0x03d0, all -> 0x0c4d }
        r48 = r9;
        goto L_0x03da;
    L_0x03d0:
        r0 = move-exception;
        goto L_0x042a;
    L_0x03d3:
        r48 = r9;
        r8 = r7;
        r7 = 0;
        r4.seekTo(r8, r7);	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
    L_0x03da:
        r7 = new org.telegram.messenger.video.AudioRecoder;	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
        r7.<init>(r2, r4, r3);	 Catch:{ Exception -> 0x0407, all -> 0x0c4d }
        r7.startTime = r11;	 Catch:{ Exception -> 0x03fa, all -> 0x0c4d }
        r8 = r83;
        r7.endTime = r8;	 Catch:{ Exception -> 0x03f8, all -> 0x0c4d }
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x03f8, all -> 0x0c4d }
        r4 = r7.format;	 Catch:{ Exception -> 0x03f8, all -> 0x0c4d }
        r30 = r3;
        r3 = 1;
        r2 = r2.addTrack(r4, r3);	 Catch:{ Exception -> 0x03f8, all -> 0x0c4d }
        r3 = r2;
        r4 = r7;
        r7 = r30;
        r49 = r32;
        r2 = 0;
        goto L_0x0447;
    L_0x03f8:
        r0 = move-exception;
        goto L_0x03fd;
    L_0x03fa:
        r0 = move-exception;
        r8 = r83;
    L_0x03fd:
        r4 = r80;
        r10 = r1;
        r56 = r7;
        r8 = r13;
        r11 = r19;
        goto L_0x0189;
    L_0x0407:
        r0 = move-exception;
        r8 = r83;
        goto L_0x042a;
    L_0x040b:
        r30 = r3;
        r47 = r4;
        r32 = r7;
        r46 = r8;
        r48 = r9;
        r45 = r11;
        r11 = r81;
        r8 = r83;
        r7 = r30;
        r49 = r32;
        r2 = 0;
        r3 = -5;
        r4 = 0;
        goto L_0x0447;
    L_0x0423:
        r0 = move-exception;
        r11 = r81;
        r8 = r83;
    L_0x0428:
        r47 = r4;
    L_0x042a:
        r4 = r80;
        r10 = r1;
        r8 = r13;
        r11 = r19;
        goto L_0x0187;
    L_0x0432:
        r47 = r4;
        r30 = r7;
        r46 = r8;
        r48 = r9;
        r45 = r11;
        r31 = r12;
        r11 = r81;
        r8 = r83;
        r2 = 0;
        r3 = -5;
        r4 = 0;
        r49 = 1;
    L_0x0447:
        if (r7 >= 0) goto L_0x044c;
    L_0x0449:
        r30 = 1;
        goto L_0x044e;
    L_0x044c:
        r30 = 0;
    L_0x044e:
        r72.checkConversionCanceled();	 Catch:{ Exception -> 0x0b39, all -> 0x0c4d }
        r33 = r31;
        r24 = 0;
        r25 = 0;
        r31 = 0;
        r32 = -5;
        r50 = 0;
        r51 = 1;
        r52 = -1;
        r54 = 0;
    L_0x0463:
        if (r24 == 0) goto L_0x0477;
    L_0x0465:
        if (r49 != 0) goto L_0x046a;
    L_0x0467:
        if (r30 != 0) goto L_0x046a;
    L_0x0469:
        goto L_0x0477;
    L_0x046a:
        r9 = r79;
        r10 = r1;
        r56 = r4;
        r8 = r13;
        r3 = 0;
        r21 = 0;
        r4 = r80;
        goto L_0x0bf3;
    L_0x0477:
        r72.checkConversionCanceled();	 Catch:{ Exception -> 0x0b39, all -> 0x0c4d }
        if (r49 != 0) goto L_0x0491;
    L_0x047c:
        if (r4 == 0) goto L_0x0491;
    L_0x047e:
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0487, all -> 0x0c4d }
        r13 = r4.step(r13, r3);	 Catch:{ Exception -> 0x0487, all -> 0x0c4d }
        r56 = r4;
        goto L_0x0495;
    L_0x0487:
        r0 = move-exception;
        r8 = r78;
        r10 = r1;
        r56 = r4;
        r11 = r19;
        goto L_0x02ac;
    L_0x0491:
        r56 = r4;
        r13 = r30;
    L_0x0495:
        r30 = r5;
        if (r25 != 0) goto L_0x05ef;
    L_0x0499:
        r4 = r14.extractor;	 Catch:{ Exception -> 0x05e2, all -> 0x0c4d }
        r4 = r4.getSampleTrackIndex();	 Catch:{ Exception -> 0x05e2, all -> 0x0c4d }
        if (r4 != r1) goto L_0x04fd;
    L_0x04a1:
        r11 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r4 = r6.dequeueInputBuffer(r11);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        if (r4 < 0) goto L_0x04e7;
    L_0x04a9:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r11 = 21;
        if (r5 >= r11) goto L_0x04b2;
    L_0x04af:
        r5 = r10[r4];	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        goto L_0x04b6;
    L_0x04b2:
        r5 = r6.getInputBuffer(r4);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
    L_0x04b6:
        r11 = r14.extractor;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r12 = 0;
        r40 = r11.readSampleData(r5, r12);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        if (r40 >= 0) goto L_0x04d1;
    L_0x04bf:
        r39 = 0;
        r40 = 0;
        r41 = 0;
        r43 = 4;
        r37 = r6;
        r38 = r4;
        r37.queueInputBuffer(r38, r39, r40, r41, r43);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r25 = 1;
        goto L_0x04e7;
    L_0x04d1:
        r39 = 0;
        r5 = r14.extractor;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r41 = r5.getSampleTime();	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r43 = 0;
        r37 = r6;
        r38 = r4;
        r37.queueInputBuffer(r38, r39, r40, r41, r43);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r4 = r14.extractor;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r4.advance();	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
    L_0x04e7:
        r61 = r1;
        r60 = r2;
        r59 = r3;
        r62 = r7;
        r4 = r25;
        r11 = r48;
        r1 = 0;
        r2 = r81;
        r48 = r13;
        goto L_0x05b9;
    L_0x04fa:
        r0 = move-exception;
        goto L_0x05e4;
    L_0x04fd:
        if (r49 == 0) goto L_0x05a1;
    L_0x04ff:
        r5 = -1;
        if (r7 == r5) goto L_0x05a1;
    L_0x0502:
        if (r4 != r7) goto L_0x05a1;
    L_0x0504:
        r4 = r14.extractor;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r5 = 0;
        r4 = r4.readSampleData(r2, r5);	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r11 = r48;
        r11.size = r4;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r12 = 21;
        if (r4 >= r12) goto L_0x051d;
    L_0x0515:
        r2.position(r5);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r4 = r11.size;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r2.limit(r4);	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
    L_0x051d:
        r4 = r11.size;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        if (r4 < 0) goto L_0x052f;
    L_0x0521:
        r4 = r14.extractor;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r4 = r4.getSampleTime();	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r11.presentationTimeUs = r4;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r4 = r14.extractor;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r4.advance();	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        goto L_0x0534;
    L_0x052f:
        r4 = 0;
        r11.size = r4;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r25 = 1;
    L_0x0534:
        r4 = r11.size;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        if (r4 <= 0) goto L_0x0592;
    L_0x0538:
        r4 = 0;
        r12 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r12 < 0) goto L_0x0544;
    L_0x053e:
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r12 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r12 >= 0) goto L_0x0592;
    L_0x0544:
        r4 = 0;
        r11.offset = r4;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r5 = r14.extractor;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r5 = r5.getSampleFlags();	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r11.flags = r5;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r5 = r14.mediaMuxer;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r48 = r13;
        r12 = r5.writeSampleData(r3, r2, r11, r4);	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r4 = 0;
        r37 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1));
        if (r37 == 0) goto L_0x0587;
    L_0x055d:
        r4 = r14.callback;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        if (r4 == 0) goto L_0x0587;
    L_0x0561:
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x059d, all -> 0x0c4d }
        r60 = r2;
        r59 = r3;
        r2 = r81;
        r4 = r4 - r2;
        r37 = (r4 > r54 ? 1 : (r4 == r54 ? 0 : -1));
        if (r37 <= 0) goto L_0x0572;
    L_0x056e:
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x04fa, all -> 0x0c4d }
        r54 = r4 - r2;
    L_0x0572:
        r61 = r1;
        r4 = r54;
        r1 = r14.callback;	 Catch:{ Exception -> 0x05d2, all -> 0x0c4d }
        r62 = r7;
        r7 = (float) r4;	 Catch:{ Exception -> 0x05d2, all -> 0x0c4d }
        r37 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r7 = r7 / r37;
        r7 = r7 / r18;
        r1.didWriteData(r12, r7);	 Catch:{ Exception -> 0x05d2, all -> 0x0c4d }
        r54 = r4;
        goto L_0x05b6;
    L_0x0587:
        r61 = r1;
        r60 = r2;
        r59 = r3;
        r62 = r7;
    L_0x058f:
        r2 = r81;
        goto L_0x05b6;
    L_0x0592:
        r61 = r1;
        r60 = r2;
        r59 = r3;
        r62 = r7;
        r48 = r13;
        goto L_0x058f;
    L_0x059d:
        r0 = move-exception;
        r2 = r81;
        goto L_0x05e4;
    L_0x05a1:
        r61 = r1;
        r60 = r2;
        r59 = r3;
        r62 = r7;
        r11 = r48;
        r2 = r81;
        r48 = r13;
        r1 = -1;
        if (r4 != r1) goto L_0x05b6;
    L_0x05b2:
        r4 = r25;
        r1 = 1;
        goto L_0x05b9;
    L_0x05b6:
        r4 = r25;
        r1 = 0;
    L_0x05b9:
        if (r1 == 0) goto L_0x05fe;
    L_0x05bb:
        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r38 = r6.dequeueInputBuffer(r12);	 Catch:{ Exception -> 0x05d2, all -> 0x0c4d }
        if (r38 < 0) goto L_0x05fe;
    L_0x05c3:
        r39 = 0;
        r40 = 0;
        r41 = 0;
        r43 = 4;
        r37 = r6;
        r37.queueInputBuffer(r38, r39, r40, r41, r43);	 Catch:{ Exception -> 0x05d2, all -> 0x0c4d }
        r4 = 1;
        goto L_0x05fe;
    L_0x05d2:
        r0 = move-exception;
        r8 = r78;
    L_0x05d5:
        r4 = r80;
        r1 = r0;
        r11 = r19;
        r5 = r30;
    L_0x05dc:
        r10 = r61;
        r67 = 0;
        goto L_0x0bb4;
    L_0x05e2:
        r0 = move-exception;
        r2 = r11;
    L_0x05e4:
        r8 = r78;
        r4 = r80;
        r10 = r1;
        r11 = r19;
        r5 = r30;
        goto L_0x0189;
    L_0x05ef:
        r61 = r1;
        r60 = r2;
        r59 = r3;
        r62 = r7;
        r2 = r11;
        r11 = r48;
        r48 = r13;
        r4 = r25;
    L_0x05fe:
        r1 = r31 ^ 1;
        r12 = r1;
        r25 = r4;
        r4 = r32;
        r1 = 1;
    L_0x0606:
        if (r12 != 0) goto L_0x0622;
    L_0x0608:
        if (r1 == 0) goto L_0x060b;
    L_0x060a:
        goto L_0x0622;
    L_0x060b:
        r13 = r78;
        r32 = r4;
        r5 = r30;
        r30 = r48;
        r4 = r56;
        r1 = r61;
        r7 = r62;
        r48 = r11;
        r11 = r2;
        r3 = r59;
        r2 = r60;
        goto L_0x0463;
    L_0x0622:
        r72.checkConversionCanceled();	 Catch:{ Exception -> 0x0b25, all -> 0x0c4d }
        if (r88 == 0) goto L_0x0632;
    L_0x0627:
        r37 = 22000; // 0x55f0 float:3.0829E-41 double:1.08694E-319;
        r5 = r30;
        r70 = r37;
        r37 = r12;
        r12 = r70;
        goto L_0x0638;
    L_0x0632:
        r37 = r12;
        r5 = r30;
        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x0638:
        r7 = r5.dequeueOutputBuffer(r11, r12);	 Catch:{ Exception -> 0x0b18, all -> 0x0c4d }
        r12 = -1;
        if (r7 != r12) goto L_0x064f;
    L_0x063f:
        r8 = r78;
        r12 = r4;
        r63 = r10;
        r17 = r33;
        r9 = r46;
        r4 = -1;
        r10 = 3;
        r13 = 0;
    L_0x064b:
        r23 = 2;
        goto L_0x081c;
    L_0x064f:
        r12 = -3;
        if (r7 != r12) goto L_0x0673;
    L_0x0652:
        r12 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = 21;
        if (r12 >= r13) goto L_0x065c;
    L_0x0658:
        r33 = r5.getOutputBuffers();	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
    L_0x065c:
        r8 = r78;
        r13 = r1;
        r12 = r4;
        r63 = r10;
    L_0x0662:
        r17 = r33;
        r9 = r46;
        r4 = -1;
        r10 = 3;
        goto L_0x064b;
    L_0x0669:
        r0 = move-exception;
        r8 = r78;
    L_0x066c:
        r4 = r80;
        r1 = r0;
        r11 = r19;
        goto L_0x05dc;
    L_0x0673:
        r12 = -2;
        if (r7 != r12) goto L_0x06bb;
    L_0x0676:
        r12 = r5.getOutputFormat();	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = -5;
        if (r4 != r13) goto L_0x06b1;
    L_0x067d:
        if (r12 == 0) goto L_0x06b1;
    L_0x067f:
        r4 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = 0;
        r4 = r4.addTrack(r12, r13);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = "prepend-sps-pps-to-idr-frames";
        r13 = r12.containsKey(r13);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r13 == 0) goto L_0x06b1;
    L_0x068e:
        r13 = "prepend-sps-pps-to-idr-frames";
        r13 = r12.getInteger(r13);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r30 = r1;
        r1 = 1;
        if (r13 != r1) goto L_0x06b3;
    L_0x0699:
        r1 = "csd-0";
        r1 = r12.getByteBuffer(r1);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = "csd-1";
        r12 = r12.getByteBuffer(r13);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1 = r1.limit();	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r12.limit();	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1 = r1 + r12;
        r50 = r1;
        goto L_0x06b3;
    L_0x06b1:
        r30 = r1;
    L_0x06b3:
        r8 = r78;
        r12 = r4;
        r63 = r10;
        r13 = r30;
        goto L_0x0662;
    L_0x06bb:
        r30 = r1;
        if (r7 < 0) goto L_0x0aea;
    L_0x06bf:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b18, all -> 0x0c4d }
        r12 = 21;
        if (r1 >= r12) goto L_0x06c8;
    L_0x06c5:
        r1 = r33[r7];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        goto L_0x06cc;
    L_0x06c8:
        r1 = r5.getOutputBuffer(r7);	 Catch:{ Exception -> 0x0b18, all -> 0x0c4d }
    L_0x06cc:
        if (r1 == 0) goto L_0x0ac1;
    L_0x06ce:
        r13 = r11.size;	 Catch:{ Exception -> 0x0b18, all -> 0x0c4d }
        r12 = 1;
        if (r13 <= r12) goto L_0x07fd;
    L_0x06d3:
        r13 = r11.flags;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r23 = 2;
        r13 = r13 & 2;
        if (r13 != 0) goto L_0x077e;
    L_0x06db:
        if (r50 == 0) goto L_0x06ee;
    L_0x06dd:
        r13 = r11.flags;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = r13 & r12;
        if (r13 == 0) goto L_0x06ee;
    L_0x06e2:
        r12 = r11.offset;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r12 + r50;
        r11.offset = r12;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r12 - r50;
        r11.size = r12;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
    L_0x06ee:
        if (r51 == 0) goto L_0x0749;
    L_0x06f0:
        r12 = r11.flags;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = 1;
        r12 = r12 & r13;
        if (r12 == 0) goto L_0x0749;
    L_0x06f6:
        r12 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = 100;
        if (r12 <= r13) goto L_0x0744;
    L_0x06fc:
        r12 = r11.offset;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1.position(r12);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = 100;
        r12 = new byte[r12];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1.get(r12);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r63 = r10;
        r13 = 0;
        r24 = 0;
    L_0x070d:
        r10 = r12.length;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r10 = r10 + -4;
        if (r13 >= r10) goto L_0x0746;
    L_0x0712:
        r10 = r12[r13];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r10 != 0) goto L_0x073d;
    L_0x0716:
        r10 = r13 + 1;
        r10 = r12[r10];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r10 != 0) goto L_0x073d;
    L_0x071c:
        r10 = r13 + 2;
        r10 = r12[r10];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r10 != 0) goto L_0x073d;
    L_0x0722:
        r10 = r13 + 3;
        r10 = r12[r10];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r32 = r12;
        r12 = 1;
        if (r10 != r12) goto L_0x073f;
    L_0x072b:
        r10 = r24 + 1;
        if (r10 <= r12) goto L_0x073a;
    L_0x072f:
        r10 = r11.offset;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r10 = r10 + r13;
        r11.offset = r10;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r10 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r10 = r10 - r13;
        r11.size = r10;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        goto L_0x0746;
    L_0x073a:
        r24 = r10;
        goto L_0x073f;
    L_0x073d:
        r32 = r12;
    L_0x073f:
        r13 = r13 + 1;
        r12 = r32;
        goto L_0x070d;
    L_0x0744:
        r63 = r10;
    L_0x0746:
        r51 = 0;
        goto L_0x074b;
    L_0x0749:
        r63 = r10;
    L_0x074b:
        r10 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = 1;
        r8 = r10.writeSampleData(r4, r1, r11, r12);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = 0;
        r1 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
        if (r1 == 0) goto L_0x0777;
    L_0x0758:
        r1 = r14.callback;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r1 == 0) goto L_0x0777;
    L_0x075c:
        r12 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r12 - r2;
        r1 = (r12 > r54 ? 1 : (r12 == r54 ? 0 : -1));
        if (r1 <= 0) goto L_0x0767;
    L_0x0763:
        r12 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r54 = r12 - r2;
    L_0x0767:
        r12 = r54;
        r1 = r14.callback;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r10 = (float) r12;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r24 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r10 = r10 / r24;
        r10 = r10 / r18;
        r1.didWriteData(r8, r10);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r54 = r12;
    L_0x0777:
        r8 = r78;
        r9 = r46;
        r10 = 3;
        goto L_0x0806;
    L_0x077e:
        r63 = r10;
        r8 = -5;
        if (r4 != r8) goto L_0x0777;
    L_0x0783:
        r4 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r4 = new byte[r4];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r9 = r11.offset;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r10 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r9 = r9 + r10;
        r1.limit(r9);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r9 = r11.offset;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1.position(r9);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1.get(r4);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r1 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r9 = 1;
        r1 = r1 - r9;
    L_0x079b:
        r10 = 3;
        if (r1 < 0) goto L_0x07d9;
    L_0x079e:
        if (r1 <= r10) goto L_0x07d9;
    L_0x07a0:
        r12 = r4[r1];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r12 != r9) goto L_0x07d4;
    L_0x07a4:
        r9 = r1 + -1;
        r9 = r4[r9];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r9 != 0) goto L_0x07d4;
    L_0x07aa:
        r9 = r1 + -2;
        r9 = r4[r9];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r9 != 0) goto L_0x07d4;
    L_0x07b0:
        r9 = r1 + -3;
        r12 = r4[r9];	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        if (r12 != 0) goto L_0x07d4;
    L_0x07b6:
        r1 = java.nio.ByteBuffer.allocate(r9);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r12 = r12 - r9;
        r12 = java.nio.ByteBuffer.allocate(r12);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r13 = 0;
        r8 = r1.put(r4, r13, r9);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r8.position(r13);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r8 = r11.size;	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r8 = r8 - r9;
        r4 = r12.put(r4, r9, r8);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        r4.position(r13);	 Catch:{ Exception -> 0x0669, all -> 0x0c4d }
        goto L_0x07db;
    L_0x07d4:
        r1 = r1 + -1;
        r8 = -5;
        r9 = 1;
        goto L_0x079b;
    L_0x07d9:
        r1 = 0;
        r12 = 0;
    L_0x07db:
        r8 = r78;
        r9 = r46;
        r4 = android.media.MediaFormat.createVideoFormat(r9, r15, r8);	 Catch:{ Exception -> 0x07fa, all -> 0x0c4d }
        if (r1 == 0) goto L_0x07f1;
    L_0x07e5:
        if (r12 == 0) goto L_0x07f1;
    L_0x07e7:
        r13 = "csd-0";
        r4.setByteBuffer(r13, r1);	 Catch:{ Exception -> 0x07fa, all -> 0x0c4d }
        r1 = "csd-1";
        r4.setByteBuffer(r1, r12);	 Catch:{ Exception -> 0x07fa, all -> 0x0c4d }
    L_0x07f1:
        r1 = r14.mediaMuxer;	 Catch:{ Exception -> 0x07fa, all -> 0x0c4d }
        r12 = 0;
        r1 = r1.addTrack(r4, r12);	 Catch:{ Exception -> 0x07fa, all -> 0x0c4d }
        r4 = r1;
        goto L_0x0806;
    L_0x07fa:
        r0 = move-exception;
        goto L_0x066c;
    L_0x07fd:
        r8 = r78;
        r63 = r10;
        r9 = r46;
        r10 = 3;
        r23 = 2;
    L_0x0806:
        r1 = r11.flags;	 Catch:{ Exception -> 0x0abf, all -> 0x0c4d }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x0810;
    L_0x080c:
        r1 = 0;
        r24 = 1;
        goto L_0x0813;
    L_0x0810:
        r1 = 0;
        r24 = 0;
    L_0x0813:
        r5.releaseOutputBuffer(r7, r1);	 Catch:{ Exception -> 0x0abf, all -> 0x0c4d }
        r12 = r4;
        r13 = r30;
        r17 = r33;
        r4 = -1;
    L_0x081c:
        if (r7 == r4) goto L_0x082e;
    L_0x081e:
        r30 = r5;
        r46 = r9;
        r4 = r12;
        r1 = r13;
        r33 = r17;
        r12 = r37;
        r10 = r63;
        r8 = r83;
        goto L_0x0606;
    L_0x082e:
        if (r31 != 0) goto L_0x0a8e;
    L_0x0830:
        r30 = r5;
        r4 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r1 = r6.dequeueOutputBuffer(r11, r4);	 Catch:{ Exception -> 0x0a8b, all -> 0x0c4d }
        r4 = -1;
        if (r1 != r4) goto L_0x085d;
    L_0x083b:
        r68 = r6;
        r69 = r19;
        r3 = r30;
        r22 = r47;
        r58 = r59;
        r47 = r60;
        r10 = r61;
        r2 = 18;
        r37 = 0;
    L_0x084d:
        r57 = -5;
        r61 = -1;
        r65 = 0;
        r67 = 0;
        r59 = r12;
        r60 = r13;
        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a9f;
    L_0x085d:
        r5 = -3;
        if (r1 != r5) goto L_0x0868;
    L_0x0860:
        r68 = r6;
        r69 = r19;
        r3 = r30;
        goto L_0x0a93;
    L_0x0868:
        r5 = -2;
        if (r1 != r5) goto L_0x088b;
    L_0x086b:
        r1 = r6.getOutputFormat();	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        if (r5 == 0) goto L_0x0860;
    L_0x0873:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r5.<init>();	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r7 = "newFormat = ";
        r5.append(r7);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r5.append(r1);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r1 = r5.toString();	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        goto L_0x0860;
    L_0x0888:
        r0 = move-exception;
        goto L_0x05d5;
    L_0x088b:
        if (r1 < 0) goto L_0x0a68;
    L_0x088d:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a8b, all -> 0x0c4d }
        r7 = 18;
        if (r5 < r7) goto L_0x089e;
    L_0x0893:
        r5 = r11.size;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        if (r5 == 0) goto L_0x0899;
    L_0x0897:
        r5 = 1;
        goto L_0x089a;
    L_0x0899:
        r5 = 0;
    L_0x089a:
        r4 = r5;
        r27 = 0;
        goto L_0x08b0;
    L_0x089e:
        r5 = r11.size;	 Catch:{ Exception -> 0x0a8b, all -> 0x0c4d }
        if (r5 != 0) goto L_0x08ad;
    L_0x08a2:
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r27 = 0;
        r7 = (r4 > r27 ? 1 : (r4 == r27 ? 0 : -1));
        if (r7 == 0) goto L_0x08ab;
    L_0x08aa:
        goto L_0x08af;
    L_0x08ab:
        r4 = 0;
        goto L_0x08b0;
    L_0x08ad:
        r27 = 0;
    L_0x08af:
        r4 = 1;
    L_0x08b0:
        r5 = (r83 > r27 ? 1 : (r83 == r27 ? 0 : -1));
        if (r5 <= 0) goto L_0x08c9;
    L_0x08b4:
        r7 = r4;
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r32 = (r4 > r83 ? 1 : (r4 == r83 ? 0 : -1));
        if (r32 < 0) goto L_0x08ca;
    L_0x08bb:
        r4 = r11.flags;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r4 = r4 | 4;
        r11.flags = r4;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r7 = 0;
        r25 = 1;
        r27 = 0;
        r46 = 1;
        goto L_0x08ce;
    L_0x08c9:
        r7 = r4;
    L_0x08ca:
        r46 = r31;
        r27 = 0;
    L_0x08ce:
        r4 = (r2 > r27 ? 1 : (r2 == r27 ? 0 : -1));
        if (r4 <= 0) goto L_0x0906;
    L_0x08d2:
        r4 = -1;
        r31 = (r52 > r4 ? 1 : (r52 == r4 ? 0 : -1));
        if (r31 != 0) goto L_0x0906;
    L_0x08d8:
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r31 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r31 >= 0) goto L_0x0902;
    L_0x08de:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        if (r4 == 0) goto L_0x0900;
    L_0x08e2:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r4.<init>();	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r5 = "drop frame startTime = ";
        r4.append(r5);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r4.append(r2);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r5 = " present time = ";
        r4.append(r5);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r2 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r4.append(r2);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r2 = r4.toString();	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
    L_0x0900:
        r7 = 0;
        goto L_0x0906;
    L_0x0902:
        r2 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0888, all -> 0x0c4d }
        r52 = r2;
    L_0x0906:
        r6.releaseOutputBuffer(r1, r7);	 Catch:{ Exception -> 0x0a8b, all -> 0x0c4d }
        if (r7 == 0) goto L_0x09fe;
    L_0x090b:
        r47.awaitNewImage();	 Catch:{ Exception -> 0x0910, all -> 0x0c4d }
        r1 = 0;
        goto L_0x0916;
    L_0x0910:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x09ea, all -> 0x0c4d }
        r1 = 1;
    L_0x0916:
        if (r1 != 0) goto L_0x09fe;
    L_0x0918:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x09ea, all -> 0x0c4d }
        r2 = 18;
        if (r1 < r2) goto L_0x0951;
    L_0x091e:
        r4 = r47;
        r5 = 0;
        r4.drawImage(r5);	 Catch:{ Exception -> 0x0940, all -> 0x0c4d }
        r1 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0940, all -> 0x0c4d }
        r31 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1 = r1 * r31;
        r7 = r19;
        r7.setPresentationTime(r1);	 Catch:{ Exception -> 0x093e, all -> 0x0c4d }
        r7.swapBuffers();	 Catch:{ Exception -> 0x093e, all -> 0x0c4d }
        r22 = r4;
        r68 = r6;
        r69 = r7;
        r65 = r27;
        r64 = r30;
        goto L_0x0a08;
    L_0x093e:
        r0 = move-exception;
        goto L_0x0943;
    L_0x0940:
        r0 = move-exception;
        r7 = r19;
    L_0x0943:
        r1 = r0;
        r47 = r4;
        r11 = r7;
        r5 = r30;
        r10 = r61;
        r67 = 0;
        r4 = r80;
        goto L_0x0bb4;
    L_0x0951:
        r7 = r19;
        r3 = r30;
        r4 = r47;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r5 = 0;
        r19 = r3.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x09d7, all -> 0x0c4d }
        if (r19 < 0) goto L_0x09b2;
    L_0x0960:
        r10 = 1;
        r4.drawImage(r10);	 Catch:{ Exception -> 0x09d7, all -> 0x0c4d }
        r16 = r4.getFrame();	 Catch:{ Exception -> 0x09d7, all -> 0x0c4d }
        r30 = r45[r19];	 Catch:{ Exception -> 0x09d7, all -> 0x0c4d }
        r30.clear();	 Catch:{ Exception -> 0x09d7, all -> 0x0c4d }
        r31 = r1;
        r2 = r61;
        r1 = r16;
        r10 = r2;
        r47 = r60;
        r57 = -5;
        r2 = r30;
        r29 = r3;
        r58 = r59;
        r3 = r44;
        r22 = r4;
        r59 = r12;
        r60 = r13;
        r12 = r31;
        r61 = -1;
        r4 = r77;
        r65 = r27;
        r64 = r29;
        r67 = 0;
        r5 = r78;
        r68 = r6;
        r6 = r36;
        r69 = r7;
        r7 = r35;
        org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        r29 = 0;
        r1 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        r33 = 0;
        r27 = r64;
        r28 = r19;
        r30 = r34;
        r31 = r1;
        r27.queueInputBuffer(r28, r29, r30, r31, r33);	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        goto L_0x0a1a;
    L_0x09b2:
        r64 = r3;
        r22 = r4;
        r68 = r6;
        r69 = r7;
        r65 = r27;
        r58 = r59;
        r47 = r60;
        r10 = r61;
        r57 = -5;
        r61 = -1;
        r67 = 0;
        r59 = r12;
        r60 = r13;
        r12 = r1;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        if (r1 == 0) goto L_0x0a1a;
    L_0x09d1:
        r1 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        goto L_0x0a1a;
    L_0x09d7:
        r0 = move-exception;
        r64 = r3;
        r22 = r4;
        r68 = r6;
        r69 = r7;
        r10 = r61;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        r47 = r22;
        goto L_0x09fa;
    L_0x09ea:
        r0 = move-exception;
        r68 = r6;
        r69 = r19;
        r64 = r30;
        r22 = r47;
        r10 = r61;
        r67 = 0;
        r4 = r80;
        r1 = r0;
    L_0x09fa:
        r5 = r64;
        goto L_0x0b4a;
    L_0x09fe:
        r68 = r6;
        r69 = r19;
        r65 = r27;
        r64 = r30;
        r22 = r47;
    L_0x0a08:
        r58 = r59;
        r47 = r60;
        r10 = r61;
        r57 = -5;
        r61 = -1;
        r67 = 0;
        r59 = r12;
        r60 = r13;
        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x0a1a:
        r1 = r11.flags;	 Catch:{ Exception -> 0x0a63, all -> 0x0c4d }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x0a5c;
    L_0x0a20:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0a63, all -> 0x0c4d }
        if (r1 == 0) goto L_0x0a34;
    L_0x0a24:
        r1 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        goto L_0x0a34;
    L_0x0a2a:
        r0 = move-exception;
        r4 = r80;
        r1 = r0;
        r47 = r22;
        r5 = r64;
        goto L_0x0b15;
    L_0x0a34:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a63, all -> 0x0c4d }
        r2 = 18;
        if (r1 < r2) goto L_0x0a40;
    L_0x0a3a:
        r64.signalEndOfInputStream();	 Catch:{ Exception -> 0x0a2a, all -> 0x0c4d }
        r3 = r64;
        goto L_0x0a57;
    L_0x0a40:
        r3 = r64;
        r38 = r3.dequeueInputBuffer(r12);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        if (r38 < 0) goto L_0x0a57;
    L_0x0a48:
        r39 = 0;
        r40 = 1;
        r4 = r11.presentationTimeUs;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r43 = 4;
        r37 = r3;
        r41 = r4;
        r37.queueInputBuffer(r38, r39, r40, r41, r43);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
    L_0x0a57:
        r31 = r46;
        r37 = 0;
        goto L_0x0a9f;
    L_0x0a5c:
        r3 = r64;
        r2 = 18;
        r31 = r46;
        goto L_0x0a9f;
    L_0x0a63:
        r0 = move-exception;
        r3 = r64;
        goto L_0x0b0f;
    L_0x0a68:
        r68 = r6;
        r69 = r19;
        r3 = r30;
        r22 = r47;
        r10 = r61;
        r67 = 0;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r4.<init>();	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r5 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r4.append(r5);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r4.append(r1);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r1 = r4.toString();	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2.<init>(r1);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        throw r2;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
    L_0x0a8b:
        r0 = move-exception;
        goto L_0x0b28;
    L_0x0a8e:
        r3 = r5;
        r68 = r6;
        r69 = r19;
    L_0x0a93:
        r22 = r47;
        r58 = r59;
        r47 = r60;
        r10 = r61;
        r2 = 18;
        goto L_0x084d;
    L_0x0a9f:
        r30 = r3;
        r46 = r9;
        r61 = r10;
        r33 = r17;
        r12 = r37;
        r4 = r59;
        r1 = r60;
        r10 = r63;
        r6 = r68;
        r19 = r69;
        r2 = r81;
        r8 = r83;
        r60 = r47;
        r59 = r58;
        r47 = r22;
        goto L_0x0606;
    L_0x0abf:
        r0 = move-exception;
        goto L_0x0b1b;
    L_0x0ac1:
        r8 = r78;
        r3 = r5;
        r68 = r6;
        r69 = r19;
        r22 = r47;
        r10 = r61;
        r67 = 0;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2.<init>();	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r4 = "encoderOutputBuffer ";
        r2.append(r4);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2.append(r7);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r4 = " was null";
        r2.append(r4);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        throw r1;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
    L_0x0aea:
        r8 = r78;
        r3 = r5;
        r68 = r6;
        r69 = r19;
        r22 = r47;
        r10 = r61;
        r67 = 0;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2.<init>();	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r4 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r2.append(r4);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2.append(r7);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
        throw r1;	 Catch:{ Exception -> 0x0b0e, all -> 0x0c4d }
    L_0x0b0e:
        r0 = move-exception;
    L_0x0b0f:
        r4 = r80;
        r1 = r0;
        r5 = r3;
        r47 = r22;
    L_0x0b15:
        r6 = r68;
        goto L_0x0b4a;
    L_0x0b18:
        r0 = move-exception;
        r8 = r78;
    L_0x0b1b:
        r3 = r5;
        r68 = r6;
        r69 = r19;
        r22 = r47;
        r10 = r61;
        goto L_0x0b45;
    L_0x0b25:
        r0 = move-exception;
        r8 = r78;
    L_0x0b28:
        r68 = r6;
        r69 = r19;
        r3 = r30;
        r22 = r47;
        r10 = r61;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        r5 = r3;
        goto L_0x0b4a;
    L_0x0b39:
        r0 = move-exception;
        r10 = r1;
        r56 = r4;
        r3 = r5;
        r68 = r6;
        r8 = r13;
        r69 = r19;
        r22 = r47;
    L_0x0b45:
        r67 = 0;
        r4 = r80;
        r1 = r0;
    L_0x0b4a:
        r11 = r69;
        goto L_0x0bb4;
    L_0x0b4e:
        r0 = move-exception;
        r10 = r1;
        r22 = r4;
        r3 = r5;
        r68 = r6;
        r8 = r13;
        r69 = r19;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        r47 = r22;
        r11 = r69;
        goto L_0x0bb2;
    L_0x0b63:
        r0 = move-exception;
        r10 = r1;
        r22 = r4;
        r3 = r5;
        r68 = r6;
        r69 = r11;
        r8 = r13;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        r47 = r22;
        goto L_0x0bb2;
    L_0x0b75:
        r0 = move-exception;
        r10 = r1;
        r3 = r5;
        r68 = r6;
        r69 = r11;
        r8 = r13;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        goto L_0x0bb0;
    L_0x0b83:
        r0 = move-exception;
        r10 = r1;
        r3 = r5;
        r69 = r11;
        r8 = r13;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        r6 = 0;
        goto L_0x0bb0;
    L_0x0b90:
        r0 = move-exception;
        r10 = r1;
        r3 = r5;
        r8 = r13;
        r67 = 0;
        r4 = r80;
        r1 = r0;
        goto L_0x0bae;
    L_0x0b9a:
        r0 = move-exception;
        r10 = r1;
        r80 = r11;
        goto L_0x0ba7;
    L_0x0b9f:
        r0 = move-exception;
        r10 = r1;
        r8 = r13;
        r67 = 0;
        goto L_0x0bac;
    L_0x0ba5:
        r0 = move-exception;
        r10 = r1;
    L_0x0ba7:
        r8 = r13;
        r67 = 0;
        r4 = r80;
    L_0x0bac:
        r1 = r0;
        r5 = 0;
    L_0x0bae:
        r6 = 0;
        r11 = 0;
    L_0x0bb0:
        r47 = 0;
    L_0x0bb2:
        r56 = 0;
    L_0x0bb4:
        r2 = r1 instanceof java.lang.IllegalStateException;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c4d }
        if (r2 == 0) goto L_0x0bbc;
    L_0x0bb8:
        if (r88 != 0) goto L_0x0bbc;
    L_0x0bba:
        r3 = 1;
        goto L_0x0bbd;
    L_0x0bbc:
        r3 = 0;
    L_0x0bbd:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c1f, all -> 0x0c4d }
        r2.<init>();	 Catch:{ Exception -> 0x0c1f, all -> 0x0c4d }
        r7 = "bitrate: ";
        r2.append(r7);	 Catch:{ Exception -> 0x0c1f, all -> 0x0c4d }
        r2.append(r4);	 Catch:{ Exception -> 0x0c1f, all -> 0x0c4d }
        r7 = " framerate: ";
        r2.append(r7);	 Catch:{ Exception -> 0x0c1f, all -> 0x0c4d }
        r9 = r79;
        r2.append(r9);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r7 = " size: ";
        r2.append(r7);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r2.append(r8);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r7 = "x";
        r2.append(r7);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r2.append(r15);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r19 = r11;
        r21 = 1;
    L_0x0bf3:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r1.unselectTrack(r10);	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        if (r47 == 0) goto L_0x0bfd;
    L_0x0bfa:
        r47.release();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
    L_0x0bfd:
        if (r19 == 0) goto L_0x0CLASSNAME;
    L_0x0bff:
        r19.release();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
    L_0x0CLASSNAME:
        if (r6 == 0) goto L_0x0c0a;
    L_0x0CLASSNAME:
        r6.stop();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r6.release();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
    L_0x0c0a:
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0c0c:
        r5.stop();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r5.release();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
    L_0x0CLASSNAME:
        if (r56 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r56.release();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
    L_0x0CLASSNAME:
        r72.checkConversionCanceled();	 Catch:{ Exception -> 0x0c1d, all -> 0x0c4d }
        r67 = r21;
        goto L_0x0CLASSNAME;
    L_0x0c1d:
        r0 = move-exception;
        goto L_0x0CLASSNAME;
    L_0x0c1f:
        r0 = move-exception;
        r9 = r79;
    L_0x0CLASSNAME:
        r1 = r0;
        goto L_0x0c5b;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r9 = r79;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r9 = r79;
        r8 = r13;
        r67 = 0;
        r4 = r80;
        r3 = 0;
    L_0x0CLASSNAME:
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1.release();
    L_0x0CLASSNAME:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c3b:
        r1.finishMovie();	 Catch:{ Exception -> 0x0c3f }
        goto L_0x0CLASSNAME;
    L_0x0c3f:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0CLASSNAME:
        r10 = r4;
        r16 = r67;
        goto L_0x0ca2;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r15 = r77;
        r8 = r11;
        goto L_0x0CLASSNAME;
    L_0x0c4d:
        r0 = move-exception;
        r2 = r0;
        r1 = r14;
        goto L_0x0cc9;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r8 = r11;
        r15 = r12;
    L_0x0CLASSNAME:
        r67 = 0;
        r4 = r80;
    L_0x0CLASSNAME:
        r1 = r0;
    L_0x0c5a:
        r3 = 0;
    L_0x0c5b:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0cc5 }
        r2.<init>();	 Catch:{ all -> 0x0cc5 }
        r5 = "bitrate: ";
        r2.append(r5);	 Catch:{ all -> 0x0cc5 }
        r2.append(r4);	 Catch:{ all -> 0x0cc5 }
        r5 = " framerate: ";
        r2.append(r5);	 Catch:{ all -> 0x0cc5 }
        r2.append(r9);	 Catch:{ all -> 0x0cc5 }
        r5 = " size: ";
        r2.append(r5);	 Catch:{ all -> 0x0cc5 }
        r2.append(r8);	 Catch:{ all -> 0x0cc5 }
        r5 = "x";
        r2.append(r5);	 Catch:{ all -> 0x0cc5 }
        r2.append(r15);	 Catch:{ all -> 0x0cc5 }
        r2 = r2.toString();	 Catch:{ all -> 0x0cc5 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0cc5 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0cc5 }
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c8f:
        r1.release();
    L_0x0CLASSNAME:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0c9f;
    L_0x0CLASSNAME:
        r1.finishMovie();	 Catch:{ Exception -> 0x0c9a }
        goto L_0x0c9f;
    L_0x0c9a:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0c9f:
        r10 = r4;
        r16 = 1;
    L_0x0ca2:
        if (r3 == 0) goto L_0x0cc4;
    L_0x0ca4:
        r17 = 1;
        r1 = r72;
        r2 = r73;
        r3 = r74;
        r4 = r75;
        r5 = r76;
        r6 = r77;
        r7 = r78;
        r8 = r79;
        r9 = r10;
        r10 = r81;
        r12 = r83;
        r14 = r85;
        r16 = r87;
        r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17);
        return r1;
    L_0x0cc4:
        return r16;
    L_0x0cc5:
        r0 = move-exception;
        r1 = r72;
        r2 = r0;
    L_0x0cc9:
        r3 = r1.extractor;
        if (r3 == 0) goto L_0x0cd0;
    L_0x0ccd:
        r3.release();
    L_0x0cd0:
        r3 = r1.mediaMuxer;
        if (r3 == 0) goto L_0x0cdd;
    L_0x0cd4:
        r3.finishMovie();	 Catch:{ Exception -> 0x0cd8 }
        goto L_0x0cdd;
    L_0x0cd8:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0cdd:
        goto L_0x0cdf;
    L_0x0cde:
        throw r2;
    L_0x0cdf:
        goto L_0x0cde;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, long, long, long, boolean, boolean):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:92:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0196  */
    /* JADX WARNING: Missing block: B:51:0x00f2, code skipped:
            if (r9[r15 + 3] != (byte) 1) goto L_0x00f8;
     */
    private long readAndWriteTracks(android.media.MediaExtractor r29, org.telegram.messenger.video.MP4Builder r30, android.media.MediaCodec.BufferInfo r31, long r32, long r34, long r36, java.io.File r38, boolean r39) throws java.lang.Exception {
        /*
        r28 = this;
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r3 = r31;
        r4 = r32;
        r6 = 0;
        r7 = org.telegram.messenger.MediaController.findTrack(r1, r6);
        r9 = 1;
        if (r39 == 0) goto L_0x001a;
    L_0x0012:
        r10 = org.telegram.messenger.MediaController.findTrack(r1, r9);
        r12 = r10;
        r10 = r36;
        goto L_0x001d;
    L_0x001a:
        r10 = r36;
        r12 = -1;
    L_0x001d:
        r10 = (float) r10;
        r11 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r10 = r10 / r11;
        r13 = "max-input-size";
        r14 = 0;
        if (r7 < 0) goto L_0x0042;
    L_0x0027:
        r1.selectTrack(r7);
        r11 = r1.getTrackFormat(r7);
        r16 = r2.addTrack(r11, r6);
        r11 = r11.getInteger(r13);
        r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r17 <= 0) goto L_0x003e;
    L_0x003a:
        r1.seekTo(r4, r6);
        goto L_0x0045;
    L_0x003e:
        r1.seekTo(r14, r6);
        goto L_0x0045;
    L_0x0042:
        r11 = 0;
        r16 = -1;
    L_0x0045:
        if (r12 < 0) goto L_0x007c;
    L_0x0047:
        r1.selectTrack(r12);
        r8 = r1.getTrackFormat(r12);
        r6 = "mime";
        r6 = r8.getString(r6);
        r14 = "audio/unknown";
        r6 = r6.equals(r14);
        if (r6 == 0) goto L_0x005f;
    L_0x005c:
        r8 = -1;
        r12 = -1;
        goto L_0x007d;
    L_0x005f:
        r6 = r2.addTrack(r8, r9);
        r8 = r8.getInteger(r13);
        r11 = java.lang.Math.max(r8, r11);
        r13 = 0;
        r8 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1));
        if (r8 <= 0) goto L_0x0076;
    L_0x0071:
        r8 = 0;
        r1.seekTo(r4, r8);
        goto L_0x007a;
    L_0x0076:
        r8 = 0;
        r1.seekTo(r13, r8);
    L_0x007a:
        r8 = r6;
        goto L_0x007d;
    L_0x007c:
        r8 = -1;
    L_0x007d:
        r6 = java.nio.ByteBuffer.allocateDirect(r11);
        r13 = -1;
        if (r12 >= 0) goto L_0x0089;
    L_0x0085:
        if (r7 < 0) goto L_0x0088;
    L_0x0087:
        goto L_0x0089;
    L_0x0088:
        return r13;
    L_0x0089:
        r28.checkConversionCanceled();
        r20 = r13;
        r11 = 0;
        r22 = 0;
    L_0x0091:
        if (r11 != 0) goto L_0x01be;
    L_0x0093:
        r28.checkConversionCanceled();
        r15 = 0;
        r13 = r1.readSampleData(r6, r15);
        r3.size = r13;
        r13 = r29.getSampleTrackIndex();
        if (r13 != r7) goto L_0x00a7;
    L_0x00a3:
        r14 = r16;
    L_0x00a5:
        r15 = -1;
        goto L_0x00ad;
    L_0x00a7:
        if (r13 != r12) goto L_0x00ab;
    L_0x00a9:
        r14 = r8;
        goto L_0x00a5;
    L_0x00ab:
        r14 = -1;
        goto L_0x00a5;
    L_0x00ad:
        if (r14 == r15) goto L_0x019c;
    L_0x00af:
        r15 = android.os.Build.VERSION.SDK_INT;
        r9 = 21;
        if (r15 >= r9) goto L_0x00be;
    L_0x00b5:
        r9 = 0;
        r6.position(r9);
        r9 = r3.size;
        r6.limit(r9);
    L_0x00be:
        if (r13 == r12) goto L_0x0128;
    L_0x00c0:
        r9 = r6.array();
        if (r9 == 0) goto L_0x0128;
    L_0x00c6:
        r15 = r6.arrayOffset();
        r24 = r6.limit();
        r24 = r15 + r24;
        r37 = r8;
        r8 = -1;
    L_0x00d3:
        r25 = 4;
        r39 = r11;
        r11 = r24 + -4;
        if (r15 > r11) goto L_0x012c;
    L_0x00db:
        r26 = r9[r15];
        if (r26 != 0) goto L_0x00f5;
    L_0x00df:
        r26 = r15 + 1;
        r26 = r9[r26];
        if (r26 != 0) goto L_0x00f5;
    L_0x00e5:
        r26 = r15 + 2;
        r26 = r9[r26];
        if (r26 != 0) goto L_0x00f5;
    L_0x00eb:
        r26 = r15 + 3;
        r27 = r12;
        r12 = r9[r26];
        r1 = 1;
        if (r12 == r1) goto L_0x00fa;
    L_0x00f4:
        goto L_0x00f8;
    L_0x00f5:
        r27 = r12;
        r1 = 1;
    L_0x00f8:
        if (r15 != r11) goto L_0x011f;
    L_0x00fa:
        r12 = -1;
        if (r8 == r12) goto L_0x011e;
    L_0x00fd:
        r12 = r15 - r8;
        if (r15 == r11) goto L_0x0102;
    L_0x0101:
        goto L_0x0104;
    L_0x0102:
        r25 = 0;
    L_0x0104:
        r12 = r12 - r25;
        r11 = r12 >> 24;
        r11 = (byte) r11;
        r9[r8] = r11;
        r11 = r8 + 1;
        r1 = r12 >> 16;
        r1 = (byte) r1;
        r9[r11] = r1;
        r1 = r8 + 2;
        r11 = r12 >> 8;
        r11 = (byte) r11;
        r9[r1] = r11;
        r8 = r8 + 3;
        r1 = (byte) r12;
        r9[r8] = r1;
    L_0x011e:
        r8 = r15;
    L_0x011f:
        r15 = r15 + 1;
        r1 = r29;
        r11 = r39;
        r12 = r27;
        goto L_0x00d3;
    L_0x0128:
        r37 = r8;
        r39 = r11;
    L_0x012c:
        r27 = r12;
        r1 = r3.size;
        if (r1 < 0) goto L_0x013a;
    L_0x0132:
        r8 = r29.getSampleTime();
        r3.presentationTimeUs = r8;
        r1 = 0;
        goto L_0x013e;
    L_0x013a:
        r1 = 0;
        r3.size = r1;
        r1 = 1;
    L_0x013e:
        r8 = r3.size;
        if (r8 <= 0) goto L_0x0162;
    L_0x0142:
        if (r1 != 0) goto L_0x0162;
    L_0x0144:
        r8 = 0;
        if (r13 != r7) goto L_0x0156;
    L_0x0148:
        r11 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r11 <= 0) goto L_0x0156;
    L_0x014c:
        r11 = -1;
        r13 = (r20 > r11 ? 1 : (r20 == r11 ? 0 : -1));
        if (r13 != 0) goto L_0x0156;
    L_0x0152:
        r11 = r3.presentationTimeUs;
        r20 = r11;
    L_0x0156:
        r11 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1));
        if (r11 < 0) goto L_0x0165;
    L_0x015a:
        r8 = r3.presentationTimeUs;
        r11 = (r8 > r34 ? 1 : (r8 == r34 ? 0 : -1));
        if (r11 >= 0) goto L_0x0161;
    L_0x0160:
        goto L_0x0165;
    L_0x0161:
        r1 = 1;
    L_0x0162:
        r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        goto L_0x0194;
    L_0x0165:
        r8 = 0;
        r3.offset = r8;
        r9 = r29.getSampleFlags();
        r3.flags = r9;
        r11 = r2.writeSampleData(r14, r6, r3, r8);
        r14 = 0;
        r9 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1));
        if (r9 == 0) goto L_0x0162;
    L_0x0178:
        r9 = r0.callback;
        if (r9 == 0) goto L_0x0162;
    L_0x017c:
        r8 = r3.presentationTimeUs;
        r18 = r8 - r20;
        r13 = (r18 > r22 ? 1 : (r18 == r22 ? 0 : -1));
        if (r13 <= 0) goto L_0x0186;
    L_0x0184:
        r22 = r8 - r20;
    L_0x0186:
        r8 = r22;
        r13 = r0.callback;
        r14 = (float) r8;
        r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r14 = r14 / r15;
        r14 = r14 / r10;
        r13.didWriteData(r11, r14);
        r22 = r8;
    L_0x0194:
        if (r1 != 0) goto L_0x0199;
    L_0x0196:
        r29.advance();
    L_0x0199:
        r8 = r1;
        r1 = -1;
        goto L_0x01ad;
    L_0x019c:
        r37 = r8;
        r39 = r11;
        r27 = r12;
        r1 = -1;
        r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        if (r13 != r1) goto L_0x01a9;
    L_0x01a7:
        r8 = 1;
        goto L_0x01ad;
    L_0x01a9:
        r29.advance();
        r8 = 0;
    L_0x01ad:
        if (r8 == 0) goto L_0x01b1;
    L_0x01af:
        r11 = 1;
        goto L_0x01b3;
    L_0x01b1:
        r11 = r39;
    L_0x01b3:
        r1 = r29;
        r8 = r37;
        r12 = r27;
        r9 = 1;
        r13 = -1;
        goto L_0x0091;
    L_0x01be:
        r27 = r12;
        r1 = r29;
        if (r7 < 0) goto L_0x01c7;
    L_0x01c4:
        r1.unselectTrack(r7);
    L_0x01c7:
        if (r27 < 0) goto L_0x01ce;
    L_0x01c9:
        r12 = r27;
        r1.unselectTrack(r12);
    L_0x01ce:
        return r20;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener != null && videoConvertorListener.checkConversionCanceled()) {
            throw new RuntimeException("canceled conversion");
        }
    }
}
