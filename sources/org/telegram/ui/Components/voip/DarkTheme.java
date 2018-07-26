package org.telegram.ui.Components.voip;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class DarkTheme {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getColor(String key) {
        Object obj;
        switch (key.hashCode()) {
            case -2147269658:
                if (key.equals(Theme.key_chat_outMenu)) {
                    obj = 23;
                    break;
                }
            case -2139469579:
                if (key.equals(Theme.key_chat_emojiPanelEmptyText)) {
                    obj = 99;
                    break;
                }
            case -2132427577:
                if (key.equals(Theme.key_chat_outViews)) {
                    obj = 40;
                    break;
                }
            case -2103805301:
                if (key.equals(Theme.key_actionBarActionModeDefault)) {
                    obj = 24;
                    break;
                }
            case -2102232027:
                if (key.equals(Theme.key_profile_actionIcon)) {
                    obj = 150;
                    break;
                }
            case -2019587427:
                if (key.equals("listSelector")) {
                    obj = 101;
                    break;
                }
            case -1992864503:
                if (key.equals(Theme.key_actionBarDefaultSubmenuBackground)) {
                    obj = 88;
                    break;
                }
            case -1992639563:
                if (key.equals(Theme.key_avatar_actionBarSelectorViolet)) {
                    obj = 195;
                    break;
                }
            case -1975862704:
                if (key.equals(Theme.key_player_button)) {
                    obj = 33;
                    break;
                }
            case -1974166005:
                if (key.equals(Theme.key_chat_outFileProgressSelected)) {
                    obj = 30;
                    break;
                }
            case -1961633574:
                if (key.equals(Theme.key_chat_outLoader)) {
                    obj = 179;
                    break;
                }
            case -1942198229:
                if (key.equals(Theme.key_chats_menuPhone)) {
                    obj = 196;
                    break;
                }
            case -1927175348:
                if (key.equals(Theme.key_chat_outFileBackgroundSelected)) {
                    obj = 208;
                    break;
                }
            case -1926854985:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText2)) {
                    obj = 148;
                    break;
                }
            case -1926854984:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText3)) {
                    obj = 87;
                    break;
                }
            case -1926854983:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText4)) {
                    obj = 159;
                    break;
                }
            case -1924841028:
                if (key.equals(Theme.key_actionBarDefaultSubtitle)) {
                    obj = 105;
                    break;
                }
            case -1891930735:
                if (key.equals(Theme.key_chat_outFileBackground)) {
                    obj = 162;
                    break;
                }
            case -1878988531:
                if (key.equals(Theme.key_avatar_actionBarSelectorGreen)) {
                    obj = 108;
                    break;
                }
            case -1853661732:
                if (key.equals(Theme.key_chat_outTimeSelectedText)) {
                    obj = 90;
                    break;
                }
            case -1850167367:
                if (key.equals(Theme.key_chat_emojiPanelShadowLine)) {
                    obj = 25;
                    break;
                }
            case -1849805674:
                if (key.equals(Theme.key_dialogBackground)) {
                    obj = 26;
                    break;
                }
            case -1787129273:
                if (key.equals(Theme.key_chat_outContactBackground)) {
                    obj = 211;
                    break;
                }
            case -1779173263:
                if (key.equals(Theme.key_chat_topPanelMessage)) {
                    obj = 132;
                    break;
                }
            case -1777297962:
                if (key.equals(Theme.key_chats_muteIcon)) {
                    obj = 244;
                    break;
                }
            case -1767675171:
                if (key.equals(Theme.key_chat_inViaBotNameText)) {
                    obj = 156;
                    break;
                }
            case -1758608141:
                if (key.equals(Theme.key_windowBackgroundWhiteValueText)) {
                    obj = 232;
                    break;
                }
            case -1733632792:
                if (key.equals(Theme.key_emptyListPlaceholder)) {
                    obj = 241;
                    break;
                }
            case -1724033454:
                if (key.equals(Theme.key_chat_inPreviewInstantText)) {
                    obj = 27;
                    break;
                }
            case -1719903102:
                if (key.equals(Theme.key_chat_outViewsSelected)) {
                    obj = 145;
                    break;
                }
            case -1719839798:
                if (key.equals(Theme.key_avatar_backgroundInProfileBlue)) {
                    obj = PsExtractor.PRIVATE_STREAM_1;
                    break;
                }
            case -1683744660:
                if (key.equals(Theme.key_profile_verifiedBackground)) {
                    obj = 161;
                    break;
                }
            case -1654302575:
                if (key.equals(Theme.key_chats_menuBackground)) {
                    obj = 173;
                    break;
                }
            case -1633591792:
                if (key.equals(Theme.key_chat_emojiPanelStickerPackSelector)) {
                    obj = 225;
                    break;
                }
            case -1625862693:
                if (key.equals(Theme.key_chat_wallpaper)) {
                    obj = 224;
                    break;
                }
            case -1623818608:
                if (key.equals(Theme.key_chat_inForwardedNameText)) {
                    obj = 165;
                    break;
                }
            case -1604008580:
                if (key.equals(Theme.key_chat_outAudioProgress)) {
                    obj = 54;
                    break;
                }
            case -1589702002:
                if (key.equals(Theme.key_chat_inLoaderPhotoSelected)) {
                    obj = 60;
                    break;
                }
            case -1565843249:
                if (key.equals(Theme.key_files_folderIcon)) {
                    obj = 139;
                    break;
                }
            case -1543133775:
                if (key.equals(Theme.key_chat_outContactNameText)) {
                    obj = 222;
                    break;
                }
            case -1542353776:
                if (key.equals(Theme.key_chat_outVoiceSeekbar)) {
                    obj = 133;
                    break;
                }
            case -1533503664:
                if (key.equals(Theme.key_chat_outFileProgress)) {
                    obj = 124;
                    break;
                }
            case -1530345450:
                if (key.equals(Theme.key_chat_inReplyMessageText)) {
                    obj = 107;
                    break;
                }
            case -1496224782:
                if (key.equals(Theme.key_chat_inReplyLine)) {
                    obj = 104;
                    break;
                }
            case -1415980195:
                if (key.equals(Theme.key_files_folderIconBackground)) {
                    obj = 160;
                    break;
                }
            case -1407570354:
                if (key.equals(Theme.key_chat_inReplyMediaMessageText)) {
                    obj = 176;
                    break;
                }
            case -1397026623:
                if (key.equals(Theme.key_windowBackgroundGray)) {
                    obj = 9;
                    break;
                }
            case -1385379359:
                if (key.equals(Theme.key_dialogIcon)) {
                    obj = 93;
                    break;
                }
            case -1316415606:
                if (key.equals(Theme.key_actionBarActionModeDefaultSelector)) {
                    obj = 5;
                    break;
                }
            case -1310183623:
                if (key.equals(Theme.key_chat_muteIcon)) {
                    obj = 20;
                    break;
                }
            case -1262649070:
                if (key.equals(Theme.key_avatar_nameInMessageGreen)) {
                    obj = 66;
                    break;
                }
            case -1240647597:
                if (key.equals(Theme.key_chat_outBubbleShadow)) {
                    obj = 55;
                    break;
                }
            case -1229478359:
                if (key.equals(Theme.key_chats_unreadCounter)) {
                    obj = 85;
                    break;
                }
            case -1213387098:
                if (key.equals(Theme.key_chat_inMenuSelected)) {
                    obj = 56;
                    break;
                }
            case -1147596450:
                if (key.equals(Theme.key_chat_inFileInfoSelectedText)) {
                    obj = 79;
                    break;
                }
            case -1106471792:
                if (key.equals(Theme.key_chat_outAudioPerfomerText)) {
                    obj = 94;
                    break;
                }
            case -1078554766:
                if (key.equals(Theme.key_windowBackgroundWhiteBlueHeader)) {
                    obj = 97;
                    break;
                }
            case -1074293766:
                if (key.equals(Theme.key_avatar_backgroundActionBarGreen)) {
                    obj = 143;
                    break;
                }
            case -1063762099:
                if (key.equals(Theme.key_windowBackgroundWhiteGreenText2)) {
                    obj = 10;
                    break;
                }
            case -1062379852:
                if (key.equals(Theme.key_chat_messageLinkOut)) {
                    obj = 204;
                    break;
                }
            case -1046600742:
                if (key.equals(Theme.key_profile_actionBackground)) {
                    obj = 64;
                    break;
                }
            case -1019316079:
                if (key.equals(Theme.key_chat_outReplyMessageText)) {
                    obj = 210;
                    break;
                }
            case -1012016554:
                if (key.equals(Theme.key_chat_inFileBackground)) {
                    obj = 3;
                    break;
                }
            case -1006953508:
                if (key.equals(Theme.key_chat_secretTimerBackground)) {
                    obj = 1;
                    break;
                }
            case -1005376655:
                if (key.equals(Theme.key_chat_inAudioSeekbar)) {
                    obj = 187;
                    break;
                }
            case -1005120019:
                if (key.equals(Theme.key_chats_secretIcon)) {
                    obj = 92;
                    break;
                }
            case -1004973057:
                if (key.equals(Theme.key_chats_secretName)) {
                    obj = 103;
                    break;
                }
            case -960321732:
                if (key.equals(Theme.key_chat_mediaMenu)) {
                    obj = 21;
                    break;
                }
            case -955211830:
                if (key.equals(Theme.key_chat_topPanelLine)) {
                    obj = 49;
                    break;
                }
            case -938826921:
                if (key.equals(Theme.key_player_actionBarSubtitle)) {
                    obj = 223;
                    break;
                }
            case -901363160:
                if (key.equals(Theme.key_chats_menuPhoneCats)) {
                    obj = 226;
                    break;
                }
            case -834035478:
                if (key.equals(Theme.key_chat_outSentClockSelected)) {
                    obj = 65;
                    break;
                }
            case -810517465:
                if (key.equals(Theme.key_chat_outAudioSeekbarSelected)) {
                    obj = TsExtractor.TS_STREAM_TYPE_AC3;
                    break;
                }
            case -805096120:
                if (key.equals(Theme.key_chats_nameIcon)) {
                    obj = 72;
                    break;
                }
            case -792942846:
                if (key.equals(Theme.key_graySection)) {
                    obj = 71;
                    break;
                }
            case -779362418:
                if (key.equals(Theme.key_chat_emojiPanelTrendingTitle)) {
                    obj = 77;
                    break;
                }
            case -763385518:
                if (key.equals(Theme.key_chats_date)) {
                    obj = 123;
                    break;
                }
            case -763087825:
                if (key.equals(Theme.key_chats_name)) {
                    obj = 168;
                    break;
                }
            case -756337980:
                if (key.equals(Theme.key_profile_actionPressedBackground)) {
                    obj = 116;
                    break;
                }
            case -712338357:
                if (key.equals(Theme.key_chat_inSiteNameText)) {
                    obj = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
                    break;
                }
            case -687452692:
                if (key.equals(Theme.key_chat_inLoaderPhotoIcon)) {
                    obj = 193;
                    break;
                }
            case -654429213:
                if (key.equals(Theme.key_chats_message)) {
                    obj = 238;
                    break;
                }
            case -652337344:
                if (key.equals("chat_outVenueNameText")) {
                    obj = PsExtractor.VIDEO_STREAM_MASK;
                    break;
                }
            case -629209323:
                if (key.equals(Theme.key_chats_pinnedIcon)) {
                    obj = 18;
                    break;
                }
            case -608456434:
                if (key.equals(Theme.key_chat_outBubbleSelected)) {
                    obj = 125;
                    break;
                }
            case -603597494:
                if (key.equals(Theme.key_chat_inSentClock)) {
                    obj = 166;
                    break;
                }
            case -570274322:
                if (key.equals(Theme.key_chat_outReplyMediaMessageSelectedText)) {
                    obj = 248;
                    break;
                }
            case -564899147:
                if (key.equals(Theme.key_chat_outInstantSelected)) {
                    obj = 190;
                    break;
                }
            case -560721948:
                if (key.equals(Theme.key_chat_outSentCheckSelected)) {
                    obj = 89;
                    break;
                }
            case -552118908:
                if (key.equals(Theme.key_actionBarDefault)) {
                    obj = 113;
                    break;
                }
            case -493564645:
                if (key.equals(Theme.key_avatar_actionBarSelectorRed)) {
                    obj = 32;
                    break;
                }
            case -450514995:
                if (key.equals(Theme.key_chats_actionMessage)) {
                    obj = 42;
                    break;
                }
            case -427186938:
                if (key.equals(Theme.key_chat_inAudioDurationSelectedText)) {
                    obj = 111;
                    break;
                }
            case -391617936:
                if (key.equals(Theme.key_chat_selectedBackground)) {
                    obj = 17;
                    break;
                }
            case -354489314:
                if (key.equals(Theme.key_chat_outFileInfoText)) {
                    obj = 51;
                    break;
                }
            case -343666293:
                if (key.equals(Theme.key_windowBackgroundWhite)) {
                    obj = 171;
                    break;
                }
            case -294026410:
                if (key.equals(Theme.key_chat_inReplyNameText)) {
                    obj = 69;
                    break;
                }
            case -264184037:
                if (key.equals(Theme.key_inappPlayerClose)) {
                    obj = 206;
                    break;
                }
            case -260428237:
                if (key.equals(Theme.key_chat_outVoiceSeekbarFill)) {
                    obj = 197;
                    break;
                }
            case -258492929:
                if (key.equals(Theme.key_avatar_nameInMessageOrange)) {
                    obj = 158;
                    break;
                }
            case -251079667:
                if (key.equals(Theme.key_chat_outPreviewInstantText)) {
                    obj = 76;
                    break;
                }
            case -249481380:
                if (key.equals(Theme.key_listSelector)) {
                    obj = 213;
                    break;
                }
            case -248568965:
                if (key.equals(Theme.key_inappPlayerTitle)) {
                    obj = 229;
                    break;
                }
            case -212237793:
                if (key.equals(Theme.key_player_actionBar)) {
                    obj = 50;
                    break;
                }
            case -185786131:
                if (key.equals(Theme.key_chat_unreadMessagesStartText)) {
                    obj = 205;
                    break;
                }
            case -176488427:
                if (key.equals(Theme.key_chat_replyPanelLine)) {
                    obj = 175;
                    break;
                }
            case -143547632:
                if (key.equals(Theme.key_chat_inFileProgressSelected)) {
                    obj = 131;
                    break;
                }
            case -127673038:
                if (key.equals("key_chats_menuTopShadow")) {
                    obj = 170;
                    break;
                }
            case -108292334:
                if (key.equals(Theme.key_chats_menuTopShadow)) {
                    obj = 136;
                    break;
                }
            case -71280336:
                if (key.equals(Theme.key_switchTrackChecked)) {
                    obj = 146;
                    break;
                }
            case -65607089:
                if (key.equals(Theme.key_chats_menuItemIcon)) {
                    obj = 6;
                    break;
                }
            case -65277181:
                if (key.equals(Theme.key_chats_menuItemText)) {
                    obj = 37;
                    break;
                }
            case -35597940:
                if (key.equals(Theme.key_chat_inContactNameText)) {
                    obj = 218;
                    break;
                }
            case -18073397:
                if (key.equals(Theme.key_chats_tabletSelectedOverlay)) {
                    obj = 36;
                    break;
                }
            case -12871922:
                if (key.equals(Theme.key_chat_secretChatStatusText)) {
                    obj = 151;
                    break;
                }
            case 6289575:
                if (key.equals(Theme.key_chat_inLoaderPhotoIconSelected)) {
                    obj = 216;
                    break;
                }
            case 27337780:
                if (key.equals(Theme.key_chats_pinnedOverlay)) {
                    obj = 95;
                    break;
                }
            case 49148112:
                if (key.equals(Theme.key_chat_inPreviewLine)) {
                    obj = 153;
                    break;
                }
            case 51359814:
                if (key.equals(Theme.key_chat_replyPanelMessage)) {
                    obj = 75;
                    break;
                }
            case 57332012:
                if (key.equals(Theme.key_chats_sentCheck)) {
                    obj = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    break;
                }
            case 57460786:
                if (key.equals(Theme.key_chats_sentClock)) {
                    obj = 203;
                    break;
                }
            case 89466127:
                if (key.equals(Theme.key_chat_outAudioSeekbarFill)) {
                    obj = 67;
                    break;
                }
            case 117743477:
                if (key.equals(Theme.key_chat_outPreviewLine)) {
                    obj = 199;
                    break;
                }
            case 141076636:
                if (key.equals(Theme.key_groupcreate_spanBackground)) {
                    obj = 43;
                    break;
                }
            case 141894978:
                if (key.equals(Theme.key_windowBackgroundWhiteRedText5)) {
                    obj = 194;
                    break;
                }
            case 185438775:
                if (key.equals(Theme.key_chat_outAudioSelectedProgress)) {
                    obj = 182;
                    break;
                }
            case 216441603:
                if (key.equals(Theme.key_chat_goDownButton)) {
                    obj = 114;
                    break;
                }
            case 231486891:
                if (key.equals(Theme.key_chat_inAudioPerfomerText)) {
                    obj = 82;
                    break;
                }
            case 243668262:
                if (key.equals(Theme.key_chat_inTimeText)) {
                    obj = 7;
                    break;
                }
            case 257089712:
                if (key.equals(Theme.key_chat_outAudioDurationText)) {
                    obj = 119;
                    break;
                }
            case 271457747:
                if (key.equals(Theme.key_chat_inBubbleSelected)) {
                    obj = 122;
                    break;
                }
            case 303350244:
                if (key.equals(Theme.key_chat_reportSpam)) {
                    obj = 227;
                    break;
                }
            case 316847509:
                if (key.equals(Theme.key_chat_outLoaderSelected)) {
                    obj = 15;
                    break;
                }
            case 339397761:
                if (key.equals(Theme.key_windowBackgroundWhiteBlackText)) {
                    obj = 246;
                    break;
                }
            case 371859081:
                if (key.equals(Theme.key_chat_inReplyMediaMessageSelectedText)) {
                    obj = 140;
                    break;
                }
            case 415452907:
                if (key.equals(Theme.key_chat_outAudioDurationSelectedText)) {
                    obj = 142;
                    break;
                }
            case 421601145:
                if (key.equals(Theme.key_chat_emojiPanelIconSelected)) {
                    obj = 4;
                    break;
                }
            case 421601469:
                if (key.equals(Theme.key_chat_emojiPanelIconSelector)) {
                    obj = 74;
                    break;
                }
            case 426061980:
                if (key.equals(Theme.key_chat_serviceBackground)) {
                    obj = 147;
                    break;
                }
            case 429680544:
                if (key.equals(Theme.key_avatar_subtitleInProfileBlue)) {
                    obj = 239;
                    break;
                }
            case 429722217:
                if (key.equals(Theme.key_avatar_subtitleInProfileCyan)) {
                    obj = 63;
                    break;
                }
            case 430094524:
                if (key.equals(Theme.key_avatar_subtitleInProfilePink)) {
                    obj = null;
                    break;
                }
            case 435303214:
                if (key.equals(Theme.key_actionBarDefaultSubmenuItem)) {
                    obj = 198;
                    break;
                }
            case 439976061:
                if (key.equals(Theme.key_avatar_subtitleInProfileGreen)) {
                    obj = 228;
                    break;
                }
            case 444983522:
                if (key.equals(Theme.key_chat_topPanelClose)) {
                    obj = Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
                    break;
                }
            case 446162770:
                if (key.equals(Theme.key_windowBackgroundWhiteBlueText)) {
                    obj = 247;
                    break;
                }
            case 460598594:
                if (key.equals(Theme.key_chat_topPanelTitle)) {
                    obj = 219;
                    break;
                }
            case 484353662:
                if (key.equals(Theme.key_chat_inVenueInfoText)) {
                    obj = 118;
                    break;
                }
            case 503923205:
                if (key.equals(Theme.key_chat_inSentClockSelected)) {
                    obj = 183;
                    break;
                }
            case 527405547:
                if (key.equals(Theme.key_inappPlayerBackground)) {
                    obj = 48;
                    break;
                }
            case 556028747:
                if (key.equals(Theme.key_chat_outVoiceSeekbarSelected)) {
                    obj = 28;
                    break;
                }
            case 589961756:
                if (key.equals(Theme.key_chat_goDownButtonIcon)) {
                    obj = 214;
                    break;
                }
            case 613458991:
                if (key.equals(Theme.key_dialogTextLink)) {
                    obj = 164;
                    break;
                }
            case 626157205:
                if (key.equals(Theme.key_chat_inVoiceSeekbar)) {
                    obj = 34;
                    break;
                }
            case 634019162:
                if (key.equals(Theme.key_chat_emojiPanelBackspace)) {
                    obj = 11;
                    break;
                }
            case 635007317:
                if (key.equals(Theme.key_chat_inFileProgress)) {
                    obj = 242;
                    break;
                }
            case 648238646:
                if (key.equals(Theme.key_chat_outAudioTitleText)) {
                    obj = 59;
                    break;
                }
            case 655457041:
                if (key.equals(Theme.key_chat_inFileBackgroundSelected)) {
                    obj = 234;
                    break;
                }
            case 676996437:
                if (key.equals(Theme.key_chat_outLocationIcon)) {
                    obj = 81;
                    break;
                }
            case 716656587:
                if (key.equals(Theme.key_avatar_backgroundGroupCreateSpanBlue)) {
                    obj = 8;
                    break;
                }
            case 732262561:
                if (key.equals(Theme.key_chat_outTimeText)) {
                    obj = 258;
                    break;
                }
            case 759679774:
                if (key.equals(Theme.key_chat_outVenueInfoSelectedText)) {
                    obj = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    break;
                }
            case 765296599:
                if (key.equals(Theme.key_chat_outReplyLine)) {
                    obj = 254;
                    break;
                }
            case 803672502:
                if (key.equals(Theme.key_chat_messagePanelIcons)) {
                    obj = 70;
                    break;
                }
            case 826015922:
                if (key.equals(Theme.key_chat_emojiPanelTrendingDescription)) {
                    obj = 2;
                    break;
                }
            case 850854541:
                if (key.equals(Theme.key_chat_inPreviewInstantSelectedText)) {
                    obj = 78;
                    break;
                }
            case 890367586:
                if (key.equals(Theme.key_chat_inViews)) {
                    obj = 100;
                    break;
                }
            case 911091978:
                if (key.equals(Theme.key_chat_outLocationBackground)) {
                    obj = 243;
                    break;
                }
            case 913069217:
                if (key.equals(Theme.key_chat_outMenuSelected)) {
                    obj = 252;
                    break;
                }
            case 927863384:
                if (key.equals(Theme.key_chat_inBubbleShadow)) {
                    obj = 184;
                    break;
                }
            case 939137799:
                if (key.equals(Theme.key_chat_inContactPhoneText)) {
                    obj = 188;
                    break;
                }
            case 939824634:
                if (key.equals(Theme.key_chat_outInstant)) {
                    obj = 209;
                    break;
                }
            case 946144034:
                if (key.equals(Theme.key_windowBackgroundWhiteBlueText4)) {
                    obj = 217;
                    break;
                }
            case 962085693:
                if (key.equals(Theme.key_chats_menuCloudBackgroundCats)) {
                    obj = 215;
                    break;
                }
            case 983278580:
                if (key.equals(Theme.key_avatar_subtitleInProfileOrange)) {
                    obj = 261;
                    break;
                }
            case 993048796:
                if (key.equals(Theme.key_chat_inFileSelectedIcon)) {
                    obj = 149;
                    break;
                }
            case 1008947016:
                if (key.equals(Theme.key_avatar_backgroundActionBarRed)) {
                    obj = 231;
                    break;
                }
            case 1020100908:
                if (key.equals(Theme.key_chat_inAudioSeekbarSelected)) {
                    obj = 167;
                    break;
                }
            case 1045892135:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayIcon)) {
                    obj = 186;
                    break;
                }
            case 1046222043:
                if (key.equals(Theme.key_windowBackgroundWhiteGrayText)) {
                    obj = 86;
                    break;
                }
            case 1079427869:
                if (key.equals(Theme.key_chat_inViewsSelected)) {
                    obj = 141;
                    break;
                }
            case 1100033490:
                if (key.equals(Theme.key_chat_inAudioSelectedProgress)) {
                    obj = 115;
                    break;
                }
            case 1106068251:
                if (key.equals(Theme.key_groupcreate_spanText)) {
                    obj = 245;
                    break;
                }
            case 1121079660:
                if (key.equals(Theme.key_chat_outAudioSeekbar)) {
                    obj = PsExtractor.AUDIO_STREAM;
                    break;
                }
            case 1122192435:
                if (key.equals(Theme.key_chat_outLoaderPhotoSelected)) {
                    obj = 220;
                    break;
                }
            case 1175786053:
                if (key.equals(Theme.key_avatar_subtitleInProfileViolet)) {
                    obj = 181;
                    break;
                }
            case 1195322391:
                if (key.equals(Theme.key_chat_inAudioProgress)) {
                    obj = 207;
                    break;
                }
            case 1199344772:
                if (key.equals(Theme.key_chat_topPanelBackground)) {
                    obj = TsExtractor.TS_STREAM_TYPE_SPLICE_INFO;
                    break;
                }
            case 1201609915:
                if (key.equals(Theme.key_chat_outReplyNameText)) {
                    obj = 180;
                    break;
                }
            case 1202885960:
                if (key.equals(Theme.key_chat_outPreviewInstantSelectedText)) {
                    obj = 12;
                    break;
                }
            case 1212117123:
                if (key.equals(Theme.key_avatar_backgroundActionBarBlue)) {
                    obj = 155;
                    break;
                }
            case 1212158796:
                if (key.equals(Theme.key_avatar_backgroundActionBarCyan)) {
                    obj = 249;
                    break;
                }
            case 1212531103:
                if (key.equals(Theme.key_avatar_backgroundActionBarPink)) {
                    obj = 178;
                    break;
                }
            case 1231763334:
                if (key.equals(Theme.key_chat_addContact)) {
                    obj = 22;
                    break;
                }
            case 1239758101:
                if (key.equals(Theme.key_player_placeholder)) {
                    obj = 68;
                    break;
                }
            case 1265168609:
                if (key.equals(Theme.key_player_actionBarItems)) {
                    obj = TsExtractor.TS_STREAM_TYPE_DTS;
                    break;
                }
            case 1269980952:
                if (key.equals(Theme.key_chat_inBubble)) {
                    obj = 13;
                    break;
                }
            case 1275014009:
                if (key.equals(Theme.key_player_actionBarTitle)) {
                    obj = 19;
                    break;
                }
            case 1285554199:
                if (key.equals(Theme.key_avatar_backgroundActionBarOrange)) {
                    obj = 233;
                    break;
                }
            case 1288729698:
                if (key.equals(Theme.key_chat_unreadMessagesStartArrowIcon)) {
                    obj = 53;
                    break;
                }
            case 1308150651:
                if (key.equals(Theme.key_chat_outFileNameText)) {
                    obj = 38;
                    break;
                }
            case 1316752473:
                if (key.equals(Theme.key_chat_outFileInfoSelectedText)) {
                    obj = 14;
                    break;
                }
            case 1327229315:
                if (key.equals(Theme.key_actionBarDefaultSelector)) {
                    obj = 98;
                    break;
                }
            case 1333190005:
                if (key.equals(Theme.key_chat_outForwardedNameText)) {
                    obj = 29;
                    break;
                }
            case 1372411761:
                if (key.equals(Theme.key_inappPlayerPerformer)) {
                    obj = 61;
                    break;
                }
            case 1381159341:
                if (key.equals(Theme.key_chat_inContactIcon)) {
                    obj = 57;
                    break;
                }
            case 1411374187:
                if (key.equals(Theme.key_chat_messagePanelHint)) {
                    obj = 174;
                    break;
                }
            case 1411728145:
                if (key.equals(Theme.key_chat_messagePanelText)) {
                    obj = 253;
                    break;
                }
            case 1414117958:
                if (key.equals(Theme.key_chat_outSiteNameText)) {
                    obj = 121;
                    break;
                }
            case 1449754706:
                if (key.equals(Theme.key_chat_outContactIcon)) {
                    obj = 96;
                    break;
                }
            case 1450167170:
                if (key.equals(Theme.key_chat_outContactPhoneText)) {
                    obj = 117;
                    break;
                }
            case 1456911705:
                if (key.equals(Theme.key_player_progressBackground)) {
                    obj = 31;
                    break;
                }
            case 1478061672:
                if (key.equals(Theme.key_avatar_backgroundActionBarViolet)) {
                    obj = 73;
                    break;
                }
            case 1491567659:
                if (key.equals("player_seekBarBackground")) {
                    obj = 202;
                    break;
                }
            case 1504078167:
                if (key.equals(Theme.key_chat_outFileSelectedIcon)) {
                    obj = 91;
                    break;
                }
            case 1528152827:
                if (key.equals(Theme.key_chat_inAudioTitleText)) {
                    obj = 110;
                    break;
                }
            case 1549064140:
                if (key.equals(Theme.key_chat_outLoaderPhotoIconSelected)) {
                    obj = 191;
                    break;
                }
            case 1573464919:
                if (key.equals(Theme.key_chat_serviceBackgroundSelected)) {
                    obj = 47;
                    break;
                }
            case 1585168289:
                if (key.equals(Theme.key_chat_inFileIcon)) {
                    obj = 109;
                    break;
                }
            case 1595048395:
                if (key.equals(Theme.key_chat_inAudioDurationText)) {
                    obj = 212;
                    break;
                }
            case 1628297471:
                if (key.equals(Theme.key_chat_messageLinkIn)) {
                    obj = 84;
                    break;
                }
            case 1635685130:
                if (key.equals(Theme.key_profile_verifiedCheck)) {
                    obj = 144;
                    break;
                }
            case 1637669025:
                if (key.equals(Theme.key_chat_messageTextOut)) {
                    obj = 58;
                    break;
                }
            case 1647377944:
                if (key.equals(Theme.key_chat_outViaBotNameText)) {
                    obj = 230;
                    break;
                }
            case 1657795113:
                if (key.equals(Theme.key_chat_outSentCheck)) {
                    obj = 251;
                    break;
                }
            case 1657923887:
                if (key.equals(Theme.key_chat_outSentClock)) {
                    obj = 112;
                    break;
                }
            case 1663688926:
                if (key.equals(Theme.key_chats_attachMessage)) {
                    obj = 83;
                    break;
                }
            case 1674274489:
                if (key.equals(Theme.key_chat_inVenueInfoSelectedText)) {
                    obj = 236;
                    break;
                }
            case 1674318617:
                if (key.equals(Theme.key_divider)) {
                    obj = 39;
                    break;
                }
            case 1676443787:
                if (key.equals(Theme.key_avatar_subtitleInProfileRed)) {
                    obj = 80;
                    break;
                }
            case 1682961989:
                if (key.equals(Theme.key_switchThumbChecked)) {
                    obj = 106;
                    break;
                }
            case 1687612836:
                if (key.equals(Theme.key_actionBarActionModeDefaultIcon)) {
                    obj = 237;
                    break;
                }
            case 1714118894:
                if (key.equals(Theme.key_chat_unreadMessagesStartBackground)) {
                    obj = 154;
                    break;
                }
            case 1743255577:
                if (key.equals(Theme.key_dialogBackgroundGray)) {
                    obj = 255;
                    break;
                }
            case 1809914009:
                if (key.equals(Theme.key_dialogButtonSelector)) {
                    obj = 256;
                    break;
                }
            case 1814021667:
                if (key.equals(Theme.key_chat_inFileInfoText)) {
                    obj = 185;
                    break;
                }
            case 1828201066:
                if (key.equals(Theme.key_dialogTextBlack)) {
                    obj = 137;
                    break;
                }
            case 1829565163:
                if (key.equals(Theme.key_chat_inMenu)) {
                    obj = 201;
                    break;
                }
            case 1853943154:
                if (key.equals(Theme.key_chat_messageTextIn)) {
                    obj = 44;
                    break;
                }
            case 1878895888:
                if (key.equals(Theme.key_avatar_actionBarSelectorBlue)) {
                    obj = 41;
                    break;
                }
            case 1878937561:
                if (key.equals(Theme.key_avatar_actionBarSelectorCyan)) {
                    obj = 157;
                    break;
                }
            case 1879309868:
                if (key.equals(Theme.key_avatar_actionBarSelectorPink)) {
                    obj = 221;
                    break;
                }
            case 1921699010:
                if (key.equals(Theme.key_chats_unreadCounterMuted)) {
                    obj = 127;
                    break;
                }
            case 1929729373:
                if (key.equals(Theme.key_progressCircle)) {
                    obj = 126;
                    break;
                }
            case 1930276193:
                if (key.equals(Theme.key_chat_inTimeSelectedText)) {
                    obj = 259;
                    break;
                }
            case 1947549395:
                if (key.equals(Theme.key_chat_inLoaderPhoto)) {
                    obj = 163;
                    break;
                }
            case 1972802227:
                if (key.equals(Theme.key_chat_outReplyMediaMessageText)) {
                    obj = 177;
                    break;
                }
            case 1979989987:
                if (key.equals(Theme.key_chat_outVenueInfoText)) {
                    obj = 257;
                    break;
                }
            case 1994112714:
                if (key.equals(Theme.key_actionBarActionModeDefaultTop)) {
                    obj = 62;
                    break;
                }
            case 2016144760:
                if (key.equals(Theme.key_chat_outLoaderPhoto)) {
                    obj = 45;
                    break;
                }
            case 2016511272:
                if (key.equals(Theme.key_stickers_menu)) {
                    obj = 128;
                    break;
                }
            case 2052611411:
                if (key.equals(Theme.key_chat_outBubble)) {
                    obj = 172;
                    break;
                }
            case 2067556030:
                if (key.equals(Theme.key_chat_emojiPanelIcon)) {
                    obj = 16;
                    break;
                }
            case 2073762588:
                if (key.equals(Theme.key_chat_outFileIcon)) {
                    obj = 46;
                    break;
                }
            case 2090082520:
                if (key.equals(Theme.key_chats_nameMessage)) {
                    obj = 169;
                    break;
                }
            case 2099978769:
                if (key.equals(Theme.key_chat_outLoaderPhotoIcon)) {
                    obj = 52;
                    break;
                }
            case 2109820260:
                if (key.equals(Theme.key_avatar_actionBarSelectorOrange)) {
                    obj = 235;
                    break;
                }
            case 2118871810:
                if (key.equals(Theme.key_switchThumb)) {
                    obj = 35;
                    break;
                }
            case 2119150199:
                if (key.equals(Theme.key_switchTrack)) {
                    obj = 260;
                    break;
                }
            case 2131990258:
                if (key.equals(Theme.key_windowBackgroundWhiteLinkText)) {
                    obj = 120;
                    break;
                }
            case 2133456819:
                if (key.equals(Theme.key_chat_emojiPanelBackground)) {
                    obj = 152;
                    break;
                }
            case 2141345810:
                if (key.equals(Theme.key_chat_messagePanelBackground)) {
                    obj = 102;
                    break;
                }
            default:
                obj = -1;
                break;
        }
        switch (obj) {
            case null:
            case 63:
            case 80:
            case 181:
            case 228:
            case 239:
            case 261:
                return -7697782;
            case 1:
                return -NUM;
            case 2:
                return -9342607;
            case 3:
                return -10653824;
            case 4:
                return -11167525;
            case 5:
                return NUM;
            case 6:
                return -8224126;
            case 7:
                return -645885536;
            case 8:
                return -13803892;
            case 9:
                return -15921907;
            case 10:
                return -12401818;
            case 11:
                return -9276814;
            case 12:
                return -1;
            case 13:
                return -14339006;
            case 14:
                return -1;
            case 15:
                return -1;
            case 16:
                return -9342607;
            case 17:
                return NUM;
            case 18:
                return -8882056;
            case 19:
                return -1579033;
            case 20:
                return -8487298;
            case 21:
                return -1;
            case 22:
                return -11164709;
            case 23:
                return -9594162;
            case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                return -14339006;
            case 25:
                return 251658239;
            case 26:
                return -14605274;
            case 27:
                return -11164965;
            case 28:
                return -1313793;
            case 29:
                return -3019777;
            case 30:
                return -1;
            case 31:
                return -NUM;
            case 32:
                return -11972268;
            case 33:
                return -7960954;
            case 34:
                return -10653824;
            case 35:
                return -12829636;
            case TsExtractor.TS_STREAM_TYPE_H265 /*36*/:
                return 268435455;
            case 37:
                return -986896;
            case 38:
                return -2954241;
            case 39:
                return 402653183;
            case 40:
                return -8211748;
            case 41:
                return -11972524;
            case 42:
                return -11234874;
            case 43:
                return -14143949;
            case 44:
                return -328966;
            case 45:
                return -13077852;
            case 46:
                return -13143396;
            case 47:
                return NUM;
            case 48:
                return -668259541;
            case 49:
                return -11108183;
            case 50:
                return -14935012;
            case 51:
                return -5582866;
            case 52:
                return -9263664;
            case 53:
                return -10851462;
            case 54:
                return -13077596;
            case 55:
                return Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
            case 56:
                return -NUM;
            case 57:
                return -14338750;
            case 58:
                return -328966;
            case 59:
                return -3019777;
            case 60:
                return -14925725;
            case 61:
                return -328966;
            case 62:
                return -NUM;
            case 64:
                return -13091262;
            case VoIPService.CALL_MIN_LAYER /*65*/:
                return -1;
            case 66:
                return -9652901;
            case 67:
                return -3874313;
            case 68:
                return -13948117;
            case 69:
                return -11164965;
            case 70:
                return -9868951;
            case 71:
                return -14540254;
            case 72:
                return -2236963;
            case SecretChatHelper.CURRENT_SECRET_CHAT_LAYER /*73*/:
                return -14605274;
            case VoIPService.CALL_MAX_LAYER /*74*/:
                return -11167525;
            case 75:
                return -7105645;
            case 76:
                return -3019777;
            case 77:
                return -723724;
            case 78:
                return -11099429;
            case 79:
                return -5648402;
            case 81:
                return -10052929;
            case TLRPC.LAYER /*82*/:
                return -8812393;
            case 83:
                return -11234874;
            case 84:
                return -11099173;
            case 85:
                return -14183202;
            case 86:
                return -10132123;
            case 87:
                return -9408400;
            case 88:
                return -81911774;
            case TsExtractor.TS_STREAM_TYPE_DVBSUBS /*89*/:
                return -1;
            case 90:
                return -1;
            case 91:
                return -13925429;
            case 92:
                return -9316522;
            case 93:
                return -8747891;
            case 94:
                return -7028510;
            case 95:
                return 167772159;
            case 96:
                return -5452289;
            case 97:
                return -9851917;
            case 98:
                return -11972268;
            case 99:
                return -10658467;
            case 100:
                return -8812137;
            case 101:
                return 771751936;
            case 102:
                return -14803426;
            case 103:
                return -9316522;
            case 104:
                return -11230501;
            case 105:
                return -7368817;
            case 106:
                return -13600600;
            case 107:
                return -1;
            case 108:
                return -11972268;
            case 109:
                return -14470078;
            case 110:
                return -11099173;
            case 111:
                return -5648402;
            case 112:
                return -8211748;
            case 113:
                return -14407896;
            case 114:
                return -11711155;
            case 115:
                return -14925469;
            case 116:
                return -11972524;
            case 117:
                return -4792321;
            case 118:
                return -10653824;
            case 119:
                return -3019777;
            case 120:
                return -12741934;
            case 121:
                return -3019777;
            case 122:
                return -14925725;
            case 123:
                return -10592674;
            case 124:
                return -9263664;
            case 125:
                return -13859893;
            case 126:
                return -13221820;
            case 127:
                return -12303292;
            case 128:
                return -11710381;
            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
                return -1;
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
                return -11164965;
            case 131:
                return -5845010;
            case 132:
                return -9803158;
            case 133:
                return -9263664;
            case TsExtractor.TS_STREAM_TYPE_SPLICE_INFO /*134*/:
                return -98821092;
            case TsExtractor.TS_STREAM_TYPE_E_AC3 /*135*/:
                return -1;
            case 136:
                return -15724528;
            case 137:
                return -394759;
            case TsExtractor.TS_STREAM_TYPE_DTS /*138*/:
                return -1;
            case 139:
                return -5855578;
            case 140:
                return -9590561;
            case 141:
                return -5648402;
            case 142:
                return -1;
            case 143:
                return -14605274;
            case 144:
                return -1;
            case 145:
                return -1;
            case 146:
                return -15316366;
            case 147:
                return NUM;
            case 148:
                return -8816263;
            case 149:
                return -15056797;
            case 150:
                return -1;
            case 151:
                return -9934744;
            case 152:
                return -14474461;
            case 153:
                return -11230501;
            case 154:
                return -14339006;
            case 155:
                return -14605274;
            case 156:
                return -11164965;
            case 157:
                return -11972268;
            case 158:
                return -2324391;
            case 159:
                return -9539986;
            case 160:
                return -13619152;
            case 161:
                return -11416584;
            case 162:
                return -9263664;
            case 163:
                return -14404542;
            case 164:
                return -13007663;
            case 165:
                return -11164965;
            case 166:
                return -10653824;
            case 167:
                return -5648402;
            case 168:
                return -1644826;
            case 169:
                return -11696202;
            case 170:
                return 789516;
            case 171:
                return -15263719;
            case 172:
                return -13077852;
            case 173:
                return -14868445;
            case 174:
                return -11776948;
            case 175:
                return -14869219;
            case 176:
                return -8812393;
            case 177:
                return -3019777;
            case 178:
                return -14605274;
            case 179:
                return -7421976;
            case 180:
                return -3019777;
            case 182:
                return -14187829;
            case 183:
                return -5648146;
            case 184:
                return Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
            case 185:
                return -8812137;
            case 186:
                return -8224126;
            case 187:
                return -11443856;
            case 188:
                return -8812393;
            case PsExtractor.PRIVATE_STREAM_1 /*189*/:
                return -11232035;
            case 190:
                return -1;
            case 191:
                return -1;
            case PsExtractor.AUDIO_STREAM /*192*/:
                return -NUM;
            case 193:
                return -10915968;
            case 194:
                return -45994;
            case 195:
                return -11972268;
            case 196:
                return NUM;
            case 197:
                return -3874313;
            case 198:
                return -657931;
            case 199:
                return -3019777;
            case Callback.DEFAULT_DRAG_ANIMATION_DURATION /*200*/:
                return -10574624;
            case 201:
                return NUM;
            case 202:
                return NUM;
            case 203:
                return -10452291;
            case 204:
                return -4792577;
            case 205:
                return -620756993;
            case 206:
                return -10987432;
            case 207:
                return -14338750;
            case 208:
                return -1;
            case 209:
                return -4792321;
            case 210:
                return -1;
            case 211:
                return -10910270;
            case 212:
                return -8746857;
            case 213:
                return 301989887;
            case 214:
                return -1776412;
            case 215:
                return -11232035;
            case 216:
                return -5648402;
            case 217:
                return -11890739;
            case 218:
                return -11099173;
            case 219:
                return -11164709;
            case 220:
                return -13208924;
            case 221:
                return -11972268;
            case 222:
                return -3019777;
            case 223:
                return -10526881;
            case 224:
                return -15526377;
            case 225:
                return 217775871;
            case 226:
                return -7434610;
            case 227:
                return -1481631;
            case 229:
                return -6513508;
            case 230:
                return -3019777;
            case 231:
                return -14605274;
            case 232:
                return -12214815;
            case 233:
                return -14605274;
            case 234:
                return -1;
            case 235:
                return -11972268;
            case 236:
                return -5648402;
            case 237:
                return -1;
            case 238:
                return -9934744;
            case PsExtractor.VIDEO_STREAM_MASK /*240*/:
                return -3019777;
            case 241:
                return -11447983;
            case 242:
                return -10653824;
            case 243:
                return -6234891;
            case 244:
                return -10790053;
            case 245:
                return -657931;
            case 246:
                return -855310;
            case 247:
                return -12413479;
            case 248:
                return -1;
            case 249:
                return -14605274;
            case Callback.DEFAULT_SWIPE_ANIMATION_DURATION /*250*/:
                return -11184811;
            case 251:
                return -6831126;
            case 252:
                return -1;
            case 253:
                return -1118482;
            case 254:
                return -3019777;
            case 255:
                return -11840163;
            case 256:
                return 352321535;
            case 257:
                return -4792321;
            case 258:
                return -693579794;
            case 259:
                return -5582866;
            case 260:
                return -13948117;
            default:
                FileLog.m4w("returning color for key " + key + " from current theme");
                return Theme.getColor(key);
        }
    }

    public static Drawable getThemedDrawable(Context context, int resId, String key) {
        Drawable drawable = context.getResources().getDrawable(resId).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(getColor(key), Mode.MULTIPLY));
        return drawable;
    }
}
