package org.telegram.tgnet;

public abstract class TLRPC$BotInlineResult extends TLObject {
    public TLRPC$WebDocument content;
    public String description;
    public TLRPC$Document document;
    public int flags;
    public String id;
    public TLRPC$Photo photo;
    public long query_id;
    public TLRPC$BotInlineMessage send_message;
    public TLRPC$WebDocument thumb;
    public String title;
    public String type;
    public String url;

    public static TLRPC$BotInlineResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult;
        if (i != NUM) {
            tLRPC$BotInlineResult = i != NUM ? null : new TLRPC$TL_botInlineMediaResult();
        } else {
            tLRPC$BotInlineResult = new TLRPC$TL_botInlineResult();
        }
        if (tLRPC$BotInlineResult != null || !z) {
            if (tLRPC$BotInlineResult != null) {
                tLRPC$BotInlineResult.readParams(abstractSerializedData, z);
            }
            return tLRPC$BotInlineResult;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotInlineResult", new Object[]{Integer.valueOf(i)}));
    }
}
