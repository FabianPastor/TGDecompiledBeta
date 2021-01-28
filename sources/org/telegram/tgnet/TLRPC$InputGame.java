package org.telegram.tgnet;

public abstract class TLRPC$InputGame extends TLObject {
    public long access_hash;
    public TLRPC$InputUser bot_id;
    public long id;
    public String short_name;

    public static TLRPC$InputGame TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputGame tLRPC$InputGame;
        if (i != -NUM) {
            tLRPC$InputGame = i != 53231223 ? null : new TLRPC$TL_inputGameID();
        } else {
            tLRPC$InputGame = new TLRPC$TL_inputGameShortName();
        }
        if (tLRPC$InputGame != null || !z) {
            if (tLRPC$InputGame != null) {
                tLRPC$InputGame.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputGame;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputGame", new Object[]{Integer.valueOf(i)}));
    }
}
