package org.telegram.messenger;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.Emoji;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    public static final int MESSAGE_SEND_STATE_EDITING = 3;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int POSITION_FLAG_BOTTOM = 8;
    public static final int POSITION_FLAG_LEFT = 1;
    public static final int POSITION_FLAG_RIGHT = 2;
    public static final int POSITION_FLAG_TOP = 4;
    public static final int TYPE_ANIMATED_STICKER = 15;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_VIDEO = 3;
    static final String[] excludeWords = {" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
    public static Pattern instagramUrlPattern;
    public static Pattern urlPattern;
    public static Pattern videoTimeUrlPattern;
    public boolean attachPathExists;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressMs;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public float bufferedProgress;
    public boolean cancelEditing;
    public CharSequence caption;
    public ArrayList<TLRPC.TL_pollAnswer> checkedVotes;
    public int contentType;
    public int currentAccount;
    public TLRPC.TL_channelAdminLogEvent currentEvent;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public CharSequence editingMessage;
    public ArrayList<TLRPC.MessageEntity> editingMessageEntities;
    public TLRPC.Document emojiAnimatedSticker;
    public String emojiAnimatedStickerColor;
    private int emojiOnlyCount;
    public long eventId;
    public float forceSeekTo;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasRtl;
    public boolean isDateObject;
    public boolean isRestrictedMessage;
    private int isRoundVideoCached;
    public int lastLineWidth;
    private boolean layoutCreated;
    public int linesCount;
    public CharSequence linkDescription;
    public long loadedFileSize;
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public TLRPC.Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<TLRPC.PhotoSize> photoThumbs;
    public ArrayList<TLRPC.PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public String previousAttachPath;
    public String previousCaption;
    public ArrayList<TLRPC.MessageEntity> previousCaptionEntities;
    public TLRPC.MessageMedia previousMedia;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public boolean scheduled;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public float textXOffset;
    public int type;
    public boolean useCustomPhoto;
    public CharSequence vCardData;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;

    public void checkForScam() {
    }

    public static class VCardData {
        private String company;
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();

        public static CharSequence parse(String str) {
            int i;
            boolean z;
            VCardData vCardData;
            byte[] decodeQuotedPrintable;
            try {
                BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
                z = false;
                vCardData = null;
                String str2 = null;
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    } else if (!readLine.startsWith("PHOTO")) {
                        if (readLine.indexOf(58) >= 0) {
                            if (readLine.startsWith("BEGIN:VCARD")) {
                                vCardData = new VCardData();
                            } else if (readLine.startsWith("END:VCARD") && vCardData != null) {
                                z = true;
                            }
                        }
                        if (str2 != null) {
                            readLine = str2 + readLine;
                            str2 = null;
                        }
                        if (readLine.contains("=QUOTED-PRINTABLE")) {
                            if (readLine.endsWith("=")) {
                                str2 = readLine.substring(0, readLine.length() - 1);
                            }
                        }
                        int indexOf = readLine.indexOf(":");
                        int i2 = 2;
                        String[] strArr = indexOf >= 0 ? new String[]{readLine.substring(0, indexOf), readLine.substring(indexOf + 1).trim()} : new String[]{readLine.trim()};
                        if (strArr.length >= 2) {
                            if (vCardData != null) {
                                if (strArr[0].startsWith("ORG")) {
                                    String[] split = strArr[0].split(";");
                                    int length = split.length;
                                    int i3 = 0;
                                    String str3 = null;
                                    String str4 = null;
                                    while (i3 < length) {
                                        String[] split2 = split[i3].split("=");
                                        if (split2.length == i2) {
                                            if (split2[0].equals("CHARSET")) {
                                                str4 = split2[1];
                                            } else if (split2[0].equals("ENCODING")) {
                                                str3 = split2[1];
                                            }
                                        }
                                        i3++;
                                        i2 = 2;
                                    }
                                    vCardData.company = strArr[1];
                                    if (!(str3 == null || !str3.equalsIgnoreCase("QUOTED-PRINTABLE") || (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(vCardData.company))) == null || decodeQuotedPrintable.length == 0)) {
                                        vCardData.company = new String(decodeQuotedPrintable, str4);
                                    }
                                    vCardData.company = vCardData.company.replace(';', ' ');
                                } else if (strArr[0].startsWith("TEL")) {
                                    if (strArr[1].length() > 0) {
                                        vCardData.phones.add(strArr[1]);
                                    }
                                } else if (strArr[0].startsWith("EMAIL")) {
                                    String str5 = strArr[1];
                                    if (str5.length() > 0) {
                                        vCardData.emails.add(str5);
                                    }
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable unused) {
                return null;
            }
            if (!z) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i4 = 0; i4 < vCardData.phones.size(); i4++) {
                if (sb.length() > 0) {
                    sb.append(10);
                }
                String str6 = vCardData.phones.get(i4);
                if (!str6.contains("#")) {
                    if (!str6.contains("*")) {
                        sb.append(PhoneFormat.getInstance().format(str6));
                    }
                }
                sb.append(str6);
            }
            for (i = 0; i < vCardData.emails.size(); i++) {
                if (sb.length() > 0) {
                    sb.append(10);
                }
                sb.append(PhoneFormat.getInstance().format(vCardData.emails.get(i)));
            }
            if (!TextUtils.isEmpty(vCardData.company)) {
                if (sb.length() > 0) {
                    sb.append(10);
                }
                sb.append(vCardData.company);
            }
            return sb;
        }
    }

    public static class TextLayoutBlock {
        public int charactersEnd;
        public int charactersOffset;
        public byte directionFlags;
        public int height;
        public int heightByOffset;
        public StaticLayout textLayout;
        public float textYOffset;

        public boolean isRtl() {
            byte b = this.directionFlags;
            return (b & 1) != 0 && (b & 2) == 0;
        }
    }

    public static class GroupedMessagePosition {
        public float aspectRatio;
        public boolean edge;
        public int flags;
        public boolean last;
        public int leftSpanOffset;
        public byte maxX;
        public byte maxY;
        public byte minX;
        public byte minY;
        public float ph;
        public int pw;
        public float[] siblingHeights;
        public int spanSize;

        public void set(int i, int i2, int i3, int i4, int i5, float f, int i6) {
            this.minX = (byte) i;
            this.maxX = (byte) i2;
            this.minY = (byte) i3;
            this.maxY = (byte) i4;
            this.pw = i5;
            this.spanSize = i5;
            this.ph = f;
            this.flags = (byte) i6;
        }
    }

    public static class GroupedMessages {
        private int firstSpanAdditionalSize = 200;
        public long groupId;
        public boolean hasSibling;
        private int maxSizeWidth = 800;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap<>();

        private class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i, int i2, float f, float f2) {
                this.lineCounts = new int[]{i, i2};
                this.heights = new float[]{f, f2};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, float f, float f2, float f3) {
                this.lineCounts = new int[]{i, i2, i3};
                this.heights = new float[]{f, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i, i2, i3, i4};
                this.heights = new float[]{f, f2, f3, f4};
            }
        }

        private float multiHeight(float[] fArr, int i, int i2) {
            float f = 0.0f;
            while (i < i2) {
                f += fArr[i];
                i++;
            }
            return ((float) this.maxSizeWidth) / f;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:179:0x06db, code lost:
            if (r6[2] > r6[3]) goto L_0x06df;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x005e, code lost:
            if ((r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) == false) goto L_0x0062;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r36 = this;
                r10 = r36
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                r0.clear()
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.positions
                r0.clear()
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r10.messages
                int r11 = r0.size()
                r12 = 1
                if (r11 > r12) goto L_0x0016
                return
            L_0x0016:
                r13 = 1145798656(0x444b8000, float:814.0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r14 = 0
                r10.hasSibling = r14
                r2 = 0
                r3 = 0
                r4 = 1065353216(0x3var_, float:1.0)
                r5 = 0
                r15 = 0
            L_0x0027:
                r16 = 1067030938(0x3var_a, float:1.2)
                if (r2 >= r11) goto L_0x00c3
                java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r10.messages
                java.lang.Object r6 = r6.get(r2)
                org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
                if (r2 != 0) goto L_0x0065
                boolean r3 = r6.isOutOwner()
                if (r3 != 0) goto L_0x0062
                org.telegram.tgnet.TLRPC$Message r7 = r6.messageOwner
                org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
                if (r7 == 0) goto L_0x0046
                org.telegram.tgnet.TLRPC$Peer r7 = r7.saved_from_peer
                if (r7 != 0) goto L_0x0060
            L_0x0046:
                org.telegram.tgnet.TLRPC$Message r7 = r6.messageOwner
                int r8 = r7.from_id
                if (r8 <= 0) goto L_0x0062
                org.telegram.tgnet.TLRPC$Peer r8 = r7.to_id
                int r9 = r8.channel_id
                if (r9 != 0) goto L_0x0060
                int r8 = r8.chat_id
                if (r8 != 0) goto L_0x0060
                org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
                boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
                if (r8 != 0) goto L_0x0060
                boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
                if (r7 == 0) goto L_0x0062
            L_0x0060:
                r7 = 1
                goto L_0x0063
            L_0x0062:
                r7 = 0
            L_0x0063:
                r15 = r3
                r3 = r7
            L_0x0065:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r6.photoThumbs
                int r8 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
                org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r8.<init>()
                int r9 = r11 + -1
                if (r2 != r9) goto L_0x007a
                r9 = 1
                goto L_0x007b
            L_0x007a:
                r9 = 0
            L_0x007b:
                r8.last = r9
                if (r7 != 0) goto L_0x0082
                r7 = 1065353216(0x3var_, float:1.0)
                goto L_0x008a
            L_0x0082:
                int r9 = r7.w
                float r9 = (float) r9
                int r7 = r7.h
                float r7 = (float) r7
                float r7 = r9 / r7
            L_0x008a:
                r8.aspectRatio = r7
                float r7 = r8.aspectRatio
                int r9 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
                if (r9 <= 0) goto L_0x0099
                java.lang.String r7 = "w"
                r0.append(r7)
                goto L_0x00ab
            L_0x0099:
                r9 = 1061997773(0x3f4ccccd, float:0.8)
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 >= 0) goto L_0x00a6
                java.lang.String r7 = "n"
                r0.append(r7)
                goto L_0x00ab
            L_0x00a6:
                java.lang.String r7 = "q"
                r0.append(r7)
            L_0x00ab:
                float r7 = r8.aspectRatio
                float r4 = r4 + r7
                r9 = 1073741824(0x40000000, float:2.0)
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 <= 0) goto L_0x00b5
                r5 = 1
            L_0x00b5:
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r7 = r10.positions
                r7.put(r6, r8)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r10.posArray
                r6.add(r8)
                int r2 = r2 + 1
                goto L_0x0027
            L_0x00c3:
                if (r3 == 0) goto L_0x00d1
                int r2 = r10.maxSizeWidth
                int r2 = r2 + -50
                r10.maxSizeWidth = r2
                int r2 = r10.firstSpanAdditionalSize
                int r2 = r2 + 50
                r10.firstSpanAdditionalSize = r2
            L_0x00d1:
                r2 = 1123024896(0x42var_, float:120.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r6.x
                int r6 = r6.y
                int r6 = java.lang.Math.min(r7, r6)
                float r6 = (float) r6
                int r7 = r10.maxSizeWidth
                float r7 = (float) r7
                float r6 = r6 / r7
                float r2 = r2 / r6
                int r9 = (int) r2
                r2 = 1109393408(0x42200000, float:40.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r6.x
                int r6 = r6.y
                int r6 = java.lang.Math.min(r7, r6)
                float r6 = (float) r6
                int r7 = r10.maxSizeWidth
                float r8 = (float) r7
                float r6 = r6 / r8
                float r2 = r2 / r6
                int r2 = (int) r2
                float r6 = (float) r7
                float r6 = r6 / r13
                float r7 = (float) r11
                float r8 = r4 / r7
                r4 = 1120403456(0x42CLASSNAME, float:100.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                float r7 = r4 / r13
                r4 = 4
                r1 = 3
                r13 = 2
                if (r5 != 0) goto L_0x0532
                if (r11 == r13) goto L_0x011e
                if (r11 == r1) goto L_0x011e
                if (r11 != r4) goto L_0x0532
            L_0x011e:
                r5 = 1053609165(0x3ecccccd, float:0.4)
                r4 = 1137410048(0x43cb8000, float:407.0)
                if (r11 != r13) goto L_0x0254
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                java.lang.Object r1 = r1.get(r14)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                java.lang.String r0 = r0.toString()
                java.lang.String r3 = "ww"
                boolean r7 = r0.equals(r3)
                if (r7 == 0) goto L_0x01a3
                double r7 = (double) r8
                r18 = 4608983858650965606(0x3ffNUM, double:1.4)
                r26 = r15
                double r14 = (double) r6
                java.lang.Double.isNaN(r14)
                double r14 = r14 * r18
                int r6 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
                if (r6 <= 0) goto L_0x01a5
                float r6 = r1.aspectRatio
                float r7 = r2.aspectRatio
                float r8 = r6 - r7
                double r14 = (double) r8
                r18 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r8 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
                if (r8 >= 0) goto L_0x01a5
                int r0 = r10.maxSizeWidth
                float r3 = (float) r0
                float r3 = r3 / r6
                float r0 = (float) r0
                float r0 = r0 / r7
                float r0 = java.lang.Math.min(r0, r4)
                float r0 = java.lang.Math.min(r3, r0)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                r3 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r3
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                int r3 = r10.maxSizeWidth
                r25 = 7
                r18 = r1
                r23 = r3
                r24 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r21 = 1
                r22 = 1
                int r1 = r10.maxSizeWidth
                r25 = 11
                r18 = r2
                r23 = r1
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r0 = 0
                goto L_0x024e
            L_0x01a3:
                r26 = r15
            L_0x01a5:
                boolean r3 = r0.equals(r3)
                if (r3 != 0) goto L_0x0213
                java.lang.String r3 = "qq"
                boolean r0 = r0.equals(r3)
                if (r0 == 0) goto L_0x01b4
                goto L_0x0213
            L_0x01b4:
                int r0 = r10.maxSizeWidth
                float r3 = (float) r0
                float r3 = r3 * r5
                float r0 = (float) r0
                float r4 = r1.aspectRatio
                float r0 = r0 / r4
                r5 = 1065353216(0x3var_, float:1.0)
                float r4 = r5 / r4
                float r6 = r2.aspectRatio
                float r5 = r5 / r6
                float r4 = r4 + r5
                float r0 = r0 / r4
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                float r0 = java.lang.Math.max(r3, r0)
                int r0 = (int) r0
                int r3 = r10.maxSizeWidth
                int r3 = r3 - r0
                if (r3 >= r9) goto L_0x01d9
                int r3 = r9 - r3
                int r0 = r0 - r3
                r3 = r9
            L_0x01d9:
                float r4 = (float) r3
                float r5 = r1.aspectRatio
                float r4 = r4 / r5
                float r5 = (float) r0
                float r6 = r2.aspectRatio
                float r5 = r5 / r6
                float r4 = java.lang.Math.min(r4, r5)
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                r5 = 1145798656(0x444b8000, float:814.0)
                float r4 = java.lang.Math.min(r5, r4)
                float r4 = r4 / r5
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                r25 = 13
                r18 = r1
                r23 = r3
                r24 = r4
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 14
                r18 = r2
                r23 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                goto L_0x024d
            L_0x0213:
                int r0 = r10.maxSizeWidth
                int r0 = r0 / r13
                float r3 = (float) r0
                float r4 = r1.aspectRatio
                float r4 = r3 / r4
                float r5 = r2.aspectRatio
                float r3 = r3 / r5
                r5 = 1145798656(0x444b8000, float:814.0)
                float r3 = java.lang.Math.min(r3, r5)
                float r3 = java.lang.Math.min(r4, r3)
                int r3 = java.lang.Math.round(r3)
                float r3 = (float) r3
                float r3 = r3 / r5
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                r25 = 13
                r18 = r1
                r23 = r0
                r24 = r3
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 14
                r18 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
            L_0x024d:
                r0 = 1
            L_0x024e:
                r12 = r0
            L_0x024f:
                r17 = r11
                r8 = 0
                goto L_0x077f
            L_0x0254:
                r26 = r15
                r6 = 1141264221(0x44064f5d, float:537.24005)
                if (r11 != r1) goto L_0x038e
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                r3 = 0
                java.lang.Object r1 = r1.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r10.posArray
                java.lang.Object r5 = r5.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r10.posArray
                java.lang.Object r8 = r8.get(r13)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                char r0 = r0.charAt(r3)
                r3 = 110(0x6e, float:1.54E-43)
                if (r0 != r3) goto L_0x0323
                float r0 = r5.aspectRatio
                int r3 = r10.maxSizeWidth
                float r3 = (float) r3
                float r3 = r3 * r0
                float r6 = r8.aspectRatio
                float r6 = r6 + r0
                float r3 = r3 / r6
                int r0 = java.lang.Math.round(r3)
                float r0 = (float) r0
                float r0 = java.lang.Math.min(r4, r0)
                r3 = 1145798656(0x444b8000, float:814.0)
                float r4 = r3 - r0
                float r3 = (float) r9
                int r6 = r10.maxSizeWidth
                float r6 = (float) r6
                r7 = 1056964608(0x3var_, float:0.5)
                float r6 = r6 * r7
                float r7 = r8.aspectRatio
                float r7 = r7 * r0
                float r9 = r5.aspectRatio
                float r9 = r9 * r4
                float r7 = java.lang.Math.min(r7, r9)
                int r7 = java.lang.Math.round(r7)
                float r7 = (float) r7
                float r6 = java.lang.Math.min(r6, r7)
                float r3 = java.lang.Math.max(r3, r6)
                int r3 = (int) r3
                float r6 = r1.aspectRatio
                r7 = 1145798656(0x444b8000, float:814.0)
                float r6 = r6 * r7
                float r2 = (float) r2
                float r6 = r6 + r2
                int r2 = r10.maxSizeWidth
                int r2 = r2 - r3
                float r2 = (float) r2
                float r2 = java.lang.Math.min(r6, r2)
                int r2 = java.lang.Math.round(r2)
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 1
                r24 = 1065353216(0x3var_, float:1.0)
                r25 = 13
                r18 = r1
                r23 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r22 = 0
                r6 = 1145798656(0x444b8000, float:814.0)
                float r4 = r4 / r6
                r25 = 6
                r18 = r5
                r23 = r3
                r24 = r4
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 0
                r21 = 1
                r22 = 1
                r6 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r6
                r25 = 10
                r18 = r8
                r24 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r6 = r10.maxSizeWidth
                r8.spanSize = r6
                float[] r7 = new float[r13]
                r9 = 0
                r7[r9] = r0
                r7[r12] = r4
                r1.siblingHeights = r7
                if (r26 == 0) goto L_0x031a
                int r6 = r6 - r3
                r1.spanSize = r6
                goto L_0x031f
            L_0x031a:
                int r6 = r6 - r2
                r5.spanSize = r6
                r8.leftSpanOffset = r2
            L_0x031f:
                r10.hasSibling = r12
                goto L_0x024f
            L_0x0323:
                int r0 = r10.maxSizeWidth
                float r0 = (float) r0
                float r2 = r1.aspectRatio
                float r0 = r0 / r2
                float r0 = java.lang.Math.min(r0, r6)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                r2 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r2
                r19 = 0
                r20 = 1
                r21 = 0
                r22 = 0
                int r2 = r10.maxSizeWidth
                r25 = 7
                r18 = r1
                r23 = r2
                r24 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r1 = r10.maxSizeWidth
                int r1 = r1 / r13
                r2 = 1145798656(0x444b8000, float:814.0)
                float r0 = r2 - r0
                float r3 = (float) r1
                float r4 = r5.aspectRatio
                float r4 = r3 / r4
                float r6 = r8.aspectRatio
                float r3 = r3 / r6
                float r3 = java.lang.Math.min(r4, r3)
                int r3 = java.lang.Math.round(r3)
                float r3 = (float) r3
                float r0 = java.lang.Math.min(r0, r3)
                float r0 = r0 / r2
                int r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                if (r2 >= 0) goto L_0x036e
                r0 = r7
            L_0x036e:
                r19 = 0
                r20 = 0
                r21 = 1
                r22 = 1
                r25 = 9
                r18 = r5
                r23 = r1
                r24 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 10
                r18 = r8
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                goto L_0x024f
            L_0x038e:
                r4 = 4
                if (r11 != r4) goto L_0x052c
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                r8 = 0
                java.lang.Object r4 = r4.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r10.posArray
                java.lang.Object r14 = r14.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r10.posArray
                java.lang.Object r15 = r15.get(r13)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r15 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r15
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r13 = r10.posArray
                java.lang.Object r13 = r13.get(r1)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r13 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r13
                char r0 = r0.charAt(r8)
                r8 = 119(0x77, float:1.67E-43)
                r12 = 1051260355(0x3ea8f5c3, float:0.33)
                if (r0 != r8) goto L_0x0475
                int r0 = r10.maxSizeWidth
                float r0 = (float) r0
                float r1 = r4.aspectRatio
                float r0 = r0 / r1
                float r0 = java.lang.Math.min(r0, r6)
                int r0 = java.lang.Math.round(r0)
                float r0 = (float) r0
                r1 = 1145798656(0x444b8000, float:814.0)
                float r0 = r0 / r1
                r19 = 0
                r20 = 2
                r21 = 0
                r22 = 0
                int r1 = r10.maxSizeWidth
                r25 = 7
                r18 = r4
                r23 = r1
                r24 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r1 = r10.maxSizeWidth
                float r1 = (float) r1
                float r2 = r14.aspectRatio
                float r3 = r15.aspectRatio
                float r2 = r2 + r3
                float r3 = r13.aspectRatio
                float r2 = r2 + r3
                float r1 = r1 / r2
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r2 = (float) r9
                int r3 = r10.maxSizeWidth
                float r3 = (float) r3
                float r3 = r3 * r5
                float r4 = r14.aspectRatio
                float r4 = r4 * r1
                float r3 = java.lang.Math.min(r3, r4)
                float r3 = java.lang.Math.max(r2, r3)
                int r3 = (int) r3
                int r4 = r10.maxSizeWidth
                float r4 = (float) r4
                float r4 = r4 * r12
                float r2 = java.lang.Math.max(r2, r4)
                float r4 = r13.aspectRatio
                float r4 = r4 * r1
                float r2 = java.lang.Math.max(r2, r4)
                int r2 = (int) r2
                int r4 = r10.maxSizeWidth
                int r4 = r4 - r3
                int r4 = r4 - r2
                r5 = 1114112000(0x42680000, float:58.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                if (r4 >= r6) goto L_0x0435
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r6 = r6 - r4
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = r6 / 2
                int r3 = r3 - r5
                int r6 = r6 - r5
                int r2 = r2 - r6
            L_0x0435:
                r23 = r3
                r3 = r2
                r2 = 1145798656(0x444b8000, float:814.0)
                float r0 = r2 - r0
                float r0 = java.lang.Math.min(r0, r1)
                float r0 = r0 / r2
                int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                if (r1 >= 0) goto L_0x0447
                r0 = r7
            L_0x0447:
                r19 = 0
                r20 = 0
                r21 = 1
                r22 = 1
                r25 = 9
                r18 = r14
                r24 = r0
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 8
                r18 = r15
                r23 = r4
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 2
                r20 = 2
                r25 = 10
                r18 = r13
                r23 = r3
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r12 = 2
                goto L_0x024f
            L_0x0475:
                float r0 = r14.aspectRatio
                r5 = 1065353216(0x3var_, float:1.0)
                float r0 = r5 / r0
                float r6 = r15.aspectRatio
                float r6 = r5 / r6
                float r0 = r0 + r6
                float r6 = r13.aspectRatio
                float r6 = r5 / r6
                float r0 = r0 + r6
                r5 = 1145798656(0x444b8000, float:814.0)
                float r0 = r5 / r0
                int r0 = java.lang.Math.round(r0)
                int r0 = java.lang.Math.max(r9, r0)
                float r3 = (float) r3
                float r6 = (float) r0
                float r7 = r14.aspectRatio
                float r7 = r6 / r7
                float r7 = java.lang.Math.max(r3, r7)
                float r7 = r7 / r5
                float r7 = java.lang.Math.min(r12, r7)
                float r8 = r15.aspectRatio
                float r6 = r6 / r8
                float r3 = java.lang.Math.max(r3, r6)
                float r3 = r3 / r5
                float r3 = java.lang.Math.min(r12, r3)
                r6 = 1065353216(0x3var_, float:1.0)
                float r6 = r6 - r7
                float r6 = r6 - r3
                float r8 = r4.aspectRatio
                float r5 = r5 * r8
                float r2 = (float) r2
                float r5 = r5 + r2
                int r2 = r10.maxSizeWidth
                int r2 = r2 - r0
                float r2 = (float) r2
                float r2 = java.lang.Math.min(r5, r2)
                int r2 = java.lang.Math.round(r2)
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 2
                float r5 = r7 + r3
                float r24 = r5 + r6
                r25 = 13
                r18 = r4
                r23 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r22 = 0
                r25 = 6
                r18 = r14
                r23 = r0
                r24 = r7
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 0
                r21 = 1
                r22 = 1
                r25 = 2
                r18 = r15
                r24 = r3
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r5 = r10.maxSizeWidth
                r15.spanSize = r5
                r21 = 2
                r22 = 2
                r25 = 10
                r18 = r13
                r24 = r6
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r5 = r10.maxSizeWidth
                r13.spanSize = r5
                if (r26 == 0) goto L_0x0513
                int r5 = r5 - r0
                r4.spanSize = r5
                goto L_0x051a
            L_0x0513:
                int r5 = r5 - r2
                r14.spanSize = r5
                r15.leftSpanOffset = r2
                r13.leftSpanOffset = r2
            L_0x051a:
                float[] r0 = new float[r1]
                r1 = 0
                r0[r1] = r7
                r1 = 1
                r0[r1] = r3
                r2 = 2
                r0[r2] = r6
                r4.siblingHeights = r0
                r10.hasSibling = r1
                r12 = 1
                goto L_0x024f
            L_0x052c:
                r17 = r11
                r8 = 0
                r12 = 0
                goto L_0x077f
            L_0x0532:
                r26 = r15
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                int r0 = r0.size()
                float[] r12 = new float[r0]
                r0 = 0
            L_0x053d:
                if (r0 >= r11) goto L_0x0580
                r2 = 1066192077(0x3f8ccccd, float:1.1)
                int r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x0559
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                float r2 = r2.aspectRatio
                r3 = 1065353216(0x3var_, float:1.0)
                float r2 = java.lang.Math.max(r3, r2)
                r12[r0] = r2
                goto L_0x056b
            L_0x0559:
                r3 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                java.lang.Object r2 = r2.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                float r2 = r2.aspectRatio
                float r2 = java.lang.Math.min(r3, r2)
                r12[r0] = r2
            L_0x056b:
                r2 = 1059760867(0x3f2aaae3, float:0.66667)
                r5 = 1071225242(0x3fd9999a, float:1.7)
                r6 = r12[r0]
                float r5 = java.lang.Math.min(r5, r6)
                float r2 = java.lang.Math.max(r2, r5)
                r12[r0] = r2
                int r0 = r0 + 1
                goto L_0x053d
            L_0x0580:
                java.util.ArrayList r13 = new java.util.ArrayList
                r13.<init>()
                r6 = 1
            L_0x0586:
                int r0 = r12.length
                if (r6 >= r0) goto L_0x05bc
                int r0 = r12.length
                int r3 = r0 - r6
                if (r6 > r1) goto L_0x05b0
                if (r3 <= r1) goto L_0x0591
                goto L_0x05b0
            L_0x0591:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r14 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r0 = 0
                float r5 = r10.multiHeight(r12, r0, r6)
                int r0 = r12.length
                float r15 = r10.multiHeight(r12, r6, r0)
                r0 = r14
                r2 = 3
                r1 = r36
                r17 = r11
                r11 = 3
                r2 = r6
                r18 = 4
                r4 = r5
                r5 = r15
                r0.<init>(r2, r3, r4, r5)
                r13.add(r14)
                goto L_0x05b5
            L_0x05b0:
                r17 = r11
                r11 = 3
                r18 = 4
            L_0x05b5:
                int r6 = r6 + 1
                r11 = r17
                r1 = 3
                r4 = 4
                goto L_0x0586
            L_0x05bc:
                r17 = r11
                r11 = 3
                r18 = 4
                r14 = 1
            L_0x05c2:
                int r0 = r12.length
                r1 = 1
                int r0 = r0 - r1
                if (r14 >= r0) goto L_0x0614
                r15 = 1
            L_0x05c8:
                int r0 = r12.length
                int r0 = r0 - r14
                if (r15 >= r0) goto L_0x060e
                int r0 = r12.length
                int r0 = r0 - r14
                int r4 = r0 - r15
                if (r14 > r11) goto L_0x0606
                r0 = 1062836634(0x3var_a, float:0.85)
                int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x05db
                r0 = 4
                goto L_0x05dc
            L_0x05db:
                r0 = 3
            L_0x05dc:
                if (r15 > r0) goto L_0x0606
                if (r4 <= r11) goto L_0x05e1
                goto L_0x0606
            L_0x05e1:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r6 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r0 = 0
                float r5 = r10.multiHeight(r12, r0, r14)
                int r0 = r14 + r15
                float r19 = r10.multiHeight(r12, r14, r0)
                int r1 = r12.length
                float r20 = r10.multiHeight(r12, r0, r1)
                r0 = r6
                r1 = r36
                r2 = r14
                r3 = r15
                r11 = r6
                r6 = r19
                r27 = r7
                r7 = r20
                r0.<init>(r2, r3, r4, r5, r6, r7)
                r13.add(r11)
                goto L_0x0608
            L_0x0606:
                r27 = r7
            L_0x0608:
                int r15 = r15 + 1
                r7 = r27
                r11 = 3
                goto L_0x05c8
            L_0x060e:
                r27 = r7
                int r14 = r14 + 1
                r11 = 3
                goto L_0x05c2
            L_0x0614:
                r27 = r7
                r11 = 1
            L_0x0617:
                int r0 = r12.length
                r1 = 2
                int r0 = r0 - r1
                if (r11 >= r0) goto L_0x067d
                r14 = 1
            L_0x061d:
                int r0 = r12.length
                int r0 = r0 - r11
                if (r14 >= r0) goto L_0x0676
                r15 = 1
            L_0x0622:
                int r0 = r12.length
                int r0 = r0 - r11
                int r0 = r0 - r14
                if (r15 >= r0) goto L_0x066f
                int r0 = r12.length
                int r0 = r0 - r11
                int r0 = r0 - r14
                int r5 = r0 - r15
                r0 = 3
                if (r11 > r0) goto L_0x0664
                if (r14 > r0) goto L_0x0664
                if (r15 > r0) goto L_0x0664
                if (r5 <= r0) goto L_0x0636
                goto L_0x0664
            L_0x0636:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r8 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r0 = 0
                float r6 = r10.multiHeight(r12, r0, r11)
                int r0 = r11 + r14
                float r7 = r10.multiHeight(r12, r11, r0)
                int r1 = r0 + r15
                float r19 = r10.multiHeight(r12, r0, r1)
                int r0 = r12.length
                float r20 = r10.multiHeight(r12, r1, r0)
                r0 = r8
                r1 = r36
                r2 = r11
                r3 = r14
                r4 = r15
                r22 = r12
                r12 = r8
                r8 = r19
                r28 = r9
                r9 = r20
                r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
                r13.add(r12)
                goto L_0x0668
            L_0x0664:
                r28 = r9
                r22 = r12
            L_0x0668:
                int r15 = r15 + 1
                r12 = r22
                r9 = r28
                goto L_0x0622
            L_0x066f:
                r28 = r9
                r22 = r12
                int r14 = r14 + 1
                goto L_0x061d
            L_0x0676:
                r28 = r9
                r22 = r12
                int r11 = r11 + 1
                goto L_0x0617
            L_0x067d:
                r28 = r9
                r22 = r12
                r0 = 0
                r1 = 0
                int r2 = r10.maxSizeWidth
                r3 = 3
                int r2 = r2 / r3
                int r2 = r2 * 4
                float r2 = (float) r2
                r1 = r0
                r0 = 0
                r3 = 0
            L_0x068d:
                int r4 = r13.size()
                if (r0 >= r4) goto L_0x06fd
                java.lang.Object r4 = r13.get(r0)
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r4 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r4
                r5 = 0
                r6 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r5 = 0
                r6 = 0
                r7 = 2139095039(0x7f7fffff, float:3.4028235E38)
            L_0x06a2:
                float[] r8 = r4.heights
                int r9 = r8.length
                if (r5 >= r9) goto L_0x06b5
                r9 = r8[r5]
                float r6 = r6 + r9
                r9 = r8[r5]
                int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r9 >= 0) goto L_0x06b2
                r7 = r8[r5]
            L_0x06b2:
                int r5 = r5 + 1
                goto L_0x06a2
            L_0x06b5:
                float r6 = r6 - r2
                float r5 = java.lang.Math.abs(r6)
                int[] r6 = r4.lineCounts
                int r8 = r6.length
                r9 = 1
                if (r8 <= r9) goto L_0x06e2
                r8 = 0
                r11 = r6[r8]
                r12 = r6[r9]
                if (r11 > r12) goto L_0x06de
                int r11 = r6.length
                r12 = 2
                if (r11 <= r12) goto L_0x06d1
                r11 = r6[r9]
                r6 = r6[r12]
                if (r11 > r6) goto L_0x06de
            L_0x06d1:
                int[] r6 = r4.lineCounts
                int r9 = r6.length
                r11 = 3
                if (r9 <= r11) goto L_0x06e4
                r9 = r6[r12]
                r6 = r6[r11]
                if (r9 <= r6) goto L_0x06e4
                goto L_0x06df
            L_0x06de:
                r11 = 3
            L_0x06df:
                float r5 = r5 * r16
                goto L_0x06e4
            L_0x06e2:
                r8 = 0
                r11 = 3
            L_0x06e4:
                r6 = r5
                r5 = r28
                float r9 = (float) r5
                int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r7 >= 0) goto L_0x06f0
                r7 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r6 = r6 * r7
            L_0x06f0:
                if (r1 == 0) goto L_0x06f6
                int r7 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
                if (r7 >= 0) goto L_0x06f8
            L_0x06f6:
                r1 = r4
                r3 = r6
            L_0x06f8:
                int r0 = r0 + 1
                r28 = r5
                goto L_0x068d
            L_0x06fd:
                r8 = 0
                if (r1 != 0) goto L_0x0701
                return
            L_0x0701:
                r0 = 0
                r2 = 0
                r12 = 0
            L_0x0704:
                int[] r3 = r1.lineCounts
                int r4 = r3.length
                if (r0 >= r4) goto L_0x077f
                r3 = r3[r0]
                float[] r4 = r1.heights
                r4 = r4[r0]
                int r5 = r10.maxSizeWidth
                r6 = 0
                int r7 = r3 + -1
                int r12 = java.lang.Math.max(r12, r7)
                r9 = r2
                r2 = 0
            L_0x071a:
                if (r2 >= r3) goto L_0x076c
                r11 = r22[r9]
                float r11 = r11 * r4
                int r11 = (int) r11
                int r5 = r5 - r11
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r13 = r10.posArray
                java.lang.Object r13 = r13.get(r9)
                r28 = r13
                org.telegram.messenger.MessageObject$GroupedMessagePosition r28 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r28
                if (r0 != 0) goto L_0x0730
                r13 = 4
                goto L_0x0731
            L_0x0730:
                r13 = 0
            L_0x0731:
                int[] r14 = r1.lineCounts
                int r14 = r14.length
                r15 = 1
                int r14 = r14 - r15
                if (r0 != r14) goto L_0x073a
                r13 = r13 | 8
            L_0x073a:
                if (r2 != 0) goto L_0x0742
                r13 = r13 | 1
                if (r26 == 0) goto L_0x0742
                r6 = r28
            L_0x0742:
                if (r2 != r7) goto L_0x074d
                r13 = r13 | 2
                if (r26 != 0) goto L_0x074d
                r35 = r13
                r6 = r28
                goto L_0x074f
            L_0x074d:
                r35 = r13
            L_0x074f:
                r13 = 1145798656(0x444b8000, float:814.0)
                float r14 = r4 / r13
                r15 = r27
                float r34 = java.lang.Math.max(r15, r14)
                r29 = r2
                r30 = r2
                r31 = r0
                r32 = r0
                r33 = r11
                r28.set(r29, r30, r31, r32, r33, r34, r35)
                int r9 = r9 + 1
                int r2 = r2 + 1
                goto L_0x071a
            L_0x076c:
                r15 = r27
                r13 = 1145798656(0x444b8000, float:814.0)
                int r2 = r6.pw
                int r2 = r2 + r5
                r6.pw = r2
                int r2 = r6.spanSize
                int r2 = r2 + r5
                r6.spanSize = r2
                int r0 = r0 + 1
                r2 = r9
                goto L_0x0704
            L_0x077f:
                r0 = r17
            L_0x0781:
                if (r8 >= r0) goto L_0x07ff
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                java.lang.Object r1 = r1.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                if (r26 == 0) goto L_0x07a3
                byte r2 = r1.minX
                if (r2 != 0) goto L_0x0798
                int r2 = r1.spanSize
                int r3 = r10.firstSpanAdditionalSize
                int r2 = r2 + r3
                r1.spanSize = r2
            L_0x0798:
                int r2 = r1.flags
                r3 = 2
                r2 = r2 & r3
                if (r2 == 0) goto L_0x07a1
                r2 = 1
                r1.edge = r2
            L_0x07a1:
                r3 = 1
                goto L_0x07bc
            L_0x07a3:
                r3 = 2
                byte r2 = r1.maxX
                if (r2 == r12) goto L_0x07ad
                int r2 = r1.flags
                r2 = r2 & r3
                if (r2 == 0) goto L_0x07b4
            L_0x07ad:
                int r2 = r1.spanSize
                int r3 = r10.firstSpanAdditionalSize
                int r2 = r2 + r3
                r1.spanSize = r2
            L_0x07b4:
                int r2 = r1.flags
                r3 = 1
                r2 = r2 & r3
                if (r2 == 0) goto L_0x07bc
                r1.edge = r3
            L_0x07bc:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r10.messages
                java.lang.Object r2 = r2.get(r8)
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                if (r26 != 0) goto L_0x07fb
                boolean r2 = r2.needDrawAvatarInternal()
                if (r2 == 0) goto L_0x07fb
                boolean r2 = r1.edge
                if (r2 == 0) goto L_0x07e1
                int r2 = r1.spanSize
                r4 = 1000(0x3e8, float:1.401E-42)
                if (r2 == r4) goto L_0x07da
                int r2 = r2 + 108
                r1.spanSize = r2
            L_0x07da:
                int r2 = r1.pw
                int r2 = r2 + 108
                r1.pw = r2
                goto L_0x07fb
            L_0x07e1:
                int r2 = r1.flags
                r4 = 2
                r2 = r2 & r4
                if (r2 == 0) goto L_0x07fc
                int r2 = r1.spanSize
                r5 = 1000(0x3e8, float:1.401E-42)
                if (r2 == r5) goto L_0x07f2
                int r2 = r2 + -108
                r1.spanSize = r2
                goto L_0x07fc
            L_0x07f2:
                int r2 = r1.leftSpanOffset
                if (r2 == 0) goto L_0x07fc
                int r2 = r2 + 108
                r1.leftSpanOffset = r2
                goto L_0x07fc
            L_0x07fb:
                r4 = 2
            L_0x07fc:
                int r8 = r8 + 1
                goto L_0x0781
            L_0x07ff:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void");
        }

        public MessageObject findPrimaryMessageObject() {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject messageObject = this.messages.get(i);
                GroupedMessagePosition groupedMessagePosition = this.positions.get(messageObject);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & 5) != 0) {
                    return messageObject;
                }
            }
            return null;
        }
    }

    public MessageObject(int i, TLRPC.Message message, String str, String str2, String str3, boolean z, boolean z2, boolean z3) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.localType = z ? 2 : 1;
        this.currentAccount = i;
        this.localName = str2;
        this.localUserName = str3;
        this.messageText = str;
        this.messageOwner = message;
        this.localChannel = z2;
        this.localEdit = z3;
    }

    public MessageObject(int i, TLRPC.Message message, AbstractMap<Integer, TLRPC.User> abstractMap, boolean z) {
        this(i, message, abstractMap, (AbstractMap<Integer, TLRPC.Chat>) null, z);
    }

    public MessageObject(int i, TLRPC.Message message, SparseArray<TLRPC.User> sparseArray, boolean z) {
        this(i, message, sparseArray, (SparseArray<TLRPC.Chat>) null, z);
    }

    public MessageObject(int i, TLRPC.Message message, boolean z) {
        this(i, message, (MessageObject) null, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null, z, 0);
    }

    public MessageObject(int i, TLRPC.Message message, MessageObject messageObject, boolean z) {
        this(i, message, messageObject, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null, z, 0);
    }

    public MessageObject(int i, TLRPC.Message message, AbstractMap<Integer, TLRPC.User> abstractMap, AbstractMap<Integer, TLRPC.Chat> abstractMap2, boolean z) {
        this(i, message, abstractMap, abstractMap2, z, 0);
    }

    public MessageObject(int i, TLRPC.Message message, SparseArray<TLRPC.User> sparseArray, SparseArray<TLRPC.Chat> sparseArray2, boolean z) {
        this(i, message, (MessageObject) null, (AbstractMap<Integer, TLRPC.User>) null, (AbstractMap<Integer, TLRPC.Chat>) null, sparseArray, sparseArray2, z, 0);
    }

    public MessageObject(int i, TLRPC.Message message, AbstractMap<Integer, TLRPC.User> abstractMap, AbstractMap<Integer, TLRPC.Chat> abstractMap2, boolean z, long j) {
        this(i, message, (MessageObject) null, abstractMap, abstractMap2, (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null, z, j);
    }

    public MessageObject(int i, TLRPC.Message message, MessageObject messageObject, AbstractMap<Integer, TLRPC.User> abstractMap, AbstractMap<Integer, TLRPC.Chat> abstractMap2, SparseArray<TLRPC.User> sparseArray, SparseArray<TLRPC.Chat> sparseArray2, boolean z, long j) {
        TLRPC.User user;
        SparseArray<TLRPC.Chat> sparseArray3;
        AbstractMap<Integer, TLRPC.Chat> abstractMap3;
        TextPaint textPaint;
        TLRPC.Message message2 = message;
        AbstractMap<Integer, TLRPC.User> abstractMap4 = abstractMap;
        SparseArray<TLRPC.User> sparseArray4 = sparseArray;
        boolean z2 = z;
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        Theme.createChatResources((Context) null, true);
        this.currentAccount = i;
        this.messageOwner = message2;
        this.replyMessageObject = messageObject;
        this.eventId = j;
        TLRPC.Message message3 = message2.replyMessage;
        if (message3 != null) {
            MessageObject messageObject2 = r2;
            MessageObject messageObject3 = new MessageObject(this.currentAccount, message3, (MessageObject) null, abstractMap, abstractMap2, sparseArray, sparseArray2, false, j);
            this.replyMessageObject = messageObject2;
        }
        int i2 = message2.from_id;
        if (i2 > 0) {
            if (abstractMap4 != null) {
                user = abstractMap4.get(Integer.valueOf(i2));
            } else {
                user = sparseArray4 != null ? sparseArray4.get(i2) : null;
            }
            user = user == null ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(message2.from_id)) : user;
            abstractMap3 = abstractMap2;
            sparseArray3 = sparseArray2;
        } else {
            abstractMap3 = abstractMap2;
            sparseArray3 = sparseArray2;
            user = null;
        }
        updateMessageText(abstractMap4, abstractMap3, sparseArray4, sparseArray3);
        setType();
        measureInlineBotButtons();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int i3 = gregorianCalendar.get(6);
        int i4 = gregorianCalendar.get(1);
        int i5 = gregorianCalendar.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i3)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
        createMessageSendInfo();
        generateCaption();
        if (z2) {
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            int[] iArr = allowsBigEmoji() ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1 && !(message2.media instanceof TLRPC.TL_messageMediaWebPage) && message2.entities.isEmpty()) {
                CharSequence charSequence = this.messageText;
                int indexOf = TextUtils.indexOf(charSequence, "🏻");
                if (indexOf >= 0) {
                    this.emojiAnimatedStickerColor = "_c1";
                    charSequence = charSequence.subSequence(0, indexOf);
                } else {
                    int indexOf2 = TextUtils.indexOf(charSequence, "🏼");
                    if (indexOf2 >= 0) {
                        this.emojiAnimatedStickerColor = "_c2";
                        charSequence = charSequence.subSequence(0, indexOf2);
                    } else {
                        int indexOf3 = TextUtils.indexOf(charSequence, "🏽");
                        if (indexOf3 >= 0) {
                            this.emojiAnimatedStickerColor = "_c3";
                            charSequence = charSequence.subSequence(0, indexOf3);
                        } else {
                            int indexOf4 = TextUtils.indexOf(charSequence, "🏾");
                            if (indexOf4 >= 0) {
                                this.emojiAnimatedStickerColor = "_c4";
                                charSequence = charSequence.subSequence(0, indexOf4);
                            } else {
                                int indexOf5 = TextUtils.indexOf(charSequence, "🏿");
                                if (indexOf5 >= 0) {
                                    this.emojiAnimatedStickerColor = "_c5";
                                    charSequence = charSequence.subSequence(0, indexOf5);
                                } else {
                                    this.emojiAnimatedStickerColor = "";
                                }
                            }
                        }
                    }
                }
                if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) || EmojiData.emojiColoredMap.contains(charSequence.toString())) {
                    this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence);
                }
            }
            if (this.emojiAnimatedSticker == null) {
                generateLayout(user);
            } else {
                this.type = 1000;
                if (isSticker()) {
                    this.type = 13;
                } else if (isAnimatedSticker()) {
                    this.type = 15;
                }
            }
        }
        this.layoutCreated = z2;
        generateThumbs(false);
        checkMediaExistance();
    }

    private void createDateArray(int i, TLRPC.TL_channelAdminLogEvent tL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap) {
        if (hashMap.get(this.dateKey) == null) {
            hashMap.put(this.dateKey, new ArrayList());
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.message = LocaleController.formatDateChat((long) tL_channelAdminLogEvent.date);
            tL_message.id = 0;
            tL_message.date = tL_channelAdminLogEvent.date;
            MessageObject messageObject = new MessageObject(i, tL_message, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            arrayList.add(messageObject);
        }
    }

    private void checkEmojiOnly(int[] iArr) {
        TextPaint textPaint;
        int i;
        if (iArr != null) {
            if (iArr[0] >= 1 && iArr[0] <= 3) {
                int i2 = iArr[0];
                if (i2 == 1) {
                    textPaint = Theme.chat_msgTextPaintOneEmoji;
                    i = AndroidUtilities.dp(32.0f);
                    this.emojiOnlyCount = 1;
                } else if (i2 != 2) {
                    textPaint = Theme.chat_msgTextPaintThreeEmoji;
                    i = AndroidUtilities.dp(24.0f);
                    this.emojiOnlyCount = 3;
                } else {
                    textPaint = Theme.chat_msgTextPaintTwoEmoji;
                    int dp = AndroidUtilities.dp(28.0f);
                    this.emojiOnlyCount = 2;
                    i = dp;
                }
                CharSequence charSequence = this.messageText;
                Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), Emoji.EmojiSpan.class);
                if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                    for (Emoji.EmojiSpan replaceFontMetrics : emojiSpanArr) {
                        replaceFontMetrics.replaceFontMetrics(textPaint.getFontMetricsInt(), i);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a1  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06b5  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06bb A[LOOP:0: B:228:0x0674->B:250:0x06bb, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x0e9c  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0edb  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0ef3  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0f5a  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x06d5 A[EDGE_INSN: B:557:0x06d5->B:252:0x06d5 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:559:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MessageObject(int r26, org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent r27, java.util.ArrayList<org.telegram.messenger.MessageObject> r28, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r29, org.telegram.tgnet.TLRPC.Chat r30, int[] r31) {
        /*
            r25 = this;
            r0 = r25
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r30
            r25.<init>()
            r5 = 1000(0x3e8, float:1.401E-42)
            r0.type = r5
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r0.forceSeekTo = r5
            r0.currentEvent = r1
            r5 = r26
            r0.currentAccount = r5
            int r5 = r1.user_id
            if (r5 <= 0) goto L_0x0030
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r7 = r1.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r7)
            goto L_0x0031
        L_0x0030:
            r5 = 0
        L_0x0031:
            java.util.GregorianCalendar r7 = new java.util.GregorianCalendar
            r7.<init>()
            int r8 = r1.date
            long r8 = (long) r8
            r10 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 * r10
            r7.setTimeInMillis(r8)
            r8 = 6
            int r8 = r7.get(r8)
            r9 = 1
            int r10 = r7.get(r9)
            r11 = 2
            int r7 = r7.get(r11)
            r12 = 3
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r10)
            r15 = 0
            r13[r15] = r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r7)
            r13[r9] = r14
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r13[r11] = r8
            java.lang.String r8 = "%d_%02d_%02d"
            java.lang.String r8 = java.lang.String.format(r8, r13)
            r0.dateKey = r8
            java.lang.Object[] r8 = new java.lang.Object[r11]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r15] = r10
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r8[r9] = r7
            java.lang.String r7 = "%d_%02d"
            java.lang.String r7 = java.lang.String.format(r7, r8)
            r0.monthKey = r7
            org.telegram.tgnet.TLRPC$TL_peerChannel r7 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r7.<init>()
            int r8 = r4.id
            r7.channel_id = r8
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle
            java.lang.String r13 = ""
            java.lang.String r14 = "un1"
            if (r10 == 0) goto L_0x00c9
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle) r8
            java.lang.String r7 = r8.new_value
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x00b4
            r8 = 2131625077(0x7f0e0475, float:1.8877352E38)
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r10[r15] = r7
            java.lang.String r7 = "EventLogEditedGroupTitle"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0e97
        L_0x00b4:
            r8 = 2131625074(0x7f0e0472, float:1.8877346E38)
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r10[r15] = r7
            java.lang.String r7 = "EventLogEditedChannelTitle"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0e97
        L_0x00c9:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto
            if (r10 == 0) goto L_0x0144
            org.telegram.tgnet.TLRPC$TL_messageService r7 = new org.telegram.tgnet.TLRPC$TL_messageService
            r7.<init>()
            r0.messageOwner = r7
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$Photo r7 = r7.new_photo
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r7 == 0) goto L_0x010b
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            r8.<init>()
            r7.action = r8
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x00fa
            r7 = 2131625126(0x7f0e04a6, float:1.8877451E38)
            java.lang.String r8 = "EventLogRemovedWGroupPhoto"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0e97
        L_0x00fa:
            r7 = 2131625120(0x7f0e04a0, float:1.8877439E38)
            java.lang.String r8 = "EventLogRemovedChannelPhoto"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0e97
        L_0x010b:
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            r8.<init>()
            r7.action = r8
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r7 = r7.action
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$Photo r8 = r8.new_photo
            r7.photo = r8
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0133
            r7 = 2131625076(0x7f0e0474, float:1.887735E38)
            java.lang.String r8 = "EventLogEditedGroupPhoto"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0e97
        L_0x0133:
            r7 = 2131625073(0x7f0e0471, float:1.8877344E38)
            java.lang.String r8 = "EventLogEditedChannelPhoto"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0e97
        L_0x0144:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin
            java.lang.String r6 = "EventLogChannelJoined"
            if (r10 == 0) goto L_0x016e
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x015f
            r6 = 2131625095(0x7f0e0487, float:1.8877388E38)
            java.lang.String r7 = "EventLogGroupJoined"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x015f:
            r7 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x016e:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave
            if (r10 == 0) goto L_0x01b0
            org.telegram.tgnet.TLRPC$TL_messageService r6 = new org.telegram.tgnet.TLRPC$TL_messageService
            r6.<init>()
            r0.messageOwner = r6
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser r7 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            r7.<init>()
            r6.action = r7
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            int r7 = r1.user_id
            r6.user_id = r7
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x019f
            r6 = 2131625100(0x7f0e048c, float:1.8877398E38)
            java.lang.String r7 = "EventLogLeftGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x019f:
            r6 = 2131625099(0x7f0e048b, float:1.8877396E38)
            java.lang.String r7 = "EventLogLeftChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x01b0:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite
            java.lang.String r11 = "un2"
            if (r10 == 0) goto L_0x0224
            org.telegram.tgnet.TLRPC$TL_messageService r7 = new org.telegram.tgnet.TLRPC$TL_messageService
            r7.<init>()
            r0.messageOwner = r7
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            r8.<init>()
            r7.action = r8
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.participant
            int r8 = r8.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.participant
            int r8 = r8.user_id
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            int r10 = r10.from_id
            if (r8 != r10) goto L_0x020b
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x01fc
            r6 = 2131625095(0x7f0e0487, float:1.8877388E38)
            java.lang.String r7 = "EventLogGroupJoined"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x01fc:
            r7 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x020b:
            r6 = 2131625056(0x7f0e0460, float:1.887731E38)
            java.lang.String r8 = "EventLogAdded"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r11, r7)
            r0.messageText = r6
            java.lang.CharSequence r6 = r0.messageText
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e97
        L_0x0224:
            boolean r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin
            java.lang.String r10 = "%1$s"
            r12 = 10
            if (r6 == 0) goto L_0x045f
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r0.messageOwner = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r7.prev_participant
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator
            if (r8 != 0) goto L_0x0279
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.new_participant
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator
            if (r7 == 0) goto L_0x0279
            r7 = 2131625064(0x7f0e0468, float:1.8877325E38)
            java.lang.String r8 = "EventLogChangedOwnership"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            int r8 = r7.indexOf(r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            java.lang.Object[] r11 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r12 = r12.entities
            java.lang.String r6 = r0.getUserName(r6, r12, r8)
            r11[r15] = r6
            java.lang.String r6 = java.lang.String.format(r7, r11)
            r10.<init>(r6)
            goto L_0x0457
        L_0x0279:
            r7 = 2131625107(0x7f0e0493, float:1.8877413E38)
            java.lang.String r8 = "EventLogPromoted"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            int r8 = r7.indexOf(r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            java.lang.Object[] r11 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r14 = r14.entities
            java.lang.String r6 = r0.getUserName(r6, r14, r8)
            r11[r15] = r6
            java.lang.String r6 = java.lang.String.format(r7, r11)
            r10.<init>(r6)
            java.lang.String r6 = "\n"
            r10.append(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r6.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = r7.admin_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r6 = r6.new_participant
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r6 = r6.admin_rights
            if (r7 != 0) goto L_0x02b1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r7.<init>()
        L_0x02b1:
            if (r6 != 0) goto L_0x02b8
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r6 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r6.<init>()
        L_0x02b8:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r11 = r8.prev_participant
            java.lang.String r11 = r11.rank
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.new_participant
            java.lang.String r8 = r8.rank
            boolean r8 = android.text.TextUtils.equals(r11, r8)
            if (r8 != 0) goto L_0x0311
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.new_participant
            java.lang.String r8 = r8.rank
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x02ee
            r10.append(r12)
            r8 = 45
            r10.append(r8)
            r11 = 32
            r10.append(r11)
            r14 = 2131625117(0x7f0e049d, float:1.8877433E38)
            java.lang.String r8 = "EventLogPromotedRemovedTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r14)
            r10.append(r8)
            goto L_0x0311
        L_0x02ee:
            r11 = 32
            r10.append(r12)
            r8 = 43
            r10.append(r8)
            r10.append(r11)
            r11 = 2131625118(0x7f0e049e, float:1.8877435E38)
            java.lang.Object[] r14 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.new_participant
            java.lang.String r8 = r8.rank
            r14[r15] = r8
            java.lang.String r8 = "EventLogPromotedTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14)
            r10.append(r8)
        L_0x0311:
            boolean r8 = r7.change_info
            boolean r11 = r6.change_info
            if (r8 == r11) goto L_0x0341
            r10.append(r12)
            boolean r8 = r6.change_info
            if (r8 == 0) goto L_0x0321
            r8 = 43
            goto L_0x0323
        L_0x0321:
            r8 = 45
        L_0x0323:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x0335
            r8 = 2131625112(0x7f0e0498, float:1.8877423E38)
            java.lang.String r11 = "EventLogPromotedChangeGroupInfo"
            goto L_0x033a
        L_0x0335:
            r8 = 2131625111(0x7f0e0497, float:1.887742E38)
            java.lang.String r11 = "EventLogPromotedChangeChannelInfo"
        L_0x033a:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x0341:
            boolean r8 = r4.megagroup
            if (r8 != 0) goto L_0x0391
            boolean r8 = r7.post_messages
            boolean r11 = r6.post_messages
            if (r8 == r11) goto L_0x036b
            r10.append(r12)
            boolean r8 = r6.post_messages
            if (r8 == 0) goto L_0x0355
            r8 = 43
            goto L_0x0357
        L_0x0355:
            r8 = 45
        L_0x0357:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            r8 = 2131625116(0x7f0e049c, float:1.887743E38)
            java.lang.String r11 = "EventLogPromotedPostMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x036b:
            boolean r8 = r7.edit_messages
            boolean r11 = r6.edit_messages
            if (r8 == r11) goto L_0x0391
            r10.append(r12)
            boolean r8 = r6.edit_messages
            if (r8 == 0) goto L_0x037b
            r8 = 43
            goto L_0x037d
        L_0x037b:
            r8 = 45
        L_0x037d:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            r8 = 2131625114(0x7f0e049a, float:1.8877427E38)
            java.lang.String r11 = "EventLogPromotedEditMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x0391:
            boolean r8 = r7.delete_messages
            boolean r11 = r6.delete_messages
            if (r8 == r11) goto L_0x03b7
            r10.append(r12)
            boolean r8 = r6.delete_messages
            if (r8 == 0) goto L_0x03a1
            r8 = 43
            goto L_0x03a3
        L_0x03a1:
            r8 = 45
        L_0x03a3:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            r8 = 2131625113(0x7f0e0499, float:1.8877425E38)
            java.lang.String r11 = "EventLogPromotedDeleteMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x03b7:
            boolean r8 = r7.add_admins
            boolean r11 = r6.add_admins
            if (r8 == r11) goto L_0x03dd
            r10.append(r12)
            boolean r8 = r6.add_admins
            if (r8 == 0) goto L_0x03c7
            r8 = 43
            goto L_0x03c9
        L_0x03c7:
            r8 = 45
        L_0x03c9:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            r8 = 2131625108(0x7f0e0494, float:1.8877415E38)
            java.lang.String r11 = "EventLogPromotedAddAdmins"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x03dd:
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x0407
            boolean r8 = r7.ban_users
            boolean r11 = r6.ban_users
            if (r8 == r11) goto L_0x0407
            r10.append(r12)
            boolean r8 = r6.ban_users
            if (r8 == 0) goto L_0x03f1
            r8 = 43
            goto L_0x03f3
        L_0x03f1:
            r8 = 45
        L_0x03f3:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            r8 = 2131625110(0x7f0e0496, float:1.8877419E38)
            java.lang.String r11 = "EventLogPromotedBanUsers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x0407:
            boolean r8 = r7.invite_users
            boolean r11 = r6.invite_users
            if (r8 == r11) goto L_0x042d
            r10.append(r12)
            boolean r8 = r6.invite_users
            if (r8 == 0) goto L_0x0417
            r8 = 43
            goto L_0x0419
        L_0x0417:
            r8 = 45
        L_0x0419:
            r10.append(r8)
            r8 = 32
            r10.append(r8)
            r8 = 2131625109(0x7f0e0495, float:1.8877417E38)
            java.lang.String r11 = "EventLogPromotedAddUsers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r10.append(r8)
        L_0x042d:
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x0457
            boolean r7 = r7.pin_messages
            boolean r8 = r6.pin_messages
            if (r7 == r8) goto L_0x0457
            r10.append(r12)
            boolean r6 = r6.pin_messages
            if (r6 == 0) goto L_0x0441
            r6 = 43
            goto L_0x0443
        L_0x0441:
            r6 = 45
        L_0x0443:
            r10.append(r6)
            r6 = 32
            r10.append(r6)
            r6 = 2131625115(0x7f0e049b, float:1.8877429E38)
            java.lang.String r7 = "EventLogPromotedPinMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r10.append(r6)
        L_0x0457:
            java.lang.String r6 = r10.toString()
            r0.messageText = r6
            goto L_0x0e97
        L_0x045f:
            r18 = 43
            boolean r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights
            if (r6 == 0) goto L_0x0607
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights) r8
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r0.messageOwner = r6
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = r8.prev_banned_rights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r8.new_banned_rights
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r10 = 2131625069(0x7f0e046d, float:1.8877336E38)
            java.lang.String r11 = "EventLogDefaultPermissions"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r8.<init>(r10)
            if (r6 != 0) goto L_0x0487
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r6.<init>()
        L_0x0487:
            if (r7 != 0) goto L_0x048e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r7.<init>()
        L_0x048e:
            boolean r10 = r6.send_messages
            boolean r11 = r7.send_messages
            if (r10 == r11) goto L_0x04b9
            r8.append(r12)
            r8.append(r12)
            boolean r10 = r7.send_messages
            if (r10 != 0) goto L_0x04a1
            r10 = 43
            goto L_0x04a3
        L_0x04a1:
            r10 = 45
        L_0x04a3:
            r8.append(r10)
            r10 = 32
            r8.append(r10)
            r10 = 2131625133(0x7f0e04ad, float:1.8877465E38)
            java.lang.String r11 = "EventLogRestrictedSendMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r8.append(r10)
            r10 = 1
            goto L_0x04ba
        L_0x04b9:
            r10 = 0
        L_0x04ba:
            boolean r11 = r6.send_stickers
            boolean r14 = r7.send_stickers
            if (r11 != r14) goto L_0x04d2
            boolean r11 = r6.send_inline
            boolean r14 = r7.send_inline
            if (r11 != r14) goto L_0x04d2
            boolean r11 = r6.send_gifs
            boolean r14 = r7.send_gifs
            if (r11 != r14) goto L_0x04d2
            boolean r11 = r6.send_games
            boolean r14 = r7.send_games
            if (r11 == r14) goto L_0x04f8
        L_0x04d2:
            if (r10 != 0) goto L_0x04d8
            r8.append(r12)
            r10 = 1
        L_0x04d8:
            r8.append(r12)
            boolean r11 = r7.send_stickers
            if (r11 != 0) goto L_0x04e2
            r11 = 43
            goto L_0x04e4
        L_0x04e2:
            r11 = 45
        L_0x04e4:
            r8.append(r11)
            r11 = 32
            r8.append(r11)
            r11 = 2131625135(0x7f0e04af, float:1.887747E38)
            java.lang.String r14 = "EventLogRestrictedSendStickers"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r8.append(r11)
        L_0x04f8:
            boolean r11 = r6.send_media
            boolean r14 = r7.send_media
            if (r11 == r14) goto L_0x0524
            if (r10 != 0) goto L_0x0504
            r8.append(r12)
            r10 = 1
        L_0x0504:
            r8.append(r12)
            boolean r11 = r7.send_media
            if (r11 != 0) goto L_0x050e
            r11 = 43
            goto L_0x0510
        L_0x050e:
            r11 = 45
        L_0x0510:
            r8.append(r11)
            r11 = 32
            r8.append(r11)
            r11 = 2131625132(0x7f0e04ac, float:1.8877463E38)
            java.lang.String r14 = "EventLogRestrictedSendMedia"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r8.append(r11)
        L_0x0524:
            boolean r11 = r6.send_polls
            boolean r14 = r7.send_polls
            if (r11 == r14) goto L_0x0550
            if (r10 != 0) goto L_0x0530
            r8.append(r12)
            r10 = 1
        L_0x0530:
            r8.append(r12)
            boolean r11 = r7.send_polls
            if (r11 != 0) goto L_0x053a
            r11 = 43
            goto L_0x053c
        L_0x053a:
            r11 = 45
        L_0x053c:
            r8.append(r11)
            r11 = 32
            r8.append(r11)
            r11 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            java.lang.String r14 = "EventLogRestrictedSendPolls"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r8.append(r11)
        L_0x0550:
            boolean r11 = r6.embed_links
            boolean r14 = r7.embed_links
            if (r11 == r14) goto L_0x057c
            if (r10 != 0) goto L_0x055c
            r8.append(r12)
            r10 = 1
        L_0x055c:
            r8.append(r12)
            boolean r11 = r7.embed_links
            if (r11 != 0) goto L_0x0566
            r11 = 43
            goto L_0x0568
        L_0x0566:
            r11 = 45
        L_0x0568:
            r8.append(r11)
            r11 = 32
            r8.append(r11)
            r11 = 2131625131(0x7f0e04ab, float:1.8877461E38)
            java.lang.String r14 = "EventLogRestrictedSendEmbed"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r8.append(r11)
        L_0x057c:
            boolean r11 = r6.change_info
            boolean r14 = r7.change_info
            if (r11 == r14) goto L_0x05a8
            if (r10 != 0) goto L_0x0588
            r8.append(r12)
            r10 = 1
        L_0x0588:
            r8.append(r12)
            boolean r11 = r7.change_info
            if (r11 != 0) goto L_0x0592
            r11 = 43
            goto L_0x0594
        L_0x0592:
            r11 = 45
        L_0x0594:
            r8.append(r11)
            r11 = 32
            r8.append(r11)
            r11 = 2131625127(0x7f0e04a7, float:1.8877453E38)
            java.lang.String r14 = "EventLogRestrictedChangeInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r8.append(r11)
        L_0x05a8:
            boolean r11 = r6.invite_users
            boolean r14 = r7.invite_users
            if (r11 == r14) goto L_0x05d4
            if (r10 != 0) goto L_0x05b4
            r8.append(r12)
            r10 = 1
        L_0x05b4:
            r8.append(r12)
            boolean r11 = r7.invite_users
            if (r11 != 0) goto L_0x05be
            r11 = 43
            goto L_0x05c0
        L_0x05be:
            r11 = 45
        L_0x05c0:
            r8.append(r11)
            r11 = 32
            r8.append(r11)
            r11 = 2131625128(0x7f0e04a8, float:1.8877455E38)
            java.lang.String r14 = "EventLogRestrictedInviteUsers"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r8.append(r11)
        L_0x05d4:
            boolean r6 = r6.pin_messages
            boolean r11 = r7.pin_messages
            if (r6 == r11) goto L_0x05ff
            if (r10 != 0) goto L_0x05df
            r8.append(r12)
        L_0x05df:
            r8.append(r12)
            boolean r6 = r7.pin_messages
            if (r6 != 0) goto L_0x05e9
            r6 = 43
            goto L_0x05eb
        L_0x05e9:
            r6 = 45
        L_0x05eb:
            r8.append(r6)
            r6 = 32
            r8.append(r6)
            r6 = 2131625129(0x7f0e04a9, float:1.8877457E38)
            java.lang.String r7 = "EventLogRestrictedPinMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r8.append(r6)
        L_0x05ff:
            java.lang.String r6 = r8.toString()
            r0.messageText = r6
            goto L_0x0e97
        L_0x0607:
            boolean r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan
            r12 = 60
            if (r6 == 0) goto L_0x08fa
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r0.messageOwner = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r7.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r8 = r8.banned_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.new_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r7.banned_rights
            boolean r11 = r4.megagroup
            if (r11 == 0) goto L_0x08c5
            if (r7 == 0) goto L_0x0646
            boolean r11 = r7.view_messages
            if (r11 == 0) goto L_0x0646
            if (r7 == 0) goto L_0x08c5
            if (r8 == 0) goto L_0x08c5
            int r11 = r7.until_date
            int r14 = r8.until_date
            if (r11 == r14) goto L_0x08c5
        L_0x0646:
            if (r7 == 0) goto L_0x06c7
            boolean r11 = org.telegram.messenger.AndroidUtilities.isBannedForever(r7)
            if (r11 != 0) goto L_0x06c7
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r14 = r7.until_date
            int r15 = r1.date
            int r14 = r14 - r15
            int r15 = r14 / 60
            int r15 = r15 / r12
            int r15 = r15 / 24
            int r22 = r15 * 60
            int r22 = r22 * 60
            int r22 = r22 * 24
            int r14 = r14 - r22
            int r22 = r14 / 60
            int r9 = r22 / 60
            int r22 = r9 * 60
            int r22 = r22 * 60
            int r14 = r14 - r22
            int r14 = r14 / r12
            r2 = 3
            r12 = 0
            r17 = 0
        L_0x0674:
            if (r12 >= r2) goto L_0x06d5
            if (r12 != 0) goto L_0x0683
            if (r15 == 0) goto L_0x0698
            java.lang.String r2 = "Days"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r15)
        L_0x0680:
            int r17 = r17 + 1
            goto L_0x0699
        L_0x0683:
            r2 = 1
            if (r12 != r2) goto L_0x068f
            if (r9 == 0) goto L_0x0698
            java.lang.String r2 = "Hours"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9)
            goto L_0x0680
        L_0x068f:
            if (r14 == 0) goto L_0x0698
            java.lang.String r2 = "Minutes"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r14)
            goto L_0x0680
        L_0x0698:
            r2 = 0
        L_0x0699:
            r24 = r17
            r17 = r9
            r9 = r24
            if (r2 == 0) goto L_0x06b5
            int r23 = r11.length()
            if (r23 <= 0) goto L_0x06af
            r23 = r14
            java.lang.String r14 = ", "
            r11.append(r14)
            goto L_0x06b1
        L_0x06af:
            r23 = r14
        L_0x06b1:
            r11.append(r2)
            goto L_0x06b7
        L_0x06b5:
            r23 = r14
        L_0x06b7:
            r2 = 2
            if (r9 != r2) goto L_0x06bb
            goto L_0x06d5
        L_0x06bb:
            int r12 = r12 + 1
            r14 = r23
            r2 = 3
            r24 = r17
            r17 = r9
            r9 = r24
            goto L_0x0674
        L_0x06c7:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r2 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            java.lang.String r9 = "UserRestrictionsUntilForever"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r11.<init>(r2)
        L_0x06d5:
            r2 = 2131625136(0x7f0e04b0, float:1.8877471E38)
            java.lang.String r9 = "EventLogRestrictedUntil"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            int r9 = r2.indexOf(r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r14 = r14.entities
            java.lang.String r6 = r0.getUserName(r6, r14, r9)
            r9 = 0
            r12[r9] = r6
            java.lang.String r6 = r11.toString()
            r9 = 1
            r12[r9] = r6
            java.lang.String r2 = java.lang.String.format(r2, r12)
            r10.<init>(r2)
            if (r8 != 0) goto L_0x0707
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r8 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r8.<init>()
        L_0x0707:
            if (r7 != 0) goto L_0x070e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r7.<init>()
        L_0x070e:
            boolean r2 = r8.view_messages
            boolean r6 = r7.view_messages
            if (r2 == r6) goto L_0x073b
            r2 = 10
            r10.append(r2)
            r10.append(r2)
            boolean r2 = r7.view_messages
            if (r2 != 0) goto L_0x0723
            r2 = 43
            goto L_0x0725
        L_0x0723:
            r2 = 45
        L_0x0725:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625130(0x7f0e04aa, float:1.887746E38)
            java.lang.String r6 = "EventLogRestrictedReadMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
            r9 = 1
            goto L_0x073c
        L_0x073b:
            r9 = 0
        L_0x073c:
            boolean r2 = r8.send_messages
            boolean r6 = r7.send_messages
            if (r2 == r6) goto L_0x076a
            r2 = 10
            if (r9 != 0) goto L_0x074a
            r10.append(r2)
            r9 = 1
        L_0x074a:
            r10.append(r2)
            boolean r2 = r7.send_messages
            if (r2 != 0) goto L_0x0754
            r2 = 43
            goto L_0x0756
        L_0x0754:
            r2 = 45
        L_0x0756:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625133(0x7f0e04ad, float:1.8877465E38)
            java.lang.String r6 = "EventLogRestrictedSendMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x076a:
            boolean r2 = r8.send_stickers
            boolean r6 = r7.send_stickers
            if (r2 != r6) goto L_0x0782
            boolean r2 = r8.send_inline
            boolean r6 = r7.send_inline
            if (r2 != r6) goto L_0x0782
            boolean r2 = r8.send_gifs
            boolean r6 = r7.send_gifs
            if (r2 != r6) goto L_0x0782
            boolean r2 = r8.send_games
            boolean r6 = r7.send_games
            if (r2 == r6) goto L_0x07aa
        L_0x0782:
            r2 = 10
            if (r9 != 0) goto L_0x078a
            r10.append(r2)
            r9 = 1
        L_0x078a:
            r10.append(r2)
            boolean r2 = r7.send_stickers
            if (r2 != 0) goto L_0x0794
            r2 = 43
            goto L_0x0796
        L_0x0794:
            r2 = 45
        L_0x0796:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625135(0x7f0e04af, float:1.887747E38)
            java.lang.String r6 = "EventLogRestrictedSendStickers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x07aa:
            boolean r2 = r8.send_media
            boolean r6 = r7.send_media
            if (r2 == r6) goto L_0x07d8
            r2 = 10
            if (r9 != 0) goto L_0x07b8
            r10.append(r2)
            r9 = 1
        L_0x07b8:
            r10.append(r2)
            boolean r2 = r7.send_media
            if (r2 != 0) goto L_0x07c2
            r2 = 43
            goto L_0x07c4
        L_0x07c2:
            r2 = 45
        L_0x07c4:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625132(0x7f0e04ac, float:1.8877463E38)
            java.lang.String r6 = "EventLogRestrictedSendMedia"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x07d8:
            boolean r2 = r8.send_polls
            boolean r6 = r7.send_polls
            if (r2 == r6) goto L_0x0806
            r2 = 10
            if (r9 != 0) goto L_0x07e6
            r10.append(r2)
            r9 = 1
        L_0x07e6:
            r10.append(r2)
            boolean r2 = r7.send_polls
            if (r2 != 0) goto L_0x07f0
            r2 = 43
            goto L_0x07f2
        L_0x07f0:
            r2 = 45
        L_0x07f2:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            java.lang.String r6 = "EventLogRestrictedSendPolls"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x0806:
            boolean r2 = r8.embed_links
            boolean r6 = r7.embed_links
            if (r2 == r6) goto L_0x0834
            r2 = 10
            if (r9 != 0) goto L_0x0814
            r10.append(r2)
            r9 = 1
        L_0x0814:
            r10.append(r2)
            boolean r2 = r7.embed_links
            if (r2 != 0) goto L_0x081e
            r2 = 43
            goto L_0x0820
        L_0x081e:
            r2 = 45
        L_0x0820:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625131(0x7f0e04ab, float:1.8877461E38)
            java.lang.String r6 = "EventLogRestrictedSendEmbed"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x0834:
            boolean r2 = r8.change_info
            boolean r6 = r7.change_info
            if (r2 == r6) goto L_0x0862
            r2 = 10
            if (r9 != 0) goto L_0x0842
            r10.append(r2)
            r9 = 1
        L_0x0842:
            r10.append(r2)
            boolean r2 = r7.change_info
            if (r2 != 0) goto L_0x084c
            r2 = 43
            goto L_0x084e
        L_0x084c:
            r2 = 45
        L_0x084e:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625127(0x7f0e04a7, float:1.8877453E38)
            java.lang.String r6 = "EventLogRestrictedChangeInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x0862:
            boolean r2 = r8.invite_users
            boolean r6 = r7.invite_users
            if (r2 == r6) goto L_0x0890
            r2 = 10
            if (r9 != 0) goto L_0x0870
            r10.append(r2)
            r9 = 1
        L_0x0870:
            r10.append(r2)
            boolean r2 = r7.invite_users
            if (r2 != 0) goto L_0x087a
            r2 = 43
            goto L_0x087c
        L_0x087a:
            r2 = 45
        L_0x087c:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625128(0x7f0e04a8, float:1.8877455E38)
            java.lang.String r6 = "EventLogRestrictedInviteUsers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x0890:
            boolean r2 = r8.pin_messages
            boolean r6 = r7.pin_messages
            if (r2 == r6) goto L_0x08bd
            r2 = 10
            if (r9 != 0) goto L_0x089d
            r10.append(r2)
        L_0x089d:
            r10.append(r2)
            boolean r2 = r7.pin_messages
            if (r2 != 0) goto L_0x08a7
            r2 = 43
            goto L_0x08a9
        L_0x08a7:
            r2 = 45
        L_0x08a9:
            r10.append(r2)
            r2 = 32
            r10.append(r2)
            r2 = 2131625129(0x7f0e04a9, float:1.8877457E38)
            java.lang.String r6 = "EventLogRestrictedPinMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r10.append(r2)
        L_0x08bd:
            java.lang.String r2 = r10.toString()
            r0.messageText = r2
            goto L_0x0e97
        L_0x08c5:
            if (r7 == 0) goto L_0x08d7
            if (r8 == 0) goto L_0x08cd
            boolean r2 = r7.view_messages
            if (r2 == 0) goto L_0x08d7
        L_0x08cd:
            r2 = 2131625067(0x7f0e046b, float:1.8877331E38)
            java.lang.String r7 = "EventLogChannelRestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            goto L_0x08e0
        L_0x08d7:
            r2 = 2131625068(0x7f0e046c, float:1.8877334E38)
            java.lang.String r7 = "EventLogChannelUnrestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
        L_0x08e0:
            int r7 = r2.indexOf(r10)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            java.lang.String r6 = r0.getUserName(r6, r8, r7)
            r7 = 0
            r9[r7] = r6
            java.lang.String r2 = java.lang.String.format(r2, r9)
            r0.messageText = r2
            goto L_0x0e97
        L_0x08fa:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned
            if (r2 == 0) goto L_0x0977
            if (r5 == 0) goto L_0x094d
            int r2 = r5.id
            r6 = 136817688(0x827aCLASSNAME, float:5.045703E-34)
            if (r2 != r6) goto L_0x094d
            org.telegram.tgnet.TLRPC$Message r2 = r8.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            if (r2 == 0) goto L_0x094d
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            org.telegram.tgnet.TLRPC$Message r6 = r6.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            int r6 = r6.channel_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            org.telegram.tgnet.TLRPC$Message r6 = r6.message
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r6 == 0) goto L_0x093c
            r6 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            java.lang.String r7 = "EventLogUnpinnedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r14, r2)
            r0.messageText = r2
            goto L_0x0e97
        L_0x093c:
            r6 = 2131625104(0x7f0e0490, float:1.8877407E38)
            java.lang.String r7 = "EventLogPinnedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r14, r2)
            r0.messageText = r2
            goto L_0x0e97
        L_0x094d:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            org.telegram.tgnet.TLRPC$Message r2 = r2.message
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r2 == 0) goto L_0x0966
            r2 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            java.lang.String r6 = "EventLogUnpinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0966:
            r2 = 2131625104(0x7f0e0490, float:1.8877407E38)
            java.lang.String r6 = "EventLogPinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0977:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionStopPoll
            if (r2 == 0) goto L_0x09ad
            org.telegram.tgnet.TLRPC$Message r2 = r8.message
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r6 == 0) goto L_0x099c
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$TL_poll r2 = r2.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x099c
            r2 = 2131625139(0x7f0e04b3, float:1.8877478E38)
            java.lang.String r6 = "EventLogStopQuiz"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x099c:
            r2 = 2131625138(0x7f0e04b2, float:1.8877475E38)
            java.lang.String r6 = "EventLogStopPoll"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x09ad:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures
            if (r2 == 0) goto L_0x09d9
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures) r8
            boolean r2 = r8.new_value
            if (r2 == 0) goto L_0x09c8
            r2 = 2131625145(0x7f0e04b9, float:1.887749E38)
            java.lang.String r6 = "EventLogToggledSignaturesOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x09c8:
            r2 = 2131625144(0x7f0e04b8, float:1.8877488E38)
            java.lang.String r6 = "EventLogToggledSignaturesOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x09d9:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites
            if (r2 == 0) goto L_0x0a05
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites) r8
            boolean r2 = r8.new_value
            if (r2 == 0) goto L_0x09f4
            r2 = 2131625143(0x7f0e04b7, float:1.8877486E38)
            java.lang.String r6 = "EventLogToggledInvitesOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x09f4:
            r2 = 2131625142(0x7f0e04b6, float:1.8877484E38)
            java.lang.String r6 = "EventLogToggledInvitesOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0a05:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage
            if (r2 == 0) goto L_0x0a1a
            r2 = 2131625070(0x7f0e046e, float:1.8877338E38)
            java.lang.String r6 = "EventLogDeletedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0a1a:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat
            if (r2 == 0) goto L_0x0acb
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r2
            int r2 = r2.new_value
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r8
            int r6 = r8.prev_value
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0a7b
            if (r2 != 0) goto L_0x0a54
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            r6 = 2131625122(0x7f0e04a2, float:1.8877443E38)
            java.lang.String r7 = "EventLogRemovedLinkedChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r6 = r0.messageText
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r11, r2)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0a54:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r6.getChat(r2)
            r6 = 2131625061(0x7f0e0465, float:1.887732E38)
            java.lang.String r7 = "EventLogChangedLinkedChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r6 = r0.messageText
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r11, r2)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0a7b:
            if (r2 != 0) goto L_0x0aa4
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            r6 = 2131625123(0x7f0e04a3, float:1.8877445E38)
            java.lang.String r7 = "EventLogRemovedLinkedGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r6 = r0.messageText
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r11, r2)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0aa4:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r6.getChat(r2)
            r6 = 2131625062(0x7f0e0466, float:1.8877321E38)
            java.lang.String r7 = "EventLogChangedLinkedGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r6 = r0.messageText
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r11, r2)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0acb:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden
            if (r2 == 0) goto L_0x0af7
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden) r8
            boolean r2 = r8.new_value
            if (r2 == 0) goto L_0x0ae6
            r2 = 2131625140(0x7f0e04b4, float:1.887748E38)
            java.lang.String r6 = "EventLogToggledInvitesHistoryOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0ae6:
            r2 = 2131625141(0x7f0e04b5, float:1.8877482E38)
            java.lang.String r6 = "EventLogToggledInvitesHistoryOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0af7:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout
            if (r2 == 0) goto L_0x0b79
            boolean r2 = r4.megagroup
            if (r2 == 0) goto L_0x0b05
            r2 = 2131625075(0x7f0e0473, float:1.8877348E38)
            java.lang.String r6 = "EventLogEditedGroupDescription"
            goto L_0x0b0a
        L_0x0b05:
            r2 = 2131625072(0x7f0e0470, float:1.8877342E38)
            java.lang.String r6 = "EventLogEditedChannelDescription"
        L_0x0b0a:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r2 = 0
            r6.out = r2
            r6.unread = r2
            int r2 = r1.user_id
            r6.from_id = r2
            r6.to_id = r7
            int r2 = r1.date
            r6.date = r2
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            r7 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r7
            java.lang.String r7 = r7.new_value
            r6.message = r7
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r2
            java.lang.String r2 = r2.prev_value
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0b70
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r2.<init>()
            r6.media = r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$TL_webPage r7 = new org.telegram.tgnet.TLRPC$TL_webPage
            r7.<init>()
            r2.webpage = r7
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            r7 = 10
            r2.flags = r7
            r2.display_url = r13
            r2.url = r13
            r7 = 2131625105(0x7f0e0491, float:1.8877409E38)
            java.lang.String r8 = "EventLogPreviousGroupDescription"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.site_name = r7
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r7
            java.lang.String r7 = r7.prev_value
            r2.description = r7
            goto L_0x0e98
        L_0x0b70:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r2.<init>()
            r6.media = r2
            goto L_0x0e98
        L_0x0b79:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername
            if (r2 == 0) goto L_0x0c7a
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r8
            java.lang.String r2 = r8.new_value
            boolean r6 = android.text.TextUtils.isEmpty(r2)
            if (r6 != 0) goto L_0x0ba1
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x0b91
            r6 = 2131625060(0x7f0e0464, float:1.8877317E38)
            java.lang.String r8 = "EventLogChangedGroupLink"
            goto L_0x0b96
        L_0x0b91:
            r6 = 2131625059(0x7f0e0463, float:1.8877315E38)
            java.lang.String r8 = "EventLogChangedChannelLink"
        L_0x0b96:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0bba
        L_0x0ba1:
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x0bab
            r6 = 2131625121(0x7f0e04a1, float:1.887744E38)
            java.lang.String r8 = "EventLogRemovedGroupLink"
            goto L_0x0bb0
        L_0x0bab:
            r6 = 2131625119(0x7f0e049f, float:1.8877437E38)
            java.lang.String r8 = "EventLogRemovedChannelLink"
        L_0x0bb0:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
        L_0x0bba:
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r8 = 0
            r6.out = r8
            r6.unread = r8
            int r8 = r1.user_id
            r6.from_id = r8
            r6.to_id = r7
            int r7 = r1.date
            r6.date = r7
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x0bf8
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "https://"
            r7.append(r8)
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.String r8 = r8.linkPrefix
            r7.append(r8)
            java.lang.String r8 = "/"
            r7.append(r8)
            r7.append(r2)
            java.lang.String r2 = r7.toString()
            r6.message = r2
            goto L_0x0bfa
        L_0x0bf8:
            r6.message = r13
        L_0x0bfa:
            org.telegram.tgnet.TLRPC$TL_messageEntityUrl r2 = new org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            r2.<init>()
            r7 = 0
            r2.offset = r7
            java.lang.String r7 = r6.message
            int r7 = r7.length()
            r2.length = r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities
            r7.add(r2)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r2
            java.lang.String r2 = r2.prev_value
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r2.<init>()
            r6.media = r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$TL_webPage r7 = new org.telegram.tgnet.TLRPC$TL_webPage
            r7.<init>()
            r2.webpage = r7
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            r7 = 10
            r2.flags = r7
            r2.display_url = r13
            r2.url = r13
            r7 = 2131625106(0x7f0e0492, float:1.887741E38)
            java.lang.String r8 = "EventLogPreviousLink"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.site_name = r7
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "https://"
            r7.append(r8)
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.String r8 = r8.linkPrefix
            r7.append(r8)
            java.lang.String r8 = "/"
            r7.append(r8)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r8
            java.lang.String r8 = r8.prev_value
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r2.description = r7
            goto L_0x0e98
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r2.<init>()
            r6.media = r2
            goto L_0x0e98
        L_0x0c7a:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage
            if (r2 == 0) goto L_0x0dc7
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r2 = 0
            r6.out = r2
            r6.unread = r2
            int r2 = r1.user_id
            r6.from_id = r2
            r6.to_id = r7
            int r2 = r1.date
            r6.date = r2
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            r7 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r7
            org.telegram.tgnet.TLRPC$Message r7 = r7.new_message
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.prev_message
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            if (r8 == 0) goto L_0x0d61
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty
            if (r9 != 0) goto L_0x0d61
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r8 != 0) goto L_0x0d61
            java.lang.String r8 = r7.message
            java.lang.String r9 = r2.message
            boolean r8 = android.text.TextUtils.equals(r8, r9)
            r9 = 1
            r8 = r8 ^ r9
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            java.lang.Class r9 = r9.getClass()
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r2.media
            java.lang.Class r10 = r10.getClass()
            if (r9 != r10) goto L_0x0cec
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            org.telegram.tgnet.TLRPC$Photo r9 = r9.photo
            if (r9 == 0) goto L_0x0cd5
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r2.media
            org.telegram.tgnet.TLRPC$Photo r10 = r10.photo
            if (r10 == 0) goto L_0x0cd5
            long r11 = r9.id
            long r9 = r10.id
            int r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r15 != 0) goto L_0x0cec
        L_0x0cd5:
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            org.telegram.tgnet.TLRPC$Document r9 = r9.document
            if (r9 == 0) goto L_0x0cea
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r2.media
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            if (r10 == 0) goto L_0x0cea
            long r11 = r9.id
            long r9 = r10.id
            int r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r15 == 0) goto L_0x0cea
            goto L_0x0cec
        L_0x0cea:
            r9 = 0
            goto L_0x0ced
        L_0x0cec:
            r9 = 1
        L_0x0ced:
            if (r9 == 0) goto L_0x0d01
            if (r8 == 0) goto L_0x0d01
            r9 = 2131625079(0x7f0e0477, float:1.8877356E38)
            java.lang.String r10 = "EventLogEditedMediaCaption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.CharSequence r9 = r0.replaceWithLink(r9, r14, r5)
            r0.messageText = r9
            goto L_0x0d22
        L_0x0d01:
            if (r8 == 0) goto L_0x0d13
            r9 = 2131625071(0x7f0e046f, float:1.887734E38)
            java.lang.String r10 = "EventLogEditedCaption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.CharSequence r9 = r0.replaceWithLink(r9, r14, r5)
            r0.messageText = r9
            goto L_0x0d22
        L_0x0d13:
            r9 = 2131625078(0x7f0e0476, float:1.8877354E38)
            java.lang.String r10 = "EventLogEditedMedia"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.CharSequence r9 = r0.replaceWithLink(r9, r14, r5)
            r0.messageText = r9
        L_0x0d22:
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            r6.media = r9
            if (r8 == 0) goto L_0x0db3
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media
            org.telegram.tgnet.TLRPC$TL_webPage r9 = new org.telegram.tgnet.TLRPC$TL_webPage
            r9.<init>()
            r8.webpage = r9
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            r9 = 2131625101(0x7f0e048d, float:1.88774E38)
            java.lang.String r10 = "EventLogOriginalCaption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.site_name = r9
            java.lang.String r8 = r2.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0d58
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            r8 = 2131625102(0x7f0e048e, float:1.8877402E38)
            java.lang.String r9 = "EventLogOriginalCaptionEmpty"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.description = r8
            goto L_0x0db3
        L_0x0d58:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r2 = r2.message
            r8.description = r2
            goto L_0x0db3
        L_0x0d61:
            r8 = 2131625080(0x7f0e0478, float:1.8877358E38)
            java.lang.String r9 = "EventLogEditedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r14, r5)
            r0.messageText = r8
            java.lang.String r8 = r7.message
            r6.message = r8
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r8 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r8.<init>()
            r6.media = r8
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media
            org.telegram.tgnet.TLRPC$TL_webPage r9 = new org.telegram.tgnet.TLRPC$TL_webPage
            r9.<init>()
            r8.webpage = r9
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            r9 = 2131625103(0x7f0e048f, float:1.8877404E38)
            java.lang.String r10 = "EventLogOriginalMessages"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.site_name = r9
            java.lang.String r8 = r2.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0dab
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            r8 = 2131625102(0x7f0e048e, float:1.8877402E38)
            java.lang.String r9 = "EventLogOriginalCaptionEmpty"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.description = r8
            goto L_0x0db3
        L_0x0dab:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r2 = r2.message
            r8.description = r2
        L_0x0db3:
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r7.reply_markup
            r6.reply_markup = r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r6.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x0e98
            r7 = 10
            r2.flags = r7
            r2.display_url = r13
            r2.url = r13
            goto L_0x0e98
        L_0x0dc7:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet
            if (r2 == 0) goto L_0x0dfd
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r2
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r2.new_stickerset
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r8
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r8.new_stickerset
            if (r2 == 0) goto L_0x0dec
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty
            if (r2 == 0) goto L_0x0ddb
            goto L_0x0dec
        L_0x0ddb:
            r2 = 2131625065(0x7f0e0469, float:1.8877327E38)
            java.lang.String r6 = "EventLogChangedStickersSet"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0dec:
            r2 = 2131625125(0x7f0e04a5, float:1.887745E38)
            java.lang.String r6 = "EventLogRemovedStickersSet"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0dfd:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation
            if (r2 == 0) goto L_0x0e34
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation) r8
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r8.new_value
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocationEmpty
            if (r6 == 0) goto L_0x0e1a
            r2 = 2131625124(0x7f0e04a4, float:1.8877447E38)
            java.lang.String r6 = "EventLogRemovedLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0e1a:
            org.telegram.tgnet.TLRPC$TL_channelLocation r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2
            r6 = 2131625063(0x7f0e0467, float:1.8877323E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r2 = r2.address
            r7 = 0
            r8[r7] = r2
            java.lang.String r2 = "EventLogChangedLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0e34:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode
            if (r2 == 0) goto L_0x0e81
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode) r8
            int r2 = r8.new_value
            if (r2 != 0) goto L_0x0e4e
            r2 = 2131625146(0x7f0e04ba, float:1.8877492E38)
            java.lang.String r6 = "EventLogToggledSlowmodeOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0e4e:
            if (r2 >= r12) goto L_0x0e57
            java.lang.String r6 = "Seconds"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            goto L_0x0e6b
        L_0x0e57:
            r6 = 3600(0xe10, float:5.045E-42)
            if (r2 >= r6) goto L_0x0e63
            int r2 = r2 / r12
            java.lang.String r6 = "Minutes"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            goto L_0x0e6b
        L_0x0e63:
            int r2 = r2 / r12
            int r2 = r2 / r12
            java.lang.String r6 = "Hours"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
        L_0x0e6b:
            r6 = 2131625147(0x7f0e04bb, float:1.8877494E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r7 = 0
            r8[r7] = r2
            java.lang.String r2 = "EventLogToggledSlowmodeOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e97
        L_0x0e81:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "unsupported "
            r2.append(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            r0.messageText = r2
        L_0x0e97:
            r6 = 0
        L_0x0e98:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            if (r2 != 0) goto L_0x0ea3
            org.telegram.tgnet.TLRPC$TL_messageService r2 = new org.telegram.tgnet.TLRPC$TL_messageService
            r2.<init>()
            r0.messageOwner = r2
        L_0x0ea3:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.CharSequence r7 = r0.messageText
            java.lang.String r7 = r7.toString()
            r2.message = r7
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            int r7 = r1.user_id
            r2.from_id = r7
            int r7 = r1.date
            r2.date = r7
            r7 = 0
            r8 = r31[r7]
            int r9 = r8 + 1
            r31[r7] = r9
            r2.id = r8
            long r8 = r1.id
            r0.eventId = r8
            r2.out = r7
            org.telegram.tgnet.TLRPC$TL_peerChannel r8 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r8.<init>()
            r2.to_id = r8
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r8 = r2.to_id
            int r9 = r4.id
            r8.channel_id = r9
            r2.unread = r7
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0ee2
            int r7 = r2.flags
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            r7 = r7 | r8
            r2.flags = r7
        L_0x0ee2:
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$Message r7 = r7.message
            if (r7 == 0) goto L_0x0ef1
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r8 != 0) goto L_0x0ef1
            r6 = r7
        L_0x0ef1:
            if (r6 == 0) goto L_0x0var_
            r7 = 0
            r6.out = r7
            r8 = r31[r7]
            int r9 = r8 + 1
            r31[r7] = r9
            r6.id = r8
            r6.reply_to_msg_id = r7
            int r7 = r6.flags
            r8 = -32769(0xffffffffffff7fff, float:NaN)
            r7 = r7 & r8
            r6.flags = r7
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x0var_
            int r4 = r6.flags
            r7 = -2147483648(0xfffffffvar_, float:-0.0)
            r4 = r4 | r7
            r6.flags = r4
        L_0x0var_:
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject
            int r15 = r0.currentAccount
            r17 = 0
            r18 = 0
            r19 = 1
            long r7 = r0.eventId
            r14 = r4
            r16 = r6
            r20 = r7
            r14.<init>((int) r15, (org.telegram.tgnet.TLRPC.Message) r16, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.User>) r17, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC.Chat>) r18, (boolean) r19, (long) r20)
            int r6 = r4.contentType
            if (r6 < 0) goto L_0x0f4e
            boolean r6 = r2.isPlayingMessage(r4)
            if (r6 == 0) goto L_0x0f3d
            org.telegram.messenger.MessageObject r6 = r2.getPlayingMessageObject()
            float r7 = r6.audioProgress
            r4.audioProgress = r7
            int r6 = r6.audioProgressSec
            r4.audioProgressSec = r6
        L_0x0f3d:
            int r6 = r0.currentAccount
            r7 = r28
            r0.createDateArray(r6, r1, r7, r3)
            int r6 = r28.size()
            r8 = 1
            int r6 = r6 - r8
            r7.add(r6, r4)
            goto L_0x0var_
        L_0x0f4e:
            r7 = r28
            r4 = -1
            r0.contentType = r4
            goto L_0x0var_
        L_0x0var_:
            r7 = r28
        L_0x0var_:
            int r4 = r0.contentType
            if (r4 < 0) goto L_0x0fc4
            int r4 = r0.currentAccount
            r0.createDateArray(r4, r1, r7, r3)
            int r1 = r28.size()
            r3 = 1
            int r1 = r1 - r3
            r7.add(r1, r0)
            java.lang.CharSequence r1 = r0.messageText
            if (r1 != 0) goto L_0x0f6e
            r0.messageText = r13
        L_0x0f6e:
            r25.setType()
            r25.measureInlineBotButtons()
            r25.generateCaption()
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r1 == 0) goto L_0x0var_
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x0var_
        L_0x0var_:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x0var_:
            boolean r3 = r25.allowsBigEmoji()
            if (r3 == 0) goto L_0x0f8e
            r3 = 1
            int[] r6 = new int[r3]
            goto L_0x0f8f
        L_0x0f8e:
            r6 = 0
        L_0x0f8f:
            java.lang.CharSequence r3 = r0.messageText
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r4 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r7 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r3, r1, r4, r7, r6)
            r0.messageText = r1
            r0.checkEmojiOnly(r6)
            boolean r1 = r2.isPlayingMessage(r0)
            if (r1 == 0) goto L_0x0fb7
            org.telegram.messenger.MessageObject r1 = r2.getPlayingMessageObject()
            float r2 = r1.audioProgress
            r0.audioProgress = r2
            int r1 = r1.audioProgressSec
            r0.audioProgressSec = r1
        L_0x0fb7:
            r0.generateLayout(r5)
            r1 = 1
            r0.layoutCreated = r1
            r1 = 0
            r0.generateThumbs(r1)
            r25.checkMediaExistance()
        L_0x0fc4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[]):void");
    }

    private String getUserName(TLRPC.User user, ArrayList<TLRPC.MessageEntity> arrayList, int i) {
        String formatName = user == null ? "" : ContactsController.formatName(user.first_name, user.last_name);
        if (i >= 0) {
            TLRPC.TL_messageEntityMentionName tL_messageEntityMentionName = new TLRPC.TL_messageEntityMentionName();
            tL_messageEntityMentionName.user_id = user.id;
            tL_messageEntityMentionName.offset = i;
            tL_messageEntityMentionName.length = formatName.length();
            arrayList.add(tL_messageEntityMentionName);
        }
        if (TextUtils.isEmpty(user.username)) {
            return formatName;
        }
        if (i >= 0) {
            TLRPC.TL_messageEntityMentionName tL_messageEntityMentionName2 = new TLRPC.TL_messageEntityMentionName();
            tL_messageEntityMentionName2.user_id = user.id;
            tL_messageEntityMentionName2.offset = i + formatName.length() + 2;
            tL_messageEntityMentionName2.length = user.username.length() + 1;
            arrayList.add(tL_messageEntityMentionName2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{formatName, user.username});
    }

    public void applyNewText() {
        TextPaint textPaint;
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            int[] iArr = null;
            TLRPC.User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id)) : null;
            TLRPC.Message message = this.messageOwner;
            this.messageText = message.message;
            if (message.media instanceof TLRPC.TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            if (allowsBigEmoji()) {
                iArr = new int[1];
            }
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            generateLayout(user);
        }
    }

    private boolean allowsBigEmoji() {
        TLRPC.Peer peer;
        if (!SharedConfig.allowBigEmoji) {
            return false;
        }
        TLRPC.Message message = this.messageOwner;
        if (message == null || (peer = message.to_id) == null || (peer.channel_id == 0 && peer.chat_id == 0)) {
            return true;
        }
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        TLRPC.Peer peer2 = this.messageOwner.to_id;
        int i = peer2.channel_id;
        if (i == 0) {
            i = peer2.chat_id;
        }
        return !ChatObject.isActionBanned(instance.getChat(Integer.valueOf(i)), 8);
    }

    public void generateGameMessageText(TLRPC.User user) {
        TLRPC.MessageMedia messageMedia;
        TLRPC.TL_game tL_game;
        if (user == null && this.messageOwner.from_id > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        TLRPC.TL_game tL_game2 = null;
        MessageObject messageObject = this.replyMessageObject;
        if (!(messageObject == null || (messageMedia = messageObject.messageOwner.media) == null || (tL_game = messageMedia.game) == null)) {
            tL_game2 = tL_game;
        }
        if (tL_game2 != null) {
            if (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", user);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", tL_game2);
        } else if (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", user);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
        }
    }

    public boolean hasValidReplyMessageObject() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            return !(message instanceof TLRPC.TL_messageEmpty) && !(message.action instanceof TLRPC.TL_messageActionHistoryClear);
        }
    }

    public void generatePaymentSentMessageText(TLRPC.User user) {
        if (user == null) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) getDialogId()));
        }
        String firstName = user != null ? UserObject.getFirstName(user) : "";
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
            LocaleController instance = LocaleController.getInstance();
            TLRPC.MessageAction messageAction = this.messageOwner.action;
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", NUM, instance.formatCurrencyString(messageAction.total_amount, messageAction.currency), firstName);
            return;
        }
        LocaleController instance2 = LocaleController.getInstance();
        TLRPC.MessageAction messageAction2 = this.messageOwner.action;
        this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", NUM, instance2.formatCurrencyString(messageAction2.total_amount, messageAction2.currency), firstName, this.replyMessageObject.messageOwner.media.title);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v48, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v54, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Incorrect type for immutable var: ssa=org.telegram.tgnet.TLRPC$User, code=org.telegram.tgnet.TLRPC$Chat, for r9v0, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generatePinMessageText(org.telegram.tgnet.TLRPC.Chat r9, org.telegram.tgnet.TLRPC.Chat r10) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0032
            if (r10 != 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            int r0 = r0.from_id
            if (r0 <= 0) goto L_0x001c
            int r9 = r8.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            int r0 = r0.from_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r0)
        L_0x001c:
            if (r9 != 0) goto L_0x0032
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r0)
        L_0x0032:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            r1 = 2131624074(0x7f0e008a, float:1.8875317E38)
            java.lang.String r2 = "ActionPinnedNoText"
            java.lang.String r3 = "un1"
            if (r0 == 0) goto L_0x0269
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r5 != 0) goto L_0x0269
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r4 == 0) goto L_0x004c
            goto L_0x0269
        L_0x004c:
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0067
            r0 = 2131624073(0x7f0e0089, float:1.8875315E38)
            java.lang.String r1 = "ActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x005e
            goto L_0x005f
        L_0x005e:
            r9 = r10
        L_0x005f:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0067:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0084
            r0 = 2131624081(0x7f0e0091, float:1.8875332E38)
            java.lang.String r1 = "ActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x007b
            goto L_0x007c
        L_0x007b:
            r9 = r10
        L_0x007c:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0084:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x00a1
            r0 = 2131624072(0x7f0e0088, float:1.8875313E38)
            java.lang.String r1 = "ActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0098
            goto L_0x0099
        L_0x0098:
            r9 = r10
        L_0x0099:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x00a1:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00be
            r0 = 2131624082(0x7f0e0092, float:1.8875334E38)
            java.lang.String r1 = "ActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00b5
            goto L_0x00b6
        L_0x00b5:
            r9 = r10
        L_0x00b6:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x00be:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x00db
            r0 = 2131624078(0x7f0e008e, float:1.8875326E38)
            java.lang.String r1 = "ActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00d2
            goto L_0x00d3
        L_0x00d2:
            r9 = r10
        L_0x00d3:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x00db:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isSticker()
            if (r0 != 0) goto L_0x0255
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isAnimatedSticker()
            if (r0 == 0) goto L_0x00ed
            goto L_0x0255
        L_0x00ed:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x010c
            r0 = 2131624068(0x7f0e0084, float:1.8875305E38)
            java.lang.String r1 = "ActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0103
            goto L_0x0104
        L_0x0103:
            r9 = r10
        L_0x0104:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x010c:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r5 == 0) goto L_0x0125
            r0 = 2131624070(0x7f0e0086, float:1.887531E38)
            java.lang.String r1 = "ActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x011c
            goto L_0x011d
        L_0x011c:
            r9 = r10
        L_0x011d:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0125:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x013e
            r0 = 2131624071(0x7f0e0087, float:1.8875311E38)
            java.lang.String r1 = "ActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0135
            goto L_0x0136
        L_0x0135:
            r9 = r10
        L_0x0136:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x013e:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r5 == 0) goto L_0x0157
            r0 = 2131624067(0x7f0e0083, float:1.8875303E38)
            java.lang.String r1 = "ActionPinnedContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x014e
            goto L_0x014f
        L_0x014e:
            r9 = r10
        L_0x014f:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0157:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r5 == 0) goto L_0x018d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$TL_poll r0 = r4.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0178
            r0 = 2131624077(0x7f0e008d, float:1.8875324E38)
            java.lang.String r1 = "ActionPinnedQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x016f
            goto L_0x0170
        L_0x016f:
            r9 = r10
        L_0x0170:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0178:
            r0 = 2131624076(0x7f0e008c, float:1.8875321E38)
            java.lang.String r1 = "ActionPinnedPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0184
            goto L_0x0185
        L_0x0184:
            r9 = r10
        L_0x0185:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x018d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r5 == 0) goto L_0x01a6
            r0 = 2131624075(0x7f0e008b, float:1.887532E38)
            java.lang.String r1 = "ActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x019d
            goto L_0x019e
        L_0x019d:
            r9 = r10
        L_0x019e:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x01a6:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            r5 = 1101004800(0x41a00000, float:20.0)
            r6 = 1
            r7 = 0
            if (r4 == 0) goto L_0x01f5
            r0 = 2131624069(0x7f0e0085, float:1.8875307E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "🎮 "
            r2.append(r4)
            org.telegram.messenger.MessageObject r4 = r8.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r1[r7] = r2
            java.lang.String r2 = "ActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            if (r9 == 0) goto L_0x01da
            goto L_0x01db
        L_0x01da:
            r9 = r10
        L_0x01db:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            java.lang.CharSequence r9 = r8.messageText
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r0, r7)
            r8.messageText = r9
            goto L_0x0277
        L_0x01f5:
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x0246
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0246
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0220
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x0220:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r7)
            r1 = 2131624080(0x7f0e0090, float:1.887533E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r7] = r0
            java.lang.String r0 = "ActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            if (r9 == 0) goto L_0x023e
            goto L_0x023f
        L_0x023e:
            r9 = r10
        L_0x023f:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0246:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r9 == 0) goto L_0x024d
            goto L_0x024e
        L_0x024d:
            r9 = r10
        L_0x024e:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0255:
            r0 = 2131624079(0x7f0e008f, float:1.8875328E38)
            java.lang.String r1 = "ActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0261
            goto L_0x0262
        L_0x0261:
            r9 = r10
        L_0x0262:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0277
        L_0x0269:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r9 == 0) goto L_0x0270
            goto L_0x0271
        L_0x0270:
            r9 = r10
        L_0x0271:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
        L_0x0277:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generatePinMessageText(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat):void");
    }

    public static void updateReactions(TLRPC.Message message, TLRPC.TL_messageReactions tL_messageReactions) {
        TLRPC.TL_messageReactions tL_messageReactions2;
        if (message != null && tL_messageReactions != null) {
            if (tL_messageReactions.min && (tL_messageReactions2 = message.reactions) != null) {
                int size = tL_messageReactions2.results.size();
                int i = 0;
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC.TL_reactionCount tL_reactionCount = message.reactions.results.get(i2);
                    if (tL_reactionCount.chosen) {
                        int size2 = tL_messageReactions.results.size();
                        while (true) {
                            if (i >= size2) {
                                break;
                            }
                            TLRPC.TL_reactionCount tL_reactionCount2 = tL_messageReactions.results.get(i);
                            if (tL_reactionCount.reaction.equals(tL_reactionCount2.reaction)) {
                                tL_reactionCount2.chosen = true;
                                break;
                            }
                            i++;
                        }
                    } else {
                        i2++;
                    }
                }
            }
            message.reactions = tL_messageReactions;
            message.flags |= 1048576;
        }
    }

    public boolean hasReactions() {
        TLRPC.TL_messageReactions tL_messageReactions = this.messageOwner.reactions;
        return tL_messageReactions != null && !tL_messageReactions.results.isEmpty();
    }

    public static void updatePollResults(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, TLRPC.PollResults pollResults) {
        byte[] bArr;
        ArrayList arrayList;
        ArrayList<TLRPC.TL_pollAnswerVoters> arrayList2;
        if (tL_messageMediaPoll != null && pollResults != null) {
            if ((pollResults.flags & 2) != 0) {
                if (!pollResults.min || (arrayList2 = tL_messageMediaPoll.results.results) == null) {
                    arrayList = null;
                    bArr = null;
                } else {
                    int size = arrayList2.size();
                    arrayList = null;
                    bArr = null;
                    for (int i = 0; i < size; i++) {
                        TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters = tL_messageMediaPoll.results.results.get(i);
                        if (tL_pollAnswerVoters.chosen) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(tL_pollAnswerVoters.option);
                        }
                        if (tL_pollAnswerVoters.correct) {
                            bArr = tL_pollAnswerVoters.option;
                        }
                    }
                }
                tL_messageMediaPoll.results.results = pollResults.results;
                if (arrayList != null || bArr != null) {
                    int size2 = tL_messageMediaPoll.results.results.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters2 = tL_messageMediaPoll.results.results.get(i2);
                        if (arrayList != null) {
                            int size3 = arrayList.size();
                            int i3 = 0;
                            while (true) {
                                if (i3 >= size3) {
                                    break;
                                } else if (Arrays.equals(tL_pollAnswerVoters2.option, (byte[]) arrayList.get(i3))) {
                                    tL_pollAnswerVoters2.chosen = true;
                                    arrayList.remove(i3);
                                    break;
                                } else {
                                    i3++;
                                }
                            }
                            if (arrayList.isEmpty()) {
                                arrayList = null;
                            }
                        }
                        if (bArr != null && Arrays.equals(tL_pollAnswerVoters2.option, bArr)) {
                            tL_pollAnswerVoters2.correct = true;
                            bArr = null;
                        }
                        if (arrayList == null && bArr == null) {
                            break;
                        }
                    }
                }
                tL_messageMediaPoll.results.flags |= 2;
            }
            if ((pollResults.flags & 4) != 0) {
                TLRPC.PollResults pollResults2 = tL_messageMediaPoll.results;
                pollResults2.total_voters = pollResults.total_voters;
                pollResults2.flags |= 4;
            }
            if ((pollResults.flags & 8) != 0) {
                TLRPC.PollResults pollResults3 = tL_messageMediaPoll.results;
                pollResults3.recent_voters = pollResults.recent_voters;
                pollResults3.flags |= 8;
            }
        }
    }

    public boolean isPollClosed() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.closed;
    }

    public boolean isQuiz() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.quiz;
    }

    public boolean isPublicPoll() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.public_voters;
    }

    public boolean isPoll() {
        return this.type == 17;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5.messageOwner.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canUnvote() {
        /*
            r5 = this;
            int r0 = r5.type
            r1 = 0
            r2 = 17
            if (r0 == r2) goto L_0x0008
            return r1
        L_0x0008:
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            if (r2 == 0) goto L_0x003f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x003f
            org.telegram.tgnet.TLRPC$TL_poll r2 = r0.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x0021
            goto L_0x003f
        L_0x0021:
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x002a:
            if (r3 >= r2) goto L_0x003f
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC.TL_pollAnswerVoters) r4
            boolean r4 = r4.chosen
            if (r4 == 0) goto L_0x003c
            r0 = 1
            return r0
        L_0x003c:
            int r3 = r3 + 1
            goto L_0x002a
        L_0x003f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canUnvote():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5.messageOwner.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVoted() {
        /*
            r5 = this;
            int r0 = r5.type
            r1 = 0
            r2 = 17
            if (r0 == r2) goto L_0x0008
            return r1
        L_0x0008:
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            if (r2 == 0) goto L_0x0039
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x001b
            goto L_0x0039
        L_0x001b:
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x0024:
            if (r3 >= r2) goto L_0x0039
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC.TL_pollAnswerVoters) r4
            boolean r4 = r4.chosen
            if (r4 == 0) goto L_0x0036
            r0 = 1
            return r0
        L_0x0036:
            int r3 = r3 + 1
            goto L_0x0024
        L_0x0039:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isVoted():boolean");
    }

    public long getPollId() {
        if (this.type != 17) {
            return 0;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.id;
    }

    private TLRPC.Photo getPhotoWithId(TLRPC.WebPage webPage, long j) {
        if (!(webPage == null || webPage.cached_page == null)) {
            TLRPC.Photo photo = webPage.photo;
            if (photo != null && photo.id == j) {
                return photo;
            }
            for (int i = 0; i < webPage.cached_page.photos.size(); i++) {
                TLRPC.Photo photo2 = webPage.cached_page.photos.get(i);
                if (photo2.id == j) {
                    return photo2;
                }
            }
        }
        return null;
    }

    private TLRPC.Document getDocumentWithId(TLRPC.WebPage webPage, long j) {
        if (!(webPage == null || webPage.cached_page == null)) {
            TLRPC.Document document = webPage.document;
            if (document != null && document.id == j) {
                return document;
            }
            for (int i = 0; i < webPage.cached_page.documents.size(); i++) {
                TLRPC.Document document2 = webPage.cached_page.documents.get(i);
                if (document2.id == j) {
                    return document2;
                }
            }
        }
        return null;
    }

    private MessageObject getMessageObjectForBlock(TLRPC.WebPage webPage, TLRPC.PageBlock pageBlock) {
        TLRPC.TL_message tL_message;
        if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.Photo photoWithId = getPhotoWithId(webPage, ((TLRPC.TL_pageBlockPhoto) pageBlock).photo_id);
            if (photoWithId == webPage.photo) {
                return this;
            }
            tL_message = new TLRPC.TL_message();
            tL_message.media = new TLRPC.TL_messageMediaPhoto();
            tL_message.media.photo = photoWithId;
        } else if (pageBlock instanceof TLRPC.TL_pageBlockVideo) {
            TLRPC.TL_pageBlockVideo tL_pageBlockVideo = (TLRPC.TL_pageBlockVideo) pageBlock;
            if (getDocumentWithId(webPage, tL_pageBlockVideo.video_id) == webPage.document) {
                return this;
            }
            TLRPC.TL_message tL_message2 = new TLRPC.TL_message();
            tL_message2.media = new TLRPC.TL_messageMediaDocument();
            tL_message2.media.document = getDocumentWithId(webPage, tL_pageBlockVideo.video_id);
            tL_message = tL_message2;
        } else {
            tL_message = null;
        }
        tL_message.message = "";
        tL_message.realId = getId();
        tL_message.id = Utilities.random.nextInt();
        TLRPC.Message message = this.messageOwner;
        tL_message.date = message.date;
        tL_message.to_id = message.to_id;
        tL_message.out = message.out;
        tL_message.from_id = message.from_id;
        return new MessageObject(this.currentAccount, tL_message, false);
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> arrayList, ArrayList<TLRPC.PageBlock> arrayList2) {
        TLRPC.WebPage webPage;
        TLRPC.Page page;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia == null || (webPage = messageMedia.webpage) == null || (page = webPage.cached_page) == null) {
            return arrayList;
        }
        if (arrayList2 == null) {
            arrayList2 = page.blocks;
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC.PageBlock pageBlock = arrayList2.get(i);
            if (pageBlock instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow tL_pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) pageBlock;
                for (int i2 = 0; i2 < tL_pageBlockSlideshow.items.size(); i2++) {
                    arrayList.add(getMessageObjectForBlock(webPage, tL_pageBlockSlideshow.items.get(i2)));
                }
            } else if (pageBlock instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage tL_pageBlockCollage = (TLRPC.TL_pageBlockCollage) pageBlock;
                for (int i3 = 0; i3 < tL_pageBlockCollage.items.size(); i3++) {
                    arrayList.add(getMessageObjectForBlock(webPage, tL_pageBlockCollage.items.get(i3)));
                }
            }
        }
        return arrayList;
    }

    public void createMessageSendInfo() {
        HashMap<String, String> hashMap;
        String str;
        TLRPC.Message message = this.messageOwner;
        if (message.message == null) {
            return;
        }
        if ((message.id < 0 || isEditing()) && (hashMap = this.messageOwner.params) != null) {
            String str2 = hashMap.get("ve");
            if (str2 != null && (isVideo() || isNewGif() || isRoundVideo())) {
                this.videoEditedInfo = new VideoEditedInfo();
                if (!this.videoEditedInfo.parseString(str2)) {
                    this.videoEditedInfo = null;
                } else {
                    this.videoEditedInfo.roundVideo = isRoundVideo();
                }
            }
            TLRPC.Message message2 = this.messageOwner;
            if (message2.send_state == 3 && (str = message2.params.get("prevMedia")) != null) {
                SerializedData serializedData = new SerializedData(Base64.decode(str, 0));
                this.previousMedia = TLRPC.MessageMedia.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                this.previousCaption = serializedData.readString(false);
                this.previousAttachPath = serializedData.readString(false);
                int readInt32 = serializedData.readInt32(false);
                this.previousCaptionEntities = new ArrayList<>(readInt32);
                for (int i = 0; i < readInt32; i++) {
                    this.previousCaptionEntities.add(TLRPC.MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                }
                serializedData.cleanup();
            }
        }
    }

    public void measureInlineBotButtons() {
        int i;
        CharSequence charSequence;
        TLRPC.TL_messageReactions tL_messageReactions;
        this.wantedBotKeyboardWidth = 0;
        TLRPC.Message message = this.messageOwner;
        if ((message.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || ((tL_messageReactions = message.reactions) != null && !tL_messageReactions.results.isEmpty())) {
            Theme.createChatResources((Context) null, true);
            StringBuilder sb = this.botButtonsLayout;
            if (sb == null) {
                this.botButtonsLayout = new StringBuilder();
            } else {
                sb.setLength(0);
            }
        }
        TLRPC.Message message2 = this.messageOwner;
        if (message2.reply_markup instanceof TLRPC.TL_replyInlineMarkup) {
            for (int i2 = 0; i2 < this.messageOwner.reply_markup.rows.size(); i2++) {
                TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow = this.messageOwner.reply_markup.rows.get(i2);
                int size = tL_keyboardButtonRow.buttons.size();
                int i3 = 0;
                for (int i4 = 0; i4 < size; i4++) {
                    TLRPC.KeyboardButton keyboardButton = tL_keyboardButtonRow.buttons.get(i4);
                    StringBuilder sb2 = this.botButtonsLayout;
                    sb2.append(i2);
                    sb2.append(i4);
                    if (!(keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                        charSequence = Emoji.replaceEmoji(keyboardButton.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    } else {
                        charSequence = LocaleController.getString("PaymentReceipt", NUM);
                    }
                    StaticLayout staticLayout = new StaticLayout(charSequence, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        float lineWidth = staticLayout.getLineWidth(0);
                        float lineLeft = staticLayout.getLineLeft(0);
                        if (lineLeft < lineWidth) {
                            lineWidth -= lineLeft;
                        }
                        i3 = Math.max(i3, ((int) Math.ceil((double) lineWidth)) + AndroidUtilities.dp(4.0f));
                    }
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i3 + AndroidUtilities.dp(12.0f)) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
            }
            return;
        }
        TLRPC.TL_messageReactions tL_messageReactions2 = message2.reactions;
        if (tL_messageReactions2 != null) {
            int size2 = tL_messageReactions2.results.size();
            for (int i5 = 0; i5 < size2; i5++) {
                TLRPC.TL_reactionCount tL_reactionCount = this.messageOwner.reactions.results.get(i5);
                StringBuilder sb3 = this.botButtonsLayout;
                sb3.append(0);
                sb3.append(i5);
                StaticLayout staticLayout2 = new StaticLayout(Emoji.replaceEmoji(String.format("%d %s", new Object[]{Integer.valueOf(tL_reactionCount.count), tL_reactionCount.reaction}), Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (staticLayout2.getLineCount() > 0) {
                    float lineWidth2 = staticLayout2.getLineWidth(0);
                    float lineLeft2 = staticLayout2.getLineLeft(0);
                    if (lineLeft2 < lineWidth2) {
                        lineWidth2 -= lineLeft2;
                    }
                    i = Math.max(0, ((int) Math.ceil((double) lineWidth2)) + AndroidUtilities.dp(4.0f));
                } else {
                    i = 0;
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i + AndroidUtilities.dp(12.0f)) * size2) + (AndroidUtilities.dp(5.0f) * (size2 - 1)));
            }
        }
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    private void updateMessageText(AbstractMap<Integer, TLRPC.User> abstractMap, AbstractMap<Integer, TLRPC.Chat> abstractMap2, SparseArray<TLRPC.User> sparseArray, SparseArray<TLRPC.Chat> sparseArray2) {
        TLRPC.User user;
        TLRPC.User user2;
        TLRPC.User user3;
        TLRPC.Chat chat;
        String str;
        String str2;
        TLRPC.User user4;
        TLRPC.User user5;
        TLRPC.User user6;
        AbstractMap<Integer, TLRPC.User> abstractMap3 = abstractMap;
        AbstractMap<Integer, TLRPC.Chat> abstractMap4 = abstractMap2;
        SparseArray<TLRPC.User> sparseArray3 = sparseArray;
        SparseArray<TLRPC.Chat> sparseArray4 = sparseArray2;
        int i = this.messageOwner.from_id;
        if (i > 0) {
            if (abstractMap3 != null) {
                user6 = abstractMap3.get(Integer.valueOf(i));
            } else {
                user6 = sparseArray3 != null ? sparseArray3.get(i) : null;
            }
            if (user6 == null) {
                user6 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            user = user6;
        } else {
            user = null;
        }
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_messageService) {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction != null) {
                if (messageAction instanceof TLRPC.TL_messageActionCustomAction) {
                    this.messageText = messageAction.message;
                } else if (messageAction instanceof TLRPC.TL_messageActionChatCreate) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouCreateGroup", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", NUM), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatDeleteUser) {
                    int i2 = messageAction.user_id;
                    if (i2 != message.from_id) {
                        if (abstractMap3 != null) {
                            user5 = abstractMap3.get(Integer.valueOf(i2));
                        } else {
                            user5 = sparseArray3 != null ? sparseArray3.get(i2) : null;
                        }
                        if (user5 == null) {
                            user5 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.action.user_id));
                        }
                        if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", NUM), "un2", user5);
                        } else if (this.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", NUM), "un1", user);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", NUM), "un2", user5);
                            this.messageText = replaceWithLink(this.messageText, "un1", user);
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouLeftUser", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", NUM), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatAddUser) {
                    int i3 = messageAction.user_id;
                    if (i3 == 0 && messageAction.users.size() == 1) {
                        i3 = this.messageOwner.action.users.get(0).intValue();
                    }
                    if (i3 != 0) {
                        if (abstractMap3 != null) {
                            user4 = abstractMap3.get(Integer.valueOf(i3));
                        } else {
                            user4 = sparseArray3 != null ? sparseArray3.get(i3) : null;
                        }
                        if (user4 == null) {
                            user4 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i3));
                        }
                        TLRPC.Message message2 = this.messageOwner;
                        if (i3 == message2.from_id) {
                            if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                this.messageText = LocaleController.getString("ChannelJoined", NUM);
                            } else if (this.messageOwner.to_id.channel_id == 0 || !isMegagroup()) {
                                if (isOut()) {
                                    this.messageText = LocaleController.getString("ActionAddUserSelfYou", NUM);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", NUM), "un1", user);
                                }
                            } else if (i3 == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                this.messageText = LocaleController.getString("ChannelMegaJoined", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", NUM), "un1", user);
                            }
                        } else if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", user4);
                        } else if (i3 != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", user4);
                            this.messageText = replaceWithLink(this.messageText, "un1", user);
                        } else if (this.messageOwner.to_id.channel_id == 0) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", NUM), "un1", user);
                        } else if (isMegagroup()) {
                            this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", NUM), "un1", user);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", NUM), "un1", user);
                        }
                    } else if (isOut()) {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", this.messageOwner.action.users, abstractMap, sparseArray);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", this.messageOwner.action.users, abstractMap, sparseArray);
                        this.messageText = replaceWithLink(this.messageText, "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatJoinedByLink) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionInviteYou", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", NUM), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatEditPhoto) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedPhoto", NUM);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedPhoto", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", NUM), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatEditTitle) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedTitle", NUM).replace("un2", this.messageOwner.action.title);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedTitle", NUM).replace("un2", this.messageOwner.action.title);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", NUM).replace("un2", this.messageOwner.action.title), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatDeletePhoto) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", NUM);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouRemovedPhoto", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", NUM), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionTTLChange) {
                    if (messageAction.ttl != 0) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(this.messageOwner.action.ttl));
                        } else {
                            this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(user), LocaleController.formatTTLString(this.messageOwner.action.ttl));
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
                    } else {
                        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, UserObject.getFirstName(user));
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                    long j = ((long) message.date) * 1000;
                    if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                        str = "" + this.messageOwner.date;
                    } else {
                        str = LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(j), LocaleController.getInstance().formatterDay.format(j));
                    }
                    TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                    if (currentUser == null) {
                        if (abstractMap3 != null) {
                            currentUser = abstractMap3.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                        } else if (sparseArray3 != null) {
                            currentUser = sparseArray3.get(this.messageOwner.to_id.user_id);
                        }
                        if (currentUser == null) {
                            currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                    }
                    if (currentUser != null) {
                        str2 = UserObject.getFirstName(currentUser);
                    } else {
                        str2 = "";
                    }
                    TLRPC.MessageAction messageAction2 = this.messageOwner.action;
                    this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, str2, str, messageAction2.title, messageAction2.address);
                } else if ((messageAction instanceof TLRPC.TL_messageActionUserJoined) || (messageAction instanceof TLRPC.TL_messageActionContactSignUp)) {
                    this.messageText = LocaleController.formatString("NotificationContactJoined", NUM, UserObject.getUserName(user));
                } else if (messageAction instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    this.messageText = LocaleController.formatString("NotificationContactNewPhoto", NUM, UserObject.getUserName(user));
                } else if (messageAction instanceof TLRPC.TL_messageEncryptedAction) {
                    TLRPC.DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
                    if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), "un1", user);
                        }
                    } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                        TLRPC.TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TLRPC.TL_decryptedMessageActionSetMessageTTL) decryptedMessageAction;
                        if (tL_decryptedMessageActionSetMessageTTL.ttl_seconds != 0) {
                            if (isOut()) {
                                this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(tL_decryptedMessageActionSetMessageTTL.ttl_seconds));
                            } else {
                                this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(user), LocaleController.formatTTLString(tL_decryptedMessageActionSetMessageTTL.ttl_seconds));
                            }
                        } else if (isOut()) {
                            this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
                        } else {
                            this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, UserObject.getFirstName(user));
                        }
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionScreenshotTaken) {
                    if (isOut()) {
                        this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), "un1", user);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionCreatedBroadcastList) {
                    this.messageText = LocaleController.formatString("YouCreatedBroadcastList", NUM, new Object[0]);
                } else if (messageAction instanceof TLRPC.TL_messageActionChannelCreate) {
                    if (isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionCreateMega", NUM);
                    } else {
                        this.messageText = LocaleController.getString("ActionCreateChannel", NUM);
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionChatMigrateTo) {
                    this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                } else if (messageAction instanceof TLRPC.TL_messageActionChannelMigrateFrom) {
                    this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                } else if (messageAction instanceof TLRPC.TL_messageActionPinMessage) {
                    if (user == null) {
                        if (abstractMap4 != null) {
                            chat = abstractMap4.get(Integer.valueOf(message.to_id.channel_id));
                        } else if (sparseArray4 != null) {
                            chat = sparseArray4.get(message.to_id.channel_id);
                        }
                        generatePinMessageText(user, chat);
                    }
                    chat = null;
                    generatePinMessageText(user, chat);
                } else if (messageAction instanceof TLRPC.TL_messageActionHistoryClear) {
                    this.messageText = LocaleController.getString("HistoryCleared", NUM);
                } else if (messageAction instanceof TLRPC.TL_messageActionGameScore) {
                    generateGameMessageText(user);
                } else if (messageAction instanceof TLRPC.TL_messageActionPhoneCall) {
                    TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall = (TLRPC.TL_messageActionPhoneCall) messageAction;
                    boolean z = tL_messageActionPhoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
                    if (message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        if (z) {
                            this.messageText = LocaleController.getString("CallMessageOutgoingMissed", NUM);
                        } else {
                            this.messageText = LocaleController.getString("CallMessageOutgoing", NUM);
                        }
                    } else if (z) {
                        this.messageText = LocaleController.getString("CallMessageIncomingMissed", NUM);
                    } else if (tL_messageActionPhoneCall.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                        this.messageText = LocaleController.getString("CallMessageIncomingDeclined", NUM);
                    } else {
                        this.messageText = LocaleController.getString("CallMessageIncoming", NUM);
                    }
                    int i4 = tL_messageActionPhoneCall.duration;
                    if (i4 > 0) {
                        String formatCallDuration = LocaleController.formatCallDuration(i4);
                        this.messageText = LocaleController.formatString("CallMessageWithDuration", NUM, this.messageText, formatCallDuration);
                        String charSequence = this.messageText.toString();
                        int indexOf = charSequence.indexOf(formatCallDuration);
                        if (indexOf != -1) {
                            SpannableString spannableString = new SpannableString(this.messageText);
                            int length = formatCallDuration.length() + indexOf;
                            if (indexOf > 0 && charSequence.charAt(indexOf - 1) == '(') {
                                indexOf--;
                            }
                            if (length < charSequence.length() && charSequence.charAt(length) == ')') {
                                length++;
                            }
                            spannableString.setSpan(new TypefaceSpan(Typeface.DEFAULT), indexOf, length, 0);
                            this.messageText = spannableString;
                        }
                    }
                } else if (messageAction instanceof TLRPC.TL_messageActionPaymentSent) {
                    int dialogId = (int) getDialogId();
                    if (abstractMap3 != null) {
                        user3 = abstractMap3.get(Integer.valueOf(dialogId));
                    } else {
                        user3 = sparseArray3 != null ? sparseArray3.get(dialogId) : null;
                    }
                    if (user3 == null) {
                        user3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(dialogId));
                    }
                    generatePaymentSentMessageText(user3);
                } else if (messageAction instanceof TLRPC.TL_messageActionBotAllowed) {
                    String str3 = ((TLRPC.TL_messageActionBotAllowed) messageAction).domain;
                    String string = LocaleController.getString("ActionBotAllowed", NUM);
                    int indexOf2 = string.indexOf("%1$s");
                    SpannableString spannableString2 = new SpannableString(String.format(string, new Object[]{str3}));
                    if (indexOf2 >= 0) {
                        spannableString2.setSpan(new URLSpanNoUnderlineBold("http://" + str3), indexOf2, str3.length() + indexOf2, 33);
                    }
                    this.messageText = spannableString2;
                } else if (messageAction instanceof TLRPC.TL_messageActionSecureValuesSent) {
                    TLRPC.TL_messageActionSecureValuesSent tL_messageActionSecureValuesSent = (TLRPC.TL_messageActionSecureValuesSent) messageAction;
                    StringBuilder sb = new StringBuilder();
                    int size = tL_messageActionSecureValuesSent.types.size();
                    for (int i5 = 0; i5 < size; i5++) {
                        TLRPC.SecureValueType secureValueType = tL_messageActionSecureValuesSent.types.get(i5);
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        if (secureValueType instanceof TLRPC.TL_secureValueTypePhone) {
                            sb.append(LocaleController.getString("ActionBotDocumentPhone", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeEmail) {
                            sb.append(LocaleController.getString("ActionBotDocumentEmail", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeAddress) {
                            sb.append(LocaleController.getString("ActionBotDocumentAddress", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                            sb.append(LocaleController.getString("ActionBotDocumentIdentity", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypePassport) {
                            sb.append(LocaleController.getString("ActionBotDocumentPassport", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                            sb.append(LocaleController.getString("ActionBotDocumentDriverLicence", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                            sb.append(LocaleController.getString("ActionBotDocumentIdentityCard", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                            sb.append(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeBankStatement) {
                            sb.append(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                            sb.append(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                            sb.append(LocaleController.getString("ActionBotDocumentInternalPassport", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                            sb.append(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
                        } else if (secureValueType instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                            sb.append(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
                        }
                    }
                    TLRPC.Peer peer = this.messageOwner.to_id;
                    if (peer != null) {
                        if (abstractMap3 != null) {
                            user2 = abstractMap3.get(Integer.valueOf(peer.user_id));
                        } else {
                            user2 = sparseArray3 != null ? sparseArray3.get(peer.user_id) : null;
                        }
                        if (user2 == null) {
                            user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                    } else {
                        user2 = null;
                    }
                    this.messageText = LocaleController.formatString("ActionBotDocuments", NUM, UserObject.getFirstName(user2), sb.toString());
                }
            }
        } else {
            this.isRestrictedMessage = false;
            String restrictionReason = MessagesController.getRestrictionReason(message.restriction_reason);
            if (!TextUtils.isEmpty(restrictionReason)) {
                this.messageText = restrictionReason;
                this.isRestrictedMessage = true;
            } else if (!isMediaEmpty()) {
                TLRPC.Message message3 = this.messageOwner;
                TLRPC.MessageMedia messageMedia = message3.media;
                if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
                    if (((TLRPC.TL_messageMediaPoll) messageMedia).poll.quiz) {
                        this.messageText = LocaleController.getString("QuizPoll", NUM);
                    } else {
                        this.messageText = LocaleController.getString("Poll", NUM);
                    }
                } else if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                    if (messageMedia.ttl_seconds == 0 || (message3 instanceof TLRPC.TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachPhoto", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingPhoto", NUM);
                    }
                } else if (isVideo() || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) && (getDocument() instanceof TLRPC.TL_documentEmpty) && this.messageOwner.media.ttl_seconds != 0)) {
                    TLRPC.Message message4 = this.messageOwner;
                    if (message4.media.ttl_seconds == 0 || (message4 instanceof TLRPC.TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachVideo", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingVideo", NUM);
                    }
                } else if (isVoice()) {
                    this.messageText = LocaleController.getString("AttachAudio", NUM);
                } else if (isRoundVideo()) {
                    this.messageText = LocaleController.getString("AttachRound", NUM);
                } else {
                    TLRPC.Message message5 = this.messageOwner;
                    TLRPC.MessageMedia messageMedia2 = message5.media;
                    if ((messageMedia2 instanceof TLRPC.TL_messageMediaGeo) || (messageMedia2 instanceof TLRPC.TL_messageMediaVenue)) {
                        this.messageText = LocaleController.getString("AttachLocation", NUM);
                    } else if (messageMedia2 instanceof TLRPC.TL_messageMediaGeoLive) {
                        this.messageText = LocaleController.getString("AttachLiveLocation", NUM);
                    } else if (messageMedia2 instanceof TLRPC.TL_messageMediaContact) {
                        this.messageText = LocaleController.getString("AttachContact", NUM);
                        if (!TextUtils.isEmpty(this.messageOwner.media.vcard)) {
                            this.vCardData = VCardData.parse(this.messageOwner.media.vcard);
                        }
                    } else if (messageMedia2 instanceof TLRPC.TL_messageMediaGame) {
                        this.messageText = message5.message;
                    } else if (messageMedia2 instanceof TLRPC.TL_messageMediaInvoice) {
                        this.messageText = messageMedia2.description;
                    } else if (messageMedia2 instanceof TLRPC.TL_messageMediaUnsupported) {
                        this.messageText = LocaleController.getString("UnsupportedMedia", NUM);
                    } else if (messageMedia2 instanceof TLRPC.TL_messageMediaDocument) {
                        if (isSticker() || isAnimatedSticker()) {
                            String strickerChar = getStrickerChar();
                            if (strickerChar == null || strickerChar.length() <= 0) {
                                this.messageText = LocaleController.getString("AttachSticker", NUM);
                            } else {
                                this.messageText = String.format("%s %s", new Object[]{strickerChar, LocaleController.getString("AttachSticker", NUM)});
                            }
                        } else if (isMusic()) {
                            this.messageText = LocaleController.getString("AttachMusic", NUM);
                        } else if (isGif()) {
                            this.messageText = LocaleController.getString("AttachGif", NUM);
                        } else {
                            String documentFileName = FileLoader.getDocumentFileName(getDocument());
                            if (documentFileName == null || documentFileName.length() <= 0) {
                                this.messageText = LocaleController.getString("AttachDocument", NUM);
                            } else {
                                this.messageText = documentFileName;
                            }
                        }
                    }
                }
            } else {
                this.messageText = this.messageOwner.message;
            }
        }
        if (this.messageText == null) {
            this.messageText = "";
        }
    }

    public void setType() {
        int i = this.type;
        this.isRoundVideoCached = 0;
        TLRPC.Message message = this.messageOwner;
        if ((message instanceof TLRPC.TL_message) || (message instanceof TLRPC.TL_messageForwarded_old2)) {
            if (this.isRestrictedMessage) {
                this.type = 0;
            } else if (this.emojiAnimatedSticker != null) {
                if (isSticker()) {
                    this.type = 13;
                } else {
                    this.type = 15;
                }
            } else if (isMediaEmpty()) {
                this.type = 0;
                if (TextUtils.isEmpty(this.messageText) && this.eventId == 0) {
                    this.messageText = "Empty message";
                }
            } else {
                TLRPC.MessageMedia messageMedia = this.messageOwner.media;
                if (messageMedia.ttl_seconds == 0 || (!(messageMedia.photo instanceof TLRPC.TL_photoEmpty) && !(getDocument() instanceof TLRPC.TL_documentEmpty))) {
                    TLRPC.MessageMedia messageMedia2 = this.messageOwner.media;
                    if (messageMedia2 instanceof TLRPC.TL_messageMediaPhoto) {
                        this.type = 1;
                    } else if ((messageMedia2 instanceof TLRPC.TL_messageMediaGeo) || (messageMedia2 instanceof TLRPC.TL_messageMediaVenue) || (messageMedia2 instanceof TLRPC.TL_messageMediaGeoLive)) {
                        this.type = 4;
                    } else if (isRoundVideo()) {
                        this.type = 5;
                    } else if (isVideo()) {
                        this.type = 3;
                    } else if (isVoice()) {
                        this.type = 2;
                    } else if (isMusic()) {
                        this.type = 14;
                    } else {
                        TLRPC.MessageMedia messageMedia3 = this.messageOwner.media;
                        if (messageMedia3 instanceof TLRPC.TL_messageMediaContact) {
                            this.type = 12;
                        } else if (messageMedia3 instanceof TLRPC.TL_messageMediaPoll) {
                            this.type = 17;
                            this.checkedVotes = new ArrayList<>();
                        } else if (messageMedia3 instanceof TLRPC.TL_messageMediaUnsupported) {
                            this.type = 0;
                        } else if (messageMedia3 instanceof TLRPC.TL_messageMediaDocument) {
                            TLRPC.Document document = getDocument();
                            if (document == null || document.mime_type == null) {
                                this.type = 9;
                            } else if (isGifDocument(document)) {
                                this.type = 8;
                            } else if (isSticker()) {
                                this.type = 13;
                            } else if (isAnimatedSticker()) {
                                this.type = 15;
                            } else {
                                this.type = 9;
                            }
                        } else if (messageMedia3 instanceof TLRPC.TL_messageMediaGame) {
                            this.type = 0;
                        } else if (messageMedia3 instanceof TLRPC.TL_messageMediaInvoice) {
                            this.type = 0;
                        }
                    }
                } else {
                    this.contentType = 1;
                    this.type = 10;
                }
            }
        } else if (message instanceof TLRPC.TL_messageService) {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((messageAction instanceof TLRPC.TL_messageActionChatEditPhoto) || (messageAction instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
                this.contentType = 1;
                this.type = 11;
            } else if (messageAction instanceof TLRPC.TL_messageEncryptedAction) {
                TLRPC.DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
                if ((decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = 1;
                    this.type = 10;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (messageAction instanceof TLRPC.TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else if (messageAction instanceof TLRPC.TL_messageActionPhoneCall) {
                this.type = 16;
            } else {
                this.contentType = 1;
                this.type = 10;
            }
        }
        if (i != 1000 && i != this.type) {
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), (SparseArray<TLRPC.User>) null, (SparseArray<TLRPC.Chat>) null);
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        TextPaint textPaint;
        if (!(this.type != 0 || this.messageOwner.to_id == null || (charSequence = this.messageText) == null || charSequence.length() == 0)) {
            if (this.layoutCreated) {
                if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                    this.layoutCreated = false;
                }
            }
            if (!this.layoutCreated) {
                this.layoutCreated = true;
                int[] iArr = null;
                TLRPC.User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id)) : null;
                if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                    textPaint = Theme.chat_msgGameTextPaint;
                } else {
                    textPaint = Theme.chat_msgTextPaint;
                }
                if (allowsBigEmoji()) {
                    iArr = new int[1];
                }
                this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
                checkEmojiOnly(iArr);
                generateLayout(user);
                return true;
            }
        }
        return false;
    }

    public void resetLayout() {
        this.layoutCreated = false;
    }

    public String getMimeType() {
        TLRPC.Document document = getDocument();
        if (document != null) {
            return document.mime_type;
        }
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaInvoice) {
            TLRPC.WebDocument webDocument = ((TLRPC.TL_messageMediaInvoice) messageMedia).photo;
            if (webDocument != null) {
                return webDocument.mime_type;
            }
            return "";
        } else if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            return "image/jpeg";
        } else {
            if (!(messageMedia instanceof TLRPC.TL_messageMediaWebPage) || messageMedia.webpage.photo == null) {
                return "";
            }
            return "image/jpeg";
        }
    }

    public boolean canPreviewDocument() {
        return canPreviewDocument(getDocument());
    }

    public static boolean isGifDocument(WebFile webFile) {
        return webFile != null && (webFile.mime_type.equals("image/gif") || isNewGifDocument(webFile));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r2.mime_type;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isGifDocument(org.telegram.tgnet.TLRPC.Document r2) {
        /*
            if (r2 == 0) goto L_0x0016
            java.lang.String r0 = r2.mime_type
            if (r0 == 0) goto L_0x0016
            java.lang.String r1 = "image/gif"
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0014
            boolean r2 = isNewGifDocument((org.telegram.tgnet.TLRPC.Document) r2)
            if (r2 == 0) goto L_0x0016
        L_0x0014:
            r2 = 1
            goto L_0x0017
        L_0x0016:
            r2 = 0
        L_0x0017:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isGifDocument(org.telegram.tgnet.TLRPC$Document):boolean");
    }

    public static boolean isDocumentHasThumb(TLRPC.Document document) {
        if (document != null && !document.thumbs.isEmpty()) {
            int size = document.thumbs.size();
            for (int i = 0; i < size; i++) {
                TLRPC.PhotoSize photoSize = document.thumbs.get(i);
                if (photoSize != null && !(photoSize instanceof TLRPC.TL_photoSizeEmpty) && !(photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canPreviewDocument(TLRPC.Document document) {
        String str;
        if (!(document == null || (str = document.mime_type) == null)) {
            String lowerCase = str.toLowerCase();
            if (isDocumentHasThumb(document) && (lowerCase.equals("image/png") || lowerCase.equals("image/jpg") || lowerCase.equals("image/jpeg"))) {
                for (int i = 0; i < document.attributes.size(); i++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        TLRPC.TL_documentAttributeImageSize tL_documentAttributeImageSize = (TLRPC.TL_documentAttributeImageSize) documentAttribute;
                        if (tL_documentAttributeImageSize.w >= 6000 || tL_documentAttributeImageSize.h >= 6000) {
                            return false;
                        }
                        return true;
                    }
                }
            } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                String documentFileName = FileLoader.getDocumentFileName(document);
                if ((!documentFileName.startsWith("tg_secret_sticker") || !documentFileName.endsWith("json")) && !documentFileName.endsWith(".svg")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoDocument(TLRPC.Document document) {
        if (document != null && "video/mp4".equals(document.mime_type)) {
            boolean z = false;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    i2 = documentAttribute.w;
                    z = documentAttribute.round_message;
                    i = i2;
                }
            }
            if (!z || i > 1280 || i2 > 1280) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isNewGifDocument(WebFile webFile) {
        if (webFile != null && "video/mp4".equals(webFile.mime_type)) {
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < webFile.attributes.size(); i3++) {
                TLRPC.DocumentAttribute documentAttribute = webFile.attributes.get(i3);
                if (!(documentAttribute instanceof TLRPC.TL_documentAttributeAnimated) && (documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                    i = documentAttribute.w;
                    i2 = documentAttribute.h;
                }
            }
            if (i > 1280 || i2 > 1280) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isNewGifDocument(TLRPC.Document document) {
        if (document != null && "video/mp4".equals(document.mime_type)) {
            boolean z = false;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAnimated) {
                    z = true;
                } else if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    i = documentAttribute.w;
                    i2 = documentAttribute.h;
                }
            }
            if (!z || i > 1280 || i2 > 1280) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void generateThumbs(boolean z) {
        ArrayList<TLRPC.PhotoSize> arrayList;
        ArrayList<TLRPC.PhotoSize> arrayList2;
        ArrayList<TLRPC.PhotoSize> arrayList3;
        ArrayList<TLRPC.PhotoSize> arrayList4;
        ArrayList<TLRPC.PhotoSize> arrayList5;
        ArrayList<TLRPC.PhotoSize> arrayList6;
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_messageService) {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionChatEditPhoto) {
                TLRPC.Photo photo = messageAction.photo;
                if (!z) {
                    this.photoThumbs = new ArrayList<>(photo.sizes);
                } else {
                    ArrayList<TLRPC.PhotoSize> arrayList7 = this.photoThumbs;
                    if (arrayList7 != null && !arrayList7.isEmpty()) {
                        for (int i = 0; i < this.photoThumbs.size(); i++) {
                            TLRPC.PhotoSize photoSize = this.photoThumbs.get(i);
                            int i2 = 0;
                            while (true) {
                                if (i2 >= photo.sizes.size()) {
                                    break;
                                }
                                TLRPC.PhotoSize photoSize2 = photo.sizes.get(i2);
                                if (!(photoSize2 instanceof TLRPC.TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                                    photoSize.location = photoSize2.location;
                                    break;
                                }
                                i2++;
                            }
                        }
                    }
                }
                if (photo.dc_id != 0) {
                    int size = this.photoThumbs.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        TLRPC.FileLocation fileLocation = this.photoThumbs.get(i3).location;
                        fileLocation.dc_id = photo.dc_id;
                        fileLocation.file_reference = photo.file_reference;
                    }
                }
                this.photoThumbsObject = this.messageOwner.action.photo;
            }
        } else if (this.emojiAnimatedSticker == null) {
            TLRPC.MessageMedia messageMedia = message.media;
            if (messageMedia != null && !(messageMedia instanceof TLRPC.TL_messageMediaEmpty)) {
                if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                    TLRPC.Photo photo2 = messageMedia.photo;
                    if (!z || !((arrayList5 = this.photoThumbs) == null || arrayList5.size() == photo2.sizes.size())) {
                        this.photoThumbs = new ArrayList<>(photo2.sizes);
                    } else {
                        ArrayList<TLRPC.PhotoSize> arrayList8 = this.photoThumbs;
                        if (arrayList8 != null && !arrayList8.isEmpty()) {
                            for (int i4 = 0; i4 < this.photoThumbs.size(); i4++) {
                                TLRPC.PhotoSize photoSize3 = this.photoThumbs.get(i4);
                                if (photoSize3 != null) {
                                    int i5 = 0;
                                    while (true) {
                                        if (i5 < photo2.sizes.size()) {
                                            TLRPC.PhotoSize photoSize4 = photo2.sizes.get(i5);
                                            if (photoSize4 != null && !(photoSize4 instanceof TLRPC.TL_photoSizeEmpty) && photoSize4.type.equals(photoSize3.type)) {
                                                photoSize3.location = photoSize4.location;
                                                break;
                                            }
                                            i5++;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.photoThumbsObject = this.messageOwner.media.photo;
                } else if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
                    TLRPC.Document document = getDocument();
                    if (isDocumentHasThumb(document)) {
                        if (!z || (arrayList4 = this.photoThumbs) == null) {
                            this.photoThumbs = new ArrayList<>();
                            this.photoThumbs.addAll(document.thumbs);
                        } else if (arrayList4 != null && !arrayList4.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                        }
                        this.photoThumbsObject = document;
                    }
                } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
                    TLRPC.Document document2 = messageMedia.game.document;
                    if (document2 != null && isDocumentHasThumb(document2)) {
                        if (!z) {
                            this.photoThumbs = new ArrayList<>();
                            this.photoThumbs.addAll(document2.thumbs);
                        } else {
                            ArrayList<TLRPC.PhotoSize> arrayList9 = this.photoThumbs;
                            if (arrayList9 != null && !arrayList9.isEmpty()) {
                                updatePhotoSizeLocations(this.photoThumbs, document2.thumbs);
                            }
                        }
                        this.photoThumbsObject = document2;
                    }
                    TLRPC.Photo photo3 = this.messageOwner.media.game.photo;
                    if (photo3 != null) {
                        if (!z || (arrayList3 = this.photoThumbs2) == null) {
                            this.photoThumbs2 = new ArrayList<>(photo3.sizes);
                        } else if (!arrayList3.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs2, photo3.sizes);
                        }
                        this.photoThumbsObject2 = photo3;
                    }
                    if (this.photoThumbs == null && (arrayList2 = this.photoThumbs2) != null) {
                        this.photoThumbs = arrayList2;
                        this.photoThumbs2 = null;
                        this.photoThumbsObject = this.photoThumbsObject2;
                        this.photoThumbsObject2 = null;
                    }
                } else if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
                    TLRPC.WebPage webPage = messageMedia.webpage;
                    TLRPC.Photo photo4 = webPage.photo;
                    TLRPC.Document document3 = webPage.document;
                    if (photo4 != null) {
                        if (!z || (arrayList = this.photoThumbs) == null) {
                            this.photoThumbs = new ArrayList<>(photo4.sizes);
                        } else if (!arrayList.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, photo4.sizes);
                        }
                        this.photoThumbsObject = photo4;
                    } else if (document3 != null && isDocumentHasThumb(document3)) {
                        if (!z) {
                            this.photoThumbs = new ArrayList<>();
                            this.photoThumbs.addAll(document3.thumbs);
                        } else {
                            ArrayList<TLRPC.PhotoSize> arrayList10 = this.photoThumbs;
                            if (arrayList10 != null && !arrayList10.isEmpty()) {
                                updatePhotoSizeLocations(this.photoThumbs, document3.thumbs);
                            }
                        }
                        this.photoThumbsObject = document3;
                    }
                }
            }
        } else if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) && isDocumentHasThumb(this.emojiAnimatedSticker)) {
            if (!z || (arrayList6 = this.photoThumbs) == null) {
                this.photoThumbs = new ArrayList<>();
                this.photoThumbs.addAll(this.emojiAnimatedSticker.thumbs);
            } else if (arrayList6 != null && !arrayList6.isEmpty()) {
                updatePhotoSizeLocations(this.photoThumbs, this.emojiAnimatedSticker.thumbs);
            }
            this.photoThumbsObject = this.emojiAnimatedSticker;
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC.PhotoSize> arrayList, ArrayList<TLRPC.PhotoSize> arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.PhotoSize photoSize = arrayList.get(i);
            if (photoSize != null) {
                int size2 = arrayList2.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size2) {
                        break;
                    }
                    TLRPC.PhotoSize photoSize2 = arrayList2.get(i2);
                    if (!(photoSize2 instanceof TLRPC.TL_photoSizeEmpty) && !(photoSize2 instanceof TLRPC.TL_photoCachedSize) && photoSize2 != null && photoSize2.type.equals(photoSize.type)) {
                        photoSize.location = photoSize2.location;
                        break;
                    }
                    i2++;
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, ArrayList<Integer> arrayList, AbstractMap<Integer, TLRPC.User> abstractMap, SparseArray<TLRPC.User> sparseArray) {
        if (TextUtils.indexOf(charSequence, str) < 0) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.User user = null;
            if (abstractMap != null) {
                user = abstractMap.get(arrayList.get(i));
            } else if (sparseArray != null) {
                user = sparseArray.get(arrayList.get(i).intValue());
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(arrayList.get(i));
            }
            if (user != null) {
                String userName = UserObject.getUserName(user);
                int length = spannableStringBuilder.length();
                if (spannableStringBuilder.length() != 0) {
                    spannableStringBuilder.append(", ");
                }
                spannableStringBuilder.append(userName);
                spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + user.id), length, userName.length() + length, 33);
            }
        }
        return TextUtils.replace(charSequence, new String[]{str}, new CharSequence[]{spannableStringBuilder});
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, TLObject tLObject) {
        String str2;
        String str3;
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf < 0) {
            return charSequence;
        }
        if (tLObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) tLObject;
            str3 = UserObject.getUserName(user);
            str2 = "" + user.id;
        } else if (tLObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            str3 = chat.title;
            str2 = "" + (-chat.id);
        } else if (tLObject instanceof TLRPC.TL_game) {
            str3 = ((TLRPC.TL_game) tLObject).title;
            str2 = "game";
        } else {
            str2 = "0";
            str3 = "";
        }
        String replace = str3.replace(10, ' ');
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new String[]{replace}));
        spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + str2), indexOf, replace.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int lastIndexOf = fileName.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? fileName.substring(lastIndexOf + 1) : null;
        if (substring == null || substring.length() == 0) {
            substring = getDocument().mime_type;
        }
        if (substring == null) {
            substring = "";
        }
        return substring.toUpperCase();
    }

    public String getFileName() {
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument());
        }
        if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto)) {
            return messageMedia instanceof TLRPC.TL_messageMediaWebPage ? FileLoader.getAttachFileName(messageMedia.webpage.document) : "";
        }
        ArrayList<TLRPC.PhotoSize> arrayList = messageMedia.photo.sizes;
        if (arrayList.size() <= 0 || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) == null) {
            return "";
        }
        return FileLoader.getAttachFileName(closestPhotoSizeWithSize);
    }

    public int getMediaType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            return 3;
        }
        return messageMedia instanceof TLRPC.TL_messageMediaPhoto ? 0 : 4;
    }

    private static boolean containsUrls(CharSequence charSequence) {
        if (charSequence != null && charSequence.length() >= 2 && charSequence.length() <= 20480) {
            int length = charSequence.length();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            char c = 0;
            while (i < length) {
                char charAt = charSequence.charAt(i);
                if (charAt >= '0' && charAt <= '9') {
                    i2++;
                    if (i2 >= 6) {
                        return true;
                    }
                    i3 = 0;
                    i4 = 0;
                } else if (charAt == ' ' || i2 <= 0) {
                    i2 = 0;
                }
                if ((charAt != '@' && charAt != '#' && charAt != '/' && charAt != '$') || i != 0) {
                    if (i != 0) {
                        int i5 = i - 1;
                        if (charSequence.charAt(i5) != ' ') {
                            if (charSequence.charAt(i5) == 10) {
                            }
                        }
                    }
                    if (charAt != ':') {
                        if (charAt != '/') {
                            if (charAt == '.') {
                                if (i4 == 0 && c != ' ') {
                                    i4++;
                                }
                            } else if (charAt != ' ' && c == '.' && i4 == 1) {
                                return true;
                            }
                            i4 = 0;
                        } else if (i3 == 2) {
                            return true;
                        } else {
                            if (i3 == 1) {
                                i3++;
                            }
                        }
                        i++;
                        c = charAt;
                    } else if (i3 == 0) {
                        i3 = 1;
                        i++;
                        c = charAt;
                    }
                    i3 = 0;
                    i++;
                    c = charAt;
                }
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLinkDescription() {
        /*
            r10 = this;
            java.lang.CharSequence r0 = r10.linkDescription
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0051
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage
            if (r1 == 0) goto L_0x0051
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0051
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.lang.String r0 = r0.site_name
            if (r0 == 0) goto L_0x0039
            java.lang.String r0 = r0.toLowerCase()
        L_0x0039:
            java.lang.String r1 = "instagram"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0043
            r0 = 1
            goto L_0x004f
        L_0x0043:
            java.lang.String r1 = "twitter"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x004e
            r0 = 2
            goto L_0x004f
        L_0x004e:
            r0 = 0
        L_0x004f:
            r7 = r0
            goto L_0x008f
        L_0x0051:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r1 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0072
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r1 = r1.game
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
            goto L_0x008e
        L_0x0072:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r1 == 0) goto L_0x008e
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x008e
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
        L_0x008e:
            r7 = 0
        L_0x008f:
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00da
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r0 = containsUrls(r0)
            if (r0 == 0) goto L_0x00ab
            java.lang.CharSequence r0 = r10.linkDescription     // Catch:{ Exception -> 0x00a7 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00a7 }
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r2)     // Catch:{ Exception -> 0x00a7 }
            goto L_0x00ab
        L_0x00a7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00ab:
            java.lang.CharSequence r0 = r10.linkDescription
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r3)
            r10.linkDescription = r0
            if (r7 == 0) goto L_0x00da
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r1 = r0 instanceof android.text.Spannable
            if (r1 != 0) goto L_0x00ce
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r0)
            r10.linkDescription = r1
        L_0x00ce:
            boolean r4 = r10.isOutOwner()
            java.lang.CharSequence r5 = r10.linkDescription
            r6 = 0
            r8 = 0
            r9 = 0
            addUrlsByPattern(r4, r5, r6, r7, r8, r9)
        L_0x00da:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLinkDescription():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0095, code lost:
        if (r9.messageOwner.send_state == 0) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x009b, code lost:
        if (r9.messageOwner.id >= 0) goto L_0x009e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateCaption() {
        /*
            r9 = this;
            java.lang.CharSequence r0 = r9.caption
            if (r0 != 0) goto L_0x00fb
            boolean r0 = r9.isRoundVideo()
            if (r0 == 0) goto L_0x000c
            goto L_0x00fb
        L_0x000c:
            boolean r0 = r9.isMediaEmpty()
            if (r0 != 0) goto L_0x00fb
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r1 != 0) goto L_0x00fb
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00fb
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            java.lang.String r0 = r0.message
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r3)
            r9.caption = r0
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            int r1 = r0.send_state
            r2 = 1
            if (r1 == 0) goto L_0x005e
            r0 = 0
        L_0x0041:
            org.telegram.tgnet.TLRPC$Message r1 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x005c
            org.telegram.tgnet.TLRPC$Message r1 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            java.lang.Object r1 = r1.get(r0)
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName
            if (r1 != 0) goto L_0x0059
            r0 = 1
            goto L_0x0065
        L_0x0059:
            int r0 = r0 + 1
            goto L_0x0041
        L_0x005c:
            r0 = 0
            goto L_0x0065
        L_0x005e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r2
        L_0x0065:
            if (r0 != 0) goto L_0x009e
            long r0 = r9.eventId
            r4 = 0
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_old
            if (r1 != 0) goto L_0x009f
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer68
            if (r1 != 0) goto L_0x009f
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer74
            if (r1 != 0) goto L_0x009f
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument_old
            if (r1 != 0) goto L_0x009f
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer68
            if (r1 != 0) goto L_0x009f
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer74
            if (r0 != 0) goto L_0x009f
            boolean r0 = r9.isOut()
            if (r0 == 0) goto L_0x0097
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x009f
        L_0x0097:
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            int r0 = r0.id
            if (r0 >= 0) goto L_0x009e
            goto L_0x009f
        L_0x009e:
            r2 = 0
        L_0x009f:
            if (r2 == 0) goto L_0x00c3
            java.lang.CharSequence r0 = r9.caption
            boolean r0 = containsUrls(r0)
            if (r0 == 0) goto L_0x00b6
            java.lang.CharSequence r0 = r9.caption     // Catch:{ Exception -> 0x00b2 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00b2 }
            r1 = 5
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r1)     // Catch:{ Exception -> 0x00b2 }
            goto L_0x00b6
        L_0x00b2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b6:
            boolean r3 = r9.isOutOwner()
            java.lang.CharSequence r4 = r9.caption
            r5 = 1
            r6 = 0
            r7 = 0
            r8 = 1
            addUrlsByPattern(r3, r4, r5, r6, r7, r8)
        L_0x00c3:
            java.lang.CharSequence r0 = r9.caption
            r9.addEntitiesToText(r0, r2)
            boolean r0 = r9.isVideo()
            if (r0 == 0) goto L_0x00df
            boolean r1 = r9.isOutOwner()
            java.lang.CharSequence r2 = r9.caption
            r3 = 1
            r4 = 3
            int r5 = r9.getDuration()
            r6 = 0
            addUrlsByPattern(r1, r2, r3, r4, r5, r6)
            goto L_0x00fb
        L_0x00df:
            boolean r0 = r9.isMusic()
            if (r0 != 0) goto L_0x00eb
            boolean r0 = r9.isVoice()
            if (r0 == 0) goto L_0x00fb
        L_0x00eb:
            boolean r1 = r9.isOutOwner()
            java.lang.CharSequence r2 = r9.caption
            r3 = 1
            r4 = 4
            int r5 = r9.getDuration()
            r6 = 0
            addUrlsByPattern(r1, r2, r3, r4, r5, r6)
        L_0x00fb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateCaption():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x01da A[ADDED_TO_REGION, Catch:{ Exception -> 0x01f3 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addUrlsByPattern(boolean r16, java.lang.CharSequence r17, boolean r18, int r19, int r20, boolean r21) {
        /*
            r0 = r17
            r1 = r19
            r2 = 4
            r3 = 3
            r4 = 1
            if (r1 == r3) goto L_0x0034
            if (r1 != r2) goto L_0x000c
            goto L_0x0034
        L_0x000c:
            if (r1 != r4) goto L_0x0021
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x01f3 }
            if (r5 != 0) goto L_0x001a
            java.lang.String r5 = "(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01f3 }
            instagramUrlPattern = r5     // Catch:{ Exception -> 0x01f3 }
        L_0x001a:
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x01f3 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0046
        L_0x0021:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x01f3 }
            if (r5 != 0) goto L_0x002d
            java.lang.String r5 = "(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[^0-9][\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01f3 }
            urlPattern = r5     // Catch:{ Exception -> 0x01f3 }
        L_0x002d:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x01f3 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0046
        L_0x0034:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x01f3 }
            if (r5 != 0) goto L_0x0040
            java.lang.String r5 = "\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01f3 }
            videoTimeUrlPattern = r5     // Catch:{ Exception -> 0x01f3 }
        L_0x0040:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x01f3 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01f3 }
        L_0x0046:
            r6 = r0
            android.text.Spannable r6 = (android.text.Spannable) r6     // Catch:{ Exception -> 0x01f3 }
        L_0x0049:
            boolean r7 = r5.find()     // Catch:{ Exception -> 0x01f3 }
            if (r7 == 0) goto L_0x01f7
            int r7 = r5.start()     // Catch:{ Exception -> 0x01f3 }
            int r8 = r5.end()     // Catch:{ Exception -> 0x01f3 }
            r9 = 0
            r10 = 0
            r11 = 2
            if (r1 == r3) goto L_0x0144
            if (r1 != r2) goto L_0x0060
            goto L_0x0144
        L_0x0060:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x01f3 }
            r13 = 47
            r14 = 35
            r15 = 64
            if (r1 == 0) goto L_0x007b
            if (r12 == r15) goto L_0x0072
            if (r12 == r14) goto L_0x0072
            int r7 = r7 + 1
        L_0x0072:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x01f3 }
            if (r12 == r15) goto L_0x0087
            if (r12 == r14) goto L_0x0087
            goto L_0x0049
        L_0x007b:
            if (r12 == r15) goto L_0x0087
            if (r12 == r14) goto L_0x0087
            if (r12 == r13) goto L_0x0087
            r2 = 36
            if (r12 == r2) goto L_0x0087
            int r7 = r7 + 1
        L_0x0087:
            if (r1 != r4) goto L_0x00d0
            if (r12 != r15) goto L_0x00ad
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f3 }
            r2.<init>()     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = "https://instagram.com/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f3 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0132
        L_0x00ad:
            if (r12 != r14) goto L_0x0132
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f3 }
            r2.<init>()     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = "https://www.instagram.com/explore/tags/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f3 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0132
        L_0x00d0:
            if (r1 != r11) goto L_0x0118
            if (r12 != r15) goto L_0x00f5
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f3 }
            r2.<init>()     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = "https://twitter.com/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f3 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0132
        L_0x00f5:
            if (r12 != r14) goto L_0x0132
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f3 }
            r2.<init>()     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = "https://twitter.com/hashtag/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f3 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0132
        L_0x0118:
            char r2 = r0.charAt(r7)     // Catch:{ Exception -> 0x01f3 }
            if (r2 != r13) goto L_0x0136
            if (r18 == 0) goto L_0x0132
            org.telegram.ui.Components.URLSpanBotCommand r9 = new org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x01f3 }
            java.lang.CharSequence r2 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f3 }
            if (r16 == 0) goto L_0x012e
            r11 = 1
            goto L_0x012f
        L_0x012e:
            r11 = 0
        L_0x012f:
            r9.<init>(r2, r11)     // Catch:{ Exception -> 0x01f3 }
        L_0x0132:
            r2 = r20
            goto L_0x01d8
        L_0x0136:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.CharSequence r2 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0132
        L_0x0144:
            java.lang.Class<android.text.style.URLSpan> r2 = android.text.style.URLSpan.class
            java.lang.Object[] r2 = r6.getSpans(r7, r8, r2)     // Catch:{ Exception -> 0x01f3 }
            android.text.style.URLSpan[] r2 = (android.text.style.URLSpan[]) r2     // Catch:{ Exception -> 0x01f3 }
            if (r2 == 0) goto L_0x0154
            int r2 = r2.length     // Catch:{ Exception -> 0x01f3 }
            if (r2 <= 0) goto L_0x0154
        L_0x0151:
            r2 = 4
            goto L_0x0049
        L_0x0154:
            r5.groupCount()     // Catch:{ Exception -> 0x01f3 }
            int r2 = r5.start(r4)     // Catch:{ Exception -> 0x01f3 }
            int r9 = r5.end(r4)     // Catch:{ Exception -> 0x01f3 }
            int r12 = r5.start(r11)     // Catch:{ Exception -> 0x01f3 }
            int r11 = r5.end(r11)     // Catch:{ Exception -> 0x01f3 }
            int r13 = r5.start(r3)     // Catch:{ Exception -> 0x01f3 }
            int r14 = r5.end(r3)     // Catch:{ Exception -> 0x01f3 }
            java.lang.CharSequence r11 = r0.subSequence(r12, r11)     // Catch:{ Exception -> 0x01f3 }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ Exception -> 0x01f3 }
            int r11 = r11.intValue()     // Catch:{ Exception -> 0x01f3 }
            java.lang.CharSequence r12 = r0.subSequence(r13, r14)     // Catch:{ Exception -> 0x01f3 }
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)     // Catch:{ Exception -> 0x01f3 }
            int r12 = r12.intValue()     // Catch:{ Exception -> 0x01f3 }
            if (r2 < 0) goto L_0x0198
            if (r9 < 0) goto L_0x0198
            java.lang.CharSequence r2 = r0.subSequence(r2, r9)     // Catch:{ Exception -> 0x01f3 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x01f3 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0199
        L_0x0198:
            r2 = -1
        L_0x0199:
            int r11 = r11 * 60
            int r12 = r12 + r11
            if (r2 <= 0) goto L_0x01a3
            int r2 = r2 * 60
            int r2 = r2 * 60
            int r12 = r12 + r2
        L_0x01a3:
            r2 = r20
            if (r12 <= r2) goto L_0x01a8
            goto L_0x0151
        L_0x01a8:
            if (r1 != r3) goto L_0x01c2
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f3 }
            r11.<init>()     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r13 = "video?"
            r11.append(r13)     // Catch:{ Exception -> 0x01f3 }
            r11.append(r12)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x01d8
        L_0x01c2:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f3 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f3 }
            r11.<init>()     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r13 = "audio?"
            r11.append(r13)     // Catch:{ Exception -> 0x01f3 }
            r11.append(r12)     // Catch:{ Exception -> 0x01f3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f3 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01f3 }
        L_0x01d8:
            if (r9 == 0) goto L_0x0151
            if (r21 == 0) goto L_0x01ee
            java.lang.Class<android.text.style.ClickableSpan> r11 = android.text.style.ClickableSpan.class
            java.lang.Object[] r11 = r6.getSpans(r7, r8, r11)     // Catch:{ Exception -> 0x01f3 }
            android.text.style.ClickableSpan[] r11 = (android.text.style.ClickableSpan[]) r11     // Catch:{ Exception -> 0x01f3 }
            if (r11 == 0) goto L_0x01ee
            int r12 = r11.length     // Catch:{ Exception -> 0x01f3 }
            if (r12 <= 0) goto L_0x01ee
            r11 = r11[r10]     // Catch:{ Exception -> 0x01f3 }
            r6.removeSpan(r11)     // Catch:{ Exception -> 0x01f3 }
        L_0x01ee:
            r6.setSpan(r9, r7, r8, r10)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0151
        L_0x01f3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01f7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addUrlsByPattern(boolean, java.lang.CharSequence, boolean, int, int, boolean):void");
    }

    public static int[] getWebDocumentWidthAndHeight(TLRPC.WebDocument webDocument) {
        if (webDocument == null) {
            return null;
        }
        int size = webDocument.attributes.size();
        int i = 0;
        while (i < size) {
            TLRPC.DocumentAttribute documentAttribute = webDocument.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                return new int[]{documentAttribute.w, documentAttribute.h};
            } else if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                return new int[]{documentAttribute.w, documentAttribute.h};
            } else {
                i++;
            }
        }
        return null;
    }

    public static int getWebDocumentDuration(TLRPC.WebDocument webDocument) {
        if (webDocument == null) {
            return 0;
        }
        int size = webDocument.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = webDocument.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                return documentAttribute.duration;
            }
            if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                return documentAttribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(TLRPC.BotInlineResult botInlineResult) {
        int[] webDocumentWidthAndHeight = getWebDocumentWidthAndHeight(botInlineResult.content);
        if (webDocumentWidthAndHeight != null) {
            return webDocumentWidthAndHeight;
        }
        int[] webDocumentWidthAndHeight2 = getWebDocumentWidthAndHeight(botInlineResult.thumb);
        if (webDocumentWidthAndHeight2 == null) {
            return new int[]{0, 0};
        }
        return webDocumentWidthAndHeight2;
    }

    public static int getInlineResultDuration(TLRPC.BotInlineResult botInlineResult) {
        int webDocumentDuration = getWebDocumentDuration(botInlineResult.content);
        return webDocumentDuration == 0 ? getWebDocumentDuration(botInlineResult.thumb) : webDocumentDuration;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r0 = r5.photoThumbs;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasValidGroupId() {
        /*
            r5 = this;
            long r0 = r5.getGroupId()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0016
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.photoThumbs
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0016
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.hasValidGroupId():boolean");
    }

    public long getGroupIdForUse() {
        long j = this.localSentGroupId;
        return j != 0 ? j : this.messageOwner.grouped_id;
    }

    public long getGroupId() {
        long j = this.localGroupId;
        return j != 0 ? j : getGroupIdForUse();
    }

    public static void addLinks(boolean z, CharSequence charSequence) {
        addLinks(z, charSequence, true, false);
    }

    public static void addLinks(boolean z, CharSequence charSequence, boolean z2, boolean z3) {
        if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
            if (charSequence.length() < 1000) {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 5);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 1);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            addUrlsByPattern(z, charSequence, z2, 0, 0, z3);
        }
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence charSequence, boolean z) {
        return addEntitiesToText(charSequence, false, z);
    }

    public boolean addEntitiesToText(CharSequence charSequence, boolean z, boolean z2) {
        if (this.isRestrictedMessage) {
            ArrayList arrayList = new ArrayList();
            TLRPC.TL_messageEntityItalic tL_messageEntityItalic = new TLRPC.TL_messageEntityItalic();
            tL_messageEntityItalic.offset = 0;
            tL_messageEntityItalic.length = charSequence.length();
            arrayList.add(tL_messageEntityItalic);
            return addEntitiesToText(charSequence, arrayList, isOutOwner(), this.type, true, z, z2);
        }
        return addEntitiesToText(charSequence, this.messageOwner.entities, isOutOwner(), this.type, true, z, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0204  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0209 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean addEntitiesToText(java.lang.CharSequence r16, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r17, boolean r18, int r19, boolean r20, boolean r21, boolean r22) {
        /*
            r0 = r16
            boolean r1 = r0 instanceof android.text.Spannable
            r2 = 0
            if (r1 != 0) goto L_0x0008
            return r2
        L_0x0008:
            r1 = r0
            android.text.Spannable r1 = (android.text.Spannable) r1
            int r3 = r16.length()
            java.lang.Class<android.text.style.URLSpan> r4 = android.text.style.URLSpan.class
            java.lang.Object[] r3 = r1.getSpans(r2, r3, r4)
            android.text.style.URLSpan[] r3 = (android.text.style.URLSpan[]) r3
            if (r3 == 0) goto L_0x001e
            int r5 = r3.length
            if (r5 <= 0) goto L_0x001e
            r5 = 1
            goto L_0x001f
        L_0x001e:
            r5 = 0
        L_0x001f:
            boolean r6 = r17.isEmpty()
            if (r6 == 0) goto L_0x0026
            return r5
        L_0x0026:
            if (r21 == 0) goto L_0x002a
            r7 = 2
            goto L_0x002f
        L_0x002a:
            if (r18 == 0) goto L_0x002e
            r7 = 1
            goto L_0x002f
        L_0x002e:
            r7 = 0
        L_0x002f:
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.util.ArrayList r9 = new java.util.ArrayList
            r10 = r17
            r9.<init>(r10)
            org.telegram.messenger.-$$Lambda$MessageObject$TayxkIvYR-DxCFsN9JUXpTKGe7s r10 = org.telegram.messenger.$$Lambda$MessageObject$TayxkIvYRDxCFsN9JUXpTKGe7s.INSTANCE
            java.util.Collections.sort(r9, r10)
            int r10 = r9.size()
            r11 = 0
        L_0x0045:
            r13 = 0
            if (r11 >= r10) goto L_0x020e
            java.lang.Object r14 = r9.get(r11)
            org.telegram.tgnet.TLRPC$MessageEntity r14 = (org.telegram.tgnet.TLRPC.MessageEntity) r14
            int r15 = r14.length
            if (r15 <= 0) goto L_0x0208
            int r15 = r14.offset
            if (r15 < 0) goto L_0x0208
            int r2 = r16.length()
            if (r15 < r2) goto L_0x005e
            goto L_0x0208
        L_0x005e:
            int r2 = r14.offset
            int r15 = r14.length
            int r2 = r2 + r15
            int r15 = r16.length()
            if (r2 <= r15) goto L_0x0072
            int r2 = r16.length()
            int r15 = r14.offset
            int r2 = r2 - r15
            r14.length = r2
        L_0x0072:
            if (r22 == 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl
            if (r2 == 0) goto L_0x00d2
        L_0x009c:
            if (r3 == 0) goto L_0x00d2
            int r2 = r3.length
            if (r2 <= 0) goto L_0x00d2
            r2 = 0
        L_0x00a2:
            int r15 = r3.length
            if (r2 >= r15) goto L_0x00d2
            r15 = r3[r2]
            if (r15 != 0) goto L_0x00aa
            goto L_0x00cf
        L_0x00aa:
            r15 = r3[r2]
            int r15 = r1.getSpanStart(r15)
            r12 = r3[r2]
            int r12 = r1.getSpanEnd(r12)
            int r6 = r14.offset
            if (r6 > r15) goto L_0x00bf
            int r4 = r14.length
            int r6 = r6 + r4
            if (r6 >= r15) goto L_0x00c8
        L_0x00bf:
            int r4 = r14.offset
            if (r4 > r12) goto L_0x00cf
            int r6 = r14.length
            int r4 = r4 + r6
            if (r4 < r12) goto L_0x00cf
        L_0x00c8:
            r4 = r3[r2]
            r1.removeSpan(r4)
            r3[r2] = r13
        L_0x00cf:
            int r2 = r2 + 1
            goto L_0x00a2
        L_0x00d2:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r2.<init>()
            int r4 = r14.offset
            r2.start = r4
            int r4 = r2.start
            int r6 = r14.length
            int r4 = r4 + r6
            r2.end = r4
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike
            if (r4 == 0) goto L_0x00ed
            r4 = 8
            r2.flags = r4
        L_0x00ea:
            r4 = 2
            goto L_0x0161
        L_0x00ed:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline
            if (r4 == 0) goto L_0x00f6
            r4 = 16
            r2.flags = r4
            goto L_0x00ea
        L_0x00f6:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote
            if (r4 == 0) goto L_0x00ff
            r4 = 32
            r2.flags = r4
            goto L_0x00ea
        L_0x00ff:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold
            if (r4 == 0) goto L_0x0107
            r4 = 1
            r2.flags = r4
            goto L_0x00ea
        L_0x0107:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic
            if (r4 == 0) goto L_0x010f
            r4 = 2
            r2.flags = r4
            goto L_0x0161
        L_0x010f:
            r4 = 2
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode
            if (r6 != 0) goto L_0x015e
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre
            if (r6 == 0) goto L_0x0119
            goto L_0x015e
        L_0x0119:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName
            r12 = 64
            if (r6 == 0) goto L_0x0128
            if (r20 != 0) goto L_0x0123
            goto L_0x0208
        L_0x0123:
            r2.flags = r12
            r2.urlEntity = r14
            goto L_0x0161
        L_0x0128:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName
            if (r6 == 0) goto L_0x0135
            if (r20 != 0) goto L_0x0130
            goto L_0x0208
        L_0x0130:
            r2.flags = r12
            r2.urlEntity = r14
            goto L_0x0161
        L_0x0135:
            if (r22 == 0) goto L_0x013d
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl
            if (r6 != 0) goto L_0x013d
            goto L_0x0208
        L_0x013d:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl
            if (r6 != 0) goto L_0x0145
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl
            if (r6 == 0) goto L_0x014f
        L_0x0145:
            java.lang.String r6 = r14.url
            boolean r6 = org.telegram.messenger.browser.Browser.isPassportUrl(r6)
            if (r6 == 0) goto L_0x014f
            goto L_0x0208
        L_0x014f:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMention
            if (r6 == 0) goto L_0x0157
            if (r20 != 0) goto L_0x0157
            goto L_0x0208
        L_0x0157:
            r6 = 128(0x80, float:1.794E-43)
            r2.flags = r6
            r2.urlEntity = r14
            goto L_0x0161
        L_0x015e:
            r6 = 4
            r2.flags = r6
        L_0x0161:
            int r6 = r8.size()
            r12 = r6
            r6 = 0
        L_0x0167:
            if (r6 >= r12) goto L_0x01fd
            java.lang.Object r13 = r8.get(r6)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r13 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r13
            int r14 = r2.start
            int r15 = r13.start
            if (r14 <= r15) goto L_0x01bb
            int r15 = r13.end
            if (r14 < r15) goto L_0x017a
            goto L_0x01bf
        L_0x017a:
            int r14 = r2.end
            if (r14 >= r15) goto L_0x019d
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r6 = r6 + 1
            int r12 = r12 + 1
            r8.add(r6, r14)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r13)
            int r15 = r2.end
            r14.start = r15
            r15 = 1
            int r6 = r6 + r15
            int r12 = r12 + r15
            r8.add(r6, r14)
            goto L_0x01b2
        L_0x019d:
            if (r14 < r15) goto L_0x01b2
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r15 = r13.end
            r14.end = r15
            int r6 = r6 + 1
            int r12 = r12 + 1
            r8.add(r6, r14)
        L_0x01b2:
            int r14 = r2.start
            int r15 = r13.end
            r2.start = r15
            r13.end = r14
            goto L_0x01bf
        L_0x01bb:
            int r14 = r2.end
            if (r15 < r14) goto L_0x01c1
        L_0x01bf:
            r4 = 1
            goto L_0x01f9
        L_0x01c1:
            int r4 = r13.end
            if (r14 != r4) goto L_0x01c9
            r13.merge(r2)
            goto L_0x01f6
        L_0x01c9:
            if (r14 >= r4) goto L_0x01e3
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r4.<init>(r13)
            r4.merge(r2)
            int r14 = r2.end
            r4.end = r14
            int r6 = r6 + 1
            int r12 = r12 + 1
            r8.add(r6, r4)
            int r4 = r2.end
            r13.start = r4
            goto L_0x01f6
        L_0x01e3:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r4.<init>(r2)
            int r14 = r13.end
            r4.start = r14
            int r6 = r6 + 1
            int r12 = r12 + 1
            r8.add(r6, r4)
            r13.merge(r2)
        L_0x01f6:
            r2.end = r15
            goto L_0x01bf
        L_0x01f9:
            int r6 = r6 + r4
            r4 = 2
            goto L_0x0167
        L_0x01fd:
            r4 = 1
            int r6 = r2.start
            int r12 = r2.end
            if (r6 >= r12) goto L_0x0209
            r8.add(r2)
            goto L_0x0209
        L_0x0208:
            r4 = 1
        L_0x0209:
            int r11 = r11 + 1
            r2 = 0
            goto L_0x0045
        L_0x020e:
            r4 = 1
            int r2 = r8.size()
            r3 = 0
        L_0x0214:
            if (r3 >= r2) goto L_0x03b3
            java.lang.Object r6 = r8.get(r3)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r6 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r6
            org.telegram.tgnet.TLRPC$MessageEntity r9 = r6.urlEntity
            if (r9 == 0) goto L_0x022a
            int r10 = r9.offset
            int r9 = r9.length
            int r9 = r9 + r10
            java.lang.String r9 = android.text.TextUtils.substring(r0, r10, r9)
            goto L_0x022b
        L_0x022a:
            r9 = r13
        L_0x022b:
            org.telegram.tgnet.TLRPC$MessageEntity r10 = r6.urlEntity
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand
            r12 = 33
            if (r11 == 0) goto L_0x0241
            org.telegram.ui.Components.URLSpanBotCommand r10 = new org.telegram.ui.Components.URLSpanBotCommand
            r10.<init>(r9, r7, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r10, r9, r6, r12)
            goto L_0x02ce
        L_0x0241:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityHashtag
            if (r11 != 0) goto L_0x03a2
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMention
            if (r11 != 0) goto L_0x03a2
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCashtag
            if (r11 == 0) goto L_0x024f
            goto L_0x03a2
        L_0x024f:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityEmail
            if (r11 == 0) goto L_0x0271
            org.telegram.ui.Components.URLSpanReplacement r10 = new org.telegram.ui.Components.URLSpanReplacement
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r14 = "mailto:"
            r11.append(r14)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r10.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r10, r9, r6, r12)
            goto L_0x02ce
        L_0x0271:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl
            if (r11 == 0) goto L_0x02ac
            java.lang.String r5 = r9.toLowerCase()
            java.lang.String r10 = "://"
            boolean r5 = r5.contains(r10)
            if (r5 != 0) goto L_0x029f
            org.telegram.ui.Components.URLSpanBrowser r5 = new org.telegram.ui.Components.URLSpanBrowser
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "http://"
            r10.append(r11)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r5.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r5, r9, r6, r12)
            goto L_0x02cd
        L_0x029f:
            org.telegram.ui.Components.URLSpanBrowser r5 = new org.telegram.ui.Components.URLSpanBrowser
            r5.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r5, r9, r6, r12)
            goto L_0x02cd
        L_0x02ac:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBankCard
            if (r11 == 0) goto L_0x02d1
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "card:"
            r10.append(r11)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r5.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r5, r9, r6, r12)
        L_0x02cd:
            r5 = 1
        L_0x02ce:
            r10 = 4
            goto L_0x03af
        L_0x02d1:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPhone
            if (r11 == 0) goto L_0x030e
            java.lang.String r5 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r9)
            java.lang.String r10 = "+"
            boolean r9 = r9.startsWith(r10)
            if (r9 == 0) goto L_0x02f0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r10)
            r9.append(r5)
            java.lang.String r5 = r9.toString()
        L_0x02f0:
            org.telegram.ui.Components.URLSpanBrowser r9 = new org.telegram.ui.Components.URLSpanBrowser
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "tel:"
            r10.append(r11)
            r10.append(r5)
            java.lang.String r5 = r10.toString()
            r9.<init>(r5, r6)
            int r5 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r5, r6, r12)
            goto L_0x02cd
        L_0x030e:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl
            if (r9 == 0) goto L_0x0321
            org.telegram.ui.Components.URLSpanReplacement r9 = new org.telegram.ui.Components.URLSpanReplacement
            java.lang.String r10 = r10.url
            r9.<init>(r10, r6)
            int r10 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r10, r6, r12)
            goto L_0x02ce
        L_0x0321:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName
            java.lang.String r11 = ""
            if (r9 == 0) goto L_0x0349
            org.telegram.ui.Components.URLSpanUserMention r9 = new org.telegram.ui.Components.URLSpanUserMention
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r11)
            org.telegram.tgnet.TLRPC$MessageEntity r11 = r6.urlEntity
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r11 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r11
            int r11 = r11.user_id
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>(r10, r7, r6)
            int r10 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r10, r6, r12)
            goto L_0x02ce
        L_0x0349:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName
            if (r9 == 0) goto L_0x0372
            org.telegram.ui.Components.URLSpanUserMention r9 = new org.telegram.ui.Components.URLSpanUserMention
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r11)
            org.telegram.tgnet.TLRPC$MessageEntity r11 = r6.urlEntity
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r11 = (org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName) r11
            org.telegram.tgnet.TLRPC$InputUser r11 = r11.user_id
            int r11 = r11.user_id
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>(r10, r7, r6)
            int r10 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r10, r6, r12)
            goto L_0x02ce
        L_0x0372:
            int r9 = r6.flags
            r10 = 4
            r9 = r9 & r10
            if (r9 == 0) goto L_0x0395
            org.telegram.ui.Components.URLSpanMono r9 = new org.telegram.ui.Components.URLSpanMono
            int r11 = r6.start
            int r14 = r6.end
            r17 = r9
            r18 = r1
            r19 = r11
            r20 = r14
            r21 = r7
            r22 = r6
            r17.<init>(r18, r19, r20, r21, r22)
            int r11 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r11, r6, r12)
            goto L_0x03af
        L_0x0395:
            org.telegram.ui.Components.TextStyleSpan r9 = new org.telegram.ui.Components.TextStyleSpan
            r9.<init>(r6)
            int r11 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r11, r6, r12)
            goto L_0x03af
        L_0x03a2:
            r10 = 4
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline
            r11.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r11, r9, r6, r12)
        L_0x03af:
            int r3 = r3 + 1
            goto L_0x0214
        L_0x03b3:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addEntitiesToText(java.lang.CharSequence, java.util.ArrayList, boolean, int, boolean, boolean, boolean):boolean");
    }

    static /* synthetic */ int lambda$addEntitiesToText$0(TLRPC.MessageEntity messageEntity, TLRPC.MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public boolean needDrawShareButton() {
        int i;
        TLRPC.Chat chat;
        String str;
        if (this.scheduled || this.eventId != 0) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            return true;
        }
        int i2 = this.type;
        if (!(i2 == 13 || i2 == 15)) {
            TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
            if (messageFwdHeader != null && messageFwdHeader.channel_id != 0 && !isOutOwner()) {
                return true;
            }
            if (isFromUser()) {
                TLRPC.MessageMedia messageMedia = this.messageOwner.media;
                if ((messageMedia instanceof TLRPC.TL_messageMediaEmpty) || messageMedia == null || ((messageMedia instanceof TLRPC.TL_messageMediaWebPage) && !(messageMedia.webpage instanceof TLRPC.TL_webPage))) {
                    return false;
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                if (user != null && user.bot) {
                    return true;
                }
                if (!isOut()) {
                    TLRPC.MessageMedia messageMedia2 = this.messageOwner.media;
                    if ((messageMedia2 instanceof TLRPC.TL_messageMediaGame) || (messageMedia2 instanceof TLRPC.TL_messageMediaInvoice)) {
                        return true;
                    }
                    if (!isMegagroup() || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id))) == null || (str = chat.username) == null || str.length() <= 0) {
                        return false;
                    }
                    TLRPC.MessageMedia messageMedia3 = this.messageOwner.media;
                    if ((messageMedia3 instanceof TLRPC.TL_messageMediaContact) || (messageMedia3 instanceof TLRPC.TL_messageMediaGeo)) {
                        return false;
                    }
                    return true;
                }
            } else {
                TLRPC.Message message = this.messageOwner;
                if (message.from_id < 0 || message.post) {
                    TLRPC.Message message2 = this.messageOwner;
                    if (message2.to_id.channel_id == 0 || ((message2.via_bot_id != 0 || message2.reply_to_msg_id != 0) && ((i = this.type) == 13 || i == 15))) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isYouTubeVideo() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0026
            java.lang.String r0 = r0.embed_url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.lang.String r0 = r0.site_name
            java.lang.String r1 = "YouTube"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0026
            r0 = 1
            goto L_0x0027
        L_0x0026:
            r0 = 0
        L_0x0027:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isYouTubeVideo():boolean");
    }

    public int getMaxMessageTextWidth() {
        TLRPC.WebPage webPage;
        if (!AndroidUtilities.isTablet() || this.eventId == 0) {
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
        } else {
            this.generatedWithMinSize = AndroidUtilities.dp(530.0f);
        }
        this.generatedWithDensity = AndroidUtilities.density;
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        int i = 0;
        if ((messageMedia instanceof TLRPC.TL_messageMediaWebPage) && (webPage = messageMedia.webpage) != null && "telegram_background".equals(webPage.type)) {
            try {
                Uri parse = Uri.parse(this.messageOwner.media.webpage.url);
                String lastPathSegment = parse.getLastPathSegment();
                if (parse.getQueryParameter("bg_color") != null) {
                    i = AndroidUtilities.dp(220.0f);
                } else if (lastPathSegment.length() == 6 || (lastPathSegment.length() == 13 && lastPathSegment.charAt(6) == '-')) {
                    i = AndroidUtilities.dp(200.0f);
                }
            } catch (Exception unused) {
            }
        } else if (isAndroidTheme()) {
            i = AndroidUtilities.dp(200.0f);
        }
        if (i != 0) {
            return i;
        }
        int dp = this.generatedWithMinSize - AndroidUtilities.dp((!needDrawAvatarInternal() || isOutOwner()) ? 80.0f : 132.0f);
        if (needDrawShareButton() && !isOutOwner()) {
            dp -= AndroidUtilities.dp(10.0f);
        }
        int i2 = dp;
        return this.messageOwner.media instanceof TLRPC.TL_messageMediaGame ? i2 - AndroidUtilities.dp(10.0f) : i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0077, code lost:
        if ((r0.media instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported) == false) goto L_0x007b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02a2 A[Catch:{ Exception -> 0x043c }] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02c7  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0324  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0329  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0336  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0122 A[Catch:{ Exception -> 0x0477 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0141 A[Catch:{ Exception -> 0x0477 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0164  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0178  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLayout(org.telegram.tgnet.TLRPC.User r31) {
        /*
            r30 = this;
            r1 = r30
            int r0 = r1.type
            if (r0 != 0) goto L_0x047b
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            if (r0 == 0) goto L_0x047b
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0016
            goto L_0x047b
        L_0x0016:
            r30.generateLinkDescription()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.textLayoutBlocks = r0
            r2 = 0
            r1.textWidth = r2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r3 = r0.send_state
            r4 = 1
            if (r3 == 0) goto L_0x002c
            r0 = 0
            goto L_0x0033
        L_0x002c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r4
        L_0x0033:
            if (r0 != 0) goto L_0x007b
            long r5 = r1.eventId
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old2
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old3
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old4
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old2
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_secret
            if (r3 != 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r0 != 0) goto L_0x0079
            boolean r0 = r30.isOut()
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x0079
        L_0x006d:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r3 = r0.id
            if (r3 < 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x007b
        L_0x0079:
            r3 = 1
            goto L_0x007c
        L_0x007b:
            r3 = 0
        L_0x007c:
            if (r3 == 0) goto L_0x0088
            boolean r0 = r30.isOutOwner()
            java.lang.CharSequence r5 = r1.messageText
            addLinks(r0, r5, r4, r4)
            goto L_0x00a3
        L_0x0088:
            java.lang.CharSequence r0 = r1.messageText
            boolean r5 = r0 instanceof android.text.Spannable
            if (r5 == 0) goto L_0x00a3
            int r0 = r0.length()
            r5 = 1000(0x3e8, float:1.401E-42)
            if (r0 >= r5) goto L_0x00a3
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ all -> 0x009f }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ all -> 0x009f }
            r5 = 4
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r5)     // Catch:{ all -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00a3:
            boolean r0 = r30.isYouTubeVideo()
            if (r0 != 0) goto L_0x00f4
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x00b4
            boolean r0 = r0.isYouTubeVideo()
            if (r0 == 0) goto L_0x00b4
            goto L_0x00f4
        L_0x00b4:
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x0104
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00d1
            boolean r5 = r30.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 3
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            int r9 = r0.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x0104
        L_0x00d1:
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            boolean r0 = r0.isMusic()
            if (r0 != 0) goto L_0x00e1
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0104
        L_0x00e1:
            boolean r5 = r30.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 4
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            int r9 = r0.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x0104
        L_0x00f4:
            boolean r11 = r30.isOutOwner()
            java.lang.CharSequence r12 = r1.messageText
            r13 = 0
            r14 = 3
            r15 = 2147483647(0x7fffffff, float:NaN)
            r16 = 0
            addUrlsByPattern(r11, r12, r13, r14, r15, r16)
        L_0x0104:
            java.lang.CharSequence r0 = r1.messageText
            boolean r3 = r1.addEntitiesToText(r0, r3)
            int r15 = r30.getMaxMessageTextWidth()
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0119
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x011b
        L_0x0119:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x011b:
            r14 = r0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0477 }
            r13 = 24
            if (r0 < r13) goto L_0x0141
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ Exception -> 0x0477 }
            java.lang.CharSequence r5 = r1.messageText     // Catch:{ Exception -> 0x0477 }
            int r5 = r5.length()     // Catch:{ Exception -> 0x0477 }
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r2, r5, r14, r15)     // Catch:{ Exception -> 0x0477 }
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r4)     // Catch:{ Exception -> 0x0477 }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x0477 }
            android.text.Layout$Alignment r5 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0477 }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r5)     // Catch:{ Exception -> 0x0477 }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x0477 }
            goto L_0x0151
        L_0x0141:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0477 }
            java.lang.CharSequence r6 = r1.messageText     // Catch:{ Exception -> 0x0477 }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0477 }
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            r5 = r0
            r7 = r14
            r8 = r15
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0477 }
        L_0x0151:
            r12 = r0
            int r0 = r12.getHeight()
            r1.textHeight = r0
            int r0 = r12.getLineCount()
            r1.linesCount = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x0164
            r11 = 1
            goto L_0x0171
        L_0x0164:
            int r0 = r1.linesCount
            float r0 = (float) r0
            r5 = 1092616192(0x41200000, float:10.0)
            float r0 = r0 / r5
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            r11 = r0
        L_0x0171:
            r10 = 0
            r8 = 0
            r9 = 0
            r16 = 0
        L_0x0176:
            if (r9 >= r11) goto L_0x0476
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x017f
            int r0 = r1.linesCount
            goto L_0x0188
        L_0x017f:
            r0 = 10
            int r5 = r1.linesCount
            int r5 = r5 - r8
            int r0 = java.lang.Math.min(r0, r5)
        L_0x0188:
            org.telegram.messenger.MessageObject$TextLayoutBlock r7 = new org.telegram.messenger.MessageObject$TextLayoutBlock
            r7.<init>()
            r6 = 2
            if (r11 != r4) goto L_0x0206
            r7.textLayout = r12
            r7.textYOffset = r10
            r7.charactersOffset = r2
            java.lang.CharSequence r5 = r12.getText()
            int r5 = r5.length()
            r7.charactersEnd = r5
            int r5 = r1.emojiOnlyCount
            if (r5 == 0) goto L_0x01f2
            if (r5 == r4) goto L_0x01db
            if (r5 == r6) goto L_0x01c4
            r6 = 3
            if (r5 == r6) goto L_0x01ac
            goto L_0x01f2
        L_0x01ac:
            int r5 = r1.textHeight
            r6 = 1082549862(0x40866666, float:4.2)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r17
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
            goto L_0x01f2
        L_0x01c4:
            int r5 = r1.textHeight
            r6 = 1083179008(0x40900000, float:4.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r17
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
            goto L_0x01f2
        L_0x01db:
            int r5 = r1.textHeight
            r6 = 1084856730(0x40a9999a, float:5.3)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r17
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
        L_0x01f2:
            int r5 = r1.textHeight
            r7.height = r5
            r5 = r7
            r2 = r8
            r4 = r9
            r8 = r11
            r6 = r12
            r18 = r14
            r7 = r16
            r17 = 24
            r25 = 2
        L_0x0203:
            r9 = r0
            goto L_0x02e9
        L_0x0206:
            int r6 = r12.getLineStart(r8)
            int r5 = r8 + r0
            int r5 = r5 - r4
            int r5 = r12.getLineEnd(r5)
            if (r5 >= r6) goto L_0x0224
            r19 = r3
            r20 = r8
            r4 = r9
            r8 = r11
            r28 = r12
            r18 = r14
            r3 = r15
            r2 = 0
            r10 = 1
            r17 = 24
            goto L_0x0462
        L_0x0224:
            r7.charactersOffset = r6
            r7.charactersEnd = r5
            if (r3 == 0) goto L_0x025d
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x044f }
            if (r10 < r13) goto L_0x025d
            java.lang.CharSequence r10 = r1.messageText     // Catch:{ Exception -> 0x044f }
            r18 = 1073741824(0x40000000, float:2.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x044f }
            int r13 = r15 + r18
            android.text.StaticLayout$Builder r5 = android.text.StaticLayout.Builder.obtain(r10, r6, r5, r14, r13)     // Catch:{ Exception -> 0x044f }
            android.text.StaticLayout$Builder r5 = r5.setBreakStrategy(r4)     // Catch:{ Exception -> 0x044f }
            android.text.StaticLayout$Builder r5 = r5.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x044f }
            android.text.Layout$Alignment r6 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x044f }
            android.text.StaticLayout$Builder r5 = r5.setAlignment(r6)     // Catch:{ Exception -> 0x044f }
            android.text.StaticLayout r5 = r5.build()     // Catch:{ Exception -> 0x044f }
            r7.textLayout = r5     // Catch:{ Exception -> 0x044f }
            r5 = r7
            r2 = r8
            r4 = r9
            r27 = r11
            r6 = r12
            r18 = r14
            r17 = 24
            r25 = 2
            goto L_0x0299
        L_0x025d:
            android.text.StaticLayout r13 = new android.text.StaticLayout     // Catch:{ Exception -> 0x044f }
            java.lang.CharSequence r10 = r1.messageText     // Catch:{ Exception -> 0x044f }
            android.text.Layout$Alignment r18 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x044f }
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r23 = r5
            r5 = r13
            r24 = r6
            r25 = 2
            r6 = r10
            r10 = r7
            r7 = r24
            r2 = r8
            r8 = r23
            r4 = r9
            r9 = r14
            r26 = r10
            r10 = r15
            r27 = r11
            r11 = r18
            r28 = r12
            r12 = r20
            r29 = r13
            r17 = 24
            r13 = r21
            r18 = r14
            r14 = r22
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0444 }
            r5 = r26
            r6 = r29
            r5.textLayout = r6     // Catch:{ Exception -> 0x0444 }
            r6 = r28
        L_0x0299:
            int r7 = r6.getLineTop(r2)     // Catch:{ Exception -> 0x043c }
            float r7 = (float) r7     // Catch:{ Exception -> 0x043c }
            r5.textYOffset = r7     // Catch:{ Exception -> 0x043c }
            if (r4 == 0) goto L_0x02a9
            float r7 = r5.textYOffset     // Catch:{ Exception -> 0x043c }
            float r7 = r7 - r16
            int r7 = (int) r7     // Catch:{ Exception -> 0x043c }
            r5.height = r7     // Catch:{ Exception -> 0x043c }
        L_0x02a9:
            int r7 = r5.height     // Catch:{ Exception -> 0x043c }
            android.text.StaticLayout r8 = r5.textLayout     // Catch:{ Exception -> 0x043c }
            android.text.StaticLayout r9 = r5.textLayout     // Catch:{ Exception -> 0x043c }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x043c }
            r10 = 1
            int r9 = r9 - r10
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x043c }
            int r7 = java.lang.Math.max(r7, r8)     // Catch:{ Exception -> 0x043c }
            r5.height = r7     // Catch:{ Exception -> 0x043c }
            float r7 = r5.textYOffset     // Catch:{ Exception -> 0x043c }
            r8 = r27
            int r11 = r8 + -1
            if (r4 != r11) goto L_0x0203
            android.text.StaticLayout r9 = r5.textLayout
            int r9 = r9.getLineCount()
            int r9 = java.lang.Math.max(r0, r9)
            int r0 = r1.textHeight     // Catch:{ Exception -> 0x02e5 }
            float r10 = r5.textYOffset     // Catch:{ Exception -> 0x02e5 }
            android.text.StaticLayout r11 = r5.textLayout     // Catch:{ Exception -> 0x02e5 }
            int r11 = r11.getHeight()     // Catch:{ Exception -> 0x02e5 }
            float r11 = (float) r11     // Catch:{ Exception -> 0x02e5 }
            float r10 = r10 + r11
            int r10 = (int) r10     // Catch:{ Exception -> 0x02e5 }
            int r0 = java.lang.Math.max(r0, r10)     // Catch:{ Exception -> 0x02e5 }
            r1.textHeight = r0     // Catch:{ Exception -> 0x02e5 }
            goto L_0x02e9
        L_0x02e5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02e9:
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r1.textLayoutBlocks
            r0.add(r5)
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x0302 }
            int r10 = r9 + -1
            float r10 = r0.getLineLeft(r10)     // Catch:{ Exception -> 0x0302 }
            r11 = 0
            if (r4 != 0) goto L_0x030c
            int r0 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r0 < 0) goto L_0x030c
            r1.textXOffset = r10     // Catch:{ Exception -> 0x0300 }
            goto L_0x030c
        L_0x0300:
            r0 = move-exception
            goto L_0x0304
        L_0x0302:
            r0 = move-exception
            r11 = 0
        L_0x0304:
            if (r4 != 0) goto L_0x0308
            r1.textXOffset = r11
        L_0x0308:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r10 = 0
        L_0x030c:
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x0315 }
            int r12 = r9 + -1
            float r0 = r0.getLineWidth(r12)     // Catch:{ Exception -> 0x0315 }
            goto L_0x031a
        L_0x0315:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x031a:
            double r12 = (double) r0
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            int r13 = r15 + 80
            if (r12 <= r13) goto L_0x0325
            r12 = r15
        L_0x0325:
            int r13 = r8 + -1
            if (r4 != r13) goto L_0x032b
            r1.lastLineWidth = r12
        L_0x032b:
            float r0 = r0 + r10
            r14 = r12
            double r11 = (double) r0
            double r11 = java.lang.Math.ceil(r11)
            int r11 = (int) r11
            r12 = 1
            if (r9 <= r12) goto L_0x03ed
            r19 = r3
            r28 = r6
            r16 = r7
            r7 = r11
            r3 = r14
            r6 = 0
            r10 = 0
            r12 = 0
            r14 = 0
        L_0x0342:
            if (r10 >= r9) goto L_0x03c8
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x034b }
            float r0 = r0.getLineWidth(r10)     // Catch:{ Exception -> 0x034b }
            goto L_0x0350
        L_0x034b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0350:
            r20 = r2
            int r2 = r15 + 20
            float r2 = (float) r2
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x035a
            float r0 = (float) r15
        L_0x035a:
            r2 = r0
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x0362 }
            float r0 = r0.getLineLeft(r10)     // Catch:{ Exception -> 0x0362 }
            goto L_0x0367
        L_0x0362:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0367:
            r21 = 0
            int r22 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1))
            if (r22 <= 0) goto L_0x0383
            r21 = r9
            float r9 = r1.textXOffset
            float r9 = java.lang.Math.min(r9, r0)
            r1.textXOffset = r9
            byte r9 = r5.directionFlags
            r22 = r15
            r15 = 1
            r9 = r9 | r15
            byte r9 = (byte) r9
            r5.directionFlags = r9
            r1.hasRtl = r15
            goto L_0x038e
        L_0x0383:
            r21 = r9
            r22 = r15
            byte r9 = r5.directionFlags
            r9 = r9 | 2
            byte r9 = (byte) r9
            r5.directionFlags = r9
        L_0x038e:
            if (r12 != 0) goto L_0x039f
            r9 = 0
            int r15 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r15 != 0) goto L_0x039f
            android.text.StaticLayout r9 = r5.textLayout     // Catch:{ Exception -> 0x039e }
            int r9 = r9.getParagraphDirection(r10)     // Catch:{ Exception -> 0x039e }
            r15 = 1
            if (r9 != r15) goto L_0x039f
        L_0x039e:
            r12 = 1
        L_0x039f:
            float r6 = java.lang.Math.max(r6, r2)
            float r0 = r0 + r2
            float r14 = java.lang.Math.max(r14, r0)
            r9 = r14
            double r14 = (double) r2
            double r14 = java.lang.Math.ceil(r14)
            int r2 = (int) r14
            int r3 = java.lang.Math.max(r3, r2)
            double r14 = (double) r0
            double r14 = java.lang.Math.ceil(r14)
            int r0 = (int) r14
            int r7 = java.lang.Math.max(r7, r0)
            int r10 = r10 + 1
            r14 = r9
            r2 = r20
            r9 = r21
            r15 = r22
            goto L_0x0342
        L_0x03c8:
            r20 = r2
            r21 = r9
            r22 = r15
            if (r12 == 0) goto L_0x03d5
            if (r4 != r13) goto L_0x03da
            r1.lastLineWidth = r11
            goto L_0x03da
        L_0x03d5:
            if (r4 != r13) goto L_0x03d9
            r1.lastLineWidth = r3
        L_0x03d9:
            r14 = r6
        L_0x03da:
            int r0 = r1.textWidth
            double r2 = (double) r14
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r0 = java.lang.Math.max(r0, r2)
            r1.textWidth = r0
            r3 = r22
            r2 = 0
            r10 = 1
            goto L_0x0439
        L_0x03ed:
            r20 = r2
            r19 = r3
            r28 = r6
            r16 = r7
            r21 = r9
            r22 = r15
            r2 = 0
            int r0 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0422
            float r0 = r1.textXOffset
            float r0 = java.lang.Math.min(r0, r10)
            r1.textXOffset = r0
            float r0 = r1.textXOffset
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            r15 = r14
            if (r0 != 0) goto L_0x0411
            float r0 = (float) r15
            float r0 = r0 + r10
            int r12 = (int) r0
            goto L_0x0412
        L_0x0411:
            r12 = r15
        L_0x0412:
            r10 = 1
            if (r8 == r10) goto L_0x0417
            r0 = 1
            goto L_0x0418
        L_0x0417:
            r0 = 0
        L_0x0418:
            r1.hasRtl = r0
            byte r0 = r5.directionFlags
            r0 = r0 | r10
            byte r0 = (byte) r0
            r5.directionFlags = r0
            r15 = r12
            goto L_0x042b
        L_0x0422:
            r15 = r14
            r10 = 1
            byte r0 = r5.directionFlags
            r0 = r0 | 2
            byte r0 = (byte) r0
            r5.directionFlags = r0
        L_0x042b:
            int r0 = r1.textWidth
            r3 = r22
            int r5 = java.lang.Math.min(r3, r15)
            int r0 = java.lang.Math.max(r0, r5)
            r1.textWidth = r0
        L_0x0439:
            int r0 = r20 + r21
            goto L_0x0464
        L_0x043c:
            r0 = move-exception
            r20 = r2
            r19 = r3
            r28 = r6
            goto L_0x0449
        L_0x0444:
            r0 = move-exception
            r20 = r2
            r19 = r3
        L_0x0449:
            r3 = r15
            r8 = r27
            r2 = 0
            r10 = 1
            goto L_0x045f
        L_0x044f:
            r0 = move-exception
            r19 = r3
            r20 = r8
            r4 = r9
            r8 = r11
            r28 = r12
            r18 = r14
            r3 = r15
            r2 = 0
            r10 = 1
            r17 = 24
        L_0x045f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0462:
            r0 = r20
        L_0x0464:
            int r9 = r4 + 1
            r15 = r3
            r11 = r8
            r14 = r18
            r3 = r19
            r12 = r28
            r2 = 0
            r4 = 1
            r10 = 0
            r13 = 24
            r8 = r0
            goto L_0x0176
        L_0x0476:
            return
        L_0x0477:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x047b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        TLRPC.Peer peer;
        TLRPC.Message message = this.messageOwner;
        if (!message.out || message.from_id <= 0 || message.post) {
            return false;
        }
        if (message.fwd_from == null) {
            return true;
        }
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (getDialogId() == ((long) clientUserId)) {
            TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
            if (messageFwdHeader.from_id == clientUserId && ((peer = messageFwdHeader.saved_from_peer) == null || peer.user_id == clientUserId)) {
                return true;
            }
            TLRPC.Peer peer2 = this.messageOwner.fwd_from.saved_from_peer;
            if (peer2 == null || peer2.user_id != clientUserId) {
                return false;
            }
            return true;
        }
        TLRPC.Peer peer3 = this.messageOwner.fwd_from.saved_from_peer;
        if (peer3 == null || peer3.user_id == clientUserId) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000e, code lost:
        r0 = r5.messageOwner.fwd_from;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawAvatar() {
        /*
            r5 = this;
            boolean r0 = r5.isFromUser()
            if (r0 != 0) goto L_0x001b
            long r0 = r5.eventId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x001b
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x0019
            org.telegram.tgnet.TLRPC$Peer r0 = r0.saved_from_peer
            if (r0 == 0) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            r0 = 0
            goto L_0x001c
        L_0x001b:
            r0 = 1
        L_0x001c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawAvatar():boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0014, code lost:
        r0 = r5.messageOwner.fwd_from;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawAvatarInternal() {
        /*
            r5 = this;
            boolean r0 = r5.isFromChat()
            if (r0 == 0) goto L_0x000c
            boolean r0 = r5.isFromUser()
            if (r0 != 0) goto L_0x0021
        L_0x000c:
            long r0 = r5.eventId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0021
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x001f
            org.telegram.tgnet.TLRPC$Peer r0 = r0.saved_from_peer
            if (r0 == 0) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r0 = 0
            goto L_0x0022
        L_0x0021:
            r0 = 1
        L_0x0022:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawAvatarInternal():boolean");
    }

    public boolean isFromChat() {
        TLRPC.Peer peer;
        TLRPC.Chat chat;
        if (getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).clientUserId) || isMegagroup() || ((peer = this.messageOwner.to_id) != null && peer.chat_id != 0)) {
            return true;
        }
        TLRPC.Peer peer2 = this.messageOwner.to_id;
        if (peer2 == null || peer2.channel_id == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id))) == null || !chat.megagroup) {
            return false;
        }
        return true;
    }

    public boolean isFromUser() {
        TLRPC.Message message = this.messageOwner;
        return message.from_id > 0 && !message.post;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.fwd_from;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isForwardedChannelPost() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r1 = r0.from_id
            if (r1 > 0) goto L_0x0010
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x0010
            int r0 = r0.channel_post
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isForwardedChannelPost():boolean");
    }

    public boolean isUnread() {
        return this.messageOwner.unread;
    }

    public boolean isContentUnread() {
        return this.messageOwner.media_unread;
    }

    public void setIsRead() {
        this.messageOwner.unread = false;
    }

    public int getUnradFlags() {
        return getUnreadFlags(this.messageOwner);
    }

    public static int getUnreadFlags(TLRPC.Message message) {
        int i = !message.unread ? 1 : 0;
        return !message.media_unread ? i | 2 : i;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public int getRealId() {
        TLRPC.Message message = this.messageOwner;
        int i = message.realId;
        return i != 0 ? i : message.id;
    }

    public static int getMessageSize(TLRPC.Message message) {
        TLRPC.Document document;
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            document = messageMedia.webpage.document;
        } else if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
            document = messageMedia.game.document;
        } else {
            document = messageMedia != null ? messageMedia.document : null;
        }
        if (document != null) {
            return document.size;
        }
        return 0;
    }

    public int getSize() {
        return getMessageSize(this.messageOwner);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0009, code lost:
        r0 = r0.channel_id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getIdWithChannel() {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r1 = r0.id
            long r1 = (long) r1
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            if (r0 == 0) goto L_0x0012
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0012
            long r3 = (long) r0
            r0 = 32
            long r3 = r3 << r0
            long r1 = r1 | r3
        L_0x0012:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getIdWithChannel():long");
    }

    public int getChannelId() {
        TLRPC.Peer peer = this.messageOwner.to_id;
        if (peer != null) {
            return peer.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(TLRPC.Message message) {
        int i;
        if (!(message instanceof TLRPC.TL_message_secret)) {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        } else if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isVideoMessage(message)) && (i = message.ttl) > 0 && i <= 60) {
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.messageOwner);
    }

    public static boolean isSecretPhotoOrVideo(TLRPC.Message message) {
        int i;
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && (i = message.ttl) > 0 && i <= 60) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public static boolean isSecretMedia(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean needDrawBluredPreview() {
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            int max = Math.max(message.ttl, message.media.ttl_seconds);
            if (max <= 0 || (((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && !isVideo() && !isGif()) || max > 60) && !isRoundVideo())) {
                return false;
            }
            return true;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && this.messageOwner.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean isSecretMedia() {
        int i;
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            if ((((message.media instanceof TLRPC.TL_messageMediaPhoto) || isGif()) && (i = this.messageOwner.ttl) > 0 && i <= 60) || isVoice() || isRoundVideo() || isVideo()) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && this.messageOwner.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public static void setUnreadFlags(TLRPC.Message message, int i) {
        boolean z = false;
        message.unread = (i & 1) == 0;
        if ((i & 2) == 0) {
            z = true;
        }
        message.media_unread = z;
    }

    public static boolean isUnread(TLRPC.Message message) {
        return message.unread;
    }

    public static boolean isContentUnread(TLRPC.Message message) {
        return message.media_unread;
    }

    public boolean isMegagroup() {
        return isMegagroup(this.messageOwner);
    }

    public boolean isSavedFromMegagroup() {
        TLRPC.Peer peer;
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader == null || (peer = messageFwdHeader.saved_from_peer) == null || peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
    }

    public static boolean isMegagroup(TLRPC.Message message) {
        return (message.flags & Integer.MIN_VALUE) != 0;
    }

    public static boolean isOut(TLRPC.Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public boolean canStreamVideo() {
        TLRPC.Document document = getDocument();
        if (document != null && !(document instanceof TLRPC.TL_documentEncrypted)) {
            if (SharedConfig.streamAllVideo) {
                return true;
            }
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    return documentAttribute.supports_streaming;
                }
            }
            if (!SharedConfig.streamMkv || !"video/x-matroska".equals(document.mime_type)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static long getDialogId(TLRPC.Message message) {
        TLRPC.Peer peer;
        if (message.dialog_id == 0 && (peer = message.to_id) != null) {
            int i = peer.chat_id;
            if (i != 0) {
                message.dialog_id = (long) (-i);
            } else {
                int i2 = peer.channel_id;
                if (i2 != 0) {
                    message.dialog_id = (long) (-i2);
                } else if (isOut(message)) {
                    message.dialog_id = (long) message.to_id.user_id;
                } else {
                    message.dialog_id = (long) message.from_id;
                }
            }
        }
        return message.dialog_id;
    }

    public boolean isSending() {
        TLRPC.Message message = this.messageOwner;
        return message.send_state == 1 && message.id < 0;
    }

    public boolean isEditing() {
        TLRPC.Message message = this.messageOwner;
        return message.send_state == 3 && message.id > 0;
    }

    public boolean isSendError() {
        TLRPC.Message message = this.messageOwner;
        if (message.send_state != 2 || message.id >= 0) {
            if (this.scheduled) {
                TLRPC.Message message2 = this.messageOwner;
                if (message2.id <= 0 || message2.date >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - 60) {
                    return false;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isSent() {
        TLRPC.Message message = this.messageOwner;
        return message.send_state == 0 || message.id > 0;
    }

    public int getSecretTimeLeft() {
        TLRPC.Message message = this.messageOwner;
        int i = message.ttl;
        int i2 = message.destroyTime;
        return i2 != 0 ? Math.max(0, i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) : i;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int secretTimeLeft = getSecretTimeLeft();
        if (secretTimeLeft < 60) {
            return secretTimeLeft + "s";
        }
        return (secretTimeLeft / 60) + "m";
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public static boolean isStickerDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                if (document.attributes.get(i) instanceof TLRPC.TL_documentAttributeSticker) {
                    return "image/webp".equals(document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isStickerHasSet(TLRPC.Document document) {
        TLRPC.InputStickerSet inputStickerSet;
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) && (inputStickerSet = documentAttribute.stickerset) != null && !(inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document, boolean z) {
        TLRPC.InputStickerSet inputStickerSet;
        if (document != null && "application/x-tgsticker".equals(document.mime_type) && !document.thumbs.isEmpty()) {
            if (z) {
                return true;
            }
            int size = document.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) && (inputStickerSet = documentAttribute.stickerset) != null && !(inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC.Document document) {
        return isAnimatedStickerDocument(document, true) && SharedConfig.getDevicePerfomanceClass() != 0;
    }

    public static boolean isMaskDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) && documentAttribute.mask) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    return documentAttribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(WebFile webFile) {
        return webFile != null && !isGifDocument(webFile) && webFile.mime_type.startsWith("image/");
    }

    public static boolean isVideoWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    return !documentAttribute.voice;
                }
            }
            if (!TextUtils.isEmpty(document.mime_type)) {
                String lowerCase = document.mime_type.toLowerCase();
                if (lowerCase.equals("audio/flac") || lowerCase.equals("audio/ogg") || lowerCase.equals("audio/opus") || lowerCase.equals("audio/x-opus+ogg") || (lowerCase.equals("application/octet-stream") && FileLoader.getDocumentFileName(document).endsWith(".opus"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVideoDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        boolean z = false;
        int i = 0;
        int i2 = 0;
        boolean z2 = false;
        for (int i3 = 0; i3 < document.attributes.size(); i3++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                if (documentAttribute.round_message) {
                    return false;
                }
                i = documentAttribute.w;
                i2 = documentAttribute.h;
                z2 = true;
            } else if (documentAttribute instanceof TLRPC.TL_documentAttributeAnimated) {
                z = true;
            }
        }
        if (z && (i > 1280 || i2 > 1280)) {
            z = false;
        }
        if (SharedConfig.streamMkv && !z2 && "video/x-matroska".equals(document.mime_type)) {
            z2 = true;
        }
        if (!z2 || z) {
            return false;
        }
        return true;
    }

    public TLRPC.Document getDocument() {
        TLRPC.Document document = this.emojiAnimatedSticker;
        if (document != null) {
            return document;
        }
        return getDocument(this.messageOwner);
    }

    public static TLRPC.Document getDocument(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return messageMedia.webpage.document;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
            return messageMedia.game.document;
        }
        if (messageMedia != null) {
            return messageMedia.document;
        }
        return null;
    }

    public static TLRPC.Photo getPhoto(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return messageMedia.webpage.photo;
        }
        if (messageMedia != null) {
            return messageMedia.photo;
        }
        return null;
    }

    public static boolean isStickerMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        return messageMedia != null && isStickerDocument(messageMedia.document);
    }

    public static boolean isAnimatedStickerMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        return messageMedia != null && isAnimatedStickerDocument(messageMedia.document, DialogObject.isSecretDialogId(message.dialog_id) ^ true);
    }

    public static boolean isLocationMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        return (messageMedia instanceof TLRPC.TL_messageMediaGeo) || (messageMedia instanceof TLRPC.TL_messageMediaGeoLive) || (messageMedia instanceof TLRPC.TL_messageMediaVenue);
    }

    public static boolean isMaskMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        return messageMedia != null && isMaskDocument(messageMedia.document);
    }

    public static boolean isMusicMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return isMusicDocument(messageMedia.webpage.document);
        }
        return messageMedia != null && isMusicDocument(messageMedia.document);
    }

    public static boolean isGifMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return isGifDocument(messageMedia.webpage.document);
        }
        return messageMedia != null && isGifDocument(messageMedia.document);
    }

    public static boolean isRoundVideoMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return isRoundVideoDocument(messageMedia.webpage.document);
        }
        return messageMedia != null && isRoundVideoDocument(messageMedia.document);
    }

    public static boolean isPhoto(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (!(messageMedia instanceof TLRPC.TL_messageMediaWebPage)) {
            return messageMedia instanceof TLRPC.TL_messageMediaPhoto;
        }
        TLRPC.WebPage webPage = messageMedia.webpage;
        return (webPage.photo instanceof TLRPC.TL_photo) && !(webPage.document instanceof TLRPC.TL_document);
    }

    public static boolean isVoiceMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return isVoiceDocument(messageMedia.webpage.document);
        }
        return messageMedia != null && isVoiceDocument(messageMedia.document);
    }

    public static boolean isNewGifMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return isNewGifDocument(messageMedia.webpage.document);
        }
        return messageMedia != null && isNewGifDocument(messageMedia.document);
    }

    public static boolean isLiveLocationMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaWebPage) {
            return isVideoDocument(messageMedia.webpage.document);
        }
        return messageMedia != null && isVideoDocument(messageMedia.document);
    }

    public static boolean isGameMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaInvoice;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message message) {
        TLRPC.Document document;
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia == null || (document = messageMedia.document) == null) {
            return null;
        }
        return getInputStickerSet(document);
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                }
                return inputStickerSet;
            }
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        if (document == null) {
            return -1;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return -1;
                }
                return inputStickerSet.id;
            }
        }
        return -1;
    }

    public String getStrickerChar() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        Iterator<TLRPC.DocumentAttribute> it = document.attributes.iterator();
        while (it.hasNext()) {
            TLRPC.DocumentAttribute next = it.next();
            if (next instanceof TLRPC.TL_documentAttributeSticker) {
                return next.alt;
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5 = this.type;
        int i6 = 0;
        if (i5 == 0) {
            int i7 = this.textHeight;
            TLRPC.MessageMedia messageMedia = this.messageOwner.media;
            if ((messageMedia instanceof TLRPC.TL_messageMediaWebPage) && (messageMedia.webpage instanceof TLRPC.TL_webPage)) {
                i6 = AndroidUtilities.dp(100.0f);
            }
            int i8 = i7 + i6;
            return isReply() ? i8 + AndroidUtilities.dp(42.0f) : i8;
        } else if (i5 == 2) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (i5 == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (i5 == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (i5 == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (i5 == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (i5 == 10) {
                return AndroidUtilities.dp(30.0f);
            }
            if (i5 == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (i5 == 5) {
                return AndroidUtilities.roundMessageSize;
            }
            if (i5 == 13 || i5 == 15) {
                float f = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.getMinTabletSide();
                } else {
                    i = AndroidUtilities.displaySize.x;
                }
                float f2 = ((float) i) * 0.5f;
                TLRPC.Document document = getDocument();
                int size = document.attributes.size();
                int i9 = 0;
                while (true) {
                    if (i9 >= size) {
                        i2 = 0;
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i9);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        i6 = documentAttribute.w;
                        i2 = documentAttribute.h;
                        break;
                    }
                    i9++;
                }
                if (i6 == 0) {
                    i2 = (int) f;
                    i6 = AndroidUtilities.dp(100.0f) + i2;
                }
                float f3 = (float) i2;
                if (f3 > f) {
                    i6 = (int) (((float) i6) * (f / f3));
                    i2 = (int) f;
                }
                float f4 = (float) i6;
                if (f4 > f2) {
                    i2 = (int) (((float) i2) * (f2 / f4));
                }
                return i2 + AndroidUtilities.dp(14.0f);
            }
            if (AndroidUtilities.isTablet()) {
                i3 = AndroidUtilities.getMinTabletSide();
            } else {
                Point point = AndroidUtilities.displaySize;
                i3 = Math.min(point.x, point.y);
            }
            int i10 = (int) (((float) i3) * 0.7f);
            int dp = AndroidUtilities.dp(100.0f) + i10;
            if (i10 > AndroidUtilities.getPhotoSize()) {
                i10 = AndroidUtilities.getPhotoSize();
            }
            if (dp > AndroidUtilities.getPhotoSize()) {
                dp = AndroidUtilities.getPhotoSize();
            }
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (closestPhotoSizeWithSize != null) {
                int i11 = (int) (((float) closestPhotoSizeWithSize.h) / (((float) closestPhotoSizeWithSize.w) / ((float) i10)));
                if (i11 == 0) {
                    i11 = AndroidUtilities.dp(100.0f);
                }
                if (i11 <= dp) {
                    dp = i11 < AndroidUtilities.dp(120.0f) ? AndroidUtilities.dp(120.0f) : i11;
                }
                if (needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        i4 = AndroidUtilities.getMinTabletSide();
                    } else {
                        Point point2 = AndroidUtilities.displaySize;
                        i4 = Math.min(point2.x, point2.y);
                    }
                    dp = (int) (((float) i4) * 0.5f);
                }
            }
            return dp + AndroidUtilities.dp(14.0f);
        }
    }

    public String getStickerEmoji() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                String str = documentAttribute.alt;
                if (str == null || str.length() <= 0) {
                    return null;
                }
                return documentAttribute.alt;
            }
        }
        return null;
    }

    public boolean isAnimatedEmoji() {
        return this.emojiAnimatedSticker != null;
    }

    public boolean isSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 13;
        }
        return isStickerDocument(getDocument());
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 15;
        }
        return isAnimatedStickerDocument(getDocument(), true ^ DialogObject.isSecretDialogId(getDialogId()));
    }

    public boolean isAnyKindOfSticker() {
        int i = this.type;
        return i == 13 || i == 15;
    }

    public boolean shouldDrawWithoutBackground() {
        int i = this.type;
        return i == 13 || i == 15 || i == 5;
    }

    public boolean isLocation() {
        return isLocationMessage(this.messageOwner);
    }

    public boolean isMask() {
        return isMaskMessage(this.messageOwner);
    }

    public boolean isMusic() {
        return isMusicMessage(this.messageOwner);
    }

    public boolean isVoice() {
        return isVoiceMessage(this.messageOwner);
    }

    public boolean isVideo() {
        return isVideoMessage(this.messageOwner);
    }

    public boolean isPhoto() {
        return isPhoto(this.messageOwner);
    }

    public boolean isLiveLocation() {
        return isLiveLocationMessage(this.messageOwner);
    }

    public boolean isGame() {
        return isGameMessage(this.messageOwner);
    }

    public boolean isInvoice() {
        return isInvoiceMessage(this.messageOwner);
    }

    public boolean isRoundVideo() {
        if (this.isRoundVideoCached == 0) {
            this.isRoundVideoCached = (this.type == 5 || isRoundVideoMessage(this.messageOwner)) ? 1 : 2;
        }
        if (this.isRoundVideoCached == 1) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasPhotoStickers() {
        /*
            r1 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0010
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x0010
            boolean r0 = r0.has_stickers
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.hasPhotoStickers():boolean");
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage.document;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isWebpageDocument() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0016
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            if (r0 == 0) goto L_0x0016
            boolean r0 = isGifDocument((org.telegram.tgnet.TLRPC.Document) r0)
            if (r0 != 0) goto L_0x0016
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isWebpageDocument():boolean");
    }

    public boolean isWebpage() {
        return this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        return messageMedia != null && isNewGifDocument(messageMedia.document);
    }

    public boolean isAndroidTheme() {
        TLRPC.WebPage webPage;
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        if (!(messageMedia == null || (webPage = messageMedia.webpage) == null || webPage.attributes.isEmpty())) {
            int size = this.messageOwner.media.webpage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.TL_webPageAttributeTheme tL_webPageAttributeTheme = this.messageOwner.media.webpage.attributes.get(i);
                ArrayList<TLRPC.Document> arrayList = tL_webPageAttributeTheme.documents;
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    if ("application/x-tgtheme-android".equals(arrayList.get(i2).mime_type)) {
                        return true;
                    }
                }
                if (tL_webPageAttributeTheme.settings != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean z) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            int i = 0;
            while (i < document.attributes.size()) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (!documentAttribute.voice) {
                        String str = documentAttribute.title;
                        if (str != null && str.length() != 0) {
                            return str;
                        }
                        String documentFileName = FileLoader.getDocumentFileName(document);
                        return (!TextUtils.isEmpty(documentFileName) || !z) ? documentFileName : LocaleController.getString("AudioUnknownTitle", NUM);
                    } else if (!z) {
                        return null;
                    } else {
                        return LocaleController.formatDateAudio((long) this.messageOwner.date);
                    }
                } else if ((documentAttribute instanceof TLRPC.TL_documentAttributeVideo) && documentAttribute.round_message) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date);
                } else {
                    i++;
                }
            }
            String documentFileName2 = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(documentFileName2)) {
                return documentFileName2;
            }
        }
        return LocaleController.getString("AudioUnknownTitle", NUM);
    }

    public int getDuration() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return 0;
        }
        int i = this.audioPlayerDuration;
        if (i > 0) {
            return i;
        }
        for (int i2 = 0; i2 < document.attributes.size(); i2++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i2);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                return documentAttribute.duration;
            }
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                return documentAttribute.duration;
            }
        }
        return this.audioPlayerDuration;
    }

    public String getArtworkUrl(boolean z) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            int size = document.attributes.size();
            int i = 0;
            while (i < size) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (!(documentAttribute instanceof TLRPC.TL_documentAttributeAudio)) {
                    i++;
                } else if (documentAttribute.voice) {
                    return null;
                } else {
                    String str = documentAttribute.performer;
                    String str2 = documentAttribute.title;
                    if (!TextUtils.isEmpty(str)) {
                        String str3 = str;
                        int i2 = 0;
                        while (true) {
                            String[] strArr = excludeWords;
                            if (i2 >= strArr.length) {
                                break;
                            }
                            str3 = str3.replace(strArr[i2], " ");
                            i2++;
                        }
                        str = str3;
                    }
                    if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
                        return null;
                    }
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append("athumb://itunes.apple.com/search?term=");
                        sb.append(URLEncoder.encode(str + " - " + str2, "UTF-8"));
                        sb.append("&entity=song&limit=4");
                        sb.append(z ? "&s=1" : "");
                        return sb.toString();
                    } catch (Exception unused) {
                    }
                }
            }
        }
        return null;
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003d, code lost:
        if (r5.round_message != false) goto L_0x0026;
     */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x010f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getMusicAuthor(boolean r10) {
        /*
            r9 = this;
            org.telegram.tgnet.TLRPC$Document r0 = r9.getDocument()
            r1 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r2 = "AudioUnknownArtist"
            if (r0 == 0) goto L_0x0113
            r3 = 0
            r4 = 0
        L_0x000d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0113
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio
            r7 = 1
            if (r6 == 0) goto L_0x0037
            boolean r4 = r5.voice
            if (r4 == 0) goto L_0x0028
        L_0x0026:
            r4 = 1
            goto L_0x0040
        L_0x0028:
            java.lang.String r0 = r5.performer
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L_0x0036
            if (r10 == 0) goto L_0x0036
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x0036:
            return r0
        L_0x0037:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo
            if (r6 == 0) goto L_0x0040
            boolean r5 = r5.round_message
            if (r5 == 0) goto L_0x0040
            goto L_0x0026
        L_0x0040:
            if (r4 == 0) goto L_0x010f
            r5 = 0
            if (r10 != 0) goto L_0x0046
            return r5
        L_0x0046:
            boolean r6 = r9.isOutOwner()
            if (r6 != 0) goto L_0x0105
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x0062
            int r6 = r6.from_id
            int r7 = r9.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            int r7 = r7.getClientUserId()
            if (r6 != r7) goto L_0x0062
            goto L_0x0105
        L_0x0062:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x0082
            int r6 = r6.channel_id
            if (r6 == 0) goto L_0x0082
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00f9
        L_0x0082:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x00a4
            int r6 = r6.from_id
            if (r6 == 0) goto L_0x00a4
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            int r7 = r7.from_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
        L_0x00a0:
            r8 = r6
            r6 = r5
            r5 = r8
            goto L_0x00f9
        L_0x00a4:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x00af
            java.lang.String r6 = r6.from_name
            if (r6 == 0) goto L_0x00af
            return r6
        L_0x00af:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            int r7 = r6.from_id
            if (r7 >= 0) goto L_0x00c9
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            int r7 = r7.from_id
            int r7 = -r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00f9
        L_0x00c9:
            if (r7 != 0) goto L_0x00e6
            org.telegram.tgnet.TLRPC$Peer r6 = r6.to_id
            int r6 = r6.channel_id
            if (r6 == 0) goto L_0x00e6
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.to_id
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00f9
        L_0x00e6:
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            int r7 = r7.from_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            goto L_0x00a0
        L_0x00f9:
            if (r5 == 0) goto L_0x0100
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r5)
            return r10
        L_0x0100:
            if (r6 == 0) goto L_0x010f
            java.lang.String r10 = r6.title
            return r10
        L_0x0105:
            r10 = 2131625269(0x7f0e0535, float:1.8877741E38)
            java.lang.String r0 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
            return r10
        L_0x010f:
            int r3 = r3 + 1
            goto L_0x000d
        L_0x0113:
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r2, r1)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMusicAuthor(boolean):java.lang.String");
    }

    public TLRPC.InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
        r1 = (r0 = r0.fwd_from).saved_from_peer;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawForwarded() {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r1 = r0.flags
            r1 = r1 & 4
            if (r1 == 0) goto L_0x002b
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x002b
            org.telegram.tgnet.TLRPC$Peer r1 = r0.saved_from_peer
            if (r1 == 0) goto L_0x0016
            int r1 = r1.channel_id
            int r0 = r0.channel_id
            if (r1 == r0) goto L_0x002b
        L_0x0016:
            int r0 = r5.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.getClientUserId()
            long r0 = (long) r0
            long r2 = r5.getDialogId()
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x002b
            r0 = 1
            goto L_0x002c
        L_0x002b:
            r0 = 0
        L_0x002c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawForwarded():boolean");
    }

    public static boolean isForwardedMessage(TLRPC.Message message) {
        return ((message.flags & 4) == 0 || message.fwd_from == null) ? false : true;
    }

    public boolean isReply() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) {
            TLRPC.Message message = this.messageOwner;
            if (!((message.reply_to_msg_id == 0 && message.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public boolean isMediaEmptyWebpage() {
        return isMediaEmptyWebpage(this.messageOwner);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r1 = r1.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isMediaEmpty(org.telegram.tgnet.TLRPC.Message r1) {
        /*
            if (r1 == 0) goto L_0x0011
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            if (r1 == 0) goto L_0x0011
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty
            if (r0 != 0) goto L_0x0011
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r1 == 0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r1 = 0
            goto L_0x0012
        L_0x0011:
            r1 = 1
        L_0x0012:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isMediaEmpty(org.telegram.tgnet.TLRPC$Message):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r0.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isMediaEmptyWebpage(org.telegram.tgnet.TLRPC.Message r0) {
        /*
            if (r0 == 0) goto L_0x000d
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x000d
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty
            if (r0 == 0) goto L_0x000b
            goto L_0x000d
        L_0x000b:
            r0 = 0
            goto L_0x000e
        L_0x000d:
            r0 = 1
        L_0x000e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isMediaEmptyWebpage(org.telegram.tgnet.TLRPC$Message):boolean");
    }

    public boolean canEditMessage(TLRPC.Chat chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, chat, this.scheduled);
    }

    public boolean canEditMessageScheduleTime(TLRPC.Chat chat) {
        return canEditMessageScheduleTime(this.currentAccount, this.messageOwner, chat);
    }

    public boolean canForwardMessage() {
        return !(this.messageOwner instanceof TLRPC.TL_message_secret) && !needDrawBluredPreview() && !isLiveLocation() && this.type != 16;
    }

    public boolean canEditMedia() {
        if (isSecretMedia()) {
            return false;
        }
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            return true;
        }
        if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument) || isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo()) {
            return false;
        }
        return true;
    }

    public boolean canEditMessageAnytime(TLRPC.Chat chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, chat);
    }

    public static boolean canEditMessageAnytime(int i, TLRPC.Message message, TLRPC.Chat chat) {
        TLRPC.MessageMedia messageMedia;
        TLRPC.MessageAction messageAction;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        if (!(message == null || message.to_id == null || (((messageMedia = message.media) != null && (isRoundVideoDocument(messageMedia.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document, true))) || (((messageAction = message.action) != null && !(messageAction instanceof TLRPC.TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0)))) {
            int i2 = message.from_id;
            if (i2 == message.to_id.user_id && i2 == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(message)) {
                return true;
            }
            if (!(chat == null && message.to_id.channel_id != 0 && (chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(message.to_id.channel_id))) == null) && message.out && chat != null && chat.megagroup && (chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && tL_chatAdminRights.pin_messages))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEditMessageScheduleTime(int i, TLRPC.Message message, TLRPC.Chat chat) {
        if (chat == null && message.to_id.channel_id != 0 && (chat = MessagesController.getInstance(i).getChat(Integer.valueOf(message.to_id.channel_id))) == null) {
            return false;
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || chat.creator) {
            return true;
        }
        TLRPC.TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
        if (tL_chatAdminRights == null || (!tL_chatAdminRights.edit_messages && !message.out)) {
            return false;
        }
        return true;
    }

    public static boolean canEditMessage(int i, TLRPC.Message message, TLRPC.Chat chat, boolean z) {
        TLRPC.MessageMedia messageMedia;
        TLRPC.MessageAction messageAction;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.TL_chatAdminRights tL_chatAdminRights2;
        if (z && message.date < ConnectionsManager.getInstance(i).getCurrentTime() - 60) {
            return false;
        }
        if ((chat == null || (!chat.left && !chat.kicked)) && message != null && message.to_id != null && (((messageMedia = message.media) == null || (!isRoundVideoDocument(messageMedia.document) && !isStickerDocument(message.media.document) && !isAnimatedStickerDocument(message.media.document, true) && !isLocationMessage(message))) && (((messageAction = message.action) == null || (messageAction instanceof TLRPC.TL_messageActionEmpty)) && !isForwardedMessage(message) && message.via_bot_id == 0 && message.id >= 0))) {
            int i2 = message.from_id;
            if (i2 == message.to_id.user_id && i2 == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(message) && !(message.media instanceof TLRPC.TL_messageMediaContact)) {
                return true;
            }
            if (chat == null && message.to_id.channel_id != 0 && (chat = MessagesController.getInstance(i).getChat(Integer.valueOf(message.to_id.channel_id))) == null) {
                return false;
            }
            TLRPC.MessageMedia messageMedia2 = message.media;
            if (messageMedia2 != null && !(messageMedia2 instanceof TLRPC.TL_messageMediaEmpty) && !(messageMedia2 instanceof TLRPC.TL_messageMediaPhoto) && !(messageMedia2 instanceof TLRPC.TL_messageMediaDocument) && !(messageMedia2 instanceof TLRPC.TL_messageMediaWebPage)) {
                return false;
            }
            if (message.out && chat != null && chat.megagroup && (chat.creator || ((tL_chatAdminRights2 = chat.admin_rights) != null && tL_chatAdminRights2.pin_messages))) {
                return true;
            }
            if (!z && Math.abs(message.date - ConnectionsManager.getInstance(i).getCurrentTime()) > MessagesController.getInstance(i).maxEditTime) {
                return false;
            }
            if (message.to_id.channel_id == 0) {
                if (!message.out && message.from_id != UserConfig.getInstance(i).getClientUserId()) {
                    return false;
                }
                TLRPC.MessageMedia messageMedia3 = message.media;
                if (!(messageMedia3 instanceof TLRPC.TL_messageMediaPhoto) && (!(messageMedia3 instanceof TLRPC.TL_messageMediaDocument) || isStickerMessage(message) || isAnimatedStickerMessage(message))) {
                    TLRPC.MessageMedia messageMedia4 = message.media;
                    if ((messageMedia4 instanceof TLRPC.TL_messageMediaEmpty) || (messageMedia4 instanceof TLRPC.TL_messageMediaWebPage) || messageMedia4 == null) {
                        return true;
                    }
                    return false;
                }
                return true;
            } else if ((chat.megagroup && message.out) || (!chat.megagroup && ((chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && (tL_chatAdminRights.edit_messages || (message.out && tL_chatAdminRights.post_messages)))) && message.post))) {
                TLRPC.MessageMedia messageMedia5 = message.media;
                if (!(messageMedia5 instanceof TLRPC.TL_messageMediaPhoto) && (!(messageMedia5 instanceof TLRPC.TL_messageMediaDocument) || isStickerMessage(message) || isAnimatedStickerMessage(message))) {
                    TLRPC.MessageMedia messageMedia6 = message.media;
                    if ((messageMedia6 instanceof TLRPC.TL_messageMediaEmpty) || (messageMedia6 instanceof TLRPC.TL_messageMediaWebPage) || messageMedia6 == null) {
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean canDeleteMessage(boolean z, TLRPC.Chat chat) {
        return this.eventId == 0 && canDeleteMessage(this.currentAccount, z, this.messageOwner, chat);
    }

    public static boolean canDeleteMessage(int i, boolean z, TLRPC.Message message, TLRPC.Chat chat) {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance(i).getChat(Integer.valueOf(message.to_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            if (!z || chat.megagroup) {
                if (!z) {
                    if (message.id == 1) {
                        return false;
                    }
                    if (chat.creator || (((tL_chatAdminRights = chat.admin_rights) != null && (tL_chatAdminRights.delete_messages || (message.out && (chat.megagroup || tL_chatAdminRights.post_messages)))) || (chat.megagroup && message.out && message.from_id > 0))) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
            if (!chat.creator) {
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = chat.admin_rights;
                if (tL_chatAdminRights2 == null) {
                    return false;
                }
                if (tL_chatAdminRights2.delete_messages || message.out) {
                    return true;
                }
                return false;
            }
            return true;
        } else if (z || isOut(message) || !ChatObject.isChannel(chat)) {
            return true;
        } else {
            return false;
        }
    }

    public String getForwardedName() {
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader == null) {
            return null;
        }
        if (messageFwdHeader.channel_id != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
            if (chat != null) {
                return chat.title;
            }
            return null;
        } else if (messageFwdHeader.from_id != 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
            if (user != null) {
                return UserObject.getUserName(user);
            }
            return null;
        } else {
            String str = messageFwdHeader.from_name;
            if (str != null) {
                return str;
            }
            return null;
        }
    }

    public int getFromId() {
        TLRPC.Peer peer;
        int i;
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader == null || (peer = messageFwdHeader.saved_from_peer) == null) {
            TLRPC.Message message = this.messageOwner;
            int i2 = message.from_id;
            if (i2 != 0) {
                return i2;
            }
            if (message.post) {
                return message.to_id.channel_id;
            }
            return 0;
        }
        int i3 = peer.user_id;
        if (i3 != 0) {
            int i4 = messageFwdHeader.from_id;
            return i4 != 0 ? i4 : i3;
        } else if (peer.channel_id == 0) {
            int i5 = peer.chat_id;
            if (i5 == 0) {
                return 0;
            }
            int i6 = messageFwdHeader.from_id;
            if (i6 != 0) {
                return i6;
            }
            int i7 = messageFwdHeader.channel_id;
            return i7 != 0 ? -i7 : -i5;
        } else if (isSavedFromMegagroup() && (i = this.messageOwner.fwd_from.from_id) != 0) {
            return i;
        } else {
            TLRPC.MessageFwdHeader messageFwdHeader2 = this.messageOwner.fwd_from;
            int i8 = messageFwdHeader2.channel_id;
            if (i8 != 0) {
                return -i8;
            }
            return -messageFwdHeader2.saved_from_peer.channel_id;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isWallpaper() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0018
            java.lang.String r0 = r0.type
            java.lang.String r1 = "telegram_background"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0018
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isWallpaper():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isTheme() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0018
            java.lang.String r0 = r0.type
            java.lang.String r1 = "telegram_theme"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0018
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isTheme():boolean");
    }

    public int getMediaExistanceFlags() {
        int i = this.attachPathExists ? 1 : 0;
        return this.mediaExists ? i | 2 : i;
    }

    public void applyMediaExistanceFlags(int i) {
        if (i == -1) {
            checkMediaExistance();
            return;
        }
        boolean z = false;
        this.attachPathExists = (i & 1) != 0;
        if ((i & 2) != 0) {
            z = true;
        }
        this.mediaExists = z;
    }

    public void checkMediaExistance() {
        int i;
        boolean z = false;
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type == 1 && FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
            File pathToMessage = FileLoader.getPathToMessage(this.messageOwner);
            if (needDrawBluredPreview()) {
                this.mediaExists = new File(pathToMessage.getAbsolutePath() + ".enc").exists();
            }
            if (!this.mediaExists) {
                this.mediaExists = pathToMessage.exists();
            }
        }
        if ((!this.mediaExists && this.type == 8) || (i = this.type) == 3 || i == 9 || i == 2 || i == 14 || i == 5) {
            String str = this.messageOwner.attachPath;
            if (str != null && str.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                File pathToMessage2 = FileLoader.getPathToMessage(this.messageOwner);
                if (this.type == 3 && needDrawBluredPreview()) {
                    this.mediaExists = new File(pathToMessage2.getAbsolutePath() + ".enc").exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = pathToMessage2.exists();
                }
            }
        }
        if (!this.mediaExists) {
            TLRPC.Document document = getDocument();
            if (document != null) {
                if (isWallpaper()) {
                    this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
                } else {
                    this.mediaExists = FileLoader.getPathToAttach(document).exists();
                }
            } else if (this.type == 0) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    if (closestPhotoSizeWithSize != null) {
                        this.mediaExists = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
                    }
                } else {
                    return;
                }
            }
            if (!this.mediaExists && document != null) {
                if (isVideoDocument(document) && shouldEncryptPhotoOrVideo()) {
                    z = true;
                }
                this.loadedFileSize = FileLoader.getTempFileSize(document, z);
            }
        }
    }

    public boolean equals(MessageObject messageObject) {
        return getId() == messageObject.getId() && getDialogId() == messageObject.getDialogId();
    }
}
