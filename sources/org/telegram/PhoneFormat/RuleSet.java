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

    /* Access modifiers changed, original: 0000 */
    public String format(String str, String str2, String str3, boolean z) {
        int length = str.length();
        int i = this.matchLen;
        if (length >= i) {
            length = 0;
            Matcher matcher = pattern.matcher(str.substring(0, i));
            if (matcher.find()) {
                length = Integer.parseInt(matcher.group(0));
            }
            Iterator it = this.rules.iterator();
            while (it.hasNext()) {
                PhoneRule phoneRule = (PhoneRule) it.next();
                if (length >= phoneRule.minVal && length <= phoneRule.maxVal && str.length() <= phoneRule.maxLen) {
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
                Iterator it2;
                PhoneRule phoneRule2;
                if (str2 != null) {
                    it2 = this.rules.iterator();
                    while (it2.hasNext()) {
                        phoneRule2 = (PhoneRule) it2.next();
                        if (length >= phoneRule2.minVal && length <= phoneRule2.maxVal && str.length() <= phoneRule2.maxLen) {
                            if (str3 == null || (phoneRule2.flag12 & 1) != 0) {
                                return phoneRule2.format(str, str2, str3);
                            }
                        }
                    }
                } else if (str3 != null) {
                    it2 = this.rules.iterator();
                    while (it2.hasNext()) {
                        phoneRule2 = (PhoneRule) it2.next();
                        if (length >= phoneRule2.minVal && length <= phoneRule2.maxVal && str.length() <= phoneRule2.maxLen) {
                            if (str2 == null || (phoneRule2.flag12 & 2) != 0) {
                                return phoneRule2.format(str, str2, str3);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isValid(String str, String str2, String str3, boolean z) {
        int length = str.length();
        int i = this.matchLen;
        if (length >= i) {
            Matcher matcher = pattern.matcher(str.substring(0, i));
            length = matcher.find() ? Integer.parseInt(matcher.group(0)) : 0;
            Iterator it = this.rules.iterator();
            while (it.hasNext()) {
                PhoneRule phoneRule = (PhoneRule) it.next();
                if (length >= phoneRule.minVal && length <= phoneRule.maxVal && str.length() == phoneRule.maxLen) {
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
                    Iterator it2 = this.rules.iterator();
                    while (it2.hasNext()) {
                        phoneRule2 = (PhoneRule) it2.next();
                        if (length >= phoneRule2.minVal && length <= phoneRule2.maxVal && str.length() == phoneRule2.maxLen) {
                            if (str3 == null || (phoneRule2.flag12 & 1) != 0) {
                                return true;
                            }
                        }
                    }
                } else if (!(str3 == null || this.hasRuleWithTrunkPrefix)) {
                    Iterator it3 = this.rules.iterator();
                    while (it3.hasNext()) {
                        phoneRule2 = (PhoneRule) it3.next();
                        if (length >= phoneRule2.minVal && length <= phoneRule2.maxVal && str.length() == phoneRule2.maxLen) {
                            if (str2 == null || (phoneRule2.flag12 & 2) != 0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
