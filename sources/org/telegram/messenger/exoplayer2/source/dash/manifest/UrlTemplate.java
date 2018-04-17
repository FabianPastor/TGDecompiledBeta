package org.telegram.messenger.exoplayer2.source.dash.manifest;

import java.util.Locale;

public final class UrlTemplate {
    private static final String BANDWIDTH = "Bandwidth";
    private static final int BANDWIDTH_ID = 3;
    private static final String DEFAULT_FORMAT_TAG = "%01d";
    private static final String ESCAPED_DOLLAR = "$$";
    private static final String NUMBER = "Number";
    private static final int NUMBER_ID = 2;
    private static final String REPRESENTATION = "RepresentationID";
    private static final int REPRESENTATION_ID = 1;
    private static final String TIME = "Time";
    private static final int TIME_ID = 4;
    private final int identifierCount;
    private final String[] identifierFormatTags;
    private final int[] identifiers;
    private final String[] urlPieces;

    public static UrlTemplate compile(String template) {
        String[] urlPieces = new String[5];
        int[] identifiers = new int[4];
        String[] identifierFormatTags = new String[4];
        return new UrlTemplate(urlPieces, identifiers, identifierFormatTags, parseTemplate(template, urlPieces, identifiers, identifierFormatTags));
    }

    private UrlTemplate(String[] urlPieces, int[] identifiers, String[] identifierFormatTags, int identifierCount) {
        this.urlPieces = urlPieces;
        this.identifiers = identifiers;
        this.identifierFormatTags = identifierFormatTags;
        this.identifierCount = identifierCount;
    }

    public String buildUri(String representationId, int segmentNumber, int bandwidth, long time) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.identifierCount; i++) {
            builder.append(this.urlPieces[i]);
            if (this.identifiers[i] == 1) {
                builder.append(representationId);
            } else if (this.identifiers[i] == 2) {
                builder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[]{Integer.valueOf(segmentNumber)}));
            } else if (this.identifiers[i] == 3) {
                builder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[]{Integer.valueOf(bandwidth)}));
            } else if (this.identifiers[i] == 4) {
                builder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[]{Long.valueOf(time)}));
            }
        }
        builder.append(this.urlPieces[this.identifierCount]);
        return builder.toString();
    }

    private static int parseTemplate(String template, String[] urlPieces, int[] identifiers, String[] identifierFormatTags) {
        String str = template;
        urlPieces[0] = TtmlNode.ANONYMOUS_REGION_ID;
        int templateIndex = 0;
        int identifierCount = 0;
        while (templateIndex < str.length()) {
            int dollarIndex = str.indexOf("$", templateIndex);
            StringBuilder stringBuilder;
            if (dollarIndex == -1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(urlPieces[identifierCount]);
                stringBuilder.append(str.substring(templateIndex));
                urlPieces[identifierCount] = stringBuilder.toString();
                templateIndex = str.length();
            } else if (dollarIndex != templateIndex) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(urlPieces[identifierCount]);
                stringBuilder.append(str.substring(templateIndex, dollarIndex));
                urlPieces[identifierCount] = stringBuilder.toString();
                templateIndex = dollarIndex;
            } else if (str.startsWith(ESCAPED_DOLLAR, templateIndex)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(urlPieces[identifierCount]);
                stringBuilder.append("$");
                urlPieces[identifierCount] = stringBuilder.toString();
                templateIndex += 2;
            } else {
                int templateIndex2 = str.indexOf("$", templateIndex + 1);
                String identifier = str.substring(templateIndex + 1, templateIndex2);
                int i = 1;
                if (identifier.equals(REPRESENTATION)) {
                    identifiers[identifierCount] = 1;
                } else {
                    StringBuilder stringBuilder2;
                    int formatTagIndex = identifier.indexOf("%0");
                    String formatTag = DEFAULT_FORMAT_TAG;
                    if (formatTagIndex != -1) {
                        formatTag = identifier.substring(formatTagIndex);
                        if (!formatTag.endsWith("d")) {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(formatTag);
                            stringBuilder3.append("d");
                            formatTag = stringBuilder3.toString();
                        }
                        identifier = identifier.substring(0, formatTagIndex);
                    }
                    int hashCode = identifier.hashCode();
                    if (hashCode != -NUM) {
                        if (hashCode != 2606829) {
                            if (hashCode == 38199441) {
                                if (identifier.equals(BANDWIDTH)) {
                                    switch (i) {
                                        case 0:
                                            identifiers[identifierCount] = 2;
                                            break;
                                        case 1:
                                            identifiers[identifierCount] = 3;
                                            break;
                                        case 2:
                                            identifiers[identifierCount] = 4;
                                            break;
                                        default:
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("Invalid template: ");
                                            stringBuilder2.append(str);
                                            throw new IllegalArgumentException(stringBuilder2.toString());
                                    }
                                    identifierFormatTags[identifierCount] = formatTag;
                                }
                            }
                        } else if (identifier.equals(TIME)) {
                            i = 2;
                            switch (i) {
                                case 0:
                                    identifiers[identifierCount] = 2;
                                    break;
                                case 1:
                                    identifiers[identifierCount] = 3;
                                    break;
                                case 2:
                                    identifiers[identifierCount] = 4;
                                    break;
                                default:
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("Invalid template: ");
                                    stringBuilder2.append(str);
                                    throw new IllegalArgumentException(stringBuilder2.toString());
                            }
                            identifierFormatTags[identifierCount] = formatTag;
                        }
                    } else if (identifier.equals(NUMBER)) {
                        i = 0;
                        switch (i) {
                            case 0:
                                identifiers[identifierCount] = 2;
                                break;
                            case 1:
                                identifiers[identifierCount] = 3;
                                break;
                            case 2:
                                identifiers[identifierCount] = 4;
                                break;
                            default:
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Invalid template: ");
                                stringBuilder2.append(str);
                                throw new IllegalArgumentException(stringBuilder2.toString());
                        }
                        identifierFormatTags[identifierCount] = formatTag;
                    }
                    i = -1;
                    switch (i) {
                        case 0:
                            identifiers[identifierCount] = 2;
                            break;
                        case 1:
                            identifiers[identifierCount] = 3;
                            break;
                        case 2:
                            identifiers[identifierCount] = 4;
                            break;
                        default:
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Invalid template: ");
                            stringBuilder2.append(str);
                            throw new IllegalArgumentException(stringBuilder2.toString());
                    }
                    identifierFormatTags[identifierCount] = formatTag;
                }
                identifierCount++;
                urlPieces[identifierCount] = TtmlNode.ANONYMOUS_REGION_ID;
                templateIndex = templateIndex2 + 1;
            }
        }
        return identifierCount;
    }
}
