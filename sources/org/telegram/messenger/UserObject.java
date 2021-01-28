package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC$TL_userContact_old2;
import org.telegram.tgnet.TLRPC$TL_userDeleted_old2;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$TL_userSelf_old3;
import org.telegram.tgnet.TLRPC$User;

public class UserObject {
    public static boolean isReplyUser(long j) {
        return j == 708513 || j == NUM;
    }

    public static boolean isDeleted(TLRPC$User tLRPC$User) {
        return tLRPC$User == null || (tLRPC$User instanceof TLRPC$TL_userDeleted_old2) || (tLRPC$User instanceof TLRPC$TL_userEmpty) || tLRPC$User.deleted;
    }

    public static boolean isContact(TLRPC$User tLRPC$User) {
        return tLRPC$User != null && ((tLRPC$User instanceof TLRPC$TL_userContact_old2) || tLRPC$User.contact || tLRPC$User.mutual_contact);
    }

    public static boolean isUserSelf(TLRPC$User tLRPC$User) {
        return tLRPC$User != null && ((tLRPC$User instanceof TLRPC$TL_userSelf_old3) || tLRPC$User.self);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r1 = r1.id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isReplyUser(org.telegram.tgnet.TLRPC$User r1) {
        /*
            if (r1 == 0) goto L_0x0010
            int r1 = r1.id
            r0 = 708513(0xacfa1, float:9.92838E-40)
            if (r1 == r0) goto L_0x000e
            r0 = 1271266957(0x4bc5fe8d, float:2.5951514E7)
            if (r1 != r0) goto L_0x0010
        L_0x000e:
            r1 = 1
            goto L_0x0011
        L_0x0010:
            r1 = 0
        L_0x0011:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserObject.isReplyUser(org.telegram.tgnet.TLRPC$User):boolean");
    }

    public static String getUserName(TLRPC$User tLRPC$User) {
        if (tLRPC$User == null || isDeleted(tLRPC$User)) {
            return LocaleController.getString("HiddenName", NUM);
        }
        String formatName = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
        if (formatName.length() != 0 || TextUtils.isEmpty(tLRPC$User.phone)) {
            return formatName;
        }
        PhoneFormat instance = PhoneFormat.getInstance();
        return instance.format("+" + tLRPC$User.phone);
    }

    public static String getFirstName(TLRPC$User tLRPC$User) {
        return getFirstName(tLRPC$User, true);
    }

    public static String getFirstName(TLRPC$User tLRPC$User, boolean z) {
        if (tLRPC$User == null || isDeleted(tLRPC$User)) {
            return "DELETED";
        }
        String str = tLRPC$User.first_name;
        if (TextUtils.isEmpty(str)) {
            str = tLRPC$User.last_name;
        } else if (!z && str.length() <= 2) {
            return ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
        }
        return !TextUtils.isEmpty(str) ? str : LocaleController.getString("HiddenName", NUM);
    }
}
