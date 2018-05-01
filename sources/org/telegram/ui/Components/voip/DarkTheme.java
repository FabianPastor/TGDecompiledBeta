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
    public static int getColor(String str) {
        int i;
        String str2 = str;
        switch (str.hashCode()) {
            case -2147269658:
                if (str2.equals(Theme.key_chat_outMenu)) {
                    i = 23;
                    break;
                }
            case -2139469579:
                if (str2.equals(Theme.key_chat_emojiPanelEmptyText)) {
                    i = 99;
                    break;
                }
            case -2132427577:
                if (str2.equals(Theme.key_chat_outViews)) {
                    i = 40;
                    break;
                }
            case -2103805301:
                if (str2.equals(Theme.key_actionBarActionModeDefault)) {
                    i = 24;
                    break;
                }
            case -2102232027:
                if (str2.equals(Theme.key_profile_actionIcon)) {
                    i = 150;
                    break;
                }
            case -2019587427:
                if (str2.equals("listSelector")) {
                    i = 101;
                    break;
                }
            case -1992864503:
                if (str2.equals(Theme.key_actionBarDefaultSubmenuBackground)) {
                    i = 88;
                    break;
                }
            case -1992639563:
                if (str2.equals(Theme.key_avatar_actionBarSelectorViolet)) {
                    i = 195;
                    break;
                }
            case -1975862704:
                if (str2.equals(Theme.key_player_button)) {
                    i = 33;
                    break;
                }
            case -1974166005:
                if (str2.equals(Theme.key_chat_outFileProgressSelected)) {
                    i = 30;
                    break;
                }
            case -1961633574:
                if (str2.equals(Theme.key_chat_outLoader)) {
                    i = 179;
                    break;
                }
            case -1942198229:
                if (str2.equals(Theme.key_chats_menuPhone)) {
                    i = 196;
                    break;
                }
            case -1927175348:
                if (str2.equals(Theme.key_chat_outFileBackgroundSelected)) {
                    i = 208;
                    break;
                }
            case -1926854985:
                if (str2.equals(Theme.key_windowBackgroundWhiteGrayText2)) {
                    i = 148;
                    break;
                }
            case -1926854984:
                if (str2.equals(Theme.key_windowBackgroundWhiteGrayText3)) {
                    i = 87;
                    break;
                }
            case -1926854983:
                if (str2.equals(Theme.key_windowBackgroundWhiteGrayText4)) {
                    i = 159;
                    break;
                }
            case -1924841028:
                if (str2.equals(Theme.key_actionBarDefaultSubtitle)) {
                    i = 105;
                    break;
                }
            case -1891930735:
                if (str2.equals(Theme.key_chat_outFileBackground)) {
                    i = 162;
                    break;
                }
            case -1878988531:
                if (str2.equals(Theme.key_avatar_actionBarSelectorGreen)) {
                    i = 108;
                    break;
                }
            case -1853661732:
                if (str2.equals(Theme.key_chat_outTimeSelectedText)) {
                    i = 90;
                    break;
                }
            case -1850167367:
                if (str2.equals(Theme.key_chat_emojiPanelShadowLine)) {
                    i = 25;
                    break;
                }
            case -1849805674:
                if (str2.equals(Theme.key_dialogBackground)) {
                    i = 26;
                    break;
                }
            case -1787129273:
                if (str2.equals(Theme.key_chat_outContactBackground)) {
                    i = 211;
                    break;
                }
            case -1779173263:
                if (str2.equals(Theme.key_chat_topPanelMessage)) {
                    i = 132;
                    break;
                }
            case -1777297962:
                if (str2.equals(Theme.key_chats_muteIcon)) {
                    i = 244;
                    break;
                }
            case -1767675171:
                if (str2.equals(Theme.key_chat_inViaBotNameText)) {
                    i = 156;
                    break;
                }
            case -1758608141:
                if (str2.equals(Theme.key_windowBackgroundWhiteValueText)) {
                    i = 232;
                    break;
                }
            case -1733632792:
                if (str2.equals(Theme.key_emptyListPlaceholder)) {
                    i = 241;
                    break;
                }
            case -1724033454:
                if (str2.equals(Theme.key_chat_inPreviewInstantText)) {
                    i = 27;
                    break;
                }
            case -1719903102:
                if (str2.equals(Theme.key_chat_outViewsSelected)) {
                    i = 145;
                    break;
                }
            case -1719839798:
                if (str2.equals(Theme.key_avatar_backgroundInProfileBlue)) {
                    i = PsExtractor.PRIVATE_STREAM_1;
                    break;
                }
            case -1683744660:
                if (str2.equals(Theme.key_profile_verifiedBackground)) {
                    i = 161;
                    break;
                }
            case -1654302575:
                if (str2.equals(Theme.key_chats_menuBackground)) {
                    i = 173;
                    break;
                }
            case -1633591792:
                if (str2.equals(Theme.key_chat_emojiPanelStickerPackSelector)) {
                    i = 225;
                    break;
                }
            case -1625862693:
                if (str2.equals(Theme.key_chat_wallpaper)) {
                    i = 224;
                    break;
                }
            case -1623818608:
                if (str2.equals(Theme.key_chat_inForwardedNameText)) {
                    i = 165;
                    break;
                }
            case -1604008580:
                if (str2.equals(Theme.key_chat_outAudioProgress)) {
                    i = 54;
                    break;
                }
            case -1589702002:
                if (str2.equals(Theme.key_chat_inLoaderPhotoSelected)) {
                    i = 60;
                    break;
                }
            case -1565843249:
                if (str2.equals(Theme.key_files_folderIcon)) {
                    i = 139;
                    break;
                }
            case -1543133775:
                if (str2.equals(Theme.key_chat_outContactNameText)) {
                    i = 222;
                    break;
                }
            case -1542353776:
                if (str2.equals(Theme.key_chat_outVoiceSeekbar)) {
                    i = 133;
                    break;
                }
            case -1533503664:
                if (str2.equals(Theme.key_chat_outFileProgress)) {
                    i = 124;
                    break;
                }
            case -1530345450:
                if (str2.equals(Theme.key_chat_inReplyMessageText)) {
                    i = 107;
                    break;
                }
            case -1496224782:
                if (str2.equals(Theme.key_chat_inReplyLine)) {
                    i = 104;
                    break;
                }
            case -1415980195:
                if (str2.equals(Theme.key_files_folderIconBackground)) {
                    i = 160;
                    break;
                }
            case -1407570354:
                if (str2.equals(Theme.key_chat_inReplyMediaMessageText)) {
                    i = 176;
                    break;
                }
            case -1397026623:
                if (str2.equals(Theme.key_windowBackgroundGray)) {
                    i = 9;
                    break;
                }
            case -1385379359:
                if (str2.equals(Theme.key_dialogIcon)) {
                    i = 93;
                    break;
                }
            case -1316415606:
                if (str2.equals(Theme.key_actionBarActionModeDefaultSelector)) {
                    i = 5;
                    break;
                }
            case -1310183623:
                if (str2.equals(Theme.key_chat_muteIcon)) {
                    i = 20;
                    break;
                }
            case -1262649070:
                if (str2.equals(Theme.key_avatar_nameInMessageGreen)) {
                    i = 66;
                    break;
                }
            case -1240647597:
                if (str2.equals(Theme.key_chat_outBubbleShadow)) {
                    i = 55;
                    break;
                }
            case -1229478359:
                if (str2.equals(Theme.key_chats_unreadCounter)) {
                    i = 85;
                    break;
                }
            case -1213387098:
                if (str2.equals(Theme.key_chat_inMenuSelected)) {
                    i = 56;
                    break;
                }
            case -1147596450:
                if (str2.equals(Theme.key_chat_inFileInfoSelectedText)) {
                    i = 79;
                    break;
                }
            case -1106471792:
                if (str2.equals(Theme.key_chat_outAudioPerfomerText)) {
                    i = 94;
                    break;
                }
            case -1078554766:
                if (str2.equals(Theme.key_windowBackgroundWhiteBlueHeader)) {
                    i = 97;
                    break;
                }
            case -1074293766:
                if (str2.equals(Theme.key_avatar_backgroundActionBarGreen)) {
                    i = 143;
                    break;
                }
            case -1063762099:
                if (str2.equals(Theme.key_windowBackgroundWhiteGreenText2)) {
                    i = 10;
                    break;
                }
            case -1062379852:
                if (str2.equals(Theme.key_chat_messageLinkOut)) {
                    i = 204;
                    break;
                }
            case -1046600742:
                if (str2.equals(Theme.key_profile_actionBackground)) {
                    i = 64;
                    break;
                }
            case -1019316079:
                if (str2.equals(Theme.key_chat_outReplyMessageText)) {
                    i = 210;
                    break;
                }
            case -1012016554:
                if (str2.equals(Theme.key_chat_inFileBackground)) {
                    i = 3;
                    break;
                }
            case -1006953508:
                if (str2.equals(Theme.key_chat_secretTimerBackground)) {
                    i = 1;
                    break;
                }
            case -1005376655:
                if (str2.equals(Theme.key_chat_inAudioSeekbar)) {
                    i = 187;
                    break;
                }
            case -1005120019:
                if (str2.equals(Theme.key_chats_secretIcon)) {
                    i = 92;
                    break;
                }
            case -1004973057:
                if (str2.equals(Theme.key_chats_secretName)) {
                    i = 103;
                    break;
                }
            case -960321732:
                if (str2.equals(Theme.key_chat_mediaMenu)) {
                    i = 21;
                    break;
                }
            case -955211830:
                if (str2.equals(Theme.key_chat_topPanelLine)) {
                    i = 49;
                    break;
                }
            case -938826921:
                if (str2.equals(Theme.key_player_actionBarSubtitle)) {
                    i = 223;
                    break;
                }
            case -901363160:
                if (str2.equals(Theme.key_chats_menuPhoneCats)) {
                    i = 226;
                    break;
                }
            case -834035478:
                if (str2.equals(Theme.key_chat_outSentClockSelected)) {
                    i = 65;
                    break;
                }
            case -810517465:
                if (str2.equals(Theme.key_chat_outAudioSeekbarSelected)) {
                    i = TsExtractor.TS_STREAM_TYPE_AC3;
                    break;
                }
            case -805096120:
                if (str2.equals(Theme.key_chats_nameIcon)) {
                    i = 72;
                    break;
                }
            case -792942846:
                if (str2.equals(Theme.key_graySection)) {
                    i = 71;
                    break;
                }
            case -779362418:
                if (str2.equals(Theme.key_chat_emojiPanelTrendingTitle)) {
                    i = 77;
                    break;
                }
            case -763385518:
                if (str2.equals(Theme.key_chats_date)) {
                    i = 123;
                    break;
                }
            case -763087825:
                if (str2.equals(Theme.key_chats_name)) {
                    i = 168;
                    break;
                }
            case -756337980:
                if (str2.equals(Theme.key_profile_actionPressedBackground)) {
                    i = 116;
                    break;
                }
            case -712338357:
                if (str2.equals(Theme.key_chat_inSiteNameText)) {
                    i = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
                    break;
                }
            case -687452692:
                if (str2.equals(Theme.key_chat_inLoaderPhotoIcon)) {
                    i = 193;
                    break;
                }
            case -654429213:
                if (str2.equals(Theme.key_chats_message)) {
                    i = 238;
                    break;
                }
            case -652337344:
                if (str2.equals(Theme.key_chat_outVenueNameText)) {
                    i = PsExtractor.VIDEO_STREAM_MASK;
                    break;
                }
            case -629209323:
                if (str2.equals(Theme.key_chats_pinnedIcon)) {
                    i = 18;
                    break;
                }
            case -608456434:
                if (str2.equals(Theme.key_chat_outBubbleSelected)) {
                    i = 125;
                    break;
                }
            case -603597494:
                if (str2.equals(Theme.key_chat_inSentClock)) {
                    i = 166;
                    break;
                }
            case -570274322:
                if (str2.equals(Theme.key_chat_outReplyMediaMessageSelectedText)) {
                    i = 248;
                    break;
                }
            case -564899147:
                if (str2.equals(Theme.key_chat_outInstantSelected)) {
                    i = 190;
                    break;
                }
            case -560721948:
                if (str2.equals(Theme.key_chat_outSentCheckSelected)) {
                    i = 89;
                    break;
                }
            case -552118908:
                if (str2.equals(Theme.key_actionBarDefault)) {
                    i = 113;
                    break;
                }
            case -493564645:
                if (str2.equals(Theme.key_avatar_actionBarSelectorRed)) {
                    i = 32;
                    break;
                }
            case -450514995:
                if (str2.equals(Theme.key_chats_actionMessage)) {
                    i = 42;
                    break;
                }
            case -427186938:
                if (str2.equals(Theme.key_chat_inAudioDurationSelectedText)) {
                    i = 111;
                    break;
                }
            case -391617936:
                if (str2.equals(Theme.key_chat_selectedBackground)) {
                    i = 17;
                    break;
                }
            case -354489314:
                if (str2.equals(Theme.key_chat_outFileInfoText)) {
                    i = 51;
                    break;
                }
            case -343666293:
                if (str2.equals(Theme.key_windowBackgroundWhite)) {
                    i = 171;
                    break;
                }
            case -294026410:
                if (str2.equals(Theme.key_chat_inReplyNameText)) {
                    i = 69;
                    break;
                }
            case -264184037:
                if (str2.equals(Theme.key_inappPlayerClose)) {
                    i = 206;
                    break;
                }
            case -260428237:
                if (str2.equals(Theme.key_chat_outVoiceSeekbarFill)) {
                    i = 197;
                    break;
                }
            case -258492929:
                if (str2.equals(Theme.key_avatar_nameInMessageOrange)) {
                    i = 158;
                    break;
                }
            case -251079667:
                if (str2.equals(Theme.key_chat_outPreviewInstantText)) {
                    i = 76;
                    break;
                }
            case -249481380:
                if (str2.equals(Theme.key_listSelector)) {
                    i = 213;
                    break;
                }
            case -248568965:
                if (str2.equals(Theme.key_inappPlayerTitle)) {
                    i = 229;
                    break;
                }
            case -212237793:
                if (str2.equals(Theme.key_player_actionBar)) {
                    i = 50;
                    break;
                }
            case -185786131:
                if (str2.equals(Theme.key_chat_unreadMessagesStartText)) {
                    i = 205;
                    break;
                }
            case -176488427:
                if (str2.equals(Theme.key_chat_replyPanelLine)) {
                    i = 175;
                    break;
                }
            case -143547632:
                if (str2.equals(Theme.key_chat_inFileProgressSelected)) {
                    i = 131;
                    break;
                }
            case -127673038:
                if (str2.equals("key_chats_menuTopShadow")) {
                    i = 170;
                    break;
                }
            case -108292334:
                if (str2.equals(Theme.key_chats_menuTopShadow)) {
                    i = 136;
                    break;
                }
            case -71280336:
                if (str2.equals(Theme.key_switchTrackChecked)) {
                    i = 146;
                    break;
                }
            case -65607089:
                if (str2.equals(Theme.key_chats_menuItemIcon)) {
                    i = 6;
                    break;
                }
            case -65277181:
                if (str2.equals(Theme.key_chats_menuItemText)) {
                    i = 37;
                    break;
                }
            case -35597940:
                if (str2.equals(Theme.key_chat_inContactNameText)) {
                    i = 218;
                    break;
                }
            case -18073397:
                if (str2.equals(Theme.key_chats_tabletSelectedOverlay)) {
                    i = 36;
                    break;
                }
            case -12871922:
                if (str2.equals(Theme.key_chat_secretChatStatusText)) {
                    i = 151;
                    break;
                }
            case 6289575:
                if (str2.equals(Theme.key_chat_inLoaderPhotoIconSelected)) {
                    i = 216;
                    break;
                }
            case 27337780:
                if (str2.equals(Theme.key_chats_pinnedOverlay)) {
                    i = 95;
                    break;
                }
            case 49148112:
                if (str2.equals(Theme.key_chat_inPreviewLine)) {
                    i = 153;
                    break;
                }
            case 51359814:
                if (str2.equals(Theme.key_chat_replyPanelMessage)) {
                    i = 75;
                    break;
                }
            case 57332012:
                if (str2.equals(Theme.key_chats_sentCheck)) {
                    i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    break;
                }
            case 57460786:
                if (str2.equals(Theme.key_chats_sentClock)) {
                    i = 203;
                    break;
                }
            case 89466127:
                if (str2.equals(Theme.key_chat_outAudioSeekbarFill)) {
                    i = 67;
                    break;
                }
            case 117743477:
                if (str2.equals(Theme.key_chat_outPreviewLine)) {
                    i = 199;
                    break;
                }
            case 141076636:
                if (str2.equals(Theme.key_groupcreate_spanBackground)) {
                    i = 43;
                    break;
                }
            case 141894978:
                if (str2.equals(Theme.key_windowBackgroundWhiteRedText5)) {
                    i = 194;
                    break;
                }
            case 185438775:
                if (str2.equals(Theme.key_chat_outAudioSelectedProgress)) {
                    i = 182;
                    break;
                }
            case 216441603:
                if (str2.equals(Theme.key_chat_goDownButton)) {
                    i = 114;
                    break;
                }
            case 231486891:
                if (str2.equals(Theme.key_chat_inAudioPerfomerText)) {
                    i = 82;
                    break;
                }
            case 243668262:
                if (str2.equals(Theme.key_chat_inTimeText)) {
                    i = 7;
                    break;
                }
            case 257089712:
                if (str2.equals(Theme.key_chat_outAudioDurationText)) {
                    i = 119;
                    break;
                }
            case 271457747:
                if (str2.equals(Theme.key_chat_inBubbleSelected)) {
                    i = 122;
                    break;
                }
            case 303350244:
                if (str2.equals(Theme.key_chat_reportSpam)) {
                    i = 227;
                    break;
                }
            case 316847509:
                if (str2.equals(Theme.key_chat_outLoaderSelected)) {
                    i = 15;
                    break;
                }
            case 339397761:
                if (str2.equals(Theme.key_windowBackgroundWhiteBlackText)) {
                    i = 246;
                    break;
                }
            case 371859081:
                if (str2.equals(Theme.key_chat_inReplyMediaMessageSelectedText)) {
                    i = 140;
                    break;
                }
            case 415452907:
                if (str2.equals(Theme.key_chat_outAudioDurationSelectedText)) {
                    i = 142;
                    break;
                }
            case 421601145:
                if (str2.equals(Theme.key_chat_emojiPanelIconSelected)) {
                    i = 4;
                    break;
                }
            case 421601469:
                if (str2.equals(Theme.key_chat_emojiPanelIconSelector)) {
                    i = 74;
                    break;
                }
            case 426061980:
                if (str2.equals(Theme.key_chat_serviceBackground)) {
                    i = 147;
                    break;
                }
            case 429680544:
                if (str2.equals(Theme.key_avatar_subtitleInProfileBlue)) {
                    i = 239;
                    break;
                }
            case 429722217:
                if (str2.equals(Theme.key_avatar_subtitleInProfileCyan)) {
                    i = 63;
                    break;
                }
            case 430094524:
                if (str2.equals(Theme.key_avatar_subtitleInProfilePink)) {
                    i = 0;
                    break;
                }
            case 435303214:
                if (str2.equals(Theme.key_actionBarDefaultSubmenuItem)) {
                    i = 198;
                    break;
                }
            case 439976061:
                if (str2.equals(Theme.key_avatar_subtitleInProfileGreen)) {
                    i = 228;
                    break;
                }
            case 444983522:
                if (str2.equals(Theme.key_chat_topPanelClose)) {
                    i = Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
                    break;
                }
            case 446162770:
                if (str2.equals(Theme.key_windowBackgroundWhiteBlueText)) {
                    i = 247;
                    break;
                }
            case 460598594:
                if (str2.equals(Theme.key_chat_topPanelTitle)) {
                    i = 219;
                    break;
                }
            case 484353662:
                if (str2.equals(Theme.key_chat_inVenueInfoText)) {
                    i = 118;
                    break;
                }
            case 503923205:
                if (str2.equals(Theme.key_chat_inSentClockSelected)) {
                    i = 183;
                    break;
                }
            case 527405547:
                if (str2.equals(Theme.key_inappPlayerBackground)) {
                    i = 48;
                    break;
                }
            case 556028747:
                if (str2.equals(Theme.key_chat_outVoiceSeekbarSelected)) {
                    i = 28;
                    break;
                }
            case 589961756:
                if (str2.equals(Theme.key_chat_goDownButtonIcon)) {
                    i = 214;
                    break;
                }
            case 613458991:
                if (str2.equals(Theme.key_dialogTextLink)) {
                    i = 164;
                    break;
                }
            case 626157205:
                if (str2.equals(Theme.key_chat_inVoiceSeekbar)) {
                    i = 34;
                    break;
                }
            case 634019162:
                if (str2.equals(Theme.key_chat_emojiPanelBackspace)) {
                    i = 11;
                    break;
                }
            case 635007317:
                if (str2.equals(Theme.key_chat_inFileProgress)) {
                    i = 242;
                    break;
                }
            case 648238646:
                if (str2.equals(Theme.key_chat_outAudioTitleText)) {
                    i = 59;
                    break;
                }
            case 655457041:
                if (str2.equals(Theme.key_chat_inFileBackgroundSelected)) {
                    i = 234;
                    break;
                }
            case 676996437:
                if (str2.equals(Theme.key_chat_outLocationIcon)) {
                    i = 81;
                    break;
                }
            case 716656587:
                if (str2.equals(Theme.key_avatar_backgroundGroupCreateSpanBlue)) {
                    i = 8;
                    break;
                }
            case 732262561:
                if (str2.equals(Theme.key_chat_outTimeText)) {
                    i = 258;
                    break;
                }
            case 759679774:
                if (str2.equals(Theme.key_chat_outVenueInfoSelectedText)) {
                    i = TsExtractor.TS_STREAM_TYPE_E_AC3;
                    break;
                }
            case 765296599:
                if (str2.equals(Theme.key_chat_outReplyLine)) {
                    i = 254;
                    break;
                }
            case 803672502:
                if (str2.equals(Theme.key_chat_messagePanelIcons)) {
                    i = 70;
                    break;
                }
            case 826015922:
                if (str2.equals(Theme.key_chat_emojiPanelTrendingDescription)) {
                    i = 2;
                    break;
                }
            case 850854541:
                if (str2.equals(Theme.key_chat_inPreviewInstantSelectedText)) {
                    i = 78;
                    break;
                }
            case 890367586:
                if (str2.equals(Theme.key_chat_inViews)) {
                    i = 100;
                    break;
                }
            case 911091978:
                if (str2.equals(Theme.key_chat_outLocationBackground)) {
                    i = 243;
                    break;
                }
            case 913069217:
                if (str2.equals(Theme.key_chat_outMenuSelected)) {
                    i = 252;
                    break;
                }
            case 927863384:
                if (str2.equals(Theme.key_chat_inBubbleShadow)) {
                    i = 184;
                    break;
                }
            case 939137799:
                if (str2.equals(Theme.key_chat_inContactPhoneText)) {
                    i = 188;
                    break;
                }
            case 939824634:
                if (str2.equals(Theme.key_chat_outInstant)) {
                    i = 209;
                    break;
                }
            case 946144034:
                if (str2.equals(Theme.key_windowBackgroundWhiteBlueText4)) {
                    i = 217;
                    break;
                }
            case 962085693:
                if (str2.equals(Theme.key_chats_menuCloudBackgroundCats)) {
                    i = 215;
                    break;
                }
            case 983278580:
                if (str2.equals(Theme.key_avatar_subtitleInProfileOrange)) {
                    i = 261;
                    break;
                }
            case 993048796:
                if (str2.equals(Theme.key_chat_inFileSelectedIcon)) {
                    i = 149;
                    break;
                }
            case 1008947016:
                if (str2.equals(Theme.key_avatar_backgroundActionBarRed)) {
                    i = 231;
                    break;
                }
            case 1020100908:
                if (str2.equals(Theme.key_chat_inAudioSeekbarSelected)) {
                    i = 167;
                    break;
                }
            case 1045892135:
                if (str2.equals(Theme.key_windowBackgroundWhiteGrayIcon)) {
                    i = 186;
                    break;
                }
            case 1046222043:
                if (str2.equals(Theme.key_windowBackgroundWhiteGrayText)) {
                    i = 86;
                    break;
                }
            case 1079427869:
                if (str2.equals(Theme.key_chat_inViewsSelected)) {
                    i = 141;
                    break;
                }
            case 1100033490:
                if (str2.equals(Theme.key_chat_inAudioSelectedProgress)) {
                    i = 115;
                    break;
                }
            case 1106068251:
                if (str2.equals(Theme.key_groupcreate_spanText)) {
                    i = 245;
                    break;
                }
            case 1121079660:
                if (str2.equals(Theme.key_chat_outAudioSeekbar)) {
                    i = PsExtractor.AUDIO_STREAM;
                    break;
                }
            case 1122192435:
                if (str2.equals(Theme.key_chat_outLoaderPhotoSelected)) {
                    i = 220;
                    break;
                }
            case 1175786053:
                if (str2.equals(Theme.key_avatar_subtitleInProfileViolet)) {
                    i = 181;
                    break;
                }
            case 1195322391:
                if (str2.equals(Theme.key_chat_inAudioProgress)) {
                    i = 207;
                    break;
                }
            case 1199344772:
                if (str2.equals(Theme.key_chat_topPanelBackground)) {
                    i = TsExtractor.TS_STREAM_TYPE_SPLICE_INFO;
                    break;
                }
            case 1201609915:
                if (str2.equals(Theme.key_chat_outReplyNameText)) {
                    i = 180;
                    break;
                }
            case 1202885960:
                if (str2.equals(Theme.key_chat_outPreviewInstantSelectedText)) {
                    i = 12;
                    break;
                }
            case 1212117123:
                if (str2.equals(Theme.key_avatar_backgroundActionBarBlue)) {
                    i = 155;
                    break;
                }
            case 1212158796:
                if (str2.equals(Theme.key_avatar_backgroundActionBarCyan)) {
                    i = 249;
                    break;
                }
            case 1212531103:
                if (str2.equals(Theme.key_avatar_backgroundActionBarPink)) {
                    i = 178;
                    break;
                }
            case 1231763334:
                if (str2.equals(Theme.key_chat_addContact)) {
                    i = 22;
                    break;
                }
            case 1239758101:
                if (str2.equals(Theme.key_player_placeholder)) {
                    i = 68;
                    break;
                }
            case 1265168609:
                if (str2.equals(Theme.key_player_actionBarItems)) {
                    i = TsExtractor.TS_STREAM_TYPE_DTS;
                    break;
                }
            case 1269980952:
                if (str2.equals(Theme.key_chat_inBubble)) {
                    i = 13;
                    break;
                }
            case 1275014009:
                if (str2.equals(Theme.key_player_actionBarTitle)) {
                    i = 19;
                    break;
                }
            case 1285554199:
                if (str2.equals(Theme.key_avatar_backgroundActionBarOrange)) {
                    i = 233;
                    break;
                }
            case 1288729698:
                if (str2.equals(Theme.key_chat_unreadMessagesStartArrowIcon)) {
                    i = 53;
                    break;
                }
            case 1308150651:
                if (str2.equals(Theme.key_chat_outFileNameText)) {
                    i = 38;
                    break;
                }
            case 1316752473:
                if (str2.equals(Theme.key_chat_outFileInfoSelectedText)) {
                    i = 14;
                    break;
                }
            case 1327229315:
                if (str2.equals(Theme.key_actionBarDefaultSelector)) {
                    i = 98;
                    break;
                }
            case 1333190005:
                if (str2.equals(Theme.key_chat_outForwardedNameText)) {
                    i = 29;
                    break;
                }
            case 1372411761:
                if (str2.equals(Theme.key_inappPlayerPerformer)) {
                    i = 61;
                    break;
                }
            case 1381159341:
                if (str2.equals(Theme.key_chat_inContactIcon)) {
                    i = 57;
                    break;
                }
            case 1411374187:
                if (str2.equals(Theme.key_chat_messagePanelHint)) {
                    i = 174;
                    break;
                }
            case 1411728145:
                if (str2.equals(Theme.key_chat_messagePanelText)) {
                    i = 253;
                    break;
                }
            case 1414117958:
                if (str2.equals(Theme.key_chat_outSiteNameText)) {
                    i = 121;
                    break;
                }
            case 1449754706:
                if (str2.equals(Theme.key_chat_outContactIcon)) {
                    i = 96;
                    break;
                }
            case 1450167170:
                if (str2.equals(Theme.key_chat_outContactPhoneText)) {
                    i = 117;
                    break;
                }
            case 1456911705:
                if (str2.equals(Theme.key_player_progressBackground)) {
                    i = 31;
                    break;
                }
            case 1478061672:
                if (str2.equals(Theme.key_avatar_backgroundActionBarViolet)) {
                    i = 73;
                    break;
                }
            case 1491567659:
                if (str2.equals("player_seekBarBackground")) {
                    i = 202;
                    break;
                }
            case 1504078167:
                if (str2.equals(Theme.key_chat_outFileSelectedIcon)) {
                    i = 91;
                    break;
                }
            case 1528152827:
                if (str2.equals(Theme.key_chat_inAudioTitleText)) {
                    i = 110;
                    break;
                }
            case 1549064140:
                if (str2.equals(Theme.key_chat_outLoaderPhotoIconSelected)) {
                    i = 191;
                    break;
                }
            case 1573464919:
                if (str2.equals(Theme.key_chat_serviceBackgroundSelected)) {
                    i = 47;
                    break;
                }
            case 1585168289:
                if (str2.equals(Theme.key_chat_inFileIcon)) {
                    i = 109;
                    break;
                }
            case 1595048395:
                if (str2.equals(Theme.key_chat_inAudioDurationText)) {
                    i = 212;
                    break;
                }
            case 1628297471:
                if (str2.equals(Theme.key_chat_messageLinkIn)) {
                    i = 84;
                    break;
                }
            case 1635685130:
                if (str2.equals(Theme.key_profile_verifiedCheck)) {
                    i = 144;
                    break;
                }
            case 1637669025:
                if (str2.equals(Theme.key_chat_messageTextOut)) {
                    i = 58;
                    break;
                }
            case 1647377944:
                if (str2.equals(Theme.key_chat_outViaBotNameText)) {
                    i = 230;
                    break;
                }
            case 1657795113:
                if (str2.equals(Theme.key_chat_outSentCheck)) {
                    i = 251;
                    break;
                }
            case 1657923887:
                if (str2.equals(Theme.key_chat_outSentClock)) {
                    i = 112;
                    break;
                }
            case 1663688926:
                if (str2.equals(Theme.key_chats_attachMessage)) {
                    i = 83;
                    break;
                }
            case 1674274489:
                if (str2.equals(Theme.key_chat_inVenueInfoSelectedText)) {
                    i = 236;
                    break;
                }
            case 1674318617:
                if (str2.equals(Theme.key_divider)) {
                    i = 39;
                    break;
                }
            case 1676443787:
                if (str2.equals(Theme.key_avatar_subtitleInProfileRed)) {
                    i = 80;
                    break;
                }
            case 1682961989:
                if (str2.equals(Theme.key_switchThumbChecked)) {
                    i = 106;
                    break;
                }
            case 1687612836:
                if (str2.equals(Theme.key_actionBarActionModeDefaultIcon)) {
                    i = 237;
                    break;
                }
            case 1714118894:
                if (str2.equals(Theme.key_chat_unreadMessagesStartBackground)) {
                    i = 154;
                    break;
                }
            case 1743255577:
                if (str2.equals(Theme.key_dialogBackgroundGray)) {
                    i = 255;
                    break;
                }
            case 1809914009:
                if (str2.equals(Theme.key_dialogButtonSelector)) {
                    i = 256;
                    break;
                }
            case 1814021667:
                if (str2.equals(Theme.key_chat_inFileInfoText)) {
                    i = 185;
                    break;
                }
            case 1828201066:
                if (str2.equals(Theme.key_dialogTextBlack)) {
                    i = 137;
                    break;
                }
            case 1829565163:
                if (str2.equals(Theme.key_chat_inMenu)) {
                    i = 201;
                    break;
                }
            case 1853943154:
                if (str2.equals(Theme.key_chat_messageTextIn)) {
                    i = 44;
                    break;
                }
            case 1878895888:
                if (str2.equals(Theme.key_avatar_actionBarSelectorBlue)) {
                    i = 41;
                    break;
                }
            case 1878937561:
                if (str2.equals(Theme.key_avatar_actionBarSelectorCyan)) {
                    i = 157;
                    break;
                }
            case 1879309868:
                if (str2.equals(Theme.key_avatar_actionBarSelectorPink)) {
                    i = 221;
                    break;
                }
            case 1921699010:
                if (str2.equals(Theme.key_chats_unreadCounterMuted)) {
                    i = 127;
                    break;
                }
            case 1929729373:
                if (str2.equals(Theme.key_progressCircle)) {
                    i = 126;
                    break;
                }
            case 1930276193:
                if (str2.equals(Theme.key_chat_inTimeSelectedText)) {
                    i = 259;
                    break;
                }
            case 1947549395:
                if (str2.equals(Theme.key_chat_inLoaderPhoto)) {
                    i = 163;
                    break;
                }
            case 1972802227:
                if (str2.equals(Theme.key_chat_outReplyMediaMessageText)) {
                    i = 177;
                    break;
                }
            case 1979989987:
                if (str2.equals(Theme.key_chat_outVenueInfoText)) {
                    i = 257;
                    break;
                }
            case 1994112714:
                if (str2.equals(Theme.key_actionBarActionModeDefaultTop)) {
                    i = 62;
                    break;
                }
            case 2016144760:
                if (str2.equals(Theme.key_chat_outLoaderPhoto)) {
                    i = 45;
                    break;
                }
            case 2016511272:
                if (str2.equals(Theme.key_stickers_menu)) {
                    i = 128;
                    break;
                }
            case 2052611411:
                if (str2.equals(Theme.key_chat_outBubble)) {
                    i = 172;
                    break;
                }
            case 2067556030:
                if (str2.equals(Theme.key_chat_emojiPanelIcon)) {
                    i = 16;
                    break;
                }
            case 2073762588:
                if (str2.equals(Theme.key_chat_outFileIcon)) {
                    i = 46;
                    break;
                }
            case 2090082520:
                if (str2.equals(Theme.key_chats_nameMessage)) {
                    i = 169;
                    break;
                }
            case 2099978769:
                if (str2.equals(Theme.key_chat_outLoaderPhotoIcon)) {
                    i = 52;
                    break;
                }
            case 2109820260:
                if (str2.equals(Theme.key_avatar_actionBarSelectorOrange)) {
                    i = 235;
                    break;
                }
            case 2118871810:
                if (str2.equals(Theme.key_switchThumb)) {
                    i = 35;
                    break;
                }
            case 2119150199:
                if (str2.equals(Theme.key_switchTrack)) {
                    i = 260;
                    break;
                }
            case 2131990258:
                if (str2.equals(Theme.key_windowBackgroundWhiteLinkText)) {
                    i = 120;
                    break;
                }
            case 2133456819:
                if (str2.equals(Theme.key_chat_emojiPanelBackground)) {
                    i = 152;
                    break;
                }
            case 2141345810:
                if (str2.equals(Theme.key_chat_messagePanelBackground)) {
                    i = 102;
                    break;
                }
            default:
        }
        i = -1;
        switch (i) {
            case 0:
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
            case 63:
                return -7697782;
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
            case TLRPC.LAYER /*76*/:
                return -3019777;
            case 77:
                return -723724;
            case 78:
                return -11099429;
            case 79:
                return -5648402;
            case 80:
                return -7697782;
            case 81:
                return -10052929;
            case 82:
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
            case 181:
                return -7697782;
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
            case 228:
                return -7697782;
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
            case 239:
                return -7697782;
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
            case 261:
                return -7697782;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("returning color for key ");
                stringBuilder.append(str2);
                stringBuilder.append(" from current theme");
                FileLog.m4w(stringBuilder.toString());
                return Theme.getColor(str);
        }
    }

    public static Drawable getThemedDrawable(Context context, int i, String str) {
        context = context.getResources().getDrawable(i).mutate();
        context.setColorFilter(new PorterDuffColorFilter(getColor(str), Mode.MULTIPLY));
        return context;
    }
}
