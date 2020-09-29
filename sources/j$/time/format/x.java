package j$.time.format;

import j$.time.f;
import j$.time.o;
import j$.time.p;
import j$.time.u.D;
import j$.time.u.j;
import j$.time.v.g;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatterBuilder;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class x implements CLASSNAMEj {
    private static volatile Map.Entry c;
    private static volatile Map.Entry d;
    private final D a;
    private final String b;

    x(D d2, String description) {
        this.a = d2;
        this.b = description;
    }

    public boolean i(C context, StringBuilder buf) {
        o zone = (o) context.g(this.a);
        if (zone == null) {
            return false;
        }
        buf.append(zone.getId());
        return true;
    }

    /* access modifiers changed from: protected */
    public r a(A context) {
        Set<String> regionIds = g.a();
        int regionIdsSize = ((HashSet) regionIds).size();
        Map.Entry<Integer, DateTimeFormatterBuilder.PrefixTree> cached = context.k() ? c : d;
        if (cached == null || cached.getKey().intValue() != regionIdsSize) {
            synchronized (this) {
                Map.Entry<Integer, DateTimeFormatterBuilder.PrefixTree> cached2 = context.k() ? c : d;
                if (cached2 == null || cached2.getKey().intValue() != regionIdsSize) {
                    cached2 = new AbstractMap.SimpleImmutableEntry<>(Integer.valueOf(regionIdsSize), r.g(regionIds, context));
                    if (context.k()) {
                        c = cached2;
                    } else {
                        d = cached2;
                    }
                }
            }
        }
        return (r) cached.getValue();
    }

    public int p(A context, CharSequence text, int position) {
        int length = text.length();
        if (position > length) {
            throw new IndexOutOfBoundsException();
        } else if (position == length) {
            return position ^ -1;
        } else {
            char nextChar = text.charAt(position);
            if (nextChar == '+' || nextChar == '-') {
                return b(context, text, position, position, o.d);
            }
            if (length >= position + 2) {
                char nextNextChar = text.charAt(position + 1);
                if (!context.b(nextChar, 'U') || !context.b(nextNextChar, 'T')) {
                    if (context.b(nextChar, 'G') && length >= position + 3 && context.b(nextNextChar, 'M') && context.b(text.charAt(position + 2), 'T')) {
                        return b(context, text, position, position + 3, o.e);
                    }
                } else if (length < position + 3 || !context.b(text.charAt(position + 2), 'C')) {
                    return b(context, text, position, position + 2, o.e);
                } else {
                    return b(context, text, position, position + 3, o.e);
                }
            }
            r tree = a(context);
            ParsePosition ppos = new ParsePosition(position);
            String parsedZoneId = tree.d(text, ppos);
            if (parsedZoneId != null) {
                context.n(o.L(parsedZoneId));
                return ppos.getIndex();
            } else if (!context.b(nextChar, 'Z')) {
                return position ^ -1;
            } else {
                context.n(p.f);
                return position + 1;
            }
        }
    }

    private int b(A context, CharSequence text, int prefixPos, int position, o parser) {
        String prefix = text.toString().substring(prefixPos, position).toUpperCase();
        if (position >= text.length()) {
            context.n(o.L(prefix));
            return position;
        } else if (text.charAt(position) == '0' || context.b(text.charAt(position), 'Z')) {
            context.n(o.L(prefix));
            return position;
        } else {
            A newContext = context.d();
            int endPos = parser.p(newContext, text, position);
            if (endPos < 0) {
                try {
                    if (parser == o.d) {
                        return prefixPos ^ -1;
                    }
                    context.n(o.L(prefix));
                    return position;
                } catch (f e) {
                    return prefixPos ^ -1;
                }
            } else {
                context.n(o.P(prefix, p.X((int) newContext.j(j.OFFSET_SECONDS).longValue())));
                return endPos;
            }
        }
    }

    public String toString() {
        return this.b;
    }
}
