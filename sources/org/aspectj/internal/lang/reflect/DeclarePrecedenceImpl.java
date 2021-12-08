package org.aspectj.internal.lang.reflect;

import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.TypePattern;

public class DeclarePrecedenceImpl implements DeclarePrecedence {
    private AjType<?> declaringType;
    private TypePattern[] precedenceList;
    private String precedenceString;

    public DeclarePrecedenceImpl(String precedenceList2, AjType declaring) {
        this.declaringType = declaring;
        this.precedenceString = precedenceList2;
        String toTokenize = precedenceList2;
        StringTokenizer strTok = new StringTokenizer(toTokenize.startsWith("(") ? toTokenize.substring(1, toTokenize.length() - 1) : toTokenize, ",");
        this.precedenceList = new TypePattern[strTok.countTokens()];
        int i = 0;
        while (true) {
            TypePattern[] typePatternArr = this.precedenceList;
            if (i < typePatternArr.length) {
                typePatternArr[i] = new TypePatternImpl(strTok.nextToken().trim());
                i++;
            } else {
                return;
            }
        }
    }

    public AjType getDeclaringType() {
        return this.declaringType;
    }

    public TypePattern[] getPrecedenceOrder() {
        return this.precedenceList;
    }

    public String toString() {
        return "declare precedence : " + this.precedenceString;
    }
}
