package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_userDeleted_old2;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userSelf_old3;
import org.telegram.tgnet.TLRPC.User;

public class UserObject {
    public static boolean isDeleted(User user) {
        return user == null || (user instanceof TL_userDeleted_old2) || (user instanceof TL_userEmpty) || user.deleted;
    }

    public static boolean isContact(User user) {
        return user != null && ((user instanceof TL_userContact_old2) || user.contact || user.mutual_contact);
    }

    public static boolean isUserSelf(User user) {
        return user != null && ((user instanceof TL_userSelf_old3) || user.self);
    }

    public static String getUserName(User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", NUM);
        }
        String formatName = ContactsController.formatName(user.first_name, user.last_name);
        if (formatName.length() == 0) {
            String str = user.phone;
            if (!(str == null || str.length() == 0)) {
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(user.phone);
                formatName = instance.format(stringBuilder.toString());
            }
        }
        return formatName;
    }

    public static String getFirstName(User user) {
        return getFirstName(user, true);
    }

    public static String getFirstName(User user, boolean z) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String str = user.first_name;
        if (TextUtils.isEmpty(str)) {
            str = user.last_name;
        } else if (!z && str.length() <= 2) {
            return ContactsController.formatName(user.first_name, user.last_name);
        }
        if (TextUtils.isEmpty(str)) {
            str = LocaleController.getString("HiddenName", NUM);
        }
        return str;
    }
}
