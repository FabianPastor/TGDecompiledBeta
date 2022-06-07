package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$User extends TLObject {
    public long access_hash;
    public boolean apply_min_photo;
    public boolean bot;
    public boolean bot_attach_menu;
    public boolean bot_chat_history;
    public int bot_info_version;
    public boolean bot_inline_geo;
    public String bot_inline_placeholder;
    public boolean bot_menu_webview;
    public boolean bot_nochats;
    public boolean contact;
    public boolean deleted;
    public boolean explicit_content;
    public boolean fake;
    public String first_name;
    public int flags;
    public long id;
    public boolean inactive;
    public String lang_code;
    public String last_name;
    public boolean min;
    public boolean mutual_contact;
    public String phone;
    public TLRPC$UserProfilePhoto photo;
    public boolean premium;
    public boolean restricted;
    public ArrayList<TLRPC$TL_restrictionReason> restriction_reason = new ArrayList<>();
    public boolean scam;
    public boolean self;
    public TLRPC$UserStatus status;
    public boolean support;
    public String username;
    public boolean verified;

    public static TLRPC$User TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$User tLRPC$User;
        switch (i) {
            case -1820043071:
                tLRPC$User = new TLRPC$TL_user_layer131();
                break;
            case -1298475060:
                tLRPC$User = new TLRPC$TL_userDeleted_old();
                break;
            case -894214632:
                tLRPC$User = new TLRPC$TL_userContact_old2();
                break;
            case -787638374:
                tLRPC$User = new TLRPC$TL_user_layer65();
                break;
            case -742634630:
                tLRPC$User = new TLRPC$TL_userEmpty();
                break;
            case -704549510:
                tLRPC$User = new TLRPC$TL_userDeleted_old2();
                break;
            case -640891665:
                tLRPC$User = new TLRPC$TL_userRequest_old2();
                break;
            case -218397927:
                tLRPC$User = new TLRPC$TL_userContact_old();
                break;
            case 123533224:
                tLRPC$User = new TLRPC$TL_userForeign_old2();
                break;
            case 476112392:
                tLRPC$User = new TLRPC$TL_userSelf_old3();
                break;
            case 537022650:
                tLRPC$User = new TLRPC$TL_userEmpty_layer131();
                break;
            case 585404530:
                tLRPC$User = new TLRPC$TL_user_old();
                break;
            case 585682608:
                tLRPC$User = new TLRPC$TL_userRequest_old();
                break;
            case 773059779:
                tLRPC$User = new TLRPC$TL_user_layer104();
                break;
            case 1073147056:
                tLRPC$User = new TLRPC$TL_user();
                break;
            case 1377093789:
                tLRPC$User = new TLRPC$TL_userForeign_old();
                break;
            case 1879553105:
                tLRPC$User = new TLRPC$TL_userSelf_old2();
                break;
            case 1912944108:
                tLRPC$User = new TLRPC$TL_userSelf_old();
                break;
            default:
                tLRPC$User = null;
                break;
        }
        if (tLRPC$User != null || !z) {
            if (tLRPC$User != null) {
                tLRPC$User.readParams(abstractSerializedData, z);
            }
            return tLRPC$User;
        }
        throw new RuntimeException(String.format("can't parse magic %x in User", new Object[]{Integer.valueOf(i)}));
    }
}
