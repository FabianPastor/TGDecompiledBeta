package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$BotInfo extends TLObject {
    public ArrayList<TLRPC$TL_botCommand> commands = new ArrayList<>();
    public String description;
    public int user_id;
    public int version;

    public static TLRPC$BotInfo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_botInfo tLRPC$TL_botInfo;
        if (i == -NUM) {
            tLRPC$TL_botInfo = new TLRPC$TL_botInfo();
        } else if (i != -NUM) {
            tLRPC$TL_botInfo = i != NUM ? null : new TLRPC$TL_botInfo_layer48();
        } else {
            tLRPC$TL_botInfo = new TLRPC$TL_botInfoEmpty_layer48();
        }
        if (tLRPC$TL_botInfo != null || !z) {
            if (tLRPC$TL_botInfo != null) {
                tLRPC$TL_botInfo.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_botInfo;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotInfo", new Object[]{Integer.valueOf(i)}));
    }
}
