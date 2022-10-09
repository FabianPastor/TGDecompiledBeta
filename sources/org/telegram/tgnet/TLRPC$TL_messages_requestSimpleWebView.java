package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_requestSimpleWebView extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputUser bot;
    public int flags;
    public String platform;
    public TLRPC$TL_dataJSON theme_params;
    public String url;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_simpleWebViewResultUrl.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.bot.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.url);
        if ((this.flags & 1) != 0) {
            this.theme_params.serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.platform);
    }
}
