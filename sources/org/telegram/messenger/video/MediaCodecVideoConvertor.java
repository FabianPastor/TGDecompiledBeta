package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
/* loaded from: classes.dex */
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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, boolean z4, MediaController.VideoConvertorListener videoConvertorListener) {
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, i7, i8, j, j2, j3, j4, z2, false, savedFilterState, str2, arrayList, z3, cropState, z4);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:(25:(2:496|497)(2:1308|(47:1310|1311|1312|1313|1314|499|(3:501|(1:1302)(1:505)|506)(1:1303)|507|(1:509)|510|511|(3:513|514|(39:516|(1:518)|519|520|521|522|523|524|525|526|528|529|530|531|532|533|534|(8:536|537|538|539|540|541|542|(23:544|545|546|548|549|550|551|552|553|(2:1232|1233)(1:555)|556|(7:1146|1147|(3:1223|1224|(4:1226|1150|(1:1152)|(11:(9:1192|1193|1194|1195|(1:1197)|1198|1199|(2:1201|1202)(2:1205|1206)|1203)(12:1155|1156|1157|1158|(3:1160|1161|1162)(2:1184|1185)|1163|1164|1165|1166|1167|1168|1169)|(1:561)(1:1145)|562|563|564|(2:(6:594|595|(1:1136)(4:598|599|600|601)|(5:1012|1013|1014|(5:1016|1017|1018|(4:1020|(1:1022)(1:1043)|1023|(1:1025)(1:1042))(1:1044)|1026)(5:1049|(2:1051|(1:(16:1054|1055|1056|(4:1104|1105|1106|(3:1108|1109|1110))(1:1058)|1059|1060|1061|1062|(1:1064)|1065|(1:1067)(2:1098|1099)|1068|(3:1075|1076|(9:1080|1081|1082|1083|(1:1085)|1086|1087|1088|1089))|1097|1088|1089))(3:1122|(1:1124)|1089))|1125|(0)|1089)|(3:1028|1029|(2:1031|1032)))(1:603)|604|(1:(11:609|610|(1:612)|613|614|615|616|(1:618)(2:829|(4:993|994|(1:996)|997)(2:831|(3:833|(1:865)(7:836|837|838|839|(3:841|842|(5:844|845|846|847|848))(1:859)|858|848)|849)(3:866|867|(4:869|870|(1:872)(1:987)|(12:874|875|(12:892|893|894|(5:(1:947)(3:899|900|901)|(3:905|(2:907|(2:908|(1:927)(3:910|(2:925|926)(2:916|(2:920|921))|923)))(0)|928)|929|930|(4:934|935|(1:937)|938))(2:948|(14:950|(3:954|(2:960|(2:962|963)(1:970))|971)|976|964|(1:967)|968|969|880|881|(1:883)(1:886)|884|885|(3:826|827|828)(5:621|(7:623|624|625|626|(1:628)(2:632|(23:634|(3:810|811|(1:813))(1:(20:637|(1:639)(1:805)|640|641|(1:804)(3:645|646|647)|648|(4:650|651|652|(6:654|655|656|657|658|(16:660|(3:785|786|787)(4:662|663|664|665)|666|667|668|669|670|(4:672|673|674|(1:678))(1:779)|679|(1:681)(1:772)|682|(1:771)(2:686|(3:688|(1:690)(1:766)|691)(3:767|(1:769)|770))|(1:693)(3:760|(1:764)|765)|(9:703|704|705|(1:707)(1:754)|708|709|710|(4:712|713|714|715)(1:749)|716)(1:695)|696|(3:698|(1:700)|701)(1:702))(13:791|792|670|(0)(0)|679|(0)(0)|682|(1:684)|771|(0)(0)|(0)(0)|696|(0)(0))))(1:803)|799|792|670|(0)(0)|679|(0)(0)|682|(0)|771|(0)(0)|(0)(0)|696|(0)(0))(1:806))|797|798|719|720|721|722|723|(1:745)(1:726)|727|728|729|730|731|732|733|734|735|570|571|(1:573)|575))|629|630)(1:825)|819|629|630)|631))|879|880|881|(0)(0)|884|885|(0)(0)|631)(1:877)|878|879|880|881|(0)(0)|884|885|(0)(0)|631)(4:983|984|985|986))(3:988|989|990))))|619|(0)(0)|631)))|608)|569|570|571|(0)|575)(1:1221)))|1149|1150|(0)|(0)(0))(1:558)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(1:1249))(1:1261)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575))(1:1293)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(6:1319|1320|1321|1322|1323|1324))|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)|522|523|524|525|526|528|529|530|531|532) */
    /* JADX WARN: Can't wrap try/catch for region: R(12:874|875|(12:892|893|894|(5:(1:947)(3:899|900|901)|(3:905|(2:907|(2:908|(1:927)(3:910|(2:925|926)(2:916|(2:920|921))|923)))(0)|928)|929|930|(4:934|935|(1:937)|938))(2:948|(14:950|(3:954|(2:960|(2:962|963)(1:970))|971)|976|964|(1:967)|968|969|880|881|(1:883)(1:886)|884|885|(3:826|827|828)(5:621|(7:623|624|625|626|(1:628)(2:632|(23:634|(3:810|811|(1:813))(1:(20:637|(1:639)(1:805)|640|641|(1:804)(3:645|646|647)|648|(4:650|651|652|(6:654|655|656|657|658|(16:660|(3:785|786|787)(4:662|663|664|665)|666|667|668|669|670|(4:672|673|674|(1:678))(1:779)|679|(1:681)(1:772)|682|(1:771)(2:686|(3:688|(1:690)(1:766)|691)(3:767|(1:769)|770))|(1:693)(3:760|(1:764)|765)|(9:703|704|705|(1:707)(1:754)|708|709|710|(4:712|713|714|715)(1:749)|716)(1:695)|696|(3:698|(1:700)|701)(1:702))(13:791|792|670|(0)(0)|679|(0)(0)|682|(1:684)|771|(0)(0)|(0)(0)|696|(0)(0))))(1:803)|799|792|670|(0)(0)|679|(0)(0)|682|(0)|771|(0)(0)|(0)(0)|696|(0)(0))(1:806))|797|798|719|720|721|722|723|(1:745)(1:726)|727|728|729|730|731|732|733|734|735|570|571|(1:573)|575))|629|630)(1:825)|819|629|630)|631))|879|880|881|(0)(0)|884|885|(0)(0)|631)(1:877)|878|879|880|881|(0)(0)|884|885|(0)(0)|631) */
    /* JADX WARN: Can't wrap try/catch for region: R(25:(2:496|497)(2:1308|(47:1310|1311|1312|1313|1314|499|(3:501|(1:1302)(1:505)|506)(1:1303)|507|(1:509)|510|511|(3:513|514|(39:516|(1:518)|519|520|521|522|523|524|525|526|528|529|530|531|532|533|534|(8:536|537|538|539|540|541|542|(23:544|545|546|548|549|550|551|552|553|(2:1232|1233)(1:555)|556|(7:1146|1147|(3:1223|1224|(4:1226|1150|(1:1152)|(11:(9:1192|1193|1194|1195|(1:1197)|1198|1199|(2:1201|1202)(2:1205|1206)|1203)(12:1155|1156|1157|1158|(3:1160|1161|1162)(2:1184|1185)|1163|1164|1165|1166|1167|1168|1169)|(1:561)(1:1145)|562|563|564|(2:(6:594|595|(1:1136)(4:598|599|600|601)|(5:1012|1013|1014|(5:1016|1017|1018|(4:1020|(1:1022)(1:1043)|1023|(1:1025)(1:1042))(1:1044)|1026)(5:1049|(2:1051|(1:(16:1054|1055|1056|(4:1104|1105|1106|(3:1108|1109|1110))(1:1058)|1059|1060|1061|1062|(1:1064)|1065|(1:1067)(2:1098|1099)|1068|(3:1075|1076|(9:1080|1081|1082|1083|(1:1085)|1086|1087|1088|1089))|1097|1088|1089))(3:1122|(1:1124)|1089))|1125|(0)|1089)|(3:1028|1029|(2:1031|1032)))(1:603)|604|(1:(11:609|610|(1:612)|613|614|615|616|(1:618)(2:829|(4:993|994|(1:996)|997)(2:831|(3:833|(1:865)(7:836|837|838|839|(3:841|842|(5:844|845|846|847|848))(1:859)|858|848)|849)(3:866|867|(4:869|870|(1:872)(1:987)|(12:874|875|(12:892|893|894|(5:(1:947)(3:899|900|901)|(3:905|(2:907|(2:908|(1:927)(3:910|(2:925|926)(2:916|(2:920|921))|923)))(0)|928)|929|930|(4:934|935|(1:937)|938))(2:948|(14:950|(3:954|(2:960|(2:962|963)(1:970))|971)|976|964|(1:967)|968|969|880|881|(1:883)(1:886)|884|885|(3:826|827|828)(5:621|(7:623|624|625|626|(1:628)(2:632|(23:634|(3:810|811|(1:813))(1:(20:637|(1:639)(1:805)|640|641|(1:804)(3:645|646|647)|648|(4:650|651|652|(6:654|655|656|657|658|(16:660|(3:785|786|787)(4:662|663|664|665)|666|667|668|669|670|(4:672|673|674|(1:678))(1:779)|679|(1:681)(1:772)|682|(1:771)(2:686|(3:688|(1:690)(1:766)|691)(3:767|(1:769)|770))|(1:693)(3:760|(1:764)|765)|(9:703|704|705|(1:707)(1:754)|708|709|710|(4:712|713|714|715)(1:749)|716)(1:695)|696|(3:698|(1:700)|701)(1:702))(13:791|792|670|(0)(0)|679|(0)(0)|682|(1:684)|771|(0)(0)|(0)(0)|696|(0)(0))))(1:803)|799|792|670|(0)(0)|679|(0)(0)|682|(0)|771|(0)(0)|(0)(0)|696|(0)(0))(1:806))|797|798|719|720|721|722|723|(1:745)(1:726)|727|728|729|730|731|732|733|734|735|570|571|(1:573)|575))|629|630)(1:825)|819|629|630)|631))|879|880|881|(0)(0)|884|885|(0)(0)|631)(1:877)|878|879|880|881|(0)(0)|884|885|(0)(0)|631)(4:983|984|985|986))(3:988|989|990))))|619|(0)(0)|631)))|608)|569|570|571|(0)|575)(1:1221)))|1149|1150|(0)|(0)(0))(1:558)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(1:1249))(1:1261)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575))(1:1293)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(6:1319|1320|1321|1322|1323|1324))|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575) */
    /* JADX WARN: Can't wrap try/catch for region: R(35:(2:496|497)(2:1308|(47:1310|1311|1312|1313|1314|499|(3:501|(1:1302)(1:505)|506)(1:1303)|507|(1:509)|510|511|(3:513|514|(39:516|(1:518)|519|520|521|522|523|524|525|526|528|529|530|531|532|533|534|(8:536|537|538|539|540|541|542|(23:544|545|546|548|549|550|551|552|553|(2:1232|1233)(1:555)|556|(7:1146|1147|(3:1223|1224|(4:1226|1150|(1:1152)|(11:(9:1192|1193|1194|1195|(1:1197)|1198|1199|(2:1201|1202)(2:1205|1206)|1203)(12:1155|1156|1157|1158|(3:1160|1161|1162)(2:1184|1185)|1163|1164|1165|1166|1167|1168|1169)|(1:561)(1:1145)|562|563|564|(2:(6:594|595|(1:1136)(4:598|599|600|601)|(5:1012|1013|1014|(5:1016|1017|1018|(4:1020|(1:1022)(1:1043)|1023|(1:1025)(1:1042))(1:1044)|1026)(5:1049|(2:1051|(1:(16:1054|1055|1056|(4:1104|1105|1106|(3:1108|1109|1110))(1:1058)|1059|1060|1061|1062|(1:1064)|1065|(1:1067)(2:1098|1099)|1068|(3:1075|1076|(9:1080|1081|1082|1083|(1:1085)|1086|1087|1088|1089))|1097|1088|1089))(3:1122|(1:1124)|1089))|1125|(0)|1089)|(3:1028|1029|(2:1031|1032)))(1:603)|604|(1:(11:609|610|(1:612)|613|614|615|616|(1:618)(2:829|(4:993|994|(1:996)|997)(2:831|(3:833|(1:865)(7:836|837|838|839|(3:841|842|(5:844|845|846|847|848))(1:859)|858|848)|849)(3:866|867|(4:869|870|(1:872)(1:987)|(12:874|875|(12:892|893|894|(5:(1:947)(3:899|900|901)|(3:905|(2:907|(2:908|(1:927)(3:910|(2:925|926)(2:916|(2:920|921))|923)))(0)|928)|929|930|(4:934|935|(1:937)|938))(2:948|(14:950|(3:954|(2:960|(2:962|963)(1:970))|971)|976|964|(1:967)|968|969|880|881|(1:883)(1:886)|884|885|(3:826|827|828)(5:621|(7:623|624|625|626|(1:628)(2:632|(23:634|(3:810|811|(1:813))(1:(20:637|(1:639)(1:805)|640|641|(1:804)(3:645|646|647)|648|(4:650|651|652|(6:654|655|656|657|658|(16:660|(3:785|786|787)(4:662|663|664|665)|666|667|668|669|670|(4:672|673|674|(1:678))(1:779)|679|(1:681)(1:772)|682|(1:771)(2:686|(3:688|(1:690)(1:766)|691)(3:767|(1:769)|770))|(1:693)(3:760|(1:764)|765)|(9:703|704|705|(1:707)(1:754)|708|709|710|(4:712|713|714|715)(1:749)|716)(1:695)|696|(3:698|(1:700)|701)(1:702))(13:791|792|670|(0)(0)|679|(0)(0)|682|(1:684)|771|(0)(0)|(0)(0)|696|(0)(0))))(1:803)|799|792|670|(0)(0)|679|(0)(0)|682|(0)|771|(0)(0)|(0)(0)|696|(0)(0))(1:806))|797|798|719|720|721|722|723|(1:745)(1:726)|727|728|729|730|731|732|733|734|735|570|571|(1:573)|575))|629|630)(1:825)|819|629|630)|631))|879|880|881|(0)(0)|884|885|(0)(0)|631)(1:877)|878|879|880|881|(0)(0)|884|885|(0)(0)|631)(4:983|984|985|986))(3:988|989|990))))|619|(0)(0)|631)))|608)|569|570|571|(0)|575)(1:1221)))|1149|1150|(0)|(0)(0))(1:558)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(1:1249))(1:1261)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575))(1:1293)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(6:1319|1320|1321|1322|1323|1324))|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575) */
    /* JADX WARN: Can't wrap try/catch for region: R(46:472|(14:473|474|475|(3:477|478|479)(2:1355|1356)|480|481|482|(3:484|(1:486)(2:1342|(1:1344)(1:1345))|487)(1:(1:1347)(1:1348))|488|(2:1335|1336)|490|(1:492)(1:1334)|493|494)|(2:496|497)(2:1308|(47:1310|1311|1312|1313|1314|499|(3:501|(1:1302)(1:505)|506)(1:1303)|507|(1:509)|510|511|(3:513|514|(39:516|(1:518)|519|520|521|522|523|524|525|526|528|529|530|531|532|533|534|(8:536|537|538|539|540|541|542|(23:544|545|546|548|549|550|551|552|553|(2:1232|1233)(1:555)|556|(7:1146|1147|(3:1223|1224|(4:1226|1150|(1:1152)|(11:(9:1192|1193|1194|1195|(1:1197)|1198|1199|(2:1201|1202)(2:1205|1206)|1203)(12:1155|1156|1157|1158|(3:1160|1161|1162)(2:1184|1185)|1163|1164|1165|1166|1167|1168|1169)|(1:561)(1:1145)|562|563|564|(2:(6:594|595|(1:1136)(4:598|599|600|601)|(5:1012|1013|1014|(5:1016|1017|1018|(4:1020|(1:1022)(1:1043)|1023|(1:1025)(1:1042))(1:1044)|1026)(5:1049|(2:1051|(1:(16:1054|1055|1056|(4:1104|1105|1106|(3:1108|1109|1110))(1:1058)|1059|1060|1061|1062|(1:1064)|1065|(1:1067)(2:1098|1099)|1068|(3:1075|1076|(9:1080|1081|1082|1083|(1:1085)|1086|1087|1088|1089))|1097|1088|1089))(3:1122|(1:1124)|1089))|1125|(0)|1089)|(3:1028|1029|(2:1031|1032)))(1:603)|604|(1:(11:609|610|(1:612)|613|614|615|616|(1:618)(2:829|(4:993|994|(1:996)|997)(2:831|(3:833|(1:865)(7:836|837|838|839|(3:841|842|(5:844|845|846|847|848))(1:859)|858|848)|849)(3:866|867|(4:869|870|(1:872)(1:987)|(12:874|875|(12:892|893|894|(5:(1:947)(3:899|900|901)|(3:905|(2:907|(2:908|(1:927)(3:910|(2:925|926)(2:916|(2:920|921))|923)))(0)|928)|929|930|(4:934|935|(1:937)|938))(2:948|(14:950|(3:954|(2:960|(2:962|963)(1:970))|971)|976|964|(1:967)|968|969|880|881|(1:883)(1:886)|884|885|(3:826|827|828)(5:621|(7:623|624|625|626|(1:628)(2:632|(23:634|(3:810|811|(1:813))(1:(20:637|(1:639)(1:805)|640|641|(1:804)(3:645|646|647)|648|(4:650|651|652|(6:654|655|656|657|658|(16:660|(3:785|786|787)(4:662|663|664|665)|666|667|668|669|670|(4:672|673|674|(1:678))(1:779)|679|(1:681)(1:772)|682|(1:771)(2:686|(3:688|(1:690)(1:766)|691)(3:767|(1:769)|770))|(1:693)(3:760|(1:764)|765)|(9:703|704|705|(1:707)(1:754)|708|709|710|(4:712|713|714|715)(1:749)|716)(1:695)|696|(3:698|(1:700)|701)(1:702))(13:791|792|670|(0)(0)|679|(0)(0)|682|(1:684)|771|(0)(0)|(0)(0)|696|(0)(0))))(1:803)|799|792|670|(0)(0)|679|(0)(0)|682|(0)|771|(0)(0)|(0)(0)|696|(0)(0))(1:806))|797|798|719|720|721|722|723|(1:745)(1:726)|727|728|729|730|731|732|733|734|735|570|571|(1:573)|575))|629|630)(1:825)|819|629|630)|631))|879|880|881|(0)(0)|884|885|(0)(0)|631)(1:877)|878|879|880|881|(0)(0)|884|885|(0)(0)|631)(4:983|984|985|986))(3:988|989|990))))|619|(0)(0)|631)))|608)|569|570|571|(0)|575)(1:1221)))|1149|1150|(0)|(0)(0))(1:558)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(1:1249))(1:1261)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575))(1:1293)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(6:1319|1320|1321|1322|1323|1324))|498|499|(0)(0)|507|(0)|510|511|(0)(0)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575) */
    /* JADX WARN: Can't wrap try/catch for region: R(59:472|473|474|475|(3:477|478|479)(2:1355|1356)|480|481|482|(3:484|(1:486)(2:1342|(1:1344)(1:1345))|487)(1:(1:1347)(1:1348))|488|(2:1335|1336)|490|(1:492)(1:1334)|493|494|(2:496|497)(2:1308|(47:1310|1311|1312|1313|1314|499|(3:501|(1:1302)(1:505)|506)(1:1303)|507|(1:509)|510|511|(3:513|514|(39:516|(1:518)|519|520|521|522|523|524|525|526|528|529|530|531|532|533|534|(8:536|537|538|539|540|541|542|(23:544|545|546|548|549|550|551|552|553|(2:1232|1233)(1:555)|556|(7:1146|1147|(3:1223|1224|(4:1226|1150|(1:1152)|(11:(9:1192|1193|1194|1195|(1:1197)|1198|1199|(2:1201|1202)(2:1205|1206)|1203)(12:1155|1156|1157|1158|(3:1160|1161|1162)(2:1184|1185)|1163|1164|1165|1166|1167|1168|1169)|(1:561)(1:1145)|562|563|564|(2:(6:594|595|(1:1136)(4:598|599|600|601)|(5:1012|1013|1014|(5:1016|1017|1018|(4:1020|(1:1022)(1:1043)|1023|(1:1025)(1:1042))(1:1044)|1026)(5:1049|(2:1051|(1:(16:1054|1055|1056|(4:1104|1105|1106|(3:1108|1109|1110))(1:1058)|1059|1060|1061|1062|(1:1064)|1065|(1:1067)(2:1098|1099)|1068|(3:1075|1076|(9:1080|1081|1082|1083|(1:1085)|1086|1087|1088|1089))|1097|1088|1089))(3:1122|(1:1124)|1089))|1125|(0)|1089)|(3:1028|1029|(2:1031|1032)))(1:603)|604|(1:(11:609|610|(1:612)|613|614|615|616|(1:618)(2:829|(4:993|994|(1:996)|997)(2:831|(3:833|(1:865)(7:836|837|838|839|(3:841|842|(5:844|845|846|847|848))(1:859)|858|848)|849)(3:866|867|(4:869|870|(1:872)(1:987)|(12:874|875|(12:892|893|894|(5:(1:947)(3:899|900|901)|(3:905|(2:907|(2:908|(1:927)(3:910|(2:925|926)(2:916|(2:920|921))|923)))(0)|928)|929|930|(4:934|935|(1:937)|938))(2:948|(14:950|(3:954|(2:960|(2:962|963)(1:970))|971)|976|964|(1:967)|968|969|880|881|(1:883)(1:886)|884|885|(3:826|827|828)(5:621|(7:623|624|625|626|(1:628)(2:632|(23:634|(3:810|811|(1:813))(1:(20:637|(1:639)(1:805)|640|641|(1:804)(3:645|646|647)|648|(4:650|651|652|(6:654|655|656|657|658|(16:660|(3:785|786|787)(4:662|663|664|665)|666|667|668|669|670|(4:672|673|674|(1:678))(1:779)|679|(1:681)(1:772)|682|(1:771)(2:686|(3:688|(1:690)(1:766)|691)(3:767|(1:769)|770))|(1:693)(3:760|(1:764)|765)|(9:703|704|705|(1:707)(1:754)|708|709|710|(4:712|713|714|715)(1:749)|716)(1:695)|696|(3:698|(1:700)|701)(1:702))(13:791|792|670|(0)(0)|679|(0)(0)|682|(1:684)|771|(0)(0)|(0)(0)|696|(0)(0))))(1:803)|799|792|670|(0)(0)|679|(0)(0)|682|(0)|771|(0)(0)|(0)(0)|696|(0)(0))(1:806))|797|798|719|720|721|722|723|(1:745)(1:726)|727|728|729|730|731|732|733|734|735|570|571|(1:573)|575))|629|630)(1:825)|819|629|630)|631))|879|880|881|(0)(0)|884|885|(0)(0)|631)(1:877)|878|879|880|881|(0)(0)|884|885|(0)(0)|631)(4:983|984|985|986))(3:988|989|990))))|619|(0)(0)|631)))|608)|569|570|571|(0)|575)(1:1221)))|1149|1150|(0)|(0)(0))(1:558)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(1:1249))(1:1261)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575))(1:1293)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575)(6:1319|1320|1321|1322|1323|1324))|498|499|(0)(0)|507|(0)|510|511|(0)(0)|1291|522|523|524|525|526|528|529|530|531|532|533|534|(0)(0)|1250|548|549|550|551|552|553|(0)(0)|556|(0)(0)|559|(0)(0)|562|563|564|(9:(0)|594|595|(0)|1136|(0)(0)|604|(12:(0)|609|610|(0)|613|614|615|616|(0)(0)|619|(0)(0)|631)|608)|569|570|571|(0)|575) */
    /* JADX WARN: Code restructure failed: missing block: B:1004:0x122b, code lost:
        r10 = r87;
        r93 = r11;
        r4 = r54;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1005:0x1247, code lost:
        throw new java.lang.RuntimeException("unexpected result from decoder.dequeueOutputBuffer: " + r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:1020:0x12a4, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1021:0x12a5, code lost:
        r10 = r87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1022:0x12a9, code lost:
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1023:0x12aa, code lost:
        r10 = r87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1053:0x134e, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1054:0x134f, code lost:
        r10 = r87;
        r5 = r88;
        r69 = r9;
        r23 = r14;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r3 = r21;
        r13 = -5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1055:0x1368, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1056:0x1369, code lost:
        r10 = r87;
        r5 = r88;
        r23 = r14;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1057:0x137c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1058:0x137d, code lost:
        r10 = r87;
        r5 = r88;
        r23 = r14;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r8 = r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1059:0x1391, code lost:
        r3 = r21;
        r13 = -5;
        r10 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1062:0x139c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1063:0x139d, code lost:
        r10 = r87;
        r5 = r88;
        r14 = r2;
        r71 = r55;
        r72 = r92;
        r44 = r94;
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1064:0x13af, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1065:0x13b0, code lost:
        r10 = r87;
        r94 = r14;
        r71 = r30;
        r15 = r78;
        r14 = r2;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r54 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1066:0x13c1, code lost:
        r8 = r14;
        r3 = r21;
        r13 = -5;
        r23 = null;
        r10 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1067:0x13c7, code lost:
        r69 = null;
        r10 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1069:0x13cc, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1070:0x13cd, code lost:
        r10 = r87;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r54 = r4;
        r14 = r5;
        r3 = r21;
        r8 = null;
        r13 = -5;
        r23 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1071:0x13e6, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1072:0x13e7, code lost:
        r10 = r87;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r14 = r5;
        r3 = r21;
        r8 = null;
        r13 = -5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1078:0x1411, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:1079:0x1412, code lost:
        r10 = r87;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r3 = r21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:434:0x087b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:435:0x087c, code lost:
        r2 = r85;
        r72 = r92;
        r1 = r0;
        r7 = r3;
        r10 = r87;
        r44 = r14;
        r6 = false;
        r13 = -5;
        r15 = r78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:436:0x088b, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:437:0x088c, code lost:
        r72 = r92;
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x01fb, code lost:
        r6 = r7;
        r13 = r8;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:1001:0x1225  */
    /* JADX WARN: Removed duplicated region for block: B:1099:0x1480 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:1110:0x14cc A[Catch: all -> 0x14dc, TRY_LEAVE, TryCatch #108 {all -> 0x14dc, blocks: (B:1108:0x14c3, B:1110:0x14cc), top: B:1291:0x14c3 }] */
    /* JADX WARN: Removed duplicated region for block: B:1124:0x1502  */
    /* JADX WARN: Removed duplicated region for block: B:1131:0x152d A[Catch: all -> 0x1521, TryCatch #0 {all -> 0x1521, blocks: (B:1126:0x151d, B:1131:0x152d, B:1133:0x1532, B:1135:0x153a, B:1136:0x153d), top: B:1186:0x151d }] */
    /* JADX WARN: Removed duplicated region for block: B:1133:0x1532 A[Catch: all -> 0x1521, TryCatch #0 {all -> 0x1521, blocks: (B:1126:0x151d, B:1131:0x152d, B:1133:0x1532, B:1135:0x153a, B:1136:0x153d), top: B:1186:0x151d }] */
    /* JADX WARN: Removed duplicated region for block: B:1135:0x153a A[Catch: all -> 0x1521, TryCatch #0 {all -> 0x1521, blocks: (B:1126:0x151d, B:1131:0x152d, B:1133:0x1532, B:1135:0x153a, B:1136:0x153d), top: B:1186:0x151d }] */
    /* JADX WARN: Removed duplicated region for block: B:1140:0x1548  */
    /* JADX WARN: Removed duplicated region for block: B:1158:0x15b5  */
    /* JADX WARN: Removed duplicated region for block: B:1167:0x15d3  */
    /* JADX WARN: Removed duplicated region for block: B:1169:0x1602  */
    /* JADX WARN: Removed duplicated region for block: B:1186:0x151d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1210:0x0643 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1217:0x0a0d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1225:0x154f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1281:0x15bc A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1298:0x11d7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1324:0x0a2e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1352:0x0bd0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1389:0x045b A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1391:0x044c A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1412:0x1028 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:1414:0x1009 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:207:0x043a  */
    /* JADX WARN: Removed duplicated region for block: B:208:0x043c  */
    /* JADX WARN: Removed duplicated region for block: B:297:0x05fc A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:311:0x0658 A[Catch: all -> 0x0647, TryCatch #24 {all -> 0x0647, blocks: (B:307:0x0643, B:311:0x0658, B:313:0x065d, B:314:0x0663), top: B:1210:0x0643 }] */
    /* JADX WARN: Removed duplicated region for block: B:313:0x065d A[Catch: all -> 0x0647, TryCatch #24 {all -> 0x0647, blocks: (B:307:0x0643, B:311:0x0658, B:313:0x065d, B:314:0x0663), top: B:1210:0x0643 }] */
    /* JADX WARN: Removed duplicated region for block: B:362:0x0744  */
    /* JADX WARN: Removed duplicated region for block: B:426:0x0861  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x08a1  */
    /* JADX WARN: Removed duplicated region for block: B:442:0x08ab A[Catch: all -> 0x087b, Exception -> 0x088b, TRY_ENTER, TRY_LEAVE, TryCatch #129 {Exception -> 0x088b, all -> 0x087b, blocks: (B:431:0x086c, B:442:0x08ab, B:446:0x08e9, B:432:0x0871), top: B:1362:0x085f }] */
    /* JADX WARN: Removed duplicated region for block: B:446:0x08e9 A[Catch: all -> 0x087b, Exception -> 0x088b, TRY_ENTER, TRY_LEAVE, TryCatch #129 {Exception -> 0x088b, all -> 0x087b, blocks: (B:431:0x086c, B:442:0x08ab, B:446:0x08e9, B:432:0x0871), top: B:1362:0x085f }] */
    /* JADX WARN: Removed duplicated region for block: B:459:0x091b  */
    /* JADX WARN: Removed duplicated region for block: B:469:0x0989  */
    /* JADX WARN: Removed duplicated region for block: B:497:0x09ee  */
    /* JADX WARN: Removed duplicated region for block: B:509:0x0a28  */
    /* JADX WARN: Removed duplicated region for block: B:523:0x0a68  */
    /* JADX WARN: Removed duplicated region for block: B:525:0x0a6b  */
    /* JADX WARN: Removed duplicated region for block: B:575:0x0b21  */
    /* JADX WARN: Removed duplicated region for block: B:584:0x0b4e  */
    /* JADX WARN: Removed duplicated region for block: B:587:0x0b5c  */
    /* JADX WARN: Removed duplicated region for block: B:588:0x0b5e  */
    /* JADX WARN: Removed duplicated region for block: B:592:0x0b7f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:598:0x0b9f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:696:0x0d44  */
    /* JADX WARN: Removed duplicated region for block: B:714:0x0d84  */
    /* JADX WARN: Removed duplicated region for block: B:717:0x0da5 A[ADDED_TO_REGION, EDGE_INSN: B:717:0x0da5->B:1413:0x0da8 ?: BREAK  ] */
    /* JADX WARN: Removed duplicated region for block: B:722:0x0dc8  */
    /* JADX WARN: Removed duplicated region for block: B:727:0x0dd9  */
    /* JADX WARN: Removed duplicated region for block: B:729:0x0df1  */
    /* JADX WARN: Removed duplicated region for block: B:861:0x0ffa  */
    /* JADX WARN: Removed duplicated region for block: B:862:0x0ffc  */
    /* JADX WARN: Removed duplicated region for block: B:939:0x1156  */
    /* JADX WARN: Removed duplicated region for block: B:947:0x116b  */
    /* JADX WARN: Removed duplicated region for block: B:950:0x1173  */
    /* JADX WARN: Removed duplicated region for block: B:951:0x1177  */
    /* JADX WARN: Removed duplicated region for block: B:954:0x117e  */
    /* JADX WARN: Removed duplicated region for block: B:969:0x11bf  */
    /* JADX WARN: Removed duplicated region for block: B:970:0x11c2  */
    /* JADX WARN: Removed duplicated region for block: B:994:0x120a  */
    /* JADX WARN: Removed duplicated region for block: B:997:0x1216 A[Catch: all -> 0x1248, Exception -> 0x124a, TryCatch #25 {all -> 0x1248, blocks: (B:940:0x1158, B:942:0x1160, B:958:0x1186, B:960:0x118a, B:977:0x11d7, B:980:0x11df, B:983:0x11e6, B:989:0x11f3, B:991:0x11fe, B:995:0x1210, B:997:0x1216, B:999:0x121a, B:1000:0x121f, B:987:0x11ed, B:974:0x11cc, B:975:0x11d2, B:963:0x11ad, B:965:0x11b7, B:1004:0x122b, B:1005:0x1247), top: B:1212:0x1158 }] */
    /* JADX WARN: Type inference failed for: r14v49 */
    /* JADX WARN: Type inference failed for: r44v103 */
    /* JADX WARN: Type inference failed for: r44v104 */
    /* JADX WARN: Type inference failed for: r44v166 */
    /* JADX WARN: Type inference failed for: r44v167 */
    /* JADX WARN: Type inference failed for: r44v168 */
    /* JADX WARN: Type inference failed for: r44v169 */
    /* JADX WARN: Type inference failed for: r4v124 */
    /* JADX WARN: Type inference failed for: r4v185 */
    /* JADX WARN: Type inference failed for: r4v186 */
    /* JADX WARN: Type inference failed for: r4v187 */
    /* JADX WARN: Type inference failed for: r4v193 */
    /* JADX WARN: Type inference failed for: r4v194 */
    /* JADX WARN: Type inference failed for: r4v195 */
    /* JADX WARN: Type inference failed for: r4v196 */
    /* JADX WARN: Type inference failed for: r4v197 */
    /* JADX WARN: Type inference failed for: r4v39 */
    /* JADX WARN: Type inference failed for: r4v41 */
    /* JADX WARN: Type inference failed for: r4v46, types: [java.nio.ByteBuffer] */
    /* JADX WARN: Type inference failed for: r4v47 */
    /* JADX WARN: Type inference failed for: r4v48 */
    /* JADX WARN: Type inference failed for: r4v49 */
    /* JADX WARN: Type inference failed for: r4v54 */
    /* JADX WARN: Type inference failed for: r4v55 */
    /* JADX WARN: Type inference failed for: r4v85 */
    /* JADX WARN: Type inference failed for: r4v86 */
    /* JADX WARN: Type inference failed for: r5v41, types: [android.media.MediaExtractor] */
    /* JADX WARN: Type inference failed for: r5v47, types: [org.telegram.messenger.video.MP4Builder] */
    /* JADX WARN: Type inference failed for: r8v34 */
    /* JADX WARN: Type inference failed for: r9v40, types: [org.telegram.messenger.video.InputSurface] */
    /* JADX WARN: Type inference failed for: r9v41 */
    /* JADX WARN: Type inference failed for: r9v53, types: [org.telegram.messenger.video.InputSurface] */
    /* JADX WARN: Type inference failed for: r9v55 */
    /* JADX WARN: Type inference failed for: r9v62, types: [org.telegram.messenger.video.InputSurface] */
    @android.annotation.TargetApi(18)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean convertVideoInternal(java.lang.String r79, java.io.File r80, int r81, boolean r82, int r83, int r84, int r85, int r86, int r87, int r88, int r89, long r90, long r92, long r94, long r96, boolean r98, boolean r99, org.telegram.messenger.MediaController.SavedFilterState r100, java.lang.String r101, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r102, boolean r103, org.telegram.messenger.MediaController.CropState r104, boolean r105) {
        /*
            Method dump skipped, instructions count: 5734
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState, boolean):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:73:0x0123, code lost:
        if (r9[r6 + 3] != 1) goto L62;
     */
    /* JADX WARN: Removed duplicated region for block: B:119:0x01d0  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x01d5  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x01ec  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x01ee  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00df  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private long readAndWriteTracks(android.media.MediaExtractor r29, org.telegram.messenger.video.MP4Builder r30, android.media.MediaCodec.BufferInfo r31, long r32, long r34, long r36, java.io.File r38, boolean r39) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 524
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        MediaController.VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener == null || !videoConvertorListener.checkConversionCanceled()) {
            return;
        }
        throw new ConversionCanceledException();
    }

    private static String createFragmentShader(int i, int i2, int i3, int i4, boolean z) {
        int clamp = (int) Utilities.clamp((Math.max(i, i2) / Math.max(i4, i3)) * 0.8f, 2.0f, 1.0f);
        FileLog.d("source size " + i + "x" + i2 + "    dest size " + i3 + i4 + "   kernelRadius " + clamp);
        if (z) {
            return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + clamp + ".0;\nconst float pixelSizeX = 1.0 / " + i + ".0;\nconst float pixelSizeY = 1.0 / " + i2 + ".0;\nuniform samplerExternalOES sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
        }
        return "precision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + clamp + ".0;\nconst float pixelSizeX = 1.0 / " + i2 + ".0;\nconst float pixelSizeY = 1.0 / " + i + ".0;\nuniform sampler2D sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
    }

    /* loaded from: classes.dex */
    public class ConversionCanceledException extends RuntimeException {
        public ConversionCanceledException() {
            super("canceled conversion");
        }
    }
}
