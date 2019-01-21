package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.beta.R;
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
            return LocaleController.getString("HiddenName", R.string.HiddenName);
        }
        String name = ContactsController.formatName(user.first_name, user.last_name);
        return (name.length() != 0 || user.phone == null || user.phone.length() == 0) ? name : PhoneFormat.getInstance().format("+" + user.phone);
    }

    public static String getFirstName(User user) {
        return getFirstName(user, true);
    }

    public static String getFirstName(User user, boolean allowShort) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String name = user.first_name;
        if (TextUtils.isEmpty(name)) {
            name = user.last_name;
        } else if (!allowShort && name.length() <= 2) {
            return ContactsController.formatName(user.first_name, user.last_name);
        }
        return TextUtils.isEmpty(name) ? LocaleController.getString("HiddenName", R.string.HiddenName) : name;
    }
}
