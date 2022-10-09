package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class CallingCodeInfo {
    public String callingCode;
    public ArrayList<String> intlPrefixes;
    public ArrayList<RuleSet> ruleSets;
    public ArrayList<String> trunkPrefixes;

    public CallingCodeInfo() {
        new ArrayList();
        this.callingCode = "";
        this.trunkPrefixes = new ArrayList<>();
        this.intlPrefixes = new ArrayList<>();
        this.ruleSets = new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String matchingAccessCode(String str) {
        Iterator<String> it = this.intlPrefixes.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (str.startsWith(next)) {
                return next;
            }
        }
        return null;
    }

    String matchingTrunkCode(String str) {
        Iterator<String> it = this.trunkPrefixes.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (str.startsWith(next)) {
                return next;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String format(String str) {
        String str2;
        String str3;
        String str4 = null;
        if (str.startsWith(this.callingCode)) {
            str3 = this.callingCode;
            str2 = str.substring(str3.length());
        } else {
            String matchingTrunkCode = matchingTrunkCode(str);
            if (matchingTrunkCode != null) {
                str2 = str.substring(matchingTrunkCode.length());
                str4 = matchingTrunkCode;
                str3 = null;
            } else {
                str2 = str;
                str3 = null;
            }
        }
        Iterator<RuleSet> it = this.ruleSets.iterator();
        while (it.hasNext()) {
            String format = it.next().format(str2, str3, str4, true);
            if (format != null) {
                return format;
            }
        }
        Iterator<RuleSet> it2 = this.ruleSets.iterator();
        while (it2.hasNext()) {
            String format2 = it2.next().format(str2, str3, str4, false);
            if (format2 != null) {
                return format2;
            }
        }
        return (str3 == null || str2.length() == 0) ? str : String.format("%s %s", str3, str2);
    }
}
