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

    /* access modifiers changed from: package-private */
    public String format(String str, String intlPrefix, String trunkPrefix) {
        boolean hadC = false;
        boolean hadN = false;
        boolean hasOpen = false;
        int spot = 0;
        StringBuilder res = new StringBuilder(20);
        for (int i = 0; i < this.format.length(); i++) {
            char ch = this.format.charAt(i);
            switch (ch) {
                case '#':
                    if (spot >= str.length()) {
                        if (!hasOpen) {
                            break;
                        } else {
                            res.append(" ");
                            break;
                        }
                    } else {
                        res.append(str.substring(spot, spot + 1));
                        spot++;
                        continue;
                    }
                case '(':
                    if (spot < str.length()) {
                        hasOpen = true;
                        break;
                    }
                    break;
                case 'c':
                    hadC = true;
                    if (intlPrefix != null) {
                        res.append(intlPrefix);
                        break;
                    } else {
                        continue;
                    }
                case 'n':
                    hadN = true;
                    if (trunkPrefix != null) {
                        res.append(trunkPrefix);
                        break;
                    } else {
                        continue;
                    }
            }
            if (!(ch == ' ' && i > 0 && ((this.format.charAt(i - 1) == 'n' && trunkPrefix == null) || (this.format.charAt(i - 1) == 'c' && intlPrefix == null))) && (spot < str.length() || (hasOpen && ch == ')'))) {
                res.append(this.format.substring(i, i + 1));
                if (ch == ')') {
                    hasOpen = false;
                }
            }
        }
        if (intlPrefix != null && !hadC) {
            res.insert(0, String.format("%s ", new Object[]{intlPrefix}));
        } else if (trunkPrefix != null && !hadN) {
            res.insert(0, trunkPrefix);
        }
        return res.toString();
    }

    /* access modifiers changed from: package-private */
    public boolean hasIntlPrefix() {
        return (this.flag12 & 2) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean hasTrunkPrefix() {
        return (this.flag12 & 1) != 0;
    }
}
