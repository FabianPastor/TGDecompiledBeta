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
    public ArrayList<PhoneRule> rules = new ArrayList<>();

    /* access modifiers changed from: package-private */
    public String format(String str, String intlPrefix, String trunkPrefix, boolean prefixRequired) {
        int length = str.length();
        int i = this.matchLen;
        if (length < i) {
            return null;
        }
        int val = 0;
        Matcher matcher = pattern.matcher(str.substring(0, i));
        if (matcher.find()) {
            val = Integer.parseInt(matcher.group(0));
        }
        Iterator<PhoneRule> it = this.rules.iterator();
        while (it.hasNext()) {
            PhoneRule rule = it.next();
            if (val >= rule.minVal && val <= rule.maxVal && str.length() <= rule.maxLen) {
                if (prefixRequired) {
                    if (((rule.flag12 & 3) == 0 && trunkPrefix == null && intlPrefix == null) || !((trunkPrefix == null || (rule.flag12 & 1) == 0) && (intlPrefix == null || (rule.flag12 & 2) == 0))) {
                        return rule.format(str, intlPrefix, trunkPrefix);
                    }
                } else if ((trunkPrefix == null && intlPrefix == null) || !((trunkPrefix == null || (rule.flag12 & 1) == 0) && (intlPrefix == null || (rule.flag12 & 2) == 0))) {
                    return rule.format(str, intlPrefix, trunkPrefix);
                }
            }
        }
        if (!prefixRequired) {
            if (intlPrefix != null) {
                Iterator<PhoneRule> it2 = this.rules.iterator();
                while (it2.hasNext()) {
                    PhoneRule rule2 = it2.next();
                    if (val >= rule2.minVal && val <= rule2.maxVal && str.length() <= rule2.maxLen) {
                        if (trunkPrefix == null || (rule2.flag12 & 1) != 0) {
                            return rule2.format(str, intlPrefix, trunkPrefix);
                        }
                    }
                }
            } else if (trunkPrefix != null) {
                Iterator<PhoneRule> it3 = this.rules.iterator();
                while (it3.hasNext()) {
                    PhoneRule rule3 = it3.next();
                    if (val >= rule3.minVal && val <= rule3.maxVal && str.length() <= rule3.maxLen) {
                        if (intlPrefix == null || (rule3.flag12 & 2) != 0) {
                            return rule3.format(str, intlPrefix, trunkPrefix);
                        }
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean isValid(String str, String intlPrefix, String trunkPrefix, boolean prefixRequired) {
        int length = str.length();
        int i = this.matchLen;
        if (length < i) {
            return false;
        }
        String begin = str.substring(0, i);
        int val = 0;
        Matcher matcher = pattern.matcher(begin);
        if (matcher.find()) {
            val = Integer.parseInt(matcher.group(0));
        }
        Iterator<PhoneRule> it = this.rules.iterator();
        while (it.hasNext()) {
            PhoneRule rule = it.next();
            if (val >= rule.minVal && val <= rule.maxVal && str.length() == rule.maxLen) {
                if (prefixRequired) {
                    if (((rule.flag12 & 3) == 0 && trunkPrefix == null && intlPrefix == null) || !((trunkPrefix == null || (rule.flag12 & 1) == 0) && (intlPrefix == null || (rule.flag12 & 2) == 0))) {
                        return true;
                    }
                } else if ((trunkPrefix == null && intlPrefix == null) || !((trunkPrefix == null || (rule.flag12 & 1) == 0) && (intlPrefix == null || (rule.flag12 & 2) == 0))) {
                    return true;
                }
            }
        }
        if (!prefixRequired) {
            if (intlPrefix != null && !this.hasRuleWithIntlPrefix) {
                Iterator<PhoneRule> it2 = this.rules.iterator();
                while (it2.hasNext()) {
                    PhoneRule rule2 = it2.next();
                    if (val >= rule2.minVal && val <= rule2.maxVal && str.length() == rule2.maxLen) {
                        if (trunkPrefix == null || (rule2.flag12 & 1) != 0) {
                            return true;
                        }
                    }
                }
            } else if (trunkPrefix != null && !this.hasRuleWithTrunkPrefix) {
                Iterator<PhoneRule> it3 = this.rules.iterator();
                while (it3.hasNext()) {
                    PhoneRule rule3 = it3.next();
                    if (val >= rule3.minVal && val <= rule3.maxVal && str.length() == rule3.maxLen) {
                        if (intlPrefix == null || (rule3.flag12 & 2) != 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
