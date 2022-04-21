package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker {
    static StringMaker longStringMaker;
    static StringMaker middleStringMaker;
    static StringMaker shortStringMaker;
    int cacheOffset;
    boolean includeArgs = true;
    boolean includeEnclosingPoint = true;
    boolean includeJoinPointTypeName = true;
    boolean includeModifiers = false;
    boolean includeThrows = false;
    boolean shortKindName = true;
    boolean shortPrimaryTypeNames = false;
    boolean shortTypeNames = true;

    StringMaker() {
    }

    static {
        StringMaker stringMaker = new StringMaker();
        shortStringMaker = stringMaker;
        stringMaker.shortTypeNames = true;
        stringMaker.includeArgs = false;
        stringMaker.includeThrows = false;
        stringMaker.includeModifiers = false;
        stringMaker.shortPrimaryTypeNames = true;
        stringMaker.includeJoinPointTypeName = false;
        stringMaker.includeEnclosingPoint = false;
        stringMaker.cacheOffset = 0;
        StringMaker stringMaker2 = new StringMaker();
        middleStringMaker = stringMaker2;
        stringMaker2.shortTypeNames = true;
        stringMaker2.includeArgs = true;
        stringMaker2.includeThrows = false;
        stringMaker2.includeModifiers = false;
        stringMaker2.shortPrimaryTypeNames = false;
        shortStringMaker.cacheOffset = 1;
        StringMaker stringMaker3 = new StringMaker();
        longStringMaker = stringMaker3;
        stringMaker3.shortTypeNames = false;
        stringMaker3.includeArgs = true;
        stringMaker3.includeThrows = false;
        stringMaker3.includeModifiers = true;
        stringMaker3.shortPrimaryTypeNames = false;
        stringMaker3.shortKindName = false;
        stringMaker3.cacheOffset = 2;
    }

    /* access modifiers changed from: package-private */
    public String makeKindName(String name) {
        int dash = name.lastIndexOf(45);
        if (dash == -1) {
            return name;
        }
        return name.substring(dash + 1);
    }

    /* access modifiers changed from: package-private */
    public String makeModifiersString(int modifiers) {
        if (!this.includeModifiers) {
            return "";
        }
        String str = Modifier.toString(modifiers);
        if (str.length() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str);
        stringBuffer.append(" ");
        return stringBuffer.toString();
    }

    /* access modifiers changed from: package-private */
    public String stripPackageName(String name) {
        int dot = name.lastIndexOf(46);
        if (dot == -1) {
            return name;
        }
        return name.substring(dot + 1);
    }

    /* access modifiers changed from: package-private */
    public String makeTypeName(Class type, String typeName, boolean shortName) {
        if (type == null) {
            return "ANONYMOUS";
        }
        if (type.isArray()) {
            Class componentType = type.getComponentType();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(makeTypeName(componentType, componentType.getName(), shortName));
            stringBuffer.append("[]");
            return stringBuffer.toString();
        } else if (shortName) {
            return stripPackageName(typeName).replace('$', '.');
        } else {
            return typeName.replace('$', '.');
        }
    }

    public String makeTypeName(Class type) {
        return makeTypeName(type, type.getName(), this.shortTypeNames);
    }

    public String makePrimaryTypeName(Class type, String typeName) {
        return makeTypeName(type, typeName, this.shortPrimaryTypeNames);
    }

    public void addTypeNames(StringBuffer buf, Class[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(makeTypeName(types[i]));
        }
    }

    public void addSignature(StringBuffer buf, Class[] types) {
        if (types != null) {
            if (this.includeArgs) {
                buf.append("(");
                addTypeNames(buf, types);
                buf.append(")");
            } else if (types.length == 0) {
                buf.append("()");
            } else {
                buf.append("(..)");
            }
        }
    }

    public void addThrows(StringBuffer buf, Class[] types) {
        if (this.includeThrows && types != null && types.length != 0) {
            buf.append(" throws ");
            addTypeNames(buf, types);
        }
    }
}
