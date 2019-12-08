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

    /* JADX WARNING: Unknown top exception splitter block from list: {B:134:0x0266=Splitter:B:134:0x0266, B:156:0x02ad=Splitter:B:156:0x02ad} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010f A:{Catch:{ Exception -> 0x0120, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e0 A:{Catch:{ Exception -> 0x0120, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e0 A:{Catch:{ Exception -> 0x0120, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010f A:{Catch:{ Exception -> 0x0120, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x05a8 A:{Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a4c A:{Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a1f A:{Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a1f A:{Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a4c A:{Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x0903 A:{Catch:{ Exception -> 0x0883, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x08df A:{Catch:{ Exception -> 0x0883, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x090c A:{SYNTHETIC, Splitter:B:544:0x090c} */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a4c A:{Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a1f A:{Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x035e A:{SYNTHETIC, Splitter:B:217:0x035e} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0433 A:{Catch:{ Exception -> 0x0b18, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x05e3  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0477 A:{SYNTHETIC, Splitter:B:278:0x0477} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0621 A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x061e A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0823 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0815 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01c8 A:{SYNTHETIC, Splitter:B:95:0x01c8} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01bf A:{SYNTHETIC, Splitter:B:92:0x01bf} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01f8 A:{SYNTHETIC, Splitter:B:107:0x01f8} */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02b9 A:{SYNTHETIC, Splitter:B:163:0x02b9} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02d8 A:{SYNTHETIC, Splitter:B:176:0x02d8} */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x031a A:{SYNTHETIC, Splitter:B:196:0x031a} */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x030c A:{SYNTHETIC, Splitter:B:192:0x030c} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0337 A:{SYNTHETIC, Splitter:B:205:0x0337} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x035e A:{SYNTHETIC, Splitter:B:217:0x035e} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0433 A:{Catch:{ Exception -> 0x0b18, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0477 A:{SYNTHETIC, Splitter:B:278:0x0477} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x05e3  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x061e A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0621 A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0815 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0823 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01bf A:{SYNTHETIC, Splitter:B:92:0x01bf} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01c8 A:{SYNTHETIC, Splitter:B:95:0x01c8} */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01f8 A:{SYNTHETIC, Splitter:B:107:0x01f8} */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02b9 A:{SYNTHETIC, Splitter:B:163:0x02b9} */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02d8 A:{SYNTHETIC, Splitter:B:176:0x02d8} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x030c A:{SYNTHETIC, Splitter:B:192:0x030c} */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x031a A:{SYNTHETIC, Splitter:B:196:0x031a} */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0337 A:{SYNTHETIC, Splitter:B:205:0x0337} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x035e A:{SYNTHETIC, Splitter:B:217:0x035e} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0433 A:{Catch:{ Exception -> 0x0b18, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x05e3  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0477 A:{SYNTHETIC, Splitter:B:278:0x0477} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0621 A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x061e A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0823 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0815 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01c8 A:{SYNTHETIC, Splitter:B:95:0x01c8} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01bf A:{SYNTHETIC, Splitter:B:92:0x01bf} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01f8 A:{SYNTHETIC, Splitter:B:107:0x01f8} */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02b9 A:{SYNTHETIC, Splitter:B:163:0x02b9} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02d8 A:{SYNTHETIC, Splitter:B:176:0x02d8} */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x031a A:{SYNTHETIC, Splitter:B:196:0x031a} */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x030c A:{SYNTHETIC, Splitter:B:192:0x030c} */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0337 A:{SYNTHETIC, Splitter:B:205:0x0337} */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x035e A:{SYNTHETIC, Splitter:B:217:0x035e} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0433 A:{Catch:{ Exception -> 0x0b18, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0477 A:{SYNTHETIC, Splitter:B:278:0x0477} */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x05e3  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x05fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x061e A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x0621 A:{Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x0815 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0823 A:{Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0d29  */
    /* JADX WARNING: Removed duplicated region for block: B:718:0x0d30 A:{SYNTHETIC, Splitter:B:718:0x0d30} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cf6 A:{SYNTHETIC, Splitter:B:702:0x0cf6} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cf6 A:{SYNTHETIC, Splitter:B:702:0x0cf6} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cf6 A:{SYNTHETIC, Splitter:B:702:0x0cf6} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cf6 A:{SYNTHETIC, Splitter:B:702:0x0cf6} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cf6 A:{SYNTHETIC, Splitter:B:702:0x0cf6} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0cf6 A:{SYNTHETIC, Splitter:B:702:0x0cf6} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0ca9 A:{ExcHandler: all (r0_57 'th' java.lang.Throwable), Splitter:B:58:0x0139} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0bec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0c2a A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0c2f A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0c3c A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0CLASSNAME A:{Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:681:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0d20 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d03  */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:58:0x0139, B:673:0x0CLASSNAME] */
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
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:58:0x0139, B:666:0x0c6f] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:132:0x0262, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:133:0x0263, code skipped:
            r1 = r0;
            r10 = r4;
     */
    /* JADX WARNING: Missing block: B:140:0x0281, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:141:0x0282, code skipped:
            r1 = r0;
            r10 = r2;
            r9 = r4;
     */
    /* JADX WARNING: Missing block: B:165:0x02c6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:166:0x02c7, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:181:0x02e6, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:182:0x02e7, code skipped:
            r1 = r0;
            r6 = r2;
            r13 = r12;
            r12 = r28;
            r2 = null;
            r4 = null;
     */
    /* JADX WARNING: Missing block: B:183:0x02ef, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:184:0x02f0, code skipped:
            r1 = r0;
            r13 = r12;
            r12 = r28;
            r2 = null;
            r4 = null;
            r6 = null;
     */
    /* JADX WARNING: Missing block: B:194:0x0312, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:195:0x0313, code skipped:
            r1 = r0;
            r13 = r12;
            r12 = r28;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:211:0x034e, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:212:0x034f, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r13 = r12;
     */
    /* JADX WARNING: Missing block: B:213:0x0355, code skipped:
            r12 = r28;
     */
    /* JADX WARNING: Missing block: B:226:0x039c, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:238:0x03c0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:239:0x03c1, code skipped:
            r11 = r72;
     */
    /* JADX WARNING: Missing block: B:250:0x03da, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:251:0x03db, code skipped:
            r11 = r77;
     */
    /* JADX WARNING: Missing block: B:252:0x03df, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:253:0x03e1, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:254:0x03e2, code skipped:
            r13 = r67;
     */
    /* JADX WARNING: Missing block: B:255:0x03e4, code skipped:
            r11 = r77;
            r13 = r71;
            r11 = r72;
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:257:0x03f5, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:258:0x03f6, code skipped:
            r13 = r67;
            r11 = r77;
            r44 = r6;
            r13 = r71;
            r11 = r72;
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:273:0x045d, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:274:0x045e, code skipped:
            r13 = r71;
            r11 = r72;
            r9 = r73;
            r1 = r0;
            r56 = r10;
            r12 = r28;
     */
    /* JADX WARNING: Missing block: B:275:0x0469, code skipped:
            r6 = r44;
            r63 = true;
            r10 = r74;
     */
    /* JADX WARNING: Missing block: B:295:0x04db, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:296:0x04dc, code skipped:
            r13 = r71;
            r11 = r72;
            r9 = r73;
            r1 = r0;
            r12 = r10;
     */
    /* JADX WARNING: Missing block: B:323:0x052c, code skipped:
            if (r9.presentationTimeUs < r11) goto L_0x052e;
     */
    /* JADX WARNING: Missing block: B:340:0x0579, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:341:0x057a, code skipped:
            r12 = r10;
            r10 = r75;
     */
    /* JADX WARNING: Missing block: B:343:0x0583, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:344:0x0584, code skipped:
            r22 = r2;
            r12 = r10;
            r10 = r75;
            r13 = r71;
            r11 = r72;
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:354:0x05c0, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:357:0x05cd, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:358:0x05ce, code skipped:
            r22 = r2;
            r56 = r10;
            r10 = r75;
            r13 = r71;
            r11 = r72;
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r12 = r28;
     */
    /* JADX WARNING: Missing block: B:499:0x0883, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:500:0x0884, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r11 = r6;
     */
    /* JADX WARNING: Missing block: B:547:0x0911, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:550:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r1 = 1;
     */
    /* JADX WARNING: Missing block: B:562:0x0953, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:563:0x0955, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:564:0x0956, code skipped:
            r8 = r44;
     */
    /* JADX WARNING: Missing block: B:565:0x0958, code skipped:
            r11 = r72;
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r2 = r3;
            r6 = r8;
     */
    /* JADX WARNING: Missing block: B:577:0x09f1, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:578:0x09f2, code skipped:
            r11 = r72;
            r16 = r3;
            r59 = r4;
            r58 = r8;
     */
    /* JADX WARNING: Missing block: B:595:0x0a71, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:596:0x0a72, code skipped:
            r59 = r4;
            r11 = r6;
     */
    /* JADX WARNING: Missing block: B:604:0x0af3, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:605:0x0af4, code skipped:
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r2 = r16;
            r6 = r58;
            r4 = r59;
     */
    /* JADX WARNING: Missing block: B:606:0x0b01, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:607:0x0b02, code skipped:
            r13 = r71;
            r11 = r72;
            r59 = r4;
     */
    /* JADX WARNING: Missing block: B:608:0x0b08, code skipped:
            r16 = r22;
            r58 = r44;
     */
    /* JADX WARNING: Missing block: B:609:0x0b0c, code skipped:
            r63 = true;
            r9 = r73;
            r10 = r74;
            r1 = r0;
            r2 = r16;
     */
    /* JADX WARNING: Missing block: B:610:0x0b18, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:611:0x0b19, code skipped:
            r13 = r71;
            r11 = r72;
            r16 = r2;
            r59 = r4;
            r56 = r10;
            r12 = r28;
            r58 = r44;
            r63 = true;
            r9 = r73;
            r10 = r74;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:612:0x0b30, code skipped:
            r6 = r58;
     */
    /* JADX WARNING: Missing block: B:613:0x0b34, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:614:0x0b35, code skipped:
            r16 = r2;
            r59 = r4;
            r58 = r6;
            r44 = null;
            r13 = r12;
            r12 = r28;
            r63 = true;
            r9 = r73;
            r10 = r74;
     */
    /* JADX WARNING: Missing block: B:615:0x0b49, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:616:0x0b4a, code skipped:
            r16 = r2;
            r59 = r4;
            r58 = r6;
            r73 = r9;
            r74 = r10;
            r13 = r12;
            r12 = r28;
            r44 = null;
            r63 = true;
     */
    /* JADX WARNING: Missing block: B:617:0x0b5d, code skipped:
            r1 = r0;
            r56 = r44;
     */
    /* JADX WARNING: Missing block: B:618:0x0b62, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:619:0x0b63, code skipped:
            r59 = r4;
            r58 = r6;
            r73 = r9;
            r74 = r10;
            r13 = r12;
            r12 = r28;
            r63 = true;
            r1 = r0;
            r2 = null;
            r56 = r2;
     */
    /* JADX WARNING: Missing block: B:620:0x0b7b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:621:0x0b7c, code skipped:
            r58 = r6;
            r73 = r9;
            r74 = r10;
            r13 = r12;
            r12 = r28;
            r63 = true;
            r1 = r0;
            r2 = null;
            r4 = r2;
            r56 = r4;
     */
    /* JADX WARNING: Missing block: B:622:0x0b93, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:623:0x0b94, code skipped:
            r73 = r9;
            r74 = r10;
            r13 = r12;
            r12 = r28;
            r63 = true;
            r1 = r0;
            r2 = null;
     */
    /* JADX WARNING: Missing block: B:624:0x0ba5, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:625:0x0ba6, code skipped:
            r44 = null;
            r73 = r9;
            r74 = r10;
            r13 = r12;
            r12 = r28;
            r63 = true;
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:626:0x0bb4, code skipped:
            r4 = r2;
            r6 = r4;
            r56 = r6;
     */
    /* JADX WARNING: Missing block: B:627:0x0bb9, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:628:0x0bba, code skipped:
            r73 = r9;
            r74 = r10;
     */
    /* JADX WARNING: Missing block: B:629:0x0bbf, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:630:0x0bc0, code skipped:
            r13 = r12;
            r12 = r28;
            r44 = null;
            r63 = true;
            r1 = r0;
            r10 = r2;
     */
    /* JADX WARNING: Missing block: B:631:0x0bcc, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:632:0x0bcd, code skipped:
            r13 = r12;
            r12 = r28;
            r44 = null;
            r63 = true;
     */
    /* JADX WARNING: Missing block: B:633:0x0bd7, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:634:0x0bd8, code skipped:
            r13 = r12;
            r44 = null;
            r63 = true;
            r12 = r4;
     */
    /* JADX WARNING: Missing block: B:635:0x0be0, code skipped:
            r1 = r0;
     */
    /* JADX WARNING: Missing block: B:636:0x0be1, code skipped:
            r2 = r44;
            r4 = r2;
            r6 = r4;
            r15 = r6;
            r56 = r15;
     */
    /* JADX WARNING: Missing block: B:641:0x0bee, code skipped:
            r3 = 1;
     */
    /* JADX WARNING: Missing block: B:657:0x0c4b, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:658:0x0c4c, code skipped:
            r1 = r0;
            r15 = r11;
     */
    /* JADX WARNING: Missing block: B:659:0x0CLASSNAME, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:660:0x0CLASSNAME, code skipped:
            r1 = r0;
            r15 = r11;
     */
    /* JADX WARNING: Missing block: B:686:0x0ca4, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:687:0x0ca6, code skipped:
            r0 = e;
     */
    /* JADX WARNING: Missing block: B:688:0x0ca7, code skipped:
            r15 = r11;
     */
    /* JADX WARNING: Missing block: B:689:0x0ca9, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:690:0x0caa, code skipped:
            r2 = r0;
            r1 = r14;
     */
    /* JADX WARNING: Missing block: B:699:0x0cef, code skipped:
            r1.release();
     */
    /* JADX WARNING: Missing block: B:703:?, code skipped:
            r1.finishMovie();
     */
    /* JADX WARNING: Missing block: B:704:0x0cfa, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:705:0x0cfb, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Missing block: B:715:0x0d29, code skipped:
            r3.release();
     */
    /* JADX WARNING: Missing block: B:719:?, code skipped:
            r3.finishMovie();
     */
    /* JADX WARNING: Missing block: B:720:0x0d34, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:721:0x0d35, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
    private boolean convertVideoInternal(java.lang.String r67, java.io.File r68, int r69, boolean r70, int r71, int r72, int r73, int r74, long r75, long r77, long r79, boolean r81, boolean r82) {
        /*
        r66 = this;
        r14 = r66;
        r13 = r67;
        r15 = r69;
        r12 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r7 = r75;
        r5 = r77;
        r2 = new android.media.MediaCodec$BufferInfo;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r2.<init>();	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1 = new org.telegram.messenger.video.Mp4Movie;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1.<init>();	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r4 = r68;
        r1.setCacheFile(r4);	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1.setRotation(r15);	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1.setSize(r12, r11);	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r3 = new org.telegram.messenger.video.MP4Builder;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r3.<init>();	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r4 = r70;
        r1 = r3.createMovie(r1, r4);	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r14.mediaMuxer = r1;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1.<init>();	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r14.extractor = r1;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r1.setDataSource(r13);	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r3 = r79;
        r1 = (float) r3;	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        r18 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r18 = r1 / r18;
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0cae, all -> 0x0ca9 }
        if (r81 == 0) goto L_0x0CLASSNAME;
    L_0x004c:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0c5f, all -> 0x0ca9 }
        r3 = 0;
        r4 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x0c5f, all -> 0x0ca9 }
        r1 = -1;
        if (r10 == r1) goto L_0x0068;
    L_0x0056:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x005f, all -> 0x0ca9 }
        r3 = 1;
        r1 = org.telegram.messenger.MediaController.findTrack(r1, r3);	 Catch:{ Exception -> 0x005f, all -> 0x0ca9 }
        r3 = r1;
        goto L_0x0069;
    L_0x005f:
        r0 = move-exception;
        r1 = r0;
        r15 = r11;
        r13 = r12;
        r3 = 0;
        r63 = 1;
        goto L_0x0cbb;
    L_0x0068:
        r3 = -1;
    L_0x0069:
        if (r4 < 0) goto L_0x0CLASSNAME;
    L_0x006b:
        r20 = -1;
        r22 = android.os.Build.MANUFACTURER;	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r1 = r22.toLowerCase();	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r22 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r26 = 4;
        r5 = "video/avc";
        r6 = 18;
        if (r2 >= r6) goto L_0x0130;
    L_0x0080:
        r2 = org.telegram.messenger.MediaController.selectCodec(r5);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r29 = org.telegram.messenger.MediaController.selectColorFormat(r2, r5);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r29 == 0) goto L_0x0118;
    L_0x008a:
        r6 = r2.getName();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r13 = "OMX.qcom.";
        r13 = r6.contains(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r13 == 0) goto L_0x00b2;
    L_0x0096:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r13 = 16;
        if (r6 != r13) goto L_0x00af;
    L_0x009c:
        r6 = "lge";
        r6 = r1.equals(r6);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r6 != 0) goto L_0x00ac;
    L_0x00a4:
        r6 = "nokia";
        r6 = r1.equals(r6);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r6 == 0) goto L_0x00af;
    L_0x00ac:
        r6 = 1;
    L_0x00ad:
        r13 = 1;
        goto L_0x00dc;
    L_0x00af:
        r6 = 1;
    L_0x00b0:
        r13 = 0;
        goto L_0x00dc;
    L_0x00b2:
        r13 = "OMX.Intel.";
        r13 = r6.contains(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r13 == 0) goto L_0x00bc;
    L_0x00ba:
        r6 = 2;
        goto L_0x00b0;
    L_0x00bc:
        r13 = "OMX.MTK.VIDEO.ENCODER.AVC";
        r13 = r6.equals(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r13 == 0) goto L_0x00c6;
    L_0x00c4:
        r6 = 3;
        goto L_0x00b0;
    L_0x00c6:
        r13 = "OMX.SEC.AVC.Encoder";
        r13 = r6.equals(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r13 == 0) goto L_0x00d0;
    L_0x00ce:
        r6 = 4;
        goto L_0x00ad;
    L_0x00d0:
        r13 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r6 = r6.equals(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r6 == 0) goto L_0x00da;
    L_0x00d8:
        r6 = 5;
        goto L_0x00b0;
    L_0x00da:
        r6 = 0;
        goto L_0x00b0;
    L_0x00dc:
        r31 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r31 == 0) goto L_0x010f;
    L_0x00e0:
        r31 = r6;
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r32 = r13;
        r13 = "codec = ";
        r6.append(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = r2.getName();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = " manufacturer = ";
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r6.append(r1);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = "device = ";
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = android.os.Build.MODEL;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r6.append(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = r6.toString();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        goto L_0x0113;
    L_0x010f:
        r31 = r6;
        r32 = r13;
    L_0x0113:
        r13 = r29;
        r2 = r31;
        goto L_0x0139;
    L_0x0118:
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = "no supported color format";
        r1.<init>(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        throw r1;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
    L_0x0120:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r2 = 0;
        r6 = 0;
        r15 = 0;
        r56 = 0;
        r57 = 0;
        r63 = 1;
        r12 = r4;
        r4 = 0;
        goto L_0x0be8;
    L_0x0130:
        r2 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r2 = 0;
        r13 = NUM; // 0x7var_ float:1.701803E38 double:1.0527098025E-314;
        r32 = 0;
    L_0x0139:
        r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        if (r6 == 0) goto L_0x0154;
    L_0x013d:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r29 = r3;
        r3 = "colorFormat = ";
        r6.append(r3);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r6.append(r13);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r3 = r6.toString();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        goto L_0x0156;
    L_0x0154:
        r29 = r3;
    L_0x0156:
        r3 = r12 * r11;
        r6 = r3 * 3;
        r25 = 2;
        r6 = r6 / 2;
        if (r2 != 0) goto L_0x0177;
    L_0x0160:
        r1 = r11 % 16;
        if (r1 == 0) goto L_0x01ab;
    L_0x0164:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
    L_0x0172:
        r6 = r6 + r2;
    L_0x0173:
        r15 = r1;
        r24 = r6;
        goto L_0x01ae;
    L_0x0177:
        r15 = 1;
        if (r2 != r15) goto L_0x018d;
    L_0x017a:
        r1 = r1.toLowerCase();	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = "lge";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r1 != 0) goto L_0x01ab;
    L_0x0186:
        r1 = r3 + 2047;
        r1 = r1 & -2048;
        r1 = r1 - r3;
        r6 = r6 + r1;
        goto L_0x0173;
    L_0x018d:
        r3 = 5;
        if (r2 != r3) goto L_0x0191;
    L_0x0190:
        goto L_0x01ab;
    L_0x0191:
        r3 = 3;
        if (r2 != r3) goto L_0x01ab;
    L_0x0194:
        r2 = "baidu";
        r1 = r1.equals(r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        if (r1 == 0) goto L_0x01ab;
    L_0x019c:
        r1 = r11 % 16;
        r2 = 16;
        r1 = 16 - r1;
        r1 = r1 + r11;
        r1 = r1 - r11;
        r1 = r1 * r12;
        r2 = r1 * 5;
        r2 = r2 / 4;
        goto L_0x0172;
    L_0x01ab:
        r24 = r6;
        r15 = 0;
    L_0x01ae:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r1.selectTrack(r4);	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r1 = r1.getTrackFormat(r4);	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r2 = 0;
        r6 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r6 <= 0) goto L_0x01c8;
    L_0x01bf:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r2 = 0;
        r6.seekTo(r7, r2);	 Catch:{ Exception -> 0x0120, all -> 0x0ca9 }
        r28 = r4;
        goto L_0x01d2;
    L_0x01c8:
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0bd7, all -> 0x0ca9 }
        r28 = r4;
        r3 = 0;
        r6 = 0;
        r2.seekTo(r3, r6);	 Catch:{ Exception -> 0x0bcc, all -> 0x0ca9 }
    L_0x01d2:
        if (r10 > 0) goto L_0x01d8;
    L_0x01d4:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        goto L_0x01d9;
    L_0x01d8:
        r2 = r10;
    L_0x01d9:
        r3 = android.media.MediaFormat.createVideoFormat(r5, r12, r11);	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r4 = "color-format";
        r3.setInteger(r4, r13);	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r4 = "frame-rate";
        r3.setInteger(r4, r9);	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r4 = "i-frame-interval";
        r6 = 2;
        r3.setInteger(r4, r6);	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r6 = 23;
        if (r4 < r6) goto L_0x029b;
    L_0x01f8:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r6 = 1080; // 0x438 float:1.513E-42 double:5.336E-321;
        if (r4 < r6) goto L_0x0205;
    L_0x0200:
        r4 = 8;
        r6 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        goto L_0x0220;
    L_0x0205:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r6 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
        if (r4 < r6) goto L_0x0212;
    L_0x020d:
        r4 = 8;
        r6 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        goto L_0x0220;
    L_0x0212:
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r6 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 < r6) goto L_0x021d;
    L_0x021a:
        r6 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        goto L_0x021f;
    L_0x021d:
        r6 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
    L_0x021f:
        r4 = 1;
    L_0x0220:
        r10 = android.media.MediaCodecInfo.CodecCapabilities.createFromProfileLevel(r5, r4, r6);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r74 = r10;
        if (r10 != 0) goto L_0x0233;
    L_0x0228:
        r10 = 8;
        if (r4 != r10) goto L_0x0233;
    L_0x022c:
        r4 = 1;
        r10 = android.media.MediaCodecInfo.CodecCapabilities.createFromProfileLevel(r5, r4, r6);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = 1;
        goto L_0x0235;
    L_0x0233:
        r10 = r74;
    L_0x0235:
        r31 = r10.getEncoderCapabilities();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        if (r31 == 0) goto L_0x0286;
    L_0x023b:
        r31 = r15;
        r15 = "profile";
        r3.setInteger(r15, r4);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = "level";
        r3.setInteger(r4, r6);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r10.getVideoCapabilities();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r4.getBitrateRange();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r4.getUpper();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        if (r2 <= r4) goto L_0x0266;
    L_0x025b:
        r2 = "bitrate";
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x0262, all -> 0x0ca9 }
        r2 = r4;
        goto L_0x0266;
    L_0x0262:
        r0 = move-exception;
        r1 = r0;
        r10 = r4;
        goto L_0x028c;
    L_0x0266:
        r4 = r10.getVideoCapabilities();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r4.getSupportedFrameRates();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r4.getUpper();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = (java.lang.Integer) r4;	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
        if (r9 <= r4) goto L_0x02b2;
    L_0x027a:
        r6 = "frame-rate";
        r3.setInteger(r6, r4);	 Catch:{ Exception -> 0x0281, all -> 0x0ca9 }
        r9 = r4;
        goto L_0x02b2;
    L_0x0281:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
        r9 = r4;
        goto L_0x028c;
    L_0x0286:
        r31 = r15;
        goto L_0x02b2;
    L_0x0289:
        r0 = move-exception;
        r1 = r0;
        r10 = r2;
    L_0x028c:
        r13 = r12;
        r12 = r28;
        r2 = 0;
        r4 = 0;
        r6 = 0;
        r15 = 0;
    L_0x0293:
        r56 = 0;
    L_0x0295:
        r57 = 0;
        r63 = 1;
        goto L_0x0be8;
    L_0x029b:
        r31 = r15;
        r4 = java.lang.Math.min(r11, r12);	 Catch:{ Exception -> 0x0bbf, all -> 0x0ca9 }
        r6 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r4 > r6) goto L_0x02b2;
    L_0x02a5:
        r4 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
        if (r2 <= r4) goto L_0x02ad;
    L_0x02aa:
        r2 = 921600; // 0xe1000 float:1.291437E-39 double:4.55331E-318;
    L_0x02ad:
        r4 = "bitrate";
        r3.setInteger(r4, r2);	 Catch:{ Exception -> 0x0289, all -> 0x0ca9 }
    L_0x02b2:
        r10 = r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0bb9, all -> 0x0ca9 }
        r4 = 18;
        if (r2 >= r4) goto L_0x02c9;
    L_0x02b9:
        r2 = "stride";
        r4 = r12 + 32;
        r3.setInteger(r2, r4);	 Catch:{ Exception -> 0x02c6, all -> 0x0ca9 }
        r2 = "slice-height";
        r3.setInteger(r2, r11);	 Catch:{ Exception -> 0x02c6, all -> 0x0ca9 }
        goto L_0x02c9;
    L_0x02c6:
        r0 = move-exception;
        r1 = r0;
        goto L_0x028c;
    L_0x02c9:
        r15 = android.media.MediaCodec.createEncoderByType(r5);	 Catch:{ Exception -> 0x0bb9, all -> 0x0ca9 }
        r2 = 0;
        r4 = 1;
        r15.configure(r3, r2, r2, r4);	 Catch:{ Exception -> 0x0ba5, all -> 0x0ca9 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b93, all -> 0x0ca9 }
        r3 = 18;
        if (r2 < r3) goto L_0x02f8;
    L_0x02d8:
        r2 = new org.telegram.messenger.video.InputSurface;	 Catch:{ Exception -> 0x02ef, all -> 0x0ca9 }
        r3 = r15.createInputSurface();	 Catch:{ Exception -> 0x02ef, all -> 0x0ca9 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x02ef, all -> 0x0ca9 }
        r2.makeCurrent();	 Catch:{ Exception -> 0x02e6, all -> 0x0ca9 }
        r6 = r2;
        goto L_0x02f9;
    L_0x02e6:
        r0 = move-exception;
        r1 = r0;
        r6 = r2;
        r13 = r12;
        r12 = r28;
        r2 = 0;
        r4 = 0;
        goto L_0x0293;
    L_0x02ef:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r12 = r28;
        r2 = 0;
        r4 = 0;
        r6 = 0;
        goto L_0x0293;
    L_0x02f8:
        r6 = 0;
    L_0x02f9:
        r15.start();	 Catch:{ Exception -> 0x0b7b, all -> 0x0ca9 }
        r2 = "mime";
        r2 = r1.getString(r2);	 Catch:{ Exception -> 0x0b7b, all -> 0x0ca9 }
        r4 = android.media.MediaCodec.createDecoderByType(r2);	 Catch:{ Exception -> 0x0b7b, all -> 0x0ca9 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b62, all -> 0x0ca9 }
        r3 = 18;
        if (r2 < r3) goto L_0x031a;
    L_0x030c:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0312, all -> 0x0ca9 }
        r2.<init>();	 Catch:{ Exception -> 0x0312, all -> 0x0ca9 }
        goto L_0x0321;
    L_0x0312:
        r0 = move-exception;
        r1 = r0;
        r13 = r12;
        r12 = r28;
        r2 = 0;
        goto L_0x0293;
    L_0x031a:
        r2 = new org.telegram.messenger.video.OutputSurface;	 Catch:{ Exception -> 0x0b62, all -> 0x0ca9 }
        r3 = r69;
        r2.<init>(r12, r11, r3);	 Catch:{ Exception -> 0x0b62, all -> 0x0ca9 }
    L_0x0321:
        r3 = r2.getSurface();	 Catch:{ Exception -> 0x0b49, all -> 0x0ca9 }
        r73 = r9;
        r74 = r10;
        r9 = 0;
        r10 = 0;
        r4.configure(r1, r3, r9, r10);	 Catch:{ Exception -> 0x0b34, all -> 0x0ca9 }
        r4.start();	 Catch:{ Exception -> 0x0b34, all -> 0x0ca9 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b34, all -> 0x0ca9 }
        r10 = 21;
        if (r1 >= r10) goto L_0x0359;
    L_0x0337:
        r1 = r4.getInputBuffers();	 Catch:{ Exception -> 0x034e, all -> 0x0ca9 }
        r3 = r15.getOutputBuffers();	 Catch:{ Exception -> 0x034e, all -> 0x0ca9 }
        r9 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x034e, all -> 0x0ca9 }
        r10 = 18;
        if (r9 >= r10) goto L_0x034c;
    L_0x0345:
        r9 = r15.getInputBuffers();	 Catch:{ Exception -> 0x034e, all -> 0x0ca9 }
        r10 = r9;
        r9 = r1;
        goto L_0x035c;
    L_0x034c:
        r9 = r1;
        goto L_0x035b;
    L_0x034e:
        r0 = move-exception;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r13 = r12;
    L_0x0355:
        r12 = r28;
        goto L_0x0293;
    L_0x0359:
        r3 = 0;
        r9 = 0;
    L_0x035b:
        r10 = 0;
    L_0x035c:
        if (r29 < 0) goto L_0x0407;
    L_0x035e:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x03f5, all -> 0x0ca9 }
        r35 = r3;
        r3 = r29;
        r1 = r1.getTrackFormat(r3);	 Catch:{ Exception -> 0x03f5, all -> 0x0ca9 }
        r29 = r13;
        r13 = "mime";
        r13 = r1.getString(r13);	 Catch:{ Exception -> 0x03f5, all -> 0x0ca9 }
        r43 = r10;
        r10 = "audio/mp4a-latm";
        r10 = r13.equals(r10);	 Catch:{ Exception -> 0x03f5, all -> 0x0ca9 }
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x03f5, all -> 0x0ca9 }
        r44 = r6;
        r6 = 1;
        r13 = r13.addTrack(r1, r6);	 Catch:{ Exception -> 0x03e1, all -> 0x0ca9 }
        if (r10 == 0) goto L_0x03a4;
    L_0x0383:
        r6 = r14.extractor;	 Catch:{ Exception -> 0x039c, all -> 0x0ca9 }
        r6.selectTrack(r3);	 Catch:{ Exception -> 0x039c, all -> 0x0ca9 }
        r6 = "max-input-size";
        r1 = r1.getInteger(r6);	 Catch:{ Exception -> 0x039c, all -> 0x0ca9 }
        r1 = java.nio.ByteBuffer.allocateDirect(r1);	 Catch:{ Exception -> 0x039c, all -> 0x0ca9 }
        r11 = r77;
        r45 = r10;
        r6 = r13;
        r10 = 0;
        r13 = r67;
        goto L_0x041a;
    L_0x039c:
        r0 = move-exception;
    L_0x039d:
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r13 = r12;
        goto L_0x03ef;
    L_0x03a4:
        r6 = new android.media.MediaExtractor;	 Catch:{ Exception -> 0x03e1, all -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x03e1, all -> 0x0ca9 }
        r36 = r13;
        r13 = r67;
        r6.setDataSource(r13);	 Catch:{ Exception -> 0x03df, all -> 0x0ca9 }
        r6.selectTrack(r3);	 Catch:{ Exception -> 0x03df, all -> 0x0ca9 }
        r37 = r10;
        r10 = 0;
        r33 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r33 <= 0) goto L_0x03c4;
    L_0x03bb:
        r10 = 0;
        r6.seekTo(r7, r10);	 Catch:{ Exception -> 0x03c0, all -> 0x0ca9 }
        goto L_0x03c9;
    L_0x03c0:
        r0 = move-exception;
        r11 = r72;
        goto L_0x039d;
    L_0x03c4:
        r11 = r10;
        r10 = 0;
        r6.seekTo(r11, r10);	 Catch:{ Exception -> 0x03df, all -> 0x0ca9 }
    L_0x03c9:
        r10 = new org.telegram.messenger.video.AudioRecoder;	 Catch:{ Exception -> 0x03df, all -> 0x0ca9 }
        r10.<init>(r1, r6, r3);	 Catch:{ Exception -> 0x03df, all -> 0x0ca9 }
        r10.startTime = r7;	 Catch:{ Exception -> 0x03da, all -> 0x0ca9 }
        r11 = r77;
        r10.endTime = r11;	 Catch:{ Exception -> 0x045d, all -> 0x0ca9 }
        r6 = r36;
        r45 = r37;
        r1 = 0;
        goto L_0x041a;
    L_0x03da:
        r0 = move-exception;
        r11 = r77;
        goto L_0x045e;
    L_0x03df:
        r0 = move-exception;
        goto L_0x03e4;
    L_0x03e1:
        r0 = move-exception;
        r13 = r67;
    L_0x03e4:
        r11 = r77;
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x03ef:
        r12 = r28;
        r6 = r44;
        goto L_0x0293;
    L_0x03f5:
        r0 = move-exception;
        r13 = r67;
        r11 = r77;
        r44 = r6;
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        goto L_0x0355;
    L_0x0407:
        r11 = r77;
        r35 = r3;
        r44 = r6;
        r43 = r10;
        r3 = r29;
        r29 = r13;
        r13 = r67;
        r1 = 0;
        r6 = -5;
        r10 = 0;
        r45 = 1;
    L_0x041a:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0b18, all -> 0x0ca9 }
        r52 = r20;
        r49 = r35;
        r20 = 0;
        r21 = 0;
        r46 = 0;
        r47 = 0;
        r48 = -5;
        r50 = 0;
        r51 = 1;
        r54 = 0;
    L_0x0431:
        if (r20 == 0) goto L_0x044d;
    L_0x0433:
        if (r45 != 0) goto L_0x0438;
    L_0x0435:
        if (r21 != 0) goto L_0x0438;
    L_0x0437:
        goto L_0x044d;
    L_0x0438:
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r56 = r10;
        r12 = r28;
        r6 = r44;
        r3 = 0;
        r17 = 0;
        r63 = 1;
        r10 = r74;
        goto L_0x0CLASSNAME;
    L_0x044d:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0b18, all -> 0x0ca9 }
        if (r45 != 0) goto L_0x0473;
    L_0x0452:
        if (r10 == 0) goto L_0x0473;
    L_0x0454:
        r13 = r14.mediaMuxer;	 Catch:{ Exception -> 0x045d, all -> 0x0ca9 }
        r13 = r10.step(r13, r6);	 Catch:{ Exception -> 0x045d, all -> 0x0ca9 }
        r21 = r13;
        goto L_0x0473;
    L_0x045d:
        r0 = move-exception;
    L_0x045e:
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r1 = r0;
        r56 = r10;
        r12 = r28;
    L_0x0469:
        r6 = r44;
        r57 = 0;
        r63 = 1;
        r10 = r74;
        goto L_0x0be8;
    L_0x0473:
        r7 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        if (r46 != 0) goto L_0x05e3;
    L_0x0477:
        r13 = r14.extractor;	 Catch:{ Exception -> 0x05cd, all -> 0x0ca9 }
        r13 = r13.getSampleTrackIndex();	 Catch:{ Exception -> 0x05cd, all -> 0x0ca9 }
        r56 = r10;
        r10 = r28;
        if (r13 != r10) goto L_0x04e5;
    L_0x0483:
        r13 = r4.dequeueInputBuffer(r7);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        if (r13 < 0) goto L_0x04ca;
    L_0x0489:
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r8 = 21;
        if (r7 >= r8) goto L_0x0492;
    L_0x048f:
        r7 = r9[r13];	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        goto L_0x0496;
    L_0x0492:
        r7 = r4.getInputBuffer(r13);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
    L_0x0496:
        r8 = r14.extractor;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r28 = r9;
        r9 = 0;
        r38 = r8.readSampleData(r7, r9);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        if (r38 >= 0) goto L_0x04b3;
    L_0x04a1:
        r37 = 0;
        r38 = 0;
        r39 = 0;
        r41 = 4;
        r35 = r4;
        r36 = r13;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r46 = 1;
        goto L_0x04cc;
    L_0x04b3:
        r37 = 0;
        r7 = r14.extractor;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r39 = r7.getSampleTime();	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r41 = 0;
        r35 = r4;
        r36 = r13;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r7 = r14.extractor;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r7.advance();	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        goto L_0x04cc;
    L_0x04ca:
        r28 = r9;
    L_0x04cc:
        r59 = r3;
        r57 = r5;
        r58 = r6;
        r12 = r10;
        r9 = r22;
        r10 = r75;
        r22 = r2;
        goto L_0x05a5;
    L_0x04db:
        r0 = move-exception;
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r1 = r0;
        r12 = r10;
        goto L_0x0469;
    L_0x04e5:
        r28 = r9;
        if (r45 == 0) goto L_0x0593;
    L_0x04e9:
        r7 = -1;
        if (r3 == r7) goto L_0x0593;
    L_0x04ec:
        if (r13 != r3) goto L_0x0593;
    L_0x04ee:
        r7 = r14.extractor;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r8 = 0;
        r7 = r7.readSampleData(r1, r8);	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r9 = r22;
        r9.size = r7;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r13 = 21;
        if (r7 >= r13) goto L_0x0507;
    L_0x04ff:
        r1.position(r8);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r7 = r9.size;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r1.limit(r7);	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
    L_0x0507:
        r7 = r9.size;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        if (r7 < 0) goto L_0x0519;
    L_0x050b:
        r7 = r14.extractor;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r7 = r7.getSampleTime();	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r9.presentationTimeUs = r7;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r7 = r14.extractor;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r7.advance();	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        goto L_0x051e;
    L_0x0519:
        r7 = 0;
        r9.size = r7;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r46 = 1;
    L_0x051e:
        r7 = r9.size;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        if (r7 <= 0) goto L_0x057e;
    L_0x0522:
        r7 = 0;
        r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1));
        if (r13 < 0) goto L_0x052e;
    L_0x0528:
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x04db, all -> 0x0ca9 }
        r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));
        if (r13 >= 0) goto L_0x057e;
    L_0x052e:
        r7 = 0;
        r9.offset = r7;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r8 = r14.extractor;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r8 = r8.getSampleFlags();	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r9.flags = r8;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r8 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0583, all -> 0x0ca9 }
        r22 = r2;
        r59 = r3;
        r2 = r8.writeSampleData(r6, r1, r9, r7);	 Catch:{ Exception -> 0x0579, all -> 0x0ca9 }
        r7 = 0;
        r13 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
        if (r13 == 0) goto L_0x0571;
    L_0x0549:
        r7 = r14.callback;	 Catch:{ Exception -> 0x0579, all -> 0x0ca9 }
        if (r7 == 0) goto L_0x0571;
    L_0x054d:
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0579, all -> 0x0ca9 }
        r57 = r5;
        r58 = r6;
        r12 = r10;
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r10 = r75;
        r7 = r7 - r10;
        r13 = (r7 > r54 ? 1 : (r7 == r54 ? 0 : -1));
        if (r13 <= 0) goto L_0x0561;
    L_0x055d:
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r54 = r7 - r10;
    L_0x0561:
        r7 = r54;
        r13 = r14.callback;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r5 = (float) r7;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r5 = r5 / r6;
        r5 = r5 / r18;
        r13.didWriteData(r2, r5);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r54 = r7;
        goto L_0x05a5;
    L_0x0571:
        r57 = r5;
        r58 = r6;
        r12 = r10;
        r10 = r75;
        goto L_0x05a5;
    L_0x0579:
        r0 = move-exception;
        r12 = r10;
        r10 = r75;
        goto L_0x05c1;
    L_0x057e:
        r22 = r2;
        r59 = r3;
        goto L_0x0571;
    L_0x0583:
        r0 = move-exception;
        r22 = r2;
        r12 = r10;
        r10 = r75;
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        goto L_0x05df;
    L_0x0593:
        r59 = r3;
        r57 = r5;
        r58 = r6;
        r12 = r10;
        r9 = r22;
        r10 = r75;
        r22 = r2;
        r2 = -1;
        if (r13 != r2) goto L_0x05a5;
    L_0x05a3:
        r2 = 1;
        goto L_0x05a6;
    L_0x05a5:
        r2 = 0;
    L_0x05a6:
        if (r2 == 0) goto L_0x05f5;
    L_0x05a8:
        r2 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r36 = r4.dequeueInputBuffer(r2);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r36 < 0) goto L_0x05f5;
    L_0x05b0:
        r37 = 0;
        r38 = 0;
        r39 = 0;
        r41 = 4;
        r35 = r4;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r46 = 1;
        goto L_0x05f5;
    L_0x05c0:
        r0 = move-exception;
    L_0x05c1:
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x05ca:
        r2 = r22;
        goto L_0x05df;
    L_0x05cd:
        r0 = move-exception;
        r22 = r2;
        r56 = r10;
        r10 = r75;
        r13 = r71;
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r12 = r28;
    L_0x05df:
        r6 = r44;
        goto L_0x0295;
    L_0x05e3:
        r59 = r3;
        r57 = r5;
        r58 = r6;
        r56 = r10;
        r12 = r28;
        r10 = r75;
        r28 = r9;
        r9 = r22;
        r22 = r2;
    L_0x05f5:
        r2 = r47 ^ 1;
        r8 = r2;
        r3 = r48;
        r2 = 1;
    L_0x05fb:
        if (r8 != 0) goto L_0x0619;
    L_0x05fd:
        if (r2 == 0) goto L_0x0600;
    L_0x05ff:
        goto L_0x0619;
    L_0x0600:
        r13 = r67;
        r48 = r3;
        r7 = r10;
        r2 = r22;
        r10 = r56;
        r5 = r57;
        r6 = r58;
        r3 = r59;
        r22 = r9;
        r9 = r28;
        r28 = r12;
        r11 = r77;
        goto L_0x0431;
    L_0x0619:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }
        if (r82 == 0) goto L_0x0621;
    L_0x061e:
        r5 = 22000; // 0x55f0 float:3.0829E-41 double:1.08694E-319;
        goto L_0x0623;
    L_0x0621:
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
    L_0x0623:
        r5 = r15.dequeueOutputBuffer(r9, r5);	 Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }
        r6 = -1;
        if (r5 != r6) goto L_0x063f;
    L_0x062a:
        r13 = r71;
        r6 = r72;
        r35 = r1;
        r19 = r3;
        r25 = r8;
        r8 = r20;
        r2 = r57;
        r1 = -1;
        r20 = 2;
        r48 = 0;
        goto L_0x0813;
    L_0x063f:
        r6 = -3;
        if (r5 != r6) goto L_0x0661;
    L_0x0642:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = 21;
        if (r6 >= r7) goto L_0x064c;
    L_0x0648:
        r49 = r15.getOutputBuffers();	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
    L_0x064c:
        r13 = r71;
        r6 = r72;
        r35 = r1;
        r48 = r2;
        r19 = r3;
        r25 = r8;
        r8 = r20;
        r2 = r57;
        r1 = -1;
        r20 = 2;
        goto L_0x0813;
    L_0x0661:
        r6 = -2;
        if (r5 != r6) goto L_0x069d;
    L_0x0664:
        r6 = r15.getOutputFormat();	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = -5;
        if (r3 != r7) goto L_0x064c;
    L_0x066b:
        if (r6 == 0) goto L_0x064c;
    L_0x066d:
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = 0;
        r3 = r3.addTrack(r6, r7);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = "prepend-sps-pps-to-idr-frames";
        r7 = r6.containsKey(r7);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r7 == 0) goto L_0x064c;
    L_0x067c:
        r7 = "prepend-sps-pps-to-idr-frames";
        r7 = r6.getInteger(r7);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r13 = 1;
        if (r7 != r13) goto L_0x064c;
    L_0x0685:
        r7 = "csd-0";
        r7 = r6.getByteBuffer(r7);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r13 = "csd-1";
        r6 = r6.getByteBuffer(r13);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r7.limit();	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6 = r6.limit();	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r7 + r6;
        r50 = r7;
        goto L_0x064c;
    L_0x069d:
        if (r5 < 0) goto L_0x0ace;
    L_0x069f:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }
        r13 = 21;
        if (r6 >= r13) goto L_0x06a8;
    L_0x06a5:
        r6 = r49[r5];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        goto L_0x06ac;
    L_0x06a8:
        r6 = r15.getOutputBuffer(r5);	 Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }
    L_0x06ac:
        if (r6 == 0) goto L_0x0aa4;
    L_0x06ae:
        r7 = r9.size;	 Catch:{ Exception -> 0x0b01, all -> 0x0ca9 }
        r13 = 1;
        if (r7 <= r13) goto L_0x07f2;
    L_0x06b3:
        r7 = r9.flags;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r20 = 2;
        r7 = r7 & 2;
        if (r7 != 0) goto L_0x076a;
    L_0x06bb:
        if (r50 == 0) goto L_0x06ce;
    L_0x06bd:
        r7 = r9.flags;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r7 & r13;
        if (r7 == 0) goto L_0x06ce;
    L_0x06c2:
        r7 = r9.offset;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r7 + r50;
        r9.offset = r7;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r7 - r50;
        r9.size = r7;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
    L_0x06ce:
        if (r51 == 0) goto L_0x072b;
    L_0x06d0:
        r7 = r9.flags;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r13 = 1;
        r7 = r7 & r13;
        if (r7 == 0) goto L_0x072b;
    L_0x06d6:
        r7 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r13 = 100;
        if (r7 <= r13) goto L_0x0724;
    L_0x06dc:
        r7 = r9.offset;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6.position(r7);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = 100;
        r7 = new byte[r7];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6.get(r7);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r35 = r1;
        r13 = 0;
        r25 = 0;
    L_0x06ed:
        r1 = r7.length;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r1 + -4;
        if (r13 >= r1) goto L_0x0726;
    L_0x06f2:
        r1 = r7[r13];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r1 != 0) goto L_0x071d;
    L_0x06f6:
        r1 = r13 + 1;
        r1 = r7[r1];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r1 != 0) goto L_0x071d;
    L_0x06fc:
        r1 = r13 + 2;
        r1 = r7[r1];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r1 != 0) goto L_0x071d;
    L_0x0702:
        r1 = r13 + 3;
        r1 = r7[r1];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r36 = r2;
        r2 = 1;
        if (r1 != r2) goto L_0x071f;
    L_0x070b:
        r1 = r25 + 1;
        if (r1 <= r2) goto L_0x071a;
    L_0x070f:
        r1 = r9.offset;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r1 + r13;
        r9.offset = r1;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r1 - r13;
        r9.size = r1;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        goto L_0x0728;
    L_0x071a:
        r25 = r1;
        goto L_0x071f;
    L_0x071d:
        r36 = r2;
    L_0x071f:
        r13 = r13 + 1;
        r2 = r36;
        goto L_0x06ed;
    L_0x0724:
        r35 = r1;
    L_0x0726:
        r36 = r2;
    L_0x0728:
        r51 = 0;
        goto L_0x072f;
    L_0x072b:
        r35 = r1;
        r36 = r2;
    L_0x072f:
        r1 = r14.mediaMuxer;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r2 = 1;
        r6 = r1.writeSampleData(r3, r6, r9, r2);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = 0;
        r13 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r13 == 0) goto L_0x075c;
    L_0x073c:
        r1 = r14.callback;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r1 == 0) goto L_0x075c;
    L_0x0740:
        r1 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r1 - r10;
        r13 = (r1 > r54 ? 1 : (r1 == r54 ? 0 : -1));
        if (r13 <= 0) goto L_0x074b;
    L_0x0747:
        r1 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r54 = r1 - r10;
    L_0x074b:
        r1 = r54;
        r13 = r14.callback;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r25 = r8;
        r8 = (float) r1;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r37 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r8 = r8 / r37;
        r8 = r8 / r18;
        r13.didWriteData(r6, r8);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        goto L_0x0760;
    L_0x075c:
        r25 = r8;
        r1 = r54;
    L_0x0760:
        r13 = r71;
        r6 = r72;
        r54 = r1;
    L_0x0766:
        r2 = r57;
        goto L_0x0800;
    L_0x076a:
        r35 = r1;
        r36 = r2;
        r25 = r8;
        r1 = -5;
        if (r3 != r1) goto L_0x07ec;
    L_0x0773:
        r2 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r2 = new byte[r2];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r3 = r9.offset;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r7 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r3 = r3 + r7;
        r6.limit(r3);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r3 = r9.offset;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6.position(r3);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6.get(r2);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r3 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r6 = 1;
        r3 = r3 - r6;
    L_0x078b:
        r7 = 3;
        if (r3 < 0) goto L_0x07c9;
    L_0x078e:
        if (r3 <= r7) goto L_0x07c9;
    L_0x0790:
        r8 = r2[r3];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r8 != r6) goto L_0x07c4;
    L_0x0794:
        r6 = r3 + -1;
        r6 = r2[r6];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r6 != 0) goto L_0x07c4;
    L_0x079a:
        r6 = r3 + -2;
        r6 = r2[r6];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r6 != 0) goto L_0x07c4;
    L_0x07a0:
        r6 = r3 + -3;
        r8 = r2[r6];	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        if (r8 != 0) goto L_0x07c4;
    L_0x07a6:
        r3 = java.nio.ByteBuffer.allocate(r6);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r8 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r8 = r8 - r6;
        r8 = java.nio.ByteBuffer.allocate(r8);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r13 = 0;
        r1 = r3.put(r2, r13, r6);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1.position(r13);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r9.size;	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1 = r1 - r6;
        r1 = r8.put(r2, r6, r1);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        r1.position(r13);	 Catch:{ Exception -> 0x05c0, all -> 0x0ca9 }
        goto L_0x07cb;
    L_0x07c4:
        r3 = r3 + -1;
        r1 = -5;
        r6 = 1;
        goto L_0x078b;
    L_0x07c9:
        r3 = 0;
        r8 = 0;
    L_0x07cb:
        r13 = r71;
        r6 = r72;
        r2 = r57;
        r1 = android.media.MediaFormat.createVideoFormat(r2, r13, r6);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        if (r3 == 0) goto L_0x07e3;
    L_0x07d7:
        if (r8 == 0) goto L_0x07e3;
    L_0x07d9:
        r7 = "csd-0";
        r1.setByteBuffer(r7, r3);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r3 = "csd-1";
        r1.setByteBuffer(r3, r8);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
    L_0x07e3:
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r7 = 0;
        r1 = r3.addTrack(r1, r7);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r3 = r1;
        goto L_0x0800;
    L_0x07ec:
        r13 = r71;
        r6 = r72;
        goto L_0x0766;
    L_0x07f2:
        r13 = r71;
        r6 = r72;
        r35 = r1;
        r36 = r2;
        r25 = r8;
        r2 = r57;
        r20 = 2;
    L_0x0800:
        r1 = r9.flags;	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x0808;
    L_0x0806:
        r1 = 1;
        goto L_0x0809;
    L_0x0808:
        r1 = 0;
    L_0x0809:
        r7 = 0;
        r15.releaseOutputBuffer(r5, r7);	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        r8 = r1;
        r19 = r3;
        r48 = r36;
        r1 = -1;
    L_0x0813:
        if (r5 == r1) goto L_0x0823;
    L_0x0815:
        r57 = r2;
        r20 = r8;
        r3 = r19;
        r8 = r25;
        r1 = r35;
        r2 = r48;
        goto L_0x05fb;
    L_0x0823:
        if (r47 != 0) goto L_0x0a77;
    L_0x0825:
        r57 = r2;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r3 = r4.dequeueOutputBuffer(r9, r1);	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        r1 = -1;
        if (r3 != r1) goto L_0x0855;
    L_0x0830:
        r11 = r6;
        r62 = r8;
        r16 = r22;
        r23 = r35;
        r64 = r57;
        r30 = r58;
        r17 = r59;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r8 = 18;
        r25 = 0;
    L_0x0843:
        r27 = 3;
        r42 = -5;
        r57 = 0;
        r60 = 0;
        r63 = 1;
        r59 = r4;
        r58 = r44;
        r44 = 0;
        goto L_0x0a8a;
    L_0x0855:
        r2 = -3;
        if (r3 != r2) goto L_0x0863;
    L_0x0858:
        r11 = r6;
        r62 = r8;
        r16 = r22;
        r23 = r35;
        r64 = r57;
        goto L_0x0a80;
    L_0x0863:
        r2 = -2;
        if (r3 != r2) goto L_0x088c;
    L_0x0866:
        r2 = r4.getOutputFormat();	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        if (r3 == 0) goto L_0x0858;
    L_0x086e:
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r3.<init>();	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r5 = "newFormat = ";
        r3.append(r5);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r3.append(r2);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r2 = r3.toString();	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        goto L_0x0858;
    L_0x0883:
        r0 = move-exception;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r11 = r6;
        goto L_0x05ca;
    L_0x088c:
        if (r3 < 0) goto L_0x0a4f;
    L_0x088e:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        r5 = 18;
        if (r2 < r5) goto L_0x089f;
    L_0x0894:
        r2 = r9.size;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        if (r2 == 0) goto L_0x089a;
    L_0x0898:
        r2 = 1;
        goto L_0x089b;
    L_0x089a:
        r2 = 0;
    L_0x089b:
        r1 = r2;
        r33 = 0;
        goto L_0x08b1;
    L_0x089f:
        r2 = r9.size;	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        if (r2 != 0) goto L_0x08ae;
    L_0x08a3:
        r1 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r33 = 0;
        r5 = (r1 > r33 ? 1 : (r1 == r33 ? 0 : -1));
        if (r5 == 0) goto L_0x08ac;
    L_0x08ab:
        goto L_0x08b0;
    L_0x08ac:
        r1 = 0;
        goto L_0x08b1;
    L_0x08ae:
        r33 = 0;
    L_0x08b0:
        r1 = 1;
    L_0x08b1:
        r2 = (r77 > r33 ? 1 : (r77 == r33 ? 0 : -1));
        if (r2 <= 0) goto L_0x08cb;
    L_0x08b5:
        r62 = r8;
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r5 = (r7 > r77 ? 1 : (r7 == r77 ? 0 : -1));
        if (r5 < 0) goto L_0x08cd;
    L_0x08bd:
        r1 = r9.flags;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r1 = r1 | 4;
        r9.flags = r1;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r1 = 0;
        r7 = 0;
        r46 = 1;
        r47 = 1;
        goto L_0x08cf;
    L_0x08cb:
        r62 = r8;
    L_0x08cd:
        r7 = 0;
    L_0x08cf:
        r5 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1));
        if (r5 <= 0) goto L_0x0907;
    L_0x08d3:
        r33 = -1;
        r5 = (r52 > r33 ? 1 : (r52 == r33 ? 0 : -1));
        if (r5 != 0) goto L_0x0907;
    L_0x08d9:
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r5 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1));
        if (r5 >= 0) goto L_0x0903;
    L_0x08df:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        if (r1 == 0) goto L_0x0901;
    L_0x08e3:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r1.<init>();	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r5 = "drop frame startTime = ";
        r1.append(r5);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r1.append(r10);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r5 = " present time = ";
        r1.append(r5);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r1.append(r7);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
    L_0x0901:
        r1 = 0;
        goto L_0x0907;
    L_0x0903:
        r7 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0883, all -> 0x0ca9 }
        r52 = r7;
    L_0x0907:
        r4.releaseOutputBuffer(r3, r1);	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        if (r1 == 0) goto L_0x09fc;
    L_0x090c:
        r22.awaitNewImage();	 Catch:{ Exception -> 0x0911, all -> 0x0ca9 }
        r1 = 0;
        goto L_0x0917;
    L_0x0911:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        r1 = 1;
    L_0x0917:
        if (r1 != 0) goto L_0x09fc;
    L_0x0919:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a71, all -> 0x0ca9 }
        r7 = 18;
        if (r1 < r7) goto L_0x0963;
    L_0x091f:
        r3 = r22;
        r5 = 0;
        r3.drawImage(r5);	 Catch:{ Exception -> 0x0955, all -> 0x0ca9 }
        r5 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0955, all -> 0x0ca9 }
        r36 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 * r36;
        r8 = r44;
        r8.setPresentationTime(r5);	 Catch:{ Exception -> 0x0953, all -> 0x0ca9 }
        r8.swapBuffers();	 Catch:{ Exception -> 0x0953, all -> 0x0ca9 }
        r11 = r72;
        r16 = r3;
        r23 = r35;
        r64 = r57;
        r30 = r58;
        r17 = r59;
        r27 = 3;
        r42 = -5;
        r44 = 0;
        r57 = 0;
        r60 = 0;
        r63 = 1;
        r59 = r4;
        r58 = r8;
        r8 = 18;
        goto L_0x0a19;
    L_0x0953:
        r0 = move-exception;
        goto L_0x0958;
    L_0x0955:
        r0 = move-exception;
        r8 = r44;
    L_0x0958:
        r11 = r72;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r2 = r3;
        r6 = r8;
        goto L_0x0295;
    L_0x0963:
        r3 = r22;
        r8 = r44;
        r5 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r22 = r15.dequeueInputBuffer(r5);	 Catch:{ Exception -> 0x09f1, all -> 0x0ca9 }
        if (r22 < 0) goto L_0x09c9;
    L_0x096f:
        r1 = 1;
        r3.drawImage(r1);	 Catch:{ Exception -> 0x09f1, all -> 0x0ca9 }
        r16 = r3.getFrame();	 Catch:{ Exception -> 0x09f1, all -> 0x0ca9 }
        r30 = r43[r22];	 Catch:{ Exception -> 0x09f1, all -> 0x0ca9 }
        r30.clear();	 Catch:{ Exception -> 0x09f1, all -> 0x0ca9 }
        r23 = r35;
        r2 = -1;
        r35 = 1;
        r42 = -5;
        r44 = 0;
        r1 = r16;
        r16 = r3;
        r33 = r57;
        r3 = -1;
        r60 = 0;
        r2 = r30;
        r30 = r8;
        r17 = r59;
        r8 = -1;
        r57 = 0;
        r3 = r29;
        r59 = r4;
        r63 = 1;
        r4 = r71;
        r64 = r33;
        r27 = 3;
        r5 = r72;
        r7 = r72;
        r8 = 18;
        r65 = r58;
        r58 = r30;
        r30 = r65;
        r6 = r31;
        r11 = r7;
        r7 = r32;
        org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r35 = 0;
        r1 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r39 = 0;
        r33 = r15;
        r34 = r22;
        r36 = r24;
        r37 = r1;
        r33.queueInputBuffer(r34, r35, r36, r37, r39);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        goto L_0x0a19;
    L_0x09c9:
        r11 = r72;
        r16 = r3;
        r23 = r35;
        r64 = r57;
        r30 = r58;
        r17 = r59;
        r27 = 3;
        r42 = -5;
        r44 = 0;
        r57 = 0;
        r60 = 0;
        r63 = 1;
        r59 = r4;
        r58 = r8;
        r8 = 18;
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        if (r1 == 0) goto L_0x0a19;
    L_0x09eb:
        r1 = "input buffer not available";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        goto L_0x0a19;
    L_0x09f1:
        r0 = move-exception;
        r11 = r72;
        r16 = r3;
        r59 = r4;
        r58 = r8;
        goto L_0x0b0c;
    L_0x09fc:
        r11 = r6;
        r16 = r22;
        r23 = r35;
        r64 = r57;
        r30 = r58;
        r17 = r59;
        r8 = 18;
        r27 = 3;
        r42 = -5;
        r57 = 0;
        r60 = 0;
        r63 = 1;
        r59 = r4;
        r58 = r44;
        r44 = 0;
    L_0x0a19:
        r1 = r9.flags;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x0a4c;
    L_0x0a1f:
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        if (r1 == 0) goto L_0x0a28;
    L_0x0a23:
        r1 = "decoder stream end";
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
    L_0x0a28:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        if (r1 < r8) goto L_0x0a32;
    L_0x0a2c:
        r15.signalEndOfInputStream();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a49;
    L_0x0a32:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r36 = r15.dequeueInputBuffer(r1);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        if (r36 < 0) goto L_0x0a49;
    L_0x0a3a:
        r37 = 0;
        r38 = 1;
        r3 = r9.presentationTimeUs;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r41 = 4;
        r35 = r15;
        r39 = r3;
        r35.queueInputBuffer(r36, r37, r38, r39, r41);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
    L_0x0a49:
        r25 = 0;
        goto L_0x0a8a;
    L_0x0a4c:
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        goto L_0x0a8a;
    L_0x0a4f:
        r59 = r4;
        r11 = r6;
        r16 = r22;
        r58 = r44;
        r57 = 0;
        r63 = 1;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2.<init>();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r4 = "unexpected result from decoder.dequeueOutputBuffer: ";
        r2.append(r4);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2.append(r3);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        throw r1;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
    L_0x0a71:
        r0 = move-exception;
        r59 = r4;
        r11 = r6;
        goto L_0x0b08;
    L_0x0a77:
        r64 = r2;
        r11 = r6;
        r62 = r8;
        r16 = r22;
        r23 = r35;
    L_0x0a80:
        r30 = r58;
        r17 = r59;
        r1 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;
        r8 = 18;
        goto L_0x0843;
    L_0x0a8a:
        r10 = r75;
        r22 = r16;
        r3 = r19;
        r1 = r23;
        r8 = r25;
        r2 = r48;
        r44 = r58;
        r4 = r59;
        r20 = r62;
        r57 = r64;
        r59 = r17;
        r58 = r30;
        goto L_0x05fb;
    L_0x0aa4:
        r13 = r71;
        r11 = r72;
        r59 = r4;
        r16 = r22;
        r58 = r44;
        r57 = 0;
        r63 = 1;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2.<init>();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r3 = "encoderOutputBuffer ";
        r2.append(r3);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2.append(r5);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r3 = " was null";
        r2.append(r3);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        throw r1;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
    L_0x0ace:
        r13 = r71;
        r11 = r72;
        r59 = r4;
        r16 = r22;
        r58 = r44;
        r57 = 0;
        r63 = 1;
        r1 = new java.lang.RuntimeException;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2.<init>();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r3 = "unexpected result from encoder.dequeueOutputBuffer: ";
        r2.append(r3);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2.append(r5);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
        throw r1;	 Catch:{ Exception -> 0x0af3, all -> 0x0ca9 }
    L_0x0af3:
        r0 = move-exception;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r2 = r16;
        r6 = r58;
        r4 = r59;
        goto L_0x0be8;
    L_0x0b01:
        r0 = move-exception;
        r13 = r71;
        r11 = r72;
        r59 = r4;
    L_0x0b08:
        r16 = r22;
        r58 = r44;
    L_0x0b0c:
        r57 = 0;
        r63 = 1;
        r9 = r73;
        r10 = r74;
        r1 = r0;
        r2 = r16;
        goto L_0x0b30;
    L_0x0b18:
        r0 = move-exception;
        r13 = r71;
        r11 = r72;
        r16 = r2;
        r59 = r4;
        r56 = r10;
        r12 = r28;
        r58 = r44;
        r57 = 0;
        r63 = 1;
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x0b30:
        r6 = r58;
        goto L_0x0be8;
    L_0x0b34:
        r0 = move-exception;
        r16 = r2;
        r59 = r4;
        r58 = r6;
        r44 = r9;
        r13 = r12;
        r12 = r28;
        r57 = 0;
        r63 = 1;
        r9 = r73;
        r10 = r74;
        goto L_0x0b5d;
    L_0x0b49:
        r0 = move-exception;
        r16 = r2;
        r59 = r4;
        r58 = r6;
        r73 = r9;
        r74 = r10;
        r13 = r12;
        r12 = r28;
        r44 = 0;
        r57 = 0;
        r63 = 1;
    L_0x0b5d:
        r1 = r0;
        r56 = r44;
        goto L_0x0be8;
    L_0x0b62:
        r0 = move-exception;
        r59 = r4;
        r58 = r6;
        r73 = r9;
        r74 = r10;
        r13 = r12;
        r12 = r28;
        r44 = 0;
        r57 = 0;
        r63 = 1;
        r1 = r0;
        r2 = r44;
        r56 = r2;
        goto L_0x0be8;
    L_0x0b7b:
        r0 = move-exception;
        r58 = r6;
        r73 = r9;
        r74 = r10;
        r13 = r12;
        r12 = r28;
        r44 = 0;
        r57 = 0;
        r63 = 1;
        r1 = r0;
        r2 = r44;
        r4 = r2;
        r56 = r4;
        goto L_0x0be8;
    L_0x0b93:
        r0 = move-exception;
        r73 = r9;
        r74 = r10;
        r13 = r12;
        r12 = r28;
        r44 = 0;
        r57 = 0;
        r63 = 1;
        r1 = r0;
        r2 = r44;
        goto L_0x0bb4;
    L_0x0ba5:
        r0 = move-exception;
        r44 = r2;
        r73 = r9;
        r74 = r10;
        r13 = r12;
        r12 = r28;
        r57 = 0;
        r63 = 1;
        r1 = r0;
    L_0x0bb4:
        r4 = r2;
        r6 = r4;
        r56 = r6;
        goto L_0x0be8;
    L_0x0bb9:
        r0 = move-exception;
        r73 = r9;
        r74 = r10;
        goto L_0x0bcd;
    L_0x0bbf:
        r0 = move-exception;
        r13 = r12;
        r12 = r28;
        r44 = 0;
        r57 = 0;
        r63 = 1;
        r1 = r0;
        r10 = r2;
        goto L_0x0be1;
    L_0x0bcc:
        r0 = move-exception;
    L_0x0bcd:
        r13 = r12;
        r12 = r28;
        r44 = 0;
        r57 = 0;
        r63 = 1;
        goto L_0x0be0;
    L_0x0bd7:
        r0 = move-exception;
        r13 = r12;
        r44 = 0;
        r57 = 0;
        r63 = 1;
        r12 = r4;
    L_0x0be0:
        r1 = r0;
    L_0x0be1:
        r2 = r44;
        r4 = r2;
        r6 = r4;
        r15 = r6;
        r56 = r15;
    L_0x0be8:
        r3 = r1 instanceof java.lang.IllegalStateException;	 Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0ca9 }
        if (r3 == 0) goto L_0x0bf0;
    L_0x0bec:
        if (r82 != 0) goto L_0x0bf0;
    L_0x0bee:
        r3 = 1;
        goto L_0x0bf1;
    L_0x0bf0:
        r3 = 0;
    L_0x0bf1:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r5.<init>();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r7 = "bitrate: ";
        r5.append(r7);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r5.append(r10);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r7 = " framerate: ";
        r5.append(r7);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r5.append(r9);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r7 = " size: ";
        r5.append(r7);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r5.append(r11);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r7 = "x";
        r5.append(r7);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r5.append(r13);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r17 = 1;
    L_0x0CLASSNAME:
        r1 = r14.extractor;	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r1.unselectTrack(r12);	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        if (r2 == 0) goto L_0x0c2d;
    L_0x0c2a:
        r2.release();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
    L_0x0c2d:
        if (r6 == 0) goto L_0x0CLASSNAME;
    L_0x0c2f:
        r6.release();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
    L_0x0CLASSNAME:
        if (r4 == 0) goto L_0x0c3a;
    L_0x0CLASSNAME:
        r4.stop();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r4.release();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
    L_0x0c3a:
        if (r15 == 0) goto L_0x0CLASSNAME;
    L_0x0c3c:
        r15.stop();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        r15.release();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
    L_0x0CLASSNAME:
        if (r56 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r56.release();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
    L_0x0CLASSNAME:
        r66.checkConversionCanceled();	 Catch:{ Exception -> 0x0c4b, all -> 0x0ca9 }
        goto L_0x0c5b;
    L_0x0c4b:
        r0 = move-exception;
        r1 = r0;
        r15 = r11;
        goto L_0x0cbb;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r1 = r0;
        r15 = r11;
        goto L_0x0cba;
    L_0x0CLASSNAME:
        r13 = r12;
        r57 = 0;
        r3 = 0;
        r17 = 0;
    L_0x0c5b:
        r15 = r11;
        r4 = r17;
        goto L_0x0c8d;
    L_0x0c5f:
        r0 = move-exception;
        r57 = 0;
        r63 = 1;
        r1 = r0;
        r15 = r11;
        r13 = r12;
        goto L_0x0cba;
    L_0x0CLASSNAME:
        r4 = r2;
        r13 = r12;
        r57 = 0;
        r63 = 1;
        r2 = r14.extractor;	 Catch:{ Exception -> 0x0ca6, all -> 0x0ca9 }
        r3 = r14.mediaMuxer;	 Catch:{ Exception -> 0x0ca6, all -> 0x0ca9 }
        r1 = -1;
        if (r10 == r1) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r12 = 1;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r12 = 0;
    L_0x0CLASSNAME:
        r1 = r66;
        r5 = r75;
        r7 = r77;
        r9 = r79;
        r15 = r11;
        r11 = r68;
        r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12);	 Catch:{ Exception -> 0x0ca4, all -> 0x0ca9 }
        r9 = r73;
        r10 = r74;
        r3 = 0;
        r4 = 0;
    L_0x0c8d:
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1.release();
    L_0x0CLASSNAME:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0ca1;
    L_0x0CLASSNAME:
        r1.finishMovie();	 Catch:{ Exception -> 0x0c9c }
        goto L_0x0ca1;
    L_0x0c9c:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0ca1:
        r63 = r4;
        goto L_0x0cff;
    L_0x0ca4:
        r0 = move-exception;
        goto L_0x0cb5;
    L_0x0ca6:
        r0 = move-exception;
        r15 = r11;
        goto L_0x0cb5;
    L_0x0ca9:
        r0 = move-exception;
        r2 = r0;
        r1 = r14;
        goto L_0x0d25;
    L_0x0cae:
        r0 = move-exception;
        r15 = r11;
        r13 = r12;
        r57 = 0;
        r63 = 1;
    L_0x0cb5:
        r9 = r73;
        r10 = r74;
        r1 = r0;
    L_0x0cba:
        r3 = 0;
    L_0x0cbb:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0d21 }
        r2.<init>();	 Catch:{ all -> 0x0d21 }
        r4 = "bitrate: ";
        r2.append(r4);	 Catch:{ all -> 0x0d21 }
        r2.append(r10);	 Catch:{ all -> 0x0d21 }
        r4 = " framerate: ";
        r2.append(r4);	 Catch:{ all -> 0x0d21 }
        r2.append(r9);	 Catch:{ all -> 0x0d21 }
        r4 = " size: ";
        r2.append(r4);	 Catch:{ all -> 0x0d21 }
        r2.append(r15);	 Catch:{ all -> 0x0d21 }
        r4 = "x";
        r2.append(r4);	 Catch:{ all -> 0x0d21 }
        r2.append(r13);	 Catch:{ all -> 0x0d21 }
        r2 = r2.toString();	 Catch:{ all -> 0x0d21 }
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0d21 }
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0d21 }
        r1 = r14.extractor;
        if (r1 == 0) goto L_0x0cf2;
    L_0x0cef:
        r1.release();
    L_0x0cf2:
        r1 = r14.mediaMuxer;
        if (r1 == 0) goto L_0x0cff;
    L_0x0cf6:
        r1.finishMovie();	 Catch:{ Exception -> 0x0cfa }
        goto L_0x0cff;
    L_0x0cfa:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
    L_0x0cff:
        r8 = r9;
        r9 = r10;
        if (r3 == 0) goto L_0x0d20;
    L_0x0d03:
        r17 = 1;
        r1 = r66;
        r2 = r67;
        r3 = r68;
        r4 = r69;
        r5 = r70;
        r6 = r71;
        r7 = r72;
        r10 = r75;
        r12 = r77;
        r14 = r79;
        r16 = r81;
        r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17);
        return r1;
    L_0x0d20:
        return r63;
    L_0x0d21:
        r0 = move-exception;
        r1 = r66;
        r2 = r0;
    L_0x0d25:
        r3 = r1.extractor;
        if (r3 == 0) goto L_0x0d2c;
    L_0x0d29:
        r3.release();
    L_0x0d2c:
        r3 = r1.mediaMuxer;
        if (r3 == 0) goto L_0x0d39;
    L_0x0d30:
        r3.finishMovie();	 Catch:{ Exception -> 0x0d34 }
        goto L_0x0d39;
    L_0x0d34:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0d39:
        goto L_0x0d3b;
    L_0x0d3a:
        throw r2;
    L_0x0d3b:
        goto L_0x0d3a;
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
