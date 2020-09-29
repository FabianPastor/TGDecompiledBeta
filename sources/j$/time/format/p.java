package j$.time.format;

import j$.time.f;

final class p implements CLASSNAMEj {
    private final CLASSNAMEj a;
    private final int b;
    private final char c;

    p(CLASSNAMEj printerParser, int padWidth, char padChar) {
        this.a = printerParser;
        this.b = padWidth;
        this.c = padChar;
    }

    public boolean i(C context, StringBuilder buf) {
        int preLen = buf.length();
        if (!this.a.i(context, buf)) {
            return false;
        }
        int len = buf.length() - preLen;
        if (len <= this.b) {
            for (int i = 0; i < this.b - len; i++) {
                buf.insert(preLen, this.c);
            }
            return true;
        }
        throw new f("Cannot print as output of " + len + " characters exceeds pad width of " + this.b);
    }

    public int p(A context, CharSequence text, int position) {
        boolean strict = context.l();
        if (position > text.length()) {
            throw new IndexOutOfBoundsException();
        } else if (position == text.length()) {
            return position ^ -1;
        } else {
            int endPos = this.b + position;
            if (endPos > text.length()) {
                if (strict) {
                    return position ^ -1;
                }
                endPos = text.length();
            }
            int pos = position;
            while (pos < endPos && context.b(text.charAt(pos), this.c)) {
                pos++;
            }
            int resultPos = this.a.p(context, text.subSequence(0, endPos), pos);
            if (resultPos == endPos || !strict) {
                return resultPos;
            }
            return (position + pos) ^ -1;
        }
    }

    public String toString() {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append("Pad(");
        sb.append(this.a);
        sb.append(",");
        sb.append(this.b);
        if (this.c == ' ') {
            str = ")";
        } else {
            str = ",'" + this.c + "')";
        }
        sb.append(str);
        return sb.toString();
    }
}
