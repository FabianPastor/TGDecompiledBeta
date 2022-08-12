package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC$TL_userContact_old2;
import org.telegram.tgnet.TLRPC$TL_userDeleted_old2;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$TL_userSelf_old3;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;

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

    public static boolean isReplyUser(TLRPC$User tLRPC$User) {
        if (tLRPC$User != null) {
            long j = tLRPC$User.id;
            if (j == 708513 || j == NUM) {
                return true;
            }
        }
        return false;
    }

    public static String getUserName(TLRPC$User tLRPC$User) {
        if (tLRPC$User == null || isDeleted(tLRPC$User)) {
            return LocaleController.getString("HiddenName", R.string.HiddenName);
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
        return !TextUtils.isEmpty(str) ? str : LocaleController.getString("HiddenName", R.string.HiddenName);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r0.photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean hasPhoto(org.telegram.tgnet.TLRPC$User r0) {
        /*
            if (r0 == 0) goto L_0x000c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            if (r0 == 0) goto L_0x000c
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_userProfilePhotoEmpty
            if (r0 != 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.UserObject.hasPhoto(org.telegram.tgnet.TLRPC$User):boolean");
    }

    public static TLRPC$UserProfilePhoto getPhoto(TLRPC$User tLRPC$User) {
        if (hasPhoto(tLRPC$User)) {
            return tLRPC$User.photo;
        }
        return null;
    }
}
