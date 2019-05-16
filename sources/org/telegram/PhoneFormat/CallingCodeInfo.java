package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;

public class CallingCodeInfo {
    public String callingCode = "";
    public ArrayList<String> countries = new ArrayList();
    public ArrayList<String> intlPrefixes = new ArrayList();
    public ArrayList<RuleSet> ruleSets = new ArrayList();
    public ArrayList<String> trunkPrefixes = new ArrayList();

    /* Access modifiers changed, original: 0000 */
    public String matchingAccessCode(String str) {
        Iterator it = this.intlPrefixes.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (str.startsWith(str2)) {
                return str2;
            }
        }
        return null;
    }

    /* Access modifiers changed, original: 0000 */
    public String matchingTrunkCode(String str) {
        Iterator it = this.trunkPrefixes.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (str.startsWith(str2)) {
                return str2;
            }
        }
        return null;
    }

    /* Access modifiers changed, original: 0000 */
    public String format(String str) {
        String str2;
        String substring;
        String format;
        String str3 = null;
        if (str.startsWith(this.callingCode)) {
            str2 = this.callingCode;
            substring = str.substring(str2.length());
        } else {
            str2 = matchingTrunkCode(str);
            if (str2 != null) {
                substring = str.substring(str2.length());
                str3 = str2;
                str2 = null;
            } else {
                substring = str;
                str2 = null;
            }
        }
        Iterator it = this.ruleSets.iterator();
        while (it.hasNext()) {
            format = ((RuleSet) it.next()).format(substring, str2, str3, true);
            if (format != null) {
                return format;
            }
        }
        it = this.ruleSets.iterator();
        while (it.hasNext()) {
            format = ((RuleSet) it.next()).format(substring, str2, str3, false);
            if (format != null) {
                return format;
            }
        }
        if (!(str2 == null || substring.length() == 0)) {
            str = String.format("%s %s", new Object[]{str2, substring});
        }
        return str;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isValidPhoneNumber(String str) {
        String str2;
        String str3 = null;
        if (str.startsWith(this.callingCode)) {
            str2 = this.callingCode;
            str = str.substring(str2.length());
        } else {
            str2 = matchingTrunkCode(str);
            if (str2 != null) {
                str = str.substring(str2.length());
                str3 = str2;
                str2 = null;
            } else {
                str2 = null;
            }
        }
        Iterator it = this.ruleSets.iterator();
        while (it.hasNext()) {
            if (((RuleSet) it.next()).isValid(str, str2, str3, true)) {
                return true;
            }
        }
        it = this.ruleSets.iterator();
        while (it.hasNext()) {
            if (((RuleSet) it.next()).isValid(str, str2, str3, false)) {
                return true;
            }
        }
        return false;
    }
}
