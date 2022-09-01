package org.telegram.tgnet;

public abstract class TLRPC$account_Password extends TLObject {
    public TLRPC$PasswordKdfAlgo current_algo;
    public String email_unconfirmed_pattern;
    public int flags;
    public boolean has_password;
    public boolean has_recovery;
    public boolean has_secure_values;
    public String hint;
    public String login_email_pattern;
    public TLRPC$PasswordKdfAlgo new_algo;
    public TLRPC$SecurePasswordKdfAlgo new_secure_algo;
    public int pending_reset_date;
    public byte[] secure_random;
    public byte[] srp_B;
    public long srp_id;

    public static TLRPC$account_Password TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_Password tLRPC$account_Password;
        if (i != -NUM) {
            tLRPC$account_Password = i != NUM ? null : new TLRPC$TL_account_password_layer144();
        } else {
            tLRPC$account_Password = new TLRPC$TL_account_password();
        }
        if (tLRPC$account_Password != null || !z) {
            if (tLRPC$account_Password != null) {
                tLRPC$account_Password.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_Password;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_Password", new Object[]{Integer.valueOf(i)}));
    }
}
