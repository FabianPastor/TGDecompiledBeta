package org.telegram.PhoneFormat;

public class PhoneRule {
    public int byte8;
    public int flag12;
    public int flag13;
    public String format;
    public boolean hasIntlPrefix;
    public boolean hasTrunkPrefix;
    public int maxLen;
    public int maxVal;
    public int minVal;
    public int otherFlag;
    public int prefixLen;

    String format(String str, String intlPrefix, String trunkPrefix) {
        int spot = 0;
        StringBuilder res = new StringBuilder(20);
        boolean hasOpen = false;
        boolean hadN = false;
        boolean hadC = false;
        int i = 0;
        while (i < this.format.length()) {
            char ch = this.format.charAt(i);
            if (ch != '#') {
                if (ch != '(') {
                    if (ch == 'c') {
                        hadC = true;
                        if (intlPrefix != null) {
                            res.append(intlPrefix);
                        }
                    } else if (ch == 'n') {
                        hadN = true;
                        if (trunkPrefix != null) {
                            res.append(trunkPrefix);
                        }
                    }
                } else if (spot < str.length()) {
                    hasOpen = true;
                }
                if (!(ch == ' ' && i > 0 && ((this.format.charAt(i - 1) == 'n' && trunkPrefix == null) || (this.format.charAt(i - 1) == 'c' && intlPrefix == null))) && (spot < str.length() || (hasOpen && ch == ')'))) {
                    res.append(this.format.substring(i, i + 1));
                    if (ch == ')') {
                        hasOpen = false;
                    }
                }
            } else if (spot < str.length()) {
                res.append(str.substring(spot, spot + 1));
                spot++;
            } else if (hasOpen) {
                res.append(" ");
            }
            i++;
        }
        if (intlPrefix != null && !hadC) {
            res.insert(0, String.format("%s ", new Object[]{intlPrefix}));
        } else if (!(trunkPrefix == null || hadN)) {
            res.insert(0, trunkPrefix);
        }
        return res.toString();
    }

    boolean hasIntlPrefix() {
        return (this.flag12 & 2) != 0;
    }

    boolean hasTrunkPrefix() {
        return (this.flag12 & 1) != 0;
    }
}
