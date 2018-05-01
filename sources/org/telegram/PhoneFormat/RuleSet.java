package org.telegram.PhoneFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleSet {
    public static Pattern pattern = Pattern.compile("[0-9]+");
    public boolean hasRuleWithIntlPrefix;
    public boolean hasRuleWithTrunkPrefix;
    public int matchLen;
    public ArrayList<PhoneRule> rules = new ArrayList();

    String format(String str, String str2, String str3, boolean z) {
        if (str.length() < this.matchLen) {
            return null;
        }
        int i = 0;
        Matcher matcher = pattern.matcher(str.substring(0, this.matchLen));
        if (matcher.find()) {
            i = Integer.parseInt(matcher.group(0));
        }
        Iterator it = this.rules.iterator();
        while (it.hasNext()) {
            PhoneRule phoneRule = (PhoneRule) it.next();
            if (i >= phoneRule.minVal && i <= phoneRule.maxVal && str.length() <= phoneRule.maxLen) {
                if (z) {
                    if (((phoneRule.flag12 & 3) == 0 && str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                        return phoneRule.format(str, str2, str3);
                    }
                } else if ((str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                    return phoneRule.format(str, str2, str3);
                }
            }
        }
        if (!z) {
            PhoneRule phoneRule2;
            if (str2 != null) {
                z = this.rules.iterator();
                while (z.hasNext()) {
                    phoneRule2 = (PhoneRule) z.next();
                    if (i >= phoneRule2.minVal && i <= phoneRule2.maxVal && str.length() <= phoneRule2.maxLen) {
                        if (str3 == null || (phoneRule2.flag12 & 1) != 0) {
                            return phoneRule2.format(str, str2, str3);
                        }
                    }
                }
            } else if (str3 != null) {
                z = this.rules.iterator();
                while (z.hasNext()) {
                    phoneRule2 = (PhoneRule) z.next();
                    if (i >= phoneRule2.minVal && i <= phoneRule2.maxVal && str.length() <= phoneRule2.maxLen) {
                        if (str2 == null || (phoneRule2.flag12 & 2) != 0) {
                            return phoneRule2.format(str, str2, str3);
                        }
                    }
                }
            }
        }
        return null;
    }

    boolean isValid(String str, String str2, String str3, boolean z) {
        if (str.length() < this.matchLen) {
            return false;
        }
        Matcher matcher = pattern.matcher(str.substring(0, this.matchLen));
        int parseInt = matcher.find() ? Integer.parseInt(matcher.group(0)) : 0;
        Iterator it = this.rules.iterator();
        while (it.hasNext()) {
            PhoneRule phoneRule = (PhoneRule) it.next();
            if (parseInt >= phoneRule.minVal && parseInt <= phoneRule.maxVal && str.length() == phoneRule.maxLen) {
                if (z) {
                    if (((phoneRule.flag12 & 3) == 0 && str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                        return true;
                    }
                } else if ((str3 == null && str2 == null) || !((str3 == null || (phoneRule.flag12 & 1) == 0) && (str2 == null || (phoneRule.flag12 & 2) == 0))) {
                    return true;
                }
            }
        }
        if (!z) {
            PhoneRule phoneRule2;
            if (str2 != null && !this.hasRuleWithIntlPrefix) {
                str2 = this.rules.iterator();
                while (str2.hasNext()) {
                    phoneRule2 = (PhoneRule) str2.next();
                    if (parseInt >= phoneRule2.minVal && parseInt <= phoneRule2.maxVal && str.length() == phoneRule2.maxLen) {
                        if (str3 == null || (phoneRule2.flag12 & true)) {
                            return true;
                        }
                    }
                }
            } else if (str3 != null && this.hasRuleWithTrunkPrefix == null) {
                str3 = this.rules.iterator();
                while (str3.hasNext()) {
                    phoneRule2 = (PhoneRule) str3.next();
                    if (parseInt >= phoneRule2.minVal && parseInt <= phoneRule2.maxVal && str.length() == phoneRule2.maxLen) {
                        if (str2 == null || (phoneRule2.flag12 & 2)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
