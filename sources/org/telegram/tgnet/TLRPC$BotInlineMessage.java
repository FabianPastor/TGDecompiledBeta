package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$BotInlineMessage extends TLObject {
    public String address;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public String first_name;
    public int flags;
    public TLRPC$GeoPoint geo;
    public int heading;
    public String last_name;
    public String message;
    public boolean no_webpage;
    public int period;
    public String phone_number;
    public String provider;
    public int proximity_notification_radius;
    public TLRPC$ReplyMarkup reply_markup;
    public String title;
    public String vcard;
    public String venue_id;
    public String venue_type;

    public static TLRPC$BotInlineMessage TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$BotInlineMessage tLRPC$BotInlineMessage;
        switch (i) {
            case -1970903652:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaVenue();
                break;
            case -1937807902:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageText();
                break;
            case -1222451611:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaGeo_layer119();
                break;
            case 85477117:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaGeo();
                break;
            case 175419739:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaAuto_layer74();
                break;
            case 416402882:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaContact();
                break;
            case 894081801:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaInvoice();
                break;
            case 904770772:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaContact_layer81();
                break;
            case 982505656:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaGeo_layer71();
                break;
            case 1130767150:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaVenue_layer77();
                break;
            case 1984755728:
                tLRPC$BotInlineMessage = new TLRPC$TL_botInlineMessageMediaAuto();
                break;
            default:
                tLRPC$BotInlineMessage = null;
                break;
        }
        if (tLRPC$BotInlineMessage != null || !z) {
            if (tLRPC$BotInlineMessage != null) {
                tLRPC$BotInlineMessage.readParams(abstractSerializedData, z);
            }
            return tLRPC$BotInlineMessage;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotInlineMessage", new Object[]{Integer.valueOf(i)}));
    }
}
