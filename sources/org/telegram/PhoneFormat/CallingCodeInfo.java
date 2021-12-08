package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;

public class CallingCodeInfo {
    public String callingCode = "";
    public ArrayList<String> countries = new ArrayList<>();
    public ArrayList<String> intlPrefixes = new ArrayList<>();
    public ArrayList<RuleSet> ruleSets = new ArrayList<>();
    public ArrayList<String> trunkPrefixes = new ArrayList<>();

    /* access modifiers changed from: package-private */
    public String matchingAccessCode(String str) {
        Iterator<String> it = this.intlPrefixes.iterator();
        while (it.hasNext()) {
            String code = it.next();
            if (str.startsWith(code)) {
                return code;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public String matchingTrunkCode(String str) {
        Iterator<String> it = this.trunkPrefixes.iterator();
        while (it.hasNext()) {
            String code = it.next();
            if (str.startsWith(code)) {
                return code;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public String format(String orig) {
        String str = orig;
        String trunkPrefix = null;
        String intlPrefix = null;
        if (str.startsWith(this.callingCode)) {
            intlPrefix = this.callingCode;
            str = str.substring(intlPrefix.length());
        } else {
            String trunk = matchingTrunkCode(str);
            if (trunk != null) {
                trunkPrefix = trunk;
                str = str.substring(trunkPrefix.length());
            }
        }
        Iterator<RuleSet> it = this.ruleSets.iterator();
        while (it.hasNext()) {
            String phone = it.next().format(str, intlPrefix, trunkPrefix, true);
            if (phone != null) {
                return phone;
            }
        }
        Iterator<RuleSet> it2 = this.ruleSets.iterator();
        while (it2.hasNext()) {
            String phone2 = it2.next().format(str, intlPrefix, trunkPrefix, false);
            if (phone2 != null) {
                return phone2;
            }
        }
        if (intlPrefix == null || str.length() == 0) {
            return orig;
        }
        return String.format("%s %s", new Object[]{intlPrefix, str});
    }

    /* access modifiers changed from: package-private */
    public boolean isValidPhoneNumber(String orig) {
        String str = orig;
        String trunkPrefix = null;
        String intlPrefix = null;
        if (str.startsWith(this.callingCode)) {
            intlPrefix = this.callingCode;
            str = str.substring(intlPrefix.length());
        } else {
            String trunk = matchingTrunkCode(str);
            if (trunk != null) {
                trunkPrefix = trunk;
                str = str.substring(trunkPrefix.length());
            }
        }
        Iterator<RuleSet> it = this.ruleSets.iterator();
        while (it.hasNext()) {
            if (it.next().isValid(str, intlPrefix, trunkPrefix, true)) {
                return true;
            }
        }
        Iterator<RuleSet> it2 = this.ruleSets.iterator();
        while (it2.hasNext()) {
            if (it2.next().isValid(str, intlPrefix, trunkPrefix, false)) {
                return true;
            }
        }
        return false;
    }
}
