package org.telegram.tgnet;

public abstract class TLRPC$KeyboardButton extends TLObject {
    public TLRPC$InputUser bot;
    public int button_id;
    public byte[] data;
    public int flags;
    public String fwd_text;
    public TLRPC$InputUser inputUser;
    public String query;
    public boolean quiz;
    public boolean request_write_access;
    public boolean requires_password;
    public boolean same_peer;
    public String text;
    public String url;
    public long user_id;

    public static TLRPC$KeyboardButton TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$KeyboardButton tLRPC$KeyboardButton;
        switch (i) {
            case -1560655744:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButton();
                break;
            case -1344716869:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonBuy();
                break;
            case -1318425559:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonRequestPhone();
                break;
            case -1144565411:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonRequestPoll();
                break;
            case -802258988:
                tLRPC$KeyboardButton = new TLRPC$TL_inputKeyboardButtonUrlAuth();
                break;
            case -376962181:
                tLRPC$KeyboardButton = new TLRPC$TL_inputKeyboardButtonUserProfile();
                break;
            case -59151553:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonRequestGeoLocation();
                break;
            case 90744648:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonSwitchInline();
                break;
            case 280464681:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonUrlAuth();
                break;
            case 629866245:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonUrl();
                break;
            case 814112961:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonUserProfile();
                break;
            case 901503851:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonCallback();
                break;
            case 1358175439:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonGame();
                break;
            case 1748655686:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonCallback_layer117();
                break;
            default:
                tLRPC$KeyboardButton = null;
                break;
        }
        if (tLRPC$KeyboardButton != null || !z) {
            if (tLRPC$KeyboardButton != null) {
                tLRPC$KeyboardButton.readParams(abstractSerializedData, z);
            }
            return tLRPC$KeyboardButton;
        }
        throw new RuntimeException(String.format("can't parse magic %x in KeyboardButton", new Object[]{Integer.valueOf(i)}));
    }
}
