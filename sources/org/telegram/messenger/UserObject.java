package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC;

public class UserObject {
    public static boolean isDeleted(TLRPC.User user) {
        return user == null || (user instanceof TLRPC.TL_userDeleted_old2) || (user instanceof TLRPC.TL_userEmpty) || user.deleted;
    }

    public static boolean isContact(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userContact_old2) || user.contact || user.mutual_contact);
    }

    public static boolean isUserSelf(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userSelf_old3) || user.self);
    }

    public static String getUserName(TLRPC.User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", NUM);
        }
        String formatName = ContactsController.formatName(user.first_name, user.last_name);
        if (formatName.length() != 0 || TextUtils.isEmpty(user.phone)) {
            return formatName;
        }
        PhoneFormat instance = PhoneFormat.getInstance();
        return instance.format("+" + user.phone);
    }

    public static String getFirstName(TLRPC.User user) {
        return getFirstName(user, true);
    }

    public static String getFirstName(TLRPC.User user, boolean z) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String str = user.first_name;
        if (TextUtils.isEmpty(str)) {
            str = user.last_name;
        } else if (!z && str.length() <= 2) {
            return ContactsController.formatName(user.first_name, user.last_name);
        }
        return !TextUtils.isEmpty(str) ? str : LocaleController.getString("HiddenName", NUM);
    }
}
