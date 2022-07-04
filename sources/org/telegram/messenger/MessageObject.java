package org.telegram.messenger;

import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.View;
import androidx.collection.LongSparseArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

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
    public static final int TYPE_GEO = 4;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_VIDEO = 3;
    static final String[] excludeWords = {" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
    public static Pattern instagramUrlPattern;
    public static Pattern urlPattern;
    public static Pattern videoTimeUrlPattern;
    public boolean animateComments;
    public boolean attachPathExists;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressMs;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public String botStartParam;
    public float bufferedProgress;
    public Boolean cachedIsSupergroup;
    public boolean cancelEditing;
    public CharSequence caption;
    public ArrayList<TLRPC.TL_pollAnswer> checkedVotes;
    public int contentType;
    public int currentAccount;
    public TLRPC.TL_channelAdminLogEvent currentEvent;
    public Drawable customAvatarDrawable;
    public String customName;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public CharSequence editingMessage;
    public ArrayList<TLRPC.MessageEntity> editingMessageEntities;
    public boolean editingMessageSearchWebPage;
    public TLRPC.Document emojiAnimatedSticker;
    public String emojiAnimatedStickerColor;
    private int emojiOnlyCount;
    public long eventId;
    public boolean forcePlayEffect;
    public float forceSeekTo;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasRtl;
    public boolean hideSendersName;
    public ArrayList<String> highlightedWords;
    public boolean isDateObject;
    public boolean isDownloadingFile;
    public boolean isReactionPush;
    public boolean isRestrictedMessage;
    private int isRoundVideoCached;
    public boolean isSpoilersRevealed;
    public int lastLineWidth;
    private boolean layoutCreated;
    public int linesCount;
    public CharSequence linkDescription;
    public long loadedFileSize;
    public boolean loadingCancelled;
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public boolean localSupergroup;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public ImageLocation mediaSmallThumb;
    public ImageLocation mediaThumb;
    public TLRPC.Message messageOwner;
    public CharSequence messageText;
    public String messageTrimmedToHighlight;
    public String monthKey;
    public int parentWidth;
    public SvgHelper.SvgDrawable pathThumb;
    public ArrayList<TLRPC.PhotoSize> photoThumbs;
    public ArrayList<TLRPC.PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public boolean preview;
    public String previousAttachPath;
    public TLRPC.MessageMedia previousMedia;
    public String previousMessage;
    public ArrayList<TLRPC.MessageEntity> previousMessageEntities;
    public boolean putInDownloadsStore;
    public boolean reactionsChanged;
    public long reactionsLastCheckTime;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public boolean scheduled;
    public SendAnimationData sendAnimationData;
    public TLRPC.Peer sendAsPeer;
    public boolean shouldRemoveVideoEditedInfo;
    public int sponsoredChannelPost;
    public TLRPC.ChatInvite sponsoredChatInvite;
    public String sponsoredChatInviteHash;
    public byte[] sponsoredId;
    public int stableId;
    public BitmapDrawable strippedThumb;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public float textXOffset;
    public int type;
    public boolean useCustomPhoto;
    public CharSequence vCardData;
    public VideoEditedInfo videoEditedInfo;
    public AtomicReference<WeakReference<View>> viewRef;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;
    public boolean wasJustSent;
    public boolean wasUnread;

    public static class SendAnimationData {
        public float currentScale;
        public float currentX;
        public float currentY;
        public float height;
        public float timeAlpha;
        public float width;
        public float x;
        public float y;
    }

    public static boolean hasUnreadReactions(TLRPC.Message message) {
        if (message == null) {
            return false;
        }
        return hasUnreadReactions(message.reactions);
    }

    public static boolean hasUnreadReactions(TLRPC.TL_messageReactions reactions) {
        if (reactions == null) {
            return false;
        }
        for (int i = 0; i < reactions.recent_reactions.size(); i++) {
            if (((TLRPC.TL_messagePeerReaction) reactions.recent_reactions.get(i)).unread) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPremiumSticker(TLRPC.Document document) {
        if (document == null || document.thumbs == null) {
            return false;
        }
        for (int i = 0; i < document.video_thumbs.size(); i++) {
            if ("f".equals(document.video_thumbs.get(i).type)) {
                return true;
            }
        }
        return false;
    }

    public int getEmojiOnlyCount() {
        return this.emojiOnlyCount;
    }

    public boolean shouldDrawReactionsInLayout() {
        return getDialogId() < 0;
    }

    public TLRPC.TL_messagePeerReaction getRandomUnreadReaction() {
        if (this.messageOwner.reactions == null || this.messageOwner.reactions.recent_reactions == null || this.messageOwner.reactions.recent_reactions.isEmpty()) {
            return null;
        }
        return (TLRPC.TL_messagePeerReaction) this.messageOwner.reactions.recent_reactions.get(0);
    }

    public void markReactionsAsRead() {
        if (this.messageOwner.reactions != null && this.messageOwner.reactions.recent_reactions != null) {
            boolean changed = false;
            for (int i = 0; i < this.messageOwner.reactions.recent_reactions.size(); i++) {
                if (((TLRPC.TL_messagePeerReaction) this.messageOwner.reactions.recent_reactions.get(i)).unread) {
                    ((TLRPC.TL_messagePeerReaction) this.messageOwner.reactions.recent_reactions.get(i)).unread = false;
                    changed = true;
                }
            }
            if (changed) {
                MessagesStorage.getInstance(this.currentAccount).markMessageReactionsAsRead(this.messageOwner.dialog_id, this.messageOwner.id, true);
            }
        }
    }

    public boolean isPremiumSticker() {
        if (this.messageOwner.media == null || !this.messageOwner.media.nopremium) {
            return isPremiumSticker(getDocument());
        }
        return false;
    }

    public TLRPC.VideoSize getPremiumStickerAnimation() {
        return getPremiumStickerAnimation(getDocument());
    }

    public static TLRPC.VideoSize getPremiumStickerAnimation(TLRPC.Document document) {
        if (document == null || document.thumbs == null) {
            return null;
        }
        for (int i = 0; i < document.video_thumbs.size(); i++) {
            if ("f".equals(document.video_thumbs.get(i).type)) {
                return document.video_thumbs.get(i);
            }
        }
        return null;
    }

    public static class VCardData {
        private String company;
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();

        public static CharSequence parse(String data) {
            boolean finished;
            VCardData currentData;
            boolean finished2;
            byte[] bytes;
            try {
                BufferedReader bufferedReader = new BufferedReader(new StringReader(data));
                String pendingLine = null;
                finished = false;
                currentData = null;
                while (true) {
                    String readLine = bufferedReader.readLine();
                    String line = readLine;
                    String originalLine = readLine;
                    if (readLine == null) {
                        break;
                    } else if (!originalLine.startsWith("PHOTO")) {
                        if (originalLine.indexOf(58) >= 0) {
                            if (originalLine.startsWith("BEGIN:VCARD")) {
                                currentData = new VCardData();
                            } else if (originalLine.startsWith("END:VCARD") && currentData != null) {
                                finished = true;
                            }
                        }
                        if (pendingLine != null) {
                            line = pendingLine + line;
                            pendingLine = null;
                        }
                        int i = 0;
                        if (line.contains("=QUOTED-PRINTABLE")) {
                            if (line.endsWith("=")) {
                                pendingLine = line.substring(0, line.length() - 1);
                            }
                        }
                        int idx = line.indexOf(":");
                        String[] args = idx >= 0 ? new String[]{line.substring(0, idx), line.substring(idx + 1).trim()} : new String[]{line.trim()};
                        if (args.length < 2) {
                            int i2 = idx;
                            finished2 = finished;
                        } else if (currentData == null) {
                            finished2 = finished;
                        } else if (args[0].startsWith("ORG")) {
                            String nameEncoding = null;
                            String nameCharset = null;
                            String[] params = args[0].split(";");
                            int length = params.length;
                            while (i < length) {
                                int idx2 = idx;
                                String[] args2 = params[i].split("=");
                                boolean args22 = finished;
                                if (args2.length == 2) {
                                    if (args2[0].equals("CHARSET")) {
                                        nameCharset = args2[1];
                                    } else if (args2[0].equals("ENCODING")) {
                                        nameEncoding = args2[1];
                                    }
                                }
                                i++;
                                String str = data;
                                idx = idx2;
                                finished = args22;
                            }
                            finished2 = finished;
                            currentData.company = args[1];
                            if (!(nameEncoding == null || !nameEncoding.equalsIgnoreCase("QUOTED-PRINTABLE") || (bytes = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(currentData.company))) == null || bytes.length == 0)) {
                                currentData.company = new String(bytes, nameCharset);
                            }
                            currentData.company = currentData.company.replace(';', ' ');
                        } else {
                            finished2 = finished;
                            if (args[0].startsWith("TEL")) {
                                if (args[1].length() > 0) {
                                    currentData.phones.add(args[1]);
                                }
                            } else if (args[0].startsWith("EMAIL")) {
                                String email = args[1];
                                if (email.length() > 0) {
                                    currentData.emails.add(email);
                                }
                            }
                        }
                        String str2 = data;
                        finished = finished2;
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable th) {
                return null;
            }
            if (!finished) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            for (int a = 0; a < currentData.phones.size(); a++) {
                if (result.length() > 0) {
                    result.append(10);
                }
                String phone = currentData.phones.get(a);
                if (!phone.contains("#")) {
                    if (!phone.contains("*")) {
                        result.append(PhoneFormat.getInstance().format(phone));
                    }
                }
                result.append(phone);
            }
            for (int a2 = 0; a2 < currentData.emails.size(); a2++) {
                if (result.length() > 0) {
                    result.append(10);
                }
                result.append(PhoneFormat.getInstance().format(currentData.emails.get(a2)));
            }
            if (!TextUtils.isEmpty(currentData.company)) {
                if (result.length() > 0) {
                    result.append(10);
                }
                result.append(currentData.company);
            }
            return result;
        }
    }

    public static class TextLayoutBlock {
        public static final int FLAG_NOT_RTL = 2;
        public static final int FLAG_RTL = 1;
        public int charactersEnd;
        public int charactersOffset;
        public byte directionFlags;
        public int height;
        public int heightByOffset;
        public List<SpoilerEffect> spoilers = new ArrayList();
        public AtomicReference<Layout> spoilersPatchedTextLayout = new AtomicReference<>();
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
        public float left;
        public int leftSpanOffset;
        public byte maxX;
        public byte maxY;
        public byte minX;
        public byte minY;
        public float ph;
        public int pw;
        public float[] siblingHeights;
        public int spanSize;
        public float top;

        public void set(int minX2, int maxX2, int minY2, int maxY2, int w, float h, int flags2) {
            this.minX = (byte) minX2;
            this.maxX = (byte) maxX2;
            this.minY = (byte) minY2;
            this.maxY = (byte) maxY2;
            this.pw = w;
            this.spanSize = w;
            this.ph = h;
            this.flags = (byte) flags2;
        }
    }

    public static class GroupedMessages {
        public long groupId;
        public boolean hasCaption;
        public boolean hasSibling;
        public boolean isDocuments;
        private int maxSizeWidth = 800;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap<>();
        public final TransitionParams transitionParams = new TransitionParams();

        private static class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                this.lineCounts = new int[]{i1, i2};
                this.heights = new float[]{f1, f2};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                this.lineCounts = new int[]{i1, i2, i3};
                this.heights = new float[]{f1, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i1, i2, i3, i4};
                this.heights = new float[]{f1, f2, f3, f4};
            }
        }

        private float multiHeight(float[] array, int start, int end) {
            float sum = 0.0f;
            for (int a = start; a < end; a++) {
                sum += array[a];
            }
            return ((float) this.maxSizeWidth) / sum;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v76, resolved type: byte} */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0083, code lost:
            if ((r12.messageOwner.media instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) == false) goto L_0x0089;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:246:0x08f3  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r51 = this;
                r0 = r51
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.posArray
                r1.clear()
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.positions
                r1.clear()
                r1 = 800(0x320, float:1.121E-42)
                r0.maxSizeWidth = r1
                r1 = 200(0xc8, float:2.8E-43)
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.messages
                int r2 = r2.size()
                r3 = 1
                if (r2 > r3) goto L_0x001c
                return
            L_0x001c:
                r4 = 1145798656(0x444b8000, float:814.0)
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r6 = 1065353216(0x3var_, float:1.0)
                r7 = 0
                r8 = 0
                r9 = 0
                r10 = 0
                r11 = 0
                r12 = 0
                r0.hasSibling = r12
                r0.hasCaption = r12
                r13 = 0
            L_0x0031:
                if (r13 >= r2) goto L_0x010b
                java.util.ArrayList<org.telegram.messenger.MessageObject> r12 = r0.messages
                java.lang.Object r12 = r12.get(r13)
                org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
                if (r13 != 0) goto L_0x009b
                boolean r7 = r12.isOutOwner()
                if (r7 != 0) goto L_0x0087
                org.telegram.tgnet.TLRPC$Message r14 = r12.messageOwner
                org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
                if (r14 == 0) goto L_0x0055
                org.telegram.tgnet.TLRPC$Message r14 = r12.messageOwner
                org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
                org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
                if (r14 != 0) goto L_0x0052
                goto L_0x0055
            L_0x0052:
                r20 = r4
                goto L_0x0085
            L_0x0055:
                org.telegram.tgnet.TLRPC$Message r14 = r12.messageOwner
                org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
                boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_peerUser
                if (r14 == 0) goto L_0x0087
                org.telegram.tgnet.TLRPC$Message r14 = r12.messageOwner
                org.telegram.tgnet.TLRPC$Peer r14 = r14.peer_id
                r20 = r4
                long r3 = r14.channel_id
                r21 = 0
                int r14 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
                if (r14 != 0) goto L_0x0085
                org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
                long r3 = r3.chat_id
                int r14 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
                if (r14 != 0) goto L_0x0085
                org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
                if (r3 != 0) goto L_0x0085
                org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
                if (r3 == 0) goto L_0x0089
            L_0x0085:
                r3 = 1
                goto L_0x008a
            L_0x0087:
                r20 = r4
            L_0x0089:
                r3 = 0
            L_0x008a:
                boolean r4 = r12.isMusic()
                if (r4 != 0) goto L_0x0096
                boolean r4 = r12.isDocument()
                if (r4 == 0) goto L_0x0099
            L_0x0096:
                r4 = 1
                r0.isDocuments = r4
            L_0x0099:
                r10 = r3
                goto L_0x009d
            L_0x009b:
                r20 = r4
            L_0x009d:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r12.photoThumbs
                int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
                org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r4.<init>()
                int r14 = r2 + -1
                if (r13 != r14) goto L_0x00b2
                r14 = 1
                goto L_0x00b3
            L_0x00b2:
                r14 = 0
            L_0x00b3:
                r4.last = r14
                if (r3 != 0) goto L_0x00ba
                r14 = 1065353216(0x3var_, float:1.0)
                goto L_0x00c1
            L_0x00ba:
                int r14 = r3.w
                float r14 = (float) r14
                int r15 = r3.h
                float r15 = (float) r15
                float r14 = r14 / r15
            L_0x00c1:
                r4.aspectRatio = r14
                float r14 = r4.aspectRatio
                r15 = 1067030938(0x3var_a, float:1.2)
                int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
                if (r14 <= 0) goto L_0x00d2
                java.lang.String r14 = "w"
                r5.append(r14)
                goto L_0x00e6
            L_0x00d2:
                float r14 = r4.aspectRatio
                r15 = 1061997773(0x3f4ccccd, float:0.8)
                int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
                if (r14 >= 0) goto L_0x00e1
                java.lang.String r14 = "n"
                r5.append(r14)
                goto L_0x00e6
            L_0x00e1:
                java.lang.String r14 = "q"
                r5.append(r14)
            L_0x00e6:
                float r14 = r4.aspectRatio
                float r6 = r6 + r14
                float r14 = r4.aspectRatio
                r15 = 1073741824(0x40000000, float:2.0)
                int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
                if (r14 <= 0) goto L_0x00f2
                r9 = 1
            L_0x00f2:
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r0.positions
                r14.put(r12, r4)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r0.posArray
                r14.add(r4)
                java.lang.CharSequence r14 = r12.caption
                if (r14 == 0) goto L_0x0103
                r14 = 1
                r0.hasCaption = r14
            L_0x0103:
                int r13 = r13 + 1
                r4 = r20
                r3 = 1
                r12 = 0
                goto L_0x0031
            L_0x010b:
                r20 = r4
                boolean r3 = r0.isDocuments
                r12 = 1000(0x3e8, float:1.401E-42)
                r13 = 4
                r14 = 3
                if (r3 == 0) goto L_0x015c
                r3 = 0
            L_0x0116:
                if (r3 >= r2) goto L_0x015b
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r0.posArray
                java.lang.Object r15 = r15.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r15 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r15
                int r4 = r15.flags
                r4 = r4 | r14
                r15.flags = r4
                if (r3 != 0) goto L_0x012e
                int r4 = r15.flags
                r4 = r4 | r13
                r15.flags = r4
                r4 = 1
                goto L_0x013d
            L_0x012e:
                int r4 = r2 + -1
                if (r3 != r4) goto L_0x013c
                int r4 = r15.flags
                r4 = r4 | 8
                r15.flags = r4
                r4 = 1
                r15.last = r4
                goto L_0x013d
            L_0x013c:
                r4 = 1
            L_0x013d:
                r15.edge = r4
                r4 = 1065353216(0x3var_, float:1.0)
                r15.aspectRatio = r4
                r4 = 0
                r15.minX = r4
                r15.maxX = r4
                byte r4 = (byte) r3
                r15.minY = r4
                byte r4 = (byte) r3
                r15.maxY = r4
                r15.spanSize = r12
                int r4 = r0.maxSizeWidth
                r15.pw = r4
                r4 = 1120403456(0x42CLASSNAME, float:100.0)
                r15.ph = r4
                int r3 = r3 + 1
                goto L_0x0116
            L_0x015b:
                return
            L_0x015c:
                if (r10 == 0) goto L_0x0166
                int r3 = r0.maxSizeWidth
                int r3 = r3 + -50
                r0.maxSizeWidth = r3
                int r1 = r1 + 50
            L_0x0166:
                r3 = 1123024896(0x42var_, float:120.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r15 = r15.x
                android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
                int r12 = r12.y
                int r12 = java.lang.Math.min(r15, r12)
                float r12 = (float) r12
                int r15 = r0.maxSizeWidth
                float r15 = (float) r15
                float r12 = r12 / r15
                float r3 = r3 / r12
                int r3 = (int) r3
                r12 = 1109393408(0x42200000, float:40.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                float r12 = (float) r12
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r15 = r15.x
                android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
                int r13 = r13.y
                int r13 = java.lang.Math.min(r15, r13)
                float r13 = (float) r13
                int r15 = r0.maxSizeWidth
                float r14 = (float) r15
                float r13 = r13 / r14
                float r12 = r12 / r13
                int r12 = (int) r12
                float r13 = (float) r15
                float r13 = r13 / r20
                float r14 = (float) r2
                float r6 = r6 / r14
                r14 = 1120403456(0x42CLASSNAME, float:100.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r14 = (float) r14
                float r14 = r14 / r20
                r15 = 2
                if (r9 != 0) goto L_0x065d
                if (r2 == r15) goto L_0x01d1
                r15 = 3
                if (r2 == r15) goto L_0x01d1
                r15 = 4
                if (r2 != r15) goto L_0x01b8
                goto L_0x01d1
            L_0x01b8:
                r37 = r2
                r40 = r5
                r39 = r6
                r38 = r7
                r25 = r8
                r34 = r9
                r35 = r10
                r36 = r11
                r18 = r13
                r11 = r20
                r20 = r1
                r13 = r12
                goto L_0x0674
            L_0x01d1:
                r15 = 2
                if (r2 != r15) goto L_0x0333
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r0.posArray
                r25 = r8
                r8 = 0
                java.lang.Object r8 = r15.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r0.posArray
                r34 = r9
                r9 = 1
                java.lang.Object r15 = r15.get(r9)
                r9 = r15
                org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9
                java.lang.String r15 = r5.toString()
                r35 = r10
                java.lang.String r10 = "ww"
                boolean r16 = r15.equals(r10)
                if (r16 == 0) goto L_0x0269
                r36 = r11
                r37 = r12
                double r11 = (double) r6
                r23 = 4608983858650965606(0x3ffNUM, double:1.4)
                r39 = r6
                r38 = r7
                double r6 = (double) r13
                java.lang.Double.isNaN(r6)
                double r6 = r6 * r23
                int r16 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
                if (r16 <= 0) goto L_0x0271
                float r6 = r8.aspectRatio
                float r7 = r9.aspectRatio
                float r6 = r6 - r7
                double r6 = (double) r6
                r11 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r16 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
                if (r16 >= 0) goto L_0x0271
                int r6 = r0.maxSizeWidth
                float r6 = (float) r6
                float r7 = r8.aspectRatio
                float r6 = r6 / r7
                int r7 = r0.maxSizeWidth
                float r7 = (float) r7
                float r10 = r9.aspectRatio
                float r7 = r7 / r10
                r10 = 1073741824(0x40000000, float:2.0)
                float r10 = r20 / r10
                float r7 = java.lang.Math.min(r7, r10)
                float r6 = java.lang.Math.min(r6, r7)
                int r6 = java.lang.Math.round(r6)
                float r6 = (float) r6
                float r6 = r6 / r20
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 0
                int r7 = r0.maxSizeWidth
                r33 = 7
                r26 = r8
                r31 = r7
                r32 = r6
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r29 = 1
                r30 = 1
                int r7 = r0.maxSizeWidth
                r33 = 11
                r26 = r9
                r31 = r7
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r11 = r20
                r8 = r25
                goto L_0x0324
            L_0x0269:
                r39 = r6
                r38 = r7
                r36 = r11
                r37 = r12
            L_0x0271:
                boolean r6 = r15.equals(r10)
                if (r6 != 0) goto L_0x02e8
                java.lang.String r6 = "qq"
                boolean r6 = r15.equals(r6)
                if (r6 == 0) goto L_0x0282
                r11 = r20
                goto L_0x02ea
            L_0x0282:
                int r6 = r0.maxSizeWidth
                float r7 = (float) r6
                r10 = 1053609165(0x3ecccccd, float:0.4)
                float r7 = r7 * r10
                float r6 = (float) r6
                float r10 = r8.aspectRatio
                float r6 = r6 / r10
                float r10 = r8.aspectRatio
                r11 = 1065353216(0x3var_, float:1.0)
                float r12 = r11 / r10
                float r10 = r9.aspectRatio
                float r10 = r11 / r10
                float r12 = r12 + r10
                float r6 = r6 / r12
                int r6 = java.lang.Math.round(r6)
                float r6 = (float) r6
                float r6 = java.lang.Math.max(r7, r6)
                int r6 = (int) r6
                int r7 = r0.maxSizeWidth
                int r7 = r7 - r6
                if (r7 >= r3) goto L_0x02ad
                int r10 = r3 - r7
                r7 = r3
                int r6 = r6 - r10
            L_0x02ad:
                float r10 = (float) r7
                float r11 = r8.aspectRatio
                float r10 = r10 / r11
                float r11 = (float) r6
                float r12 = r9.aspectRatio
                float r11 = r11 / r12
                float r10 = java.lang.Math.min(r10, r11)
                int r10 = java.lang.Math.round(r10)
                float r10 = (float) r10
                r11 = r20
                float r10 = java.lang.Math.min(r11, r10)
                float r10 = r10 / r11
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 0
                r33 = 13
                r26 = r8
                r31 = r7
                r32 = r10
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 1
                r28 = 1
                r33 = 14
                r26 = r9
                r31 = r6
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r12 = 1
                r8 = r12
                goto L_0x0324
            L_0x02e8:
                r11 = r20
            L_0x02ea:
                int r6 = r0.maxSizeWidth
                r7 = 2
                int r6 = r6 / r7
                float r7 = (float) r6
                float r10 = r8.aspectRatio
                float r7 = r7 / r10
                float r10 = (float) r6
                float r12 = r9.aspectRatio
                float r10 = r10 / r12
                float r10 = java.lang.Math.min(r10, r11)
                float r7 = java.lang.Math.min(r7, r10)
                int r7 = java.lang.Math.round(r7)
                float r7 = (float) r7
                float r7 = r7 / r11
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 0
                r33 = 13
                r26 = r8
                r31 = r6
                r32 = r7
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 1
                r28 = 1
                r33 = 14
                r26 = r9
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r6 = 1
                r8 = r6
            L_0x0324:
                r20 = r1
                r21 = r3
                r26 = r4
                r40 = r5
                r18 = r13
                r13 = r37
                r5 = r2
                goto L_0x08ee
            L_0x0333:
                r39 = r6
                r38 = r7
                r25 = r8
                r34 = r9
                r35 = r10
                r36 = r11
                r37 = r12
                r11 = r20
                r6 = 1059648963(0x3var_f5c3, float:0.66)
                r7 = 3
                if (r2 != r7) goto L_0x049b
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r7 = r0.posArray
                r8 = 0
                java.lang.Object r7 = r7.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r7
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r9 = r0.posArray
                r10 = 1
                java.lang.Object r9 = r9.get(r10)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r10 = r0.posArray
                r12 = 2
                java.lang.Object r10 = r10.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r10 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r10
                char r12 = r5.charAt(r8)
                r8 = 110(0x6e, float:1.54E-43)
                if (r12 != r8) goto L_0x0422
                r6 = 1056964608(0x3var_, float:0.5)
                float r8 = r11 * r6
                float r12 = r9.aspectRatio
                int r15 = r0.maxSizeWidth
                float r15 = (float) r15
                float r12 = r12 * r15
                float r15 = r10.aspectRatio
                float r6 = r9.aspectRatio
                float r15 = r15 + r6
                float r12 = r12 / r15
                int r6 = java.lang.Math.round(r12)
                float r6 = (float) r6
                float r6 = java.lang.Math.min(r8, r6)
                float r8 = r11 - r6
                float r12 = (float) r3
                int r15 = r0.maxSizeWidth
                float r15 = (float) r15
                r17 = 1056964608(0x3var_, float:0.5)
                float r15 = r15 * r17
                r18 = r13
                float r13 = r10.aspectRatio
                float r13 = r13 * r6
                r20 = r1
                float r1 = r9.aspectRatio
                float r1 = r1 * r8
                float r1 = java.lang.Math.min(r13, r1)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = java.lang.Math.min(r15, r1)
                float r1 = java.lang.Math.max(r12, r1)
                int r1 = (int) r1
                float r12 = r7.aspectRatio
                float r12 = r12 * r11
                r13 = r37
                float r15 = (float) r13
                float r12 = r12 + r15
                int r15 = r0.maxSizeWidth
                int r15 = r15 - r1
                float r15 = (float) r15
                float r12 = java.lang.Math.min(r12, r15)
                int r12 = java.lang.Math.round(r12)
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 1
                r32 = 1065353216(0x3var_, float:1.0)
                r33 = 13
                r26 = r7
                r31 = r12
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 1
                r28 = 1
                r30 = 0
                float r32 = r8 / r11
                r33 = 6
                r26 = r9
                r31 = r1
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 0
                r29 = 1
                r30 = 1
                float r32 = r6 / r11
                r33 = 10
                r26 = r10
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                int r15 = r0.maxSizeWidth
                r10.spanSize = r15
                r37 = r2
                r15 = 2
                float[] r2 = new float[r15]
                float r15 = r6 / r11
                r16 = 0
                r2[r16] = r15
                float r15 = r8 / r11
                r16 = 1
                r2[r16] = r15
                r7.siblingHeights = r2
                if (r38 == 0) goto L_0x0414
                int r2 = r0.maxSizeWidth
                int r2 = r2 - r1
                r7.spanSize = r2
                goto L_0x041b
            L_0x0414:
                int r2 = r0.maxSizeWidth
                int r2 = r2 - r12
                r9.spanSize = r2
                r10.leftSpanOffset = r12
            L_0x041b:
                r2 = 1
                r0.hasSibling = r2
                r1 = 1
                r8 = r1
                goto L_0x0491
            L_0x0422:
                r20 = r1
                r18 = r13
                r13 = r37
                r37 = r2
                int r1 = r0.maxSizeWidth
                float r1 = (float) r1
                float r2 = r7.aspectRatio
                float r1 = r1 / r2
                float r2 = r11 * r6
                float r1 = java.lang.Math.min(r1, r2)
                int r1 = java.lang.Math.round(r1)
                float r1 = (float) r1
                float r1 = r1 / r11
                r27 = 0
                r28 = 1
                r29 = 0
                r30 = 0
                int r2 = r0.maxSizeWidth
                r33 = 7
                r26 = r7
                r31 = r2
                r32 = r1
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                int r2 = r0.maxSizeWidth
                r6 = 2
                int r2 = r2 / r6
                float r6 = r11 - r1
                float r8 = (float) r2
                float r12 = r9.aspectRatio
                float r8 = r8 / r12
                float r12 = (float) r2
                float r15 = r10.aspectRatio
                float r12 = r12 / r15
                float r8 = java.lang.Math.min(r8, r12)
                int r8 = java.lang.Math.round(r8)
                float r8 = (float) r8
                float r6 = java.lang.Math.min(r6, r8)
                float r6 = r6 / r11
                int r8 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
                if (r8 >= 0) goto L_0x0472
                r6 = r14
            L_0x0472:
                r27 = 0
                r28 = 0
                r29 = 1
                r30 = 1
                r33 = 9
                r26 = r9
                r31 = r2
                r32 = r6
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 1
                r28 = 1
                r33 = 10
                r26 = r10
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r8 = 1
            L_0x0491:
                r21 = r3
                r26 = r4
                r40 = r5
                r5 = r37
                goto L_0x08ee
            L_0x049b:
                r20 = r1
                r18 = r13
                r13 = r37
                r37 = r2
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.posArray
                r2 = 0
                java.lang.Object r1 = r1.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r0.posArray
                r7 = 1
                java.lang.Object r2 = r2.get(r7)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r7 = r0.posArray
                r8 = 2
                java.lang.Object r7 = r7.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r7
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                r9 = 3
                java.lang.Object r8 = r8.get(r9)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                r9 = 0
                char r10 = r5.charAt(r9)
                r9 = 119(0x77, float:1.67E-43)
                if (r10 != r9) goto L_0x0592
                int r9 = r0.maxSizeWidth
                float r9 = (float) r9
                float r10 = r1.aspectRatio
                float r9 = r9 / r10
                float r6 = r6 * r11
                float r6 = java.lang.Math.min(r9, r6)
                int r6 = java.lang.Math.round(r6)
                float r6 = (float) r6
                float r6 = r6 / r11
                r27 = 0
                r28 = 2
                r29 = 0
                r30 = 0
                int r9 = r0.maxSizeWidth
                r33 = 7
                r26 = r1
                r31 = r9
                r32 = r6
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                int r9 = r0.maxSizeWidth
                float r9 = (float) r9
                float r10 = r2.aspectRatio
                float r15 = r7.aspectRatio
                float r10 = r10 + r15
                float r15 = r8.aspectRatio
                float r10 = r10 + r15
                float r9 = r9 / r10
                int r9 = java.lang.Math.round(r9)
                float r9 = (float) r9
                float r10 = (float) r3
                int r15 = r0.maxSizeWidth
                float r15 = (float) r15
                r16 = 1053609165(0x3ecccccd, float:0.4)
                float r15 = r15 * r16
                float r12 = r2.aspectRatio
                float r12 = r12 * r9
                float r12 = java.lang.Math.min(r15, r12)
                float r10 = java.lang.Math.max(r10, r12)
                int r10 = (int) r10
                float r12 = (float) r3
                int r15 = r0.maxSizeWidth
                float r15 = (float) r15
                r16 = 1051260355(0x3ea8f5c3, float:0.33)
                float r15 = r15 * r16
                float r12 = java.lang.Math.max(r12, r15)
                float r15 = r8.aspectRatio
                float r15 = r15 * r9
                float r12 = java.lang.Math.max(r12, r15)
                int r12 = (int) r12
                int r15 = r0.maxSizeWidth
                int r15 = r15 - r10
                int r15 = r15 - r12
                r16 = 1114112000(0x42680000, float:58.0)
                r40 = r5
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
                if (r15 >= r5) goto L_0x0555
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
                int r5 = r5 - r15
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r16)
                int r16 = r5 / 2
                int r10 = r10 - r16
                int r16 = r5 / 2
                int r16 = r5 - r16
                int r12 = r12 - r16
            L_0x0555:
                float r5 = r11 - r6
                float r5 = java.lang.Math.min(r5, r9)
                float r5 = r5 / r11
                int r9 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
                if (r9 >= 0) goto L_0x0561
                r5 = r14
            L_0x0561:
                r27 = 0
                r28 = 0
                r29 = 1
                r30 = 1
                r33 = 9
                r26 = r2
                r31 = r10
                r32 = r5
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 1
                r28 = 1
                r33 = 8
                r26 = r7
                r31 = r15
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 2
                r28 = 2
                r33 = 10
                r26 = r8
                r31 = r12
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r5 = 2
                r8 = r5
                goto L_0x0655
            L_0x0592:
                r40 = r5
                float r5 = r2.aspectRatio
                r6 = 1065353216(0x3var_, float:1.0)
                float r12 = r6 / r5
                float r5 = r7.aspectRatio
                float r5 = r6 / r5
                float r12 = r12 + r5
                float r5 = r8.aspectRatio
                float r5 = r6 / r5
                float r12 = r12 + r5
                float r5 = r11 / r12
                int r5 = java.lang.Math.round(r5)
                int r5 = java.lang.Math.max(r3, r5)
                float r6 = (float) r4
                float r9 = (float) r5
                float r10 = r2.aspectRatio
                float r9 = r9 / r10
                float r6 = java.lang.Math.max(r6, r9)
                float r6 = r6 / r11
                r9 = 1051260355(0x3ea8f5c3, float:0.33)
                float r6 = java.lang.Math.min(r9, r6)
                float r10 = (float) r4
                float r12 = (float) r5
                float r15 = r7.aspectRatio
                float r12 = r12 / r15
                float r10 = java.lang.Math.max(r10, r12)
                float r10 = r10 / r11
                float r9 = java.lang.Math.min(r9, r10)
                r10 = 1065353216(0x3var_, float:1.0)
                float r12 = r10 - r6
                float r12 = r12 - r9
                float r10 = r1.aspectRatio
                float r10 = r10 * r11
                float r15 = (float) r13
                float r10 = r10 + r15
                int r15 = r0.maxSizeWidth
                int r15 = r15 - r5
                float r15 = (float) r15
                float r10 = java.lang.Math.min(r10, r15)
                int r10 = java.lang.Math.round(r10)
                r27 = 0
                r28 = 0
                r29 = 0
                r30 = 2
                float r15 = r6 + r9
                float r32 = r15 + r12
                r33 = 13
                r26 = r1
                r31 = r10
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 1
                r28 = 1
                r30 = 0
                r33 = 6
                r26 = r2
                r31 = r5
                r32 = r6
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                r27 = 0
                r29 = 1
                r30 = 1
                r33 = 2
                r26 = r7
                r32 = r9
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                int r15 = r0.maxSizeWidth
                r7.spanSize = r15
                r29 = 2
                r30 = 2
                r33 = 10
                r26 = r8
                r32 = r12
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                int r15 = r0.maxSizeWidth
                r8.spanSize = r15
                if (r38 == 0) goto L_0x0636
                int r15 = r0.maxSizeWidth
                int r15 = r15 - r5
                r1.spanSize = r15
                goto L_0x063f
            L_0x0636:
                int r15 = r0.maxSizeWidth
                int r15 = r15 - r10
                r2.spanSize = r15
                r7.leftSpanOffset = r10
                r8.leftSpanOffset = r10
            L_0x063f:
                r15 = 3
                float[] r15 = new float[r15]
                r16 = 0
                r15[r16] = r6
                r16 = r2
                r2 = 1
                r15[r2] = r9
                r17 = 2
                r15[r17] = r12
                r1.siblingHeights = r15
                r0.hasSibling = r2
                r2 = 1
                r8 = r2
            L_0x0655:
                r21 = r3
                r26 = r4
                r5 = r37
                goto L_0x08ee
            L_0x065d:
                r37 = r2
                r40 = r5
                r39 = r6
                r38 = r7
                r25 = r8
                r34 = r9
                r35 = r10
                r36 = r11
                r18 = r13
                r11 = r20
                r20 = r1
                r13 = r12
            L_0x0674:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.posArray
                int r1 = r1.size()
                float[] r1 = new float[r1]
                r2 = 0
            L_0x067d:
                r5 = r37
                if (r2 >= r5) goto L_0x06c4
                r6 = 1066192077(0x3f8ccccd, float:1.1)
                int r6 = (r39 > r6 ? 1 : (r39 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x069b
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r0.posArray
                java.lang.Object r6 = r6.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r6
                float r6 = r6.aspectRatio
                r7 = 1065353216(0x3var_, float:1.0)
                float r6 = java.lang.Math.max(r7, r6)
                r1[r2] = r6
                goto L_0x06ad
            L_0x069b:
                r7 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r0.posArray
                java.lang.Object r6 = r6.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r6
                float r6 = r6.aspectRatio
                float r6 = java.lang.Math.min(r7, r6)
                r1[r2] = r6
            L_0x06ad:
                r6 = 1059760867(0x3f2aaae3, float:0.66667)
                r8 = 1071225242(0x3fd9999a, float:1.7)
                r9 = r1[r2]
                float r8 = java.lang.Math.min(r8, r9)
                float r6 = java.lang.Math.max(r6, r8)
                r1[r2] = r6
                int r2 = r2 + 1
                r37 = r5
                goto L_0x067d
            L_0x06c4:
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r6 = 1
            L_0x06ca:
                int r7 = r1.length
                if (r6 >= r7) goto L_0x06ea
                int r7 = r1.length
                int r7 = r7 - r6
                r8 = 3
                if (r6 > r8) goto L_0x06e7
                if (r7 <= r8) goto L_0x06d5
                goto L_0x06e7
            L_0x06d5:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r8 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r9 = 0
                float r10 = r0.multiHeight(r1, r9, r6)
                int r9 = r1.length
                float r9 = r0.multiHeight(r1, r6, r9)
                r8.<init>(r6, r7, r10, r9)
                r2.add(r8)
            L_0x06e7:
                int r6 = r6 + 1
                goto L_0x06ca
            L_0x06ea:
                r6 = 1
            L_0x06eb:
                int r7 = r1.length
                r8 = 1
                int r7 = r7 - r8
                if (r6 >= r7) goto L_0x0733
                r7 = 1
            L_0x06f1:
                int r8 = r1.length
                int r8 = r8 - r6
                if (r7 >= r8) goto L_0x0730
                int r8 = r1.length
                int r8 = r8 - r6
                int r8 = r8 - r7
                r9 = 3
                if (r6 > r9) goto L_0x072d
                r9 = 1062836634(0x3var_a, float:0.85)
                int r9 = (r39 > r9 ? 1 : (r39 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x0704
                r9 = 4
                goto L_0x0705
            L_0x0704:
                r9 = 3
            L_0x0705:
                if (r7 > r9) goto L_0x072d
                r9 = 3
                if (r8 <= r9) goto L_0x070b
                goto L_0x072d
            L_0x070b:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r9 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r10 = 0
                float r30 = r0.multiHeight(r1, r10, r6)
                int r10 = r6 + r7
                float r31 = r0.multiHeight(r1, r6, r10)
                int r10 = r6 + r7
                int r12 = r1.length
                float r32 = r0.multiHeight(r1, r10, r12)
                r26 = r9
                r27 = r6
                r28 = r7
                r29 = r8
                r26.<init>(r27, r28, r29, r30, r31, r32)
                r2.add(r9)
            L_0x072d:
                int r7 = r7 + 1
                goto L_0x06f1
            L_0x0730:
                int r6 = r6 + 1
                goto L_0x06eb
            L_0x0733:
                r6 = 1
            L_0x0734:
                int r7 = r1.length
                r8 = 2
                int r7 = r7 - r8
                if (r6 >= r7) goto L_0x0789
                r7 = 1
            L_0x073a:
                int r8 = r1.length
                int r8 = r8 - r6
                if (r7 >= r8) goto L_0x0786
                r8 = 1
            L_0x073f:
                int r9 = r1.length
                int r9 = r9 - r6
                int r9 = r9 - r7
                if (r8 >= r9) goto L_0x0783
                int r9 = r1.length
                int r9 = r9 - r6
                int r9 = r9 - r7
                int r9 = r9 - r8
                r10 = 3
                if (r6 > r10) goto L_0x0780
                if (r7 > r10) goto L_0x0780
                if (r8 > r10) goto L_0x0780
                if (r9 <= r10) goto L_0x0752
                goto L_0x0780
            L_0x0752:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r10 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r12 = 0
                float r46 = r0.multiHeight(r1, r12, r6)
                int r12 = r6 + r7
                float r47 = r0.multiHeight(r1, r6, r12)
                int r12 = r6 + r7
                int r15 = r6 + r7
                int r15 = r15 + r8
                float r48 = r0.multiHeight(r1, r12, r15)
                int r12 = r6 + r7
                int r12 = r12 + r8
                int r15 = r1.length
                float r49 = r0.multiHeight(r1, r12, r15)
                r41 = r10
                r42 = r6
                r43 = r7
                r44 = r8
                r45 = r9
                r41.<init>(r42, r43, r44, r45, r46, r47, r48, r49)
                r2.add(r10)
            L_0x0780:
                int r8 = r8 + 1
                goto L_0x073f
            L_0x0783:
                int r7 = r7 + 1
                goto L_0x073a
            L_0x0786:
                int r6 = r6 + 1
                goto L_0x0734
            L_0x0789:
                r7 = 0
                r8 = 0
                int r9 = r0.maxSizeWidth
                r10 = 3
                int r9 = r9 / r10
                r10 = 4
                int r9 = r9 * 4
                float r9 = (float) r9
                r10 = 0
            L_0x0794:
                int r12 = r2.size()
                if (r10 >= r12) goto L_0x083c
                java.lang.Object r12 = r2.get(r10)
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r12 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r12
                r15 = 0
                r17 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r23 = 0
                r50 = r23
                r23 = r2
                r2 = r50
            L_0x07ac:
                r26 = r4
                float[] r4 = r12.heights
                int r4 = r4.length
                if (r2 >= r4) goto L_0x07cb
                float[] r4 = r12.heights
                r4 = r4[r2]
                float r15 = r15 + r4
                float[] r4 = r12.heights
                r4 = r4[r2]
                int r4 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
                if (r4 >= 0) goto L_0x07c6
                float[] r4 = r12.heights
                r4 = r4[r2]
                r17 = r4
            L_0x07c6:
                int r2 = r2 + 1
                r4 = r26
                goto L_0x07ac
            L_0x07cb:
                float r2 = r15 - r9
                float r2 = java.lang.Math.abs(r2)
                int[] r4 = r12.lineCounts
                int r4 = r4.length
                r27 = r6
                r6 = 1
                if (r4 <= r6) goto L_0x0817
                int[] r4 = r12.lineCounts
                r16 = 0
                r4 = r4[r16]
                r28 = r9
                int[] r9 = r12.lineCounts
                r9 = r9[r6]
                if (r4 > r9) goto L_0x0810
                int[] r4 = r12.lineCounts
                int r4 = r4.length
                r9 = 2
                if (r4 <= r9) goto L_0x07fa
                int[] r4 = r12.lineCounts
                r4 = r4[r6]
                int[] r6 = r12.lineCounts
                r6 = r6[r9]
                if (r4 > r6) goto L_0x07f8
                goto L_0x07fa
            L_0x07f8:
                r6 = 3
                goto L_0x0811
            L_0x07fa:
                int[] r4 = r12.lineCounts
                int r4 = r4.length
                r6 = 3
                if (r4 <= r6) goto L_0x080c
                int[] r4 = r12.lineCounts
                r9 = 2
                r4 = r4[r9]
                int[] r9 = r12.lineCounts
                r9 = r9[r6]
                if (r4 <= r9) goto L_0x080c
                goto L_0x0811
            L_0x080c:
                r4 = 1067030938(0x3var_a, float:1.2)
                goto L_0x081f
            L_0x0810:
                r6 = 3
            L_0x0811:
                r4 = 1067030938(0x3var_a, float:1.2)
                float r2 = r2 * r4
                goto L_0x081f
            L_0x0817:
                r28 = r9
                r4 = 1067030938(0x3var_a, float:1.2)
                r6 = 3
                r16 = 0
            L_0x081f:
                float r9 = (float) r3
                int r9 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x0828
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r2 = r2 * r9
            L_0x0828:
                if (r7 == 0) goto L_0x082e
                int r9 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
                if (r9 >= 0) goto L_0x0830
            L_0x082e:
                r7 = r12
                r8 = r2
            L_0x0830:
                int r10 = r10 + 1
                r2 = r23
                r4 = r26
                r6 = r27
                r9 = r28
                goto L_0x0794
            L_0x083c:
                r23 = r2
                r26 = r4
                r27 = r6
                r28 = r9
                if (r7 != 0) goto L_0x0847
                return
            L_0x0847:
                r2 = 0
                r4 = 0
                r6 = 0
                r9 = r25
            L_0x084c:
                int[] r10 = r7.lineCounts
                int r10 = r10.length
                if (r6 >= r10) goto L_0x08e5
                int[] r10 = r7.lineCounts
                r10 = r10[r6]
                float[] r12 = r7.heights
                r12 = r12[r6]
                int r15 = r0.maxSizeWidth
                r16 = 0
                r17 = r2
                int r2 = r10 + -1
                int r9 = java.lang.Math.max(r9, r2)
                r2 = 0
                r21 = r3
                r3 = r16
                r16 = r15
                r15 = r17
            L_0x086e:
                if (r2 >= r10) goto L_0x08c9
                r17 = r1[r15]
                r24 = r1
                float r1 = r17 * r12
                int r1 = (int) r1
                int r16 = r16 - r1
                r25 = r8
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r15)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                r29 = 0
                if (r6 != 0) goto L_0x0889
                r29 = r29 | 4
            L_0x0889:
                r30 = r9
                int[] r9 = r7.lineCounts
                int r9 = r9.length
                r19 = 1
                int r9 = r9 + -1
                if (r6 != r9) goto L_0x0896
                r29 = r29 | 8
            L_0x0896:
                if (r2 != 0) goto L_0x089d
                r29 = r29 | 1
                if (r38 == 0) goto L_0x089d
                r3 = r8
            L_0x089d:
                int r9 = r10 + -1
                if (r2 != r9) goto L_0x08a6
                r29 = r29 | 2
                if (r38 != 0) goto L_0x08a6
                r3 = r8
            L_0x08a6:
                float r9 = r12 / r11
                float r47 = java.lang.Math.max(r14, r9)
                r41 = r8
                r42 = r2
                r43 = r2
                r44 = r6
                r45 = r6
                r46 = r1
                r48 = r29
                r41.set(r42, r43, r44, r45, r46, r47, r48)
                int r15 = r15 + 1
                int r2 = r2 + 1
                r1 = r24
                r8 = r25
                r9 = r30
                goto L_0x086e
            L_0x08c9:
                r24 = r1
                r25 = r8
                r30 = r9
                int r1 = r3.pw
                int r1 = r1 + r16
                r3.pw = r1
                int r1 = r3.spanSize
                int r1 = r1 + r16
                r3.spanSize = r1
                float r4 = r4 + r12
                int r6 = r6 + 1
                r2 = r15
                r3 = r21
                r1 = r24
                goto L_0x084c
            L_0x08e5:
                r24 = r1
                r17 = r2
                r21 = r3
                r25 = r8
                r8 = r9
            L_0x08ee:
                r1 = 108(0x6c, float:1.51E-43)
                r2 = 0
            L_0x08f1:
                if (r2 >= r5) goto L_0x097a
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                if (r38 == 0) goto L_0x0914
                byte r4 = r3.minX
                if (r4 != 0) goto L_0x0907
                int r4 = r3.spanSize
                int r4 = r4 + r20
                r3.spanSize = r4
            L_0x0907:
                int r4 = r3.flags
                r6 = 2
                r4 = r4 & r6
                if (r4 == 0) goto L_0x0912
                r4 = 1
                r3.edge = r4
                r6 = 1
                goto L_0x092c
            L_0x0912:
                r6 = 1
                goto L_0x092c
            L_0x0914:
                byte r4 = r3.maxX
                if (r4 == r8) goto L_0x091e
                int r4 = r3.flags
                r6 = 2
                r4 = r4 & r6
                if (r4 == 0) goto L_0x0924
            L_0x091e:
                int r4 = r3.spanSize
                int r4 = r4 + r20
                r3.spanSize = r4
            L_0x0924:
                int r4 = r3.flags
                r6 = 1
                r4 = r4 & r6
                if (r4 == 0) goto L_0x092c
                r3.edge = r6
            L_0x092c:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.messages
                java.lang.Object r4 = r4.get(r2)
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                if (r38 != 0) goto L_0x0973
                boolean r7 = r4.needDrawAvatarInternal()
                if (r7 == 0) goto L_0x0973
                boolean r7 = r3.edge
                if (r7 == 0) goto L_0x0954
                int r7 = r3.spanSize
                r9 = 1000(0x3e8, float:1.401E-42)
                if (r7 == r9) goto L_0x094b
                int r7 = r3.spanSize
                int r7 = r7 + r1
                r3.spanSize = r7
            L_0x094b:
                int r7 = r3.pw
                int r7 = r7 + r1
                r3.pw = r7
                r9 = 2
                r10 = 1000(0x3e8, float:1.401E-42)
                goto L_0x0976
            L_0x0954:
                int r7 = r3.flags
                r9 = 2
                r7 = r7 & r9
                if (r7 == 0) goto L_0x0970
                int r7 = r3.spanSize
                r10 = 1000(0x3e8, float:1.401E-42)
                if (r7 == r10) goto L_0x0966
                int r7 = r3.spanSize
                int r7 = r7 - r1
                r3.spanSize = r7
                goto L_0x0976
            L_0x0966:
                int r7 = r3.leftSpanOffset
                if (r7 == 0) goto L_0x0976
                int r7 = r3.leftSpanOffset
                int r7 = r7 + r1
                r3.leftSpanOffset = r7
                goto L_0x0976
            L_0x0970:
                r10 = 1000(0x3e8, float:1.401E-42)
                goto L_0x0976
            L_0x0973:
                r9 = 2
                r10 = 1000(0x3e8, float:1.401E-42)
            L_0x0976:
                int r2 = r2 + 1
                goto L_0x08f1
            L_0x097a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void");
        }

        public MessageObject findPrimaryMessageObject() {
            return findMessageWithFlags(5);
        }

        public MessageObject findMessageWithFlags(int flags) {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject object = this.messages.get(i);
                GroupedMessagePosition position = this.positions.get(object);
                if (position != null && (position.flags & flags) == flags) {
                    return object;
                }
            }
            return null;
        }

        public static class TransitionParams {
            public boolean backgroundChangeBounds;
            public int bottom;
            public float captionEnterProgress = 1.0f;
            public ChatMessageCell cell;
            public boolean drawBackgroundForDeletedItems;
            public boolean drawCaptionLayout;
            public boolean isNewGroup;
            public int left;
            public float offsetBottom;
            public float offsetLeft;
            public float offsetRight;
            public float offsetTop;
            public boolean pinnedBotton;
            public boolean pinnedTop;
            public int right;
            public int top;

            public void reset() {
                this.captionEnterProgress = 1.0f;
                this.offsetBottom = 0.0f;
                this.offsetTop = 0.0f;
                this.offsetRight = 0.0f;
                this.offsetLeft = 0.0f;
                this.backgroundChangeBounds = false;
            }
        }
    }

    public MessageObject(int accountNum, TLRPC.Message message, String formattedMessage, String name, String userName, boolean localMessage, boolean isChannel, boolean supergroup, boolean edit) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.viewRef = new AtomicReference<>((Object) null);
        this.localType = localMessage ? 2 : 1;
        this.currentAccount = accountNum;
        this.localName = name;
        this.localUserName = userName;
        this.messageText = formattedMessage;
        this.messageOwner = message;
        this.localChannel = isChannel;
        this.localSupergroup = supergroup;
        this.localEdit = edit;
    }

    public MessageObject(int accountNum, TLRPC.Message message, AbstractMap<Long, TLRPC.User> users, boolean generateLayout, boolean checkMediaExists) {
        this(accountNum, message, users, (AbstractMap<Long, TLRPC.Chat>) null, generateLayout, checkMediaExists);
    }

    public MessageObject(int accountNum, TLRPC.Message message, LongSparseArray<TLRPC.User> users, boolean generateLayout, boolean checkMediaExists) {
        this(accountNum, message, users, (LongSparseArray<TLRPC.Chat>) null, generateLayout, checkMediaExists);
    }

    public MessageObject(int accountNum, TLRPC.Message message, boolean generateLayout, boolean checkMediaExists) {
        this(accountNum, message, (MessageObject) null, (AbstractMap<Long, TLRPC.User>) null, (AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.User>) null, (LongSparseArray<TLRPC.Chat>) null, generateLayout, checkMediaExists, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, MessageObject replyToMessage, boolean generateLayout, boolean checkMediaExists) {
        this(accountNum, message, replyToMessage, (AbstractMap<Long, TLRPC.User>) null, (AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.User>) null, (LongSparseArray<TLRPC.Chat>) null, generateLayout, checkMediaExists, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, AbstractMap<Long, TLRPC.User> users, AbstractMap<Long, TLRPC.Chat> chats, boolean generateLayout, boolean checkMediaExists) {
        this(accountNum, message, users, chats, generateLayout, checkMediaExists, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, LongSparseArray<TLRPC.User> users, LongSparseArray<TLRPC.Chat> chats, boolean generateLayout, boolean checkMediaExists) {
        this(accountNum, message, (MessageObject) null, (AbstractMap<Long, TLRPC.User>) null, (AbstractMap<Long, TLRPC.Chat>) null, users, chats, generateLayout, checkMediaExists, 0);
    }

    public MessageObject(int accountNum, TLRPC.Message message, AbstractMap<Long, TLRPC.User> users, AbstractMap<Long, TLRPC.Chat> chats, boolean generateLayout, boolean checkMediaExists, long eid) {
        this(accountNum, message, (MessageObject) null, users, chats, (LongSparseArray<TLRPC.User>) null, (LongSparseArray<TLRPC.Chat>) null, generateLayout, checkMediaExists, eid);
    }

    public MessageObject(int accountNum, TLRPC.Message message, MessageObject replyToMessage, AbstractMap<Long, TLRPC.User> users, AbstractMap<Long, TLRPC.Chat> chats, LongSparseArray<TLRPC.User> sUsers, LongSparseArray<TLRPC.Chat> sChats, boolean generateLayout, boolean checkMediaExists, long eid) {
        TextPaint paint;
        CharSequence emoji;
        TLRPC.Message message2 = message;
        AbstractMap<Long, TLRPC.User> abstractMap = users;
        LongSparseArray<TLRPC.User> longSparseArray = sUsers;
        boolean z = generateLayout;
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.viewRef = new AtomicReference<>((Object) null);
        Theme.createCommonMessageResources();
        this.currentAccount = accountNum;
        this.messageOwner = message2;
        this.replyMessageObject = replyToMessage;
        this.eventId = eid;
        this.wasUnread = !message2.out && this.messageOwner.unread;
        if (message2.replyMessage != null) {
            MessageObject messageObject = r2;
            MessageObject messageObject2 = new MessageObject(this.currentAccount, message2.replyMessage, (MessageObject) null, users, chats, sUsers, sChats, false, checkMediaExists, eid);
            this.replyMessageObject = messageObject;
        }
        TLRPC.User fromUser = message2.from_id instanceof TLRPC.TL_peerUser ? getUser(abstractMap, longSparseArray, message2.from_id.user_id) : null;
        updateMessageText(abstractMap, chats, longSparseArray, sChats);
        setType();
        measureInlineBotButtons();
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int dateDay = rightNow.get(6);
        int dateYear = rightNow.get(1);
        int dateMonth = rightNow.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth), Integer.valueOf(dateDay)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth)});
        createMessageSendInfo();
        generateCaption();
        boolean z2 = generateLayout;
        if (z2) {
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            int[] emojiOnly = allowsBigEmoji() ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly, this.contentType == 0, this.viewRef);
            checkEmojiOnly(emojiOnly);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1 && !(message2.media instanceof TLRPC.TL_messageMediaWebPage) && !(message2.media instanceof TLRPC.TL_messageMediaInvoice) && message2.entities.isEmpty() && (((message2.media instanceof TLRPC.TL_messageMediaEmpty) || message2.media == null) && this.messageOwner.grouped_id == 0)) {
                CharSequence emoji2 = this.messageText;
                int indexOf = TextUtils.indexOf(emoji2, "🏻");
                int index = indexOf;
                if (indexOf >= 0) {
                    this.emojiAnimatedStickerColor = "_c1";
                    emoji2 = emoji2.subSequence(0, index);
                } else {
                    int indexOf2 = TextUtils.indexOf(emoji2, "🏼");
                    index = indexOf2;
                    if (indexOf2 >= 0) {
                        this.emojiAnimatedStickerColor = "_c2";
                        emoji2 = emoji2.subSequence(0, index);
                    } else {
                        int indexOf3 = TextUtils.indexOf(emoji2, "🏽");
                        index = indexOf3;
                        if (indexOf3 >= 0) {
                            this.emojiAnimatedStickerColor = "_c3";
                            emoji2 = emoji2.subSequence(0, index);
                        } else {
                            int indexOf4 = TextUtils.indexOf(emoji2, "🏾");
                            index = indexOf4;
                            if (indexOf4 >= 0) {
                                this.emojiAnimatedStickerColor = "_c4";
                                emoji2 = emoji2.subSequence(0, index);
                            } else {
                                int indexOf5 = TextUtils.indexOf(emoji2, "🏿");
                                index = indexOf5;
                                if (indexOf5 >= 0) {
                                    this.emojiAnimatedStickerColor = "_c5";
                                    emoji2 = emoji2.subSequence(0, index);
                                } else {
                                    this.emojiAnimatedStickerColor = "";
                                }
                            }
                        }
                    }
                }
                if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) || index + 2 >= this.messageText.length()) {
                    emoji = emoji2;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(emoji2.toString());
                    CharSequence charSequence = this.messageText;
                    CharSequence charSequence2 = emoji2;
                    sb.append(charSequence.subSequence(index + 2, charSequence.length()).toString());
                    emoji = sb.toString();
                }
                if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) || EmojiData.emojiColoredMap.contains(emoji.toString())) {
                    this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji);
                }
            }
            if (this.emojiAnimatedSticker == null) {
                generateLayout(fromUser);
            } else {
                this.type = 1000;
                if (isSticker()) {
                    this.type = 13;
                } else if (isAnimatedSticker()) {
                    this.type = 15;
                }
            }
            createPathThumb();
        }
        this.layoutCreated = z2;
        generateThumbs(false);
        if (checkMediaExists) {
            checkMediaExistance();
        }
    }

    private void createPathThumb() {
        TLRPC.Document document = getDocument();
        if (document != null) {
            this.pathThumb = DocumentObject.getSvgThumb(document, "chat_serviceBackground", 1.0f);
        }
    }

    public void createStrippedThumb() {
        if (this.photoThumbs != null && SharedConfig.getDevicePerformanceClass() == 2) {
            try {
                int N = this.photoThumbs.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.PhotoSize photoSize = this.photoThumbs.get(a);
                    if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                        this.strippedThumb = new BitmapDrawable(ImageLoader.getStrippedPhotoBitmap(photoSize.bytes, "b"));
                        return;
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private void createDateArray(int accountNum, TLRPC.TL_channelAdminLogEvent event, ArrayList<MessageObject> messageObjects, HashMap<String, ArrayList<MessageObject>> messagesByDays, boolean addToEnd) {
        if (messagesByDays.get(this.dateKey) == null) {
            messagesByDays.put(this.dateKey, new ArrayList());
            TLRPC.TL_message dateMsg = new TLRPC.TL_message();
            dateMsg.message = LocaleController.formatDateChat((long) event.date);
            dateMsg.id = 0;
            dateMsg.date = event.date;
            MessageObject dateObj = new MessageObject(accountNum, dateMsg, false, false);
            dateObj.type = 10;
            dateObj.contentType = 1;
            dateObj.isDateObject = true;
            if (addToEnd) {
                messageObjects.add(0, dateObj);
            } else {
                messageObjects.add(dateObj);
            }
        }
    }

    public void checkForScam() {
    }

    private void checkEmojiOnly(int[] emojiOnly) {
        int size;
        TextPaint emojiPaint;
        if (emojiOnly != null && emojiOnly[0] >= 1 && emojiOnly[0] <= 3) {
            switch (emojiOnly[0]) {
                case 1:
                    emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                    int size2 = AndroidUtilities.dp(32.0f);
                    this.emojiOnlyCount = 1;
                    size = size2;
                    break;
                case 2:
                    emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                    size = AndroidUtilities.dp(28.0f);
                    this.emojiOnlyCount = 2;
                    break;
                default:
                    emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                    size = AndroidUtilities.dp(24.0f);
                    this.emojiOnlyCount = 3;
                    break;
            }
            CharSequence charSequence = this.messageText;
            Emoji.EmojiSpan[] spans = (Emoji.EmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), Emoji.EmojiSpan.class);
            if (spans != null && spans.length > 0) {
                for (Emoji.EmojiSpan replaceFontMetrics : spans) {
                    replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), size);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:164:0x0505, code lost:
        if (r12.until_date != r11.until_date) goto L_0x0512;
     */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0592  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x05a4 A[LOOP:0: B:172:0x054c->B:194:0x05a4, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x16c6  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x171f  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x1722  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x1725  */
    /* JADX WARNING: Removed duplicated region for block: B:762:0x17a3  */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x17ae  */
    /* JADX WARNING: Removed duplicated region for block: B:788:0x183d  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x05b2 A[EDGE_INSN: B:790:0x05b2->B:196:0x05b2 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MessageObject(int r44, org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent r45, java.util.ArrayList<org.telegram.messenger.MessageObject> r46, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r47, org.telegram.tgnet.TLRPC.Chat r48, int[] r49, boolean r50) {
        /*
            r43 = this;
            r6 = r43
            r7 = r45
            r8 = r46
            r9 = r48
            r43.<init>()
            r0 = 1000(0x3e8, float:1.401E-42)
            r6.type = r0
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6.forceSeekTo = r0
            java.util.concurrent.atomic.AtomicReference r0 = new java.util.concurrent.atomic.AtomicReference
            r10 = 0
            r0.<init>(r10)
            r6.viewRef = r0
            r6.currentEvent = r7
            r11 = r44
            r6.currentAccount = r11
            r0 = 0
            long r1 = r7.user_id
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x003c
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r12 = r7.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r2)
            r12 = r0
            goto L_0x003d
        L_0x003c:
            r12 = r0
        L_0x003d:
            java.util.GregorianCalendar r0 = new java.util.GregorianCalendar
            r0.<init>()
            r13 = r0
            int r0 = r7.date
            long r0 = (long) r0
            r14 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 * r14
            r13.setTimeInMillis(r0)
            r0 = 6
            int r14 = r13.get(r0)
            r15 = 1
            int r16 = r13.get(r15)
            r0 = 2
            int r17 = r13.get(r0)
            r1 = 3
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r16)
            r10 = 0
            r2[r10] = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r17)
            r2[r15] = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r14)
            r2[r0] = r5
            java.lang.String r5 = "%d_%02d_%02d"
            java.lang.String r2 = java.lang.String.format(r5, r2)
            r6.dateKey = r2
            java.lang.Object[] r2 = new java.lang.Object[r0]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r16)
            r2[r10] = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r17)
            r2[r15] = r5
            java.lang.String r5 = "%d_%02d"
            java.lang.String r2 = java.lang.String.format(r5, r2)
            r6.monthKey = r2
            org.telegram.tgnet.TLRPC$TL_peerChannel r2 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r2.<init>()
            r5 = r2
            long r0 = r9.id
            r5.channel_id = r0
            r0 = 0
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle
            java.lang.String r3 = ""
            java.lang.String r4 = "un1"
            if (r1 == 0) goto L_0x00e5
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle) r1
            java.lang.String r1 = r1.new_value
            boolean r2 = r9.megagroup
            if (r2 == 0) goto L_0x00c3
            java.lang.Object[] r2 = new java.lang.Object[r15]
            r2[r10] = r1
            java.lang.String r10 = "EventLogEditedGroupTitle"
            r15 = 2131625692(0x7f0e06dc, float:1.88786E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r10, r15, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x00d8
        L_0x00c3:
            r2 = 2131625687(0x7f0e06d7, float:1.887859E38)
            r10 = 1
            java.lang.Object[] r15 = new java.lang.Object[r10]
            r10 = 0
            r15[r10] = r1
            java.lang.String r10 = "EventLogEditedChannelTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r10, r2, r15)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
        L_0x00d8:
            r21 = r0
            r31 = r3
            r32 = r5
            r8 = r12
            r22 = r13
            r23 = r14
            goto L_0x16c0
        L_0x00e5:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto
            if (r1 == 0) goto L_0x0196
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto) r1
            org.telegram.tgnet.TLRPC$TL_messageService r2 = new org.telegram.tgnet.TLRPC$TL_messageService
            r2.<init>()
            r6.messageOwner = r2
            org.telegram.tgnet.TLRPC$Photo r2 = r1.new_photo
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r2 == 0) goto L_0x0129
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto r10 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            r10.<init>()
            r2.action = r10
            boolean r2 = r9.megagroup
            if (r2 == 0) goto L_0x0119
            r2 = 2131625750(0x7f0e0716, float:1.8878717E38)
            java.lang.String r10 = "EventLogRemovedWGroupPhoto"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x0189
        L_0x0119:
            r2 = 2131625744(0x7f0e0710, float:1.8878705E38)
            java.lang.String r10 = "EventLogRemovedChannelPhoto"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x0189
        L_0x0129:
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto r10 = new org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            r10.<init>()
            r2.action = r10
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            org.telegram.tgnet.TLRPC$Photo r10 = r1.new_photo
            r2.photo = r10
            boolean r2 = r9.megagroup
            if (r2 == 0) goto L_0x0164
            boolean r2 = r43.isVideoAvatar()
            if (r2 == 0) goto L_0x0154
            r2 = 2131625693(0x7f0e06dd, float:1.8878601E38)
            java.lang.String r10 = "EventLogEditedGroupVideo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x0189
        L_0x0154:
            r2 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r10 = "EventLogEditedGroupPhoto"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x0189
        L_0x0164:
            boolean r2 = r43.isVideoAvatar()
            if (r2 == 0) goto L_0x017a
            r2 = 2131625688(0x7f0e06d8, float:1.8878591E38)
            java.lang.String r10 = "EventLogEditedChannelVideo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x0189
        L_0x017a:
            r2 = 2131625685(0x7f0e06d5, float:1.8878585E38)
            java.lang.String r10 = "EventLogEditedChannelPhoto"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
        L_0x0189:
            r21 = r0
            r31 = r3
            r32 = r5
            r8 = r12
            r22 = r13
            r23 = r14
            goto L_0x16c0
        L_0x0196:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin
            java.lang.String r10 = "EventLogGroupJoined"
            r15 = 2131625678(0x7f0e06ce, float:1.887857E38)
            java.lang.String r2 = "EventLogChannelJoined"
            if (r1 == 0) goto L_0x01d8
            boolean r1 = r9.megagroup
            if (r1 == 0) goto L_0x01c1
            r1 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r12)
            r6.messageText = r1
            r21 = r0
            r31 = r3
            r32 = r5
            r8 = r12
            r22 = r13
            r23 = r14
            goto L_0x16c0
        L_0x01c1:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r15)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r12)
            r6.messageText = r1
            r21 = r0
            r31 = r3
            r32 = r5
            r8 = r12
            r22 = r13
            r23 = r14
            goto L_0x16c0
        L_0x01d8:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave
            if (r1 == 0) goto L_0x0230
            org.telegram.tgnet.TLRPC$TL_messageService r1 = new org.telegram.tgnet.TLRPC$TL_messageService
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser r2 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            r2.<init>()
            r1.action = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            long r10 = r7.user_id
            r1.user_id = r10
            boolean r1 = r9.megagroup
            if (r1 == 0) goto L_0x0214
            r1 = 2131625720(0x7f0e06f8, float:1.8878656E38)
            java.lang.String r2 = "EventLogLeftGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r12)
            r6.messageText = r1
            r21 = r0
            r31 = r3
            r32 = r5
            r8 = r12
            r22 = r13
            r23 = r14
            goto L_0x16c0
        L_0x0214:
            r1 = 2131625719(0x7f0e06f7, float:1.8878654E38)
            java.lang.String r2 = "EventLogLeftChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r12)
            r6.messageText = r1
            r21 = r0
            r31 = r3
            r32 = r5
            r8 = r12
            r22 = r13
            r23 = r14
            goto L_0x16c0
        L_0x0230:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite
            java.lang.String r11 = "un2"
            if (r1 == 0) goto L_0x02d3
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantInvite r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite) r1
            org.telegram.tgnet.TLRPC$TL_messageService r15 = new org.telegram.tgnet.TLRPC$TL_messageService
            r15.<init>()
            r6.messageOwner = r15
            r21 = r0
            org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser r0 = new org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            r0.<init>()
            r15.action = r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r0 = r1.participant
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
            r15 = r1
            long r0 = getPeerId(r0)
            r18 = 0
            int r20 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r20 <= 0) goto L_0x0270
            r22 = r13
            int r13 = r6.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            r23 = r14
            java.lang.Long r14 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r13 = r13.getUser(r14)
            r18 = r15
            goto L_0x0285
        L_0x0270:
            r22 = r13
            r23 = r14
            int r13 = r6.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            r18 = r15
            long r14 = -r0
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r13 = r13.getChat(r14)
        L_0x0285:
            org.telegram.tgnet.TLRPC$Message r14 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_peerUser
            if (r14 == 0) goto L_0x02b7
            org.telegram.tgnet.TLRPC$Message r14 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            long r14 = r14.user_id
            int r19 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r19 != 0) goto L_0x02b7
            boolean r11 = r9.megagroup
            if (r11 == 0) goto L_0x02a9
            r2 = 2131625715(0x7f0e06f3, float:1.8878646E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x02cc
        L_0x02a9:
            r10 = 2131625678(0x7f0e06ce, float:1.887857E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r10)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
            goto L_0x02cc
        L_0x02b7:
            r2 = 2131625668(0x7f0e06c4, float:1.887855E38)
            java.lang.String r10 = "EventLogAdded"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r11, r13)
            r6.messageText = r2
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r12)
            r6.messageText = r2
        L_0x02cc:
            r31 = r3
            r32 = r5
            r8 = r12
            goto L_0x16c0
        L_0x02d3:
            r21 = r0
            r22 = r13
            r23 = r14
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin
            java.lang.String r1 = "%1$s"
            r14 = 32
            r15 = 10
            if (r0 != 0) goto L_0x13e8
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan
            if (r0 == 0) goto L_0x0302
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan) r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r0 = r0.prev_participant
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin
            if (r0 == 0) goto L_0x0302
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan) r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r0 = r0.new_participant
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipant
            if (r0 == 0) goto L_0x0302
            r8 = r12
            goto L_0x13e9
        L_0x0302:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights
            if (r0 == 0) goto L_0x04a2
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights) r0
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.prev_banned_rights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r0.new_banned_rights
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r11 = 2131625681(0x7f0e06d1, float:1.8878577E38)
            java.lang.String r10 = "EventLogDefaultPermissions"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
            r4.<init>(r10)
            r10 = 0
            if (r1 != 0) goto L_0x032e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r11 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r11.<init>()
            r1 = r11
        L_0x032e:
            if (r2 != 0) goto L_0x0336
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r11 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r11.<init>()
            r2 = r11
        L_0x0336:
            boolean r11 = r1.send_messages
            boolean r13 = r2.send_messages
            if (r11 == r13) goto L_0x035e
            r4.append(r15)
            r10 = 1
            r4.append(r15)
            boolean r11 = r2.send_messages
            if (r11 != 0) goto L_0x034a
            r11 = 43
            goto L_0x034c
        L_0x034a:
            r11 = 45
        L_0x034c:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625757(0x7f0e071d, float:1.887873E38)
            java.lang.String r13 = "EventLogRestrictedSendMessages"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x035e:
            boolean r11 = r1.send_stickers
            boolean r13 = r2.send_stickers
            if (r11 != r13) goto L_0x0376
            boolean r11 = r1.send_inline
            boolean r13 = r2.send_inline
            if (r11 != r13) goto L_0x0376
            boolean r11 = r1.send_gifs
            boolean r13 = r2.send_gifs
            if (r11 != r13) goto L_0x0376
            boolean r11 = r1.send_games
            boolean r13 = r2.send_games
            if (r11 == r13) goto L_0x039a
        L_0x0376:
            if (r10 != 0) goto L_0x037c
            r4.append(r15)
            r10 = 1
        L_0x037c:
            r4.append(r15)
            boolean r11 = r2.send_stickers
            if (r11 != 0) goto L_0x0386
            r11 = 43
            goto L_0x0388
        L_0x0386:
            r11 = 45
        L_0x0388:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625759(0x7f0e071f, float:1.8878735E38)
            java.lang.String r13 = "EventLogRestrictedSendStickers"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x039a:
            boolean r11 = r1.send_media
            boolean r13 = r2.send_media
            if (r11 == r13) goto L_0x03c4
            if (r10 != 0) goto L_0x03a6
            r4.append(r15)
            r10 = 1
        L_0x03a6:
            r4.append(r15)
            boolean r11 = r2.send_media
            if (r11 != 0) goto L_0x03b0
            r11 = 43
            goto L_0x03b2
        L_0x03b0:
            r11 = 45
        L_0x03b2:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625756(0x7f0e071c, float:1.8878729E38)
            java.lang.String r13 = "EventLogRestrictedSendMedia"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x03c4:
            boolean r11 = r1.send_polls
            boolean r13 = r2.send_polls
            if (r11 == r13) goto L_0x03ee
            if (r10 != 0) goto L_0x03d0
            r4.append(r15)
            r10 = 1
        L_0x03d0:
            r4.append(r15)
            boolean r11 = r2.send_polls
            if (r11 != 0) goto L_0x03da
            r11 = 43
            goto L_0x03dc
        L_0x03da:
            r11 = 45
        L_0x03dc:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625758(0x7f0e071e, float:1.8878733E38)
            java.lang.String r13 = "EventLogRestrictedSendPolls"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x03ee:
            boolean r11 = r1.embed_links
            boolean r13 = r2.embed_links
            if (r11 == r13) goto L_0x0418
            if (r10 != 0) goto L_0x03fa
            r4.append(r15)
            r10 = 1
        L_0x03fa:
            r4.append(r15)
            boolean r11 = r2.embed_links
            if (r11 != 0) goto L_0x0404
            r11 = 43
            goto L_0x0406
        L_0x0404:
            r11 = 45
        L_0x0406:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625755(0x7f0e071b, float:1.8878727E38)
            java.lang.String r13 = "EventLogRestrictedSendEmbed"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x0418:
            boolean r11 = r1.change_info
            boolean r13 = r2.change_info
            if (r11 == r13) goto L_0x0442
            if (r10 != 0) goto L_0x0424
            r4.append(r15)
            r10 = 1
        L_0x0424:
            r4.append(r15)
            boolean r11 = r2.change_info
            if (r11 != 0) goto L_0x042e
            r11 = 43
            goto L_0x0430
        L_0x042e:
            r11 = 45
        L_0x0430:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625751(0x7f0e0717, float:1.8878719E38)
            java.lang.String r13 = "EventLogRestrictedChangeInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x0442:
            boolean r11 = r1.invite_users
            boolean r13 = r2.invite_users
            if (r11 == r13) goto L_0x046c
            if (r10 != 0) goto L_0x044e
            r4.append(r15)
            r10 = 1
        L_0x044e:
            r4.append(r15)
            boolean r11 = r2.invite_users
            if (r11 != 0) goto L_0x0458
            r11 = 43
            goto L_0x045a
        L_0x0458:
            r11 = 45
        L_0x045a:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625752(0x7f0e0718, float:1.887872E38)
            java.lang.String r13 = "EventLogRestrictedInviteUsers"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x046c:
            boolean r11 = r1.pin_messages
            boolean r13 = r2.pin_messages
            if (r11 == r13) goto L_0x0495
            if (r10 != 0) goto L_0x0477
            r4.append(r15)
        L_0x0477:
            r4.append(r15)
            boolean r11 = r2.pin_messages
            if (r11 != 0) goto L_0x0481
            r11 = 43
            goto L_0x0483
        L_0x0481:
            r11 = 45
        L_0x0483:
            r4.append(r11)
            r4.append(r14)
            r11 = 2131625753(0x7f0e0719, float:1.8878723E38)
            java.lang.String r13 = "EventLogRestrictedPinMessages"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r4.append(r11)
        L_0x0495:
            java.lang.String r11 = r4.toString()
            r6.messageText = r11
            r31 = r3
            r32 = r5
            r8 = r12
            goto L_0x16c0
        L_0x04a2:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan
            java.lang.String r10 = ", "
            java.lang.String r13 = "Hours"
            java.lang.String r2 = "Minutes"
            if (r0 == 0) goto L_0x080f
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan) r0
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message
            r4.<init>()
            r6.messageOwner = r4
            org.telegram.tgnet.TLRPC$ChannelParticipant r4 = r0.prev_participant
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer
            long r14 = getPeerId(r4)
            r19 = 0
            int r4 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1))
            if (r4 <= 0) goto L_0x04d8
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Long r11 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r11)
            r28 = r12
            goto L_0x04e9
        L_0x04d8:
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r28 = r12
            long r11 = -r14
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r11)
        L_0x04e9:
            org.telegram.tgnet.TLRPC$ChannelParticipant r11 = r0.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r11 = r11.banned_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r12 = r0.new_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r12 = r12.banned_rights
            r19 = r0
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x07d0
            if (r12 == 0) goto L_0x0510
            boolean r0 = r12.view_messages
            if (r0 == 0) goto L_0x0510
            if (r11 == 0) goto L_0x0508
            int r0 = r12.until_date
            r29 = r14
            int r14 = r11.until_date
            if (r0 == r14) goto L_0x050a
            goto L_0x0512
        L_0x0508:
            r29 = r14
        L_0x050a:
            r31 = r3
            r32 = r5
            goto L_0x07d6
        L_0x0510:
            r29 = r14
        L_0x0512:
            if (r12 == 0) goto L_0x05b5
            boolean r0 = org.telegram.messenger.AndroidUtilities.isBannedForever(r12)
            if (r0 != 0) goto L_0x05b5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r14 = r12.until_date
            int r15 = r7.date
            int r14 = r14 - r15
            int r15 = r14 / 60
            r20 = 60
            int r15 = r15 / 60
            int r15 = r15 / 24
            int r27 = r15 * 60
            int r27 = r27 * 60
            int r27 = r27 * 24
            int r14 = r14 - r27
            int r27 = r14 / 60
            int r8 = r27 / 60
            int r27 = r8 * 60
            int r27 = r27 * 60
            int r14 = r14 - r27
            r31 = r3
            int r3 = r14 / 60
            r20 = 0
            r27 = 0
            r42 = r27
            r27 = r14
            r14 = r42
        L_0x054c:
            r32 = r5
            r5 = 3
            if (r14 >= r5) goto L_0x05b0
            r18 = 0
            if (r14 != 0) goto L_0x0567
            if (r15 == 0) goto L_0x058c
            r5 = 0
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r5 = "Days"
            java.lang.String r18 = org.telegram.messenger.LocaleController.formatPluralString(r5, r15, r9)
            int r20 = r20 + 1
            r5 = r18
            r9 = r20
            goto L_0x0590
        L_0x0567:
            r5 = 1
            if (r14 != r5) goto L_0x057c
            if (r8 == 0) goto L_0x057a
            r5 = 0
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r18 = org.telegram.messenger.LocaleController.formatPluralString(r13, r8, r9)
            int r20 = r20 + 1
            r5 = r18
            r9 = r20
            goto L_0x0590
        L_0x057a:
            r5 = 0
            goto L_0x058c
        L_0x057c:
            r5 = 0
            if (r3 == 0) goto L_0x058c
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r18 = org.telegram.messenger.LocaleController.formatPluralString(r2, r3, r9)
            int r20 = r20 + 1
            r5 = r18
            r9 = r20
            goto L_0x0590
        L_0x058c:
            r5 = r18
            r9 = r20
        L_0x0590:
            if (r5 == 0) goto L_0x059e
            int r18 = r0.length()
            if (r18 <= 0) goto L_0x059b
            r0.append(r10)
        L_0x059b:
            r0.append(r5)
        L_0x059e:
            r18 = r0
            r0 = 2
            if (r9 != r0) goto L_0x05a4
            goto L_0x05b2
        L_0x05a4:
            r0 = r2
            int r14 = r14 + 1
            r20 = r9
            r0 = r18
            r5 = r32
            r9 = r48
            goto L_0x054c
        L_0x05b0:
            r18 = r0
        L_0x05b2:
            r0 = r18
            goto L_0x05c7
        L_0x05b5:
            r31 = r3
            r32 = r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r3 = 2131628840(0x7f0e1328, float:1.8884984E38)
            java.lang.String r5 = "UserRestrictionsUntilForever"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r0.<init>(r3)
        L_0x05c7:
            r3 = 2131625760(0x7f0e0720, float:1.8878737E38)
            java.lang.String r5 = "EventLogRestrictedUntil"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            int r1 = r3.indexOf(r1)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$Message r8 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            java.lang.String r8 = r6.getUserName(r4, r8, r1)
            r9 = 0
            r2[r9] = r8
            java.lang.String r8 = r0.toString()
            r9 = 1
            r2[r9] = r8
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r5.<init>(r2)
            r2 = r5
            r5 = 0
            if (r11 != 0) goto L_0x05fc
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r8 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r8.<init>()
            r11 = r8
        L_0x05fc:
            if (r12 != 0) goto L_0x0604
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r8 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r8.<init>()
            r12 = r8
        L_0x0604:
            boolean r8 = r11.view_messages
            boolean r9 = r12.view_messages
            if (r8 == r9) goto L_0x0630
            r8 = 10
            r2.append(r8)
            r5 = 1
            r2.append(r8)
            boolean r8 = r12.view_messages
            if (r8 != 0) goto L_0x061a
            r8 = 43
            goto L_0x061c
        L_0x061a:
            r8 = 45
        L_0x061c:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625754(0x7f0e071a, float:1.8878725E38)
            java.lang.String r9 = "EventLogRestrictedReadMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x0630:
            boolean r8 = r11.send_messages
            boolean r9 = r12.send_messages
            if (r8 == r9) goto L_0x0661
            if (r5 != 0) goto L_0x063f
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x0641
        L_0x063f:
            r8 = 10
        L_0x0641:
            r2.append(r8)
            boolean r8 = r12.send_messages
            if (r8 != 0) goto L_0x064b
            r8 = 43
            goto L_0x064d
        L_0x064b:
            r8 = 45
        L_0x064d:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625757(0x7f0e071d, float:1.887873E38)
            java.lang.String r9 = "EventLogRestrictedSendMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x0661:
            boolean r8 = r11.send_stickers
            boolean r9 = r12.send_stickers
            if (r8 != r9) goto L_0x0679
            boolean r8 = r11.send_inline
            boolean r9 = r12.send_inline
            if (r8 != r9) goto L_0x0679
            boolean r8 = r11.send_gifs
            boolean r9 = r12.send_gifs
            if (r8 != r9) goto L_0x0679
            boolean r8 = r11.send_games
            boolean r9 = r12.send_games
            if (r8 == r9) goto L_0x06a4
        L_0x0679:
            if (r5 != 0) goto L_0x0682
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x0684
        L_0x0682:
            r8 = 10
        L_0x0684:
            r2.append(r8)
            boolean r8 = r12.send_stickers
            if (r8 != 0) goto L_0x068e
            r8 = 43
            goto L_0x0690
        L_0x068e:
            r8 = 45
        L_0x0690:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625759(0x7f0e071f, float:1.8878735E38)
            java.lang.String r9 = "EventLogRestrictedSendStickers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x06a4:
            boolean r8 = r11.send_media
            boolean r9 = r12.send_media
            if (r8 == r9) goto L_0x06d5
            if (r5 != 0) goto L_0x06b3
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x06b5
        L_0x06b3:
            r8 = 10
        L_0x06b5:
            r2.append(r8)
            boolean r8 = r12.send_media
            if (r8 != 0) goto L_0x06bf
            r8 = 43
            goto L_0x06c1
        L_0x06bf:
            r8 = 45
        L_0x06c1:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625756(0x7f0e071c, float:1.8878729E38)
            java.lang.String r9 = "EventLogRestrictedSendMedia"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x06d5:
            boolean r8 = r11.send_polls
            boolean r9 = r12.send_polls
            if (r8 == r9) goto L_0x0706
            if (r5 != 0) goto L_0x06e4
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x06e6
        L_0x06e4:
            r8 = 10
        L_0x06e6:
            r2.append(r8)
            boolean r8 = r12.send_polls
            if (r8 != 0) goto L_0x06f0
            r8 = 43
            goto L_0x06f2
        L_0x06f0:
            r8 = 45
        L_0x06f2:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625758(0x7f0e071e, float:1.8878733E38)
            java.lang.String r9 = "EventLogRestrictedSendPolls"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x0706:
            boolean r8 = r11.embed_links
            boolean r9 = r12.embed_links
            if (r8 == r9) goto L_0x0737
            if (r5 != 0) goto L_0x0715
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x0717
        L_0x0715:
            r8 = 10
        L_0x0717:
            r2.append(r8)
            boolean r8 = r12.embed_links
            if (r8 != 0) goto L_0x0721
            r8 = 43
            goto L_0x0723
        L_0x0721:
            r8 = 45
        L_0x0723:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625755(0x7f0e071b, float:1.8878727E38)
            java.lang.String r9 = "EventLogRestrictedSendEmbed"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x0737:
            boolean r8 = r11.change_info
            boolean r9 = r12.change_info
            if (r8 == r9) goto L_0x0768
            if (r5 != 0) goto L_0x0746
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x0748
        L_0x0746:
            r8 = 10
        L_0x0748:
            r2.append(r8)
            boolean r8 = r12.change_info
            if (r8 != 0) goto L_0x0752
            r8 = 43
            goto L_0x0754
        L_0x0752:
            r8 = 45
        L_0x0754:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625751(0x7f0e0717, float:1.8878719E38)
            java.lang.String r9 = "EventLogRestrictedChangeInfo"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x0768:
            boolean r8 = r11.invite_users
            boolean r9 = r12.invite_users
            if (r8 == r9) goto L_0x0799
            if (r5 != 0) goto L_0x0777
            r8 = 10
            r2.append(r8)
            r5 = 1
            goto L_0x0779
        L_0x0777:
            r8 = 10
        L_0x0779:
            r2.append(r8)
            boolean r8 = r12.invite_users
            if (r8 != 0) goto L_0x0783
            r8 = 43
            goto L_0x0785
        L_0x0783:
            r8 = 45
        L_0x0785:
            r2.append(r8)
            r8 = 32
            r2.append(r8)
            r8 = 2131625752(0x7f0e0718, float:1.887872E38)
            java.lang.String r9 = "EventLogRestrictedInviteUsers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x0799:
            boolean r8 = r11.pin_messages
            boolean r9 = r12.pin_messages
            if (r8 == r9) goto L_0x07c9
            if (r5 != 0) goto L_0x07a7
            r8 = 10
            r2.append(r8)
            goto L_0x07a9
        L_0x07a7:
            r8 = 10
        L_0x07a9:
            r2.append(r8)
            boolean r8 = r12.pin_messages
            if (r8 != 0) goto L_0x07b3
            r10 = 43
            goto L_0x07b5
        L_0x07b3:
            r10 = 45
        L_0x07b5:
            r2.append(r10)
            r8 = 32
            r2.append(r8)
            r8 = 2131625753(0x7f0e0719, float:1.8878723E38)
            java.lang.String r9 = "EventLogRestrictedPinMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.append(r8)
        L_0x07c9:
            java.lang.String r8 = r2.toString()
            r6.messageText = r8
            goto L_0x0809
        L_0x07d0:
            r31 = r3
            r32 = r5
            r29 = r14
        L_0x07d6:
            if (r12 == 0) goto L_0x07e8
            if (r11 == 0) goto L_0x07de
            boolean r0 = r12.view_messages
            if (r0 == 0) goto L_0x07e8
        L_0x07de:
            r0 = 2131625679(0x7f0e06cf, float:1.8878573E38)
            java.lang.String r2 = "EventLogChannelRestricted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            goto L_0x07f1
        L_0x07e8:
            r0 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r2 = "EventLogChannelUnrestricted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
        L_0x07f1:
            int r1 = r0.indexOf(r1)
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            java.lang.String r2 = r6.getUserName(r4, r2, r1)
            r5 = 0
            r3[r5] = r2
            java.lang.String r2 = java.lang.String.format(r0, r3)
            r6.messageText = r2
        L_0x0809:
            r9 = r48
            r8 = r28
            goto L_0x16c0
        L_0x080f:
            r0 = r2
            r31 = r3
            r32 = r5
            r28 = r12
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned
            if (r1 == 0) goto L_0x08b5
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned) r0
            org.telegram.tgnet.TLRPC$Message r1 = r0.message
            if (r28 == 0) goto L_0x0882
            r8 = r28
            long r2 = r8.id
            r9 = 136817688(0x827aCLASSNAME, double:6.75969194E-316)
            int r5 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r5 != 0) goto L_0x0884
            org.telegram.tgnet.TLRPC$Message r2 = r0.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            if (r2 == 0) goto L_0x0884
            org.telegram.tgnet.TLRPC$Message r2 = r0.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            org.telegram.tgnet.TLRPC$Peer r2 = r2.from_id
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel
            if (r2 == 0) goto L_0x0884
            int r2 = r6.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            org.telegram.tgnet.TLRPC$Message r3 = r0.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            long r9 = r3.channel_id
            java.lang.Long r3 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r0.message
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r3 != 0) goto L_0x0872
            org.telegram.tgnet.TLRPC$Message r3 = r0.message
            boolean r3 = r3.pinned
            if (r3 != 0) goto L_0x0862
            goto L_0x0872
        L_0x0862:
            r3 = 2131625724(0x7f0e06fc, float:1.8878664E38)
            java.lang.String r5 = "EventLogPinnedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.CharSequence r3 = replaceWithLink(r3, r4, r2)
            r6.messageText = r3
            goto L_0x0881
        L_0x0872:
            r3 = 2131625775(0x7f0e072f, float:1.8878767E38)
            java.lang.String r5 = "EventLogUnpinnedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            java.lang.CharSequence r3 = replaceWithLink(r3, r4, r2)
            r6.messageText = r3
        L_0x0881:
            goto L_0x08b0
        L_0x0882:
            r8 = r28
        L_0x0884:
            org.telegram.tgnet.TLRPC$Message r2 = r0.message
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r2 != 0) goto L_0x08a1
            org.telegram.tgnet.TLRPC$Message r2 = r0.message
            boolean r2 = r2.pinned
            if (r2 != 0) goto L_0x0891
            goto L_0x08a1
        L_0x0891:
            r2 = 2131625724(0x7f0e06fc, float:1.8878664E38)
            java.lang.String r3 = "EventLogPinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            goto L_0x08b0
        L_0x08a1:
            r2 = 2131625775(0x7f0e072f, float:1.8878767E38)
            java.lang.String r3 = "EventLogUnpinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x08b0:
            r9 = r48
            r0 = r1
            goto L_0x16c2
        L_0x08b5:
            r8 = r28
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionStopPoll
            if (r1 == 0) goto L_0x08f7
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionStopPoll) r0
            org.telegram.tgnet.TLRPC$Message r1 = r0.message
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r2 == 0) goto L_0x08e3
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x08e3
            r2 = 2131625766(0x7f0e0726, float:1.887875E38)
            java.lang.String r3 = "EventLogStopQuiz"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            goto L_0x08f2
        L_0x08e3:
            r2 = 2131625765(0x7f0e0725, float:1.8878747E38)
            java.lang.String r3 = "EventLogStopPoll"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x08f2:
            r9 = r48
            r0 = r1
            goto L_0x16c2
        L_0x08f7:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures
            if (r1 == 0) goto L_0x092b
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures) r0
            boolean r0 = r0.new_value
            if (r0 == 0) goto L_0x0918
            r0 = 2131625772(0x7f0e072c, float:1.8878761E38)
            java.lang.String r1 = "EventLogToggledSignaturesOn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r9 = r48
            goto L_0x16c0
        L_0x0918:
            r0 = 2131625771(0x7f0e072b, float:1.887876E38)
            java.lang.String r1 = "EventLogToggledSignaturesOff"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r9 = r48
            goto L_0x16c0
        L_0x092b:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites
            if (r1 == 0) goto L_0x095f
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites) r0
            boolean r0 = r0.new_value
            if (r0 == 0) goto L_0x094c
            r0 = 2131625770(0x7f0e072a, float:1.8878757E38)
            java.lang.String r1 = "EventLogToggledInvitesOn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r9 = r48
            goto L_0x16c0
        L_0x094c:
            r0 = 2131625769(0x7f0e0729, float:1.8878755E38)
            java.lang.String r1 = "EventLogToggledInvitesOff"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r9 = r48
            goto L_0x16c0
        L_0x095f:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage
            if (r1 == 0) goto L_0x097e
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.message
            r1 = 2131625682(0x7f0e06d2, float:1.8878579E38)
            java.lang.String r2 = "EventLogDeletedMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            r9 = r48
            goto L_0x16c2
        L_0x097e:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat
            if (r1 == 0) goto L_0x0a33
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r0
            long r0 = r0.new_value
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r2
            long r2 = r2.prev_value
            r9 = r48
            boolean r5 = r9.megagroup
            if (r5 == 0) goto L_0x09e4
            r12 = 0
            int r5 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r5 != 0) goto L_0x09c0
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r10 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r10)
            r10 = 2131625746(0x7f0e0712, float:1.8878709E38)
            java.lang.String r12 = "EventLogRemovedLinkedChannel"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            java.lang.CharSequence r4 = replaceWithLink(r10, r4, r8)
            r6.messageText = r4
            java.lang.CharSequence r4 = replaceWithLink(r4, r11, r5)
            r6.messageText = r4
            goto L_0x0a31
        L_0x09c0:
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r10 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r10)
            r10 = 2131625673(0x7f0e06c9, float:1.887856E38)
            java.lang.String r12 = "EventLogChangedLinkedChannel"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            java.lang.CharSequence r4 = replaceWithLink(r10, r4, r8)
            r6.messageText = r4
            java.lang.CharSequence r4 = replaceWithLink(r4, r11, r5)
            r6.messageText = r4
            goto L_0x0a31
        L_0x09e4:
            r12 = 0
            int r5 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r5 != 0) goto L_0x0a0e
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r10 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r10)
            r10 = 2131625747(0x7f0e0713, float:1.887871E38)
            java.lang.String r12 = "EventLogRemovedLinkedGroup"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            java.lang.CharSequence r4 = replaceWithLink(r10, r4, r8)
            r6.messageText = r4
            java.lang.CharSequence r4 = replaceWithLink(r4, r11, r5)
            r6.messageText = r4
            goto L_0x0a31
        L_0x0a0e:
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r10 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r10)
            r10 = 2131625674(0x7f0e06ca, float:1.8878563E38)
            java.lang.String r12 = "EventLogChangedLinkedGroup"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r12, r10)
            java.lang.CharSequence r4 = replaceWithLink(r10, r4, r8)
            r6.messageText = r4
            java.lang.CharSequence r4 = replaceWithLink(r4, r11, r5)
            r6.messageText = r4
        L_0x0a31:
            goto L_0x16c0
        L_0x0a33:
            r9 = r48
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden
            if (r1 == 0) goto L_0x0a65
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden) r0
            boolean r0 = r0.new_value
            if (r0 == 0) goto L_0x0a54
            r0 = 2131625767(0x7f0e0727, float:1.8878751E38)
            java.lang.String r1 = "EventLogToggledInvitesHistoryOff"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            goto L_0x16c0
        L_0x0a54:
            r0 = 2131625768(0x7f0e0728, float:1.8878753E38)
            java.lang.String r1 = "EventLogToggledInvitesHistoryOn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            goto L_0x16c0
        L_0x0a65:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout
            if (r1 == 0) goto L_0x0b08
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x0a75
            r0 = 2131625689(0x7f0e06d9, float:1.8878593E38)
            java.lang.String r1 = "EventLogEditedGroupDescription"
            goto L_0x0a7a
        L_0x0a75:
            r0 = 2131625684(0x7f0e06d4, float:1.8878583E38)
            java.lang.String r1 = "EventLogEditedChannelDescription"
        L_0x0a7a:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            org.telegram.tgnet.TLRPC$TL_message r0 = new org.telegram.tgnet.TLRPC$TL_message
            r0.<init>()
            r1 = 0
            r0.out = r1
            r0.unread = r1
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r1.<init>()
            r0.from_id = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.from_id
            long r2 = r7.user_id
            r1.user_id = r2
            r5 = r32
            r0.peer_id = r5
            int r1 = r7.date
            r0.date = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r1
            java.lang.String r1 = r1.new_value
            r0.message = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r1
            java.lang.String r1 = r1.prev_value
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0af9
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1.<init>()
            r0.media = r1
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$TL_webPage r2 = new org.telegram.tgnet.TLRPC$TL_webPage
            r2.<init>()
            r1.webpage = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r2 = 10
            r1.flags = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r3 = r31
            r1.display_url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r1.url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r2 = 2131625725(0x7f0e06fd, float:1.8878666E38)
            java.lang.String r4 = "EventLogPreviousGroupDescription"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.site_name = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r2
            java.lang.String r2 = r2.prev_value
            r1.description = r2
            goto L_0x0b02
        L_0x0af9:
            r3 = r31
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r0.media = r1
        L_0x0b02:
            r31 = r3
            r32 = r5
            goto L_0x16c2
        L_0x0b08:
            r3 = r31
            r5 = r32
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTheme
            if (r1 == 0) goto L_0x0ba6
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x0b1c
            r0 = 2131625691(0x7f0e06db, float:1.8878597E38)
            java.lang.String r1 = "EventLogEditedGroupTheme"
            goto L_0x0b21
        L_0x0b1c:
            r0 = 2131625686(0x7f0e06d6, float:1.8878587E38)
            java.lang.String r1 = "EventLogEditedChannelTheme"
        L_0x0b21:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            org.telegram.tgnet.TLRPC$TL_message r0 = new org.telegram.tgnet.TLRPC$TL_message
            r0.<init>()
            r1 = 0
            r0.out = r1
            r0.unread = r1
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r1.<init>()
            r0.from_id = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.from_id
            long r10 = r7.user_id
            r1.user_id = r10
            r0.peer_id = r5
            int r1 = r7.date
            r0.date = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTheme) r1
            java.lang.String r1 = r1.new_value
            r0.message = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTheme) r1
            java.lang.String r1 = r1.prev_value
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b9d
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1.<init>()
            r0.media = r1
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$TL_webPage r2 = new org.telegram.tgnet.TLRPC$TL_webPage
            r2.<init>()
            r1.webpage = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r2 = 10
            r1.flags = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r1.display_url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r1.url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r2 = 2131625726(0x7f0e06fe, float:1.8878668E38)
            java.lang.String r4 = "EventLogPreviousGroupTheme"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.site_name = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTheme) r2
            java.lang.String r2 = r2.prev_value
            r1.description = r2
            goto L_0x0b02
        L_0x0b9d:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r0.media = r1
            goto L_0x0b02
        L_0x0ba6:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername
            if (r1 == 0) goto L_0x0cc4
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r0
            java.lang.String r0 = r0.new_value
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0bd2
            boolean r1 = r9.megagroup
            if (r1 == 0) goto L_0x0bc2
            r1 = 2131625672(0x7f0e06c8, float:1.8878559E38)
            java.lang.String r2 = "EventLogChangedGroupLink"
            goto L_0x0bc7
        L_0x0bc2:
            r1 = 2131625671(0x7f0e06c7, float:1.8878557E38)
            java.lang.String r2 = "EventLogChangedChannelLink"
        L_0x0bc7:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            goto L_0x0beb
        L_0x0bd2:
            boolean r1 = r9.megagroup
            if (r1 == 0) goto L_0x0bdc
            r1 = 2131625745(0x7f0e0711, float:1.8878707E38)
            java.lang.String r2 = "EventLogRemovedGroupLink"
            goto L_0x0be1
        L_0x0bdc:
            r1 = 2131625743(0x7f0e070f, float:1.8878703E38)
            java.lang.String r2 = "EventLogRemovedChannelLink"
        L_0x0be1:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
        L_0x0beb:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            r2 = 0
            r1.out = r2
            r1.unread = r2
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r1.from_id = r2
            org.telegram.tgnet.TLRPC$Peer r2 = r1.from_id
            long r10 = r7.user_id
            r2.user_id = r10
            r1.peer_id = r5
            int r2 = r7.date
            r1.date = r2
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "https://"
            r2.append(r4)
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.String r4 = r4.linkPrefix
            r2.append(r4)
            java.lang.String r4 = "/"
            r2.append(r4)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            r1.message = r2
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1.message = r3
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_messageEntityUrl r2 = new org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            r2.<init>()
            r4 = 0
            r2.offset = r4
            java.lang.String r4 = r1.message
            int r4 = r4.length()
            r2.length = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r1.entities
            r4.add(r2)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r4 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r4 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r4
            java.lang.String r4 = r4.prev_value
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0cb6
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r4.<init>()
            r1.media = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_webPage r10 = new org.telegram.tgnet.TLRPC$TL_webPage
            r10.<init>()
            r4.webpage = r10
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r10 = 10
            r4.flags = r10
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r4.display_url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r4.url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r10 = 2131625727(0x7f0e06ff, float:1.887867E38)
            java.lang.String r11 = "EventLogPreviousLink"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.site_name = r10
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "https://"
            r10.append(r11)
            int r11 = r6.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            java.lang.String r11 = r11.linkPrefix
            r10.append(r11)
            java.lang.String r11 = "/"
            r10.append(r11)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r11 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r11 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r11
            java.lang.String r11 = r11.prev_value
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r4.description = r10
            goto L_0x0cbd
        L_0x0cb6:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r4.<init>()
            r1.media = r4
        L_0x0cbd:
            r0 = r1
            r31 = r3
            r32 = r5
            goto L_0x16c2
        L_0x0cc4:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage
            if (r1 == 0) goto L_0x0e5e
            org.telegram.tgnet.TLRPC$TL_message r0 = new org.telegram.tgnet.TLRPC$TL_message
            r0.<init>()
            r1 = 0
            r0.out = r1
            r0.unread = r1
            r0.peer_id = r5
            int r1 = r7.date
            r0.date = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.new_message
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r2
            org.telegram.tgnet.TLRPC$Message r2 = r2.prev_message
            if (r1 == 0) goto L_0x0cf1
            org.telegram.tgnet.TLRPC$Peer r10 = r1.from_id
            if (r10 == 0) goto L_0x0cf1
            org.telegram.tgnet.TLRPC$Peer r10 = r1.from_id
            r0.from_id = r10
            goto L_0x0cfe
        L_0x0cf1:
            org.telegram.tgnet.TLRPC$TL_peerUser r10 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r10.<init>()
            r0.from_id = r10
            org.telegram.tgnet.TLRPC$Peer r10 = r0.from_id
            long r11 = r7.user_id
            r10.user_id = r11
        L_0x0cfe:
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r1.media
            if (r10 == 0) goto L_0x0dd9
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r1.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty
            if (r10 != 0) goto L_0x0dd9
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r1.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r10 != 0) goto L_0x0dd9
            java.lang.String r10 = r1.message
            java.lang.String r11 = r2.message
            boolean r10 = android.text.TextUtils.equals(r10, r11)
            if (r10 != 0) goto L_0x0d1a
            r10 = 1
            goto L_0x0d1b
        L_0x0d1a:
            r10 = 0
        L_0x0d1b:
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r1.media
            java.lang.Class r11 = r11.getClass()
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r2.media
            java.lang.Class r12 = r12.getClass()
            if (r11 != r12) goto L_0x0d64
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r1.media
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            if (r11 == 0) goto L_0x0d45
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r2.media
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            if (r11 == 0) goto L_0x0d45
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r1.media
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            long r11 = r11.id
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r2.media
            org.telegram.tgnet.TLRPC$Photo r13 = r13.photo
            long r13 = r13.id
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 != 0) goto L_0x0d64
        L_0x0d45:
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r1.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            if (r11 == 0) goto L_0x0d62
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r2.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            if (r11 == 0) goto L_0x0d62
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r1.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            long r11 = r11.id
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r2.media
            org.telegram.tgnet.TLRPC$Document r13 = r13.document
            long r13 = r13.id
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 == 0) goto L_0x0d62
            goto L_0x0d64
        L_0x0d62:
            r11 = 0
            goto L_0x0d65
        L_0x0d64:
            r11 = 1
        L_0x0d65:
            if (r11 == 0) goto L_0x0d79
            if (r10 == 0) goto L_0x0d79
            r12 = 2131625695(0x7f0e06df, float:1.8878605E38)
            java.lang.String r13 = "EventLogEditedMediaCaption"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.CharSequence r4 = replaceWithLink(r12, r4, r8)
            r6.messageText = r4
            goto L_0x0d9a
        L_0x0d79:
            if (r10 == 0) goto L_0x0d8b
            r12 = 2131625683(0x7f0e06d3, float:1.887858E38)
            java.lang.String r13 = "EventLogEditedCaption"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.CharSequence r4 = replaceWithLink(r12, r4, r8)
            r6.messageText = r4
            goto L_0x0d9a
        L_0x0d8b:
            r12 = 2131625694(0x7f0e06de, float:1.8878603E38)
            java.lang.String r13 = "EventLogEditedMedia"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.CharSequence r4 = replaceWithLink(r12, r4, r8)
            r6.messageText = r4
        L_0x0d9a:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            r0.media = r4
            if (r10 == 0) goto L_0x0dd8
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$TL_webPage r12 = new org.telegram.tgnet.TLRPC$TL_webPage
            r12.<init>()
            r4.webpage = r12
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r12 = 2131625721(0x7f0e06f9, float:1.8878658E38)
            java.lang.String r13 = "EventLogOriginalCaption"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r4.site_name = r12
            java.lang.String r4 = r2.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0dd0
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r12 = 2131625722(0x7f0e06fa, float:1.887866E38)
            java.lang.String r13 = "EventLogOriginalCaptionEmpty"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r4.description = r12
            goto L_0x0dd8
        L_0x0dd0:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r12 = r2.message
            r4.description = r12
        L_0x0dd8:
            goto L_0x0e3a
        L_0x0dd9:
            r10 = 2131625696(0x7f0e06e0, float:1.8878607E38)
            java.lang.String r11 = "EventLogEditedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            java.lang.CharSequence r4 = replaceWithLink(r10, r4, r8)
            r6.messageText = r4
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCall
            if (r4 == 0) goto L_0x0df7
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r4.<init>()
            r0.media = r4
            goto L_0x0e3a
        L_0x0df7:
            java.lang.String r4 = r1.message
            r0.message = r4
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r4.<init>()
            r0.media = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$TL_webPage r10 = new org.telegram.tgnet.TLRPC$TL_webPage
            r10.<init>()
            r4.webpage = r10
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r10 = 2131625723(0x7f0e06fb, float:1.8878662E38)
            java.lang.String r11 = "EventLogOriginalMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.site_name = r10
            java.lang.String r4 = r2.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0e32
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r10 = 2131625722(0x7f0e06fa, float:1.887866E38)
            java.lang.String r11 = "EventLogOriginalCaptionEmpty"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.description = r10
            goto L_0x0e3a
        L_0x0e32:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r10 = r2.message
            r4.description = r10
        L_0x0e3a:
            org.telegram.tgnet.TLRPC$ReplyMarkup r4 = r1.reply_markup
            r0.reply_markup = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            if (r4 == 0) goto L_0x0e58
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r10 = 10
            r4.flags = r10
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r4.display_url = r3
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r4.url = r3
        L_0x0e58:
            r31 = r3
            r32 = r5
            goto L_0x16c2
        L_0x0e5e:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet
            if (r1 == 0) goto L_0x0e9c
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r0
            org.telegram.tgnet.TLRPC$InputStickerSet r0 = r0.new_stickerset
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r1
            org.telegram.tgnet.TLRPC$InputStickerSet r1 = r1.new_stickerset
            if (r0 == 0) goto L_0x0e87
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty
            if (r2 == 0) goto L_0x0e77
            goto L_0x0e87
        L_0x0e77:
            r2 = 2131625677(0x7f0e06cd, float:1.8878569E38)
            java.lang.String r10 = "EventLogChangedStickersSet"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            goto L_0x0e96
        L_0x0e87:
            r2 = 2131625749(0x7f0e0715, float:1.8878715E38)
            java.lang.String r10 = "EventLogRemovedStickersSet"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x0e96:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0e9c:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation
            if (r1 == 0) goto L_0x0edd
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation) r0
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r0.new_value
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelLocationEmpty
            if (r1 == 0) goto L_0x0ebc
            r1 = 2131625748(0x7f0e0714, float:1.8878713E38)
            java.lang.String r2 = "EventLogRemovedLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            goto L_0x0ed7
        L_0x0ebc:
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r0.new_value
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r1
            r2 = 2131625675(0x7f0e06cb, float:1.8878565E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.String r10 = r1.address
            r12 = 0
            r11[r12] = r10
            java.lang.String r10 = "EventLogChangedLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r10, r2, r11)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x0ed7:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0edd:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode
            r12 = 3600(0xe10, float:5.045E-42)
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode) r1
            int r2 = r1.new_value
            if (r2 != 0) goto L_0x0efd
            r0 = 2131625773(0x7f0e072d, float:1.8878763E38)
            java.lang.String r2 = "EventLogToggledSlowmodeOff"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0efd:
            int r2 = r1.new_value
            r10 = 60
            if (r2 >= r10) goto L_0x0f0f
            int r0 = r1.new_value
            r2 = 0
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r11 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r11, r0, r10)
            goto L_0x0f2c
        L_0x0f0f:
            r2 = 0
            int r10 = r1.new_value
            if (r10 >= r12) goto L_0x0var_
            int r10 = r1.new_value
            r11 = 60
            int r10 = r10 / r11
            java.lang.Object[] r11 = new java.lang.Object[r2]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r10, r11)
            goto L_0x0f2c
        L_0x0var_:
            r11 = 60
            int r0 = r1.new_value
            int r0 = r0 / r11
            int r0 = r0 / r11
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r13, r0, r10)
        L_0x0f2c:
            r10 = 2131625774(0x7f0e072e, float:1.8878765E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            r12[r2] = r0
            java.lang.String r2 = "EventLogToggledSlowmodeOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r10, r12)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x0var_:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0var_:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionStartGroupCall
            if (r1 == 0) goto L_0x0var_
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r48)
            if (r0 == 0) goto L_0x0f6f
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x0f5a
            boolean r0 = r9.gigagroup
            if (r0 == 0) goto L_0x0f6f
        L_0x0f5a:
            r0 = 2131625763(0x7f0e0723, float:1.8878743E38)
            java.lang.String r1 = "EventLogStartedLiveStream"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0f6f:
            r0 = 2131625764(0x7f0e0724, float:1.8878745E38)
            java.lang.String r1 = "EventLogStartedVoiceChat"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0var_:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDiscardGroupCall
            if (r1 == 0) goto L_0x0fc2
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r48)
            if (r0 == 0) goto L_0x0fad
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x0var_
            boolean r0 = r9.gigagroup
            if (r0 == 0) goto L_0x0fad
        L_0x0var_:
            r0 = 2131625701(0x7f0e06e5, float:1.8878617E38)
            java.lang.String r1 = "EventLogEndedLiveStream"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0fad:
            r0 = 2131625702(0x7f0e06e6, float:1.887862E38)
            java.lang.String r1 = "EventLogEndedVoiceChat"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x0fc2:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantMute
            if (r1 == 0) goto L_0x1013
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantMute) r0
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r12 = 0
            int r10 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r10 <= 0) goto L_0x0fe9
            int r10 = r6.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            java.lang.Long r12 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r12)
            goto L_0x0ff8
        L_0x0fe9:
            int r10 = r6.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            long r12 = -r1
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r12)
        L_0x0ff8:
            r12 = 2131625777(0x7f0e0731, float:1.8878772E38)
            java.lang.String r13 = "EventLogVoiceChatMuted"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.CharSequence r4 = replaceWithLink(r12, r4, r8)
            r6.messageText = r4
            java.lang.CharSequence r4 = replaceWithLink(r4, r11, r10)
            r6.messageText = r4
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1013:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantUnmute
            if (r1 == 0) goto L_0x1064
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantUnmute) r0
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r12 = 0
            int r10 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r10 <= 0) goto L_0x103a
            int r10 = r6.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            java.lang.Long r12 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r12)
            goto L_0x1049
        L_0x103a:
            int r10 = r6.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            long r12 = -r1
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r12)
        L_0x1049:
            r12 = 2131625779(0x7f0e0733, float:1.8878776E38)
            java.lang.String r13 = "EventLogVoiceChatUnmuted"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            java.lang.CharSequence r4 = replaceWithLink(r12, r4, r8)
            r6.messageText = r4
            java.lang.CharSequence r4 = replaceWithLink(r4, r11, r10)
            r6.messageText = r4
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1064:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleGroupCallSetting
            if (r1 == 0) goto L_0x1097
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleGroupCallSetting) r0
            boolean r1 = r0.join_muted
            if (r1 == 0) goto L_0x1082
            r1 = 2131625778(0x7f0e0732, float:1.8878774E38)
            java.lang.String r2 = "EventLogVoiceChatNotAllowedToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            goto L_0x1091
        L_0x1082:
            r1 = 2131625776(0x7f0e0730, float:1.887877E38)
            java.lang.String r2 = "EventLogVoiceChatAllowedToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
        L_0x1091:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1097:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoinByInvite
            if (r1 == 0) goto L_0x10b6
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoinByInvite) r0
            r1 = 2131624184(0x7f0e00f8, float:1.887554E38)
            java.lang.String r2 = "ActionInviteUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x10b6:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleNoForwards
            if (r1 == 0) goto L_0x111a
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleNoForwards) r0
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r48)
            if (r1 == 0) goto L_0x10cc
            boolean r1 = r9.megagroup
            if (r1 != 0) goto L_0x10cc
            r1 = 1
            goto L_0x10cd
        L_0x10cc:
            r1 = 0
        L_0x10cd:
            boolean r2 = r0.new_value
            if (r2 == 0) goto L_0x10f3
            if (r1 == 0) goto L_0x10e3
            r2 = 2131624172(0x7f0e00ec, float:1.8875516E38)
            java.lang.String r10 = "ActionForwardsRestrictedChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            goto L_0x1114
        L_0x10e3:
            r2 = 2131624173(0x7f0e00ed, float:1.8875518E38)
            java.lang.String r10 = "ActionForwardsRestrictedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            goto L_0x1114
        L_0x10f3:
            if (r1 == 0) goto L_0x1105
            r2 = 2131624170(0x7f0e00ea, float:1.8875512E38)
            java.lang.String r10 = "ActionForwardsEnabledChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            goto L_0x1114
        L_0x1105:
            r2 = 2131624171(0x7f0e00eb, float:1.8875514E38)
            java.lang.String r10 = "ActionForwardsEnabledGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x1114:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x111a:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionExportedInviteDelete
            if (r1 == 0) goto L_0x1144
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionExportedInviteDelete) r0
            r1 = 2131624167(0x7f0e00e7, float:1.8875506E38)
            r2 = 0
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r2 = "ActionDeletedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r11, r2)
            r6.messageText = r1
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1144:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke
            if (r1 == 0) goto L_0x1175
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke) r0
            r1 = 2131624209(0x7f0e0111, float:1.8875591E38)
            r2 = 1
            java.lang.Object[] r10 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.invite
            java.lang.String r2 = r2.link
            r12 = 0
            r10[r12] = r2
            java.lang.String r2 = "ActionRevokedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r11, r2)
            r6.messageText = r1
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1175:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionExportedInviteEdit
            if (r1 == 0) goto L_0x11d2
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionExportedInviteEdit) r0
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.prev_invite
            java.lang.String r1 = r1.link
            if (r1 == 0) goto L_0x11a6
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.prev_invite
            java.lang.String r1 = r1.link
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.new_invite
            java.lang.String r2 = r2.link
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x11a6
            r1 = 2131624169(0x7f0e00e9, float:1.887551E38)
            r2 = 0
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r2 = "ActionEditedInviteLinkToSameClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            goto L_0x11b8
        L_0x11a6:
            r1 = 2131624168(0x7f0e00e8, float:1.8875508E38)
            r2 = 0
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r2 = "ActionEditedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r10)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
        L_0x11b8:
            java.lang.CharSequence r1 = r6.messageText
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.prev_invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r11, r2)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r0.new_invite
            java.lang.String r4 = "un3"
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r2)
            r6.messageText = r1
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x11d2:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantVolume
            if (r1 == 0) goto L_0x1250
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantVolume) r0
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r12 = 0
            int r10 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r10 <= 0) goto L_0x11f9
            int r10 = r6.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            java.lang.Long r12 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r12)
            goto L_0x1208
        L_0x11f9:
            int r10 = r6.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            long r12 = -r1
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r12)
        L_0x1208:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r0.participant
            int r12 = org.telegram.messenger.ChatObject.getParticipantVolume(r12)
            double r12 = (double) r12
            r14 = 4636737291354636288(0xNUM, double:100.0)
            java.lang.Double.isNaN(r12)
            double r12 = r12 / r14
            r15 = 1
            java.lang.Object[] r14 = new java.lang.Object[r15]
            r19 = 0
            int r15 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1))
            if (r15 <= 0) goto L_0x1228
            r15 = r0
            r19 = r1
            r0 = 4607182418800017408(0x3ffNUM, double:1.0)
            double r0 = java.lang.Math.max(r12, r0)
            goto L_0x122d
        L_0x1228:
            r15 = r0
            r19 = r1
            r0 = 0
        L_0x122d:
            int r0 = (int) r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1 = 0
            r14[r1] = r0
            java.lang.String r0 = "ActionVolumeChanged"
            r1 = 2131624225(0x7f0e0121, float:1.8875624E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r14)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r11, r10)
            r6.messageText = r0
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1250:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL
            if (r1 == 0) goto L_0x12f5
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL) r1
            boolean r2 = r9.megagroup
            if (r2 != 0) goto L_0x1287
            int r0 = r1.new_value
            if (r0 == 0) goto L_0x127b
            r0 = 2131624211(0x7f0e0113, float:1.8875595E38)
            r2 = 1
            java.lang.Object[] r4 = new java.lang.Object[r2]
            int r2 = r1.new_value
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r10 = 0
            r4[r10] = r2
            java.lang.String r2 = "ActionTTLChannelChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r4)
            r6.messageText = r0
            goto L_0x12ef
        L_0x127b:
            r0 = 2131624212(0x7f0e0114, float:1.8875597E38)
            java.lang.String r2 = "ActionTTLChannelDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r6.messageText = r0
            goto L_0x12ef
        L_0x1287:
            int r2 = r1.new_value
            if (r2 != 0) goto L_0x129b
            r0 = 2131624213(0x7f0e0115, float:1.88756E38)
            java.lang.String r2 = "ActionTTLDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r4, r8)
            r6.messageText = r0
            goto L_0x12ef
        L_0x129b:
            int r2 = r1.new_value
            r10 = 86400(0x15180, float:1.21072E-40)
            if (r2 <= r10) goto L_0x12b2
            int r0 = r1.new_value
            r2 = 86400(0x15180, float:1.21072E-40)
            int r0 = r0 / r2
            r2 = 0
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r11 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r11, r0, r10)
            goto L_0x12db
        L_0x12b2:
            r2 = 0
            int r10 = r1.new_value
            if (r10 < r12) goto L_0x12c1
            int r0 = r1.new_value
            int r0 = r0 / r12
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r13, r0, r10)
            goto L_0x12db
        L_0x12c1:
            int r10 = r1.new_value
            r11 = 60
            if (r10 < r11) goto L_0x12d1
            int r10 = r1.new_value
            int r10 = r10 / r11
            java.lang.Object[] r11 = new java.lang.Object[r2]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r10, r11)
            goto L_0x12db
        L_0x12d1:
            int r0 = r1.new_value
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r11 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r11, r0, r10)
        L_0x12db:
            r10 = 2131624210(0x7f0e0112, float:1.8875593E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            r12[r2] = r0
            java.lang.String r2 = "ActionTTLChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r10, r12)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
        L_0x12ef:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x12f5:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoinByRequest
            if (r0 == 0) goto L_0x1374
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoinByRequest) r0
            org.telegram.tgnet.TLRPC$ExportedChatInvite r1 = r0.invite
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatInviteExported
            if (r1 == 0) goto L_0x1313
            org.telegram.tgnet.TLRPC$ExportedChatInvite r1 = r0.invite
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = (org.telegram.tgnet.TLRPC.TL_chatInviteExported) r1
            java.lang.String r1 = r1.link
            java.lang.String r2 = "https://t.me/+PublicChat"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x1319
        L_0x1313:
            org.telegram.tgnet.TLRPC$ExportedChatInvite r1 = r0.invite
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatInvitePublicJoinRequests
            if (r1 == 0) goto L_0x133f
        L_0x1319:
            r1 = 2131626333(0x7f0e095d, float:1.88799E38)
            java.lang.String r2 = "JoinedViaRequestApproved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            int r2 = r6.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r12 = r0.approved_by
            java.lang.Long r4 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r11, r2)
            r6.messageText = r1
            goto L_0x136e
        L_0x133f:
            r1 = 2131626332(0x7f0e095c, float:1.8879897E38)
            java.lang.String r2 = "JoinedViaInviteLinkApproved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$ExportedChatInvite r2 = r0.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r11, r2)
            r6.messageText = r1
            int r2 = r6.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r10 = r0.approved_by
            java.lang.Long r4 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            java.lang.String r4 = "un3"
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r2)
            r6.messageText = r1
        L_0x136e:
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x1374:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionSendMessage
            if (r0 == 0) goto L_0x1395
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionSendMessage) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.message
            r1 = 2131625762(0x7f0e0722, float:1.8878741E38)
            java.lang.String r2 = "EventLogSendMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r4, r8)
            r6.messageText = r1
            r31 = r3
            r32 = r5
            goto L_0x16c2
        L_0x1395:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAvailableReactions
            if (r0 == 0) goto L_0x13cd
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAvailableReactions) r0
            java.util.ArrayList<java.lang.String> r0 = r0.prev_value
            java.lang.String r0 = android.text.TextUtils.join(r10, r0)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions r1 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAvailableReactions) r1
            java.util.ArrayList<java.lang.String> r1 = r1.new_value
            java.lang.String r1 = android.text.TextUtils.join(r10, r1)
            r10 = 2131624207(0x7f0e010f, float:1.8875587E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r11 = 0
            r2[r11] = r0
            r11 = 1
            r2[r11] = r1
            java.lang.String r11 = "ActionReactionsChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r10, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r4, r8)
            r6.messageText = r2
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x13cd:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "unsupported "
            r0.append(r1)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r6.messageText = r0
            r31 = r3
            r32 = r5
            goto L_0x16c0
        L_0x13e8:
            r8 = r12
        L_0x13e9:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin
            if (r0 == 0) goto L_0x13f8
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin) r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r0.prev_participant
            org.telegram.tgnet.TLRPC$ChannelParticipant r0 = r0.new_participant
            goto L_0x1401
        L_0x13f8:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r0 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r0 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan) r0
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r0.prev_participant
            org.telegram.tgnet.TLRPC$ChannelParticipant r4 = r0.new_participant
            r0 = r4
        L_0x1401:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message
            r4.<init>()
            r6.messageOwner = r4
            org.telegram.tgnet.TLRPC$Peer r4 = r2.peer
            long r10 = getPeerId(r4)
            r12 = 0
            int r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r4 <= 0) goto L_0x1423
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Long r12 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r12)
            goto L_0x1432
        L_0x1423:
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r12 = -r10
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r12)
        L_0x1432:
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator
            if (r12 != 0) goto L_0x1468
            boolean r12 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator
            if (r12 == 0) goto L_0x1468
            r12 = 2131625676(0x7f0e06cc, float:1.8878567E38)
            java.lang.String r13 = "EventLogChangedOwnership"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            int r1 = r12.indexOf(r1)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            org.telegram.tgnet.TLRPC$Message r14 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r14 = r14.entities
            java.lang.String r14 = r6.getUserName(r4, r14, r1)
            r18 = 0
            r15[r18] = r14
            java.lang.String r14 = java.lang.String.format(r12, r15)
            r13.<init>(r14)
            r1 = r13
            r19 = r0
            r31 = r3
            r32 = r5
            goto L_0x16b9
        L_0x1468:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r12 = r2.admin_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r13 = r0.admin_rights
            if (r12 != 0) goto L_0x1474
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r14 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r14.<init>()
            r12 = r14
        L_0x1474:
            if (r13 != 0) goto L_0x147c
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r14 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r14.<init>()
            r13 = r14
        L_0x147c:
            boolean r14 = r13.other
            if (r14 == 0) goto L_0x148a
            r14 = 2131625737(0x7f0e0709, float:1.887869E38)
            java.lang.String r15 = "EventLogPromotedNoRights"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            goto L_0x1493
        L_0x148a:
            r14 = 2131625728(0x7f0e0700, float:1.8878672E38)
            java.lang.String r15 = "EventLogPromoted"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
        L_0x1493:
            int r1 = r14.indexOf(r1)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r31 = r3
            r32 = r5
            r3 = 1
            java.lang.Object[] r5 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$Message r3 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.entities
            java.lang.String r3 = r6.getUserName(r4, r3, r1)
            r18 = 0
            r5[r18] = r3
            java.lang.String r3 = java.lang.String.format(r14, r5)
            r15.<init>(r3)
            r3 = r15
            java.lang.String r5 = "\n"
            r3.append(r5)
            java.lang.String r5 = r2.rank
            java.lang.String r15 = r0.rank
            boolean r5 = android.text.TextUtils.equals(r5, r15)
            if (r5 != 0) goto L_0x1510
            java.lang.String r5 = r0.rank
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x14e9
            r5 = 10
            r3.append(r5)
            r15 = 45
            r3.append(r15)
            r15 = 32
            r3.append(r15)
            r15 = 2131625740(0x7f0e070c, float:1.8878696E38)
            java.lang.String r5 = "EventLogPromotedRemovedTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r15)
            r3.append(r5)
            r19 = r0
            goto L_0x1512
        L_0x14e9:
            r5 = 10
            r3.append(r5)
            r5 = 43
            r3.append(r5)
            r15 = 32
            r3.append(r15)
            r5 = 1
            java.lang.Object[] r15 = new java.lang.Object[r5]
            java.lang.String r5 = r0.rank
            r19 = 0
            r15[r19] = r5
            java.lang.String r5 = "EventLogPromotedTitle"
            r19 = r0
            r0 = 2131625742(0x7f0e070e, float:1.88787E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r0, r15)
            r3.append(r0)
            goto L_0x1512
        L_0x1510:
            r19 = r0
        L_0x1512:
            boolean r0 = r12.change_info
            boolean r5 = r13.change_info
            if (r0 == r5) goto L_0x1544
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.change_info
            if (r0 == 0) goto L_0x1524
            r0 = 43
            goto L_0x1526
        L_0x1524:
            r0 = 45
        L_0x1526:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x1538
            r0 = 2131625733(0x7f0e0705, float:1.8878682E38)
            java.lang.String r5 = "EventLogPromotedChangeGroupInfo"
            goto L_0x153d
        L_0x1538:
            r0 = 2131625732(0x7f0e0704, float:1.887868E38)
            java.lang.String r5 = "EventLogPromotedChangeChannelInfo"
        L_0x153d:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x1544:
            boolean r0 = r9.megagroup
            if (r0 != 0) goto L_0x1598
            boolean r0 = r12.post_messages
            boolean r5 = r13.post_messages
            if (r0 == r5) goto L_0x1570
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.post_messages
            if (r0 == 0) goto L_0x155a
            r0 = 43
            goto L_0x155c
        L_0x155a:
            r0 = 45
        L_0x155c:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625739(0x7f0e070b, float:1.8878694E38)
            java.lang.String r5 = "EventLogPromotedPostMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x1570:
            boolean r0 = r12.edit_messages
            boolean r5 = r13.edit_messages
            if (r0 == r5) goto L_0x1598
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.edit_messages
            if (r0 == 0) goto L_0x1582
            r0 = 43
            goto L_0x1584
        L_0x1582:
            r0 = 45
        L_0x1584:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625735(0x7f0e0707, float:1.8878686E38)
            java.lang.String r5 = "EventLogPromotedEditMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x1598:
            boolean r0 = r12.delete_messages
            boolean r5 = r13.delete_messages
            if (r0 == r5) goto L_0x15c0
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.delete_messages
            if (r0 == 0) goto L_0x15aa
            r0 = 43
            goto L_0x15ac
        L_0x15aa:
            r0 = 45
        L_0x15ac:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625734(0x7f0e0706, float:1.8878684E38)
            java.lang.String r5 = "EventLogPromotedDeleteMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x15c0:
            boolean r0 = r12.add_admins
            boolean r5 = r13.add_admins
            if (r0 == r5) goto L_0x15e8
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.add_admins
            if (r0 == 0) goto L_0x15d2
            r0 = 43
            goto L_0x15d4
        L_0x15d2:
            r0 = 45
        L_0x15d4:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625729(0x7f0e0701, float:1.8878674E38)
            java.lang.String r5 = "EventLogPromotedAddAdmins"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x15e8:
            boolean r0 = r12.anonymous
            boolean r5 = r13.anonymous
            if (r0 == r5) goto L_0x1610
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.anonymous
            if (r0 == 0) goto L_0x15fa
            r0 = 43
            goto L_0x15fc
        L_0x15fa:
            r0 = 45
        L_0x15fc:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625741(0x7f0e070d, float:1.8878699E38)
            java.lang.String r5 = "EventLogPromotedSendAnonymously"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x1610:
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x1664
            boolean r0 = r12.ban_users
            boolean r5 = r13.ban_users
            if (r0 == r5) goto L_0x163c
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.ban_users
            if (r0 == 0) goto L_0x1626
            r0 = 43
            goto L_0x1628
        L_0x1626:
            r0 = 45
        L_0x1628:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625731(0x7f0e0703, float:1.8878678E38)
            java.lang.String r5 = "EventLogPromotedBanUsers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x163c:
            boolean r0 = r12.manage_call
            boolean r5 = r13.manage_call
            if (r0 == r5) goto L_0x1664
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.manage_call
            if (r0 == 0) goto L_0x164e
            r0 = 43
            goto L_0x1650
        L_0x164e:
            r0 = 45
        L_0x1650:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625736(0x7f0e0708, float:1.8878688E38)
            java.lang.String r5 = "EventLogPromotedManageCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x1664:
            boolean r0 = r12.invite_users
            boolean r5 = r13.invite_users
            if (r0 == r5) goto L_0x168c
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.invite_users
            if (r0 == 0) goto L_0x1676
            r0 = 43
            goto L_0x1678
        L_0x1676:
            r0 = 45
        L_0x1678:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.String r5 = "EventLogPromotedAddUsers"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x168c:
            boolean r0 = r9.megagroup
            if (r0 == 0) goto L_0x16b8
            boolean r0 = r12.pin_messages
            boolean r5 = r13.pin_messages
            if (r0 == r5) goto L_0x16b8
            r0 = 10
            r3.append(r0)
            boolean r0 = r13.pin_messages
            if (r0 == 0) goto L_0x16a2
            r0 = 43
            goto L_0x16a4
        L_0x16a2:
            r0 = 45
        L_0x16a4:
            r3.append(r0)
            r0 = 32
            r3.append(r0)
            r0 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.String r5 = "EventLogPromotedPinMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r3.append(r0)
        L_0x16b8:
            r1 = r3
        L_0x16b9:
            java.lang.String r0 = r1.toString()
            r6.messageText = r0
        L_0x16c0:
            r0 = r21
        L_0x16c2:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            if (r1 != 0) goto L_0x16cd
            org.telegram.tgnet.TLRPC$TL_messageService r1 = new org.telegram.tgnet.TLRPC$TL_messageService
            r1.<init>()
            r6.messageOwner = r1
        L_0x16cd:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            java.lang.CharSequence r2 = r6.messageText
            java.lang.String r2 = r2.toString()
            r1.message = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r1.from_id = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r2 = r7.user_id
            r1.user_id = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            int r2 = r7.date
            r1.date = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            r2 = 0
            r3 = r49[r2]
            int r4 = r3 + 1
            r49[r2] = r4
            r1.id = r3
            long r3 = r7.id
            r6.eventId = r3
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            r1.out = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$TL_peerChannel r2 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r2.<init>()
            r1.peer_id = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r2 = r9.id
            r1.channel_id = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            r2 = 0
            r1.unread = r2
            org.telegram.messenger.MediaController r10 = org.telegram.messenger.MediaController.getInstance()
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r1 == 0) goto L_0x1722
            r0 = 0
            r11 = r0
            goto L_0x1723
        L_0x1722:
            r11 = r0
        L_0x1723:
            if (r11 == 0) goto L_0x17a3
            r0 = 0
            r11.out = r0
            r1 = r49[r0]
            int r2 = r1 + 1
            r49[r0] = r2
            r11.id = r1
            int r0 = r11.flags
            r0 = r0 & -9
            r11.flags = r0
            r12 = 0
            r11.reply_to = r12
            int r0 = r11.flags
            r1 = -32769(0xffffffffffff7fff, float:NaN)
            r0 = r0 & r1
            r11.flags = r0
            org.telegram.messenger.MessageObject r0 = new org.telegram.messenger.MessageObject
            int r1 = r6.currentAccount
            r36 = 0
            r37 = 0
            r38 = 1
            r39 = 1
            long r2 = r6.eventId
            r33 = r0
            r34 = r1
            r35 = r11
            r40 = r2
            r33.<init>((int) r34, (org.telegram.tgnet.TLRPC.Message) r35, (java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.User>) r36, (java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.Chat>) r37, (boolean) r38, (boolean) r39, (long) r40)
            r13 = r0
            int r0 = r13.contentType
            if (r0 < 0) goto L_0x1799
            boolean r0 = r10.isPlayingMessage(r13)
            if (r0 == 0) goto L_0x1771
            org.telegram.messenger.MessageObject r0 = r10.getPlayingMessageObject()
            float r1 = r0.audioProgress
            r13.audioProgress = r1
            int r1 = r0.audioProgressSec
            r13.audioProgressSec = r1
        L_0x1771:
            int r1 = r6.currentAccount
            r0 = r43
            r2 = r45
            r14 = r31
            r3 = r46
            r4 = r47
            r15 = r32
            r5 = r50
            r0.createDateArray(r1, r2, r3, r4, r5)
            if (r50 == 0) goto L_0x178d
            r5 = r46
            r0 = 0
            r5.add(r0, r13)
            goto L_0x17aa
        L_0x178d:
            r5 = r46
            int r0 = r46.size()
            r1 = 1
            int r0 = r0 - r1
            r5.add(r0, r13)
            goto L_0x17aa
        L_0x1799:
            r5 = r46
            r14 = r31
            r15 = r32
            r0 = -1
            r6.contentType = r0
            goto L_0x17aa
        L_0x17a3:
            r5 = r46
            r14 = r31
            r15 = r32
            r12 = 0
        L_0x17aa:
            int r0 = r6.contentType
            if (r0 < 0) goto L_0x183d
            int r1 = r6.currentAccount
            r0 = r43
            r2 = r45
            r3 = r46
            r4 = r47
            r13 = r5
            r5 = r50
            r0.createDateArray(r1, r2, r3, r4, r5)
            if (r50 == 0) goto L_0x17c5
            r0 = 0
            r13.add(r0, r6)
            goto L_0x17ce
        L_0x17c5:
            int r0 = r46.size()
            r1 = 1
            int r0 = r0 - r1
            r13.add(r0, r6)
        L_0x17ce:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 != 0) goto L_0x17d4
            r6.messageText = r14
        L_0x17d4:
            r43.setType()
            r43.measureInlineBotButtons()
            r43.generateCaption()
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x17e8
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x17ea
        L_0x17e8:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x17ea:
            boolean r1 = r43.allowsBigEmoji()
            if (r1 == 0) goto L_0x17f4
            r1 = 1
            int[] r2 = new int[r1]
            r12 = r2
        L_0x17f4:
            r1 = r12
            java.lang.CharSequence r2 = r6.messageText
            android.graphics.Paint$FontMetricsInt r25 = r0.getFontMetricsInt()
            r3 = 1101004800(0x41a00000, float:20.0)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r27 = 0
            int r3 = r6.contentType
            if (r3 != 0) goto L_0x180a
            r29 = 1
            goto L_0x180c
        L_0x180a:
            r29 = 0
        L_0x180c:
            java.util.concurrent.atomic.AtomicReference<java.lang.ref.WeakReference<android.view.View>> r3 = r6.viewRef
            r24 = r2
            r28 = r1
            r30 = r3
            java.lang.CharSequence r2 = org.telegram.messenger.Emoji.replaceEmoji(r24, r25, r26, r27, r28, r29, r30)
            r6.messageText = r2
            r6.checkEmojiOnly(r1)
            boolean r2 = r10.isPlayingMessage(r6)
            if (r2 == 0) goto L_0x182f
            org.telegram.messenger.MessageObject r2 = r10.getPlayingMessageObject()
            float r3 = r2.audioProgress
            r6.audioProgress = r3
            int r3 = r2.audioProgressSec
            r6.audioProgressSec = r3
        L_0x182f:
            r6.generateLayout(r8)
            r2 = 1
            r6.layoutCreated = r2
            r2 = 0
            r6.generateThumbs(r2)
            r43.checkMediaExistance()
            return
        L_0x183d:
            r13 = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[], boolean):void");
    }

    private String getUserName(TLObject object, ArrayList<TLRPC.MessageEntity> entities, int offset) {
        long id;
        String name;
        String name2;
        String name3;
        if (object == null) {
            name2 = "";
            name = null;
            id = 0;
        } else if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            if (user.deleted) {
                name3 = LocaleController.getString("HiddenName", NUM);
            } else {
                name3 = ContactsController.formatName(user.first_name, user.last_name);
            }
            String username = user.username;
            long id2 = user.id;
            name2 = name3;
            name = username;
            id = id2;
        } else {
            TLRPC.Chat chat = (TLRPC.Chat) object;
            String name4 = chat.title;
            String username2 = chat.username;
            name2 = name4;
            name = username2;
            id = -chat.id;
        }
        if (offset >= 0) {
            TLRPC.TL_messageEntityMentionName entity = new TLRPC.TL_messageEntityMentionName();
            entity.user_id = id;
            entity.offset = offset;
            entity.length = name2.length();
            entities.add(entity);
        }
        if (TextUtils.isEmpty(name)) {
            return name2;
        }
        if (offset >= 0) {
            TLRPC.TL_messageEntityMentionName entity2 = new TLRPC.TL_messageEntityMentionName();
            entity2.user_id = id;
            entity2.offset = name2.length() + offset + 2;
            entity2.length = name.length() + 1;
            entities.add(entity2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{name2, name});
    }

    public void applyNewText() {
        applyNewText(this.messageOwner.message);
    }

    public void applyNewText(CharSequence text) {
        TextPaint paint;
        if (!TextUtils.isEmpty(text)) {
            TLRPC.User fromUser = null;
            if (isFromUser()) {
                fromUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
            }
            this.messageText = text;
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            int[] emojiOnly = allowsBigEmoji() ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly, this.contentType == 0, this.viewRef);
            checkEmojiOnly(emojiOnly);
            generateLayout(fromUser);
        }
    }

    private boolean allowsBigEmoji() {
        if (!SharedConfig.allowBigEmoji) {
            return false;
        }
        TLRPC.Message message = this.messageOwner;
        if (message == null || message.peer_id == null || (this.messageOwner.peer_id.channel_id == 0 && this.messageOwner.peer_id.chat_id == 0)) {
            return true;
        }
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        int i = (this.messageOwner.peer_id.channel_id > 0 ? 1 : (this.messageOwner.peer_id.channel_id == 0 ? 0 : -1));
        TLRPC.Peer peer = this.messageOwner.peer_id;
        TLRPC.Chat chat = instance.getChat(Long.valueOf(i != 0 ? peer.channel_id : peer.chat_id));
        if ((chat == null || !chat.gigagroup) && ChatObject.isActionBanned(chat, 8) && !ChatObject.hasAdminRights(chat)) {
            return false;
        }
        return true;
    }

    public void generateGameMessageText(TLRPC.User fromUser) {
        if (fromUser == null && isFromUser()) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
        }
        TLRPC.TL_game game = null;
        MessageObject messageObject = this.replyMessageObject;
        if (!(messageObject == null || messageObject.messageOwner.media == null || this.replyMessageObject.messageOwner.media.game == null)) {
            game = this.replyMessageObject.messageOwner.media.game;
        }
        if (game != null) {
            if (fromUser == null || fromUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0])), "un1", fromUser);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0]));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", game);
        } else if (fromUser == null || fromUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0])), "un1", fromUser);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0]));
        }
    }

    public boolean hasValidReplyMessageObject() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            return !(message instanceof TLRPC.TL_messageEmpty) && !(message.action instanceof TLRPC.TL_messageActionHistoryClear);
        }
    }

    public void generatePaymentSentMessageText(TLRPC.User fromUser) {
        String name;
        String currency;
        if (fromUser == null) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(getDialogId()));
        }
        if (fromUser != null) {
            name = UserObject.getFirstName(fromUser);
        } else {
            name = "";
        }
        try {
            currency = LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            currency = "<error>";
        }
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
            if (this.messageOwner.action.recurring_init) {
                this.messageText = LocaleController.formatString(NUM, currency, name);
                return;
            }
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", NUM, currency, name);
        } else if (this.messageOwner.action.recurring_init) {
            this.messageText = LocaleController.formatString(NUM, currency, name, this.replyMessageObject.messageOwner.media.title);
        } else {
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", NUM, currency, name, this.replyMessageObject.messageOwner.media.title);
        }
    }

    /* JADX WARNING: Failed to insert additional move for type inference */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generatePinMessageText(org.telegram.tgnet.TLRPC.User r14, org.telegram.tgnet.TLRPC.Chat r15) {
        /*
            r13 = this;
            if (r14 != 0) goto L_0x0059
            if (r15 != 0) goto L_0x0059
            boolean r0 = r13.isFromUser()
            if (r0 == 0) goto L_0x001e
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r1 = r1.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r14 = r0.getUser(r1)
        L_0x001e:
            if (r14 != 0) goto L_0x0059
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel
            if (r0 == 0) goto L_0x003d
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.channel_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r15 = r0.getChat(r1)
            goto L_0x0059
        L_0x003d:
            org.telegram.tgnet.TLRPC$Message r0 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChat
            if (r0 == 0) goto L_0x0059
            int r0 = r13.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.chat_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r15 = r0.getChat(r1)
        L_0x0059:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            r1 = 2131624198(0x7f0e0106, float:1.8875569E38)
            java.lang.String r2 = "ActionPinnedNoText"
            java.lang.String r3 = "un1"
            if (r0 == 0) goto L_0x02f2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty
            if (r4 != 0) goto L_0x02f2
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r0 == 0) goto L_0x0072
            goto L_0x02f2
        L_0x0072:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0090
            r0 = 2131624197(0x7f0e0105, float:1.8875567E38)
            java.lang.String r1 = "ActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x0087
            r1 = r14
            goto L_0x0088
        L_0x0087:
            r1 = r15
        L_0x0088:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x0090:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00ae
            r0 = 2131624205(0x7f0e010d, float:1.8875583E38)
            java.lang.String r1 = "ActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x00a5
            r1 = r14
            goto L_0x00a6
        L_0x00a5:
            r1 = r15
        L_0x00a6:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x00ae:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x00cc
            r0 = 2131624196(0x7f0e0104, float:1.8875565E38)
            java.lang.String r1 = "ActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x00c3
            r1 = r14
            goto L_0x00c4
        L_0x00c3:
            r1 = r15
        L_0x00c4:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x00cc:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00ea
            r0 = 2131624206(0x7f0e010e, float:1.8875585E38)
            java.lang.String r1 = "ActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x00e1
            r1 = r14
            goto L_0x00e2
        L_0x00e1:
            r1 = r15
        L_0x00e2:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x00ea:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x0108
            r0 = 2131624202(0x7f0e010a, float:1.8875577E38)
            java.lang.String r1 = "ActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x00ff
            r1 = r14
            goto L_0x0100
        L_0x00ff:
            r1 = r15
        L_0x0100:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x0108:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isSticker()
            if (r0 != 0) goto L_0x0118
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isAnimatedSticker()
            if (r0 == 0) goto L_0x0136
        L_0x0118:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            boolean r0 = r0.isAnimatedEmoji()
            if (r0 != 0) goto L_0x0136
            r0 = 2131624203(0x7f0e010b, float:1.887558E38)
            java.lang.String r1 = "ActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x012d
            r1 = r14
            goto L_0x012e
        L_0x012d:
            r1 = r15
        L_0x012e:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x0136:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r0 == 0) goto L_0x0156
            r0 = 2131624192(0x7f0e0100, float:1.8875557E38)
            java.lang.String r1 = "ActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x014d
            r1 = r14
            goto L_0x014e
        L_0x014d:
            r1 = r15
        L_0x014e:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x0156:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r0 == 0) goto L_0x0176
            r0 = 2131624194(0x7f0e0102, float:1.887556E38)
            java.lang.String r1 = "ActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x016d
            r1 = r14
            goto L_0x016e
        L_0x016d:
            r1 = r15
        L_0x016e:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x0176:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r0 == 0) goto L_0x0196
            r0 = 2131624195(0x7f0e0103, float:1.8875563E38)
            java.lang.String r1 = "ActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x018d
            r1 = r14
            goto L_0x018e
        L_0x018d:
            r1 = r15
        L_0x018e:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x0196:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r0 == 0) goto L_0x01b6
            r0 = 2131624191(0x7f0e00ff, float:1.8875555E38)
            java.lang.String r1 = "ActionPinnedContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x01ad
            r1 = r14
            goto L_0x01ae
        L_0x01ad:
            r1 = r15
        L_0x01ae:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x01b6:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x01fa
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x01e4
            r0 = 2131624201(0x7f0e0109, float:1.8875575E38)
            java.lang.String r1 = "ActionPinnedQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x01db
            r1 = r14
            goto L_0x01dc
        L_0x01db:
            r1 = r15
        L_0x01dc:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x01e4:
            r0 = 2131624200(0x7f0e0108, float:1.8875573E38)
            java.lang.String r1 = "ActionPinnedPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x01f1
            r1 = r14
            goto L_0x01f2
        L_0x01f1:
            r1 = r15
        L_0x01f2:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x01fa:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x021a
            r0 = 2131624199(0x7f0e0107, float:1.887557E38)
            java.lang.String r1 = "ActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r14 == 0) goto L_0x0211
            r1 = r14
            goto L_0x0212
        L_0x0211:
            r1 = r15
        L_0x0212:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x021a:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            r4 = 1101004800(0x41a00000, float:20.0)
            r5 = 1
            r6 = 0
            if (r0 == 0) goto L_0x0277
            r0 = 2131624193(0x7f0e0101, float:1.8875559E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "🎮 "
            r2.append(r7)
            org.telegram.messenger.MessageObject r7 = r13.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$TL_game r7 = r7.game
            java.lang.String r7 = r7.title
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r1[r6] = r2
            java.lang.String r2 = "ActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            if (r14 == 0) goto L_0x0254
            r1 = r14
            goto L_0x0255
        L_0x0254:
            r1 = r15
        L_0x0255:
            java.lang.CharSequence r7 = replaceWithLink(r0, r3, r1)
            r13.messageText = r7
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r8 = r0.getFontMetricsInt()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r10 = 0
            int r0 = r13.contentType
            if (r0 != 0) goto L_0x026c
            r11 = 1
            goto L_0x026d
        L_0x026c:
            r11 = 0
        L_0x026d:
            java.util.concurrent.atomic.AtomicReference<java.lang.ref.WeakReference<android.view.View>> r12 = r13.viewRef
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r7, r8, r9, r10, r11, r12)
            r13.messageText = r0
            goto L_0x0301
        L_0x0277:
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x02e2
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x02e2
            org.telegram.messenger.MessageObject r0 = r13.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x02a4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r2 = r0.subSequence(r6, r2)
            r1.append(r2)
            java.lang.String r2 = "..."
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x02a4:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r8 = r1.getFontMetricsInt()
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r10 = 0
            int r1 = r13.contentType
            if (r1 != 0) goto L_0x02b5
            r11 = 1
            goto L_0x02b6
        L_0x02b5:
            r11 = 0
        L_0x02b6:
            java.util.concurrent.atomic.AtomicReference<java.lang.ref.WeakReference<android.view.View>> r12 = r13.viewRef
            r7 = r0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r7, r8, r9, r10, r11, r12)
            org.telegram.messenger.MessageObject r1 = r13.replyMessageObject
            r2 = r0
            android.text.Spannable r2 = (android.text.Spannable) r2
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r1, r2)
            r1 = 2131624204(0x7f0e010c, float:1.8875581E38)
            java.lang.String r2 = "ActionPinnedText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r5]
            r2[r6] = r0
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.formatSpannable(r1, r2)
            if (r14 == 0) goto L_0x02da
            r2 = r14
            goto L_0x02db
        L_0x02da:
            r2 = r15
        L_0x02db:
            java.lang.CharSequence r1 = replaceWithLink(r1, r3, r2)
            r13.messageText = r1
            goto L_0x0301
        L_0x02e2:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r14 == 0) goto L_0x02ea
            r1 = r14
            goto L_0x02eb
        L_0x02ea:
            r1 = r15
        L_0x02eb:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
            goto L_0x0301
        L_0x02f2:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r14 == 0) goto L_0x02fa
            r1 = r14
            goto L_0x02fb
        L_0x02fa:
            r1 = r15
        L_0x02fb:
            java.lang.CharSequence r0 = replaceWithLink(r0, r3, r1)
            r13.messageText = r0
        L_0x0301:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generatePinMessageText(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat):void");
    }

    public static void updateReactions(TLRPC.Message message, TLRPC.TL_messageReactions reactions) {
        if (message != null && reactions != null) {
            if (reactions.min && message.reactions != null) {
                int a = 0;
                int N = message.reactions.results.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_reactionCount reaction = (TLRPC.TL_reactionCount) message.reactions.results.get(a);
                    if (reaction.chosen) {
                        int b = 0;
                        int N2 = reactions.results.size();
                        while (true) {
                            if (b >= N2) {
                                break;
                            }
                            TLRPC.TL_reactionCount newReaction = (TLRPC.TL_reactionCount) reactions.results.get(b);
                            if (reaction.reaction.equals(newReaction.reaction)) {
                                newReaction.chosen = true;
                                break;
                            }
                            b++;
                        }
                    } else {
                        a++;
                    }
                }
            }
            message.reactions = reactions;
            message.flags |= 1048576;
        }
    }

    public boolean hasReactions() {
        return this.messageOwner.reactions != null && !this.messageOwner.reactions.results.isEmpty();
    }

    public static void updatePollResults(TLRPC.TL_messageMediaPoll media, TLRPC.PollResults results) {
        if (media != null && results != null) {
            if ((results.flags & 2) != 0) {
                ArrayList<byte[]> chosen = null;
                byte[] correct = null;
                if (results.min && media.results.results != null) {
                    int N2 = media.results.results.size();
                    for (int b = 0; b < N2; b++) {
                        TLRPC.TL_pollAnswerVoters answerVoters = media.results.results.get(b);
                        if (answerVoters.chosen) {
                            if (chosen == null) {
                                chosen = new ArrayList<>();
                            }
                            chosen.add(answerVoters.option);
                        }
                        if (answerVoters.correct) {
                            correct = answerVoters.option;
                        }
                    }
                }
                media.results.results = results.results;
                if (chosen != null || correct != null) {
                    int N22 = media.results.results.size();
                    for (int b2 = 0; b2 < N22; b2++) {
                        TLRPC.TL_pollAnswerVoters answerVoters2 = media.results.results.get(b2);
                        if (chosen != null) {
                            int a = 0;
                            int N = chosen.size();
                            while (true) {
                                if (a >= N) {
                                    break;
                                } else if (Arrays.equals(answerVoters2.option, chosen.get(a))) {
                                    answerVoters2.chosen = true;
                                    chosen.remove(a);
                                    break;
                                } else {
                                    a++;
                                }
                            }
                            if (chosen.isEmpty() != 0) {
                                chosen = null;
                            }
                        }
                        if (correct != null && Arrays.equals(answerVoters2.option, correct)) {
                            answerVoters2.correct = true;
                            correct = null;
                        }
                        if (chosen == null && correct == null) {
                            break;
                        }
                    }
                }
                media.results.flags |= 2;
            }
            if ((results.flags & 4) != 0) {
                media.results.total_voters = results.total_voters;
                media.results.flags |= 4;
            }
            if ((results.flags & 8) != 0) {
                media.results.recent_voters = results.recent_voters;
                media.results.flags |= 8;
            }
            if ((results.flags & 16) != 0) {
                media.results.solution = results.solution;
                media.results.solution_entities = results.solution_entities;
                media.results.flags |= 16;
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

    public boolean canUnvote() {
        if (this.type != 17) {
            return false;
        }
        TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) this.messageOwner.media;
        if (mediaPoll.results == null || mediaPoll.results.results.isEmpty() || mediaPoll.poll.quiz) {
            return false;
        }
        int N = mediaPoll.results.results.size();
        for (int a = 0; a < N; a++) {
            if (mediaPoll.results.results.get(a).chosen) {
                return true;
            }
        }
        return false;
    }

    public boolean isVoted() {
        if (this.type != 17) {
            return false;
        }
        TLRPC.TL_messageMediaPoll mediaPoll = (TLRPC.TL_messageMediaPoll) this.messageOwner.media;
        if (mediaPoll.results == null || mediaPoll.results.results.isEmpty()) {
            return false;
        }
        int N = mediaPoll.results.results.size();
        for (int a = 0; a < N; a++) {
            if (mediaPoll.results.results.get(a).chosen) {
                return true;
            }
        }
        return false;
    }

    public boolean isSponsored() {
        return this.sponsoredId != null;
    }

    public long getPollId() {
        if (this.type != 17) {
            return 0;
        }
        return ((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.id;
    }

    private TLRPC.Photo getPhotoWithId(TLRPC.WebPage webPage, long id) {
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (webPage.photo != null && webPage.photo.id == id) {
            return webPage.photo;
        }
        for (int a = 0; a < webPage.cached_page.photos.size(); a++) {
            TLRPC.Photo photo = webPage.cached_page.photos.get(a);
            if (photo.id == id) {
                return photo;
            }
        }
        return null;
    }

    private TLRPC.Document getDocumentWithId(TLRPC.WebPage webPage, long id) {
        if (webPage == null || webPage.cached_page == null) {
            return null;
        }
        if (webPage.document != null && webPage.document.id == id) {
            return webPage.document;
        }
        for (int a = 0; a < webPage.cached_page.documents.size(); a++) {
            TLRPC.Document document = webPage.cached_page.documents.get(a);
            if (document.id == id) {
                return document;
            }
        }
        return null;
    }

    public boolean isSupergroup() {
        if (this.localSupergroup) {
            return true;
        }
        Boolean bool = this.cachedIsSupergroup;
        if (bool != null) {
            return bool.booleanValue();
        }
        if (this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0) {
            this.cachedIsSupergroup = false;
            return false;
        }
        TLRPC.Chat chat = getChat((AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.Chat>) null, this.messageOwner.peer_id.channel_id);
        if (chat == null) {
            return false;
        }
        Boolean valueOf = Boolean.valueOf(chat.megagroup);
        this.cachedIsSupergroup = valueOf;
        return valueOf.booleanValue();
    }

    private MessageObject getMessageObjectForBlock(TLRPC.WebPage webPage, TLRPC.PageBlock pageBlock) {
        TLRPC.TL_message message = null;
        if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.Photo photo = getPhotoWithId(webPage, ((TLRPC.TL_pageBlockPhoto) pageBlock).photo_id);
            if (photo == webPage.photo) {
                return this;
            }
            message = new TLRPC.TL_message();
            message.media = new TLRPC.TL_messageMediaPhoto();
            message.media.photo = photo;
        } else if (pageBlock instanceof TLRPC.TL_pageBlockVideo) {
            TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) pageBlock;
            if (getDocumentWithId(webPage, pageBlockVideo.video_id) == webPage.document) {
                return this;
            }
            message = new TLRPC.TL_message();
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.document = getDocumentWithId(webPage, pageBlockVideo.video_id);
        }
        message.message = "";
        message.realId = getId();
        message.id = Utilities.random.nextInt();
        message.date = this.messageOwner.date;
        message.peer_id = this.messageOwner.peer_id;
        message.out = this.messageOwner.out;
        message.from_id = this.messageOwner.from_id;
        return new MessageObject(this.currentAccount, message, false, true);
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> array, ArrayList<TLRPC.PageBlock> blocksToSearch) {
        ArrayList<MessageObject> messageObjects = array == null ? new ArrayList<>() : array;
        if (this.messageOwner.media == null || this.messageOwner.media.webpage == null) {
            return messageObjects;
        }
        TLRPC.WebPage webPage = this.messageOwner.media.webpage;
        if (webPage.cached_page == null) {
            return messageObjects;
        }
        ArrayList<TLRPC.PageBlock> blocks = blocksToSearch == null ? webPage.cached_page.blocks : blocksToSearch;
        for (int a = 0; a < blocks.size(); a++) {
            TLRPC.PageBlock block = blocks.get(a);
            if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow slideshow = (TLRPC.TL_pageBlockSlideshow) block;
                for (int b = 0; b < slideshow.items.size(); b++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, slideshow.items.get(b)));
                }
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage slideshow2 = (TLRPC.TL_pageBlockCollage) block;
                for (int b2 = 0; b2 < slideshow2.items.size(); b2++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, slideshow2.items.get(b2)));
                }
            }
        }
        return messageObjects;
    }

    public void createMessageSendInfo() {
        if (this.messageOwner.message == null) {
            return;
        }
        if ((this.messageOwner.id < 0 || isEditing()) && this.messageOwner.params != null) {
            String str = this.messageOwner.params.get("ve");
            String param = str;
            if (str != null && (isVideo() || isNewGif() || isRoundVideo())) {
                VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
                this.videoEditedInfo = videoEditedInfo2;
                if (!videoEditedInfo2.parseString(param)) {
                    this.videoEditedInfo = null;
                } else {
                    this.videoEditedInfo.roundVideo = isRoundVideo();
                }
            }
            if (this.messageOwner.send_state == 3) {
                String str2 = this.messageOwner.params.get("prevMedia");
                String param2 = str2;
                if (str2 != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(param2, 0));
                    this.previousMedia = TLRPC.MessageMedia.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    this.previousMessage = serializedData.readString(false);
                    this.previousAttachPath = serializedData.readString(false);
                    int count = serializedData.readInt32(false);
                    this.previousMessageEntities = new ArrayList<>(count);
                    for (int a = 0; a < count; a++) {
                        this.previousMessageEntities.add(TLRPC.MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                    }
                    serializedData.cleanup();
                }
            }
        }
    }

    public void measureInlineBotButtons() {
        CharSequence text;
        float width;
        if (!this.isRestrictedMessage) {
            this.wantedBotKeyboardWidth = 0;
            if ((this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || (this.messageOwner.reactions != null && !this.messageOwner.reactions.results.isEmpty())) {
                Theme.createCommonMessageResources();
                StringBuilder sb = this.botButtonsLayout;
                if (sb == null) {
                    this.botButtonsLayout = new StringBuilder();
                } else {
                    sb.setLength(0);
                }
            }
            float f = 2000.0f;
            float f2 = 15.0f;
            if (this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) {
                int a = 0;
                while (a < this.messageOwner.reply_markup.rows.size()) {
                    TLRPC.TL_keyboardButtonRow row = this.messageOwner.reply_markup.rows.get(a);
                    int maxButtonSize = 0;
                    int size = row.buttons.size();
                    int b = 0;
                    while (b < size) {
                        TLRPC.KeyboardButton button = row.buttons.get(b);
                        StringBuilder sb2 = this.botButtonsLayout;
                        sb2.append(a);
                        sb2.append(b);
                        if (!(button instanceof TLRPC.TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                            String str = button.text;
                            if (str == null) {
                                str = "";
                            }
                            text = Emoji.replaceEmoji(str, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(f2), false, this.contentType == 0, this.viewRef);
                        } else {
                            text = LocaleController.getString("PaymentReceipt", NUM);
                        }
                        StaticLayout staticLayout = new StaticLayout(text, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (staticLayout.getLineCount() > 0) {
                            float width2 = staticLayout.getLineWidth(0);
                            float left = staticLayout.getLineLeft(0);
                            if (left < width2) {
                                width = width2 - left;
                            } else {
                                width = width2;
                            }
                            maxButtonSize = Math.max(maxButtonSize, ((int) Math.ceil((double) width)) + AndroidUtilities.dp(4.0f));
                        }
                        b++;
                        f = 2000.0f;
                        f2 = 15.0f;
                    }
                    this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
                    a++;
                    f = 2000.0f;
                    f2 = 15.0f;
                }
            } else if (this.messageOwner.reactions != null) {
                int size2 = this.messageOwner.reactions.results.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    TLRPC.TL_reactionCount reactionCount = (TLRPC.TL_reactionCount) this.messageOwner.reactions.results.get(a2);
                    int maxButtonSize2 = 0;
                    StringBuilder sb3 = this.botButtonsLayout;
                    sb3.append(0);
                    sb3.append(a2);
                    CharSequence text2 = Emoji.replaceEmoji(String.format("%d %s", new Object[]{Integer.valueOf(reactionCount.count), reactionCount.reaction}), Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false, this.contentType == 0, this.viewRef);
                    StaticLayout staticLayout2 = new StaticLayout(text2, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout2.getLineCount() > 0) {
                        float width3 = staticLayout2.getLineWidth(0);
                        float left2 = staticLayout2.getLineLeft(0);
                        if (left2 < width3) {
                            width3 -= left2;
                        }
                        CharSequence charSequence = text2;
                        maxButtonSize2 = Math.max(0, ((int) Math.ceil((double) width3)) + AndroidUtilities.dp(4.0f));
                    }
                    this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize2) * size2) + (AndroidUtilities.dp(5.0f) * (size2 - 1)));
                }
            }
        }
    }

    public boolean isVideoAvatar() {
        return (this.messageOwner.action == null || this.messageOwner.action.photo == null || this.messageOwner.action.photo.video_sizes.isEmpty()) ? false : true;
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    private TLRPC.User getUser(AbstractMap<Long, TLRPC.User> users, LongSparseArray<TLRPC.User> sUsers, long userId) {
        TLRPC.User user = null;
        if (users != null) {
            user = users.get(Long.valueOf(userId));
        } else if (sUsers != null) {
            user = sUsers.get(userId);
        }
        if (user == null) {
            return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userId));
        }
        return user;
    }

    private TLRPC.Chat getChat(AbstractMap<Long, TLRPC.Chat> chats, LongSparseArray<TLRPC.Chat> sChats, long chatId) {
        TLRPC.Chat chat = null;
        if (chats != null) {
            chat = chats.get(Long.valueOf(chatId));
        } else if (sChats != null) {
            chat = sChats.get(chatId);
        }
        if (chat == null) {
            return MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatId));
        }
        return chat;
    }

    private void updateMessageText(AbstractMap<Long, TLRPC.User> users, AbstractMap<Long, TLRPC.Chat> chats, LongSparseArray<TLRPC.User> sUsers, LongSparseArray<TLRPC.Chat> sChats) {
        TLRPC.Chat fromChat;
        TLRPC.Chat fromUser;
        TLRPC.User fromUser2;
        String str;
        String str2;
        String str3;
        TLRPC.Chat chat;
        String date;
        long singleUserId;
        TLRPC.Chat chat2;
        String str4;
        TLObject from;
        String str5;
        TLRPC.User user;
        TLObject to;
        long singleUserId2;
        String time;
        AbstractMap<Long, TLRPC.User> abstractMap = users;
        AbstractMap<Long, TLRPC.Chat> abstractMap2 = chats;
        LongSparseArray<TLRPC.User> longSparseArray = sUsers;
        LongSparseArray<TLRPC.Chat> longSparseArray2 = sChats;
        if (this.messageOwner.from_id instanceof TLRPC.TL_peerUser) {
            fromUser = getUser(abstractMap, longSparseArray, this.messageOwner.from_id.user_id);
            fromChat = null;
        } else if (this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
            fromUser = null;
            fromChat = getChat(abstractMap2, longSparseArray2, this.messageOwner.from_id.channel_id);
        } else {
            fromUser = null;
            fromChat = null;
        }
        TLObject tLObject = fromUser != null ? fromUser : fromChat;
        TLRPC.Message message = this.messageOwner;
        String str6 = "";
        if (!(message instanceof TLRPC.TL_messageService)) {
            TLRPC.Chat chat3 = fromChat;
            TLObject fromUser3 = tLObject;
            this.isRestrictedMessage = false;
            String restrictionReason = MessagesController.getRestrictionReason(message.restriction_reason);
            if (!TextUtils.isEmpty(restrictionReason)) {
                this.messageText = restrictionReason;
                this.isRestrictedMessage = true;
            } else if (!isMediaEmpty()) {
                if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDice) {
                    this.messageText = getDiceEmoji();
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPoll) {
                    if (((TLRPC.TL_messageMediaPoll) this.messageOwner.media).poll.quiz) {
                        this.messageText = LocaleController.getString("QuizPoll", NUM);
                    } else {
                        this.messageText = LocaleController.getString("Poll", NUM);
                    }
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                    if (this.messageOwner.media.ttl_seconds == 0 || (this.messageOwner instanceof TLRPC.TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachPhoto", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingPhoto", NUM);
                    }
                } else if (isVideo() || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) && (getDocument() instanceof TLRPC.TL_documentEmpty) && this.messageOwner.media.ttl_seconds != 0)) {
                    if (this.messageOwner.media.ttl_seconds == 0 || (this.messageOwner instanceof TLRPC.TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachVideo", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingVideo", NUM);
                    }
                } else if (isVoice()) {
                    this.messageText = LocaleController.getString("AttachAudio", NUM);
                } else if (isRoundVideo()) {
                    this.messageText = LocaleController.getString("AttachRound", NUM);
                } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) {
                    this.messageText = LocaleController.getString("AttachLocation", NUM);
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) {
                    this.messageText = LocaleController.getString("AttachLiveLocation", NUM);
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaContact) {
                    this.messageText = LocaleController.getString("AttachContact", NUM);
                    if (!TextUtils.isEmpty(this.messageOwner.media.vcard)) {
                        this.vCardData = VCardData.parse(this.messageOwner.media.vcard);
                    }
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                    this.messageText = this.messageOwner.message;
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                    this.messageText = this.messageOwner.media.description;
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaUnsupported) {
                    this.messageText = LocaleController.getString("UnsupportedMedia", NUM);
                } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
                    if (isSticker() || isAnimatedStickerDocument(getDocument(), true)) {
                        String sch = getStickerChar();
                        if (sch == null || sch.length() <= 0) {
                            this.messageText = LocaleController.getString("AttachSticker", NUM);
                        } else {
                            this.messageText = String.format("%s %s", new Object[]{sch, LocaleController.getString("AttachSticker", NUM)});
                        }
                    } else if (isMusic()) {
                        this.messageText = LocaleController.getString("AttachMusic", NUM);
                    } else if (isGif()) {
                        this.messageText = LocaleController.getString("AttachGif", NUM);
                    } else {
                        String name = FileLoader.getDocumentFileName(getDocument());
                        if (!TextUtils.isEmpty(name)) {
                            this.messageText = name;
                        } else {
                            this.messageText = LocaleController.getString("AttachDocument", NUM);
                        }
                    }
                }
            } else if (this.messageOwner.message != null) {
                try {
                    if (this.messageOwner.message.length() > 200) {
                        this.messageText = AndroidUtilities.BAD_CHARS_MESSAGE_LONG_PATTERN.matcher(this.messageOwner.message).replaceAll("‌");
                    } else {
                        this.messageText = AndroidUtilities.BAD_CHARS_MESSAGE_PATTERN.matcher(this.messageOwner.message).replaceAll("‌");
                    }
                } catch (Throwable th) {
                    this.messageText = this.messageOwner.message;
                }
            } else {
                this.messageText = this.messageOwner.message;
            }
        } else if (message.action == null) {
            TLRPC.Chat chat4 = fromChat;
            TLObject fromUser4 = tLObject;
        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionGroupCallScheduled) {
            TLRPC.TL_messageActionGroupCallScheduled action = (TLRPC.TL_messageActionGroupCallScheduled) this.messageOwner.action;
            if ((this.messageOwner.peer_id instanceof TLRPC.TL_peerChat) || isSupergroup()) {
                this.messageText = LocaleController.formatString("ActionGroupCallScheduled", NUM, LocaleController.formatStartsTime((long) action.schedule_date, 3, false));
            } else {
                this.messageText = LocaleController.formatString("ActionChannelCallScheduled", NUM, LocaleController.formatStartsTime((long) action.schedule_date, 3, false));
            }
            TLRPC.User user2 = fromUser;
            TLRPC.Chat chat5 = fromChat;
            TLRPC.User fromUser5 = tLObject;
        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionGroupCall) {
            if (this.messageOwner.action.duration != 0) {
                int days = this.messageOwner.action.duration / 86400;
                if (days > 0) {
                    time = LocaleController.formatPluralString("Days", days, new Object[0]);
                } else {
                    int hours = this.messageOwner.action.duration / 3600;
                    if (hours > 0) {
                        time = LocaleController.formatPluralString("Hours", hours, new Object[0]);
                    } else {
                        int minutes = this.messageOwner.action.duration / 60;
                        if (minutes > 0) {
                            time = LocaleController.formatPluralString("Minutes", minutes, new Object[0]);
                        } else {
                            time = LocaleController.formatPluralString("Seconds", this.messageOwner.action.duration, new Object[0]);
                        }
                    }
                }
                if (!(this.messageOwner.peer_id instanceof TLRPC.TL_peerChat) && !isSupergroup()) {
                    this.messageText = LocaleController.formatString("ActionChannelCallEnded", NUM, time);
                } else if (isOut()) {
                    this.messageText = LocaleController.formatString("ActionGroupCallEndedByYou", NUM, time);
                } else {
                    this.messageText = replaceWithLink(LocaleController.formatString("ActionGroupCallEndedBy", NUM, time), "un1", tLObject);
                }
                TLRPC.User user3 = fromUser;
                TLRPC.Chat chat6 = fromChat;
                TLRPC.User fromUser6 = tLObject;
            } else if (!(this.messageOwner.peer_id instanceof TLRPC.TL_peerChat) && !isSupergroup()) {
                this.messageText = LocaleController.getString("ActionChannelCallJustStarted", NUM);
                TLRPC.User user4 = fromUser;
                TLRPC.Chat chat7 = fromChat;
                TLRPC.User fromUser7 = tLObject;
            } else if (isOut()) {
                this.messageText = LocaleController.getString("ActionGroupCallStartedByYou", NUM);
                TLRPC.User user5 = fromUser;
                TLRPC.Chat chat8 = fromChat;
                TLRPC.User fromUser8 = tLObject;
            } else {
                this.messageText = replaceWithLink(LocaleController.getString("ActionGroupCallStarted", NUM), "un1", tLObject);
                TLRPC.User user6 = fromUser;
                TLRPC.Chat chat9 = fromChat;
                TLRPC.User fromUser9 = tLObject;
            }
        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionInviteToGroupCall) {
            long singleUserId3 = this.messageOwner.action.user_id;
            if (singleUserId3 == 0 && this.messageOwner.action.users.size() == 1) {
                singleUserId2 = this.messageOwner.action.users.get(0).longValue();
            } else {
                singleUserId2 = singleUserId3;
            }
            if (singleUserId2 != 0) {
                TLRPC.User whoUser = getUser(abstractMap, longSparseArray, singleUserId2);
                if (isOut()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionGroupCallYouInvited", NUM), "un2", whoUser);
                } else if (singleUserId2 == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionGroupCallInvitedYou", NUM), "un1", tLObject);
                } else {
                    CharSequence replaceWithLink = replaceWithLink(LocaleController.getString("ActionGroupCallInvited", NUM), "un2", whoUser);
                    this.messageText = replaceWithLink;
                    this.messageText = replaceWithLink(replaceWithLink, "un1", tLObject);
                }
            } else if (isOut()) {
                long j = singleUserId2;
                this.messageText = replaceWithLink(LocaleController.getString("ActionGroupCallYouInvited", NUM), "un2", this.messageOwner.action.users, users, sUsers);
            } else {
                CharSequence replaceWithLink2 = replaceWithLink(LocaleController.getString("ActionGroupCallInvited", NUM), "un2", this.messageOwner.action.users, users, sUsers);
                this.messageText = replaceWithLink2;
                this.messageText = replaceWithLink(replaceWithLink2, "un1", tLObject);
            }
            TLRPC.User user7 = fromUser;
            TLRPC.Chat chat10 = fromChat;
            TLRPC.User fromUser10 = tLObject;
        } else {
            String str7 = "un1";
            if (this.messageOwner.action instanceof TLRPC.TL_messageActionGeoProximityReached) {
                TLRPC.TL_messageActionGeoProximityReached action2 = (TLRPC.TL_messageActionGeoProximityReached) this.messageOwner.action;
                long fromId = getPeerId(action2.from_id);
                if (fromId > 0) {
                    str4 = "un2";
                    from = getUser(abstractMap, longSparseArray, fromId);
                } else {
                    str4 = "un2";
                    from = getChat(abstractMap2, longSparseArray2, -fromId);
                }
                TLRPC.User fromUser11 = fromUser;
                TLRPC.Chat chat11 = fromChat;
                long toId = getPeerId(action2.to_id);
                long selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (toId == selfUserId) {
                    str5 = str6;
                    this.messageText = replaceWithLink(LocaleController.formatString("ActionUserWithinRadius", NUM, LocaleController.formatDistance((float) action2.distance, 2)), str7, from);
                    user = tLObject;
                } else {
                    str5 = str6;
                    if (toId > 0) {
                        to = getUser(abstractMap, longSparseArray, toId);
                        user = tLObject;
                    } else {
                        user = tLObject;
                        to = getChat(abstractMap2, longSparseArray2, -toId);
                    }
                    if (fromId == selfUserId) {
                        this.messageText = replaceWithLink(LocaleController.formatString("ActionUserWithinYouRadius", NUM, LocaleController.formatDistance((float) action2.distance, 2)), str7, to);
                    } else {
                        CharSequence replaceWithLink3 = replaceWithLink(LocaleController.formatString("ActionUserWithinOtherRadius", NUM, LocaleController.formatDistance((float) action2.distance, 2)), str4, to);
                        this.messageText = replaceWithLink3;
                        this.messageText = replaceWithLink(replaceWithLink3, str7, from);
                    }
                }
                TLRPC.User user8 = fromUser11;
                TLRPC.User user9 = user;
                str6 = str5;
            } else {
                TLRPC.User fromUser12 = fromUser;
                TLRPC.Chat chat12 = fromChat;
                TLObject fromObject = tLObject;
                String str8 = str6;
                String str9 = "un2";
                if (this.messageOwner.action instanceof TLRPC.TL_messageActionCustomAction) {
                    this.messageText = this.messageOwner.action.message;
                    TLRPC.User user10 = fromUser12;
                    TLObject tLObject2 = fromObject;
                    str6 = str8;
                } else if (!(this.messageOwner.action instanceof TLRPC.TL_messageActionChatCreate)) {
                    TLObject fromObject2 = fromObject;
                    if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser) {
                        if (!isFromUser() || this.messageOwner.action.user_id != this.messageOwner.from_id.user_id) {
                            TLRPC.User whoUser2 = getUser(abstractMap, longSparseArray, this.messageOwner.action.user_id);
                            if (isOut()) {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", NUM), str9, whoUser2);
                            } else if (this.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", NUM), str7, fromObject2);
                            } else {
                                CharSequence replaceWithLink4 = replaceWithLink(LocaleController.getString("ActionKickUser", NUM), str9, whoUser2);
                                this.messageText = replaceWithLink4;
                                this.messageText = replaceWithLink(replaceWithLink4, str7, fromObject2);
                            }
                            TLRPC.User whoUser3 = fromUser12;
                            str6 = str8;
                        } else if (isOut()) {
                            this.messageText = LocaleController.getString("ActionYouLeftUser", NUM);
                            TLRPC.User user11 = fromUser12;
                            str6 = str8;
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", NUM), str7, fromObject2);
                            TLRPC.User user12 = fromUser12;
                            str6 = str8;
                        }
                    } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser) {
                        long singleUserId4 = this.messageOwner.action.user_id;
                        if (singleUserId4 == 0 && this.messageOwner.action.users.size() == 1) {
                            singleUserId = this.messageOwner.action.users.get(0).longValue();
                        } else {
                            singleUserId = singleUserId4;
                        }
                        if (singleUserId != 0) {
                            TLRPC.User whoUser4 = getUser(abstractMap, longSparseArray, singleUserId);
                            String str10 = "ActionYouAddUser";
                            if (this.messageOwner.peer_id.channel_id != 0) {
                                chat2 = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                            } else {
                                chat2 = null;
                            }
                            if (this.messageOwner.from_id == null || singleUserId != this.messageOwner.from_id.user_id) {
                                if (isOut()) {
                                    this.messageText = replaceWithLink(LocaleController.getString(str10, NUM), str9, whoUser4);
                                } else if (singleUserId != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                    CharSequence replaceWithLink5 = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), str9, whoUser4);
                                    this.messageText = replaceWithLink5;
                                    this.messageText = replaceWithLink(replaceWithLink5, str7, fromObject2);
                                } else if (this.messageOwner.peer_id.channel_id == 0) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", NUM), str7, fromObject2);
                                } else if (chat2 == null || !chat2.megagroup) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", NUM), str7, fromObject2);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", NUM), str7, fromObject2);
                                }
                            } else if (ChatObject.isChannel(chat2) && !chat2.megagroup) {
                                this.messageText = LocaleController.getString("ChannelJoined", NUM);
                            } else if (this.messageOwner.peer_id.channel_id != 0) {
                                if (singleUserId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                    this.messageText = LocaleController.getString("ChannelMegaJoined", NUM);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", NUM), str7, fromObject2);
                                }
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionAddUserSelfYou", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", NUM), str7, fromObject2);
                            }
                        } else if (isOut()) {
                            long j2 = singleUserId;
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", this.messageOwner.action.users, users, sUsers);
                        } else {
                            CharSequence replaceWithLink6 = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", this.messageOwner.action.users, users, sUsers);
                            this.messageText = replaceWithLink6;
                            this.messageText = replaceWithLink(replaceWithLink6, str7, fromObject2);
                        }
                        TLRPC.User user13 = fromUser12;
                        str6 = str8;
                    } else if (!(this.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink)) {
                        TLRPC.Chat chat13 = null;
                        if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto) {
                            if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
                                chat13 = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                            }
                            TLRPC.Chat chat14 = chat13;
                            if (!ChatObject.isChannel(chat14) || chat14.megagroup) {
                                if (isOut()) {
                                    if (isVideoAvatar()) {
                                        this.messageText = LocaleController.getString("ActionYouChangedVideo", NUM);
                                    } else {
                                        this.messageText = LocaleController.getString("ActionYouChangedPhoto", NUM);
                                    }
                                } else if (isVideoAvatar()) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionChangedVideo", NUM), str7, fromObject2);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", NUM), str7, fromObject2);
                                }
                            } else if (isVideoAvatar()) {
                                this.messageText = LocaleController.getString("ActionChannelChangedVideo", NUM);
                            } else {
                                this.messageText = LocaleController.getString("ActionChannelChangedPhoto", NUM);
                            }
                            TLRPC.User user14 = fromUser12;
                            str6 = str8;
                        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle) {
                            if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
                                chat13 = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                            }
                            TLRPC.Chat chat15 = chat13;
                            if (ChatObject.isChannel(chat15) && !chat15.megagroup) {
                                this.messageText = LocaleController.getString("ActionChannelChangedTitle", NUM).replace(str9, this.messageOwner.action.title);
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionYouChangedTitle", NUM).replace(str9, this.messageOwner.action.title);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", NUM).replace(str9, this.messageOwner.action.title), str7, fromObject2);
                            }
                            TLRPC.User user15 = fromUser12;
                            str6 = str8;
                        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto) {
                            if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
                                chat13 = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                            }
                            TLRPC.Chat chat16 = chat13;
                            if (ChatObject.isChannel(chat16) && !chat16.megagroup) {
                                this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", NUM);
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionYouRemovedPhoto", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", NUM), str7, fromObject2);
                            }
                            TLRPC.User user16 = fromUser12;
                            str6 = str8;
                        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionTTLChange) {
                            if (this.messageOwner.action.ttl != 0) {
                                if (isOut()) {
                                    this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(this.messageOwner.action.ttl));
                                    TLRPC.User user17 = fromUser12;
                                    str6 = str8;
                                } else {
                                    this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(fromUser12), LocaleController.formatTTLString(this.messageOwner.action.ttl));
                                    TLRPC.User user18 = fromUser12;
                                    str6 = str8;
                                }
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
                                TLRPC.User user19 = fromUser12;
                                str6 = str8;
                            } else {
                                this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, UserObject.getFirstName(fromUser12));
                                TLRPC.User user20 = fromUser12;
                                str6 = str8;
                            }
                        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionSetMessagesTTL) {
                            TLRPC.TL_messageActionSetMessagesTTL action3 = (TLRPC.TL_messageActionSetMessagesTTL) this.messageOwner.action;
                            if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
                                chat13 = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                            }
                            TLRPC.Chat chat17 = chat13;
                            if (chat17 == null || chat17.megagroup) {
                                if (action3.period != 0) {
                                    if (isOut()) {
                                        this.messageText = LocaleController.formatString("ActionTTLYouChanged", NUM, LocaleController.formatTTLString(action3.period));
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.formatString("ActionTTLChanged", NUM, LocaleController.formatTTLString(action3.period)), str7, fromObject2);
                                    }
                                } else if (isOut()) {
                                    this.messageText = LocaleController.getString("ActionTTLYouDisabled", NUM);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionTTLDisabled", NUM), str7, fromObject2);
                                }
                            } else if (action3.period != 0) {
                                this.messageText = LocaleController.formatString("ActionTTLChannelChanged", NUM, LocaleController.formatTTLString(action3.period));
                            } else {
                                this.messageText = LocaleController.getString("ActionTTLChannelDisabled", NUM);
                            }
                            TLRPC.User user21 = fromUser12;
                            str6 = str8;
                        } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                            long time2 = ((long) this.messageOwner.date) * 1000;
                            if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                                StringBuilder sb = new StringBuilder();
                                str6 = str8;
                                sb.append(str6);
                                sb.append(this.messageOwner.date);
                                date = sb.toString();
                            } else {
                                date = LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(time2), LocaleController.getInstance().formatterDay.format(time2));
                                str6 = str8;
                            }
                            TLRPC.User to_user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                            if (to_user == null) {
                                to_user = getUser(abstractMap, longSparseArray, this.messageOwner.peer_id.user_id);
                            }
                            this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, to_user != null ? UserObject.getFirstName(to_user) : str6, date, this.messageOwner.action.title, this.messageOwner.action.address);
                            TLRPC.User user22 = fromUser12;
                        } else {
                            str6 = str8;
                            if (this.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined) {
                                fromUser2 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp) {
                                fromUser2 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                                this.messageText = LocaleController.formatString("NotificationContactNewPhoto", NUM, UserObject.getUserName(fromUser12));
                                TLRPC.User user23 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) {
                                if (this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                                    if (isOut()) {
                                        this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                                        TLRPC.User user24 = fromUser12;
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), str7, fromObject2);
                                        TLRPC.User user25 = fromUser12;
                                    }
                                } else if (this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                                    TLRPC.TL_decryptedMessageActionSetMessageTTL action4 = (TLRPC.TL_decryptedMessageActionSetMessageTTL) this.messageOwner.action.encryptedAction;
                                    if (action4.ttl_seconds != 0) {
                                        if (isOut()) {
                                            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(action4.ttl_seconds));
                                        } else {
                                            this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(fromUser12), LocaleController.formatTTLString(action4.ttl_seconds));
                                        }
                                    } else if (isOut()) {
                                        this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
                                    } else {
                                        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, UserObject.getFirstName(fromUser12));
                                    }
                                    TLRPC.User user26 = fromUser12;
                                } else {
                                    TLRPC.User user27 = fromUser12;
                                }
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken) {
                                if (isOut()) {
                                    this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                                    TLRPC.User user28 = fromUser12;
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), str7, fromObject2);
                                    TLRPC.User user29 = fromUser12;
                                }
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionCreatedBroadcastList) {
                                this.messageText = LocaleController.formatString("YouCreatedBroadcastList", NUM, new Object[0]);
                                TLRPC.User user30 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate) {
                                if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
                                    chat13 = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                                }
                                TLRPC.Chat chat18 = chat13;
                                if (!ChatObject.isChannel(chat18) || !chat18.megagroup) {
                                    this.messageText = LocaleController.getString("ActionCreateChannel", NUM);
                                } else {
                                    this.messageText = LocaleController.getString("ActionCreateMega", NUM);
                                }
                                TLRPC.User user31 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo) {
                                this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                                TLRPC.User user32 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom) {
                                this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                                TLRPC.User user33 = fromUser12;
                            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
                                if (fromUser12 == null) {
                                    chat = getChat(abstractMap2, longSparseArray2, this.messageOwner.peer_id.channel_id);
                                } else {
                                    chat = null;
                                }
                                generatePinMessageText(fromUser12, chat);
                            } else {
                                TLRPC.User fromUser13 = fromUser12;
                                if (this.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear) {
                                    this.messageText = LocaleController.getString("HistoryCleared", NUM);
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionGameScore) {
                                    generateGameMessageText(fromUser13);
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                                    TLRPC.TL_messageActionPhoneCall call = (TLRPC.TL_messageActionPhoneCall) this.messageOwner.action;
                                    boolean isMissed = call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
                                    if (!isFromUser() || this.messageOwner.from_id.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                        if (isMissed) {
                                            if (call.video) {
                                                this.messageText = LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
                                            } else {
                                                this.messageText = LocaleController.getString("CallMessageIncomingMissed", NUM);
                                            }
                                        } else if (call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) {
                                            if (call.video) {
                                                this.messageText = LocaleController.getString("CallMessageVideoIncomingDeclined", NUM);
                                            } else {
                                                this.messageText = LocaleController.getString("CallMessageIncomingDeclined", NUM);
                                            }
                                        } else if (call.video) {
                                            this.messageText = LocaleController.getString("CallMessageVideoIncoming", NUM);
                                        } else {
                                            this.messageText = LocaleController.getString("CallMessageIncoming", NUM);
                                        }
                                    } else if (isMissed) {
                                        if (call.video) {
                                            this.messageText = LocaleController.getString("CallMessageVideoOutgoingMissed", NUM);
                                        } else {
                                            this.messageText = LocaleController.getString("CallMessageOutgoingMissed", NUM);
                                        }
                                    } else if (call.video) {
                                        this.messageText = LocaleController.getString("CallMessageVideoOutgoing", NUM);
                                    } else {
                                        this.messageText = LocaleController.getString("CallMessageOutgoing", NUM);
                                    }
                                    if (call.duration > 0) {
                                        String duration = LocaleController.formatCallDuration(call.duration);
                                        String formatString = LocaleController.formatString("CallMessageWithDuration", NUM, this.messageText, duration);
                                        this.messageText = formatString;
                                        String _messageText = formatString.toString();
                                        int start = _messageText.indexOf(duration);
                                        if (start != -1) {
                                            SpannableString sp = new SpannableString(this.messageText);
                                            int end = duration.length() + start;
                                            if (start > 0) {
                                                TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall = call;
                                                if (_messageText.charAt(start - 1) == '(') {
                                                    start--;
                                                }
                                            }
                                            if (end < _messageText.length() && _messageText.charAt(end) == ')') {
                                                end++;
                                            }
                                            sp.setSpan(new TypefaceSpan(Typeface.DEFAULT), start, end, 0);
                                            this.messageText = sp;
                                        }
                                    }
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent) {
                                    generatePaymentSentMessageText(getUser(abstractMap, longSparseArray, getDialogId()));
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionBotAllowed) {
                                    String domain = ((TLRPC.TL_messageActionBotAllowed) this.messageOwner.action).domain;
                                    String text = LocaleController.getString("ActionBotAllowed", NUM);
                                    int start2 = text.indexOf("%1$s");
                                    SpannableString str11 = new SpannableString(String.format(text, new Object[]{domain}));
                                    if (start2 >= 0) {
                                        str11.setSpan(new URLSpanNoUnderlineBold("http://" + domain), start2, domain.length() + start2, 33);
                                    }
                                    this.messageText = str11;
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionSecureValuesSent) {
                                    TLRPC.TL_messageActionSecureValuesSent valuesSent = (TLRPC.TL_messageActionSecureValuesSent) this.messageOwner.action;
                                    StringBuilder str12 = new StringBuilder();
                                    int size = valuesSent.types.size();
                                    for (int a = 0; a < size; a++) {
                                        TLRPC.SecureValueType type2 = valuesSent.types.get(a);
                                        if (str12.length() > 0) {
                                            str12.append(", ");
                                        }
                                        if (type2 instanceof TLRPC.TL_secureValueTypePhone) {
                                            str12.append(LocaleController.getString("ActionBotDocumentPhone", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeEmail) {
                                            str12.append(LocaleController.getString("ActionBotDocumentEmail", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeAddress) {
                                            str12.append(LocaleController.getString("ActionBotDocumentAddress", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypePersonalDetails) {
                                            str12.append(LocaleController.getString("ActionBotDocumentIdentity", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypePassport) {
                                            str12.append(LocaleController.getString("ActionBotDocumentPassport", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeDriverLicense) {
                                            str12.append(LocaleController.getString("ActionBotDocumentDriverLicence", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeIdentityCard) {
                                            str12.append(LocaleController.getString("ActionBotDocumentIdentityCard", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeUtilityBill) {
                                            str12.append(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeBankStatement) {
                                            str12.append(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeRentalAgreement) {
                                            str12.append(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeInternalPassport) {
                                            str12.append(LocaleController.getString("ActionBotDocumentInternalPassport", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypePassportRegistration) {
                                            str12.append(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
                                        } else if (type2 instanceof TLRPC.TL_secureValueTypeTemporaryRegistration) {
                                            str12.append(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
                                        }
                                    }
                                    TLRPC.User user34 = null;
                                    if (this.messageOwner.peer_id != null) {
                                        user34 = getUser(abstractMap, longSparseArray, this.messageOwner.peer_id.user_id);
                                    }
                                    this.messageText = LocaleController.formatString("ActionBotDocuments", NUM, UserObject.getFirstName(user34), str12.toString());
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionWebViewDataSent) {
                                    this.messageText = LocaleController.formatString("ActionBotWebViewData", NUM, ((TLRPC.TL_messageActionWebViewDataSent) this.messageOwner.action).text);
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionSetChatTheme) {
                                    String emoticon = ((TLRPC.TL_messageActionSetChatTheme) this.messageOwner.action).emoticon;
                                    String userName = UserObject.getFirstName(fromUser13);
                                    boolean isUserSelf = UserObject.isUserSelf(fromUser13);
                                    if (TextUtils.isEmpty(emoticon)) {
                                        if (isUserSelf) {
                                            str3 = LocaleController.formatString("ChatThemeDisabledYou", NUM, new Object[0]);
                                        } else {
                                            str3 = LocaleController.formatString("ChatThemeDisabled", NUM, userName, emoticon);
                                        }
                                        this.messageText = str3;
                                    } else {
                                        if (isUserSelf) {
                                            str2 = LocaleController.formatString("ChatThemeChangedYou", NUM, emoticon);
                                        } else {
                                            str2 = LocaleController.formatString("ChatThemeChangedTo", NUM, userName, emoticon);
                                        }
                                        this.messageText = str2;
                                    }
                                } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByRequest) {
                                    if (UserObject.isUserSelf(fromUser13)) {
                                        if (ChatObject.isChannelAndNotMegaGroup(this.messageOwner.peer_id.channel_id, this.currentAccount)) {
                                            str = LocaleController.getString("RequestToJoinChannelApproved", NUM);
                                        } else {
                                            str = LocaleController.getString("RequestToJoinGroupApproved", NUM);
                                        }
                                        this.messageText = str;
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.getString("UserAcceptedToGroupAction", NUM), str7, fromObject2);
                                    }
                                }
                            }
                            this.messageText = LocaleController.formatString("NotificationContactJoined", NUM, UserObject.getUserName(fromUser2));
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionInviteYou", NUM);
                        TLRPC.User user35 = fromUser12;
                        str6 = str8;
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", NUM), str7, fromObject2);
                        TLRPC.User user36 = fromUser12;
                        str6 = str8;
                    }
                } else if (isOut()) {
                    this.messageText = LocaleController.getString("ActionYouCreateGroup", NUM);
                    TLRPC.User user37 = fromUser12;
                    TLObject tLObject3 = fromObject;
                    str6 = str8;
                } else {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", NUM), str7, fromObject);
                    TLRPC.User user38 = fromUser12;
                    str6 = str8;
                }
            }
        }
        if (this.messageText == null) {
            this.messageText = str6;
        }
    }

    public void setType() {
        int oldType = this.type;
        this.type = 1000;
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
            } else if (this.messageOwner.media.ttl_seconds != 0 && ((this.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty) || (getDocument() instanceof TLRPC.TL_documentEmpty))) {
                this.contentType = 1;
                this.type = 10;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDice) {
                this.type = 15;
                if (this.messageOwner.media.document == null) {
                    this.messageOwner.media.document = new TLRPC.TL_document();
                    this.messageOwner.media.document.file_reference = new byte[0];
                    this.messageOwner.media.document.mime_type = "application/x-tgsdice";
                    this.messageOwner.media.document.dc_id = Integer.MIN_VALUE;
                    this.messageOwner.media.document.id = -2147483648L;
                    TLRPC.TL_documentAttributeImageSize attributeImageSize = new TLRPC.TL_documentAttributeImageSize();
                    attributeImageSize.w = 512;
                    attributeImageSize.h = 512;
                    this.messageOwner.media.document.attributes.add(attributeImageSize);
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                this.type = 1;
            } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive)) {
                this.type = 4;
            } else if (isRoundVideo()) {
                this.type = 5;
            } else if (isVideo()) {
                this.type = 3;
            } else if (isVoice()) {
                this.type = 2;
            } else if (isMusic()) {
                this.type = 14;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaContact) {
                this.type = 12;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPoll) {
                this.type = 17;
                this.checkedVotes = new ArrayList<>();
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaUnsupported) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.Document document = getDocument();
                if (document == null || document.mime_type == null) {
                    this.type = 9;
                } else if (isGifDocument(document, hasValidGroupId())) {
                    this.type = 8;
                } else if (isSticker()) {
                    this.type = 13;
                } else if (isAnimatedSticker()) {
                    this.type = 15;
                } else {
                    this.type = 9;
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                this.type = 0;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
                this.type = 0;
            }
        } else if (message instanceof TLRPC.TL_messageService) {
            if (message.action instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto) || (this.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
                this.contentType = 1;
                this.type = 11;
            } else if (this.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) {
                if ((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) || (this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = 1;
                    this.type = 10;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else if (this.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                this.type = 16;
            } else {
                this.contentType = 1;
                this.type = 10;
            }
        }
        if (oldType != 1000 && oldType != this.type) {
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), (LongSparseArray<TLRPC.User>) null, (LongSparseArray<TLRPC.Chat>) null);
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        TextPaint paint;
        if (this.type != 0 || this.messageOwner.peer_id == null || (charSequence = this.messageText) == null || charSequence.length() == 0) {
            return false;
        }
        if (this.layoutCreated) {
            if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                this.layoutCreated = false;
            }
        }
        if (this.layoutCreated != 0) {
            return false;
        }
        this.layoutCreated = true;
        TLRPC.User fromUser = null;
        if (isFromUser()) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
            paint = Theme.chat_msgGameTextPaint;
        } else {
            paint = Theme.chat_msgTextPaint;
        }
        int[] emojiOnly = allowsBigEmoji() ? new int[1] : null;
        this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly, this.contentType == 0, this.viewRef);
        checkEmojiOnly(emojiOnly);
        generateLayout(fromUser);
        return true;
    }

    public void resetLayout() {
        this.layoutCreated = false;
    }

    public String getMimeType() {
        TLRPC.Document document = getDocument();
        if (document != null) {
            return document.mime_type;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) {
            TLRPC.WebDocument photo = ((TLRPC.TL_messageMediaInvoice) this.messageOwner.media).photo;
            if (photo != null) {
                return photo.mime_type;
            }
            return "";
        } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            return "image/jpeg";
        } else {
            if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || this.messageOwner.media.webpage.photo == null) {
                return "";
            }
            return "image/jpeg";
        }
    }

    public boolean canPreviewDocument() {
        return canPreviewDocument(getDocument());
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document) {
        return document != null && document.mime_type.equals("video/webm");
    }

    public static boolean isGifDocument(WebFile document) {
        return document != null && (document.mime_type.equals("image/gif") || isNewGifDocument(document));
    }

    public static boolean isGifDocument(TLRPC.Document document) {
        return isGifDocument(document, false);
    }

    public static boolean isGifDocument(TLRPC.Document document, boolean hasGroup) {
        return (document == null || document.mime_type == null || ((!document.mime_type.equals("image/gif") || hasGroup) && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isDocumentHasThumb(TLRPC.Document document) {
        if (document == null || document.thumbs.isEmpty()) {
            return false;
        }
        int N = document.thumbs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.PhotoSize photoSize = document.thumbs.get(a);
            if (photoSize != null && !(photoSize instanceof TLRPC.TL_photoSizeEmpty) && !(photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canPreviewDocument(TLRPC.Document document) {
        if (!(document == null || document.mime_type == null)) {
            String mime = document.mime_type.toLowerCase();
            if ((isDocumentHasThumb(document) && (mime.equals("image/png") || mime.equals("image/jpg") || mime.equals("image/jpeg"))) || (Build.VERSION.SDK_INT >= 26 && mime.equals("image/heic"))) {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        TLRPC.TL_documentAttributeImageSize size = (TLRPC.TL_documentAttributeImageSize) attribute;
                        if (size.w >= 6000 || size.h >= 6000) {
                            return false;
                        }
                        return true;
                    }
                }
            } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                String fileName = FileLoader.getDocumentFileName(document);
                if ((!fileName.startsWith("tg_secret_sticker") || !fileName.endsWith("json")) && !fileName.endsWith(".svg")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoDocument(TLRPC.Document document) {
        if (document == null || !"video/mp4".equals(document.mime_type)) {
            return false;
        }
        int width = 0;
        int height = 0;
        boolean round = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                width = attribute.w;
                height = attribute.h;
                round = attribute.round_message;
            }
        }
        if (!round || width > 1280 || height > 1280) {
            return false;
        }
        return true;
    }

    public static boolean isNewGifDocument(WebFile document) {
        if (document == null || !"video/mp4".equals(document.mime_type)) {
            return false;
        }
        int width = 0;
        int height = 0;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeAnimated) && (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                width = attribute.w;
                height = attribute.h;
            }
        }
        if (width > 1280 || height > 1280) {
            return false;
        }
        return true;
    }

    public static boolean isNewGifDocument(TLRPC.Document document) {
        if (document == null || !"video/mp4".equals(document.mime_type)) {
            return false;
        }
        int width = 0;
        int height = 0;
        boolean animated = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                animated = true;
            } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                width = attribute.w;
                height = attribute.h;
            }
        }
        if (!animated || width > 1280 || height > 1280) {
            return false;
        }
        return true;
    }

    public static boolean isSystemSignUp(MessageObject message) {
        if (message != null) {
            TLRPC.Message message2 = message.messageOwner;
            return (message2 instanceof TLRPC.TL_messageService) && (((TLRPC.TL_messageService) message2).action instanceof TLRPC.TL_messageActionContactSignUp);
        }
    }

    public void generateThumbs(boolean update) {
        ArrayList<TLRPC.PhotoSize> arrayList;
        ArrayList<TLRPC.PhotoSize> arrayList2;
        ArrayList<TLRPC.PhotoSize> arrayList3;
        ArrayList<TLRPC.PhotoSize> arrayList4;
        ArrayList<TLRPC.PhotoSize> arrayList5;
        ArrayList<TLRPC.PhotoSize> arrayList6;
        ArrayList<TLRPC.PhotoSize> arrayList7;
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action instanceof TLRPC.TL_messageActionChatEditPhoto) {
                TLRPC.Photo photo = this.messageOwner.action.photo;
                if (!update) {
                    this.photoThumbs = new ArrayList<>(photo.sizes);
                } else {
                    ArrayList<TLRPC.PhotoSize> arrayList8 = this.photoThumbs;
                    if (arrayList8 != null && !arrayList8.isEmpty()) {
                        for (int a = 0; a < this.photoThumbs.size(); a++) {
                            TLRPC.PhotoSize photoObject = this.photoThumbs.get(a);
                            int b = 0;
                            while (true) {
                                if (b >= photo.sizes.size()) {
                                    break;
                                }
                                TLRPC.PhotoSize size = photo.sizes.get(b);
                                if (!(size instanceof TLRPC.TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                    photoObject.location = size.location;
                                    break;
                                }
                                b++;
                            }
                        }
                    }
                }
                if (!(photo.dc_id == 0 || (arrayList7 = this.photoThumbs) == null)) {
                    int N = arrayList7.size();
                    for (int a2 = 0; a2 < N; a2++) {
                        TLRPC.FileLocation location = this.photoThumbs.get(a2).location;
                        if (location != null) {
                            location.dc_id = photo.dc_id;
                            location.file_reference = photo.file_reference;
                        }
                    }
                }
                this.photoThumbsObject = this.messageOwner.action.photo;
            }
        } else if (this.emojiAnimatedSticker != null) {
            if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) && isDocumentHasThumb(this.emojiAnimatedSticker)) {
                if (!update || (arrayList6 = this.photoThumbs) == null) {
                    ArrayList<TLRPC.PhotoSize> arrayList9 = new ArrayList<>();
                    this.photoThumbs = arrayList9;
                    arrayList9.addAll(this.emojiAnimatedSticker.thumbs);
                } else if (!arrayList6.isEmpty()) {
                    updatePhotoSizeLocations(this.photoThumbs, this.emojiAnimatedSticker.thumbs);
                }
                this.photoThumbsObject = this.emojiAnimatedSticker;
            }
        } else if (message.media != null && !(this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) {
            if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                TLRPC.Photo photo2 = this.messageOwner.media.photo;
                if (!update || !((arrayList5 = this.photoThumbs) == null || arrayList5.size() == photo2.sizes.size())) {
                    this.photoThumbs = new ArrayList<>(photo2.sizes);
                } else {
                    ArrayList<TLRPC.PhotoSize> arrayList10 = this.photoThumbs;
                    if (arrayList10 != null && !arrayList10.isEmpty()) {
                        for (int a3 = 0; a3 < this.photoThumbs.size(); a3++) {
                            TLRPC.PhotoSize photoObject2 = this.photoThumbs.get(a3);
                            if (photoObject2 != null) {
                                int b2 = 0;
                                while (true) {
                                    if (b2 >= photo2.sizes.size()) {
                                        break;
                                    }
                                    TLRPC.PhotoSize size2 = photo2.sizes.get(b2);
                                    if (size2 != null && !(size2 instanceof TLRPC.TL_photoSizeEmpty)) {
                                        if (!size2.type.equals(photoObject2.type)) {
                                            if ("s".equals(photoObject2.type) && (size2 instanceof TLRPC.TL_photoStrippedSize)) {
                                                this.photoThumbs.set(a3, size2);
                                                break;
                                            }
                                        } else {
                                            photoObject2.location = size2.location;
                                            break;
                                        }
                                    }
                                    b2++;
                                }
                            }
                        }
                    }
                }
                this.photoThumbsObject = this.messageOwner.media.photo;
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.Document document = getDocument();
                if (isDocumentHasThumb(document)) {
                    if (!update || (arrayList4 = this.photoThumbs) == null) {
                        ArrayList<TLRPC.PhotoSize> arrayList11 = new ArrayList<>();
                        this.photoThumbs = arrayList11;
                        arrayList11.addAll(document.thumbs);
                    } else if (!arrayList4.isEmpty()) {
                        updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                    }
                    this.photoThumbsObject = document;
                }
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                TLRPC.Document document2 = this.messageOwner.media.game.document;
                if (document2 != null && isDocumentHasThumb(document2)) {
                    if (!update) {
                        ArrayList<TLRPC.PhotoSize> arrayList12 = new ArrayList<>();
                        this.photoThumbs = arrayList12;
                        arrayList12.addAll(document2.thumbs);
                    } else {
                        ArrayList<TLRPC.PhotoSize> arrayList13 = this.photoThumbs;
                        if (arrayList13 != null && !arrayList13.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document2.thumbs);
                        }
                    }
                    this.photoThumbsObject = document2;
                }
                TLRPC.Photo photo3 = this.messageOwner.media.game.photo;
                if (photo3 != null) {
                    if (!update || (arrayList3 = this.photoThumbs2) == null) {
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
            } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                TLRPC.Photo photo4 = this.messageOwner.media.webpage.photo;
                TLRPC.Document document3 = this.messageOwner.media.webpage.document;
                if (photo4 != null) {
                    if (!update || (arrayList = this.photoThumbs) == null) {
                        this.photoThumbs = new ArrayList<>(photo4.sizes);
                    } else if (!arrayList.isEmpty()) {
                        updatePhotoSizeLocations(this.photoThumbs, photo4.sizes);
                    }
                    this.photoThumbsObject = photo4;
                } else if (document3 != null && isDocumentHasThumb(document3)) {
                    if (!update) {
                        ArrayList<TLRPC.PhotoSize> arrayList14 = new ArrayList<>();
                        this.photoThumbs = arrayList14;
                        arrayList14.addAll(document3.thumbs);
                    } else {
                        ArrayList<TLRPC.PhotoSize> arrayList15 = this.photoThumbs;
                        if (arrayList15 != null && !arrayList15.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document3.thumbs);
                        }
                    }
                    this.photoThumbsObject = document3;
                }
            }
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC.PhotoSize> o, ArrayList<TLRPC.PhotoSize> n) {
        int N = o.size();
        for (int a = 0; a < N; a++) {
            TLRPC.PhotoSize photoObject = o.get(a);
            if (photoObject != null) {
                int b = 0;
                int N2 = n.size();
                while (true) {
                    if (b >= N2) {
                        break;
                    }
                    TLRPC.PhotoSize size = n.get(b);
                    if (!(size instanceof TLRPC.TL_photoSizeEmpty) && !(size instanceof TLRPC.TL_photoCachedSize) && size != null && size.type.equals(photoObject.type)) {
                        photoObject.location = size.location;
                        break;
                    }
                    b++;
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence source, String param, ArrayList<Long> uids, AbstractMap<Long, TLRPC.User> usersDict, LongSparseArray<TLRPC.User> sUsersDict) {
        if (TextUtils.indexOf(source, param) < 0) {
            return source;
        }
        SpannableStringBuilder names = new SpannableStringBuilder("");
        for (int a = 0; a < uids.size(); a++) {
            TLRPC.User user = null;
            if (usersDict != null) {
                user = usersDict.get(uids.get(a));
            } else if (sUsersDict != null) {
                user = sUsersDict.get(uids.get(a).longValue());
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(uids.get(a));
            }
            if (user != null) {
                String name = UserObject.getUserName(user);
                int start = names.length();
                if (names.length() != 0) {
                    names.append(", ");
                }
                names.append(name);
                names.setSpan(new URLSpanNoUnderlineBold("" + user.id), start, name.length() + start, 33);
            }
        }
        return TextUtils.replace(source, new String[]{param}, new CharSequence[]{names});
    }

    public static CharSequence replaceWithLink(CharSequence source, String param, TLObject object) {
        String id;
        String name;
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        TLObject spanObject = null;
        if (object instanceof TLRPC.User) {
            name = UserObject.getUserName((TLRPC.User) object);
            id = "" + ((TLRPC.User) object).id;
        } else if (object instanceof TLRPC.Chat) {
            name = ((TLRPC.Chat) object).title;
            id = "" + (-((TLRPC.Chat) object).id);
        } else if (object instanceof TLRPC.TL_game) {
            id = "game";
            name = ((TLRPC.TL_game) object).title;
        } else if (object instanceof TLRPC.TL_chatInviteExported) {
            TLRPC.TL_chatInviteExported invite = (TLRPC.TL_chatInviteExported) object;
            spanObject = invite;
            name = invite.link;
            id = "invite";
        } else {
            name = "";
            id = "0";
        }
        String name2 = name.replace(10, ' ');
        SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name2}));
        URLSpanNoUnderlineBold span = new URLSpanNoUnderlineBold("" + id);
        span.setObject(spanObject);
        builder.setSpan(span, start, name2.length() + start, 33);
        return builder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = getDocument().mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        return ext.toUpperCase();
    }

    public String getFileName() {
        return getFileName(this.messageOwner);
    }

    public static String getFileName(TLRPC.Message messageOwner2) {
        TLRPC.PhotoSize sizeFull;
        if (messageOwner2.media instanceof TLRPC.TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument(messageOwner2));
        }
        if (messageOwner2.media instanceof TLRPC.TL_messageMediaPhoto) {
            ArrayList<TLRPC.PhotoSize> sizes = messageOwner2.media.photo.sizes;
            if (sizes.size() <= 0 || (sizeFull = FileLoader.getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) == null) {
                return "";
            }
            return FileLoader.getAttachFileName(sizeFull);
        } else if (messageOwner2.media instanceof TLRPC.TL_messageMediaWebPage) {
            return FileLoader.getAttachFileName(messageOwner2.media.webpage.document);
        } else {
            return "";
        }
    }

    public int getMediaType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
            return 3;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            return 0;
        }
        return 4;
    }

    private static boolean containsUrls(CharSequence message) {
        if (message == null || message.length() < 2 || message.length() > 20480) {
            return false;
        }
        int length = message.length();
        int digitsInRow = 0;
        int schemeSequence = 0;
        int dotSequence = 0;
        char lastChar = 0;
        for (int i = 0; i < length; i++) {
            char c = message.charAt(i);
            if (c >= '0' && c <= '9') {
                digitsInRow++;
                if (digitsInRow >= 6) {
                    return true;
                }
                schemeSequence = 0;
                dotSequence = 0;
            } else if (c == ' ' || digitsInRow <= 0) {
                digitsInRow = 0;
            }
            if (((c == '@' || c == '#' || c == '/' || c == '$') && i == 0) || (i != 0 && (message.charAt(i - 1) == ' ' || message.charAt(i - 1) == 10))) {
                return true;
            }
            if (c == ':') {
                if (schemeSequence == 0) {
                    schemeSequence = 1;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '/') {
                if (schemeSequence == 2) {
                    return true;
                }
                if (schemeSequence == 1) {
                    schemeSequence++;
                } else {
                    schemeSequence = 0;
                }
            } else if (c == '.') {
                if (dotSequence != 0 || lastChar == ' ') {
                    dotSequence = 0;
                } else {
                    dotSequence++;
                }
            } else if (c != ' ' && lastChar == '.' && dotSequence == 1) {
                return true;
            } else {
                dotSequence = 0;
            }
            lastChar = c;
        }
        return false;
    }

    public void generateLinkDescription() {
        if (this.linkDescription == null) {
            int hashtagsType = 0;
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TLRPC.TL_webPage) && this.messageOwner.media.webpage.description != null) {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
                String siteName = this.messageOwner.media.webpage.site_name;
                if (siteName != null) {
                    siteName = siteName.toLowerCase();
                }
                if ("instagram".equals(siteName)) {
                    hashtagsType = 1;
                } else if ("twitter".equals(siteName)) {
                    hashtagsType = 2;
                }
            } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) && this.messageOwner.media.game.description != null) {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
            } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice) && this.messageOwner.media.description != null) {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.description);
            }
            if (!TextUtils.isEmpty(this.linkDescription)) {
                if (containsUrls(this.linkDescription)) {
                    try {
                        AndroidUtilities.addLinks((Spannable) this.linkDescription, 1);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                CharSequence replaceEmoji = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, this.contentType == 0, this.viewRef);
                this.linkDescription = replaceEmoji;
                if (hashtagsType != 0) {
                    if (!(replaceEmoji instanceof Spannable)) {
                        this.linkDescription = new SpannableStringBuilder(this.linkDescription);
                    }
                    addUrlsByPattern(isOutOwner(), this.linkDescription, false, hashtagsType, 0, false);
                }
            }
        }
    }

    public CharSequence getVoiceTranscription() {
        TLRPC.Message message = this.messageOwner;
        if (message == null || message.voiceTranscription == null) {
            return null;
        }
        if (TextUtils.isEmpty(this.messageOwner.voiceTranscription)) {
            SpannableString ssb = new SpannableString(LocaleController.getString("NoWordsRecognized", NUM));
            ssb.setSpan(new CharacterStyle() {
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setTextSize(textPaint.getTextSize() * 0.8f);
                    textPaint.setColor(Theme.chat_timePaint.getColor());
                }
            }, 0, ssb.length(), 33);
            return ssb;
        }
        String str = this.messageOwner.voiceTranscription;
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        return Emoji.replaceEmoji(str, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, this.contentType == 0, this.viewRef);
    }

    public float measureVoiceTranscriptionHeight() {
        StaticLayout captionLayout;
        CharSequence voiceTranscription = getVoiceTranscription();
        if (voiceTranscription == null) {
            return 0.0f;
        }
        int width = AndroidUtilities.displaySize.x - AndroidUtilities.dp(needDrawAvatar() ? 147.0f : 95.0f);
        if (Build.VERSION.SDK_INT >= 24) {
            captionLayout = StaticLayout.Builder.obtain(voiceTranscription, 0, voiceTranscription.length(), Theme.chat_msgTextPaint, width).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).build();
        } else {
            captionLayout = new StaticLayout(voiceTranscription, Theme.chat_msgTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        return (float) captionLayout.getHeight();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.messageOwner;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVoiceTranscriptionOpen() {
        /*
            r1 = this;
            boolean r0 = r1.isVoice()
            if (r0 == 0) goto L_0x002e
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            if (r0 == 0) goto L_0x002e
            boolean r0 = r0.voiceTranscriptionOpen
            if (r0 == 0) goto L_0x002e
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.voiceTranscription
            if (r0 == 0) goto L_0x002e
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r0 = r0.voiceTranscriptionFinal
            if (r0 != 0) goto L_0x0020
            boolean r0 = org.telegram.ui.Components.TranscribeButton.isTranscribing(r1)
            if (r0 == 0) goto L_0x002e
        L_0x0020:
            int r0 = r1.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isPremium()
            if (r0 == 0) goto L_0x002e
            r0 = 1
            goto L_0x002f
        L_0x002e:
            r0 = 0
        L_0x002f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isVoiceTranscriptionOpen():boolean");
    }

    public void generateCaption() {
        boolean hasEntities;
        if (this.caption == null && !isRoundVideo() && !isMediaEmpty() && !(this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) && !TextUtils.isEmpty(this.messageOwner.message)) {
            boolean z = false;
            this.caption = Emoji.replaceEmoji(this.messageOwner.message, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, this.contentType == 0, this.viewRef);
            if (this.messageOwner.send_state != 0) {
                hasEntities = false;
            } else {
                hasEntities = !this.messageOwner.entities.isEmpty();
            }
            if (!hasEntities && (this.eventId != 0 || (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_old) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_layer68) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto_layer74) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_old) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_layer68) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument_layer74) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0))) {
                z = true;
            }
            boolean useManualParse = z;
            if (useManualParse) {
                if (containsUrls(this.caption)) {
                    try {
                        AndroidUtilities.addLinks((Spannable) this.caption, 5);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                addUrlsByPattern(isOutOwner(), this.caption, true, 0, 0, true);
            }
            addEntitiesToText(this.caption, useManualParse);
            if (isVideo()) {
                addUrlsByPattern(isOutOwner(), this.caption, true, 3, getDuration(), false);
            } else if (isMusic() || isVoice()) {
                addUrlsByPattern(isOutOwner(), this.caption, true, 4, getDuration(), false);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:80:0x01d3 A[Catch:{ Exception -> 0x0256 }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01da A[Catch:{ Exception -> 0x0256 }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0233 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0256 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addUrlsByPattern(boolean r24, java.lang.CharSequence r25, boolean r26, int r27, int r28, boolean r29) {
        /*
            r1 = r25
            r2 = r27
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            r0 = 4
            r3 = 3
            r4 = 1
            if (r2 == r3) goto L_0x0037
            if (r2 != r0) goto L_0x000f
            goto L_0x0037
        L_0x000f:
            if (r2 != r4) goto L_0x0024
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x0256 }
            if (r5 != 0) goto L_0x001d
            java.lang.String r5 = "(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x0256 }
            instagramUrlPattern = r5     // Catch:{ Exception -> 0x0256 }
        L_0x001d:
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x0256 }
            java.util.regex.Matcher r5 = r5.matcher(r1)     // Catch:{ Exception -> 0x0256 }
            goto L_0x0049
        L_0x0024:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x0256 }
            if (r5 != 0) goto L_0x0030
            java.lang.String r5 = "(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[^0-9][\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x0256 }
            urlPattern = r5     // Catch:{ Exception -> 0x0256 }
        L_0x0030:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x0256 }
            java.util.regex.Matcher r5 = r5.matcher(r1)     // Catch:{ Exception -> 0x0256 }
            goto L_0x0049
        L_0x0037:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x0256 }
            if (r5 != 0) goto L_0x0043
            java.lang.String r5 = "\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b([^\\n]*)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x0256 }
            videoTimeUrlPattern = r5     // Catch:{ Exception -> 0x0256 }
        L_0x0043:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x0256 }
            java.util.regex.Matcher r5 = r5.matcher(r1)     // Catch:{ Exception -> 0x0256 }
        L_0x0049:
            r6 = r1
            android.text.Spannable r6 = (android.text.Spannable) r6     // Catch:{ Exception -> 0x0256 }
        L_0x004c:
            boolean r7 = r5.find()     // Catch:{ Exception -> 0x0256 }
            if (r7 == 0) goto L_0x0255
            int r7 = r5.start()     // Catch:{ Exception -> 0x0256 }
            int r8 = r5.end()     // Catch:{ Exception -> 0x0256 }
            r9 = 0
            r11 = 2
            if (r2 == r3) goto L_0x014c
            if (r2 != r0) goto L_0x0062
            goto L_0x014c
        L_0x0062:
            char r12 = r1.charAt(r7)     // Catch:{ Exception -> 0x0256 }
            r13 = 47
            r14 = 35
            r15 = 64
            if (r2 == 0) goto L_0x007f
            if (r12 == r15) goto L_0x0074
            if (r12 == r14) goto L_0x0074
            int r7 = r7 + 1
        L_0x0074:
            char r16 = r1.charAt(r7)     // Catch:{ Exception -> 0x0256 }
            r12 = r16
            if (r12 == r15) goto L_0x008b
            if (r12 == r14) goto L_0x008b
            goto L_0x004c
        L_0x007f:
            if (r12 == r15) goto L_0x008b
            if (r12 == r14) goto L_0x008b
            if (r12 == r13) goto L_0x008b
            r14 = 36
            if (r12 == r14) goto L_0x008b
            int r7 = r7 + 1
        L_0x008b:
            if (r2 != r4) goto L_0x00d5
            if (r12 != r15) goto L_0x00b2
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0256 }
            r13.<init>()     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = "https://instagram.com/"
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            int r14 = r7 + 1
            java.lang.CharSequence r14 = r1.subSequence(r14, r8)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0256 }
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r13)     // Catch:{ Exception -> 0x0256 }
            r9 = r11
            goto L_0x0231
        L_0x00b2:
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0256 }
            r13.<init>()     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = "https://www.instagram.com/explore/tags/"
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            int r14 = r7 + 1
            java.lang.CharSequence r14 = r1.subSequence(r14, r8)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0256 }
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r13)     // Catch:{ Exception -> 0x0256 }
            r9 = r11
            goto L_0x0231
        L_0x00d5:
            if (r2 != r11) goto L_0x011f
            if (r12 != r15) goto L_0x00fc
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0256 }
            r13.<init>()     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = "https://twitter.com/"
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            int r14 = r7 + 1
            java.lang.CharSequence r14 = r1.subSequence(r14, r8)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0256 }
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r13)     // Catch:{ Exception -> 0x0256 }
            r9 = r11
            goto L_0x0231
        L_0x00fc:
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0256 }
            r13.<init>()     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = "https://twitter.com/hashtag/"
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            int r14 = r7 + 1
            java.lang.CharSequence r14 = r1.subSequence(r14, r8)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0256 }
            r13.append(r14)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r13)     // Catch:{ Exception -> 0x0256 }
            r9 = r11
            goto L_0x0231
        L_0x011f:
            char r11 = r1.charAt(r7)     // Catch:{ Exception -> 0x0256 }
            if (r11 != r13) goto L_0x013c
            if (r26 == 0) goto L_0x0231
            org.telegram.ui.Components.URLSpanBotCommand r11 = new org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x0256 }
            java.lang.CharSequence r13 = r1.subSequence(r7, r8)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0256 }
            if (r24 == 0) goto L_0x0135
            r14 = 1
            goto L_0x0136
        L_0x0135:
            r14 = 0
        L_0x0136:
            r11.<init>(r13, r14)     // Catch:{ Exception -> 0x0256 }
            r9 = r11
            goto L_0x0231
        L_0x013c:
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.CharSequence r13 = r1.subSequence(r7, r8)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0256 }
            r11.<init>(r13)     // Catch:{ Exception -> 0x0256 }
            r9 = r11
            goto L_0x0231
        L_0x014c:
            int r12 = r5.groupCount()     // Catch:{ Exception -> 0x0256 }
            int r13 = r5.start(r4)     // Catch:{ Exception -> 0x0256 }
            int r14 = r5.end(r4)     // Catch:{ Exception -> 0x0256 }
            int r15 = r5.start(r11)     // Catch:{ Exception -> 0x0256 }
            int r11 = r5.end(r11)     // Catch:{ Exception -> 0x0256 }
            int r16 = r5.start(r3)     // Catch:{ Exception -> 0x0256 }
            r17 = r16
            int r16 = r5.end(r3)     // Catch:{ Exception -> 0x0256 }
            r18 = r16
            int r16 = r5.start(r0)     // Catch:{ Exception -> 0x0256 }
            r19 = r16
            int r16 = r5.end(r0)     // Catch:{ Exception -> 0x0256 }
            r20 = r16
            java.lang.CharSequence r16 = r1.subSequence(r15, r11)     // Catch:{ Exception -> 0x0256 }
            java.lang.Integer r16 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r16)     // Catch:{ Exception -> 0x0256 }
            int r16 = r16.intValue()     // Catch:{ Exception -> 0x0256 }
            r0 = r17
            r4 = r18
            java.lang.CharSequence r18 = r1.subSequence(r0, r4)     // Catch:{ Exception -> 0x0256 }
            java.lang.Integer r18 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r18)     // Catch:{ Exception -> 0x0256 }
            int r18 = r18.intValue()     // Catch:{ Exception -> 0x0256 }
            if (r13 < 0) goto L_0x01a5
            if (r14 < 0) goto L_0x01a5
            java.lang.CharSequence r21 = r1.subSequence(r13, r14)     // Catch:{ Exception -> 0x0256 }
            java.lang.Integer r21 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r21)     // Catch:{ Exception -> 0x0256 }
            int r21 = r21.intValue()     // Catch:{ Exception -> 0x0256 }
            goto L_0x01a7
        L_0x01a5:
            r21 = -1
        L_0x01a7:
            r10 = r19
            if (r10 < 0) goto L_0x01b9
            r3 = r20
            if (r3 >= 0) goto L_0x01b0
            goto L_0x01bb
        L_0x01b0:
            java.lang.CharSequence r20 = r1.subSequence(r10, r3)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r20 = r20.toString()     // Catch:{ Exception -> 0x0256 }
            goto L_0x01bd
        L_0x01b9:
            r3 = r20
        L_0x01bb:
            r20 = 0
        L_0x01bd:
            r22 = r20
            if (r10 >= 0) goto L_0x01c3
            if (r3 < 0) goto L_0x01c4
        L_0x01c3:
            r8 = r4
        L_0x01c4:
            r20 = r0
            java.lang.Class<android.text.style.URLSpan> r0 = android.text.style.URLSpan.class
            java.lang.Object[] r0 = r6.getSpans(r7, r8, r0)     // Catch:{ Exception -> 0x0256 }
            android.text.style.URLSpan[] r0 = (android.text.style.URLSpan[]) r0     // Catch:{ Exception -> 0x0256 }
            if (r0 == 0) goto L_0x01da
            int r1 = r0.length     // Catch:{ Exception -> 0x0256 }
            if (r1 <= 0) goto L_0x01da
            r1 = r25
            r0 = 4
            r3 = 3
            r4 = 1
            goto L_0x004c
        L_0x01da:
            int r1 = r16 * 60
            int r18 = r18 + r1
            if (r21 <= 0) goto L_0x01e9
            int r1 = r21 * 60
            int r1 = r1 * 60
            int r18 = r18 + r1
            r1 = r18
            goto L_0x01eb
        L_0x01e9:
            r1 = r18
        L_0x01eb:
            r18 = r3
            r3 = r28
            if (r1 <= r3) goto L_0x01f8
            r1 = r25
            r0 = 4
            r3 = 3
            r4 = 1
            goto L_0x004c
        L_0x01f8:
            r23 = r0
            r0 = 3
            if (r2 != r0) goto L_0x0215
            org.telegram.ui.Components.URLSpanNoUnderline r0 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0256 }
            r2.<init>()     // Catch:{ Exception -> 0x0256 }
            java.lang.String r3 = "video?"
            r2.append(r3)     // Catch:{ Exception -> 0x0256 }
            r2.append(r1)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0256 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0256 }
            r9 = r0
            goto L_0x022c
        L_0x0215:
            org.telegram.ui.Components.URLSpanNoUnderline r0 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0256 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0256 }
            r2.<init>()     // Catch:{ Exception -> 0x0256 }
            java.lang.String r3 = "audio?"
            r2.append(r3)     // Catch:{ Exception -> 0x0256 }
            r2.append(r1)     // Catch:{ Exception -> 0x0256 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0256 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0256 }
            r9 = r0
        L_0x022c:
            r0 = r22
            r9.label = r0     // Catch:{ Exception -> 0x0256 }
        L_0x0231:
            if (r9 == 0) goto L_0x024c
            if (r29 == 0) goto L_0x0248
            java.lang.Class<android.text.style.ClickableSpan> r0 = android.text.style.ClickableSpan.class
            java.lang.Object[] r0 = r6.getSpans(r7, r8, r0)     // Catch:{ Exception -> 0x0256 }
            android.text.style.ClickableSpan[] r0 = (android.text.style.ClickableSpan[]) r0     // Catch:{ Exception -> 0x0256 }
            if (r0 == 0) goto L_0x0248
            int r1 = r0.length     // Catch:{ Exception -> 0x0256 }
            if (r1 <= 0) goto L_0x0248
            r1 = 0
            r2 = r0[r1]     // Catch:{ Exception -> 0x0256 }
            r6.removeSpan(r2)     // Catch:{ Exception -> 0x0256 }
        L_0x0248:
            r0 = 0
            r6.setSpan(r9, r7, r8, r0)     // Catch:{ Exception -> 0x0256 }
        L_0x024c:
            r1 = r25
            r2 = r27
            r0 = 4
            r3 = 3
            r4 = 1
            goto L_0x004c
        L_0x0255:
            goto L_0x025a
        L_0x0256:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x025a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addUrlsByPattern(boolean, java.lang.CharSequence, boolean, int, int, boolean):void");
    }

    public static int[] getWebDocumentWidthAndHeight(TLRPC.WebDocument document) {
        if (document == null) {
            return null;
        }
        int a = 0;
        int size = document.attributes.size();
        while (a < size) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                return new int[]{attribute.w, attribute.h};
            } else if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return new int[]{attribute.w, attribute.h};
            } else {
                a++;
            }
        }
        return null;
    }

    public static int getWebDocumentDuration(TLRPC.WebDocument document) {
        if (document == null) {
            return 0;
        }
        int size = document.attributes.size();
        for (int a = 0; a < size; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.duration;
            }
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(TLRPC.BotInlineResult inlineResult) {
        int[] result = getWebDocumentWidthAndHeight(inlineResult.content);
        if (result != null) {
            return result;
        }
        int[] result2 = getWebDocumentWidthAndHeight(inlineResult.thumb);
        if (result2 == null) {
            return new int[]{0, 0};
        }
        return result2;
    }

    public static int getInlineResultDuration(TLRPC.BotInlineResult inlineResult) {
        int result = getWebDocumentDuration(inlineResult.content);
        if (result == 0) {
            return getWebDocumentDuration(inlineResult.thumb);
        }
        return result;
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
            if (r4 == 0) goto L_0x0022
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.photoThumbs
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0020
        L_0x0014:
            boolean r0 = r5.isMusic()
            if (r0 != 0) goto L_0x0020
            boolean r0 = r5.isDocument()
            if (r0 == 0) goto L_0x0022
        L_0x0020:
            r0 = 1
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
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

    public static void addLinks(boolean isOut, CharSequence messageText2) {
        addLinks(isOut, messageText2, true, false);
    }

    public static void addLinks(boolean isOut, CharSequence messageText2, boolean botCommands, boolean check) {
        addLinks(isOut, messageText2, botCommands, check, false);
    }

    public static void addLinks(boolean isOut, CharSequence messageText2, boolean botCommands, boolean check, boolean internalOnly) {
        if ((messageText2 instanceof Spannable) && containsUrls(messageText2)) {
            if (messageText2.length() < 1000) {
                try {
                    AndroidUtilities.addLinks((Spannable) messageText2, 5, internalOnly);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    AndroidUtilities.addLinks((Spannable) messageText2, 1, internalOnly);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            addUrlsByPattern(isOut, messageText2, botCommands, 0, 0, check);
        }
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence text, boolean useManualParse) {
        return addEntitiesToText(text, false, useManualParse);
    }

    public boolean addEntitiesToText(CharSequence text, boolean photoViewer, boolean useManualParse) {
        if (text == null) {
            return false;
        }
        if (this.isRestrictedMessage) {
            ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
            TLRPC.TL_messageEntityItalic entityItalic = new TLRPC.TL_messageEntityItalic();
            entityItalic.offset = 0;
            entityItalic.length = text.length();
            entities.add(entityItalic);
            return addEntitiesToText(text, entities, isOutOwner(), true, photoViewer, useManualParse);
        }
        return addEntitiesToText(text, this.messageOwner.entities, isOutOwner(), true, photoViewer, useManualParse);
    }

    public static boolean addEntitiesToText(CharSequence text, ArrayList<TLRPC.MessageEntity> entities, boolean out, boolean usernames, boolean photoViewer, boolean useManualParse) {
        byte t;
        int count;
        int count2;
        String url;
        int i;
        int N;
        URLSpan[] spans;
        boolean hasUrls;
        int b;
        int N2;
        CharSequence charSequence = text;
        boolean hasUrls2 = false;
        if (!(charSequence instanceof Spannable)) {
            return false;
        }
        Spannable spannable = (Spannable) charSequence;
        URLSpan[] spans2 = (URLSpan[]) spannable.getSpans(0, text.length(), URLSpan.class);
        if (spans2 != null && spans2.length > 0) {
            hasUrls2 = true;
        }
        if (entities.isEmpty()) {
            return hasUrls2;
        }
        if (photoViewer) {
            t = 2;
        } else if (out) {
            t = 1;
        } else {
            t = 0;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(entities);
        Collections.sort(arrayList2, MessageObject$$ExternalSyntheticLambda1.INSTANCE);
        int a = 0;
        int N3 = arrayList2.size();
        while (a < N3) {
            TLRPC.MessageEntity entity = (TLRPC.MessageEntity) arrayList2.get(a);
            if (entity.length <= 0 || entity.offset < 0) {
                hasUrls = hasUrls2;
                N = N3;
                spans = spans2;
            } else if (entity.offset >= text.length()) {
                hasUrls = hasUrls2;
                N = N3;
                spans = spans2;
            } else {
                if (entity.offset + entity.length > text.length()) {
                    entity.length = text.length() - entity.offset;
                }
                if ((!useManualParse || (entity instanceof TLRPC.TL_messageEntityBold) || (entity instanceof TLRPC.TL_messageEntityItalic) || (entity instanceof TLRPC.TL_messageEntityStrike) || (entity instanceof TLRPC.TL_messageEntityUnderline) || (entity instanceof TLRPC.TL_messageEntityBlockquote) || (entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre) || (entity instanceof TLRPC.TL_messageEntityMentionName) || (entity instanceof TLRPC.TL_inputMessageEntityMentionName) || (entity instanceof TLRPC.TL_messageEntityTextUrl) || (entity instanceof TLRPC.TL_messageEntitySpoiler)) && spans2 != null && spans2.length > 0) {
                    for (int b2 = 0; b2 < spans2.length; b2++) {
                        if (spans2[b2] != null) {
                            int start = spannable.getSpanStart(spans2[b2]);
                            int end = spannable.getSpanEnd(spans2[b2]);
                            if ((entity.offset <= start && entity.offset + entity.length >= start) || (entity.offset <= end && entity.offset + entity.length >= end)) {
                                spannable.removeSpan(spans2[b2]);
                                spans2[b2] = null;
                            }
                        }
                    }
                }
                TextStyleSpan.TextStyleRun newRun = new TextStyleSpan.TextStyleRun();
                newRun.start = entity.offset;
                newRun.end = newRun.start + entity.length;
                if (entity instanceof TLRPC.TL_messageEntitySpoiler) {
                    newRun.flags = 256;
                } else if (entity instanceof TLRPC.TL_messageEntityStrike) {
                    newRun.flags = 8;
                } else if (entity instanceof TLRPC.TL_messageEntityUnderline) {
                    newRun.flags = 16;
                } else if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                    newRun.flags = 32;
                } else if (entity instanceof TLRPC.TL_messageEntityBold) {
                    newRun.flags = 1;
                } else if (entity instanceof TLRPC.TL_messageEntityItalic) {
                    newRun.flags = 2;
                } else if ((entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityPre)) {
                    newRun.flags = 4;
                } else if (entity instanceof TLRPC.TL_messageEntityMentionName) {
                    if (!usernames) {
                        hasUrls = hasUrls2;
                        N = N3;
                        spans = spans2;
                    } else {
                        newRun.flags = 64;
                        newRun.urlEntity = entity;
                    }
                } else if (entity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                    if (!usernames) {
                        hasUrls = hasUrls2;
                        N = N3;
                        spans = spans2;
                    } else {
                        newRun.flags = 64;
                        newRun.urlEntity = entity;
                    }
                } else if (useManualParse && !(entity instanceof TLRPC.TL_messageEntityTextUrl)) {
                    hasUrls = hasUrls2;
                    N = N3;
                    spans = spans2;
                } else if (((entity instanceof TLRPC.TL_messageEntityUrl) || (entity instanceof TLRPC.TL_messageEntityTextUrl)) && Browser.isPassportUrl(entity.url)) {
                    hasUrls = hasUrls2;
                    N = N3;
                    spans = spans2;
                } else if (!(entity instanceof TLRPC.TL_messageEntityMention) || usernames) {
                    newRun.flags = 128;
                    newRun.urlEntity = entity;
                } else {
                    hasUrls = hasUrls2;
                    N = N3;
                    spans = spans2;
                }
                int b3 = 0;
                int N22 = arrayList.size();
                while (b < N22) {
                    TextStyleSpan.TextStyleRun run = (TextStyleSpan.TextStyleRun) arrayList.get(b);
                    boolean hasUrls3 = hasUrls2;
                    URLSpan[] spans3 = spans2;
                    if ((run.flags & 256) == 0 || newRun.start < run.start || newRun.end > run.end) {
                        if (newRun.start > run.start) {
                            if (newRun.start < run.end) {
                                if (newRun.end < run.end) {
                                    TextStyleSpan.TextStyleRun r = new TextStyleSpan.TextStyleRun(newRun);
                                    r.merge(run);
                                    int b4 = b + 1;
                                    arrayList.add(b4, r);
                                    TextStyleSpan.TextStyleRun r2 = new TextStyleSpan.TextStyleRun(run);
                                    r2.start = newRun.end;
                                    b = b4 + 1;
                                    N22 = N22 + 1 + 1;
                                    arrayList.add(b, r2);
                                } else {
                                    TextStyleSpan.TextStyleRun r3 = new TextStyleSpan.TextStyleRun(newRun);
                                    r3.merge(run);
                                    r3.end = run.end;
                                    b++;
                                    N22++;
                                    arrayList.add(b, r3);
                                }
                                int temp = newRun.start;
                                newRun.start = run.end;
                                run.end = temp;
                                N2 = N3;
                                b3 = b + 1;
                                hasUrls2 = hasUrls3;
                                spans2 = spans3;
                                N3 = N2;
                            }
                        } else if (run.start < newRun.end) {
                            int temp2 = run.start;
                            N2 = N3;
                            if (newRun.end == run.end) {
                                run.merge(newRun);
                            } else if (newRun.end < run.end) {
                                TextStyleSpan.TextStyleRun r4 = new TextStyleSpan.TextStyleRun(run);
                                r4.merge(newRun);
                                r4.end = newRun.end;
                                b++;
                                N22++;
                                arrayList.add(b, r4);
                                run.start = newRun.end;
                            } else {
                                TextStyleSpan.TextStyleRun r5 = new TextStyleSpan.TextStyleRun(newRun);
                                r5.start = run.end;
                                b++;
                                N22++;
                                arrayList.add(b, r5);
                                run.merge(newRun);
                            }
                            newRun.end = temp2;
                            b3 = b + 1;
                            hasUrls2 = hasUrls3;
                            spans2 = spans3;
                            N3 = N2;
                        }
                    }
                    N2 = N3;
                    b3 = b + 1;
                    hasUrls2 = hasUrls3;
                    spans2 = spans3;
                    N3 = N2;
                }
                hasUrls = hasUrls2;
                N = N3;
                spans = spans2;
                if (newRun.start < newRun.end) {
                    arrayList.add(newRun);
                }
            }
            a++;
            hasUrls2 = hasUrls;
            spans2 = spans;
            N3 = N;
        }
        boolean hasUrls4 = hasUrls2;
        int i2 = N3;
        URLSpan[] uRLSpanArr = spans2;
        String str = null;
        int count3 = arrayList.size();
        int a2 = 0;
        while (a2 < count3) {
            TextStyleSpan.TextStyleRun run2 = (TextStyleSpan.TextStyleRun) arrayList.get(a2);
            boolean setRun = false;
            String url2 = run2.urlEntity != null ? TextUtils.substring(charSequence, run2.urlEntity.offset, run2.urlEntity.offset + run2.urlEntity.length) : str;
            if (run2.urlEntity instanceof TLRPC.TL_messageEntityBotCommand) {
                spannable.setSpan(new URLSpanBotCommand(url2, t, run2), run2.start, run2.end, 33);
                count = count3;
                String str2 = url2;
                count2 = 256;
            } else {
                if ((run2.urlEntity instanceof TLRPC.TL_messageEntityHashtag) || (run2.urlEntity instanceof TLRPC.TL_messageEntityMention)) {
                    count = count3;
                    url = url2;
                    count2 = 256;
                    i = 33;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityCashtag) {
                    count = count3;
                    url = url2;
                    count2 = 256;
                    i = 33;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityEmail) {
                    spannable.setSpan(new URLSpanReplacement("mailto:" + url2, run2), run2.start, run2.end, 33);
                    count = count3;
                    String str3 = url2;
                    count2 = 256;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityUrl) {
                    if (!url2.toLowerCase().contains("://")) {
                        spannable.setSpan(new URLSpanBrowser("http://" + url2, run2), run2.start, run2.end, 33);
                    } else {
                        spannable.setSpan(new URLSpanBrowser(url2, run2), run2.start, run2.end, 33);
                    }
                    count = count3;
                    hasUrls4 = true;
                    String str4 = url2;
                    count2 = 256;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityBankCard) {
                    spannable.setSpan(new URLSpanNoUnderline("card:" + url2, run2), run2.start, run2.end, 33);
                    count = count3;
                    hasUrls4 = true;
                    String str5 = url2;
                    count2 = 256;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityPhone) {
                    String tel = PhoneFormat.stripExceptNumbers(url2);
                    if (url2.startsWith("+")) {
                        tel = "+" + tel;
                    }
                    spannable.setSpan(new URLSpanBrowser("tel:" + tel, run2), run2.start, run2.end, 33);
                    count = count3;
                    hasUrls4 = true;
                    String str6 = url2;
                    count2 = 256;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                    spannable.setSpan(new URLSpanReplacement(run2.urlEntity.url, run2), run2.start, run2.end, 33);
                    count = count3;
                    String str7 = url2;
                    count2 = 256;
                } else if (run2.urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("");
                    String str8 = url2;
                    sb.append(((TLRPC.TL_messageEntityMentionName) run2.urlEntity).user_id);
                    spannable.setSpan(new URLSpanUserMention(sb.toString(), t, run2), run2.start, run2.end, 33);
                    count = count3;
                    count2 = 256;
                } else {
                    if (run2.urlEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                        spannable.setSpan(new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName) run2.urlEntity).user_id.user_id, t, run2), run2.start, run2.end, 33);
                        count = count3;
                        count2 = 256;
                    } else if ((run2.flags & 4) != 0) {
                        count = count3;
                        URLSpanMono uRLSpanMono = r3;
                        count2 = 256;
                        URLSpanMono uRLSpanMono2 = new URLSpanMono(spannable, run2.start, run2.end, t, run2);
                        spannable.setSpan(uRLSpanMono, run2.start, run2.end, 33);
                    } else {
                        count = count3;
                        count2 = 256;
                        setRun = true;
                        spannable.setSpan(new TextStyleSpan(run2), run2.start, run2.end, 33);
                    }
                }
                spannable.setSpan(new URLSpanNoUnderline(url, run2), run2.start, run2.end, i);
            }
            if (!setRun && (run2.flags & count2) != 0) {
                spannable.setSpan(new TextStyleSpan(run2), run2.start, run2.end, 33);
            }
            a2++;
            str = null;
            charSequence = text;
            count3 = count;
        }
        return hasUrls4;
    }

    static /* synthetic */ int lambda$addEntitiesToText$0(TLRPC.MessageEntity o1, TLRPC.MessageEntity o2) {
        if (o1.offset > o2.offset) {
            return 1;
        }
        if (o1.offset < o2.offset) {
            return -1;
        }
        return 0;
    }

    public boolean needDrawShareButton() {
        int i;
        if (this.preview || this.scheduled || this.eventId != 0 || this.messageOwner.noforwards) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return true;
        }
        int i2 = this.type;
        if (i2 == 13 || i2 == 15) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) && !isOutOwner()) {
            return true;
        }
        if (isFromUser()) {
            if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty) || this.messageOwner.media == null || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && !(this.messageOwner.media.webpage instanceof TLRPC.TL_webPage))) {
                return false;
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
            if (user != null && user.bot) {
                return true;
            }
            if (!isOut()) {
                if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
                    return true;
                }
                TLRPC.Chat chat = null;
                if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
                    chat = getChat((AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.Chat>) null, this.messageOwner.peer_id.channel_id);
                }
                TLRPC.Chat chat2 = chat;
                if (!ChatObject.isChannel(chat2) || !chat2.megagroup || chat2.username == null || chat2.username.length() <= 0 || (this.messageOwner.media instanceof TLRPC.TL_messageMediaContact) || (this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) {
                    return false;
                }
                return true;
            }
        } else if (((this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) || this.messageOwner.post) && !isSupergroup() && this.messageOwner.peer_id.channel_id != 0 && ((this.messageOwner.via_bot_id == 0 && this.messageOwner.reply_to == null) || !((i = this.type) == 13 || i == 15))) {
            return true;
        }
        return false;
    }

    public boolean isYouTubeVideo() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && !TextUtils.isEmpty(this.messageOwner.media.webpage.embed_url) && "YouTube".equals(this.messageOwner.media.webpage.site_name);
    }

    public int getMaxMessageTextWidth() {
        int maxWidth;
        int maxWidth2 = 0;
        if (!AndroidUtilities.isTablet() || this.eventId == 0) {
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : getParentWidth();
        } else {
            this.generatedWithMinSize = AndroidUtilities.dp(530.0f);
        }
        this.generatedWithDensity = AndroidUtilities.density;
        if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && "telegram_background".equals(this.messageOwner.media.webpage.type)) {
            try {
                Uri uri = Uri.parse(this.messageOwner.media.webpage.url);
                String segment = uri.getLastPathSegment();
                if (uri.getQueryParameter("bg_color") != null) {
                    maxWidth2 = AndroidUtilities.dp(220.0f);
                } else if (segment.length() == 6 || (segment.length() == 13 && segment.charAt(6) == '-')) {
                    maxWidth2 = AndroidUtilities.dp(200.0f);
                }
            } catch (Exception e) {
            }
        } else if (isAndroidTheme()) {
            maxWidth2 = AndroidUtilities.dp(200.0f);
        }
        if (maxWidth2 != 0) {
            return maxWidth2;
        }
        int maxWidth3 = this.generatedWithMinSize - AndroidUtilities.dp((!needDrawAvatarInternal() || isOutOwner() || this.messageOwner.isThreadMessage) ? 80.0f : 132.0f);
        if (needDrawShareButton() == 0 || isOutOwner()) {
            maxWidth = maxWidth3;
        } else {
            maxWidth = maxWidth3 - AndroidUtilities.dp(10.0f);
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
            return maxWidth - AndroidUtilities.dp(10.0f);
        }
        return maxWidth;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006f, code lost:
        if (r1.messageOwner.send_state == 0) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x007d, code lost:
        if ((r1.messageOwner.media instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported) == false) goto L_0x0081;
     */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0307 A[SYNTHETIC, Splitter:B:130:0x0307] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x036b  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03dd  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x04c3  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x010e A[SYNTHETIC, Splitter:B:67:0x010e] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x013c A[SYNTHETIC, Splitter:B:72:0x013c] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0181  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLayout(org.telegram.tgnet.TLRPC.User r35) {
        /*
            r34 = this;
            r1 = r34
            int r0 = r1.type
            if (r0 != 0) goto L_0x05c7
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            if (r0 == 0) goto L_0x05c7
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0016
            goto L_0x05c7
        L_0x0016:
            r34.generateLinkDescription()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.textLayoutBlocks = r0
            r2 = 0
            r1.textWidth = r2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            r3 = 1
            if (r0 == 0) goto L_0x002d
            r0 = 0
            r4 = r0
            goto L_0x0037
        L_0x002d:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r3
            r4 = r0
        L_0x0037:
            if (r4 != 0) goto L_0x0081
            long r5 = r1.eventId
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x007f
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old
            if (r5 != 0) goto L_0x007f
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old2
            if (r5 != 0) goto L_0x007f
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old3
            if (r5 != 0) goto L_0x007f
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old4
            if (r5 != 0) goto L_0x007f
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old
            if (r5 != 0) goto L_0x007f
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old2
            if (r5 != 0) goto L_0x007f
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_secret
            if (r5 != 0) goto L_0x007f
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r0 != 0) goto L_0x007f
            boolean r0 = r34.isOut()
            if (r0 == 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x007f
        L_0x0071:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.id
            if (r0 < 0) goto L_0x007f
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x0081
        L_0x007f:
            r0 = 1
            goto L_0x0082
        L_0x0081:
            r0 = 0
        L_0x0082:
            r5 = r0
            if (r5 == 0) goto L_0x008e
            boolean r0 = r34.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            addLinks(r0, r6, r3, r3)
        L_0x008e:
            boolean r0 = r34.isYouTubeVideo()
            if (r0 != 0) goto L_0x00df
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x009f
            boolean r0 = r0.isYouTubeVideo()
            if (r0 == 0) goto L_0x009f
            goto L_0x00df
        L_0x009f:
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x00ef
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00bc
            boolean r6 = r34.isOutOwner()
            java.lang.CharSequence r7 = r1.messageText
            r8 = 0
            r9 = 3
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            int r10 = r0.getDuration()
            r11 = 0
            addUrlsByPattern(r6, r7, r8, r9, r10, r11)
            goto L_0x00ef
        L_0x00bc:
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            boolean r0 = r0.isMusic()
            if (r0 != 0) goto L_0x00cc
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00ef
        L_0x00cc:
            boolean r6 = r34.isOutOwner()
            java.lang.CharSequence r7 = r1.messageText
            r8 = 0
            r9 = 4
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            int r10 = r0.getDuration()
            r11 = 0
            addUrlsByPattern(r6, r7, r8, r9, r10, r11)
            goto L_0x00ef
        L_0x00df:
            boolean r12 = r34.isOutOwner()
            java.lang.CharSequence r13 = r1.messageText
            r14 = 0
            r15 = 3
            r16 = 2147483647(0x7fffffff, float:NaN)
            r17 = 0
            addUrlsByPattern(r12, r13, r14, r15, r16, r17)
        L_0x00ef:
            java.lang.CharSequence r0 = r1.messageText
            boolean r6 = r1.addEntitiesToText(r0, r5)
            int r15 = r34.getMaxMessageTextWidth()
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0105
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            r14 = r0
            goto L_0x0108
        L_0x0105:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            r14 = r0
        L_0x0108:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05b9 }
            r13 = 24
            if (r0 < r13) goto L_0x013c
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ Exception -> 0x0130 }
            int r7 = r0.length()     // Catch:{ Exception -> 0x0130 }
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r2, r7, r14, r15)     // Catch:{ Exception -> 0x0130 }
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r3)     // Catch:{ Exception -> 0x0130 }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x0130 }
            android.text.Layout$Alignment r7 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0130 }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r7)     // Catch:{ Exception -> 0x0130 }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x0130 }
            r19 = r14
            r2 = 24
            r14 = r0
            goto L_0x0157
        L_0x0130:
            r0 = move-exception
            r20 = r4
            r21 = r5
            r22 = r6
            r16 = r14
            r12 = r15
            goto L_0x05c3
        L_0x013c:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x05b9 }
            java.lang.CharSequence r8 = r1.messageText     // Catch:{ Exception -> 0x05b9 }
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x05b9 }
            r12 = 1065353216(0x3var_, float:1.0)
            r16 = 0
            r17 = 0
            r7 = r0
            r9 = r14
            r10 = r15
            r2 = 24
            r13 = r16
            r19 = r14
            r14 = r17
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x05ae }
            r14 = r0
        L_0x0157:
            int r0 = r14.getHeight()
            r1.textHeight = r0
            int r0 = r14.getLineCount()
            r1.linesCount = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r2) goto L_0x016b
            r0 = 1
            r13 = r0
            goto L_0x0178
        L_0x016b:
            int r0 = r1.linesCount
            float r0 = (float) r0
            r7 = 1092616192(0x41200000, float:10.0)
            float r0 = r0 / r7
            double r7 = (double) r0
            double r7 = java.lang.Math.ceil(r7)
            int r0 = (int) r7
            r13 = r0
        L_0x0178:
            r0 = 0
            r7 = 0
            r8 = 0
            r12 = r0
            r17 = r7
            r11 = r8
        L_0x017f:
            if (r11 >= r13) goto L_0x05ad
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r2) goto L_0x0189
            int r0 = r1.linesCount
            r10 = r0
            goto L_0x0193
        L_0x0189:
            r0 = 10
            int r7 = r1.linesCount
            int r7 = r7 - r12
            int r0 = java.lang.Math.min(r0, r7)
            r10 = r0
        L_0x0193:
            org.telegram.messenger.MessageObject$TextLayoutBlock r0 = new org.telegram.messenger.MessageObject$TextLayoutBlock
            r0.<init>()
            r9 = r0
            r8 = 0
            if (r13 != r3) goto L_0x020d
            r9.textLayout = r14
            r9.textYOffset = r8
            r7 = 0
            r9.charactersOffset = r7
            java.lang.CharSequence r0 = r14.getText()
            int r0 = r0.length()
            r9.charactersEnd = r0
            int r0 = r1.emojiOnlyCount
            if (r0 == 0) goto L_0x01fb
            switch(r0) {
                case 1: goto L_0x01e4;
                case 2: goto L_0x01cd;
                case 3: goto L_0x01b5;
                default: goto L_0x01b4;
            }
        L_0x01b4:
            goto L_0x01fb
        L_0x01b5:
            int r0 = r1.textHeight
            r7 = 1082549862(0x40866666, float:4.2)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r16
            r1.textHeight = r0
            float r0 = r9.textYOffset
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r0 = r0 - r7
            r9.textYOffset = r0
            goto L_0x01fb
        L_0x01cd:
            int r0 = r1.textHeight
            r7 = 1083179008(0x40900000, float:4.5)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r16
            r1.textHeight = r0
            float r0 = r9.textYOffset
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r0 = r0 - r7
            r9.textYOffset = r0
            goto L_0x01fb
        L_0x01e4:
            int r0 = r1.textHeight
            r7 = 1084856730(0x40a9999a, float:5.3)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r16
            r1.textHeight = r0
            float r0 = r9.textYOffset
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r0 = r0 - r7
            r9.textYOffset = r0
        L_0x01fb:
            int r0 = r1.textHeight
            r9.height = r0
            r7 = r9
            r9 = r11
            r8 = r12
            r2 = r14
            r33 = r15
            r3 = r19
            r19 = 0
            r11 = r10
            r10 = r13
            goto L_0x036d
        L_0x020d:
            int r7 = r14.getLineStart(r12)
            int r0 = r12 + r10
            int r0 = r0 - r3
            int r3 = r14.getLineEnd(r0)
            if (r3 >= r7) goto L_0x022e
            r20 = r4
            r21 = r5
            r22 = r6
            r9 = r11
            r30 = r12
            r10 = r13
            r32 = r14
            r12 = r15
            r16 = r19
            r3 = 1
            r19 = 0
            goto L_0x0598
        L_0x022e:
            r9.charactersOffset = r7
            r9.charactersEnd = r3
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ Exception -> 0x057b }
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)     // Catch:{ Exception -> 0x057b }
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)     // Catch:{ Exception -> 0x057b }
            if (r6 == 0) goto L_0x02b8
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x029a }
            if (r8 < r2) goto L_0x02b8
            int r8 = r0.length()     // Catch:{ Exception -> 0x029a }
            r20 = 1073741824(0x40000000, float:2.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x029a }
            int r2 = r15 + r20
            r18 = r3
            r20 = r14
            r3 = r19
            r14 = 0
            android.text.StaticLayout$Builder r2 = android.text.StaticLayout.Builder.obtain(r0, r14, r8, r3, r2)     // Catch:{ Exception -> 0x0281 }
            r8 = 1
            android.text.StaticLayout$Builder r2 = r2.setBreakStrategy(r8)     // Catch:{ Exception -> 0x0281 }
            android.text.StaticLayout$Builder r2 = r2.setHyphenationFrequency(r14)     // Catch:{ Exception -> 0x0281 }
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0281 }
            android.text.StaticLayout$Builder r2 = r2.setAlignment(r8)     // Catch:{ Exception -> 0x0281 }
            android.text.StaticLayout r2 = r2.build()     // Catch:{ Exception -> 0x0281 }
            r9.textLayout = r2     // Catch:{ Exception -> 0x0281 }
            r26 = r7
            r7 = r9
            r28 = r10
            r29 = r11
            r30 = r12
            r31 = r13
            r33 = r15
            r32 = r20
            r19 = 0
            goto L_0x02f8
        L_0x0281:
            r0 = move-exception
            r16 = r3
            r21 = r5
            r22 = r6
            r26 = r7
            r7 = r9
            r9 = r11
            r30 = r12
            r12 = r15
            r32 = r20
            r3 = 1
            r19 = 0
            r20 = r4
            r11 = r10
            r10 = r13
            goto L_0x0594
        L_0x029a:
            r0 = move-exception
            r18 = r3
            r20 = r14
            r14 = 0
            r21 = r5
            r22 = r6
            r26 = r7
            r7 = r9
            r9 = r11
            r30 = r12
            r12 = r15
            r16 = r19
            r32 = r20
            r3 = 1
            r19 = 0
            r20 = r4
            r11 = r10
            r10 = r13
            goto L_0x0594
        L_0x02b8:
            r18 = r3
            r20 = r14
            r3 = r19
            r14 = 0
            android.text.StaticLayout r2 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0563 }
            r19 = 0
            int r21 = r0.length()     // Catch:{ Exception -> 0x0563 }
            android.text.Layout$Alignment r22 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0563 }
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            r26 = r7
            r7 = r2
            r8 = r0
            r27 = r9
            r9 = r19
            r28 = r10
            r10 = r21
            r29 = r11
            r11 = r3
            r30 = r12
            r12 = r15
            r31 = r13
            r13 = r22
            r32 = r20
            r19 = 0
            r14 = r23
            r33 = r15
            r15 = r24
            r16 = r25
            r7.<init>(r8, r9, r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x054e }
            r7 = r27
            r7.textLayout = r2     // Catch:{ Exception -> 0x053b }
        L_0x02f8:
            r8 = r30
            r2 = r32
            int r9 = r2.getLineTop(r8)     // Catch:{ Exception -> 0x0523 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x0523 }
            r7.textYOffset = r9     // Catch:{ Exception -> 0x0523 }
            r9 = r29
            if (r9 == 0) goto L_0x0325
            float r10 = r7.textYOffset     // Catch:{ Exception -> 0x030f }
            float r10 = r10 - r17
            int r10 = (int) r10     // Catch:{ Exception -> 0x030f }
            r7.height = r10     // Catch:{ Exception -> 0x030f }
            goto L_0x0325
        L_0x030f:
            r0 = move-exception
            r32 = r2
            r16 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r30 = r8
            r11 = r28
            r10 = r31
            r12 = r33
            r3 = 1
            goto L_0x0594
        L_0x0325:
            int r10 = r7.height     // Catch:{ Exception -> 0x050d }
            android.text.StaticLayout r11 = r7.textLayout     // Catch:{ Exception -> 0x050d }
            android.text.StaticLayout r12 = r7.textLayout     // Catch:{ Exception -> 0x050d }
            int r12 = r12.getLineCount()     // Catch:{ Exception -> 0x050d }
            r13 = 1
            int r12 = r12 - r13
            int r11 = r11.getLineBottom(r12)     // Catch:{ Exception -> 0x050d }
            int r10 = java.lang.Math.max(r10, r11)     // Catch:{ Exception -> 0x050d }
            r7.height = r10     // Catch:{ Exception -> 0x050d }
            float r10 = r7.textYOffset     // Catch:{ Exception -> 0x050d }
            r17 = r10
            r10 = r31
            int r13 = r10 + -1
            if (r9 != r13) goto L_0x036b
            android.text.StaticLayout r0 = r7.textLayout
            int r0 = r0.getLineCount()
            r11 = r28
            int r11 = java.lang.Math.max(r11, r0)
            int r0 = r1.textHeight     // Catch:{ Exception -> 0x0366 }
            float r12 = r7.textYOffset     // Catch:{ Exception -> 0x0366 }
            android.text.StaticLayout r13 = r7.textLayout     // Catch:{ Exception -> 0x0366 }
            int r13 = r13.getHeight()     // Catch:{ Exception -> 0x0366 }
            float r13 = (float) r13     // Catch:{ Exception -> 0x0366 }
            float r12 = r12 + r13
            int r12 = (int) r12     // Catch:{ Exception -> 0x0366 }
            int r0 = java.lang.Math.max(r0, r12)     // Catch:{ Exception -> 0x0366 }
            r1.textHeight = r0     // Catch:{ Exception -> 0x0366 }
            goto L_0x036d
        L_0x0366:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x036d
        L_0x036b:
            r11 = r28
        L_0x036d:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r7.spoilers
            r0.clear()
            boolean r0 = r1.isSpoilersRevealed
            if (r0 != 0) goto L_0x037e
            android.text.StaticLayout r0 = r7.textLayout
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r12 = r7.spoilers
            r13 = 0
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r13, r0, r13, r12)
        L_0x037e:
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r1.textLayoutBlocks
            r0.add(r7)
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x039a }
            int r12 = r11 + -1
            float r0 = r0.getLineLeft(r12)     // Catch:{ Exception -> 0x039a }
            if (r9 != 0) goto L_0x0397
            r12 = 0
            int r13 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r13 < 0) goto L_0x0398
            r1.textXOffset = r0     // Catch:{ Exception -> 0x0395 }
            goto L_0x0398
        L_0x0395:
            r0 = move-exception
            goto L_0x039c
        L_0x0397:
            r12 = 0
        L_0x0398:
            r13 = r0
            goto L_0x03a4
        L_0x039a:
            r0 = move-exception
            r12 = 0
        L_0x039c:
            r13 = 0
            if (r9 != 0) goto L_0x03a1
            r1.textXOffset = r12
        L_0x03a1:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03a4:
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x03ae }
            int r14 = r11 + -1
            float r0 = r0.getLineWidth(r14)     // Catch:{ Exception -> 0x03ae }
            r14 = r0
            goto L_0x03b3
        L_0x03ae:
            r0 = move-exception
            r14 = 0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03b3:
            r15 = r13
            double r12 = (double) r14
            double r12 = java.lang.Math.ceil(r12)
            int r0 = (int) r12
            r12 = r33
            int r13 = r12 + 80
            if (r0 <= r13) goto L_0x03c1
            r0 = r12
        L_0x03c1:
            int r13 = r10 + -1
            if (r9 != r13) goto L_0x03c7
            r1.lastLineWidth = r0
        L_0x03c7:
            float r13 = (float) r0
            r32 = r2
            r2 = 0
            float r16 = java.lang.Math.max(r2, r15)
            float r13 = r13 + r16
            r16 = r3
            double r2 = (double) r13
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            r3 = r2
            r13 = 1
            if (r11 <= r13) goto L_0x04c3
            r13 = 0
            r18 = 0
            r20 = 0
            r21 = 0
            r22 = r6
            r6 = r21
            r21 = r5
            r5 = r20
            r20 = r4
            r4 = r18
            r18 = r13
            r13 = r2
            r2 = r0
        L_0x03f4:
            if (r6 >= r11) goto L_0x049a
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x03ff }
            float r0 = r0.getLineWidth(r6)     // Catch:{ Exception -> 0x03ff }
            r23 = r0
            goto L_0x0405
        L_0x03ff:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r23 = 0
        L_0x0405:
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x040c }
            float r0 = r0.getLineLeft(r6)     // Catch:{ Exception -> 0x040c }
            goto L_0x0414
        L_0x040c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r24 = 0
            r0 = r24
        L_0x0414:
            r24 = r0
            int r0 = r12 + 20
            float r0 = (float) r0
            int r0 = (r23 > r0 ? 1 : (r23 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0428
            float r0 = (float) r12
            r23 = 0
            r30 = r8
            r25 = r14
            r14 = r23
            r8 = r0
            goto L_0x0430
        L_0x0428:
            r30 = r8
            r25 = r14
            r8 = r23
            r14 = r24
        L_0x0430:
            r23 = 0
            int r0 = (r14 > r23 ? 1 : (r14 == r23 ? 0 : -1))
            if (r0 <= 0) goto L_0x044a
            float r0 = r1.textXOffset
            float r0 = java.lang.Math.min(r0, r14)
            r1.textXOffset = r0
            byte r0 = r7.directionFlags
            r23 = r11
            r11 = 1
            r0 = r0 | r11
            byte r0 = (byte) r0
            r7.directionFlags = r0
            r1.hasRtl = r11
            goto L_0x0453
        L_0x044a:
            r23 = r11
            byte r0 = r7.directionFlags
            r0 = r0 | 2
            byte r0 = (byte) r0
            r7.directionFlags = r0
        L_0x0453:
            if (r18 != 0) goto L_0x046b
            r11 = 0
            int r0 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x046b
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x0466 }
            int r0 = r0.getParagraphDirection(r6)     // Catch:{ Exception -> 0x0466 }
            r11 = 1
            if (r0 != r11) goto L_0x046b
            r18 = 1
            goto L_0x046b
        L_0x0466:
            r0 = move-exception
            r11 = 1
            r18 = r11
            goto L_0x046c
        L_0x046b:
        L_0x046c:
            float r4 = java.lang.Math.max(r4, r8)
            float r0 = r8 + r14
            float r5 = java.lang.Math.max(r5, r0)
            r0 = r4
            r11 = r5
            double r4 = (double) r8
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            int r2 = java.lang.Math.max(r2, r4)
            float r4 = r8 + r14
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            int r13 = java.lang.Math.max(r13, r4)
            int r6 = r6 + 1
            r4 = r0
            r5 = r11
            r11 = r23
            r14 = r25
            r8 = r30
            goto L_0x03f4
        L_0x049a:
            r30 = r8
            r23 = r11
            r25 = r14
            if (r18 == 0) goto L_0x04aa
            r4 = r5
            int r0 = r10 + -1
            if (r9 != r0) goto L_0x04b0
            r1.lastLineWidth = r3
            goto L_0x04b0
        L_0x04aa:
            int r0 = r10 + -1
            if (r9 != r0) goto L_0x04b0
            r1.lastLineWidth = r2
        L_0x04b0:
            int r0 = r1.textWidth
            r8 = r2
            r6 = r3
            double r2 = (double) r4
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r0 = java.lang.Math.max(r0, r2)
            r1.textWidth = r0
            r2 = r8
            r3 = 1
            goto L_0x0509
        L_0x04c3:
            r20 = r4
            r21 = r5
            r22 = r6
            r30 = r8
            r23 = r11
            r25 = r14
            r6 = r3
            r3 = 0
            int r4 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x04f3
            float r4 = r1.textXOffset
            float r4 = java.lang.Math.min(r4, r15)
            r1.textXOffset = r4
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x04e4
            float r3 = (float) r0
            float r3 = r3 + r15
            int r0 = (int) r3
        L_0x04e4:
            r3 = 1
            if (r10 == r3) goto L_0x04e9
            r4 = 1
            goto L_0x04ea
        L_0x04e9:
            r4 = 0
        L_0x04ea:
            r1.hasRtl = r4
            byte r4 = r7.directionFlags
            r4 = r4 | r3
            byte r4 = (byte) r4
            r7.directionFlags = r4
            goto L_0x04fb
        L_0x04f3:
            r3 = 1
            byte r4 = r7.directionFlags
            r4 = r4 | 2
            byte r4 = (byte) r4
            r7.directionFlags = r4
        L_0x04fb:
            int r4 = r1.textWidth
            int r5 = java.lang.Math.min(r12, r0)
            int r4 = java.lang.Math.max(r4, r5)
            r1.textWidth = r4
            r13 = r2
            r2 = r0
        L_0x0509:
            int r0 = r30 + r23
            goto L_0x059a
        L_0x050d:
            r0 = move-exception
            r32 = r2
            r16 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r30 = r8
            r11 = r28
            r10 = r31
            r12 = r33
            r3 = 1
            goto L_0x0594
        L_0x0523:
            r0 = move-exception
            r32 = r2
            r16 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r30 = r8
            r11 = r28
            r9 = r29
            r10 = r31
            r12 = r33
            r3 = 1
            goto L_0x0594
        L_0x053b:
            r0 = move-exception
            r16 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r11 = r28
            r9 = r29
            r10 = r31
            r12 = r33
            r3 = 1
            goto L_0x0594
        L_0x054e:
            r0 = move-exception
            r16 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r7 = r27
            r11 = r28
            r9 = r29
            r10 = r31
            r12 = r33
            r3 = 1
            goto L_0x0594
        L_0x0563:
            r0 = move-exception
            r16 = r3
            r21 = r5
            r22 = r6
            r26 = r7
            r7 = r9
            r9 = r11
            r30 = r12
            r12 = r15
            r32 = r20
            r3 = 1
            r19 = 0
            r20 = r4
            r11 = r10
            r10 = r13
            goto L_0x0594
        L_0x057b:
            r0 = move-exception
            r18 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r26 = r7
            r7 = r9
            r9 = r11
            r30 = r12
            r32 = r14
            r12 = r15
            r16 = r19
            r3 = 1
            r19 = 0
            r11 = r10
            r10 = r13
        L_0x0594:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0598:
            r0 = r30
        L_0x059a:
            int r11 = r9 + 1
            r13 = r10
            r15 = r12
            r19 = r16
            r4 = r20
            r5 = r21
            r6 = r22
            r14 = r32
            r2 = 24
            r12 = r0
            goto L_0x017f
        L_0x05ad:
            return
        L_0x05ae:
            r0 = move-exception
            r20 = r4
            r21 = r5
            r22 = r6
            r12 = r15
            r16 = r19
            goto L_0x05c3
        L_0x05b9:
            r0 = move-exception
            r20 = r4
            r21 = r5
            r22 = r6
            r16 = r14
            r12 = r15
        L_0x05c3:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            return
        L_0x05c7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        if (this.preview) {
            return true;
        }
        TLRPC.Chat chat = null;
        if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
            chat = getChat((AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.Chat>) null, this.messageOwner.peer_id.channel_id);
        }
        TLRPC.Chat chat2 = chat;
        if (!this.messageOwner.out || ((!(this.messageOwner.from_id instanceof TLRPC.TL_peerUser) && (!(this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) || (ChatObject.isChannel(chat2) && !chat2.megagroup))) || this.messageOwner.post)) {
            return false;
        }
        if (this.messageOwner.fwd_from == null) {
            return true;
        }
        long selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (getDialogId() == selfUserId) {
            if ((this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) && this.messageOwner.fwd_from.from_id.user_id == selfUserId && (this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.user_id == selfUserId)) {
                return true;
            }
            if (this.messageOwner.fwd_from.saved_from_peer != null && this.messageOwner.fwd_from.saved_from_peer.user_id == selfUserId && (this.messageOwner.fwd_from.from_id == null || this.messageOwner.fwd_from.from_id.user_id == selfUserId)) {
                return true;
            }
            return false;
        } else if (this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.user_id == selfUserId) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needDrawAvatar() {
        if (this.customAvatarDrawable != null) {
            return true;
        }
        if (isSponsored() && isFromChat()) {
            return true;
        }
        if (!isSponsored()) {
            if (isFromUser() || isFromGroup() || this.eventId != 0) {
                return true;
            }
            if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean needDrawAvatarInternal() {
        if (this.customAvatarDrawable != null) {
            return true;
        }
        if (!isSponsored()) {
            if ((isFromChat() && isFromUser()) || isFromGroup() || this.eventId != 0) {
                return true;
            }
            if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isFromChat() {
        if (getDialogId() == UserConfig.getInstance(this.currentAccount).clientUserId) {
            return true;
        }
        TLRPC.Chat chat = null;
        if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
            chat = getChat((AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.Chat>) null, this.messageOwner.peer_id.channel_id);
        }
        TLRPC.Chat chat2 = chat;
        if ((ChatObject.isChannel(chat2) && chat2.megagroup) || (this.messageOwner.peer_id != null && this.messageOwner.peer_id.chat_id != 0)) {
            return true;
        }
        if (this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0 || chat2 == null || !chat2.megagroup) {
            return false;
        }
        return true;
    }

    public static long getFromChatId(TLRPC.Message message) {
        return getPeerId(message.from_id);
    }

    public static long getPeerId(TLRPC.Peer peer) {
        if (peer == null) {
            return 0;
        }
        if (peer instanceof TLRPC.TL_peerChat) {
            return -peer.chat_id;
        }
        if (peer instanceof TLRPC.TL_peerChannel) {
            return -peer.channel_id;
        }
        return peer.user_id;
    }

    public long getFromChatId() {
        return getFromChatId(this.messageOwner);
    }

    public long getChatId() {
        if (this.messageOwner.peer_id instanceof TLRPC.TL_peerChat) {
            return this.messageOwner.peer_id.chat_id;
        }
        if (this.messageOwner.peer_id instanceof TLRPC.TL_peerChannel) {
            return this.messageOwner.peer_id.channel_id;
        }
        return 0;
    }

    public boolean isFromUser() {
        return (this.messageOwner.from_id instanceof TLRPC.TL_peerUser) && !this.messageOwner.post;
    }

    public boolean isFromGroup() {
        TLRPC.Chat chat = null;
        if (!(this.messageOwner.peer_id == null || this.messageOwner.peer_id.channel_id == 0)) {
            chat = getChat((AbstractMap<Long, TLRPC.Chat>) null, (LongSparseArray<TLRPC.Chat>) null, this.messageOwner.peer_id.channel_id);
        }
        TLRPC.Chat chat2 = chat;
        return (this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) && ChatObject.isChannel(chat2) && chat2.megagroup;
    }

    public boolean isForwardedChannelPost() {
        return (this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) && this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_post != 0 && (this.messageOwner.fwd_from.saved_from_peer instanceof TLRPC.TL_peerChannel) && this.messageOwner.from_id.channel_id == this.messageOwner.fwd_from.saved_from_peer.channel_id;
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
        int flags = 0;
        if (!message.unread) {
            flags = 0 | 1;
        }
        if (!message.media_unread) {
            return flags | 2;
        }
        return flags;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public int getRealId() {
        return this.messageOwner.realId != 0 ? this.messageOwner.realId : this.messageOwner.id;
    }

    public static long getMessageSize(TLRPC.Message message) {
        TLRPC.Document document;
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            document = message.media.webpage.document;
        } else if (message.media instanceof TLRPC.TL_messageMediaGame) {
            document = message.media.game.document;
        } else {
            document = message.media != null ? message.media.document : null;
        }
        if (document != null) {
            return document.size;
        }
        return 0;
    }

    public long getSize() {
        return getMessageSize(this.messageOwner);
    }

    public static void fixMessagePeer(ArrayList<TLRPC.Message> messages, long channelId) {
        if (messages != null && !messages.isEmpty() && channelId != 0) {
            for (int a = 0; a < messages.size(); a++) {
                TLRPC.Message message = messages.get(a);
                if (message instanceof TLRPC.TL_messageEmpty) {
                    message.peer_id = new TLRPC.TL_peerChannel();
                    message.peer_id.channel_id = channelId;
                }
            }
        }
    }

    public long getChannelId() {
        return getChannelId(this.messageOwner);
    }

    public static long getChannelId(TLRPC.Message message) {
        if (message.peer_id != null) {
            return message.peer_id.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60) {
                return true;
            }
            return false;
        } else if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.messageOwner);
    }

    public static boolean isSecretPhotoOrVideo(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message)) {
            return false;
        } else {
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
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
            if (((message.media instanceof TLRPC.TL_messageMediaPhoto) || (message.media instanceof TLRPC.TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean needDrawBluredPreview() {
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            int ttl = Math.max(message.ttl, this.messageOwner.media.ttl_seconds);
            if (ttl <= 0 || (((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && !isVideo() && !isGif()) || ttl > 60) && !isRoundVideo())) {
                return false;
            }
            return true;
        } else if (!(message instanceof TLRPC.TL_message) || message.media == null || this.messageOwner.media.ttl_seconds == 0 || (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && !(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSecretMedia() {
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            if ((((message.media instanceof TLRPC.TL_messageMediaPhoto) || isGif()) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60) || isVoice() || isRoundVideo() || isVideo()) {
                return true;
            }
            return false;
        } else if (!(message instanceof TLRPC.TL_message) || message.media == null || this.messageOwner.media.ttl_seconds == 0 || (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && !(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))) {
            return false;
        } else {
            return true;
        }
    }

    public static void setUnreadFlags(TLRPC.Message message, int flag) {
        boolean z = false;
        message.unread = (flag & 1) == 0;
        if ((flag & 2) == 0) {
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

    public boolean isSavedFromMegagroup() {
        if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
    }

    public static boolean isOut(TLRPC.Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public boolean canStreamVideo() {
        TLRPC.Document document = getDocument();
        if (document == null || (document instanceof TLRPC.TL_documentEncrypted)) {
            return false;
        }
        if (SharedConfig.streamAllVideo) {
            return true;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.supports_streaming;
            }
        }
        if (SharedConfig.streamMkv == 0 || !"video/x-matroska".equals(document.mime_type)) {
            return false;
        }
        return true;
    }

    public static long getDialogId(TLRPC.Message message) {
        if (message.dialog_id == 0 && message.peer_id != null) {
            if (message.peer_id.chat_id != 0) {
                message.dialog_id = -message.peer_id.chat_id;
            } else if (message.peer_id.channel_id != 0) {
                message.dialog_id = -message.peer_id.channel_id;
            } else if (message.from_id == null || isOut(message)) {
                message.dialog_id = message.peer_id.user_id;
            } else {
                message.dialog_id = message.from_id.user_id;
            }
        }
        return message.dialog_id;
    }

    public boolean isSending() {
        return this.messageOwner.send_state == 1 && this.messageOwner.id < 0;
    }

    public boolean isEditing() {
        return this.messageOwner.send_state == 3 && this.messageOwner.id > 0;
    }

    public boolean isEditingMedia() {
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            if (this.messageOwner.media.photo.id == 0) {
                return true;
            }
            return false;
        } else if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) || this.messageOwner.media.document.dc_id != 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSendError() {
        return (this.messageOwner.send_state == 2 && this.messageOwner.id < 0) || (this.scheduled && this.messageOwner.id > 0 && this.messageOwner.date < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + -60);
    }

    public boolean isSent() {
        return this.messageOwner.send_state == 0 || this.messageOwner.id > 0;
    }

    public int getSecretTimeLeft() {
        int secondsLeft = this.messageOwner.ttl;
        if (this.messageOwner.destroyTime != 0) {
            return Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
        }
        return secondsLeft;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int secondsLeft = getSecretTimeLeft();
        if (secondsLeft < 60) {
            return secondsLeft + "s";
        }
        return (secondsLeft / 60) + "m";
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public static boolean isWebM(TLRPC.Document document) {
        return document != null && "video/webm".equals(document.mime_type);
    }

    public static boolean isVideoSticker(TLRPC.Document document) {
        return document != null && isVideoStickerDocument(document);
    }

    public boolean isVideoSticker() {
        return getDocument() != null && isVideoStickerDocument(getDocument());
    }

    public static boolean isStickerDocument(TLRPC.Document document) {
        if (document != null) {
            int a = 0;
            while (a < document.attributes.size()) {
                if (!(document.attributes.get(a) instanceof TLRPC.TL_documentAttributeSticker)) {
                    a++;
                } else if ("image/webp".equals(document.mime_type) || "video/webm".equals(document.mime_type)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isVideoStickerDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            if (document.attributes.get(a) instanceof TLRPC.TL_documentAttributeSticker) {
                return "video/webm".equals(document.mime_type);
            }
        }
        return false;
    }

    public static boolean isStickerHasSet(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if ((attribute instanceof TLRPC.TL_documentAttributeSticker) && attribute.stickerset != null && !(attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document, boolean allowWithoutSet) {
        if (document == null) {
            return false;
        }
        if ((!"application/x-tgsticker".equals(document.mime_type) || document.thumbs.isEmpty()) && !"application/x-tgsdice".equals(document.mime_type)) {
            return false;
        }
        if (allowWithoutSet) {
            return true;
        }
        int N = document.attributes.size();
        for (int a = 0; a < N; a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                return attribute.stickerset instanceof TLRPC.TL_inputStickerSetShortName;
            }
        }
        return false;
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC.Document document) {
        return (isAnimatedStickerDocument(document, true) || isVideoStickerDocument(document)) && SharedConfig.getDevicePerformanceClass() != 0;
    }

    public static boolean isMaskDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if ((attribute instanceof TLRPC.TL_documentAttributeSticker) && attribute.mask) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.voice;
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(WebFile webDocument) {
        return webDocument != null && webDocument.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(WebFile webDocument) {
        return webDocument != null && !isGifDocument(webDocument) && webDocument.mime_type.startsWith("image/");
    }

    public static boolean isVideoWebDocument(WebFile webDocument) {
        return webDocument != null && webDocument.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return true ^ attribute.voice;
            }
        }
        if (TextUtils.isEmpty(document.mime_type)) {
            return false;
        }
        String mime = document.mime_type.toLowerCase();
        if (mime.equals("audio/flac") || mime.equals("audio/ogg") || mime.equals("audio/opus") || mime.equals("audio/x-opus+ogg")) {
            return true;
        }
        if (!mime.equals("application/octet-stream") || !FileLoader.getDocumentFileName(document).endsWith(".opus")) {
            return false;
        }
        return true;
    }

    public static TLRPC.VideoSize getDocumentVideoThumb(TLRPC.Document document) {
        if (document == null || document.video_thumbs.isEmpty()) {
            return null;
        }
        return document.video_thumbs.get(0);
    }

    public static boolean isVideoDocument(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        boolean isAnimated = false;
        boolean isVideo = false;
        int width = 0;
        int height = 0;
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                if (attribute.round_message) {
                    return false;
                }
                isVideo = true;
                width = attribute.w;
                height = attribute.h;
            } else if (attribute instanceof TLRPC.TL_documentAttributeAnimated) {
                isAnimated = true;
            }
        }
        if (isAnimated && (width > 1280 || height > 1280)) {
            isAnimated = false;
        }
        if (SharedConfig.streamMkv && !isVideo && "video/x-matroska".equals(document.mime_type)) {
            isVideo = true;
        }
        if (!isVideo || isAnimated) {
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
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return message.media.webpage.document;
        }
        if (message.media instanceof TLRPC.TL_messageMediaGame) {
            return message.media.game.document;
        }
        if (message.media != null) {
            return message.media.document;
        }
        return null;
    }

    public static TLRPC.Photo getPhoto(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return message.media.webpage.photo;
        }
        if (message.media != null) {
            return message.media.photo;
        }
        return null;
    }

    public static boolean isStickerMessage(TLRPC.Message message) {
        return message.media != null && isStickerDocument(message.media.document);
    }

    public static boolean isAnimatedStickerMessage(TLRPC.Message message) {
        boolean isSecretChat = DialogObject.isEncryptedDialog(message.dialog_id);
        if ((isSecretChat && message.stickerVerified != 1) || message.media == null) {
            return false;
        }
        if (isAnimatedStickerDocument(message.media.document, !isSecretChat || message.out)) {
            return true;
        }
        return false;
    }

    public static boolean isLocationMessage(TLRPC.Message message) {
        return (message.media instanceof TLRPC.TL_messageMediaGeo) || (message.media instanceof TLRPC.TL_messageMediaGeoLive) || (message.media instanceof TLRPC.TL_messageMediaVenue);
    }

    public static boolean isMaskMessage(TLRPC.Message message) {
        return message.media != null && isMaskDocument(message.media.document);
    }

    public static boolean isMusicMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isMusicDocument(message.media.webpage.document);
        }
        return message.media != null && isMusicDocument(message.media.document);
    }

    public static boolean isGifMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isGifDocument(message.media.webpage.document);
        }
        if (message.media != null) {
            if (isGifDocument(message.media.document, message.grouped_id != 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isRoundVideoDocument(message.media.webpage.document);
        }
        return message.media != null && isRoundVideoDocument(message.media.document);
    }

    public static boolean isPhoto(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return (message.media.webpage.photo instanceof TLRPC.TL_photo) && !(message.media.webpage.document instanceof TLRPC.TL_document);
        }
        return message.media instanceof TLRPC.TL_messageMediaPhoto;
    }

    public static boolean isVoiceMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isVoiceDocument(message.media.webpage.document);
        }
        return message.media != null && isVoiceDocument(message.media.document);
    }

    public static boolean isNewGifMessage(TLRPC.Message message) {
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isNewGifDocument(message.media.webpage.document);
        }
        return message.media != null && isNewGifDocument(message.media.document);
    }

    public static boolean isLiveLocationMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(TLRPC.Message message) {
        if (message.media != null && isVideoSticker(message.media.document)) {
            return false;
        }
        if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
            return isVideoDocument(message.media.webpage.document);
        }
        if (message.media == null || !isVideoDocument(message.media.document)) {
            return false;
        }
        return true;
    }

    public static boolean isGameMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC.Message message) {
        return message.media instanceof TLRPC.TL_messageMediaInvoice;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message message) {
        TLRPC.Document document = getDocument(message);
        if (document != null) {
            return getInputStickerSet(document);
        }
        return null;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        int a = 0;
        int N = document.attributes.size();
        while (a < N) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return null;
            } else {
                return attribute.stickerset;
            }
        }
        return null;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        if (document == null) {
            return -1;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return -1;
            } else {
                return attribute.stickerset.id;
            }
        }
        return -1;
    }

    public static String getStickerSetName(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty) {
                return null;
            } else {
                return attribute.stickerset.short_name;
            }
        }
        return null;
    }

    public String getStickerChar() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        Iterator<TLRPC.DocumentAttribute> it = document.attributes.iterator();
        while (it.hasNext()) {
            TLRPC.DocumentAttribute attribute = it.next();
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                return attribute.alt;
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        float maxWidth;
        int photoWidth;
        int i = this.type;
        if (i == 0) {
            int height = this.textHeight + ((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || !(this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) ? 0 : AndroidUtilities.dp(100.0f));
            if (isReply()) {
                return height + AndroidUtilities.dp(42.0f);
            }
            return height;
        } else if (i == 2) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (i == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (i == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (i == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (i == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (i == 10) {
                return AndroidUtilities.dp(30.0f);
            }
            if (i == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (i == 5) {
                return AndroidUtilities.roundMessageSize;
            }
            if (i == 13 || i == 15) {
                float maxHeight = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    maxWidth = ((float) AndroidUtilities.getMinTabletSide()) * 0.5f;
                } else {
                    maxWidth = ((float) AndroidUtilities.displaySize.x) * 0.5f;
                }
                int photoHeight = 0;
                int photoWidth2 = 0;
                TLRPC.Document document = getDocument();
                int a = 0;
                int N = document.attributes.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        photoWidth2 = attribute.w;
                        photoHeight = attribute.h;
                        break;
                    }
                    a++;
                }
                if (photoWidth2 == 0) {
                    photoHeight = (int) maxHeight;
                    photoWidth2 = photoHeight + AndroidUtilities.dp(100.0f);
                }
                if (((float) photoHeight) > maxHeight) {
                    photoWidth2 = (int) (((float) photoWidth2) * (maxHeight / ((float) photoHeight)));
                    photoHeight = (int) maxHeight;
                }
                if (((float) photoWidth2) > maxWidth) {
                    photoHeight = (int) (((float) photoHeight) * (maxWidth / ((float) photoWidth2)));
                }
                return AndroidUtilities.dp(14.0f) + photoHeight;
            }
            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            int photoHeight2 = AndroidUtilities.dp(100.0f) + photoWidth;
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight2 > AndroidUtilities.getPhotoSize()) {
                photoHeight2 = AndroidUtilities.getPhotoSize();
            }
            TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (currentPhotoObject != null) {
                int h = (int) (((float) currentPhotoObject.h) / (((float) currentPhotoObject.w) / ((float) photoWidth)));
                if (h == 0) {
                    h = AndroidUtilities.dp(100.0f);
                }
                if (h > photoHeight2) {
                    h = photoHeight2;
                } else if (h < AndroidUtilities.dp(120.0f)) {
                    h = AndroidUtilities.dp(120.0f);
                }
                if (needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                    } else {
                        h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                    }
                }
                photoHeight2 = h;
            }
            return AndroidUtilities.dp(14.0f) + photoHeight2;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.parentWidth;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getParentWidth() {
        /*
            r1 = this;
            boolean r0 = r1.preview
            if (r0 == 0) goto L_0x0009
            int r0 = r1.parentWidth
            if (r0 <= 0) goto L_0x0009
            goto L_0x000d
        L_0x0009:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getParentWidth():int");
    }

    public String getStickerEmoji() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.alt == null || attribute.alt.length() <= 0) {
                return null;
            } else {
                return attribute.alt;
            }
        }
        return null;
    }

    public boolean isVideoCall() {
        return (this.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) && this.messageOwner.action.video;
    }

    public boolean isAnimatedEmoji() {
        return this.emojiAnimatedSticker != null;
    }

    public boolean isDice() {
        return this.messageOwner.media instanceof TLRPC.TL_messageMediaDice;
    }

    public String getDiceEmoji() {
        if (!isDice()) {
            return null;
        }
        TLRPC.TL_messageMediaDice messageMediaDice = (TLRPC.TL_messageMediaDice) this.messageOwner.media;
        if (TextUtils.isEmpty(messageMediaDice.emoticon)) {
            return "🎲";
        }
        return messageMediaDice.emoticon.replace("️", "");
    }

    public int getDiceValue() {
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDice) {
            return ((TLRPC.TL_messageMediaDice) this.messageOwner.media).value;
        }
        return -1;
    }

    public boolean isSticker() {
        int i = this.type;
        if (i != 1000) {
            if (i == 13) {
                return true;
            }
            return false;
        } else if (isStickerDocument(getDocument()) || isVideoSticker(getDocument())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        boolean z = false;
        if (i == 1000) {
            boolean isSecretChat = DialogObject.isEncryptedDialog(getDialogId());
            if (isSecretChat && this.messageOwner.stickerVerified != 1) {
                return false;
            }
            TLRPC.Document document = getDocument();
            if (this.emojiAnimatedSticker != null || !isSecretChat || isOut()) {
                z = true;
            }
            return isAnimatedStickerDocument(document, z);
        } else if (i == 15) {
            return true;
        } else {
            return false;
        }
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
        return isMusicMessage(this.messageOwner) && !isVideo();
    }

    public boolean isDocument() {
        return getDocument() != null && !isVideo() && !isMusic() && !isVoice() && !isAnyKindOfSticker();
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

    public boolean isExpiredLiveLocation(int date) {
        return this.messageOwner.date + this.messageOwner.media.period <= date;
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

    public boolean shouldAnimateSending() {
        return isSending() && (this.type == 5 || isVoice() || ((isAnyKindOfSticker() && this.sendAnimationData != null) || !(this.messageText == null || this.sendAnimationData == null)));
    }

    public boolean hasAttachedStickers() {
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            if (this.messageOwner.media.photo == null || !this.messageOwner.media.photo.has_stickers) {
                return false;
            }
            return true;
        } else if (this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
            return isDocumentHasAttachedStickers(this.messageOwner.media.document);
        } else {
            return false;
        }
    }

    public static boolean isDocumentHasAttachedStickers(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            if (document.attributes.get(a) instanceof TLRPC.TL_documentAttributeHasStickers) {
                return true;
            }
        }
        return false;
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    public boolean isWebpageDocument() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage.document != null && !isGifDocument(this.messageOwner.media.webpage.document);
    }

    public boolean isWebpage() {
        return this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(getDocument());
    }

    public boolean isAndroidTheme() {
        if (this.messageOwner.media == null || this.messageOwner.media.webpage == null || this.messageOwner.media.webpage.attributes.isEmpty()) {
            return false;
        }
        int N2 = this.messageOwner.media.webpage.attributes.size();
        for (int b = 0; b < N2; b++) {
            TLRPC.TL_webPageAttributeTheme attribute = this.messageOwner.media.webpage.attributes.get(b);
            ArrayList<TLRPC.Document> documents = attribute.documents;
            int N = documents.size();
            for (int a = 0; a < N; a++) {
                if ("application/x-tgtheme-android".equals(documents.get(a).mime_type)) {
                    return true;
                }
            }
            if (attribute.settings != null) {
                return true;
            }
        }
        return false;
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean unknown) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            int a = 0;
            while (a < document.attributes.size()) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (!attribute.voice) {
                        String title = attribute.title;
                        if (title != null && title.length() != 0) {
                            return title;
                        }
                        String title2 = FileLoader.getDocumentFileName(document);
                        if (!TextUtils.isEmpty(title2) || !unknown) {
                            return title2;
                        }
                        return LocaleController.getString("AudioUnknownTitle", NUM);
                    } else if (!unknown) {
                        return null;
                    } else {
                        return LocaleController.formatDateAudio((long) this.messageOwner.date, true);
                    }
                } else if ((attribute instanceof TLRPC.TL_documentAttributeVideo) && attribute.round_message) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date, true);
                } else {
                    a++;
                }
            }
            String fileName = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(fileName)) {
                return fileName;
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
        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                return attribute.duration;
            }
            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                return attribute.duration;
            }
        }
        return this.audioPlayerDuration;
    }

    public String getArtworkUrl(boolean small) {
        TLRPC.Document document = getDocument();
        if (document == null || "audio/ogg".equals(document.mime_type)) {
            return null;
        }
        int i = 0;
        int N = document.attributes.size();
        while (i < N) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(i);
            if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                i++;
            } else if (attribute.voice) {
                return null;
            } else {
                String performer = attribute.performer;
                String title = attribute.title;
                if (!TextUtils.isEmpty(performer)) {
                    int a = 0;
                    while (true) {
                        String[] strArr = excludeWords;
                        if (a >= strArr.length) {
                            break;
                        }
                        performer = performer.replace(strArr[a], " ");
                        a++;
                    }
                }
                if (TextUtils.isEmpty(performer) != 0 && TextUtils.isEmpty(title)) {
                    return null;
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("athumb://itunes.apple.com/search?term=");
                    sb.append(URLEncoder.encode(performer + " - " + title, "UTF-8"));
                    sb.append("&entity=song&limit=4");
                    sb.append(small ? "&s=1" : "");
                    return sb.toString();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    public String getMusicAuthor(boolean unknown) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            boolean isVoice = false;
            for (int a = 0; a < document.attributes.size(); a++) {
                TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (attribute.voice) {
                        isVoice = true;
                    } else {
                        String performer = attribute.performer;
                        if (!TextUtils.isEmpty(performer) || !unknown) {
                            return performer;
                        }
                        return LocaleController.getString("AudioUnknownArtist", NUM);
                    }
                } else if ((attribute instanceof TLRPC.TL_documentAttributeVideo) && attribute.round_message) {
                    isVoice = true;
                }
                if (isVoice) {
                    if (!unknown) {
                        return null;
                    }
                    if (isOutOwner() || (this.messageOwner.fwd_from != null && (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) && this.messageOwner.fwd_from.from_id.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {
                        return LocaleController.getString("FromYou", NUM);
                    }
                    TLRPC.User user = null;
                    TLRPC.Chat chat = null;
                    if (this.messageOwner.fwd_from != null && (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel)) {
                        chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.channel_id));
                    } else if (this.messageOwner.fwd_from != null && (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat)) {
                        chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.chat_id));
                    } else if (this.messageOwner.fwd_from != null && (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser)) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.fwd_from.from_id.user_id));
                    } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_name != null) {
                        return this.messageOwner.fwd_from.from_name;
                    } else {
                        if (this.messageOwner.from_id instanceof TLRPC.TL_peerChat) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.from_id.chat_id));
                        } else if (this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.from_id.channel_id));
                        } else if (this.messageOwner.from_id != null || this.messageOwner.peer_id.channel_id == 0) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
                        } else {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.peer_id.channel_id));
                        }
                    }
                    if (user != null) {
                        return UserObject.getUserName(user);
                    }
                    if (chat != null) {
                        return chat.title;
                    }
                }
            }
        }
        return LocaleController.getString("AudioUnknownArtist", NUM);
    }

    public TLRPC.InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public boolean needDrawForwarded() {
        return (this.messageOwner.flags & 4) != 0 && this.messageOwner.fwd_from != null && !this.messageOwner.fwd_from.imported && (this.messageOwner.fwd_from.saved_from_peer == null || !(this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) || this.messageOwner.fwd_from.saved_from_peer.channel_id != this.messageOwner.fwd_from.from_id.channel_id) && UserConfig.getInstance(this.currentAccount).getClientUserId() != getDialogId();
    }

    public static boolean isForwardedMessage(TLRPC.Message message) {
        return ((message.flags & 4) == 0 || message.fwd_from == null) ? false : true;
    }

    public boolean isReply() {
        MessageObject messageObject = this.replyMessageObject;
        return ((messageObject != null && (messageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) || this.messageOwner.reply_to == null || (this.messageOwner.reply_to.reply_to_msg_id == 0 && this.messageOwner.reply_to.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0) ? false : true;
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public boolean isMediaEmptyWebpage() {
        return isMediaEmptyWebpage(this.messageOwner);
    }

    public static boolean isMediaEmpty(TLRPC.Message message) {
        return message == null || message.media == null || (message.media instanceof TLRPC.TL_messageMediaEmpty) || (message.media instanceof TLRPC.TL_messageMediaWebPage);
    }

    public static boolean isMediaEmptyWebpage(TLRPC.Message message) {
        return message == null || message.media == null || (message.media instanceof TLRPC.TL_messageMediaEmpty);
    }

    public boolean hasReplies() {
        return this.messageOwner.replies != null && this.messageOwner.replies.replies > 0;
    }

    public boolean canViewThread() {
        MessageObject messageObject;
        if (this.messageOwner.action != null) {
            return false;
        }
        if (hasReplies() || (((messageObject = this.replyMessageObject) != null && messageObject.messageOwner.replies != null) || getReplyTopMsgId() != 0)) {
            return true;
        }
        return false;
    }

    public boolean isComments() {
        return this.messageOwner.replies != null && this.messageOwner.replies.comments;
    }

    public boolean isLinkedToChat(long chatId) {
        return this.messageOwner.replies != null && (chatId == 0 || this.messageOwner.replies.channel_id == chatId);
    }

    public int getRepliesCount() {
        if (this.messageOwner.replies != null) {
            return this.messageOwner.replies.replies;
        }
        return 0;
    }

    public boolean canEditMessage(TLRPC.Chat chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, chat, this.scheduled);
    }

    public boolean canEditMessageScheduleTime(TLRPC.Chat chat) {
        return canEditMessageScheduleTime(this.currentAccount, this.messageOwner, chat);
    }

    public boolean canForwardMessage() {
        return !(this.messageOwner instanceof TLRPC.TL_message_secret) && !needDrawBluredPreview() && !isLiveLocation() && this.type != 16 && !isSponsored() && !this.messageOwner.noforwards;
    }

    public boolean canEditMedia() {
        if (isSecretMedia()) {
            return false;
        }
        if (this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            return true;
        }
        if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) || isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo()) {
            return false;
        }
        return true;
    }

    public boolean canEditMessageAnytime(TLRPC.Chat chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, chat);
    }

    public static boolean canEditMessageAnytime(int currentAccount2, TLRPC.Message message, TLRPC.Chat chat) {
        if (message == null || message.peer_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document, true))) || ((message.action != null && !(message.action instanceof TLRPC.TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0))) {
            return false;
        }
        if ((message.from_id instanceof TLRPC.TL_peerUser) && message.from_id.user_id == message.peer_id.user_id && message.from_id.user_id == UserConfig.getInstance(currentAccount2).getClientUserId() && !isLiveLocationMessage(message)) {
            return true;
        }
        if (chat == null && message.peer_id.channel_id != 0 && (chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(message.peer_id.channel_id))) == null) {
            return false;
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || (!chat.creator && (chat.admin_rights == null || !chat.admin_rights.edit_messages))) {
            return message.out && chat != null && chat.megagroup && (chat.creator || ((chat.admin_rights != null && chat.admin_rights.pin_messages) || (chat.default_banned_rights != null && !chat.default_banned_rights.pin_messages)));
        }
        return true;
    }

    public static boolean canEditMessageScheduleTime(int currentAccount2, TLRPC.Message message, TLRPC.Chat chat) {
        if (chat == null && message.peer_id.channel_id != 0 && (chat = MessagesController.getInstance(currentAccount2).getChat(Long.valueOf(message.peer_id.channel_id))) == null) {
            return false;
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || chat.creator) {
            return true;
        }
        if (chat.admin_rights == null || (!chat.admin_rights.edit_messages && !message.out)) {
            return false;
        }
        return true;
    }

    public static boolean canEditMessage(int currentAccount2, TLRPC.Message message, TLRPC.Chat chat, boolean scheduled2) {
        if (scheduled2 && message.date < ConnectionsManager.getInstance(currentAccount2).getCurrentTime() - 60) {
            return false;
        }
        if ((chat != null && ((chat.left || chat.kicked) && (!chat.megagroup || !chat.has_link))) || message == null || message.peer_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document, true) || isLocationMessage(message))) || ((message.action != null && !(message.action instanceof TLRPC.TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0))) {
            return false;
        }
        if ((message.from_id instanceof TLRPC.TL_peerUser) && message.from_id.user_id == message.peer_id.user_id && message.from_id.user_id == UserConfig.getInstance(currentAccount2).getClientUserId() && !isLiveLocationMessage(message) && !(message.media instanceof TLRPC.TL_messageMediaContact)) {
            return true;
        }
        if (chat == null && message.peer_id.channel_id != 0 && (chat = MessagesController.getInstance(currentAccount2).getChat(Long.valueOf(message.peer_id.channel_id))) == null) {
            return false;
        }
        if (message.media != null && !(message.media instanceof TLRPC.TL_messageMediaEmpty) && !(message.media instanceof TLRPC.TL_messageMediaPhoto) && !(message.media instanceof TLRPC.TL_messageMediaDocument) && !(message.media instanceof TLRPC.TL_messageMediaWebPage)) {
            return false;
        }
        if (ChatObject.isChannel(chat) && !chat.megagroup && (chat.creator || (chat.admin_rights != null && chat.admin_rights.edit_messages))) {
            return true;
        }
        if (message.out && chat != null && chat.megagroup && (chat.creator || ((chat.admin_rights != null && chat.admin_rights.pin_messages) || (chat.default_banned_rights != null && !chat.default_banned_rights.pin_messages)))) {
            return true;
        }
        if (!scheduled2 && Math.abs(message.date - ConnectionsManager.getInstance(currentAccount2).getCurrentTime()) > MessagesController.getInstance(currentAccount2).maxEditTime) {
            return false;
        }
        if (message.peer_id.channel_id == 0) {
            if (!message.out && (!(message.from_id instanceof TLRPC.TL_peerUser) || message.from_id.user_id != UserConfig.getInstance(currentAccount2).getClientUserId())) {
                return false;
            }
            if ((message.media instanceof TLRPC.TL_messageMediaPhoto) || (((message.media instanceof TLRPC.TL_messageMediaDocument) && !isStickerMessage(message) && !isAnimatedStickerMessage(message)) || (message.media instanceof TLRPC.TL_messageMediaEmpty) || (message.media instanceof TLRPC.TL_messageMediaWebPage) || message.media == null)) {
                return true;
            }
            return false;
        } else if (((chat == null || !chat.megagroup || !message.out) && (chat == null || chat.megagroup || ((!chat.creator && (chat.admin_rights == null || (!chat.admin_rights.edit_messages && (!message.out || !chat.admin_rights.post_messages)))) || !message.post))) || (!(message.media instanceof TLRPC.TL_messageMediaPhoto) && ((!(message.media instanceof TLRPC.TL_messageMediaDocument) || isStickerMessage(message) || isAnimatedStickerMessage(message)) && !(message.media instanceof TLRPC.TL_messageMediaEmpty) && !(message.media instanceof TLRPC.TL_messageMediaWebPage) && message.media != null))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canDeleteMessage(boolean inScheduleMode, TLRPC.Chat chat) {
        return this.eventId == 0 && this.sponsoredId == null && canDeleteMessage(this.currentAccount, inScheduleMode, this.messageOwner, chat);
    }

    public static boolean canDeleteMessage(int currentAccount2, boolean inScheduleMode, TLRPC.Message message, TLRPC.Chat chat) {
        if (message == null) {
            return false;
        }
        if (ChatObject.isChannelAndNotMegaGroup(chat) && (message.action instanceof TLRPC.TL_messageActionChatJoinedByRequest)) {
            return false;
        }
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.peer_id.channel_id != 0) {
            chat = MessagesController.getInstance(currentAccount2).getChat(Long.valueOf(message.peer_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            if (inScheduleMode && !chat.megagroup) {
                if (!chat.creator) {
                    if (chat.admin_rights == null) {
                        return false;
                    }
                    if (chat.admin_rights.delete_messages || message.out) {
                        return true;
                    }
                    return false;
                }
                return true;
            } else if (!message.out || !(message instanceof TLRPC.TL_messageService)) {
                if (!inScheduleMode) {
                    if (message.id == 1) {
                        return false;
                    }
                    if (chat.creator || ((chat.admin_rights != null && (chat.admin_rights.delete_messages || (message.out && (chat.megagroup || chat.admin_rights.post_messages)))) || (chat.megagroup && message.out))) {
                        return true;
                    }
                    return false;
                }
                return true;
            } else if (message.id == 1 || !ChatObject.canUserDoAdminAction(chat, 13)) {
                return false;
            } else {
                return true;
            }
        } else if (inScheduleMode || isOut(message) || !ChatObject.isChannel(chat)) {
            return true;
        } else {
            return false;
        }
    }

    public String getForwardedName() {
        if (this.messageOwner.fwd_from == null) {
            return null;
        }
        if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.channel_id));
            if (chat != null) {
                return chat.title;
            }
            return null;
        } else if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.chat_id));
            if (chat2 != null) {
                return chat2.title;
            }
            return null;
        } else if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.fwd_from.from_id.user_id));
            if (user != null) {
                return UserObject.getUserName(user);
            }
            return null;
        } else if (this.messageOwner.fwd_from.from_name != null) {
            return this.messageOwner.fwd_from.from_name;
        } else {
            return null;
        }
    }

    public int getReplyMsgId() {
        if (this.messageOwner.reply_to != null) {
            return this.messageOwner.reply_to.reply_to_msg_id;
        }
        return 0;
    }

    public int getReplyTopMsgId() {
        if (this.messageOwner.reply_to != null) {
            return this.messageOwner.reply_to.reply_to_top_id;
        }
        return 0;
    }

    public static long getReplyToDialogId(TLRPC.Message message) {
        if (message.reply_to == null) {
            return 0;
        }
        if (message.reply_to.reply_to_peer_id != null) {
            return getPeerId(message.reply_to.reply_to_peer_id);
        }
        return getDialogId(message);
    }

    public int getReplyAnyMsgId() {
        if (this.messageOwner.reply_to == null) {
            return 0;
        }
        if (this.messageOwner.reply_to.reply_to_top_id != 0) {
            return this.messageOwner.reply_to.reply_to_top_id;
        }
        return this.messageOwner.reply_to.reply_to_msg_id;
    }

    public boolean isPrivateForward() {
        return this.messageOwner.fwd_from != null && !TextUtils.isEmpty(this.messageOwner.fwd_from.from_name);
    }

    public boolean isImportedForward() {
        return this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.imported;
    }

    public long getSenderId() {
        if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null) {
            if (this.messageOwner.from_id instanceof TLRPC.TL_peerUser) {
                return this.messageOwner.from_id.user_id;
            }
            if (this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) {
                return -this.messageOwner.from_id.channel_id;
            }
            if (this.messageOwner.from_id instanceof TLRPC.TL_peerChat) {
                return -this.messageOwner.from_id.chat_id;
            }
            if (this.messageOwner.post) {
                return this.messageOwner.peer_id.channel_id;
            }
        } else if (this.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
            if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                return this.messageOwner.fwd_from.from_id.user_id;
            }
            return this.messageOwner.fwd_from.saved_from_peer.user_id;
        } else if (this.messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
            if (isSavedFromMegagroup() && (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser)) {
                return this.messageOwner.fwd_from.from_id.user_id;
            }
            if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                return -this.messageOwner.fwd_from.from_id.channel_id;
            }
            if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
                return -this.messageOwner.fwd_from.from_id.chat_id;
            }
            return -this.messageOwner.fwd_from.saved_from_peer.channel_id;
        } else if (this.messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
            if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerUser) {
                return this.messageOwner.fwd_from.from_id.user_id;
            }
            if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChannel) {
                return -this.messageOwner.fwd_from.from_id.channel_id;
            }
            if (this.messageOwner.fwd_from.from_id instanceof TLRPC.TL_peerChat) {
                return -this.messageOwner.fwd_from.from_id.chat_id;
            }
            return -this.messageOwner.fwd_from.saved_from_peer.chat_id;
        }
        return 0;
    }

    public boolean isWallpaper() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && "telegram_background".equals(this.messageOwner.media.webpage.type);
    }

    public boolean isTheme() {
        return (this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && this.messageOwner.media.webpage != null && "telegram_theme".equals(this.messageOwner.media.webpage.type);
    }

    public int getMediaExistanceFlags() {
        int flags = 0;
        if (this.attachPathExists) {
            flags = 0 | 1;
        }
        if (this.mediaExists) {
            return flags | 2;
        }
        return flags;
    }

    public void applyMediaExistanceFlags(int flags) {
        if (flags == -1) {
            checkMediaExistance();
            return;
        }
        boolean z = false;
        this.attachPathExists = (flags & 1) != 0;
        if ((flags & 2) != 0) {
            z = true;
        }
        this.mediaExists = z;
    }

    public void checkMediaExistance() {
        checkMediaExistance(true);
    }

    public void checkMediaExistance(boolean useFileDatabaseQueue) {
        TLRPC.Photo photo;
        int i;
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type == 1 && FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
            File file = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.messageOwner, useFileDatabaseQueue);
            if (needDrawBluredPreview()) {
                this.mediaExists = new File(file.getAbsolutePath() + ".enc").exists();
            }
            if (!this.mediaExists) {
                this.mediaExists = file.exists();
            }
        }
        if ((!this.mediaExists && this.type == 8) || (i = this.type) == 3 || i == 9 || i == 2 || i == 14 || i == 5) {
            if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                File file2 = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.messageOwner, useFileDatabaseQueue);
                if (this.type == 3 && needDrawBluredPreview()) {
                    this.mediaExists = new File(file2.getAbsolutePath() + ".enc").exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = file2.exists();
                }
            }
        }
        if (!this.mediaExists) {
            TLRPC.Document document = getDocument();
            if (document == null) {
                int i2 = this.type;
                if (i2 == 0) {
                    TLRPC.PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (currentPhotoObject != null) {
                        this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(currentPhotoObject, (String) null, true, useFileDatabaseQueue).exists();
                    }
                } else if (i2 == 11 && (photo = this.messageOwner.action.photo) != null && !photo.video_sizes.isEmpty()) {
                    this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(photo.video_sizes.get(0), (String) null, true, useFileDatabaseQueue).exists();
                }
            } else if (isWallpaper()) {
                this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, (String) null, true, useFileDatabaseQueue).exists();
            } else {
                this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, (String) null, false, useFileDatabaseQueue).exists();
            }
        }
    }

    public void setQuery(String query) {
        String query2;
        if (TextUtils.isEmpty(query)) {
            this.highlightedWords = null;
            this.messageTrimmedToHighlight = null;
            return;
        }
        ArrayList<String> foundWords = new ArrayList<>();
        String query3 = query.trim().toLowerCase();
        String[] queryWord = query3.split("\\P{L}+");
        ArrayList<String> searchForWords = new ArrayList<>();
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            String message = this.messageOwner.message.trim().toLowerCase();
            if (!message.contains(query3) || foundWords.contains(query3)) {
                searchForWords.addAll(Arrays.asList(message.split("\\P{L}+")));
            } else {
                foundWords.add(query3);
                handleFoundWords(foundWords, queryWord);
                return;
            }
        }
        if (getDocument() != null) {
            String fileName = FileLoader.getDocumentFileName(getDocument()).toLowerCase();
            if (fileName.contains(query3) && !foundWords.contains(query3)) {
                foundWords.add(query3);
            }
            searchForWords.addAll(Arrays.asList(fileName.split("\\P{L}+")));
        }
        if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) {
            TLRPC.WebPage webPage = this.messageOwner.media.webpage;
            String title = webPage.title;
            if (title == null) {
                title = webPage.site_name;
            }
            if (title != null) {
                String title2 = title.toLowerCase();
                if (title2.contains(query3) && !foundWords.contains(query3)) {
                    foundWords.add(query3);
                }
                searchForWords.addAll(Arrays.asList(title2.split("\\P{L}+")));
            }
        }
        String musicAuthor = getMusicAuthor();
        if (musicAuthor != null) {
            String musicAuthor2 = musicAuthor.toLowerCase();
            if (musicAuthor2.contains(query3) && !foundWords.contains(query3)) {
                foundWords.add(query3);
            }
            searchForWords.addAll(Arrays.asList(musicAuthor2.split("\\P{L}+")));
        }
        int k = 0;
        while (k < queryWord.length) {
            String currentQuery = queryWord[k];
            if (currentQuery.length() >= 2) {
                int i = 0;
                while (i < searchForWords.size()) {
                    if (foundWords.contains(searchForWords.get(i))) {
                        query2 = query3;
                    } else {
                        String word = searchForWords.get(i);
                        int startIndex = word.indexOf(currentQuery.charAt(0));
                        if (startIndex < 0) {
                            query2 = query3;
                        } else {
                            int l = Math.max(currentQuery.length(), word.length());
                            if (startIndex != 0) {
                                word = word.substring(startIndex);
                            }
                            int min = Math.min(currentQuery.length(), word.length());
                            int count = 0;
                            int j = 0;
                            while (true) {
                                if (j >= min) {
                                    query2 = query3;
                                    break;
                                }
                                query2 = query3;
                                if (word.charAt(j) != currentQuery.charAt(j)) {
                                    break;
                                }
                                count++;
                                j++;
                                query3 = query2;
                            }
                            if (((double) (((float) count) / ((float) l))) >= 0.5d) {
                                foundWords.add(searchForWords.get(i));
                            }
                        }
                    }
                    i++;
                    query3 = query2;
                }
            }
            k++;
            query3 = query3;
        }
        handleFoundWords(foundWords, queryWord);
    }

    private void handleFoundWords(ArrayList<String> foundWords, String[] queryWord) {
        if (!foundWords.isEmpty()) {
            boolean foundExactly = false;
            for (int i = 0; i < foundWords.size(); i++) {
                int j = 0;
                while (true) {
                    if (j >= queryWord.length) {
                        break;
                    } else if (foundWords.get(i).contains(queryWord[j])) {
                        foundExactly = true;
                        break;
                    } else {
                        j++;
                    }
                }
                if (foundExactly) {
                    break;
                }
            }
            if (foundExactly) {
                int i2 = 0;
                while (i2 < foundWords.size()) {
                    boolean findMatch = false;
                    int j2 = 0;
                    while (true) {
                        if (j2 >= queryWord.length) {
                            break;
                        } else if (foundWords.get(i2).contains(queryWord[j2])) {
                            findMatch = true;
                            break;
                        } else {
                            j2++;
                        }
                    }
                    if (!findMatch) {
                        foundWords.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                if (foundWords.size() > 0) {
                    Collections.sort(foundWords, MessageObject$$ExternalSyntheticLambda0.INSTANCE);
                    foundWords.clear();
                    foundWords.add(foundWords.get(0));
                }
            }
            this.highlightedWords = foundWords;
            if (this.messageOwner.message != null) {
                String str = this.messageOwner.message.replace(10, ' ').replaceAll(" +", " ").trim();
                int lastIndex = str.length();
                int startHighlightedIndex = str.toLowerCase().indexOf(foundWords.get(0));
                if (startHighlightedIndex < 0) {
                    startHighlightedIndex = 0;
                }
                if (lastIndex > 200) {
                    int newStart = Math.max(0, startHighlightedIndex - (200 / 2));
                    str = str.substring(newStart, Math.min(lastIndex, (startHighlightedIndex - newStart) + startHighlightedIndex + (200 / 2)));
                }
                this.messageTrimmedToHighlight = str;
            }
        }
    }

    static /* synthetic */ int lambda$handleFoundWords$1(String s, String s1) {
        return s1.length() - s.length();
    }

    public void createMediaThumbs() {
        if (isVideo()) {
            TLRPC.Document document = getDocument();
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
            this.mediaThumb = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320), document);
            this.mediaSmallThumb = ImageLocation.getForDocument(thumb, document);
        } else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) && this.messageOwner.media.photo != null && !this.photoThumbs.isEmpty()) {
            TLRPC.PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 50);
            this.mediaThumb = ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 320, false, currentPhotoObjectThumb, false), this.photoThumbsObject);
            this.mediaSmallThumb = ImageLocation.getForObject(currentPhotoObjectThumb, this.photoThumbsObject);
        }
    }

    public boolean hasHighlightedWords() {
        ArrayList<String> arrayList = this.highlightedWords;
        return arrayList != null && !arrayList.isEmpty();
    }

    public boolean equals(MessageObject obj) {
        return getId() == obj.getId() && getDialogId() == obj.getDialogId();
    }

    public boolean isReactionsAvailable() {
        return !isEditing() && !isSponsored() && isSent() && this.messageOwner.action == null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.tgnet.TLRPC$TL_reactionCount} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v58, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_reactionCount} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean selectReaction(java.lang.String r11, boolean r12, boolean r13) {
        /*
            r10 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r0 = r0.reactions
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r3 = new org.telegram.tgnet.TLRPC$TL_messageReactions
            r3.<init>()
            r0.reactions = r3
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r0 = r0.reactions
            boolean r3 = r10.isFromGroup()
            if (r3 != 0) goto L_0x0024
            boolean r3 = r10.isFromUser()
            if (r3 == 0) goto L_0x0022
            goto L_0x0024
        L_0x0022:
            r3 = 0
            goto L_0x0025
        L_0x0024:
            r3 = 1
        L_0x0025:
            r0.can_see_list = r3
        L_0x0027:
            r0 = 0
            r3 = 0
            r4 = 0
        L_0x002a:
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.results
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0077
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.results
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_reactionCount r5 = (org.telegram.tgnet.TLRPC.TL_reactionCount) r5
            boolean r5 = r5.chosen
            if (r5 == 0) goto L_0x0053
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.results
            java.lang.Object r5 = r5.get(r4)
            r0 = r5
            org.telegram.tgnet.TLRPC$TL_reactionCount r0 = (org.telegram.tgnet.TLRPC.TL_reactionCount) r0
        L_0x0053:
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.results
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_reactionCount r5 = (org.telegram.tgnet.TLRPC.TL_reactionCount) r5
            java.lang.String r5 = r5.reaction
            boolean r5 = r5.equals(r11)
            if (r5 == 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.results
            java.lang.Object r5 = r5.get(r4)
            r3 = r5
            org.telegram.tgnet.TLRPC$TL_reactionCount r3 = (org.telegram.tgnet.TLRPC.TL_reactionCount) r3
        L_0x0074:
            int r4 = r4 + 1
            goto L_0x002a
        L_0x0077:
            if (r0 == 0) goto L_0x007e
            if (r0 != r3) goto L_0x007e
            if (r12 == 0) goto L_0x007e
            return r2
        L_0x007e:
            if (r0 == 0) goto L_0x00dd
            if (r0 == r3) goto L_0x0084
            if (r13 == 0) goto L_0x00dd
        L_0x0084:
            r0.chosen = r1
            int r4 = r0.count
            int r4 = r4 - r2
            r0.count = r4
            int r4 = r0.count
            if (r4 > 0) goto L_0x0098
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r4 = r4.reactions
            java.util.ArrayList r4 = r4.results
            r4.remove(r0)
        L_0x0098:
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r4 = r4.reactions
            boolean r4 = r4.can_see_list
            if (r4 == 0) goto L_0x00da
            r4 = 0
        L_0x00a1:
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x00da
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r5 = (org.telegram.tgnet.TLRPC.TL_messagePeerReaction) r5
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            long r5 = getPeerId(r5)
            int r7 = r10.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            long r7 = r7.getClientUserId()
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x00d8
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            r5.remove(r4)
            int r4 = r4 + -1
        L_0x00d8:
            int r4 = r4 + r2
            goto L_0x00a1
        L_0x00da:
            r10.reactionsChanged = r2
            return r1
        L_0x00dd:
            if (r0 == 0) goto L_0x0135
            r0.chosen = r1
            int r4 = r0.count
            int r4 = r4 - r2
            r0.count = r4
            int r4 = r0.count
            if (r4 > 0) goto L_0x00f3
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r4 = r4.reactions
            java.util.ArrayList r4 = r4.results
            r4.remove(r0)
        L_0x00f3:
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r4 = r4.reactions
            boolean r4 = r4.can_see_list
            if (r4 == 0) goto L_0x0135
            r4 = 0
        L_0x00fc:
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0135
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r5 = (org.telegram.tgnet.TLRPC.TL_messagePeerReaction) r5
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            long r5 = getPeerId(r5)
            int r7 = r10.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            long r7 = r7.getClientUserId()
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0133
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            r5.remove(r4)
            int r4 = r4 + -1
        L_0x0133:
            int r4 = r4 + r2
            goto L_0x00fc
        L_0x0135:
            if (r3 != 0) goto L_0x0148
            org.telegram.tgnet.TLRPC$TL_reactionCount r4 = new org.telegram.tgnet.TLRPC$TL_reactionCount
            r4.<init>()
            r3 = r4
            r3.reaction = r11
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r4 = r4.reactions
            java.util.ArrayList r4 = r4.results
            r4.add(r3)
        L_0x0148:
            r3.chosen = r2
            int r4 = r3.count
            int r4 = r4 + r2
            r3.count = r4
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r4 = r4.reactions
            boolean r4 = r4.can_see_list
            if (r4 == 0) goto L_0x017c
            org.telegram.tgnet.TLRPC$TL_messagePeerReaction r4 = new org.telegram.tgnet.TLRPC$TL_messagePeerReaction
            r4.<init>()
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReactions r5 = r5.reactions
            java.util.ArrayList r5 = r5.recent_reactions
            r5.add(r1, r4)
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r1.<init>()
            r4.peer_id = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r4.peer_id
            int r5 = r10.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            long r5 = r5.getClientUserId()
            r1.user_id = r5
            r4.reaction = r11
        L_0x017c:
            r10.reactionsChanged = r2
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.selectReaction(java.lang.String, boolean, boolean):boolean");
    }

    public boolean probablyRingtone() {
        if (getDocument() == null || !RingtoneDataStore.ringtoneSupportedMimeType.contains(getDocument().mime_type) || getDocument().size >= ((long) (MessagesController.getInstance(this.currentAccount).ringtoneSizeMax * 2))) {
            return false;
        }
        for (int a = 0; a < getDocument().attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = getDocument().attributes.get(a);
            if ((attribute instanceof TLRPC.TL_documentAttributeAudio) && attribute.duration < 60) {
                return true;
            }
        }
        return false;
    }
}
