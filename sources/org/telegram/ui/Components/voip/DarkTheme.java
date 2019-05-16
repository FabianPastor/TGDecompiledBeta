package org.telegram.ui.Components.voip;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

public class DarkTheme {
    public static int getColor(java.lang.String r17) {
        /*
        r0 = r17;
        r1 = r17.hashCode();
        r2 = -1;
        switch(r1) {
            case -2147269658: goto L_0x0c3e;
            case -2139469579: goto L_0x0CLASSNAME;
            case -2132427577: goto L_0x0CLASSNAME;
            case -2103805301: goto L_0x0c1d;
            case -2102232027: goto L_0x0CLASSNAME;
            case -2019587427: goto L_0x0CLASSNAME;
            case -1992864503: goto L_0x0bfc;
            case -1992639563: goto L_0x0bf1;
            case -1975862704: goto L_0x0be6;
            case -1974166005: goto L_0x0bda;
            case -1961633574: goto L_0x0bce;
            case -1942198229: goto L_0x0bc2;
            case -1927175348: goto L_0x0bb6;
            case -1926854985: goto L_0x0baa;
            case -1926854984: goto L_0x0b9d;
            case -1926854983: goto L_0x0b90;
            case -1924841028: goto L_0x0b84;
            case -1891930735: goto L_0x0b78;
            case -1878988531: goto L_0x0b6c;
            case -1853661732: goto L_0x0b60;
            case -1850167367: goto L_0x0b54;
            case -1849805674: goto L_0x0b48;
            case -1787129273: goto L_0x0b3c;
            case -1779173263: goto L_0x0b30;
            case -1777297962: goto L_0x0b24;
            case -1767675171: goto L_0x0b18;
            case -1758608141: goto L_0x0b0b;
            case -1733632792: goto L_0x0aff;
            case -1724033454: goto L_0x0af3;
            case -1719903102: goto L_0x0ae7;
            case -1719839798: goto L_0x0adb;
            case -1683744660: goto L_0x0acf;
            case -1654302575: goto L_0x0ac3;
            case -1633591792: goto L_0x0ab7;
            case -1625862693: goto L_0x0aab;
            case -1623818608: goto L_0x0a9f;
            case -1604008580: goto L_0x0a93;
            case -1589702002: goto L_0x0a87;
            case -1565843249: goto L_0x0a7b;
            case -1543133775: goto L_0x0a6f;
            case -1542353776: goto L_0x0a63;
            case -1533503664: goto L_0x0a57;
            case -1530345450: goto L_0x0a4b;
            case -1496224782: goto L_0x0a3f;
            case -1415980195: goto L_0x0a33;
            case -1407570354: goto L_0x0a27;
            case -1397026623: goto L_0x0a1b;
            case -1385379359: goto L_0x0a0f;
            case -1316415606: goto L_0x0a04;
            case -1310183623: goto L_0x09f8;
            case -1262649070: goto L_0x09ec;
            case -1240647597: goto L_0x09e0;
            case -1229478359: goto L_0x09d4;
            case -1213387098: goto L_0x09c8;
            case -1147596450: goto L_0x09bc;
            case -1106471792: goto L_0x09b0;
            case -1078554766: goto L_0x09a4;
            case -1074293766: goto L_0x0998;
            case -1063762099: goto L_0x098b;
            case -1062379852: goto L_0x097f;
            case -1046600742: goto L_0x0973;
            case -1019316079: goto L_0x0967;
            case -1012016554: goto L_0x095c;
            case -1006953508: goto L_0x0951;
            case -1005376655: goto L_0x0945;
            case -1005120019: goto L_0x0939;
            case -1004973057: goto L_0x092d;
            case -960321732: goto L_0x0921;
            case -955211830: goto L_0x0915;
            case -938826921: goto L_0x0909;
            case -901363160: goto L_0x08fd;
            case -834035478: goto L_0x08f1;
            case -810517465: goto L_0x08e5;
            case -805096120: goto L_0x08d9;
            case -792942846: goto L_0x08cd;
            case -779362418: goto L_0x08c1;
            case -763385518: goto L_0x08b5;
            case -763087825: goto L_0x08a9;
            case -756337980: goto L_0x089d;
            case -712338357: goto L_0x0891;
            case -687452692: goto L_0x0885;
            case -654429213: goto L_0x0879;
            case -652337344: goto L_0x086d;
            case -629209323: goto L_0x0861;
            case -608456434: goto L_0x0855;
            case -603597494: goto L_0x0849;
            case -570274322: goto L_0x083d;
            case -564899147: goto L_0x0831;
            case -560721948: goto L_0x0825;
            case -552118908: goto L_0x0819;
            case -493564645: goto L_0x080d;
            case -450514995: goto L_0x0801;
            case -427186938: goto L_0x07f5;
            case -391617936: goto L_0x07e9;
            case -354489314: goto L_0x07dd;
            case -343666293: goto L_0x07d1;
            case -294026410: goto L_0x07c5;
            case -264184037: goto L_0x07b9;
            case -260428237: goto L_0x07ad;
            case -258492929: goto L_0x07a1;
            case -251079667: goto L_0x0795;
            case -249481380: goto L_0x0789;
            case -248568965: goto L_0x077d;
            case -212237793: goto L_0x0771;
            case -185786131: goto L_0x0765;
            case -176488427: goto L_0x0759;
            case -143547632: goto L_0x074d;
            case -127673038: goto L_0x0741;
            case -108292334: goto L_0x0735;
            case -71280336: goto L_0x0729;
            case -65607089: goto L_0x071e;
            case -65277181: goto L_0x0712;
            case -35597940: goto L_0x0706;
            case -18073397: goto L_0x06fa;
            case -12871922: goto L_0x06ee;
            case 6289575: goto L_0x06e2;
            case 27337780: goto L_0x06d6;
            case 49148112: goto L_0x06ca;
            case 51359814: goto L_0x06be;
            case 57332012: goto L_0x06b2;
            case 57460786: goto L_0x06a6;
            case 89466127: goto L_0x069a;
            case 117743477: goto L_0x068e;
            case 141076636: goto L_0x0682;
            case 141894978: goto L_0x0675;
            case 185438775: goto L_0x0669;
            case 216441603: goto L_0x065d;
            case 231486891: goto L_0x0651;
            case 243668262: goto L_0x0646;
            case 257089712: goto L_0x063a;
            case 271457747: goto L_0x062e;
            case 303350244: goto L_0x0622;
            case 316847509: goto L_0x0616;
            case 339397761: goto L_0x060a;
            case 371859081: goto L_0x05fe;
            case 415452907: goto L_0x05f2;
            case 421601145: goto L_0x05e7;
            case 421601469: goto L_0x05db;
            case 426061980: goto L_0x05cf;
            case 429680544: goto L_0x05c3;
            case 429722217: goto L_0x05b7;
            case 430094524: goto L_0x05ac;
            case 435303214: goto L_0x05a0;
            case 439976061: goto L_0x0594;
            case 444983522: goto L_0x0588;
            case 446162770: goto L_0x057c;
            case 460598594: goto L_0x0570;
            case 484353662: goto L_0x0564;
            case 503923205: goto L_0x0558;
            case 527405547: goto L_0x054c;
            case 556028747: goto L_0x0540;
            case 589961756: goto L_0x0534;
            case 613458991: goto L_0x0528;
            case 626157205: goto L_0x051c;
            case 634019162: goto L_0x0510;
            case 635007317: goto L_0x0504;
            case 648238646: goto L_0x04f8;
            case 655457041: goto L_0x04ec;
            case 676996437: goto L_0x04e0;
            case 716656587: goto L_0x04d4;
            case 732262561: goto L_0x04c8;
            case 759679774: goto L_0x04bc;
            case 765296599: goto L_0x04b0;
            case 803672502: goto L_0x04a4;
            case 826015922: goto L_0x0499;
            case 850854541: goto L_0x048d;
            case 890367586: goto L_0x0481;
            case 911091978: goto L_0x0475;
            case 913069217: goto L_0x0469;
            case 927863384: goto L_0x045d;
            case 939137799: goto L_0x0451;
            case 939824634: goto L_0x0445;
            case 946144034: goto L_0x0439;
            case 962085693: goto L_0x042d;
            case 983278580: goto L_0x0421;
            case 993048796: goto L_0x0415;
            case 1008947016: goto L_0x0409;
            case 1020100908: goto L_0x03fd;
            case 1045892135: goto L_0x03f1;
            case 1046222043: goto L_0x03e5;
            case 1079427869: goto L_0x03d9;
            case 1100033490: goto L_0x03cd;
            case 1106068251: goto L_0x03c1;
            case 1121079660: goto L_0x03b5;
            case 1122192435: goto L_0x03a9;
            case 1175786053: goto L_0x039d;
            case 1195322391: goto L_0x0391;
            case 1199344772: goto L_0x0385;
            case 1201609915: goto L_0x0379;
            case 1202885960: goto L_0x036d;
            case 1212117123: goto L_0x0361;
            case 1212158796: goto L_0x0355;
            case 1212531103: goto L_0x0349;
            case 1231763334: goto L_0x033d;
            case 1239758101: goto L_0x0331;
            case 1265168609: goto L_0x0325;
            case 1269980952: goto L_0x0319;
            case 1275014009: goto L_0x030d;
            case 1285554199: goto L_0x0301;
            case 1288729698: goto L_0x02f5;
            case 1308150651: goto L_0x02e9;
            case 1316752473: goto L_0x02dd;
            case 1327229315: goto L_0x02d1;
            case 1333190005: goto L_0x02c5;
            case 1372411761: goto L_0x02b9;
            case 1381159341: goto L_0x02ad;
            case 1411374187: goto L_0x02a1;
            case 1411728145: goto L_0x0295;
            case 1414117958: goto L_0x0289;
            case 1449754706: goto L_0x027d;
            case 1450167170: goto L_0x0271;
            case 1456911705: goto L_0x0265;
            case 1478061672: goto L_0x0259;
            case 1491567659: goto L_0x024d;
            case 1504078167: goto L_0x0241;
            case 1528152827: goto L_0x0235;
            case 1549064140: goto L_0x0229;
            case 1573464919: goto L_0x021d;
            case 1585168289: goto L_0x0211;
            case 1595048395: goto L_0x0205;
            case 1628297471: goto L_0x01f9;
            case 1635685130: goto L_0x01ed;
            case 1637669025: goto L_0x01e1;
            case 1647377944: goto L_0x01d5;
            case 1657795113: goto L_0x01c9;
            case 1657923887: goto L_0x01bd;
            case 1663688926: goto L_0x01b1;
            case 1674274489: goto L_0x01a5;
            case 1674318617: goto L_0x0199;
            case 1676443787: goto L_0x018d;
            case 1682961989: goto L_0x0181;
            case 1687612836: goto L_0x0175;
            case 1714118894: goto L_0x0169;
            case 1743255577: goto L_0x015d;
            case 1809914009: goto L_0x0151;
            case 1814021667: goto L_0x0145;
            case 1828201066: goto L_0x0139;
            case 1829565163: goto L_0x012d;
            case 1853943154: goto L_0x0121;
            case 1878895888: goto L_0x0115;
            case 1878937561: goto L_0x0109;
            case 1879309868: goto L_0x00fd;
            case 1921699010: goto L_0x00f1;
            case 1929729373: goto L_0x00e5;
            case 1930276193: goto L_0x00d9;
            case 1947549395: goto L_0x00cd;
            case 1972802227: goto L_0x00c1;
            case 1979989987: goto L_0x00b5;
            case 1994112714: goto L_0x00a9;
            case 2016144760: goto L_0x009d;
            case 2016511272: goto L_0x0091;
            case 2052611411: goto L_0x0085;
            case 2067556030: goto L_0x0079;
            case 2073762588: goto L_0x006d;
            case 2090082520: goto L_0x0061;
            case 2099978769: goto L_0x0055;
            case 2109820260: goto L_0x0049;
            case 2118871810: goto L_0x003d;
            case 2119150199: goto L_0x0031;
            case 2131990258: goto L_0x0024;
            case 2133456819: goto L_0x0018;
            case 2141345810: goto L_0x000c;
            default: goto L_0x000a;
        };
    L_0x000a:
        goto L_0x0CLASSNAME;
    L_0x000c:
        r1 = "chat_messagePanelBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0014:
        r1 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        goto L_0x0c4a;
    L_0x0018:
        r1 = "chat_emojiPanelBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0020:
        r1 = 152; // 0x98 float:2.13E-43 double:7.5E-322;
        goto L_0x0c4a;
    L_0x0024:
        r1 = "windowBackgroundWhiteLinkText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x002d:
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        goto L_0x0c4a;
    L_0x0031:
        r1 = "switchTrack";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0039:
        r1 = 260; // 0x104 float:3.64E-43 double:1.285E-321;
        goto L_0x0c4a;
    L_0x003d:
        r1 = "switchThumb";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0045:
        r1 = 35;
        goto L_0x0c4a;
    L_0x0049:
        r1 = "avatar_actionBarSelectorOrange";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0051:
        r1 = 235; // 0xeb float:3.3E-43 double:1.16E-321;
        goto L_0x0c4a;
    L_0x0055:
        r1 = "chat_outLoaderPhotoIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x005d:
        r1 = 52;
        goto L_0x0c4a;
    L_0x0061:
        r1 = "chats_nameMessage";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0069:
        r1 = 169; // 0xa9 float:2.37E-43 double:8.35E-322;
        goto L_0x0c4a;
    L_0x006d:
        r1 = "chat_outFileIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0075:
        r1 = 46;
        goto L_0x0c4a;
    L_0x0079:
        r1 = "chat_emojiPanelIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0081:
        r1 = 16;
        goto L_0x0c4a;
    L_0x0085:
        r1 = "chat_outBubble";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x008d:
        r1 = 172; // 0xac float:2.41E-43 double:8.5E-322;
        goto L_0x0c4a;
    L_0x0091:
        r1 = "stickers_menu";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0099:
        r1 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        goto L_0x0c4a;
    L_0x009d:
        r1 = "chat_outLoaderPhoto";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00a5:
        r1 = 45;
        goto L_0x0c4a;
    L_0x00a9:
        r1 = "actionBarActionModeDefaultTop";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00b1:
        r1 = 62;
        goto L_0x0c4a;
    L_0x00b5:
        r1 = "chat_outVenueInfoText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00bd:
        r1 = 257; // 0x101 float:3.6E-43 double:1.27E-321;
        goto L_0x0c4a;
    L_0x00c1:
        r1 = "chat_outReplyMediaMessageText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00c9:
        r1 = 177; // 0xb1 float:2.48E-43 double:8.74E-322;
        goto L_0x0c4a;
    L_0x00cd:
        r1 = "chat_inLoaderPhoto";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00d5:
        r1 = 163; // 0xa3 float:2.28E-43 double:8.05E-322;
        goto L_0x0c4a;
    L_0x00d9:
        r1 = "chat_inTimeSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00e1:
        r1 = 259; // 0x103 float:3.63E-43 double:1.28E-321;
        goto L_0x0c4a;
    L_0x00e5:
        r1 = "progressCircle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00ed:
        r1 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        goto L_0x0c4a;
    L_0x00f1:
        r1 = "chats_unreadCounterMuted";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x00f9:
        r1 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        goto L_0x0c4a;
    L_0x00fd:
        r1 = "avatar_actionBarSelectorPink";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0105:
        r1 = 221; // 0xdd float:3.1E-43 double:1.09E-321;
        goto L_0x0c4a;
    L_0x0109:
        r1 = "avatar_actionBarSelectorCyan";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0111:
        r1 = 157; // 0x9d float:2.2E-43 double:7.76E-322;
        goto L_0x0c4a;
    L_0x0115:
        r1 = "avatar_actionBarSelectorBlue";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x011d:
        r1 = 41;
        goto L_0x0c4a;
    L_0x0121:
        r1 = "chat_messageTextIn";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0129:
        r1 = 44;
        goto L_0x0c4a;
    L_0x012d:
        r1 = "chat_inMenu";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0135:
        r1 = 201; // 0xc9 float:2.82E-43 double:9.93E-322;
        goto L_0x0c4a;
    L_0x0139:
        r1 = "dialogTextBlack";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0141:
        r1 = 137; // 0x89 float:1.92E-43 double:6.77E-322;
        goto L_0x0c4a;
    L_0x0145:
        r1 = "chat_inFileInfoText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x014d:
        r1 = 185; // 0xb9 float:2.59E-43 double:9.14E-322;
        goto L_0x0c4a;
    L_0x0151:
        r1 = "dialogButtonSelector";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0159:
        r1 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        goto L_0x0c4a;
    L_0x015d:
        r1 = "dialogBackgroundGray";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0165:
        r1 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        goto L_0x0c4a;
    L_0x0169:
        r1 = "chat_unreadMessagesStartBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0171:
        r1 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        goto L_0x0c4a;
    L_0x0175:
        r1 = "actionBarActionModeDefaultIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x017d:
        r1 = 237; // 0xed float:3.32E-43 double:1.17E-321;
        goto L_0x0c4a;
    L_0x0181:
        r1 = "switchThumbChecked";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0189:
        r1 = 106; // 0x6a float:1.49E-43 double:5.24E-322;
        goto L_0x0c4a;
    L_0x018d:
        r1 = "avatar_subtitleInProfileRed";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0195:
        r1 = 80;
        goto L_0x0c4a;
    L_0x0199:
        r1 = "divider";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01a1:
        r1 = 39;
        goto L_0x0c4a;
    L_0x01a5:
        r1 = "chat_inVenueInfoSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01ad:
        r1 = 236; // 0xec float:3.31E-43 double:1.166E-321;
        goto L_0x0c4a;
    L_0x01b1:
        r1 = "chats_attachMessage";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01b9:
        r1 = 83;
        goto L_0x0c4a;
    L_0x01bd:
        r1 = "chat_outSentClock";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01c5:
        r1 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        goto L_0x0c4a;
    L_0x01c9:
        r1 = "chat_outSentCheck";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01d1:
        r1 = 251; // 0xfb float:3.52E-43 double:1.24E-321;
        goto L_0x0c4a;
    L_0x01d5:
        r1 = "chat_outViaBotNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01dd:
        r1 = 230; // 0xe6 float:3.22E-43 double:1.136E-321;
        goto L_0x0c4a;
    L_0x01e1:
        r1 = "chat_messageTextOut";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01e9:
        r1 = 58;
        goto L_0x0c4a;
    L_0x01ed:
        r1 = "profile_verifiedCheck";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x01f5:
        r1 = 144; // 0x90 float:2.02E-43 double:7.1E-322;
        goto L_0x0c4a;
    L_0x01f9:
        r1 = "chat_messageLinkIn";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0201:
        r1 = 84;
        goto L_0x0c4a;
    L_0x0205:
        r1 = "chat_inAudioDurationText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x020d:
        r1 = 212; // 0xd4 float:2.97E-43 double:1.047E-321;
        goto L_0x0c4a;
    L_0x0211:
        r1 = "chat_inFileIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0219:
        r1 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        goto L_0x0c4a;
    L_0x021d:
        r1 = "chat_serviceBackgroundSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0225:
        r1 = 47;
        goto L_0x0c4a;
    L_0x0229:
        r1 = "chat_outLoaderPhotoIconSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0231:
        r1 = 191; // 0xbf float:2.68E-43 double:9.44E-322;
        goto L_0x0c4a;
    L_0x0235:
        r1 = "chat_inAudioTitleText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x023d:
        r1 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        goto L_0x0c4a;
    L_0x0241:
        r1 = "chat_outFileSelectedIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0249:
        r1 = 91;
        goto L_0x0c4a;
    L_0x024d:
        r1 = "player_seekBarBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0255:
        r1 = 202; // 0xca float:2.83E-43 double:1.0E-321;
        goto L_0x0c4a;
    L_0x0259:
        r1 = "avatar_backgroundActionBarViolet";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0261:
        r1 = 73;
        goto L_0x0c4a;
    L_0x0265:
        r1 = "player_progressBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x026d:
        r1 = 31;
        goto L_0x0c4a;
    L_0x0271:
        r1 = "chat_outContactPhoneText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0279:
        r1 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        goto L_0x0c4a;
    L_0x027d:
        r1 = "chat_outContactIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0285:
        r1 = 96;
        goto L_0x0c4a;
    L_0x0289:
        r1 = "chat_outSiteNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0291:
        r1 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        goto L_0x0c4a;
    L_0x0295:
        r1 = "chat_messagePanelText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x029d:
        r1 = 253; // 0xfd float:3.55E-43 double:1.25E-321;
        goto L_0x0c4a;
    L_0x02a1:
        r1 = "chat_messagePanelHint";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02a9:
        r1 = 174; // 0xae float:2.44E-43 double:8.6E-322;
        goto L_0x0c4a;
    L_0x02ad:
        r1 = "chat_inContactIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02b5:
        r1 = 57;
        goto L_0x0c4a;
    L_0x02b9:
        r1 = "inappPlayerPerformer";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02c1:
        r1 = 61;
        goto L_0x0c4a;
    L_0x02c5:
        r1 = "chat_outForwardedNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02cd:
        r1 = 29;
        goto L_0x0c4a;
    L_0x02d1:
        r1 = "actionBarDefaultSelector";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02d9:
        r1 = 98;
        goto L_0x0c4a;
    L_0x02dd:
        r1 = "chat_outFileInfoSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02e5:
        r1 = 14;
        goto L_0x0c4a;
    L_0x02e9:
        r1 = "chat_outFileNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02f1:
        r1 = 38;
        goto L_0x0c4a;
    L_0x02f5:
        r1 = "chat_unreadMessagesStartArrowIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x02fd:
        r1 = 53;
        goto L_0x0c4a;
    L_0x0301:
        r1 = "avatar_backgroundActionBarOrange";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0309:
        r1 = 233; // 0xe9 float:3.27E-43 double:1.15E-321;
        goto L_0x0c4a;
    L_0x030d:
        r1 = "player_actionBarTitle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0315:
        r1 = 19;
        goto L_0x0c4a;
    L_0x0319:
        r1 = "chat_inBubble";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0321:
        r1 = 13;
        goto L_0x0c4a;
    L_0x0325:
        r1 = "player_actionBarItems";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x032d:
        r1 = 138; // 0x8a float:1.93E-43 double:6.8E-322;
        goto L_0x0c4a;
    L_0x0331:
        r1 = "player_placeholder";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0339:
        r1 = 68;
        goto L_0x0c4a;
    L_0x033d:
        r1 = "chat_addContact";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0345:
        r1 = 22;
        goto L_0x0c4a;
    L_0x0349:
        r1 = "avatar_backgroundActionBarPink";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0351:
        r1 = 178; // 0xb2 float:2.5E-43 double:8.8E-322;
        goto L_0x0c4a;
    L_0x0355:
        r1 = "avatar_backgroundActionBarCyan";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x035d:
        r1 = 249; // 0xf9 float:3.49E-43 double:1.23E-321;
        goto L_0x0c4a;
    L_0x0361:
        r1 = "avatar_backgroundActionBarBlue";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0369:
        r1 = 155; // 0x9b float:2.17E-43 double:7.66E-322;
        goto L_0x0c4a;
    L_0x036d:
        r1 = "chat_outPreviewInstantSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0375:
        r1 = 12;
        goto L_0x0c4a;
    L_0x0379:
        r1 = "chat_outReplyNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0381:
        r1 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        goto L_0x0c4a;
    L_0x0385:
        r1 = "chat_topPanelBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x038d:
        r1 = 134; // 0x86 float:1.88E-43 double:6.6E-322;
        goto L_0x0c4a;
    L_0x0391:
        r1 = "chat_inAudioProgress";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0399:
        r1 = 207; // 0xcf float:2.9E-43 double:1.023E-321;
        goto L_0x0c4a;
    L_0x039d:
        r1 = "avatar_subtitleInProfileViolet";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03a5:
        r1 = 181; // 0xb5 float:2.54E-43 double:8.94E-322;
        goto L_0x0c4a;
    L_0x03a9:
        r1 = "chat_outLoaderPhotoSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03b1:
        r1 = 220; // 0xdc float:3.08E-43 double:1.087E-321;
        goto L_0x0c4a;
    L_0x03b5:
        r1 = "chat_outAudioSeekbar";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03bd:
        r1 = 192; // 0xc0 float:2.69E-43 double:9.5E-322;
        goto L_0x0c4a;
    L_0x03c1:
        r1 = "groupcreate_spanText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03c9:
        r1 = 245; // 0xf5 float:3.43E-43 double:1.21E-321;
        goto L_0x0c4a;
    L_0x03cd:
        r1 = "chat_inAudioSelectedProgress";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03d5:
        r1 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        goto L_0x0c4a;
    L_0x03d9:
        r1 = "chat_inViewsSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03e1:
        r1 = 141; // 0x8d float:1.98E-43 double:6.97E-322;
        goto L_0x0c4a;
    L_0x03e5:
        r1 = "windowBackgroundWhiteGrayText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03ed:
        r1 = 86;
        goto L_0x0c4a;
    L_0x03f1:
        r1 = "windowBackgroundWhiteGrayIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x03f9:
        r1 = 186; // 0xba float:2.6E-43 double:9.2E-322;
        goto L_0x0c4a;
    L_0x03fd:
        r1 = "chat_inAudioSeekbarSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0405:
        r1 = 167; // 0xa7 float:2.34E-43 double:8.25E-322;
        goto L_0x0c4a;
    L_0x0409:
        r1 = "avatar_backgroundActionBarRed";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0411:
        r1 = 231; // 0xe7 float:3.24E-43 double:1.14E-321;
        goto L_0x0c4a;
    L_0x0415:
        r1 = "chat_inFileSelectedIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x041d:
        r1 = 149; // 0x95 float:2.09E-43 double:7.36E-322;
        goto L_0x0c4a;
    L_0x0421:
        r1 = "avatar_subtitleInProfileOrange";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0429:
        r1 = 261; // 0x105 float:3.66E-43 double:1.29E-321;
        goto L_0x0c4a;
    L_0x042d:
        r1 = "chats_menuCloudBackgroundCats";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0435:
        r1 = 215; // 0xd7 float:3.01E-43 double:1.06E-321;
        goto L_0x0c4a;
    L_0x0439:
        r1 = "windowBackgroundWhiteBlueText4";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0441:
        r1 = 217; // 0xd9 float:3.04E-43 double:1.07E-321;
        goto L_0x0c4a;
    L_0x0445:
        r1 = "chat_outInstant";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x044d:
        r1 = 209; // 0xd1 float:2.93E-43 double:1.033E-321;
        goto L_0x0c4a;
    L_0x0451:
        r1 = "chat_inContactPhoneText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0459:
        r1 = 188; // 0xbc float:2.63E-43 double:9.3E-322;
        goto L_0x0c4a;
    L_0x045d:
        r1 = "chat_inBubbleShadow";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0465:
        r1 = 184; // 0xb8 float:2.58E-43 double:9.1E-322;
        goto L_0x0c4a;
    L_0x0469:
        r1 = "chat_outMenuSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0471:
        r1 = 252; // 0xfc float:3.53E-43 double:1.245E-321;
        goto L_0x0c4a;
    L_0x0475:
        r1 = "chat_outLocationBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x047d:
        r1 = 243; // 0xf3 float:3.4E-43 double:1.2E-321;
        goto L_0x0c4a;
    L_0x0481:
        r1 = "chat_inViews";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0489:
        r1 = 100;
        goto L_0x0c4a;
    L_0x048d:
        r1 = "chat_inPreviewInstantSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0495:
        r1 = 78;
        goto L_0x0c4a;
    L_0x0499:
        r1 = "chat_emojiPanelTrendingDescription";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04a1:
        r1 = 2;
        goto L_0x0c4a;
    L_0x04a4:
        r1 = "chat_messagePanelIcons";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04ac:
        r1 = 70;
        goto L_0x0c4a;
    L_0x04b0:
        r1 = "chat_outReplyLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04b8:
        r1 = 254; // 0xfe float:3.56E-43 double:1.255E-321;
        goto L_0x0c4a;
    L_0x04bc:
        r1 = "chat_outVenueInfoSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04c4:
        r1 = 135; // 0x87 float:1.89E-43 double:6.67E-322;
        goto L_0x0c4a;
    L_0x04c8:
        r1 = "chat_outTimeText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04d0:
        r1 = 258; // 0x102 float:3.62E-43 double:1.275E-321;
        goto L_0x0c4a;
    L_0x04d4:
        r1 = "avatar_backgroundGroupCreateSpanBlue";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04dc:
        r1 = 8;
        goto L_0x0c4a;
    L_0x04e0:
        r1 = "chat_outLocationIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04e8:
        r1 = 81;
        goto L_0x0c4a;
    L_0x04ec:
        r1 = "chat_inFileBackgroundSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x04f4:
        r1 = 234; // 0xea float:3.28E-43 double:1.156E-321;
        goto L_0x0c4a;
    L_0x04f8:
        r1 = "chat_outAudioTitleText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0500:
        r1 = 59;
        goto L_0x0c4a;
    L_0x0504:
        r1 = "chat_inFileProgress";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x050c:
        r1 = 242; // 0xf2 float:3.39E-43 double:1.196E-321;
        goto L_0x0c4a;
    L_0x0510:
        r1 = "chat_emojiPanelBackspace";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0518:
        r1 = 11;
        goto L_0x0c4a;
    L_0x051c:
        r1 = "chat_inVoiceSeekbar";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0524:
        r1 = 34;
        goto L_0x0c4a;
    L_0x0528:
        r1 = "dialogTextLink";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0530:
        r1 = 164; // 0xa4 float:2.3E-43 double:8.1E-322;
        goto L_0x0c4a;
    L_0x0534:
        r1 = "chat_goDownButtonIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x053c:
        r1 = 214; // 0xd6 float:3.0E-43 double:1.057E-321;
        goto L_0x0c4a;
    L_0x0540:
        r1 = "chat_outVoiceSeekbarSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0548:
        r1 = 28;
        goto L_0x0c4a;
    L_0x054c:
        r1 = "inappPlayerBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0554:
        r1 = 48;
        goto L_0x0c4a;
    L_0x0558:
        r1 = "chat_inSentClockSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0560:
        r1 = 183; // 0xb7 float:2.56E-43 double:9.04E-322;
        goto L_0x0c4a;
    L_0x0564:
        r1 = "chat_inVenueInfoText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x056c:
        r1 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        goto L_0x0c4a;
    L_0x0570:
        r1 = "chat_topPanelTitle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0578:
        r1 = 219; // 0xdb float:3.07E-43 double:1.08E-321;
        goto L_0x0c4a;
    L_0x057c:
        r1 = "windowBackgroundWhiteBlueText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0584:
        r1 = 247; // 0xf7 float:3.46E-43 double:1.22E-321;
        goto L_0x0c4a;
    L_0x0588:
        r1 = "chat_topPanelClose";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0590:
        r1 = 250; // 0xfa float:3.5E-43 double:1.235E-321;
        goto L_0x0c4a;
    L_0x0594:
        r1 = "avatar_subtitleInProfileGreen";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x059c:
        r1 = 228; // 0xe4 float:3.2E-43 double:1.126E-321;
        goto L_0x0c4a;
    L_0x05a0:
        r1 = "actionBarDefaultSubmenuItem";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05a8:
        r1 = 198; // 0xc6 float:2.77E-43 double:9.8E-322;
        goto L_0x0c4a;
    L_0x05ac:
        r1 = "avatar_subtitleInProfilePink";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05b4:
        r1 = 0;
        goto L_0x0c4a;
    L_0x05b7:
        r1 = "avatar_subtitleInProfileCyan";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05bf:
        r1 = 63;
        goto L_0x0c4a;
    L_0x05c3:
        r1 = "avatar_subtitleInProfileBlue";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05cb:
        r1 = 239; // 0xef float:3.35E-43 double:1.18E-321;
        goto L_0x0c4a;
    L_0x05cf:
        r1 = "chat_serviceBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05d7:
        r1 = 147; // 0x93 float:2.06E-43 double:7.26E-322;
        goto L_0x0c4a;
    L_0x05db:
        r1 = "chat_emojiPanelIconSelector";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05e3:
        r1 = 74;
        goto L_0x0c4a;
    L_0x05e7:
        r1 = "chat_emojiPanelIconSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05ef:
        r1 = 4;
        goto L_0x0c4a;
    L_0x05f2:
        r1 = "chat_outAudioDurationSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x05fa:
        r1 = 142; // 0x8e float:1.99E-43 double:7.0E-322;
        goto L_0x0c4a;
    L_0x05fe:
        r1 = "chat_inReplyMediaMessageSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0606:
        r1 = 140; // 0x8c float:1.96E-43 double:6.9E-322;
        goto L_0x0c4a;
    L_0x060a:
        r1 = "windowBackgroundWhiteBlackText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0612:
        r1 = 246; // 0xf6 float:3.45E-43 double:1.215E-321;
        goto L_0x0c4a;
    L_0x0616:
        r1 = "chat_outLoaderSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x061e:
        r1 = 15;
        goto L_0x0c4a;
    L_0x0622:
        r1 = "chat_reportSpam";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x062a:
        r1 = 227; // 0xe3 float:3.18E-43 double:1.12E-321;
        goto L_0x0c4a;
    L_0x062e:
        r1 = "chat_inBubbleSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0636:
        r1 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        goto L_0x0c4a;
    L_0x063a:
        r1 = "chat_outAudioDurationText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0642:
        r1 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
        goto L_0x0c4a;
    L_0x0646:
        r1 = "chat_inTimeText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x064e:
        r1 = 7;
        goto L_0x0c4a;
    L_0x0651:
        r1 = "chat_inAudioPerfomerText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0659:
        r1 = 82;
        goto L_0x0c4a;
    L_0x065d:
        r1 = "chat_goDownButton";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0665:
        r1 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        goto L_0x0c4a;
    L_0x0669:
        r1 = "chat_outAudioSelectedProgress";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0671:
        r1 = 182; // 0xb6 float:2.55E-43 double:9.0E-322;
        goto L_0x0c4a;
    L_0x0675:
        r1 = "windowBackgroundWhiteRedText5";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x067e:
        r1 = 194; // 0xc2 float:2.72E-43 double:9.6E-322;
        goto L_0x0c4a;
    L_0x0682:
        r1 = "groupcreate_spanBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x068a:
        r1 = 43;
        goto L_0x0c4a;
    L_0x068e:
        r1 = "chat_outPreviewLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0696:
        r1 = 199; // 0xc7 float:2.79E-43 double:9.83E-322;
        goto L_0x0c4a;
    L_0x069a:
        r1 = "chat_outAudioSeekbarFill";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06a2:
        r1 = 67;
        goto L_0x0c4a;
    L_0x06a6:
        r1 = "chats_sentClock";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06ae:
        r1 = 203; // 0xcb float:2.84E-43 double:1.003E-321;
        goto L_0x0c4a;
    L_0x06b2:
        r1 = "chats_sentCheck";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06ba:
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        goto L_0x0c4a;
    L_0x06be:
        r1 = "chat_replyPanelMessage";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06c6:
        r1 = 75;
        goto L_0x0c4a;
    L_0x06ca:
        r1 = "chat_inPreviewLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06d2:
        r1 = 153; // 0x99 float:2.14E-43 double:7.56E-322;
        goto L_0x0c4a;
    L_0x06d6:
        r1 = "chats_pinnedOverlay";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06de:
        r1 = 95;
        goto L_0x0c4a;
    L_0x06e2:
        r1 = "chat_inLoaderPhotoIconSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06ea:
        r1 = 216; // 0xd8 float:3.03E-43 double:1.067E-321;
        goto L_0x0c4a;
    L_0x06ee:
        r1 = "chat_secretChatStatusText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x06f6:
        r1 = 151; // 0x97 float:2.12E-43 double:7.46E-322;
        goto L_0x0c4a;
    L_0x06fa:
        r1 = "chats_tabletSelectedOverlay";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0702:
        r1 = 36;
        goto L_0x0c4a;
    L_0x0706:
        r1 = "chat_inContactNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x070e:
        r1 = 218; // 0xda float:3.05E-43 double:1.077E-321;
        goto L_0x0c4a;
    L_0x0712:
        r1 = "chats_menuItemText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x071a:
        r1 = 37;
        goto L_0x0c4a;
    L_0x071e:
        r1 = "chats_menuItemIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0726:
        r1 = 6;
        goto L_0x0c4a;
    L_0x0729:
        r1 = "switchTrackChecked";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0731:
        r1 = 146; // 0x92 float:2.05E-43 double:7.2E-322;
        goto L_0x0c4a;
    L_0x0735:
        r1 = "chats_menuTopShadow";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x073d:
        r1 = 136; // 0x88 float:1.9E-43 double:6.7E-322;
        goto L_0x0c4a;
    L_0x0741:
        r1 = "key_chats_menuTopShadow";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0749:
        r1 = 170; // 0xaa float:2.38E-43 double:8.4E-322;
        goto L_0x0c4a;
    L_0x074d:
        r1 = "chat_inFileProgressSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0755:
        r1 = 131; // 0x83 float:1.84E-43 double:6.47E-322;
        goto L_0x0c4a;
    L_0x0759:
        r1 = "chat_replyPanelLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0761:
        r1 = 175; // 0xaf float:2.45E-43 double:8.65E-322;
        goto L_0x0c4a;
    L_0x0765:
        r1 = "chat_unreadMessagesStartText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x076d:
        r1 = 205; // 0xcd float:2.87E-43 double:1.013E-321;
        goto L_0x0c4a;
    L_0x0771:
        r1 = "player_actionBar";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0779:
        r1 = 50;
        goto L_0x0c4a;
    L_0x077d:
        r1 = "inappPlayerTitle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0785:
        r1 = 229; // 0xe5 float:3.21E-43 double:1.13E-321;
        goto L_0x0c4a;
    L_0x0789:
        r1 = "listSelectorSDK21";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0791:
        r1 = 213; // 0xd5 float:2.98E-43 double:1.05E-321;
        goto L_0x0c4a;
    L_0x0795:
        r1 = "chat_outPreviewInstantText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x079d:
        r1 = 76;
        goto L_0x0c4a;
    L_0x07a1:
        r1 = "avatar_nameInMessageOrange";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07a9:
        r1 = 158; // 0x9e float:2.21E-43 double:7.8E-322;
        goto L_0x0c4a;
    L_0x07ad:
        r1 = "chat_outVoiceSeekbarFill";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07b5:
        r1 = 197; // 0xc5 float:2.76E-43 double:9.73E-322;
        goto L_0x0c4a;
    L_0x07b9:
        r1 = "inappPlayerClose";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07c1:
        r1 = 206; // 0xce float:2.89E-43 double:1.02E-321;
        goto L_0x0c4a;
    L_0x07c5:
        r1 = "chat_inReplyNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07cd:
        r1 = 69;
        goto L_0x0c4a;
    L_0x07d1:
        r1 = "windowBackgroundWhite";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07d9:
        r1 = 171; // 0xab float:2.4E-43 double:8.45E-322;
        goto L_0x0c4a;
    L_0x07dd:
        r1 = "chat_outFileInfoText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07e5:
        r1 = 51;
        goto L_0x0c4a;
    L_0x07e9:
        r1 = "chat_selectedBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07f1:
        r1 = 17;
        goto L_0x0c4a;
    L_0x07f5:
        r1 = "chat_inAudioDurationSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x07fd:
        r1 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        goto L_0x0c4a;
    L_0x0801:
        r1 = "chats_actionMessage";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0809:
        r1 = 42;
        goto L_0x0c4a;
    L_0x080d:
        r1 = "avatar_actionBarSelectorRed";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0815:
        r1 = 32;
        goto L_0x0c4a;
    L_0x0819:
        r1 = "actionBarDefault";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0821:
        r1 = 113; // 0x71 float:1.58E-43 double:5.6E-322;
        goto L_0x0c4a;
    L_0x0825:
        r1 = "chat_outSentCheckSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x082d:
        r1 = 89;
        goto L_0x0c4a;
    L_0x0831:
        r1 = "chat_outInstantSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0839:
        r1 = 190; // 0xbe float:2.66E-43 double:9.4E-322;
        goto L_0x0c4a;
    L_0x083d:
        r1 = "chat_outReplyMediaMessageSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0845:
        r1 = 248; // 0xf8 float:3.48E-43 double:1.225E-321;
        goto L_0x0c4a;
    L_0x0849:
        r1 = "chat_inSentClock";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0851:
        r1 = 166; // 0xa6 float:2.33E-43 double:8.2E-322;
        goto L_0x0c4a;
    L_0x0855:
        r1 = "chat_outBubbleSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x085d:
        r1 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        goto L_0x0c4a;
    L_0x0861:
        r1 = "chats_pinnedIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0869:
        r1 = 18;
        goto L_0x0c4a;
    L_0x086d:
        r1 = "chat_outVenueNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0875:
        r1 = 240; // 0xf0 float:3.36E-43 double:1.186E-321;
        goto L_0x0c4a;
    L_0x0879:
        r1 = "chats_message";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0881:
        r1 = 238; // 0xee float:3.34E-43 double:1.176E-321;
        goto L_0x0c4a;
    L_0x0885:
        r1 = "chat_inLoaderPhotoIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x088d:
        r1 = 193; // 0xc1 float:2.7E-43 double:9.54E-322;
        goto L_0x0c4a;
    L_0x0891:
        r1 = "chat_inSiteNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0899:
        r1 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        goto L_0x0c4a;
    L_0x089d:
        r1 = "profile_actionPressedBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08a5:
        r1 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        goto L_0x0c4a;
    L_0x08a9:
        r1 = "chats_name";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08b1:
        r1 = 168; // 0xa8 float:2.35E-43 double:8.3E-322;
        goto L_0x0c4a;
    L_0x08b5:
        r1 = "chats_date";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08bd:
        r1 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        goto L_0x0c4a;
    L_0x08c1:
        r1 = "chat_emojiPanelTrendingTitle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08c9:
        r1 = 77;
        goto L_0x0c4a;
    L_0x08cd:
        r1 = "graySection";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08d5:
        r1 = 71;
        goto L_0x0c4a;
    L_0x08d9:
        r1 = "chats_nameIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08e1:
        r1 = 72;
        goto L_0x0c4a;
    L_0x08e5:
        r1 = "chat_outAudioSeekbarSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08ed:
        r1 = 129; // 0x81 float:1.81E-43 double:6.37E-322;
        goto L_0x0c4a;
    L_0x08f1:
        r1 = "chat_outSentClockSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x08f9:
        r1 = 65;
        goto L_0x0c4a;
    L_0x08fd:
        r1 = "chats_menuPhoneCats";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0905:
        r1 = 226; // 0xe2 float:3.17E-43 double:1.117E-321;
        goto L_0x0c4a;
    L_0x0909:
        r1 = "player_actionBarSubtitle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0911:
        r1 = 223; // 0xdf float:3.12E-43 double:1.1E-321;
        goto L_0x0c4a;
    L_0x0915:
        r1 = "chat_topPanelLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x091d:
        r1 = 49;
        goto L_0x0c4a;
    L_0x0921:
        r1 = "chat_mediaMenu";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0929:
        r1 = 21;
        goto L_0x0c4a;
    L_0x092d:
        r1 = "chats_secretName";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0935:
        r1 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        goto L_0x0c4a;
    L_0x0939:
        r1 = "chats_secretIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0941:
        r1 = 92;
        goto L_0x0c4a;
    L_0x0945:
        r1 = "chat_inAudioSeekbar";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x094d:
        r1 = 187; // 0xbb float:2.62E-43 double:9.24E-322;
        goto L_0x0c4a;
    L_0x0951:
        r1 = "chat_secretTimerBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0959:
        r1 = 1;
        goto L_0x0c4a;
    L_0x095c:
        r1 = "chat_inFileBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0964:
        r1 = 3;
        goto L_0x0c4a;
    L_0x0967:
        r1 = "chat_outReplyMessageText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x096f:
        r1 = 210; // 0xd2 float:2.94E-43 double:1.04E-321;
        goto L_0x0c4a;
    L_0x0973:
        r1 = "profile_actionBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x097b:
        r1 = 64;
        goto L_0x0c4a;
    L_0x097f:
        r1 = "chat_messageLinkOut";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0987:
        r1 = 204; // 0xcc float:2.86E-43 double:1.01E-321;
        goto L_0x0c4a;
    L_0x098b:
        r1 = "windowBackgroundWhiteGreenText2";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0994:
        r1 = 10;
        goto L_0x0c4a;
    L_0x0998:
        r1 = "avatar_backgroundActionBarGreen";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09a0:
        r1 = 143; // 0x8f float:2.0E-43 double:7.07E-322;
        goto L_0x0c4a;
    L_0x09a4:
        r1 = "windowBackgroundWhiteBlueHeader";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09ac:
        r1 = 97;
        goto L_0x0c4a;
    L_0x09b0:
        r1 = "chat_outAudioPerfomerText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09b8:
        r1 = 94;
        goto L_0x0c4a;
    L_0x09bc:
        r1 = "chat_inFileInfoSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09c4:
        r1 = 79;
        goto L_0x0c4a;
    L_0x09c8:
        r1 = "chat_inMenuSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09d0:
        r1 = 56;
        goto L_0x0c4a;
    L_0x09d4:
        r1 = "chats_unreadCounter";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09dc:
        r1 = 85;
        goto L_0x0c4a;
    L_0x09e0:
        r1 = "chat_outBubbleShadow";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09e8:
        r1 = 55;
        goto L_0x0c4a;
    L_0x09ec:
        r1 = "avatar_nameInMessageGreen";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x09f4:
        r1 = 66;
        goto L_0x0c4a;
    L_0x09f8:
        r1 = "chat_muteIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a00:
        r1 = 20;
        goto L_0x0c4a;
    L_0x0a04:
        r1 = "actionBarActionModeDefaultSelector";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a0c:
        r1 = 5;
        goto L_0x0c4a;
    L_0x0a0f:
        r1 = "dialogIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a17:
        r1 = 93;
        goto L_0x0c4a;
    L_0x0a1b:
        r1 = "windowBackgroundGray";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a23:
        r1 = 9;
        goto L_0x0c4a;
    L_0x0a27:
        r1 = "chat_inReplyMediaMessageText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a2f:
        r1 = 176; // 0xb0 float:2.47E-43 double:8.7E-322;
        goto L_0x0c4a;
    L_0x0a33:
        r1 = "files_folderIconBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a3b:
        r1 = 160; // 0xa0 float:2.24E-43 double:7.9E-322;
        goto L_0x0c4a;
    L_0x0a3f:
        r1 = "chat_inReplyLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a47:
        r1 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        goto L_0x0c4a;
    L_0x0a4b:
        r1 = "chat_inReplyMessageText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a53:
        r1 = 107; // 0x6b float:1.5E-43 double:5.3E-322;
        goto L_0x0c4a;
    L_0x0a57:
        r1 = "chat_outFileProgress";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a5f:
        r1 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        goto L_0x0c4a;
    L_0x0a63:
        r1 = "chat_outVoiceSeekbar";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a6b:
        r1 = 133; // 0x85 float:1.86E-43 double:6.57E-322;
        goto L_0x0c4a;
    L_0x0a6f:
        r1 = "chat_outContactNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a77:
        r1 = 222; // 0xde float:3.11E-43 double:1.097E-321;
        goto L_0x0c4a;
    L_0x0a7b:
        r1 = "files_folderIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a83:
        r1 = 139; // 0x8b float:1.95E-43 double:6.87E-322;
        goto L_0x0c4a;
    L_0x0a87:
        r1 = "chat_inLoaderPhotoSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a8f:
        r1 = 60;
        goto L_0x0c4a;
    L_0x0a93:
        r1 = "chat_outAudioProgress";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0a9b:
        r1 = 54;
        goto L_0x0c4a;
    L_0x0a9f:
        r1 = "chat_inForwardedNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0aa7:
        r1 = 165; // 0xa5 float:2.31E-43 double:8.15E-322;
        goto L_0x0c4a;
    L_0x0aab:
        r1 = "chat_wallpaper";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0ab3:
        r1 = 224; // 0xe0 float:3.14E-43 double:1.107E-321;
        goto L_0x0c4a;
    L_0x0ab7:
        r1 = "chat_emojiPanelStickerPackSelector";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0abf:
        r1 = 225; // 0xe1 float:3.15E-43 double:1.11E-321;
        goto L_0x0c4a;
    L_0x0ac3:
        r1 = "chats_menuBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0acb:
        r1 = 173; // 0xad float:2.42E-43 double:8.55E-322;
        goto L_0x0c4a;
    L_0x0acf:
        r1 = "profile_verifiedBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0ad7:
        r1 = 161; // 0xa1 float:2.26E-43 double:7.95E-322;
        goto L_0x0c4a;
    L_0x0adb:
        r1 = "avatar_backgroundInProfileBlue";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0ae3:
        r1 = 189; // 0xbd float:2.65E-43 double:9.34E-322;
        goto L_0x0c4a;
    L_0x0ae7:
        r1 = "chat_outViewsSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0aef:
        r1 = 145; // 0x91 float:2.03E-43 double:7.16E-322;
        goto L_0x0c4a;
    L_0x0af3:
        r1 = "chat_inPreviewInstantText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0afb:
        r1 = 27;
        goto L_0x0c4a;
    L_0x0aff:
        r1 = "emptyListPlaceholder";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b07:
        r1 = 241; // 0xf1 float:3.38E-43 double:1.19E-321;
        goto L_0x0c4a;
    L_0x0b0b:
        r1 = "windowBackgroundWhiteValueText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b14:
        r1 = 232; // 0xe8 float:3.25E-43 double:1.146E-321;
        goto L_0x0c4a;
    L_0x0b18:
        r1 = "chat_inViaBotNameText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b20:
        r1 = 156; // 0x9c float:2.19E-43 double:7.7E-322;
        goto L_0x0c4a;
    L_0x0b24:
        r1 = "chats_muteIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b2c:
        r1 = 244; // 0xf4 float:3.42E-43 double:1.206E-321;
        goto L_0x0c4a;
    L_0x0b30:
        r1 = "chat_topPanelMessage";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b38:
        r1 = 132; // 0x84 float:1.85E-43 double:6.5E-322;
        goto L_0x0c4a;
    L_0x0b3c:
        r1 = "chat_outContactBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b44:
        r1 = 211; // 0xd3 float:2.96E-43 double:1.042E-321;
        goto L_0x0c4a;
    L_0x0b48:
        r1 = "dialogBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b50:
        r1 = 26;
        goto L_0x0c4a;
    L_0x0b54:
        r1 = "chat_emojiPanelShadowLine";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b5c:
        r1 = 25;
        goto L_0x0c4a;
    L_0x0b60:
        r1 = "chat_outTimeSelectedText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b68:
        r1 = 90;
        goto L_0x0c4a;
    L_0x0b6c:
        r1 = "avatar_actionBarSelectorGreen";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b74:
        r1 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        goto L_0x0c4a;
    L_0x0b78:
        r1 = "chat_outFileBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b80:
        r1 = 162; // 0xa2 float:2.27E-43 double:8.0E-322;
        goto L_0x0c4a;
    L_0x0b84:
        r1 = "actionBarDefaultSubtitle";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b8c:
        r1 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        goto L_0x0c4a;
    L_0x0b90:
        r1 = "windowBackgroundWhiteGrayText4";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0b99:
        r1 = 159; // 0x9f float:2.23E-43 double:7.86E-322;
        goto L_0x0c4a;
    L_0x0b9d:
        r1 = "windowBackgroundWhiteGrayText3";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0ba6:
        r1 = 87;
        goto L_0x0c4a;
    L_0x0baa:
        r1 = "windowBackgroundWhiteGrayText2";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bb2:
        r1 = 148; // 0x94 float:2.07E-43 double:7.3E-322;
        goto L_0x0c4a;
    L_0x0bb6:
        r1 = "chat_outFileBackgroundSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bbe:
        r1 = 208; // 0xd0 float:2.91E-43 double:1.03E-321;
        goto L_0x0c4a;
    L_0x0bc2:
        r1 = "chats_menuPhone";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bca:
        r1 = 196; // 0xc4 float:2.75E-43 double:9.7E-322;
        goto L_0x0c4a;
    L_0x0bce:
        r1 = "chat_outLoader";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bd6:
        r1 = 179; // 0xb3 float:2.51E-43 double:8.84E-322;
        goto L_0x0c4a;
    L_0x0bda:
        r1 = "chat_outFileProgressSelected";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0be2:
        r1 = 30;
        goto L_0x0c4a;
    L_0x0be6:
        r1 = "player_button";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bee:
        r1 = 33;
        goto L_0x0c4a;
    L_0x0bf1:
        r1 = "avatar_actionBarSelectorViolet";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bf9:
        r1 = 195; // 0xc3 float:2.73E-43 double:9.63E-322;
        goto L_0x0c4a;
    L_0x0bfc:
        r1 = "actionBarDefaultSubmenuBackground";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = 88;
        goto L_0x0c4a;
    L_0x0CLASSNAME:
        r1 = "listSelector";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c0f:
        r1 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        goto L_0x0c4a;
    L_0x0CLASSNAME:
        r1 = "profile_actionIcon";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c1a:
        r1 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        goto L_0x0c4a;
    L_0x0c1d:
        r1 = "actionBarActionModeDefault";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = 24;
        goto L_0x0c4a;
    L_0x0CLASSNAME:
        r1 = "chat_outViews";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = 40;
        goto L_0x0c4a;
    L_0x0CLASSNAME:
        r1 = "chat_emojiPanelEmptyText";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c3b:
        r1 = 99;
        goto L_0x0c4a;
    L_0x0c3e:
        r1 = "chat_outMenu";
        r1 = r0.equals(r1);
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = 23;
        goto L_0x0c4a;
    L_0x0CLASSNAME:
        r1 = -1;
    L_0x0c4a:
        r3 = -9342607; // 0xfffffffffvar_ float:-3.2093297E38 double:NaN;
        r4 = -4792321; // 0xffffffffffb6dfff float:NaN double:NaN;
        r5 = -11099173; // 0xfffffffffvar_a3db float:-2.8530558E38 double:NaN;
        r6 = -8812393; // 0xfffffffffvar_ float:-3.3168699E38 double:NaN;
        r7 = -328966; // 0xfffffffffffafafa float:NaN double:NaN;
        r8 = -14339006; // 0xfffffffffvar_ float:-2.1959396E38 double:NaN;
        r9 = -9263664; // 0xfffffffffvar_a5d0 float:-3.2253412E38 double:NaN;
        r10 = -11164965; // 0xfffffffffvar_a2db float:-2.8397116E38 double:NaN;
        r11 = -10653824; // 0xffffffffff5d6var_ float:-2.9433833E38 double:NaN;
        r12 = -5648402; // 0xffffffffffa9cfee float:NaN double:NaN;
        r13 = -11972268; // 0xfffffffffvar_ float:-2.675971E38 double:NaN;
        r14 = -7697782; // 0xffffffffff8a8a8a float:NaN double:NaN;
        r15 = -14605274; // 0xfffffffffvar_ float:-2.141934E38 double:NaN;
        r16 = -3019777; // 0xffffffffffd1ebff float:NaN double:NaN;
        switch(r1) {
            case 0: goto L_0x0var_;
            case 1: goto L_0x0var_;
            case 2: goto L_0x0var_;
            case 3: goto L_0x0var_;
            case 4: goto L_0x0f7c;
            case 5: goto L_0x0var_;
            case 6: goto L_0x0var_;
            case 7: goto L_0x0var_;
            case 8: goto L_0x0f6c;
            case 9: goto L_0x0var_;
            case 10: goto L_0x0var_;
            case 11: goto L_0x0var_;
            case 12: goto L_0x0f5f;
            case 13: goto L_0x0f5e;
            case 14: goto L_0x0f5d;
            case 15: goto L_0x0f5d;
            case 16: goto L_0x0f5c;
            case 17: goto L_0x0var_;
            case 18: goto L_0x0var_;
            case 19: goto L_0x0var_;
            case 20: goto L_0x0f4c;
            case 21: goto L_0x0f4b;
            case 22: goto L_0x0var_;
            case 23: goto L_0x0var_;
            case 24: goto L_0x0var_;
            case 25: goto L_0x0f3e;
            case 26: goto L_0x0f3d;
            case 27: goto L_0x0f3c;
            case 28: goto L_0x0var_;
            case 29: goto L_0x0var_;
            case 30: goto L_0x0var_;
            case 31: goto L_0x0var_;
            case 32: goto L_0x0var_;
            case 33: goto L_0x0f2e;
            case 34: goto L_0x0f2d;
            case 35: goto L_0x0var_;
            case 36: goto L_0x0var_;
            case 37: goto L_0x0var_;
            case 38: goto L_0x0f1d;
            case 39: goto L_0x0var_;
            case 40: goto L_0x0var_;
            case 41: goto L_0x0var_;
            case 42: goto L_0x0f0d;
            case 43: goto L_0x0var_;
            case 44: goto L_0x0var_;
            case 45: goto L_0x0var_;
            case 46: goto L_0x0var_;
            case 47: goto L_0x0efc;
            case 48: goto L_0x0ef8;
            case 49: goto L_0x0ef4;
            case 50: goto L_0x0ef0;
            case 51: goto L_0x0eec;
            case 52: goto L_0x0eeb;
            case 53: goto L_0x0ee7;
            case 54: goto L_0x0ee3;
            case 55: goto L_0x0ee0;
            case 56: goto L_0x0edc;
            case 57: goto L_0x0ed8;
            case 58: goto L_0x0ed7;
            case 59: goto L_0x0ed6;
            case 60: goto L_0x0ed2;
            case 61: goto L_0x0ed1;
            case 62: goto L_0x0ece;
            case 63: goto L_0x0ecd;
            case 64: goto L_0x0ec9;
            case 65: goto L_0x0ec8;
            case 66: goto L_0x0ec4;
            case 67: goto L_0x0ec0;
            case 68: goto L_0x0ebc;
            case 69: goto L_0x0ebb;
            case 70: goto L_0x0eb7;
            case 71: goto L_0x0eb3;
            case 72: goto L_0x0eaf;
            case 73: goto L_0x0eae;
            case 74: goto L_0x0eaa;
            case 75: goto L_0x0ea6;
            case 76: goto L_0x0ea5;
            case 77: goto L_0x0ea1;
            case 78: goto L_0x0e9d;
            case 79: goto L_0x0e9c;
            case 80: goto L_0x0e9b;
            case 81: goto L_0x0e97;
            case 82: goto L_0x0e96;
            case 83: goto L_0x0e92;
            case 84: goto L_0x0e91;
            case 85: goto L_0x0e8d;
            case 86: goto L_0x0e89;
            case 87: goto L_0x0e85;
            case 88: goto L_0x0e81;
            case 89: goto L_0x0e80;
            case 90: goto L_0x0e80;
            case 91: goto L_0x0e7c;
            case 92: goto L_0x0e78;
            case 93: goto L_0x0e74;
            case 94: goto L_0x0e70;
            case 95: goto L_0x0e6c;
            case 96: goto L_0x0e68;
            case 97: goto L_0x0e64;
            case 98: goto L_0x0e63;
            case 99: goto L_0x0e5f;
            case 100: goto L_0x0e5b;
            case 101: goto L_0x0e58;
            case 102: goto L_0x0e54;
            case 103: goto L_0x0e50;
            case 104: goto L_0x0e4c;
            case 105: goto L_0x0e48;
            case 106: goto L_0x0e44;
            case 107: goto L_0x0e43;
            case 108: goto L_0x0e42;
            case 109: goto L_0x0e3e;
            case 110: goto L_0x0e3d;
            case 111: goto L_0x0e3c;
            case 112: goto L_0x0e38;
            case 113: goto L_0x0e34;
            case 114: goto L_0x0e30;
            case 115: goto L_0x0e2c;
            case 116: goto L_0x0e28;
            case 117: goto L_0x0e27;
            case 118: goto L_0x0e26;
            case 119: goto L_0x0e25;
            case 120: goto L_0x0e21;
            case 121: goto L_0x0e20;
            case 122: goto L_0x0e1c;
            case 123: goto L_0x0e18;
            case 124: goto L_0x0e17;
            case 125: goto L_0x0e13;
            case 126: goto L_0x0e0f;
            case 127: goto L_0x0e0b;
            case 128: goto L_0x0e07;
            case 129: goto L_0x0e06;
            case 130: goto L_0x0e05;
            case 131: goto L_0x0e01;
            case 132: goto L_0x0dfd;
            case 133: goto L_0x0dfc;
            case 134: goto L_0x0df8;
            case 135: goto L_0x0df7;
            case 136: goto L_0x0df3;
            case 137: goto L_0x0def;
            case 138: goto L_0x0dee;
            case 139: goto L_0x0dea;
            case 140: goto L_0x0de6;
            case 141: goto L_0x0de5;
            case 142: goto L_0x0de4;
            case 143: goto L_0x0de3;
            case 144: goto L_0x0de2;
            case 145: goto L_0x0de2;
            case 146: goto L_0x0dde;
            case 147: goto L_0x0dda;
            case 148: goto L_0x0dd6;
            case 149: goto L_0x0dd2;
            case 150: goto L_0x0dd1;
            case 151: goto L_0x0dcd;
            case 152: goto L_0x0dc9;
            case 153: goto L_0x0dc5;
            case 154: goto L_0x0dc4;
            case 155: goto L_0x0dc3;
            case 156: goto L_0x0dc2;
            case 157: goto L_0x0dc1;
            case 158: goto L_0x0dbd;
            case 159: goto L_0x0db9;
            case 160: goto L_0x0db5;
            case 161: goto L_0x0db1;
            case 162: goto L_0x0db0;
            case 163: goto L_0x0dac;
            case 164: goto L_0x0da8;
            case 165: goto L_0x0da7;
            case 166: goto L_0x0da6;
            case 167: goto L_0x0da5;
            case 168: goto L_0x0da1;
            case 169: goto L_0x0d9d;
            case 170: goto L_0x0d99;
            case 171: goto L_0x0d95;
            case 172: goto L_0x0d91;
            case 173: goto L_0x0d8d;
            case 174: goto L_0x0d89;
            case 175: goto L_0x0d85;
            case 176: goto L_0x0d84;
            case 177: goto L_0x0d83;
            case 178: goto L_0x0d82;
            case 179: goto L_0x0d7e;
            case 180: goto L_0x0d7d;
            case 181: goto L_0x0d7c;
            case 182: goto L_0x0d78;
            case 183: goto L_0x0d74;
            case 184: goto L_0x0d71;
            case 185: goto L_0x0d6d;
            case 186: goto L_0x0d69;
            case 187: goto L_0x0d65;
            case 188: goto L_0x0d64;
            case 189: goto L_0x0d60;
            case 190: goto L_0x0d5f;
            case 191: goto L_0x0d5f;
            case 192: goto L_0x0d5b;
            case 193: goto L_0x0d57;
            case 194: goto L_0x0d53;
            case 195: goto L_0x0d52;
            case 196: goto L_0x0d4e;
            case 197: goto L_0x0d4a;
            case 198: goto L_0x0d46;
            case 199: goto L_0x0d45;
            case 200: goto L_0x0d41;
            case 201: goto L_0x0d3d;
            case 202: goto L_0x0d39;
            case 203: goto L_0x0d35;
            case 204: goto L_0x0d31;
            case 205: goto L_0x0d2d;
            case 206: goto L_0x0d29;
            case 207: goto L_0x0d25;
            case 208: goto L_0x0d24;
            case 209: goto L_0x0d23;
            case 210: goto L_0x0d22;
            case 211: goto L_0x0d1e;
            case 212: goto L_0x0d1a;
            case 213: goto L_0x0d16;
            case 214: goto L_0x0d12;
            case 215: goto L_0x0d0e;
            case 216: goto L_0x0d0d;
            case 217: goto L_0x0d09;
            case 218: goto L_0x0d08;
            case 219: goto L_0x0d04;
            case 220: goto L_0x0d00;
            case 221: goto L_0x0cff;
            case 222: goto L_0x0cfe;
            case 223: goto L_0x0cfa;
            case 224: goto L_0x0cf6;
            case 225: goto L_0x0cf2;
            case 226: goto L_0x0cee;
            case 227: goto L_0x0cea;
            case 228: goto L_0x0ce9;
            case 229: goto L_0x0ce5;
            case 230: goto L_0x0ce4;
            case 231: goto L_0x0ce3;
            case 232: goto L_0x0cdf;
            case 233: goto L_0x0cde;
            case 234: goto L_0x0cdd;
            case 235: goto L_0x0cdc;
            case 236: goto L_0x0cdb;
            case 237: goto L_0x0cda;
            case 238: goto L_0x0cd6;
            case 239: goto L_0x0cd5;
            case 240: goto L_0x0cd4;
            case 241: goto L_0x0cd0;
            case 242: goto L_0x0ccf;
            case 243: goto L_0x0ccb;
            case 244: goto L_0x0cc7;
            case 245: goto L_0x0cc3;
            case 246: goto L_0x0cbf;
            case 247: goto L_0x0cbb;
            case 248: goto L_0x0cba;
            case 249: goto L_0x0cb9;
            case 250: goto L_0x0cb5;
            case 251: goto L_0x0cb1;
            case 252: goto L_0x0cb0;
            case 253: goto L_0x0cac;
            case 254: goto L_0x0cab;
            case 255: goto L_0x0ca7;
            case 256: goto L_0x0ca3;
            case 257: goto L_0x0ca2;
            case 258: goto L_0x0c9e;
            case 259: goto L_0x0c9a;
            case 260: goto L_0x0CLASSNAME;
            case 261: goto L_0x0CLASSNAME;
            default: goto L_0x0CLASSNAME;
        };
    L_0x0CLASSNAME:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "returning color for key ";
        r1.append(r2);
        r1.append(r0);
        r2 = " from current theme";
        r1.append(r2);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.w(r1);
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r17);
        return r0;
    L_0x0CLASSNAME:
        return r14;
    L_0x0CLASSNAME:
        r0 = -13948117; // 0xffffffffff2b2b2b float:-2.2752213E38 double:NaN;
        return r0;
    L_0x0c9a:
        r0 = -5582866; // 0xffffffffffaacfee float:NaN double:NaN;
        return r0;
    L_0x0c9e:
        r0 = -NUM; // 0xffffffffd6a8cfee float:-9.2805502E13 double:NaN;
        return r0;
    L_0x0ca2:
        return r4;
    L_0x0ca3:
        r0 = NUM; // 0x14ffffff float:2.5849393E-26 double:1.740699667E-315;
        return r0;
    L_0x0ca7:
        r0 = -11840163; // 0xffffffffff4b555d float:-2.7027651E38 double:NaN;
        return r0;
    L_0x0cab:
        return r16;
    L_0x0cac:
        r0 = -1118482; // 0xffffffffffeeeeee float:NaN double:NaN;
        return r0;
    L_0x0cb0:
        return r2;
    L_0x0cb1:
        r0 = -6831126; // 0xfffffffffvar_c3ea float:NaN double:NaN;
        return r0;
    L_0x0cb5:
        r0 = -11184811; // 0xfffffffffvar_ float:-2.8356863E38 double:NaN;
        return r0;
    L_0x0cb9:
        return r15;
    L_0x0cba:
        return r2;
    L_0x0cbb:
        r0 = -12413479; // 0xfffffffffvar_d9 float:-2.5864828E38 double:NaN;
        return r0;
    L_0x0cbf:
        r0 = -855310; // 0xfffffffffff2f2f2 float:NaN double:NaN;
        return r0;
    L_0x0cc3:
        r0 = -657931; // 0xfffffffffff5f5f5 float:NaN double:NaN;
        return r0;
    L_0x0cc7:
        r0 = -10790053; // 0xffffffffff5b5b5b float:-2.9157528E38 double:NaN;
        return r0;
    L_0x0ccb:
        r0 = -6234891; // 0xffffffffffa0dcf5 float:NaN double:NaN;
        return r0;
    L_0x0ccf:
        return r11;
    L_0x0cd0:
        r0 = -11447983; // 0xfffffffffvar_ float:-2.7823087E38 double:NaN;
        return r0;
    L_0x0cd4:
        return r16;
    L_0x0cd5:
        return r14;
    L_0x0cd6:
        r0 = -9934744; // 0xfffffffffvar_ float:-3.08923E38 double:NaN;
        return r0;
    L_0x0cda:
        return r2;
    L_0x0cdb:
        return r12;
    L_0x0cdc:
        return r13;
    L_0x0cdd:
        return r2;
    L_0x0cde:
        return r15;
    L_0x0cdf:
        r0 = -12214815; // 0xfffffffffvar_de1 float:-2.6267767E38 double:NaN;
        return r0;
    L_0x0ce3:
        return r15;
    L_0x0ce4:
        return r16;
    L_0x0ce5:
        r0 = -6513508; // 0xffffffffff9c9c9c float:NaN double:NaN;
        return r0;
    L_0x0ce9:
        return r14;
    L_0x0cea:
        r0 = -1481631; // 0xffffffffffe96461 float:NaN double:NaN;
        return r0;
    L_0x0cee:
        r0 = -7434610; // 0xffffffffff8e8e8e float:NaN double:NaN;
        return r0;
    L_0x0cf2:
        r0 = NUM; // 0xcfafeff float:3.867207E-31 double:1.075955764E-315;
        return r0;
    L_0x0cf6:
        r0 = -15526377; // 0xfffffffffvar_ float:-1.9551121E38 double:NaN;
        return r0;
    L_0x0cfa:
        r0 = -10526881; // 0xffffffffff5f5f5f float:-2.9691304E38 double:NaN;
        return r0;
    L_0x0cfe:
        return r16;
    L_0x0cff:
        return r13;
    L_0x0d00:
        r0 = -13208924; // 0xfffffffffvar_a4 float:-2.4251474E38 double:NaN;
        return r0;
    L_0x0d04:
        r0 = -11164709; // 0xfffffffffvar_a3db float:-2.8397635E38 double:NaN;
        return r0;
    L_0x0d08:
        return r5;
    L_0x0d09:
        r0 = -11890739; // 0xffffffffff4a8fcd float:-2.6925071E38 double:NaN;
        return r0;
    L_0x0d0d:
        return r12;
    L_0x0d0e:
        r0 = -11232035; // 0xfffffffffvar_cdd float:-2.8261082E38 double:NaN;
        return r0;
    L_0x0d12:
        r0 = -1776412; // 0xffffffffffe4e4e4 float:NaN double:NaN;
        return r0;
    L_0x0d16:
        r0 = NUM; // 0x11ffffff float:4.0389676E-28 double:1.492028286E-315;
        return r0;
    L_0x0d1a:
        r0 = -8746857; // 0xffffffffff7a8897 float:-3.3301621E38 double:NaN;
        return r0;
    L_0x0d1e:
        r0 = -10910270; // 0xfffffffffvar_c2 float:-2.8913699E38 double:NaN;
        return r0;
    L_0x0d22:
        return r2;
    L_0x0d23:
        return r4;
    L_0x0d24:
        return r2;
    L_0x0d25:
        r0 = -14338750; // 0xfffffffffvar_ float:-2.1959915E38 double:NaN;
        return r0;
    L_0x0d29:
        r0 = -10987432; // 0xfffffffffvar_ float:-2.8757195E38 double:NaN;
        return r0;
    L_0x0d2d:
        r0 = -NUM; // 0xffffffffdaffffff float:-3.60287949E16 double:NaN;
        return r0;
    L_0x0d31:
        r0 = -4792577; // 0xffffffffffb6deff float:NaN double:NaN;
        return r0;
    L_0x0d35:
        r0 = -10452291; // 0xfffffffffvar_bd float:-2.984259E38 double:NaN;
        return r0;
    L_0x0d39:
        r0 = NUM; // 0x47525252 float:53842.32 double:5.91187767E-315;
        return r0;
    L_0x0d3d:
        r0 = NUM; // 0x795c6var_ float:7.1535425E34 double:1.0059675516E-314;
        return r0;
    L_0x0d41:
        r0 = -10574624; // 0xffffffffff5ea4e0 float:-2.959447E38 double:NaN;
        return r0;
    L_0x0d45:
        return r16;
    L_0x0d46:
        r0 = -657931; // 0xfffffffffff5f5f5 float:NaN double:NaN;
        return r0;
    L_0x0d4a:
        r0 = -3874313; // 0xffffffffffc4e1f7 float:NaN double:NaN;
        return r0;
    L_0x0d4e:
        r0 = NUM; // 0x60ffffff float:1.4757394E20 double:8.04037467E-315;
        return r0;
    L_0x0d52:
        return r13;
    L_0x0d53:
        r0 = -45994; // 0xffffffffffff4CLASSNAME float:NaN double:NaN;
        return r0;
    L_0x0d57:
        r0 = -10915968; // 0xfffffffffvar_var_ float:-2.8902142E38 double:NaN;
        return r0;
    L_0x0d5b:
        r0 = -NUM; // 0xfffffffvar_a5d0 float:-1.9600926E-25 double:NaN;
        return r0;
    L_0x0d5f:
        return r2;
    L_0x0d60:
        r0 = -11232035; // 0xfffffffffvar_cdd float:-2.8261082E38 double:NaN;
        return r0;
    L_0x0d64:
        return r6;
    L_0x0d65:
        r0 = -11443856; // 0xfffffffffvar_ float:-2.7831458E38 double:NaN;
        return r0;
    L_0x0d69:
        r0 = -8224126; // 0xfffffffffvar_ float:NaN double:NaN;
        return r0;
    L_0x0d6d:
        r0 = -8812137; // 0xfffffffffvar_ float:-3.3169218E38 double:NaN;
        return r0;
    L_0x0d71:
        r0 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        return r0;
    L_0x0d74:
        r0 = -5648146; // 0xffffffffffa9d0ee float:NaN double:NaN;
        return r0;
    L_0x0d78:
        r0 = -14187829; // 0xfffffffffvar_cb float:-2.226602E38 double:NaN;
        return r0;
    L_0x0d7c:
        return r14;
    L_0x0d7d:
        return r16;
    L_0x0d7e:
        r0 = -7421976; // 0xffffffffff8ebfe8 float:NaN double:NaN;
        return r0;
    L_0x0d82:
        return r15;
    L_0x0d83:
        return r16;
    L_0x0d84:
        return r6;
    L_0x0d85:
        r0 = -14869219; // 0xffffffffff1d1d1d float:-2.0883996E38 double:NaN;
        return r0;
    L_0x0d89:
        r0 = -11776948; // 0xffffffffff4c4c4c float:-2.7155867E38 double:NaN;
        return r0;
    L_0x0d8d:
        r0 = -14868445; // 0xffffffffff1d2023 float:-2.0885566E38 double:NaN;
        return r0;
    L_0x0d91:
        r0 = -13077852; // 0xfffffffffvar_a4 float:-2.451732E38 double:NaN;
        return r0;
    L_0x0d95:
        r0 = -15263719; // 0xfffffffffvar_ float:-2.0083855E38 double:NaN;
        return r0;
    L_0x0d99:
        r0 = 789516; // 0xc0c0c float:1.106348E-39 double:3.900727E-318;
        return r0;
    L_0x0d9d:
        r0 = -11696202; // 0xffffffffff4d87b6 float:-2.731964E38 double:NaN;
        return r0;
    L_0x0da1:
        r0 = -1644826; // 0xffffffffffe6e6e6 float:NaN double:NaN;
        return r0;
    L_0x0da5:
        return r12;
    L_0x0da6:
        return r11;
    L_0x0da7:
        return r10;
    L_0x0da8:
        r0 = -13007663; // 0xfffffffffvar_d1 float:-2.465968E38 double:NaN;
        return r0;
    L_0x0dac:
        r0 = -14404542; // 0xfffffffffvar_ float:-2.1826473E38 double:NaN;
        return r0;
    L_0x0db0:
        return r9;
    L_0x0db1:
        r0 = -11416584; // 0xfffffffffvar_cbf8 float:-2.7886772E38 double:NaN;
        return r0;
    L_0x0db5:
        r0 = -13619152; // 0xfffffffffvar_ float:-2.3419433E38 double:NaN;
        return r0;
    L_0x0db9:
        r0 = -9539986; // 0xffffffffff6e6e6e float:-3.1692965E38 double:NaN;
        return r0;
    L_0x0dbd:
        r0 = -2324391; // 0xffffffffffdCLASSNAME float:NaN double:NaN;
        return r0;
    L_0x0dc1:
        return r13;
    L_0x0dc2:
        return r10;
    L_0x0dc3:
        return r15;
    L_0x0dc4:
        return r8;
    L_0x0dc5:
        r0 = -11230501; // 0xfffffffffvar_a2db float:-2.8264193E38 double:NaN;
        return r0;
    L_0x0dc9:
        r0 = -14474461; // 0xfffffffffvar_ float:-2.168466E38 double:NaN;
        return r0;
    L_0x0dcd:
        r0 = -9934744; // 0xfffffffffvar_ float:-3.08923E38 double:NaN;
        return r0;
    L_0x0dd1:
        return r2;
    L_0x0dd2:
        r0 = -15056797; // 0xffffffffff1a4063 float:-2.0503543E38 double:NaN;
        return r0;
    L_0x0dd6:
        r0 = -8816263; // 0xfffffffffvar_ float:-3.316085E38 double:NaN;
        return r0;
    L_0x0dda:
        r0 = NUM; // 0x6628323d float:1.9857108E23 double:8.467842156E-315;
        return r0;
    L_0x0dde:
        r0 = -15316366; // 0xfffffffffvar_a72 float:-1.9977074E38 double:NaN;
        return r0;
    L_0x0de2:
        return r2;
    L_0x0de3:
        return r15;
    L_0x0de4:
        return r2;
    L_0x0de5:
        return r12;
    L_0x0de6:
        r0 = -9590561; // 0xffffffffff6da8df float:-3.1590386E38 double:NaN;
        return r0;
    L_0x0dea:
        r0 = -5855578; // 0xffffffffffa6a6a6 float:NaN double:NaN;
        return r0;
    L_0x0dee:
        return r2;
    L_0x0def:
        r0 = -394759; // 0xfffffffffff9f9f9 float:NaN double:NaN;
        return r0;
    L_0x0df3:
        r0 = -15724528; // 0xfffffffffvar_ float:-1.9149223E38 double:NaN;
        return r0;
    L_0x0df7:
        return r2;
    L_0x0df8:
        r0 = -98821092; // 0xfffffffffa1c1c1c float:-2.026421E35 double:NaN;
        return r0;
    L_0x0dfc:
        return r9;
    L_0x0dfd:
        r0 = -9803158; // 0xffffffffff6a6a6a float:-3.1159188E38 double:NaN;
        return r0;
    L_0x0e01:
        r0 = -5845010; // 0xffffffffffa6cfee float:NaN double:NaN;
        return r0;
    L_0x0e05:
        return r10;
    L_0x0e06:
        return r2;
    L_0x0e07:
        r0 = -11710381; // 0xffffffffff4d5053 float:-2.729088E38 double:NaN;
        return r0;
    L_0x0e0b:
        r0 = -12303292; // 0xfffffffffvar_ float:-2.6088314E38 double:NaN;
        return r0;
    L_0x0e0f:
        r0 = -13221820; // 0xfffffffffvar_ float:-2.4225318E38 double:NaN;
        return r0;
    L_0x0e13:
        r0 = -13859893; // 0xffffffffff2CLASSNAMEcb float:-2.2931152E38 double:NaN;
        return r0;
    L_0x0e17:
        return r9;
    L_0x0e18:
        r0 = -10592674; // 0xffffffffff5e5e5e float:-2.955786E38 double:NaN;
        return r0;
    L_0x0e1c:
        r0 = -14925725; // 0xffffffffff1CLASSNAME float:-2.0769388E38 double:NaN;
        return r0;
    L_0x0e20:
        return r16;
    L_0x0e21:
        r0 = -12741934; // 0xffffffffff3d92d2 float:-2.5198643E38 double:NaN;
        return r0;
    L_0x0e25:
        return r16;
    L_0x0e26:
        return r11;
    L_0x0e27:
        return r4;
    L_0x0e28:
        r0 = -11972524; // 0xfffffffffvar_ float:-2.6759191E38 double:NaN;
        return r0;
    L_0x0e2c:
        r0 = -14925469; // 0xffffffffff1CLASSNAME float:-2.0769907E38 double:NaN;
        return r0;
    L_0x0e30:
        r0 = -11711155; // 0xffffffffff4d4d4d float:-2.728931E38 double:NaN;
        return r0;
    L_0x0e34:
        r0 = -14407896; // 0xfffffffffvar_ float:-2.181967E38 double:NaN;
        return r0;
    L_0x0e38:
        r0 = -8211748; // 0xfffffffffvar_b2dc float:NaN double:NaN;
        return r0;
    L_0x0e3c:
        return r12;
    L_0x0e3d:
        return r5;
    L_0x0e3e:
        r0 = -14470078; // 0xfffffffffvar_ float:-2.169355E38 double:NaN;
        return r0;
    L_0x0e42:
        return r13;
    L_0x0e43:
        return r2;
    L_0x0e44:
        r0 = -13600600; // 0xfffffffffvar_a8 float:-2.3457061E38 double:NaN;
        return r0;
    L_0x0e48:
        r0 = -7368817; // 0xffffffffff8f8f8f float:NaN double:NaN;
        return r0;
    L_0x0e4c:
        r0 = -11230501; // 0xfffffffffvar_a2db float:-2.8264193E38 double:NaN;
        return r0;
    L_0x0e50:
        r0 = -9316522; // 0xfffffffffvar_d756 float:-3.2146204E38 double:NaN;
        return r0;
    L_0x0e54:
        r0 = -14803426; // 0xffffffffff1e1e1e float:-2.101744E38 double:NaN;
        return r0;
    L_0x0e58:
        r0 = NUM; // 0x2e000000 float:2.910383E-11 double:3.812961187E-315;
        return r0;
    L_0x0e5b:
        r0 = -8812137; // 0xfffffffffvar_ float:-3.3169218E38 double:NaN;
        return r0;
    L_0x0e5f:
        r0 = -10658467; // 0xffffffffff5d5d5d float:-2.9424416E38 double:NaN;
        return r0;
    L_0x0e63:
        return r13;
    L_0x0e64:
        r0 = -9851917; // 0xfffffffffvar_abf3 float:-3.1060293E38 double:NaN;
        return r0;
    L_0x0e68:
        r0 = -5452289; // 0xffffffffffaccdff float:NaN double:NaN;
        return r0;
    L_0x0e6c:
        r0 = NUM; // 0x9ffffff float:6.1629755E-33 double:8.289046E-316;
        return r0;
    L_0x0e70:
        r0 = -7028510; // 0xfffffffffvar_c0e2 float:NaN double:NaN;
        return r0;
    L_0x0e74:
        r0 = -8747891; // 0xffffffffff7a848d float:-3.3299524E38 double:NaN;
        return r0;
    L_0x0e78:
        r0 = -9316522; // 0xfffffffffvar_d756 float:-3.2146204E38 double:NaN;
        return r0;
    L_0x0e7c:
        r0 = -13925429; // 0xffffffffff2b83cb float:-2.279823E38 double:NaN;
        return r0;
    L_0x0e80:
        return r2;
    L_0x0e81:
        r0 = -81911774; // 0xfffffffffb1e2022 float:-8.210346E35 double:NaN;
        return r0;
    L_0x0e85:
        r0 = -9408400; // 0xfffffffffvar_ float:-3.1959853E38 double:NaN;
        return r0;
    L_0x0e89:
        r0 = -10132123; // 0xfffffffffvar_ float:-3.0491968E38 double:NaN;
        return r0;
    L_0x0e8d:
        r0 = -14183202; // 0xfffffffffvar_de float:-2.2275404E38 double:NaN;
        return r0;
    L_0x0e91:
        return r5;
    L_0x0e92:
        r0 = -11234874; // 0xfffffffffvar_c6 float:-2.8255323E38 double:NaN;
        return r0;
    L_0x0e96:
        return r6;
    L_0x0e97:
        r0 = -10052929; // 0xfffffffffvar_abf float:-3.0652593E38 double:NaN;
        return r0;
    L_0x0e9b:
        return r14;
    L_0x0e9c:
        return r12;
    L_0x0e9d:
        r0 = -11099429; // 0xfffffffffvar_a2db float:-2.8530039E38 double:NaN;
        return r0;
    L_0x0ea1:
        r0 = -723724; // 0xfffffffffff4f4f4 float:NaN double:NaN;
        return r0;
    L_0x0ea5:
        return r16;
    L_0x0ea6:
        r0 = -7105645; // 0xfffffffffvar_ float:NaN double:NaN;
        return r0;
    L_0x0eaa:
        r0 = -11167525; // 0xfffffffffvar_db float:-2.8391923E38 double:NaN;
        return r0;
    L_0x0eae:
        return r15;
    L_0x0eaf:
        r0 = -2236963; // 0xffffffffffdddddd float:NaN double:NaN;
        return r0;
    L_0x0eb3:
        r0 = -14540254; // 0xfffffffffvar_ float:-2.1551216E38 double:NaN;
        return r0;
    L_0x0eb7:
        r0 = -9868951; // 0xfffffffffvar_ float:-3.1025744E38 double:NaN;
        return r0;
    L_0x0ebb:
        return r10;
    L_0x0ebc:
        r0 = -13948117; // 0xffffffffff2b2b2b float:-2.2752213E38 double:NaN;
        return r0;
    L_0x0ec0:
        r0 = -3874313; // 0xffffffffffc4e1f7 float:NaN double:NaN;
        return r0;
    L_0x0ec4:
        r0 = -9652901; // 0xffffffffff6cb55b float:-3.1463946E38 double:NaN;
        return r0;
    L_0x0ec8:
        return r2;
    L_0x0ec9:
        r0 = -13091262; // 0xfffffffffvar_e42 float:-2.4490121E38 double:NaN;
        return r0;
    L_0x0ecd:
        return r14;
    L_0x0ece:
        r0 = -NUM; // 0xffffffffa4000000 float:-2.7755576E-17 double:NaN;
        return r0;
    L_0x0ed1:
        return r7;
    L_0x0ed2:
        r0 = -14925725; // 0xffffffffff1CLASSNAME float:-2.0769388E38 double:NaN;
        return r0;
    L_0x0ed6:
        return r16;
    L_0x0ed7:
        return r7;
    L_0x0ed8:
        r0 = -14338750; // 0xfffffffffvar_ float:-2.1959915E38 double:NaN;
        return r0;
    L_0x0edc:
        r0 = -NUM; // 0xfffffffvar_a9cfee float:-2.4951664E-37 double:NaN;
        return r0;
    L_0x0ee0:
        r0 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        return r0;
    L_0x0ee3:
        r0 = -13077596; // 0xfffffffffvar_a4 float:-2.451784E38 double:NaN;
        return r0;
    L_0x0ee7:
        r0 = -10851462; // 0xffffffffff5a6b7a float:-2.9032975E38 double:NaN;
        return r0;
    L_0x0eeb:
        return r9;
    L_0x0eec:
        r0 = -5582866; // 0xffffffffffaacfee float:NaN double:NaN;
        return r0;
    L_0x0ef0:
        r0 = -14935012; // 0xffffffffff1c1c1c float:-2.0750552E38 double:NaN;
        return r0;
    L_0x0ef4:
        r0 = -11108183; // 0xfffffffffvar_a9 float:-2.8512283E38 double:NaN;
        return r0;
    L_0x0ef8:
        r0 = -NUM; // 0xffffffffd82b2b2b float:-7.5280757E14 double:NaN;
        return r0;
    L_0x0efc:
        r0 = NUM; // 0x60495154 float:5.8025873E19 double:7.981223813E-315;
        return r0;
    L_0x0var_:
        r0 = -13143396; // 0xfffffffffvar_c float:-2.438438E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -13077852; // 0xfffffffffvar_a4 float:-2.451732E38 double:NaN;
        return r0;
    L_0x0var_:
        return r7;
    L_0x0var_:
        r0 = -14143949; // 0xfffffffffvar_e33 float:-2.2355018E38 double:NaN;
        return r0;
    L_0x0f0d:
        r0 = -11234874; // 0xfffffffffvar_c6 float:-2.8255323E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -11972524; // 0xfffffffffvar_ float:-2.6759191E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -8211748; // 0xfffffffffvar_b2dc float:NaN double:NaN;
        return r0;
    L_0x0var_:
        r0 = NUM; // 0x17ffffff float:1.6543611E-24 double:1.98937105E-315;
        return r0;
    L_0x0f1d:
        r0 = -2954241; // 0xffffffffffd2ebff float:NaN double:NaN;
        return r0;
    L_0x0var_:
        r0 = -986896; // 0xfffffffffff0f0f0 float:NaN double:NaN;
        return r0;
    L_0x0var_:
        r0 = NUM; // 0xfffffff float:2.5243547E-29 double:1.326247364E-315;
        return r0;
    L_0x0var_:
        r0 = -12829636; // 0xffffffffff3c3c3c float:-2.5020762E38 double:NaN;
        return r0;
    L_0x0f2d:
        return r11;
    L_0x0f2e:
        r0 = -7960954; // 0xfffffffffvar_ float:NaN double:NaN;
        return r0;
    L_0x0var_:
        return r13;
    L_0x0var_:
        r0 = -NUM; // 0xffffffff8a000000 float:-6.162976E-33 double:NaN;
        return r0;
    L_0x0var_:
        return r2;
    L_0x0var_:
        return r16;
    L_0x0var_:
        r0 = -1313793; // 0xffffffffffebf3ff float:NaN double:NaN;
        return r0;
    L_0x0f3c:
        return r10;
    L_0x0f3d:
        return r15;
    L_0x0f3e:
        r0 = NUM; // 0xeffffff float:6.310887E-30 double:1.243356904E-315;
        return r0;
    L_0x0var_:
        return r8;
    L_0x0var_:
        r0 = -9594162; // 0xffffffffff6d9ace float:-3.1583083E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -11164709; // 0xfffffffffvar_a3db float:-2.8397635E38 double:NaN;
        return r0;
    L_0x0f4b:
        return r2;
    L_0x0f4c:
        r0 = -8487298; // 0xffffffffff7e7e7e float:-3.382807E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -1579033; // 0xffffffffffe7e7e7 float:NaN double:NaN;
        return r0;
    L_0x0var_:
        r0 = -8882056; // 0xfffffffffvar_ float:-3.3027405E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = NUM; // 0x4c0var_ed float:3.7644212E7 double:6.304726554E-315;
        return r0;
    L_0x0f5c:
        return r3;
    L_0x0f5d:
        return r2;
    L_0x0f5e:
        return r8;
    L_0x0f5f:
        return r2;
    L_0x0var_:
        r0 = -9276814; // 0xfffffffffvar_ float:-3.222674E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -12401818; // 0xfffffffffvar_CLASSNAME float:-2.588848E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -15921907; // 0xffffffffff0d0d0d float:-1.8748891E38 double:NaN;
        return r0;
    L_0x0f6c:
        r0 = -13803892; // 0xffffffffff2d5e8c float:-2.3044736E38 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -NUM; // 0xffffffffd98091a0 float:-4.5236142E15 double:NaN;
        return r0;
    L_0x0var_:
        r0 = -8224126; // 0xfffffffffvar_ float:NaN double:NaN;
        return r0;
    L_0x0var_:
        r0 = NUM; // 0x7a0var_ float:1.8575207E35 double:1.0117524847E-314;
        return r0;
    L_0x0f7c:
        r0 = -11167525; // 0xfffffffffvar_db float:-2.8391923E38 double:NaN;
        return r0;
    L_0x0var_:
        return r11;
    L_0x0var_:
        return r3;
    L_0x0var_:
        r0 = -NUM; // 0xffffffffb61e1e1e float:-2.3561365E-6 double:NaN;
        return r0;
    L_0x0var_:
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.DarkTheme.getColor(java.lang.String):int");
    }

    public static Drawable getThemedDrawable(Context context, int i, String str) {
        Drawable mutate = context.getResources().getDrawable(i).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getColor(str), Mode.MULTIPLY));
        return mutate;
    }
}
