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

    /* JADX WARNING: Removed duplicated region for block: B:50:0x010d A:{Catch:{ Exception -> 0x011e, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00de A:{Catch:{ Exception -> 0x011e, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00de A:{Catch:{ Exception -> 0x011e, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x010d A:{Catch:{ Exception -> 0x011e, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x054b A:{Catch:{ Exception -> 0x0562, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0a16 A:{Catch:{ Exception -> 0x0acd, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x09d7 A:{Catch:{ Exception -> 0x0a1c, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x09d7 A:{Catch:{ Exception -> 0x0a1c, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0a16 A:{Catch:{ Exception -> 0x0acd, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x08a8 A:{SYNTHETIC, Splitter:B:522:0x08a8} */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0a16 A:{Catch:{ Exception -> 0x0acd, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x09d7 A:{Catch:{ Exception -> 0x0a1c, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x089d A:{Catch:{ Exception -> 0x0823, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0879 A:{Catch:{ Exception -> 0x0823, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ce A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x07bd A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0304  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0302  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0399  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0308  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03c2  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x03de A:{Catch:{ Exception -> 0x0b00, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0585  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x042e A:{SYNTHETIC, Splitter:B:254:0x042e} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x05e6  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x05d4  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x07bd A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ce A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01c7 A:{SYNTHETIC, Splitter:B:97:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01be A:{SYNTHETIC, Splitter:B:94:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f7 A:{SYNTHETIC, Splitter:B:109:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x021c A:{SYNTHETIC, Splitter:B:124:0x021c} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x023b A:{SYNTHETIC, Splitter:B:137:0x023b} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0283 A:{SYNTHETIC, Splitter:B:157:0x0283} */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0273 A:{SYNTHETIC, Splitter:B:153:0x0273} */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x029e A:{SYNTHETIC, Splitter:B:166:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02c4 A:{SYNTHETIC, Splitter:B:177:0x02c4} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03c2  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x03de A:{Catch:{ Exception -> 0x0b00, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x03fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x042e A:{SYNTHETIC, Splitter:B:254:0x042e} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0585  */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x059e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x05d4  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x05e6  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ce A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x07bd A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01be A:{SYNTHETIC, Splitter:B:94:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01c7 A:{SYNTHETIC, Splitter:B:97:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f7 A:{SYNTHETIC, Splitter:B:109:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x021c A:{SYNTHETIC, Splitter:B:124:0x021c} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x023b A:{SYNTHETIC, Splitter:B:137:0x023b} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0273 A:{SYNTHETIC, Splitter:B:153:0x0273} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0283 A:{SYNTHETIC, Splitter:B:157:0x0283} */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x029e A:{SYNTHETIC, Splitter:B:166:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02c4 A:{SYNTHETIC, Splitter:B:177:0x02c4} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03c2  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x03de A:{Catch:{ Exception -> 0x0b00, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x03fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0585  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x042e A:{SYNTHETIC, Splitter:B:254:0x042e} */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x059e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x05e6  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x05d4  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x07bd A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ce A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01c7 A:{SYNTHETIC, Splitter:B:97:0x01c7} */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01be A:{SYNTHETIC, Splitter:B:94:0x01be} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01f7 A:{SYNTHETIC, Splitter:B:109:0x01f7} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x021c A:{SYNTHETIC, Splitter:B:124:0x021c} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x023b A:{SYNTHETIC, Splitter:B:137:0x023b} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0283 A:{SYNTHETIC, Splitter:B:157:0x0283} */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0273 A:{SYNTHETIC, Splitter:B:153:0x0273} */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x029e A:{SYNTHETIC, Splitter:B:166:0x029e} */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x02c4 A:{SYNTHETIC, Splitter:B:177:0x02c4} */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03c2  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x03de A:{Catch:{ Exception -> 0x0b00, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x03fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x042e A:{SYNTHETIC, Splitter:B:254:0x042e} */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0585  */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x059e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x05c7  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x05d4  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x05e6  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ce A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x07bd A:{Catch:{ Exception -> 0x0a45, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:729:0x0d2d  */
    /* JADX WARNING: Removed duplicated region for block: B:732:0x0d34 A:{SYNTHETIC, Splitter:B:732:0x0d34} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:716:0x0cf9 A:{SYNTHETIC, Splitter:B:716:0x0cf9} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cab A:{ExcHandler: all (r0_67 'th' java.lang.Throwable), Splitter:B:60:0x0137} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0c0a A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0c0f A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0c1c A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c2d, all -> 0x0cab }} */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:694:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d24 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0d05  */
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
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:60:0x0137, B:679:0x0c6a] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:60:0x0137, B:686:0x0CLASSNAME] */
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
    /* JADX WARNING: Missing block: B:116:0x020d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:117:0x020e, code skipped:
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:126:0x0229, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:127:0x022a, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:142:0x0249, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:143:0x024a, code skipped:
            r1 = r0;
            r4 = r3;
            r13 = r12;
            r9 = r25;
            r2 = null;
            r3 = null;
     */
    /* JADX WARNING: Missing block: B:144:0x0253, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:145:0x0254, code skipped:
            r1 = r0;
            r13 = r12;
            r9 = r25;
            r2 = null;
            r3 = null;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:155:0x0279, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:156:0x027a, code skipped:
            r10 = r70;
            r1 = r0;
            r13 = r12;
            r9 = r25;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:172:0x02b6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:173:0x02b7, code skipped:
            r10 = r70;
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:202:0x034a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:222:0x038b, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:223:0x038c, code skipped:
            r10 = r73;
     */
    /* JADX WARNING: Missing block: B:226:0x03a6, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:227:0x03a7, code skipped:
            r10 = r73;
     */
    /* JADX WARNING: Missing block: B:248:0x040e, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:249:0x040f, code skipped:
            r10 = r70;
            r1 = r0;
            r57 = r13;
            r9 = r25;
            r4 = r28;
            r2 = r53;
     */
    /* JADX WARNING: Missing block: B:250:0x041a, code skipped:
            r53 = null;
            r60 = true;
            r13 = r67;
     */
    /* JADX WARNING: Missing block: B:271:0x0490, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:272:0x0491, code skipped:
            r10 = r70;
            r1 = r0;
            r9 = r13;
            r4 = r28;
            r2 = r53;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:306:0x052e, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:307:0x052f, code skipped:
            r13 = r67;
            r10 = r70;
            r1 = r0;
            r9 = r5;
     */
    /* JADX WARNING: Missing block: B:318:0x0562, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:319:0x0563, code skipped:
            r13 = r67;
            r10 = r70;
            r1 = r0;
            r4 = r28;
            r2 = r53;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:321:0x0571, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:322:0x0572, code skipped:
            r57 = r13;
            r13 = r67;
            r10 = r70;
            r1 = r0;
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:323:0x057b, code skipped:
            r4 = r28;
            r2 = r53;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:349:0x0600, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:350:0x0601, code skipped:
            r13 = r67;
     */
    /* JADX WARNING: Missing block: B:451:0x0798, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:452:0x0799, code skipped:
            r13 = r67;
            r9 = r68;
     */
    /* JADX WARNING: Missing block: B:477:0x0823, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:525:0x08ad, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:528:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r1 = true;
     */
    /* JADX WARNING: Missing block: B:540:0x08ee, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:541:0x08f0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:542:0x08f1, code skipped:
            r1 = r28;
     */
    /* JADX WARNING: Missing block: B:543:0x08f3, code skipped:
            r10 = r70;
            r4 = r1;
            r9 = r60;
            r53 = null;
            r60 = true;
     */
    /* JADX WARNING: Missing block: B:555:0x0989, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:556:0x098a, code skipped:
            r59 = r1;
            r16 = r2;
            r28 = r3;
            r20 = r5;
            r9 = r60;
            r53 = null;
            r60 = true;
            r10 = r70;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:557:0x099c, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:558:0x099d, code skipped:
            r20 = r5;
            r59 = r28;
            r16 = r53;
            r9 = r60;
            r53 = null;
            r60 = true;
            r28 = r3;
            r10 = r70;
            r1 = r0;
            r2 = r16;
     */
    /* JADX WARNING: Missing block: B:568:0x09e1, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:569:0x09e2, code skipped:
            r10 = r70;
            r1 = r0;
            r2 = r16;
            r5 = r20;
            r3 = r28;
     */
    /* JADX WARNING: Missing block: B:584:0x0a1c, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:585:0x0a1d, code skipped:
            r1 = r20;
     */
    /* JADX WARNING: Missing block: B:588:0x0a45, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:596:0x0acd, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:597:0x0ace, code skipped:
            r10 = r70;
            r5 = r1;
            r2 = r16;
            r3 = r28;
     */
    /* JADX WARNING: Missing block: B:598:0x0ad6, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:599:0x0ad7, code skipped:
            r13 = r67;
     */
    /* JADX WARNING: Missing block: B:600:0x0ad9, code skipped:
            r1 = r5;
            r59 = r28;
            r16 = r53;
            r9 = r60;
            r53 = null;
            r60 = true;
            r28 = r3;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:601:0x0ae9, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:602:0x0aea, code skipped:
            r13 = r67;
            r59 = r28;
            r16 = r53;
            r9 = r60;
            r53 = null;
            r60 = true;
            r28 = r3;
            r10 = r70;
            r5 = r56;
     */
    /* JADX WARNING: Missing block: B:603:0x0afd, code skipped:
            r2 = r16;
     */
    /* JADX WARNING: Missing block: B:604:0x0b00, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:605:0x0b01, code skipped:
            r16 = r2;
            r1 = r5;
            r57 = r13;
            r9 = r25;
            r59 = r28;
            r53 = null;
            r60 = true;
            r13 = r67;
            r28 = r3;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:606:0x0b14, code skipped:
            r4 = r59;
     */
    /* JADX WARNING: Missing block: B:607:0x0b18, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:608:0x0b19, code skipped:
            r16 = r2;
            r28 = r3;
            r59 = r4;
            r1 = r5;
            r30 = null;
            r13 = r12;
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:609:0x0b26, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:610:0x0b27, code skipped:
            r16 = r2;
            r28 = r3;
            r59 = r4;
            r1 = r5;
            r13 = r12;
            r9 = r25;
            r30 = null;
     */
    /* JADX WARNING: Missing block: B:611:0x0b33, code skipped:
            r53 = null;
            r60 = true;
            r10 = r70;
            r57 = r30;
     */
    /* JADX WARNING: Missing block: B:612:0x0b3d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:613:0x0b3e, code skipped:
            r28 = r3;
            r59 = r4;
            r1 = r5;
            r13 = r12;
            r9 = r25;
            r30 = null;
            r53 = null;
            r60 = true;
            r10 = r70;
     */
    /* JADX WARNING: Missing block: B:614:0x0b4f, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:615:0x0b50, code skipped:
            r28 = r3;
            r59 = r4;
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r30 = null;
            r53 = null;
            r60 = true;
     */
    /* JADX WARNING: Missing block: B:616:0x0b60, code skipped:
            r2 = r30;
            r57 = r2;
     */
    /* JADX WARNING: Missing block: B:617:0x0b65, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:618:0x0b66, code skipped:
            r59 = r4;
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r53 = null;
            r60 = true;
            r2 = null;
            r3 = r2;
            r57 = r3;
     */
    /* JADX WARNING: Missing block: B:619:0x0b7a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:620:0x0b7b, code skipped:
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r53 = null;
            r60 = true;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:621:0x0b8a, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:622:0x0b8b, code skipped:
            r30 = null;
            r1 = r5;
            r70 = r10;
            r13 = r12;
            r9 = r25;
            r53 = null;
            r60 = true;
     */
    /* JADX WARNING: Missing block: B:623:0x0b97, code skipped:
            r3 = r2;
            r4 = r3;
            r57 = r4;
     */
    /* JADX WARNING: Missing block: B:624:0x0b9b, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:625:0x0b9d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:626:0x0b9e, code skipped:
            r70 = r10;
     */
    /* JADX WARNING: Missing block: B:627:0x0ba1, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:628:0x0ba2, code skipped:
            r13 = r12;
            r9 = r25;
            r30 = null;
            r53 = null;
            r60 = true;
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:629:0x0bae, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:630:0x0baf, code skipped:
            r13 = r12;
            r9 = r25;
     */
    /* JADX WARNING: Missing block: B:631:0x0bb3, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:632:0x0bb4, code skipped:
            r9 = r4;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:633:0x0bb6, code skipped:
            r30 = null;
            r53 = null;
            r60 = true;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:634:0x0bbd, code skipped:
            r2 = r30;
            r3 = r2;
            r4 = r3;
            r5 = r4;
            r57 = r5;
     */
    /* JADX WARNING: Missing block: B:663:0x0c2d, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:664:0x0c2e, code skipped:
            r1 = r0;
            r13 = r11;
            r3 = r17;
     */
    /* JADX WARNING: Missing block: B:665:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:666:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:667:0x0CLASSNAME, code skipped:
            r12 = r68;
     */
    /* JADX WARNING: Missing block: B:668:0x0CLASSNAME, code skipped:
            r1 = r0;
            r13 = r11;
     */
    /* JADX WARNING: Missing block: B:669:0x0c3c, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:670:0x0c3d, code skipped:
            r12 = r68;
            r13 = r69;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:671:0x0CLASSNAME, code skipped:
            r3 = r53;
     */
    /* JADX WARNING: Missing block: B:672:0x0CLASSNAME, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:673:0x0CLASSNAME, code skipped:
            r12 = r68;
            r13 = r69;
     */
    /* JADX WARNING: Missing block: B:699:0x0ca6, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:700:0x0ca8, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:701:0x0ca9, code skipped:
            r13 = r11;
     */
    /* JADX WARNING: Missing block: B:702:0x0cab, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:703:0x0cac, code skipped:
            r2 = r0;
            r1 = r14;
     */
    /* JADX WARNING: Missing block: B:713:0x0cf2, code skipped:
            r1.release();
     */
    /* JADX WARNING: Missing block: B:717:?, code skipped:
            r1.finishMovie();
     */
    /* JADX WARNING: Missing block: B:718:0x0cfd, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:719:0x0cfe, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:729:0x0d2d, code skipped:
            r3.release();
     */
    /* JADX WARNING: Missing block: B:733:?, code skipped:
            r3.finishMovie();
     */
    /* JADX WARNING: Missing block: B:734:0x0d38, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:735:0x0d39, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    private boolean convertVideoInternal(java.lang.String r63, java.io.File r64, int r65, boolean r66, int r67, int r68, int r69, int r70, long r71, long r73, long r75, boolean r77, boolean r78) {
        /*
        r62 = this;
        r14 = r62;
        r13 = r63;
        r15 = r65;
        r12 = r67;
        r11 = r68;
        r9 = r69;
        r10 = r70;
        r7 = r71;
        r5 = r73;
        r2 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r2.<init>();	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1.<init>();	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r4 = r64;
        r1.setCacheFile(r4);	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1.setRotation(r15);	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1.setSize(r12, r11);	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r3 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r3.<init>();	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r4 = r66;
        r1 = r3.createMovie(r1, r4);	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r14.mediaMuxer = r1;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1.<init>();	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r14.extractor = r1;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r1.setDataSource(r13);	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r3 = r75;
        r1 = (float) r3;	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        r18 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r18 = r1 / r18;
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0cb0, all -> 0x0cab }
        if (r77 == 0) goto L_0x0CLASSNAME;
    L_0x004c:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r3 = 0;
        r4 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r1 = -1;
        if (r10 == r1) goto L_0x0066;
    L_0x0056:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x005e, all -> 0x0cab }
        r3 = 1;
        r1 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x005e, all -> 0x0cab }
        goto L_0x0067;
    L_0x005e:
        r0 = move-exception;
        r1 = r0;
        r13 = r9;
        r3 = 0;
        r60 = 1;
        goto L_0x0cba;
    L_0x0066:
        r1 = -1;
    L_0x0067:
        if (r4 < 0) goto L_0x0c4d;
    L_0x0069:
        r20 = -1;
        r22 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r3 = r22.toLowerCase();	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r22 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r6 = "video/avc";
        r27 = 4;
        r5 = 18;
        if (r2 >= r5) goto L_0x012e;
    L_0x007e:
        r2 = org.telegram.messenger.MediaController.selectCodec(r6);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r28 = org.telegram.messenger.MediaController.selectColorFormat(r2, r6);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r28 == 0) goto L_0x0116;
    L_0x0088:
        r5 = r2.getName();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r13 = "OMX.qcom.";
        r13 = r5.contains(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r13 == 0) goto L_0x00b0;
    L_0x0094:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r13 = 16;
        if (r5 != r13) goto L_0x00ad;
    L_0x009a:
        r5 = "lge";
        r5 = r3.equals(r5);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r5 != 0) goto L_0x00aa;
    L_0x00a2:
        r5 = "nokia";
        r5 = r3.equals(r5);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r5 == 0) goto L_0x00ad;
    L_0x00aa:
        r5 = 1;
    L_0x00ab:
        r13 = 1;
        goto L_0x00da;
    L_0x00ad:
        r5 = 1;
    L_0x00ae:
        r13 = 0;
        goto L_0x00da;
    L_0x00b0:
        r13 = "OMX.Intel.";
        r13 = r5.contains(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r13 == 0) goto L_0x00ba;
    L_0x00b8:
        r5 = 2;
        goto L_0x00ae;
    L_0x00ba:
        r13 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r13 = r5.equals(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r13 == 0) goto L_0x00c4;
    L_0x00c2:
        r5 = 3;
        goto L_0x00ae;
    L_0x00c4:
        r13 = "OMX.SEC.AVC.Encoder";
        r13 = r5.equals(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r13 == 0) goto L_0x00ce;
    L_0x00cc:
        r5 = 4;
        goto L_0x00ab;
    L_0x00ce:
        r13 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r5 = r5.equals(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r5 == 0) goto L_0x00d8;
    L_0x00d6:
        r5 = 5;
        goto L_0x00ae;
    L_0x00d8:
        r5 = 0;
        goto L_0x00ae;
    L_0x00da:
        r30 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r30 == 0) goto L_0x010d;
    L_0x00de:
        r30 = r5;
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r5.<init>();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r31 = r13;
        r13 = "codec = ";
        r5.append(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = r2.getName();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r5.append(r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = " manufacturer = ";
        r5.append(r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r5.append(r3);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = "device = ";
        r5.append(r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r5.append(r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = r5.toString();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        goto L_0x0111;
    L_0x010d:
        r30 = r5;
        r31 = r13;
    L_0x0111:
        r13 = r28;
        r2 = r30;
        goto L_0x0137;
    L_0x0116:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        throw r1;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
    L_0x011e:
        r0 = move-exception;
        r1 = r0;
        r9 = r4;
        r13 = r12;
    L_0x0122:
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
    L_0x0126:
        r53 = 0;
        r57 = 0;
    L_0x012a:
        r60 = 1;
        goto L_0x0bc4;
    L_0x012e:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r13 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r31 = 0;
    L_0x0137:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        if (r5 == 0) goto L_0x0152;
    L_0x013b:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r5.<init>();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r28 = r1;
        r1 = "colorFormat = ";
        r5.append(r1);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r5.append(r13);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r1 = r5.toString();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        goto L_0x0154;
    L_0x0152:
        r28 = r1;
    L_0x0154:
        r1 = r12 * r11;
        r5 = r1 * 3;
        r26 = 2;
        r5 = r5 / 2;
        if (r2 != 0) goto L_0x0175;
    L_0x015e:
        r1 = r11 % 16;
        if (r1 == 0) goto L_0x01aa;
    L_0x0162:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
    L_0x0170:
        r5 = r5 + r2;
        r15 = r1;
    L_0x0172:
        r24 = r5;
        goto L_0x01ad;
    L_0x0175:
        r15 = 1;
        if (r2 != r15) goto L_0x018c;
    L_0x0178:
        r2 = r3.toLowerCase();	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r3 = "lge";
        r2 = r2.equals(r3);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r2 != 0) goto L_0x01aa;
    L_0x0184:
        r2 = r1 + 2047;
        r2 = r2 & -2048;
        r2 = r2 - r1;
        r5 = r5 + r2;
        r15 = r2;
        goto L_0x0172;
    L_0x018c:
        r1 = 5;
        if (r2 != r1) goto L_0x0190;
    L_0x018f:
        goto L_0x01aa;
    L_0x0190:
        r1 = 3;
        if (r2 != r1) goto L_0x01aa;
    L_0x0193:
        r1 = "baidu";
        r1 = r3.equals(r1);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        if (r1 == 0) goto L_0x01aa;
    L_0x019b:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
        goto L_0x0170;
    L_0x01aa:
        r24 = r5;
        r15 = 0;
    L_0x01ad:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r1.selectTrack(r4);	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r1 = r1.getTrackFormat(r4);	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r2 = 0;
        r5 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r5 <= 0) goto L_0x01c7;
    L_0x01be:
        r5 = r14.extractor;	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r2 = 0;
        r5.seekTo(r7, r2);	 Catch:{ Exception -> 0x011e, all -> 0x0cab }
        r25 = r4;
        goto L_0x01d1;
    L_0x01c7:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0bb3, all -> 0x0cab }
        r25 = r4;
        r3 = 0;
        r5 = 0;
        r2.seekTo(r3, r5);	 Catch:{ Exception -> 0x0bae, all -> 0x0cab }
    L_0x01d1:
        if (r10 > 0) goto L_0x01d7;
    L_0x01d3:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x01d8;
    L_0x01d7:
        r2 = r10;
    L_0x01d8:
        r3 = android.media.MediaFormat.createVideoFormat(r6, r12, r11);	 Catch:{ Exception -> 0x0ba1, all -> 0x0cab }
        r4 = "color-format";
        r3.setInteger(r4, r13);	 Catch:{ Exception -> 0x0ba1, all -> 0x0cab }
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0ba1, all -> 0x0cab }
        r4 = "frame-rate";
        r3.setInteger(r4, r9);	 Catch:{ Exception -> 0x0ba1, all -> 0x0cab }
        r4 = "i-frame-interval";
        r5 = 2;
        r3.setInteger(r4, r5);	 Catch:{ Exception -> 0x0ba1, all -> 0x0cab }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ba1, all -> 0x0cab }
        r5 = 23;
        if (r4 >= r5) goto L_0x0215;
    L_0x01f7:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x020d, all -> 0x0cab }
        r5 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 > r5) goto L_0x0215;
    L_0x01ff:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        if (r2 <= r4) goto L_0x0207;
    L_0x0204:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x0207:
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x020d, all -> 0x0cab }
        goto L_0x0215;
    L_0x020d:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
    L_0x0210:
        r13 = r12;
        r9 = r25;
        goto L_0x0122;
    L_0x0215:
        r10 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b9d, all -> 0x0cab }
        r4 = 18;
        if (r2 >= r4) goto L_0x022c;
    L_0x021c:
        r2 = "stride";
        r4 = r12 + 32;
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x0229, all -> 0x0cab }
        r2 = "slice-height";
        r3.setInteger(r2, r11);	 Catch:{ Exception -> 0x0229, all -> 0x0cab }
        goto L_0x022c;
    L_0x0229:
        r0 = move-exception;
        r1 = r0;
        goto L_0x0210;
    L_0x022c:
        r5 = android.media.MediaCodec.createEncoderByType(r6);	 Catch:{ Exception -> 0x0b9d, all -> 0x0cab }
        r2 = 0;
        r4 = 1;
        r5.configure(r3, r2, r2, r4);	 Catch:{ Exception -> 0x0b8a, all -> 0x0cab }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b7a, all -> 0x0cab }
        r3 = 18;
        if (r2 < r3) goto L_0x025d;
    L_0x023b:
        r3 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x0253, all -> 0x0cab }
        r2 = r5.createInputSurface();	 Catch:{ Exception -> 0x0253, all -> 0x0cab }
        r3.<init>(r2);	 Catch:{ Exception -> 0x0253, all -> 0x0cab }
        r3.makeCurrent();	 Catch:{ Exception -> 0x0249, all -> 0x0cab }
        r4 = r3;
        goto L_0x025e;
    L_0x0249:
        r0 = move-exception;
        r1 = r0;
        r4 = r3;
        r13 = r12;
        r9 = r25;
        r2 = 0;
        r3 = 0;
        goto L_0x0126;
    L_0x0253:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r9 = r25;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        goto L_0x0126;
    L_0x025d:
        r4 = 0;
    L_0x025e:
        r5.start();	 Catch:{ Exception -> 0x0b65, all -> 0x0cab }
        r2 = "mime";
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x0b65, all -> 0x0cab }
        r3 = android.media.MediaCodec.createDecoderByType(r2);	 Catch:{ Exception -> 0x0b65, all -> 0x0cab }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b4f, all -> 0x0cab }
        r70 = r10;
        r10 = 18;
        if (r2 < r10) goto L_0x0283;
    L_0x0273:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0279, all -> 0x0cab }
        r2.<init>();	 Catch:{ Exception -> 0x0279, all -> 0x0cab }
        goto L_0x028a;
    L_0x0279:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r13 = r12;
        r9 = r25;
        r2 = 0;
        goto L_0x0126;
    L_0x0283:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0b3d, all -> 0x0cab }
        r10 = r65;
        r2.<init>(r12, r11, r10);	 Catch:{ Exception -> 0x0b3d, all -> 0x0cab }
    L_0x028a:
        r10 = r2.getSurface();	 Catch:{ Exception -> 0x0b26, all -> 0x0cab }
        r23 = r15;
        r9 = 0;
        r15 = 0;
        r3.configure(r1, r10, r9, r15);	 Catch:{ Exception -> 0x0b18, all -> 0x0cab }
        r3.start();	 Catch:{ Exception -> 0x0b18, all -> 0x0cab }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b18, all -> 0x0cab }
        r10 = 21;
        if (r1 >= r10) goto L_0x02bf;
    L_0x029e:
        r1 = r3.getInputBuffers();	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        r15 = r5.getOutputBuffers();	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        r10 = 18;
        if (r9 >= r10) goto L_0x02b3;
    L_0x02ac:
        r9 = r5.getInputBuffers();	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        r10 = r9;
        r9 = r1;
        goto L_0x02c2;
    L_0x02b3:
        r9 = r1;
        r10 = 0;
        goto L_0x02c2;
    L_0x02b6:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r13 = r12;
    L_0x02bb:
        r9 = r25;
        goto L_0x0126;
    L_0x02bf:
        r9 = 0;
        r10 = 0;
        r15 = 0;
    L_0x02c2:
        if (r28 < 0) goto L_0x03b0;
    L_0x02c4:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r34 = r15;
        r15 = r28;
        r1 = r1.getTrackFormat(r15);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r28 = r15;
        r15 = "mime";
        r15 = r1.getString(r15);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r42 = r13;
        r13 = "audio/mp4a-latm";
        r13 = r15.equals(r13);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        if (r13 != 0) goto L_0x02f1;
    L_0x02e0:
        r13 = "mime";
        r13 = r1.getString(r13);	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        r15 = "audio/mpeg";
        r13 = r13.equals(r15);	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        if (r13 == 0) goto L_0x02ef;
    L_0x02ee:
        goto L_0x02f1;
    L_0x02ef:
        r13 = 0;
        goto L_0x02f2;
    L_0x02f1:
        r13 = 1;
    L_0x02f2:
        r15 = "mime";
        r15 = r1.getString(r15);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r43 = r10;
        r10 = "audio/unknown";
        r10 = r15.equals(r10);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        if (r10 == 0) goto L_0x0304;
    L_0x0302:
        r10 = -1;
        goto L_0x0306;
    L_0x0304:
        r10 = r28;
    L_0x0306:
        if (r10 < 0) goto L_0x0399;
    L_0x0308:
        if (r13 == 0) goto L_0x034d;
    L_0x030a:
        r15 = r14.mediaMuxer;	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r35 = r13;
        r13 = 1;
        r15 = r15.addTrack(r1, r13);	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r13 = r14.extractor;	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r13.selectTrack(r10);	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r13 = "max-input-size";
        r1 = r1.getInteger(r13);	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r1 = java.nio.ByteBuffer.allocateDirect(r1);	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r32 = 0;
        r13 = (r7 > r32 ? 1 : (r7 == r32 ? 0 : -1));
        if (r13 <= 0) goto L_0x0331;
    L_0x0328:
        r13 = r14.extractor;	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        r28 = r1;
        r1 = 0;
        r13.seekTo(r7, r1);	 Catch:{ Exception -> 0x02b6, all -> 0x0cab }
        goto L_0x033b;
    L_0x0331:
        r28 = r1;
        r1 = r14.extractor;	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        r11 = 0;
        r13 = 0;
        r1.seekTo(r11, r13);	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
    L_0x033b:
        r12 = r10;
        r1 = r15;
        r15 = r35;
        r13 = 0;
        r10 = r73;
        r61 = r28;
        r28 = r4;
        r4 = r61;
        goto L_0x03c0;
    L_0x034a:
        r0 = move-exception;
        goto L_0x03a9;
    L_0x034d:
        r35 = r13;
        r11 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r11.<init>();	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r13 = r63;
        r11.setDataSource(r13);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r11.selectTrack(r10);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r12 = 0;
        r15 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1));
        if (r15 <= 0) goto L_0x0367;
    L_0x0362:
        r15 = 0;
        r11.seekTo(r7, r15);	 Catch:{ Exception -> 0x034a, all -> 0x0cab }
        goto L_0x036b;
    L_0x0367:
        r15 = 0;
        r11.seekTo(r12, r15);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
    L_0x036b:
        r12 = new org.telegram.messenger.video.AudioRecoder;	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r12.<init>(r1, r11, r10);	 Catch:{ Exception -> 0x03a6, all -> 0x0cab }
        r12.startTime = r7;	 Catch:{ Exception -> 0x038b, all -> 0x0cab }
        r1 = r10;
        r10 = r73;
        r12.endTime = r10;	 Catch:{ Exception -> 0x0389, all -> 0x0cab }
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0389, all -> 0x0cab }
        r15 = r12.format;	 Catch:{ Exception -> 0x0389, all -> 0x0cab }
        r28 = r1;
        r1 = 1;
        r13 = r13.addTrack(r15, r1);	 Catch:{ Exception -> 0x0389, all -> 0x0cab }
        r1 = r13;
        r15 = r35;
        r13 = r12;
        r12 = r28;
        goto L_0x03bd;
    L_0x0389:
        r0 = move-exception;
        goto L_0x038e;
    L_0x038b:
        r0 = move-exception;
        r10 = r73;
    L_0x038e:
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r57 = r12;
        r9 = r25;
        goto L_0x0581;
    L_0x0399:
        r28 = r10;
        r35 = r13;
        r10 = r73;
        r12 = r28;
        r15 = r35;
        r1 = -5;
        r13 = 0;
        goto L_0x03bd;
    L_0x03a6:
        r0 = move-exception;
        r10 = r73;
    L_0x03a9:
        r13 = r67;
        r10 = r70;
        r1 = r0;
        goto L_0x02bb;
    L_0x03b0:
        r43 = r10;
        r42 = r13;
        r34 = r15;
        r10 = r73;
        r12 = r28;
        r1 = -5;
        r13 = 0;
        r15 = 1;
    L_0x03bd:
        r28 = r4;
        r4 = 0;
    L_0x03c0:
        if (r12 >= 0) goto L_0x03c5;
    L_0x03c2:
        r35 = 1;
        goto L_0x03c7;
    L_0x03c5:
        r35 = 0;
    L_0x03c7:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0b00, all -> 0x0cab }
        r49 = r20;
        r46 = r34;
        r20 = 0;
        r21 = 0;
        r44 = 0;
        r45 = -5;
        r47 = 0;
        r48 = 1;
        r51 = 0;
    L_0x03dc:
        if (r20 == 0) goto L_0x03f8;
    L_0x03de:
        if (r15 != 0) goto L_0x03e3;
    L_0x03e0:
        if (r35 != 0) goto L_0x03e3;
    L_0x03e2:
        goto L_0x03f8;
    L_0x03e3:
        r12 = r68;
        r11 = r69;
        r10 = r70;
        r57 = r13;
        r9 = r25;
        r4 = r28;
        r1 = 0;
        r17 = 0;
        r60 = 1;
        r13 = r67;
        goto L_0x0CLASSNAME;
    L_0x03f8:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0b00, all -> 0x0cab }
        if (r15 != 0) goto L_0x0422;
    L_0x03fd:
        if (r13 == 0) goto L_0x0422;
    L_0x03ff:
        r53 = r2;
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x040e, all -> 0x0cab }
        r2 = r13.step(r2, r1);	 Catch:{ Exception -> 0x040e, all -> 0x0cab }
        r54 = r2;
        r56 = r5;
        r55 = r6;
        goto L_0x042a;
    L_0x040e:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r57 = r13;
        r9 = r25;
        r4 = r28;
        r2 = r53;
    L_0x041a:
        r53 = 0;
        r60 = 1;
        r13 = r67;
        goto L_0x0bc4;
    L_0x0422:
        r53 = r2;
        r56 = r5;
        r55 = r6;
        r54 = r35;
    L_0x042a:
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        if (r21 != 0) goto L_0x0585;
    L_0x042e:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0571, all -> 0x0cab }
        r2 = r2.getSampleTrackIndex();	 Catch:{ Exception -> 0x0571, all -> 0x0cab }
        r57 = r13;
        r13 = r25;
        if (r2 != r13) goto L_0x049d;
    L_0x043a:
        r2 = r3.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        if (r2 < 0) goto L_0x0481;
    L_0x0440:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r6 = 21;
        if (r5 >= r6) goto L_0x0449;
    L_0x0446:
        r5 = r9[r2];	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        goto L_0x044d;
    L_0x0449:
        r5 = r3.getInputBuffer(r2);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
    L_0x044d:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r25 = r9;
        r9 = 0;
        r37 = r6.readSampleData(r5, r9);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        if (r37 >= 0) goto L_0x046a;
    L_0x0458:
        r36 = 0;
        r37 = 0;
        r38 = 0;
        r40 = 4;
        r34 = r3;
        r35 = r2;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r21 = 1;
        goto L_0x0483;
    L_0x046a:
        r36 = 0;
        r5 = r14.extractor;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r38 = r5.getSampleTime();	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r40 = 0;
        r34 = r3;
        r35 = r2;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r2.advance();	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        goto L_0x0483;
    L_0x0481:
        r25 = r9;
    L_0x0483:
        r59 = r4;
        r9 = r12;
        r60 = r13;
        r4 = r21;
        r6 = r22;
        r22 = r1;
        goto L_0x0548;
    L_0x0490:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r9 = r13;
        r4 = r28;
        r2 = r53;
        r5 = r56;
        goto L_0x041a;
    L_0x049d:
        r25 = r9;
        if (r15 == 0) goto L_0x0536;
    L_0x04a1:
        r5 = -1;
        if (r12 == r5) goto L_0x0536;
    L_0x04a4:
        if (r2 != r12) goto L_0x0536;
    L_0x04a6:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r5 = 0;
        r2 = r2.readSampleData(r4, r5);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r6 = r22;
        r6.size = r2;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r9 = 21;
        if (r2 >= r9) goto L_0x04bf;
    L_0x04b7:
        r4.position(r5);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r2 = r6.size;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r4.limit(r2);	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
    L_0x04bf:
        r2 = r6.size;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        if (r2 < 0) goto L_0x04d3;
    L_0x04c3:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0490, all -> 0x0cab }
        r9 = r12;
        r5 = r13;
        r12 = r2.getSampleTime();	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r6.presentationTimeUs = r12;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r2 = r14.extractor;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r2.advance();	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        goto L_0x04da;
    L_0x04d3:
        r9 = r12;
        r5 = r13;
        r2 = 0;
        r6.size = r2;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r21 = 1;
    L_0x04da:
        r2 = r6.size;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        if (r2 <= 0) goto L_0x0527;
    L_0x04de:
        r12 = 0;
        r2 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
        if (r2 < 0) goto L_0x04ea;
    L_0x04e4:
        r12 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r2 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r2 >= 0) goto L_0x0527;
    L_0x04ea:
        r2 = 0;
        r6.offset = r2;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r12 = r14.extractor;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r12 = r12.getSampleFlags();	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r6.flags = r12;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r12 = r14.mediaMuxer;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r12 = r12.writeSampleData(r1, r4, r6, r2);	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r32 = 0;
        r2 = (r12 > r32 ? 1 : (r12 == r32 ? 0 : -1));
        if (r2 == 0) goto L_0x0527;
    L_0x0501:
        r2 = r14.callback;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        if (r2 == 0) goto L_0x0527;
    L_0x0505:
        r22 = r1;
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r1 = r1 - r7;
        r34 = (r1 > r51 ? 1 : (r1 == r51 ? 0 : -1));
        if (r34 <= 0) goto L_0x0512;
    L_0x050e:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r51 = r1 - r7;
    L_0x0512:
        r59 = r4;
        r1 = r51;
        r4 = r14.callback;	 Catch:{ Exception -> 0x052e, all -> 0x0cab }
        r60 = r5;
        r5 = (float) r1;
        r34 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r5 = r5 / r34;
        r5 = r5 / r18;
        r4.didWriteData(r12, r5);	 Catch:{ Exception -> 0x0562, all -> 0x0cab }
        r51 = r1;
        goto L_0x0546;
    L_0x0527:
        r22 = r1;
        r59 = r4;
        r60 = r5;
        goto L_0x0546;
    L_0x052e:
        r0 = move-exception;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r9 = r5;
        goto L_0x057b;
    L_0x0536:
        r59 = r4;
        r9 = r12;
        r60 = r13;
        r6 = r22;
        r22 = r1;
        r1 = -1;
        if (r2 != r1) goto L_0x0546;
    L_0x0542:
        r4 = r21;
        r1 = 1;
        goto L_0x0549;
    L_0x0546:
        r4 = r21;
    L_0x0548:
        r1 = 0;
    L_0x0549:
        if (r1 == 0) goto L_0x0594;
    L_0x054b:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r35 = r3.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x0562, all -> 0x0cab }
        if (r35 < 0) goto L_0x0594;
    L_0x0553:
        r36 = 0;
        r37 = 0;
        r38 = 0;
        r40 = 4;
        r34 = r3;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0562, all -> 0x0cab }
        r4 = 1;
        goto L_0x0594;
    L_0x0562:
        r0 = move-exception;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r4 = r28;
        r2 = r53;
        r5 = r56;
    L_0x056e:
        r9 = r60;
        goto L_0x0581;
    L_0x0571:
        r0 = move-exception;
        r57 = r13;
        r13 = r67;
        r10 = r70;
        r1 = r0;
        r9 = r25;
    L_0x057b:
        r4 = r28;
        r2 = r53;
        r5 = r56;
    L_0x0581:
        r53 = 0;
        goto L_0x012a;
    L_0x0585:
        r59 = r4;
        r57 = r13;
        r6 = r22;
        r60 = r25;
        r22 = r1;
        r25 = r9;
        r9 = r12;
        r4 = r21;
    L_0x0594:
        r1 = r44 ^ 1;
        r12 = r1;
        r21 = r4;
        r2 = r45;
        r1 = 1;
    L_0x059c:
        if (r12 != 0) goto L_0x05ba;
    L_0x059e:
        if (r1 == 0) goto L_0x05a1;
    L_0x05a0:
        goto L_0x05ba;
    L_0x05a1:
        r45 = r2;
        r12 = r9;
        r1 = r22;
        r9 = r25;
        r2 = r53;
        r35 = r54;
        r5 = r56;
        r13 = r57;
        r4 = r59;
        r25 = r60;
        r22 = r6;
        r6 = r55;
        goto L_0x03dc;
    L_0x05ba:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0ae9, all -> 0x0cab }
        if (r78 == 0) goto L_0x05c7;
    L_0x05bf:
        r4 = 22000; // 0x55f0 float:3.0829E-41 double:1.08694E-319;
        r39 = r12;
        r12 = r4;
        r5 = r56;
        goto L_0x05cd;
    L_0x05c7:
        r39 = r12;
        r5 = r56;
        r12 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x05cd:
        r4 = r5.dequeueOutputBuffer(r6, r12);	 Catch:{ Exception -> 0x0ad6, all -> 0x0cab }
        r12 = -1;
        if (r4 != r12) goto L_0x05e6;
    L_0x05d4:
        r13 = r67;
        r12 = r2;
        r45 = r9;
        r11 = r20;
        r10 = r55;
        r1 = -1;
        r19 = 0;
    L_0x05e0:
        r20 = 2;
        r9 = r68;
        goto L_0x07bb;
    L_0x05e6:
        r12 = -3;
        if (r4 != r12) goto L_0x060c;
    L_0x05e9:
        r12 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = 21;
        if (r12 >= r13) goto L_0x05f3;
    L_0x05ef:
        r46 = r5.getOutputBuffers();	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
    L_0x05f3:
        r13 = r67;
        r19 = r1;
        r12 = r2;
        r45 = r9;
        r11 = r20;
    L_0x05fc:
        r10 = r55;
        r1 = -1;
        goto L_0x05e0;
    L_0x0600:
        r0 = move-exception;
        r13 = r67;
    L_0x0603:
        r10 = r70;
        r1 = r0;
        r4 = r28;
        r2 = r53;
        goto L_0x056e;
    L_0x060c:
        r12 = -2;
        if (r4 != r12) goto L_0x0656;
    L_0x060f:
        r12 = r5.getOutputFormat();	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = -5;
        if (r2 != r13) goto L_0x064a;
    L_0x0616:
        if (r12 == 0) goto L_0x064a;
    L_0x0618:
        r2 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = 0;
        r2 = r2.addTrack(r12, r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = "prepend-sps-pps-to-idr-frames";
        r13 = r12.containsKey(r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r13 == 0) goto L_0x064a;
    L_0x0627:
        r13 = "prepend-sps-pps-to-idr-frames";
        r13 = r12.getInteger(r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r34 = r1;
        r1 = 1;
        if (r13 != r1) goto L_0x064c;
    L_0x0632:
        r1 = "csd-0";
        r1 = r12.getByteBuffer(r1);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = "csd-1";
        r12 = r12.getByteBuffer(r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r1 = r1.limit();	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = r12.limit();	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r1 = r1 + r12;
        r47 = r1;
        goto L_0x064c;
    L_0x064a:
        r34 = r1;
    L_0x064c:
        r13 = r67;
        r12 = r2;
        r45 = r9;
        r11 = r20;
        r19 = r34;
        goto L_0x05fc;
    L_0x0656:
        r34 = r1;
        if (r4 < 0) goto L_0x0aa7;
    L_0x065a:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0ad6, all -> 0x0cab }
        r12 = 21;
        if (r1 >= r12) goto L_0x0663;
    L_0x0660:
        r1 = r46[r4];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        goto L_0x0667;
    L_0x0663:
        r1 = r5.getOutputBuffer(r4);	 Catch:{ Exception -> 0x0ad6, all -> 0x0cab }
    L_0x0667:
        if (r1 == 0) goto L_0x0a7c;
    L_0x0669:
        r13 = r6.size;	 Catch:{ Exception -> 0x0ad6, all -> 0x0cab }
        r12 = 1;
        if (r13 <= r12) goto L_0x079f;
    L_0x066e:
        r13 = r6.flags;	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r20 = 2;
        r13 = r13 & 2;
        if (r13 != 0) goto L_0x071a;
    L_0x0676:
        if (r47 == 0) goto L_0x0689;
    L_0x0678:
        r13 = r6.flags;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = r13 & r12;
        if (r13 == 0) goto L_0x0689;
    L_0x067d:
        r12 = r6.offset;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = r12 + r47;
        r6.offset = r12;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = r6.size;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = r12 - r47;
        r6.size = r12;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
    L_0x0689:
        if (r48 == 0) goto L_0x06e4;
    L_0x068b:
        r12 = r6.flags;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = 1;
        r12 = r12 & r13;
        if (r12 == 0) goto L_0x06e4;
    L_0x0691:
        r12 = r6.size;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = 100;
        if (r12 <= r13) goto L_0x06df;
    L_0x0697:
        r12 = r6.offset;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r1.position(r12);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = 100;
        r12 = new byte[r12];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r1.get(r12);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r45 = r9;
        r13 = 0;
        r26 = 0;
    L_0x06a8:
        r9 = r12.length;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9 = r9 + -4;
        if (r13 >= r9) goto L_0x06e1;
    L_0x06ad:
        r9 = r12[r13];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r9 != 0) goto L_0x06d8;
    L_0x06b1:
        r9 = r13 + 1;
        r9 = r12[r9];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r9 != 0) goto L_0x06d8;
    L_0x06b7:
        r9 = r13 + 2;
        r9 = r12[r9];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r9 != 0) goto L_0x06d8;
    L_0x06bd:
        r9 = r13 + 3;
        r9 = r12[r9];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r35 = r12;
        r12 = 1;
        if (r9 != r12) goto L_0x06da;
    L_0x06c6:
        r9 = r26 + 1;
        if (r9 <= r12) goto L_0x06d5;
    L_0x06ca:
        r9 = r6.offset;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9 = r9 + r13;
        r6.offset = r9;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9 = r6.size;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9 = r9 - r13;
        r6.size = r9;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        goto L_0x06e1;
    L_0x06d5:
        r26 = r9;
        goto L_0x06da;
    L_0x06d8:
        r35 = r12;
    L_0x06da:
        r13 = r13 + 1;
        r12 = r35;
        goto L_0x06a8;
    L_0x06df:
        r45 = r9;
    L_0x06e1:
        r48 = 0;
        goto L_0x06e6;
    L_0x06e4:
        r45 = r9;
    L_0x06e6:
        r9 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = 1;
        r9 = r9.writeSampleData(r2, r1, r6, r12);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r11 = 0;
        r1 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r1 == 0) goto L_0x0712;
    L_0x06f3:
        r1 = r14.callback;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r1 == 0) goto L_0x0712;
    L_0x06f7:
        r11 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r11 = r11 - r7;
        r1 = (r11 > r51 ? 1 : (r11 == r51 ? 0 : -1));
        if (r1 <= 0) goto L_0x0702;
    L_0x06fe:
        r11 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r51 = r11 - r7;
    L_0x0702:
        r11 = r51;
        r1 = r14.callback;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = (float) r11;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r26 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r13 = r13 / r26;
        r13 = r13 / r18;
        r1.didWriteData(r9, r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r51 = r11;
    L_0x0712:
        r13 = r67;
        r9 = r68;
        r10 = r55;
        goto L_0x07a9;
    L_0x071a:
        r45 = r9;
        r9 = -5;
        if (r2 != r9) goto L_0x0712;
    L_0x071f:
        r2 = r6.size;	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r2 = new byte[r2];	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r10 = r6.offset;	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r11 = r6.size;	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r10 = r10 + r11;
        r1.limit(r10);	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r10 = r6.offset;	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r1.position(r10);	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r1.get(r2);	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r1 = r6.size;	 Catch:{ Exception -> 0x0798, all -> 0x0cab }
        r10 = 1;
        r1 = r1 - r10;
    L_0x0737:
        r11 = 3;
        if (r1 < 0) goto L_0x0775;
    L_0x073a:
        if (r1 <= r11) goto L_0x0775;
    L_0x073c:
        r12 = r2[r1];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r12 != r10) goto L_0x0770;
    L_0x0740:
        r10 = r1 + -1;
        r10 = r2[r10];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r10 != 0) goto L_0x0770;
    L_0x0746:
        r10 = r1 + -2;
        r10 = r2[r10];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r10 != 0) goto L_0x0770;
    L_0x074c:
        r10 = r1 + -3;
        r12 = r2[r10];	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        if (r12 != 0) goto L_0x0770;
    L_0x0752:
        r1 = java.nio.ByteBuffer.allocate(r10);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = r6.size;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r12 = r12 - r10;
        r12 = java.nio.ByteBuffer.allocate(r12);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r13 = 0;
        r9 = r1.put(r2, r13, r10);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9.position(r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9 = r6.size;	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r9 = r9 - r10;
        r2 = r12.put(r2, r10, r9);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        r2.position(r13);	 Catch:{ Exception -> 0x0600, all -> 0x0cab }
        goto L_0x0777;
    L_0x0770:
        r1 = r1 + -1;
        r9 = -5;
        r10 = 1;
        goto L_0x0737;
    L_0x0775:
        r1 = 0;
        r12 = 0;
    L_0x0777:
        r13 = r67;
        r9 = r68;
        r10 = r55;
        r2 = android.media.MediaFormat.createVideoFormat(r10, r13, r9);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        if (r1 == 0) goto L_0x078f;
    L_0x0783:
        if (r12 == 0) goto L_0x078f;
    L_0x0785:
        r11 = "csd-0";
        r2.setByteBuffer(r11, r1);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1 = "csd-1";
        r2.setByteBuffer(r1, r12);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
    L_0x078f:
        r1 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r11 = 0;
        r1 = r1.addTrack(r2, r11);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r2 = r1;
        goto L_0x07a9;
    L_0x0798:
        r0 = move-exception;
        r13 = r67;
        r9 = r68;
        goto L_0x0603;
    L_0x079f:
        r13 = r67;
        r45 = r9;
        r10 = r55;
        r20 = 2;
        r9 = r68;
    L_0x07a9:
        r1 = r6.flags;	 Catch:{ Exception -> 0x0a45, all -> 0x0cab }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x07b1;
    L_0x07af:
        r1 = 1;
        goto L_0x07b2;
    L_0x07b1:
        r1 = 0;
    L_0x07b2:
        r11 = 0;
        r5.releaseOutputBuffer(r4, r11);	 Catch:{ Exception -> 0x0a45, all -> 0x0cab }
        r11 = r1;
        r12 = r2;
        r19 = r34;
        r1 = -1;
    L_0x07bb:
        if (r4 == r1) goto L_0x07ce;
    L_0x07bd:
        r56 = r5;
        r55 = r10;
        r20 = r11;
        r2 = r12;
        r1 = r19;
        r12 = r39;
        r9 = r45;
        r10 = r73;
        goto L_0x059c;
    L_0x07ce:
        if (r44 != 0) goto L_0x0a48;
    L_0x07d0:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r4 = r3.dequeueOutputBuffer(r6, r1);	 Catch:{ Exception -> 0x0a45, all -> 0x0cab }
        r1 = -1;
        if (r4 != r1) goto L_0x07fd;
    L_0x07d9:
        r1 = r5;
        r8 = r6;
        r41 = r22;
        r16 = r53;
        r22 = r59;
        r9 = r60;
        r2 = 18;
        r26 = -5;
        r29 = 2;
        r30 = 0;
        r39 = 0;
    L_0x07ed:
        r53 = 0;
        r55 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r28;
        r28 = r3;
    L_0x07f9:
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a5c;
    L_0x07fd:
        r2 = -3;
        if (r4 != r2) goto L_0x0802;
    L_0x0800:
        goto L_0x0a48;
    L_0x0802:
        r2 = -2;
        if (r4 != r2) goto L_0x0826;
    L_0x0805:
        r2 = r3.getOutputFormat();	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        if (r4 == 0) goto L_0x0a48;
    L_0x080d:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r4.<init>();	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1 = "newFormat = ";
        r4.append(r1);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r4.append(r2);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1 = r4.toString();	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        goto L_0x0a48;
    L_0x0823:
        r0 = move-exception;
        goto L_0x0603;
    L_0x0826:
        if (r4 < 0) goto L_0x0a21;
    L_0x0828:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a45, all -> 0x0cab }
        r2 = 18;
        if (r1 < r2) goto L_0x0838;
    L_0x082e:
        r1 = r6.size;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        if (r1 == 0) goto L_0x0834;
    L_0x0832:
        r1 = 1;
        goto L_0x0835;
    L_0x0834:
        r1 = 0;
    L_0x0835:
        r32 = 0;
        goto L_0x084a;
    L_0x0838:
        r1 = r6.size;	 Catch:{ Exception -> 0x0a45, all -> 0x0cab }
        if (r1 != 0) goto L_0x0847;
    L_0x083c:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r32 = 0;
        r34 = (r1 > r32 ? 1 : (r1 == r32 ? 0 : -1));
        if (r34 == 0) goto L_0x0845;
    L_0x0844:
        goto L_0x0849;
    L_0x0845:
        r1 = 0;
        goto L_0x084a;
    L_0x0847:
        r32 = 0;
    L_0x0849:
        r1 = 1;
    L_0x084a:
        r2 = (r73 > r32 ? 1 : (r73 == r32 ? 0 : -1));
        if (r2 <= 0) goto L_0x0865;
    L_0x084e:
        r34 = r1;
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r35 = (r1 > r73 ? 1 : (r1 == r73 ? 0 : -1));
        if (r35 < 0) goto L_0x0867;
    L_0x0856:
        r1 = r6.flags;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1 = r1 | 4;
        r6.flags = r1;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r21 = 1;
        r32 = 0;
        r34 = 0;
        r44 = 1;
        goto L_0x0869;
    L_0x0865:
        r34 = r1;
    L_0x0867:
        r32 = 0;
    L_0x0869:
        r1 = (r7 > r32 ? 1 : (r7 == r32 ? 0 : -1));
        if (r1 <= 0) goto L_0x08a1;
    L_0x086d:
        r1 = -1;
        r35 = (r49 > r1 ? 1 : (r49 == r1 ? 0 : -1));
        if (r35 != 0) goto L_0x08a1;
    L_0x0873:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r35 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r35 >= 0) goto L_0x089d;
    L_0x0879:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        if (r1 == 0) goto L_0x089b;
    L_0x087d:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1.<init>();	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r2 = "drop frame startTime = ";
        r1.append(r2);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1.append(r7);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r2 = " present time = ";
        r1.append(r2);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r7 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1.append(r7);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
    L_0x089b:
        r1 = 0;
        goto L_0x08a3;
    L_0x089d:
        r1 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x0823, all -> 0x0cab }
        r49 = r1;
    L_0x08a1:
        r1 = r34;
    L_0x08a3:
        r3.releaseOutputBuffer(r4, r1);	 Catch:{ Exception -> 0x0a45, all -> 0x0cab }
        if (r1 == 0) goto L_0x09b4;
    L_0x08a8:
        r53.awaitNewImage();	 Catch:{ Exception -> 0x08ad, all -> 0x0cab }
        r1 = 0;
        goto L_0x08b3;
    L_0x08ad:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x099c, all -> 0x0cab }
        r1 = 1;
    L_0x08b3:
        if (r1 != 0) goto L_0x09b4;
    L_0x08b5:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x099c, all -> 0x0cab }
        r7 = 18;
        if (r1 < r7) goto L_0x08fe;
    L_0x08bb:
        r2 = r53;
        r4 = 0;
        r2.drawImage(r4);	 Catch:{ Exception -> 0x08f0, all -> 0x0cab }
        r7 = r6.presentationTimeUs;	 Catch:{ Exception -> 0x08f0, all -> 0x0cab }
        r34 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 * r34;
        r1 = r28;
        r1.setPresentationTime(r7);	 Catch:{ Exception -> 0x08ee, all -> 0x0cab }
        r1.swapBuffers();	 Catch:{ Exception -> 0x08ee, all -> 0x0cab }
        r16 = r2;
        r28 = r3;
        r20 = r5;
        r8 = r6;
        r41 = r22;
        r55 = r32;
        r22 = r59;
        r9 = r60;
        r26 = -5;
        r29 = 2;
        r30 = 0;
        r53 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r1;
        goto L_0x09d1;
    L_0x08ee:
        r0 = move-exception;
        goto L_0x08f3;
    L_0x08f0:
        r0 = move-exception;
        r1 = r28;
    L_0x08f3:
        r10 = r70;
        r4 = r1;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        goto L_0x0b9b;
    L_0x08fe:
        r1 = r28;
        r2 = r53;
        r4 = 0;
        r7 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r17 = r5.dequeueInputBuffer(r7);	 Catch:{ Exception -> 0x0989, all -> 0x0cab }
        if (r17 < 0) goto L_0x0962;
    L_0x090b:
        r4 = 1;
        r2.drawImage(r4);	 Catch:{ Exception -> 0x0989, all -> 0x0cab }
        r16 = r2.getFrame();	 Catch:{ Exception -> 0x0989, all -> 0x0cab }
        r34 = r43[r17];	 Catch:{ Exception -> 0x0989, all -> 0x0cab }
        r34.clear();	 Catch:{ Exception -> 0x0989, all -> 0x0cab }
        r41 = r22;
        r8 = -1;
        r26 = -5;
        r22 = r1;
        r1 = r16;
        r16 = r2;
        r7 = r6;
        r55 = r32;
        r2 = r34;
        r28 = r3;
        r30 = 0;
        r53 = 0;
        r3 = r42;
        r6 = r60;
        r60 = 1;
        r61 = r59;
        r59 = r22;
        r22 = r61;
        r4 = r67;
        r20 = r5;
        r8 = 18;
        r29 = 2;
        r58 = 3;
        r5 = r68;
        r9 = r6;
        r6 = r23;
        r8 = r7;
        r7 = r31;
        org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        r34 = 0;
        r1 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        r38 = 0;
        r32 = r20;
        r33 = r17;
        r35 = r24;
        r36 = r1;
        r32.queueInputBuffer(r33, r34, r35, r36, r38);	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        goto L_0x09d1;
    L_0x0962:
        r16 = r2;
        r28 = r3;
        r20 = r5;
        r8 = r6;
        r41 = r22;
        r55 = r32;
        r22 = r59;
        r9 = r60;
        r26 = -5;
        r29 = 2;
        r30 = 0;
        r53 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r1;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        if (r1 == 0) goto L_0x09d1;
    L_0x0983:
        r1 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        goto L_0x09d1;
    L_0x0989:
        r0 = move-exception;
        r59 = r1;
        r16 = r2;
        r28 = r3;
        r20 = r5;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r10 = r70;
        r1 = r0;
        goto L_0x09b0;
    L_0x099c:
        r0 = move-exception;
        r20 = r5;
        r59 = r28;
        r16 = r53;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r28 = r3;
        r10 = r70;
        r1 = r0;
        r2 = r16;
    L_0x09b0:
        r4 = r59;
        goto L_0x0bc4;
    L_0x09b4:
        r20 = r5;
        r8 = r6;
        r41 = r22;
        r55 = r32;
        r16 = r53;
        r22 = r59;
        r9 = r60;
        r26 = -5;
        r29 = 2;
        r30 = 0;
        r53 = 0;
        r58 = 3;
        r60 = 1;
        r59 = r28;
        r28 = r3;
    L_0x09d1:
        r1 = r8.flags;	 Catch:{ Exception -> 0x0a1c, all -> 0x0cab }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x0a16;
    L_0x09d7:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0a1c, all -> 0x0cab }
        if (r1 == 0) goto L_0x09ec;
    L_0x09db:
        r1 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        goto L_0x09ec;
    L_0x09e1:
        r0 = move-exception;
        r10 = r70;
        r1 = r0;
        r2 = r16;
        r5 = r20;
        r3 = r28;
        goto L_0x09b0;
    L_0x09ec:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a1c, all -> 0x0cab }
        r2 = 18;
        if (r1 < r2) goto L_0x09fa;
    L_0x09f2:
        r20.signalEndOfInputStream();	 Catch:{ Exception -> 0x09e1, all -> 0x0cab }
        r1 = r20;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a13;
    L_0x09fa:
        r1 = r20;
        r3 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r35 = r1.dequeueInputBuffer(r3);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        if (r35 < 0) goto L_0x0a13;
    L_0x0a04:
        r36 = 0;
        r37 = 1;
        r5 = r8.presentationTimeUs;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r40 = 4;
        r34 = r1;
        r38 = r5;
        r34.queueInputBuffer(r35, r36, r37, r38, r40);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
    L_0x0a13:
        r39 = 0;
        goto L_0x0a5c;
    L_0x0a16:
        r1 = r20;
        r2 = 18;
        goto L_0x07f9;
    L_0x0a1c:
        r0 = move-exception;
        r1 = r20;
        goto L_0x0ace;
    L_0x0a21:
        r1 = r5;
        r59 = r28;
        r16 = r53;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r28 = r3;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3.<init>();	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r5 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r3.append(r5);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3.append(r4);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        throw r2;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
    L_0x0a45:
        r0 = move-exception;
        goto L_0x0ad9;
    L_0x0a48:
        r1 = r5;
        r8 = r6;
        r41 = r22;
        r16 = r53;
        r22 = r59;
        r9 = r60;
        r2 = 18;
        r26 = -5;
        r29 = 2;
        r30 = 0;
        goto L_0x07ed;
    L_0x0a5c:
        r56 = r1;
        r6 = r8;
        r60 = r9;
        r55 = r10;
        r20 = r11;
        r2 = r12;
        r53 = r16;
        r1 = r19;
        r3 = r28;
        r12 = r39;
        r9 = r45;
        r28 = r59;
        r7 = r71;
        r10 = r73;
        r59 = r22;
        r22 = r41;
        goto L_0x059c;
    L_0x0a7c:
        r13 = r67;
        r1 = r5;
        r59 = r28;
        r16 = r53;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r28 = r3;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3.<init>();	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r5 = "encoderOutputBuffer ";
        r3.append(r5);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3.append(r4);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r4 = " was null";
        r3.append(r4);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        throw r2;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
    L_0x0aa7:
        r13 = r67;
        r1 = r5;
        r59 = r28;
        r16 = r53;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r28 = r3;
        r2 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3.<init>();	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r5 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r3.append(r5);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3.append(r4);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
        throw r2;	 Catch:{ Exception -> 0x0acd, all -> 0x0cab }
    L_0x0acd:
        r0 = move-exception;
    L_0x0ace:
        r10 = r70;
        r5 = r1;
        r2 = r16;
        r3 = r28;
        goto L_0x0b14;
    L_0x0ad6:
        r0 = move-exception;
        r13 = r67;
    L_0x0ad9:
        r1 = r5;
        r59 = r28;
        r16 = r53;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r28 = r3;
        r10 = r70;
        goto L_0x0afd;
    L_0x0ae9:
        r0 = move-exception;
        r13 = r67;
        r59 = r28;
        r16 = r53;
        r1 = r56;
        r9 = r60;
        r53 = 0;
        r60 = 1;
        r28 = r3;
        r10 = r70;
        r5 = r1;
    L_0x0afd:
        r2 = r16;
        goto L_0x0b14;
    L_0x0b00:
        r0 = move-exception;
        r16 = r2;
        r1 = r5;
        r57 = r13;
        r9 = r25;
        r59 = r28;
        r53 = 0;
        r60 = 1;
        r13 = r67;
        r28 = r3;
        r10 = r70;
    L_0x0b14:
        r4 = r59;
        goto L_0x0b9b;
    L_0x0b18:
        r0 = move-exception;
        r16 = r2;
        r28 = r3;
        r59 = r4;
        r1 = r5;
        r30 = r9;
        r13 = r12;
        r9 = r25;
        goto L_0x0b33;
    L_0x0b26:
        r0 = move-exception;
        r16 = r2;
        r28 = r3;
        r59 = r4;
        r1 = r5;
        r13 = r12;
        r9 = r25;
        r30 = 0;
    L_0x0b33:
        r53 = 0;
        r60 = 1;
        r10 = r70;
        r57 = r30;
        goto L_0x0b9b;
    L_0x0b3d:
        r0 = move-exception;
        r28 = r3;
        r59 = r4;
        r1 = r5;
        r13 = r12;
        r9 = r25;
        r30 = 0;
        r53 = 0;
        r60 = 1;
        r10 = r70;
        goto L_0x0b60;
    L_0x0b4f:
        r0 = move-exception;
        r28 = r3;
        r59 = r4;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r30 = 0;
        r53 = 0;
        r60 = 1;
    L_0x0b60:
        r2 = r30;
        r57 = r2;
        goto L_0x0b9b;
    L_0x0b65:
        r0 = move-exception;
        r59 = r4;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r30 = 0;
        r53 = 0;
        r60 = 1;
        r2 = r30;
        r3 = r2;
        r57 = r3;
        goto L_0x0b9b;
    L_0x0b7a:
        r0 = move-exception;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r30 = 0;
        r53 = 0;
        r60 = 1;
        r2 = r30;
        goto L_0x0b97;
    L_0x0b8a:
        r0 = move-exception;
        r30 = r2;
        r1 = r5;
        r70 = r10;
        r13 = r12;
        r9 = r25;
        r53 = 0;
        r60 = 1;
    L_0x0b97:
        r3 = r2;
        r4 = r3;
        r57 = r4;
    L_0x0b9b:
        r1 = r0;
        goto L_0x0bc4;
    L_0x0b9d:
        r0 = move-exception;
        r70 = r10;
        goto L_0x0baf;
    L_0x0ba1:
        r0 = move-exception;
        r13 = r12;
        r9 = r25;
        r30 = 0;
        r53 = 0;
        r60 = 1;
        r1 = r0;
        r10 = r2;
        goto L_0x0bbd;
    L_0x0bae:
        r0 = move-exception;
    L_0x0baf:
        r13 = r12;
        r9 = r25;
        goto L_0x0bb6;
    L_0x0bb3:
        r0 = move-exception;
        r9 = r4;
        r13 = r12;
    L_0x0bb6:
        r30 = 0;
        r53 = 0;
        r60 = 1;
        r1 = r0;
    L_0x0bbd:
        r2 = r30;
        r3 = r2;
        r4 = r3;
        r5 = r4;
        r57 = r5;
    L_0x0bc4:
        r6 = r1 instanceof java.lang.IllegalStateException;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        if (r6 == 0) goto L_0x0bcc;
    L_0x0bc8:
        if (r78 != 0) goto L_0x0bcc;
    L_0x0bca:
        r53 = 1;
    L_0x0bcc:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c3c, all -> 0x0cab }
        r6.<init>();	 Catch:{ Exception -> 0x0c3c, all -> 0x0cab }
        r7 = "bitrate: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0c3c, all -> 0x0cab }
        r6.append(r10);	 Catch:{ Exception -> 0x0c3c, all -> 0x0cab }
        r7 = " framerate: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0c3c, all -> 0x0cab }
        r11 = r69;
        r6.append(r11);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r7 = " size: ";
        r6.append(r7);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r12 = r68;
        r6.append(r12);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r7 = "x";
        r6.append(r7);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r6.append(r13);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        org.telegram.messenger.FileLog.e(r6);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cab }
        r17 = r53;
        r1 = 1;
    L_0x0CLASSNAME:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
        r6.unselectTrack(r9);	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
        if (r2 == 0) goto L_0x0c0d;
    L_0x0c0a:
        r2.release();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
    L_0x0c0d:
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0c0f:
        r4.release();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
    L_0x0CLASSNAME:
        if (r3 == 0) goto L_0x0c1a;
    L_0x0CLASSNAME:
        r3.stop();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
        r3.release();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
    L_0x0c1a:
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0c1c:
        r5.stop();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
        r5.release();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
    L_0x0CLASSNAME:
        if (r57 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r57.release();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
    L_0x0CLASSNAME:
        r62.checkConversionCanceled();	 Catch:{ Exception -> 0x0c2d, all -> 0x0cab }
        r3 = r17;
        goto L_0x0CLASSNAME;
    L_0x0c2d:
        r0 = move-exception;
        r1 = r0;
        r13 = r11;
        r3 = r17;
        goto L_0x0cba;
    L_0x0CLASSNAME:
        r0 = move-exception;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r12 = r68;
    L_0x0CLASSNAME:
        r1 = r0;
        r13 = r11;
        goto L_0x0CLASSNAME;
    L_0x0c3c:
        r0 = move-exception;
        r12 = r68;
        r13 = r69;
        r1 = r0;
    L_0x0CLASSNAME:
        r3 = r53;
        goto L_0x0cba;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r12 = r68;
        r13 = r69;
        goto L_0x0cb8;
    L_0x0c4d:
        r13 = r12;
        r53 = 0;
        r12 = r11;
        r11 = r9;
        r1 = 0;
        r3 = 0;
    L_0x0CLASSNAME:
        r4 = r1;
        r13 = r11;
        goto L_0x0c8b;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r13 = r12;
        r53 = 0;
        r60 = 1;
        r12 = r11;
        r1 = r0;
        r13 = r9;
        goto L_0x0cb9;
    L_0x0CLASSNAME:
        r8 = r2;
        r13 = r12;
        r53 = 0;
        r60 = 1;
        r12 = r11;
        r11 = r9;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0ca8, all -> 0x0cab }
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0ca8, all -> 0x0cab }
        r1 = -1;
        if (r10 == r1) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r15 = 1;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r15 = 0;
    L_0x0CLASSNAME:
        r1 = r62;
        r4 = r8;
        r5 = r71;
        r7 = r73;
        r12 = r11;
        r9 = r75;
        r13 = r68;
        r11 = r64;
        r13 = r12;
        r12 = r15;
        r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12);	 Catch:{ Exception -> 0x0ca6, all -> 0x0cab }
        r10 = r70;
        r3 = 0;
        r4 = 0;
    L_0x0c8b:
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
        r6 = r67;
        r7 = r68;
        r60 = r4;
        goto L_0x0d02;
    L_0x0ca6:
        r0 = move-exception;
        goto L_0x0cb6;
    L_0x0ca8:
        r0 = move-exception;
        r13 = r11;
        goto L_0x0cb6;
    L_0x0cab:
        r0 = move-exception;
        r2 = r0;
        r1 = r14;
        goto L_0x0d29;
    L_0x0cb0:
        r0 = move-exception;
        r13 = r9;
        r53 = 0;
        r60 = 1;
    L_0x0cb6:
        r10 = r70;
    L_0x0cb8:
        r1 = r0;
    L_0x0cb9:
        r3 = 0;
    L_0x0cba:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0d25 }
        r2.<init>();	 Catch:{ all -> 0x0d25 }
        r4 = "bitrate: ";
        r2.append(r4);	 Catch:{ all -> 0x0d25 }
        r2.append(r10);	 Catch:{ all -> 0x0d25 }
        r4 = " framerate: ";
        r2.append(r4);	 Catch:{ all -> 0x0d25 }
        r2.append(r13);	 Catch:{ all -> 0x0d25 }
        r4 = " size: ";
        r2.append(r4);	 Catch:{ all -> 0x0d25 }
        r7 = r68;
        r2.append(r7);	 Catch:{ all -> 0x0d25 }
        r4 = "x";
        r2.append(r4);	 Catch:{ all -> 0x0d25 }
        r6 = r67;
        r2.append(r6);	 Catch:{ all -> 0x0d25 }
        r2 = r2.toString();	 Catch:{ all -> 0x0d25 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0d25 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0d25 }
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0cf5;
    L_0x0cf2:
        r1.release();
    L_0x0cf5:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0d02;
    L_0x0cf9:
        r1.finishMovie();	 Catch:{ Exception -> 0x0cfd }
        goto L_0x0d02;
    L_0x0cfd:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0d02:
        r9 = r10;
        if (r3 == 0) goto L_0x0d24;
    L_0x0d05:
        r17 = 1;
        r1 = r62;
        r2 = r63;
        r3 = r64;
        r4 = r65;
        r5 = r66;
        r6 = r67;
        r7 = r68;
        r8 = r69;
        r10 = r71;
        r12 = r73;
        r14 = r75;
        r16 = r77;
        r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17);
        return r1;
    L_0x0d24:
        return r60;
    L_0x0d25:
        r0 = move-exception;
        r1 = r62;
        r2 = r0;
    L_0x0d29:
        r3 = r1.extractor;
        if (r3 == 0) goto L_0x0d30;
    L_0x0d2d:
        r3.release();
    L_0x0d30:
        r3 = r1.mediaMuxer;
        if (r3 == 0) goto L_0x0d3d;
    L_0x0d34:
        r3.finishMovie();	 Catch:{ Exception -> 0x0d38 }
        goto L_0x0d3d;
    L_0x0d38:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0d3d:
        goto L_0x0d3f;
    L_0x0d3e:
        throw r2;
    L_0x0d3f:
        goto L_0x0d3e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, long, long, long, boolean, boolean):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:91:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0125  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x019b  */
    /* JADX WARNING: Missing block: B:48:0x00dc, code skipped:
            if (r8[r9 + 3] != (byte) 1) goto L_0x00e2;
     */
    private long readAndWriteTracks(android.media.MediaExtractor r30, org.telegram.messenger.video.MP4Builder r31, android.media.MediaCodec.BufferInfo r32, long r33, long r35, long r37, java.io.File r39, boolean r40) throws java.lang.Exception {
        /*
        r29 = this;
        r0 = r29;
        r1 = r30;
        r2 = r31;
        r3 = r32;
        r4 = r33;
        r6 = 0;
        r7 = org.telegram.messenger.MediaController.findTrack(r1, r6);
        r9 = 1;
        if (r40 == 0) goto L_0x001a;
    L_0x0012:
        r10 = org.telegram.messenger.MediaController.findTrack(r1, r9);
        r12 = r10;
        r10 = r37;
        goto L_0x001d;
    L_0x001a:
        r10 = r37;
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
        if (r12 < 0) goto L_0x0068;
    L_0x0047:
        r1.selectTrack(r12);
        r8 = r1.getTrackFormat(r12);
        r17 = r2.addTrack(r8, r9);
        r8 = r8.getInteger(r13);
        r11 = java.lang.Math.max(r8, r11);
        r8 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r8 <= 0) goto L_0x0062;
    L_0x005e:
        r1.seekTo(r4, r6);
        goto L_0x0065;
    L_0x0062:
        r1.seekTo(r14, r6);
    L_0x0065:
        r8 = r17;
        goto L_0x0069;
    L_0x0068:
        r8 = -1;
    L_0x0069:
        r11 = java.nio.ByteBuffer.allocateDirect(r11);
        r17 = -1;
        if (r12 >= 0) goto L_0x0075;
    L_0x0071:
        if (r7 < 0) goto L_0x0074;
    L_0x0073:
        goto L_0x0075;
    L_0x0074:
        return r17;
    L_0x0075:
        r29.checkConversionCanceled();
        r21 = r14;
        r19 = r17;
        r13 = 0;
    L_0x007d:
        if (r13 != 0) goto L_0x01ac;
    L_0x007f:
        r29.checkConversionCanceled();
        r14 = r1.readSampleData(r11, r6);
        r3.size = r14;
        r14 = r30.getSampleTrackIndex();
        if (r14 != r7) goto L_0x0092;
    L_0x008e:
        r15 = r16;
    L_0x0090:
        r9 = -1;
        goto L_0x0098;
    L_0x0092:
        if (r14 != r12) goto L_0x0096;
    L_0x0094:
        r15 = r8;
        goto L_0x0090;
    L_0x0096:
        r9 = -1;
        r15 = -1;
    L_0x0098:
        if (r15 == r9) goto L_0x0188;
    L_0x009a:
        r9 = android.os.Build.VERSION.SDK_INT;
        r38 = r8;
        r8 = 21;
        if (r9 >= r8) goto L_0x00aa;
    L_0x00a2:
        r11.position(r6);
        r8 = r3.size;
        r11.limit(r8);
    L_0x00aa:
        if (r14 == r12) goto L_0x0115;
    L_0x00ac:
        r8 = r11.array();
        if (r8 == 0) goto L_0x0115;
    L_0x00b2:
        r9 = r11.arrayOffset();
        r25 = r11.limit();
        r25 = r9 + r25;
        r6 = -1;
    L_0x00bd:
        r26 = 4;
        r40 = r13;
        r13 = r25 + -4;
        if (r9 > r13) goto L_0x0112;
    L_0x00c5:
        r27 = r8[r9];
        if (r27 != 0) goto L_0x00df;
    L_0x00c9:
        r27 = r9 + 1;
        r27 = r8[r27];
        if (r27 != 0) goto L_0x00df;
    L_0x00cf:
        r27 = r9 + 2;
        r27 = r8[r27];
        if (r27 != 0) goto L_0x00df;
    L_0x00d5:
        r27 = r9 + 3;
        r28 = r12;
        r12 = r8[r27];
        r1 = 1;
        if (r12 == r1) goto L_0x00e4;
    L_0x00de:
        goto L_0x00e2;
    L_0x00df:
        r28 = r12;
        r1 = 1;
    L_0x00e2:
        if (r9 != r13) goto L_0x0109;
    L_0x00e4:
        r12 = -1;
        if (r6 == r12) goto L_0x0108;
    L_0x00e7:
        r12 = r9 - r6;
        if (r9 == r13) goto L_0x00ec;
    L_0x00eb:
        goto L_0x00ee;
    L_0x00ec:
        r26 = 0;
    L_0x00ee:
        r12 = r12 - r26;
        r13 = r12 >> 24;
        r13 = (byte) r13;
        r8[r6] = r13;
        r13 = r6 + 1;
        r1 = r12 >> 16;
        r1 = (byte) r1;
        r8[r13] = r1;
        r1 = r6 + 2;
        r13 = r12 >> 8;
        r13 = (byte) r13;
        r8[r1] = r13;
        r6 = r6 + 3;
        r1 = (byte) r12;
        r8[r6] = r1;
    L_0x0108:
        r6 = r9;
    L_0x0109:
        r9 = r9 + 1;
        r1 = r30;
        r13 = r40;
        r12 = r28;
        goto L_0x00bd;
    L_0x0112:
        r28 = r12;
        goto L_0x0119;
    L_0x0115:
        r28 = r12;
        r40 = r13;
    L_0x0119:
        r1 = r3.size;
        if (r1 < 0) goto L_0x0125;
    L_0x011d:
        r8 = r30.getSampleTime();
        r3.presentationTimeUs = r8;
        r1 = 0;
        goto L_0x0129;
    L_0x0125:
        r1 = 0;
        r3.size = r1;
        r1 = 1;
    L_0x0129:
        r6 = r3.size;
        if (r6 <= 0) goto L_0x017e;
    L_0x012d:
        if (r1 != 0) goto L_0x017e;
    L_0x012f:
        r8 = 0;
        if (r14 != r7) goto L_0x013f;
    L_0x0133:
        r6 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r6 <= 0) goto L_0x013f;
    L_0x0137:
        r6 = (r19 > r17 ? 1 : (r19 == r17 ? 0 : -1));
        if (r6 != 0) goto L_0x013f;
    L_0x013b:
        r12 = r3.presentationTimeUs;
        r19 = r12;
    L_0x013f:
        r6 = (r35 > r8 ? 1 : (r35 == r8 ? 0 : -1));
        if (r6 < 0) goto L_0x014e;
    L_0x0143:
        r8 = r3.presentationTimeUs;
        r6 = (r8 > r35 ? 1 : (r8 == r35 ? 0 : -1));
        if (r6 >= 0) goto L_0x014a;
    L_0x0149:
        goto L_0x014e;
    L_0x014a:
        r6 = 1;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        goto L_0x0181;
    L_0x014e:
        r6 = 0;
        r3.offset = r6;
        r8 = r30.getSampleFlags();
        r3.flags = r8;
        r8 = r2.writeSampleData(r15, r11, r3, r6);
        r12 = 0;
        r14 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
        if (r14 == 0) goto L_0x017e;
    L_0x0161:
        r14 = r0.callback;
        if (r14 == 0) goto L_0x017e;
    L_0x0165:
        r14 = r3.presentationTimeUs;
        r23 = r14 - r19;
        r25 = (r23 > r21 ? 1 : (r23 == r21 ? 0 : -1));
        if (r25 <= 0) goto L_0x016f;
    L_0x016d:
        r21 = r14 - r19;
    L_0x016f:
        r14 = r21;
        r6 = r0.callback;
        r12 = (float) r14;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r12 = r12 / r13;
        r12 = r12 / r10;
        r6.didWriteData(r8, r12);
        r21 = r14;
        goto L_0x0180;
    L_0x017e:
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
    L_0x0180:
        r6 = r1;
    L_0x0181:
        if (r6 != 0) goto L_0x0186;
    L_0x0183:
        r30.advance();
    L_0x0186:
        r1 = -1;
        goto L_0x0199;
    L_0x0188:
        r38 = r8;
        r28 = r12;
        r40 = r13;
        r1 = -1;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        if (r14 != r1) goto L_0x0195;
    L_0x0193:
        r6 = 1;
        goto L_0x0199;
    L_0x0195:
        r30.advance();
        r6 = 0;
    L_0x0199:
        if (r6 == 0) goto L_0x019d;
    L_0x019b:
        r6 = 1;
        goto L_0x019f;
    L_0x019d:
        r6 = r40;
    L_0x019f:
        r1 = r30;
        r8 = r38;
        r13 = r6;
        r12 = r28;
        r6 = 0;
        r9 = 1;
        r14 = 0;
        goto L_0x007d;
    L_0x01ac:
        r28 = r12;
        r1 = r30;
        if (r7 < 0) goto L_0x01b5;
    L_0x01b2:
        r1.unselectTrack(r7);
    L_0x01b5:
        if (r28 < 0) goto L_0x01bc;
    L_0x01b7:
        r8 = r28;
        r1.unselectTrack(r8);
    L_0x01bc:
        return r19;
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
