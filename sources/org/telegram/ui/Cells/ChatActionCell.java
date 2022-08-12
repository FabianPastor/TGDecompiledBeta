package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_premiumGiftOption;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.LaunchActivity;

public class ChatActionCell extends BaseCell implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private static Map<Integer, String> monthsToEmoticon;
    private int TAG;
    private SpannableStringBuilder accessibilityText;
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private AvatarDrawable avatarDrawable;
    private int backgroundHeight;
    private Path backgroundPath;
    private boolean canDrawInParent;
    private int currentAccount;
    private MessageObject currentMessageObject;
    private ImageLocation currentVideoLocation;
    private int customDate;
    private CharSequence customText;
    /* access modifiers changed from: private */
    public ChatActionCellDelegate delegate;
    private boolean forceWasUnread;
    private boolean giftButtonPressed;
    private RectF giftButtonRect;
    private TLRPC$VideoSize giftEffectAnimation;
    private StaticLayout giftPremiumButtonLayout;
    private float giftPremiumButtonWidth;
    private StaticLayout giftPremiumSubtitleLayout;
    private StaticLayout giftPremiumTitleLayout;
    private int giftRectSize;
    private TLRPC$Document giftSticker;
    private ImageReceiver.ImageReceiverDelegate giftStickerDelegate;
    private TextPaint giftSubtitlePaint;
    private TextPaint giftTitlePaint;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private boolean invalidateColors;
    private boolean invalidatePath;
    private float lastTouchX;
    private float lastTouchY;
    private ArrayList<Integer> lineHeights;
    private ArrayList<Integer> lineWidths;
    private String overrideBackground;
    private Paint overrideBackgroundPaint;
    private String overrideText;
    private TextPaint overrideTextPaint;
    private URLSpan pressedLink;
    private int previousWidth;
    private RectF rect;
    private View rippleView;
    public List<SpoilerEffect> spoilers;
    private Stack<SpoilerEffect> spoilersPool;
    private StarParticlesView.Drawable starParticlesDrawable;
    private Path starsPath;
    private int starsSize;
    private int stickerSize;
    private int textHeight;
    private StaticLayout textLayout;
    TextPaint textPaint;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;
    private ThemeDelegate themeDelegate;
    private float viewTop;
    private boolean visiblePartSet;
    private boolean wasLayout;

    public interface ChatActionCellDelegate {

        /* renamed from: org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didClickImage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell) {
            }

            public static boolean $default$didLongPress(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, float f, float f2) {
                return false;
            }

            public static void $default$didOpenPremiumGift(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption, boolean z) {
            }

            public static void $default$didPressReplyMessage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, int i) {
            }

            public static void $default$needOpenInviteLink(ChatActionCellDelegate chatActionCellDelegate, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate chatActionCellDelegate, long j) {
            }

            public static void $default$needShowEffectOverlay(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, TLRPC$Document tLRPC$Document, TLRPC$VideoSize tLRPC$VideoSize) {
            }
        }

        void didClickImage(ChatActionCell chatActionCell);

        boolean didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didOpenPremiumGift(ChatActionCell chatActionCell, TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption, boolean z);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void needOpenInviteLink(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported);

        void needOpenUserProfile(long j);

        void needShowEffectOverlay(ChatActionCell chatActionCell, TLRPC$Document tLRPC$Document, TLRPC$VideoSize tLRPC$VideoSize);
    }

    public interface ThemeDelegate extends Theme.ResourcesProvider {
    }

    public void onFailedDownload(String str, boolean z) {
    }

    public void onProgressDownload(String str, long j, long j2) {
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    static {
        HashMap hashMap = new HashMap();
        monthsToEmoticon = hashMap;
        hashMap.put(1, "1⃣");
        monthsToEmoticon.put(3, "2⃣");
        monthsToEmoticon.put(6, "3⃣");
        monthsToEmoticon.put(12, "4⃣");
        monthsToEmoticon.put(24, "5⃣");
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        if (i == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
        } else if (i == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
        } else if (i == NotificationCenter.didUpdatePremiumGiftStickers) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 != null) {
                setMessageObject(messageObject2, true);
            }
        } else if (i == NotificationCenter.diceStickersDidLoad && ObjectsCompat$$ExternalSyntheticBackport0.m(objArr[0], UserConfig.getInstance(this.currentAccount).premiumGiftsStickerPack) && (messageObject = this.currentMessageObject) != null) {
            setMessageObject(messageObject, true);
        }
    }

    public void setSpoilersSuppressed(boolean z) {
        for (SpoilerEffect suppressUpdates : this.spoilers) {
            suppressUpdates.setSuppressUpdates(z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
        RLottieDrawable lottieAnimation;
        ChatActionCellDelegate chatActionCellDelegate;
        if (z && (lottieAnimation = this.imageReceiver.getLottieAnimation()) != null) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject == null || messageObject.playedGiftAnimation) {
                lottieAnimation.stop();
                lottieAnimation.setCurrentFrame(lottieAnimation.getFramesCount() - 1, false);
                return;
            }
            messageObject.playedGiftAnimation = true;
            lottieAnimation.setCurrentFrame(0, false);
            AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda2(lottieAnimation));
            if (messageObject.wasUnread || this.forceWasUnread) {
                messageObject.wasUnread = false;
                this.forceWasUnread = false;
                try {
                    performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                if (getContext() instanceof LaunchActivity) {
                    ((LaunchActivity) getContext()).getFireworksOverlay().start();
                }
                TLRPC$VideoSize tLRPC$VideoSize = this.giftEffectAnimation;
                if (tLRPC$VideoSize != null && (chatActionCellDelegate = this.delegate) != null) {
                    chatActionCellDelegate.needShowEffectOverlay(this, this.giftSticker, tLRPC$VideoSize);
                }
            }
        }
    }

    public ChatActionCell(Context context) {
        this(context, false, (ThemeDelegate) null);
    }

    public ChatActionCell(Context context, boolean z, ThemeDelegate themeDelegate2) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.giftButtonRect = new RectF();
        this.spoilers = new ArrayList();
        this.spoilersPool = new Stack<>();
        this.lineWidths = new ArrayList<>();
        this.lineHeights = new ArrayList<>();
        this.backgroundPath = new Path();
        this.rect = new RectF();
        this.invalidatePath = true;
        this.invalidateColors = false;
        this.giftTitlePaint = new TextPaint(1);
        this.giftSubtitlePaint = new TextPaint(1);
        this.giftStickerDelegate = new ChatActionCell$$ExternalSyntheticLambda3(this);
        this.starsPath = new Path();
        this.canDrawInParent = z;
        this.themeDelegate = themeDelegate2;
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        this.avatarDrawable = new AvatarDrawable();
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.giftTitlePaint.setTextSize(TypedValue.applyDimension(1, 16.0f, getResources().getDisplayMetrics()));
        this.giftSubtitlePaint.setTextSize(TypedValue.applyDimension(1, 15.0f, getResources().getDisplayMetrics()));
        View view = new View(context);
        this.rippleView = view;
        view.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 7, AndroidUtilities.dp(16.0f)));
        this.rippleView.setVisibility(8);
        addView(this.rippleView);
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(10);
        this.starParticlesDrawable = drawable;
        drawable.type = 100;
        drawable.isCircle = false;
        drawable.roundEffect = true;
        drawable.useRotate = false;
        drawable.useBlur = true;
        drawable.checkBounds = true;
        drawable.size1 = 1;
        drawable.k3 = 0.98f;
        drawable.k2 = 0.98f;
        drawable.k1 = 0.98f;
        drawable.paused = false;
        drawable.speedScale = 0.0f;
        drawable.minLifeTime = 750;
        drawable.randLifeTime = 750;
        drawable.init();
    }

    public void setDelegate(ChatActionCellDelegate chatActionCellDelegate) {
        this.delegate = chatActionCellDelegate;
    }

    public void setCustomDate(int i, boolean z, boolean z2) {
        String str;
        int i2 = this.customDate;
        if (i2 != i && i2 / 3600 != i / 3600) {
            if (!z) {
                str = LocaleController.formatDateChat((long) i);
            } else if (i == NUM) {
                str = LocaleController.getString("MessageScheduledUntilOnline", R.string.MessageScheduledUntilOnline);
            } else {
                str = LocaleController.formatString("MessageScheduledOn", R.string.MessageScheduledOn, LocaleController.formatDateChat((long) i));
            }
            this.customDate = i;
            CharSequence charSequence = this.customText;
            if (charSequence == null || !TextUtils.equals(str, charSequence)) {
                this.customText = str;
                this.accessibilityText = null;
                updateTextInternal(z2);
            }
        }
    }

    private void updateTextInternal(boolean z) {
        if (getMeasuredWidth() != 0) {
            createLayout(this.customText, getMeasuredWidth());
            invalidate();
        }
        if (this.wasLayout) {
            buildLayout();
        } else if (z) {
            AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda0(this));
        } else {
            requestLayout();
        }
    }

    public void setCustomText(CharSequence charSequence) {
        this.customText = charSequence;
        if (charSequence != null) {
            updateTextInternal(false);
        }
    }

    public void setOverrideColor(String str, String str2) {
        this.overrideBackground = str;
        this.overrideText = str2;
    }

    public void setMessageObject(MessageObject messageObject) {
        setMessageObject(messageObject, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x024f  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x026e  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x010b A[EDGE_INSN: B:112:0x010b->B:54:0x010b ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0105 A[LOOP:0: B:33:0x00ad->B:52:0x0105, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMessageObject(org.telegram.messenger.MessageObject r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            r12 = r19
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 != r12) goto L_0x0023
            android.text.StaticLayout r1 = r0.textLayout
            if (r1 == 0) goto L_0x0018
            java.lang.CharSequence r1 = r1.getText()
            java.lang.CharSequence r2 = r12.messageText
            boolean r1 = android.text.TextUtils.equals(r1, r2)
            if (r1 == 0) goto L_0x0023
        L_0x0018:
            boolean r1 = r0.hasReplyMessage
            if (r1 != 0) goto L_0x0020
            org.telegram.messenger.MessageObject r1 = r12.replyMessageObject
            if (r1 != 0) goto L_0x0023
        L_0x0020:
            if (r20 != 0) goto L_0x0023
            return
        L_0x0023:
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r1 == 0) goto L_0x0041
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            android.os.Handler r2 = org.telegram.messenger.ApplicationLoader.applicationHandler
            android.os.Looper r2 = r2.getLooper()
            java.lang.Thread r2 = r2.getThread()
            if (r1 == r2) goto L_0x0041
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "Wrong thread!!!"
            r1.<init>(r2)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0041:
            r1 = 0
            r0.accessibilityText = r1
            r0.currentMessageObject = r12
            org.telegram.messenger.MessageObject r2 = r12.replyMessageObject
            r13 = 1
            r14 = 0
            if (r2 == 0) goto L_0x004e
            r2 = 1
            goto L_0x004f
        L_0x004e:
            r2 = 0
        L_0x004f:
            r0.hasReplyMessage = r2
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            r2.removeLoadingFileObserver(r0)
            r0.previousWidth = r14
            int r2 = r12.type
            r15 = 18
            if (r2 != r15) goto L_0x01ae
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r2.setRoundRadius((int) r14)
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            java.lang.String r2 = r2.premiumGiftsStickerPack
            if (r2 != 0) goto L_0x007b
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.checkPremiumGiftStickers()
            return
        L_0x007b:
            int r3 = r0.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r3.getStickerSetByName(r2)
            if (r3 != 0) goto L_0x0091
            int r3 = r0.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r3.getStickerSetByEmojiOrName(r2)
        L_0x0091:
            r9 = r3
            if (r9 == 0) goto L_0x0120
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            int r3 = r3.months
            java.util.Map<java.lang.Integer, java.lang.String> r4 = monthsToEmoticon
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            java.lang.Object r3 = r4.get(r3)
            java.lang.String r3 = (java.lang.String) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_stickerPack> r4 = r9.packs
            java.util.Iterator r4 = r4.iterator()
            r5 = r1
        L_0x00ad:
            boolean r6 = r4.hasNext()
            if (r6 == 0) goto L_0x0109
            java.lang.Object r6 = r4.next()
            org.telegram.tgnet.TLRPC$TL_stickerPack r6 = (org.telegram.tgnet.TLRPC$TL_stickerPack) r6
            java.lang.String r7 = r6.emoticon
            boolean r7 = androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0.m(r7, r3)
            if (r7 == 0) goto L_0x0100
            java.util.ArrayList<java.lang.Long> r6 = r6.documents
            java.util.Iterator r6 = r6.iterator()
        L_0x00c7:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x0100
            java.lang.Object r7 = r6.next()
            java.lang.Long r7 = (java.lang.Long) r7
            long r7 = r7.longValue()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r9.documents
            java.util.Iterator r10 = r10.iterator()
        L_0x00dd:
            boolean r11 = r10.hasNext()
            if (r11 == 0) goto L_0x00f7
            java.lang.Object r11 = r10.next()
            org.telegram.tgnet.TLRPC$Document r11 = (org.telegram.tgnet.TLRPC$Document) r11
            r16 = r2
            long r1 = r11.id
            int r17 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r17 != 0) goto L_0x00f3
            r5 = r11
            goto L_0x00f9
        L_0x00f3:
            r2 = r16
            r1 = 0
            goto L_0x00dd
        L_0x00f7:
            r16 = r2
        L_0x00f9:
            if (r5 == 0) goto L_0x00fc
            goto L_0x0102
        L_0x00fc:
            r2 = r16
            r1 = 0
            goto L_0x00c7
        L_0x0100:
            r16 = r2
        L_0x0102:
            if (r5 == 0) goto L_0x0105
            goto L_0x010b
        L_0x0105:
            r2 = r16
            r1 = 0
            goto L_0x00ad
        L_0x0109:
            r16 = r2
        L_0x010b:
            if (r5 != 0) goto L_0x011e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r9.documents
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x011e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r9.documents
            java.lang.Object r1 = r1.get(r14)
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            goto L_0x0123
        L_0x011e:
            r1 = r5
            goto L_0x0123
        L_0x0120:
            r16 = r2
            r1 = 0
        L_0x0123:
            boolean r2 = r12.wasUnread
            r0.forceWasUnread = r2
            r0.giftSticker = r1
            if (r1 == 0) goto L_0x019d
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r2.setAllowStartLottieAnimation(r14)
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r3 = r0.giftStickerDelegate
            r2.setDelegate(r3)
            r2 = 0
            r0.giftEffectAnimation = r2
            r2 = 0
        L_0x013b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r1.video_thumbs
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0163
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r1.video_thumbs
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$VideoSize r3 = (org.telegram.tgnet.TLRPC$VideoSize) r3
            java.lang.String r3 = r3.type
            java.lang.String r4 = "f"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0160
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r1.video_thumbs
            java.lang.Object r2 = r3.get(r2)
            org.telegram.tgnet.TLRPC$VideoSize r2 = (org.telegram.tgnet.TLRPC$VideoSize) r2
            r0.giftEffectAnimation = r2
            goto L_0x0163
        L_0x0160:
            int r2 = r2 + 1
            goto L_0x013b
        L_0x0163:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r1.thumbs
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            java.lang.String r4 = "emptyListPlaceholder"
            org.telegram.messenger.SvgHelper$SvgDrawable r7 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r2, (java.lang.String) r4, (float) r3)
            if (r7 == 0) goto L_0x0175
            r2 = 512(0x200, float:7.175E-43)
            r7.overrideWidthAndHeight(r2, r2)
        L_0x0175:
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r2.setAutoRepeat(r14)
            org.telegram.messenger.ImageReceiver r4 = r0.imageReceiver
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r19.getId()
            r1.append(r2)
            java.lang.String r2 = "_130_130"
            r1.append(r2)
            java.lang.String r6 = r1.toString()
            r10 = 1
            java.lang.String r8 = "tgs"
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x02b4
        L_0x019d:
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r2 = r16
            if (r9 != 0) goto L_0x01a8
            goto L_0x01a9
        L_0x01a8:
            r13 = 0
        L_0x01a9:
            r1.loadStickersByEmojiOrName(r2, r14, r13)
            goto L_0x02b4
        L_0x01ae:
            r1 = 11
            if (r2 != r1) goto L_0x02a4
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartLottieAnimation(r13)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r2 = 0
            r1.setDelegate(r2)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            int r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            int r3 = r3 / 2
            r1.setRoundRadius((int) r3)
            long r3 = r19.getDialogId()
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setInfo(r3, r2, r2)
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r1 == 0) goto L_0x01e6
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r2 = 0
            r3 = 0
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r5 = 0
            r7 = 0
            r6 = r19
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x0299
        L_0x01e6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r12.photoThumbs
            int r1 = r1.size()
            r2 = 0
        L_0x01ed:
            if (r2 >= r1) goto L_0x0200
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r12.photoThumbs
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r4 == 0) goto L_0x01fd
            r2 = r3
            goto L_0x0201
        L_0x01fd:
            int r2 = r2 + 1
            goto L_0x01ed
        L_0x0200:
            r2 = 0
        L_0x0201:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r12.photoThumbs
            r3 = 640(0x280, float:8.97E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r3)
            if (r1 == 0) goto L_0x0292
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r4 = r3.video_sizes
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x024c
            boolean r4 = org.telegram.messenger.SharedConfig.autoplayGifs
            if (r4 == 0) goto L_0x024c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r4 = r3.video_sizes
            java.lang.Object r4 = r4.get(r14)
            org.telegram.tgnet.TLRPC$VideoSize r4 = (org.telegram.tgnet.TLRPC$VideoSize) r4
            boolean r5 = r12.mediaExists
            if (r5 != 0) goto L_0x024d
            int r5 = r0.currentAccount
            org.telegram.messenger.DownloadController r5 = org.telegram.messenger.DownloadController.getInstance(r5)
            r6 = 4
            int r7 = r4.size
            long r7 = (long) r7
            boolean r5 = r5.canDownloadMedia(r6, r7)
            if (r5 != 0) goto L_0x024d
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r3)
            r0.currentVideoLocation = r5
            java.lang.String r4 = org.telegram.messenger.FileLoader.getAttachFileName(r4)
            int r5 = r0.currentAccount
            org.telegram.messenger.DownloadController r5 = org.telegram.messenger.DownloadController.getInstance(r5)
            r5.addLoadingFileObserver(r4, r12, r0)
        L_0x024c:
            r4 = 0
        L_0x024d:
            if (r4 == 0) goto L_0x026e
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r3)
            org.telegram.tgnet.TLObject r4 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForObject(r2, r4)
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r7 = 0
            r9 = 0
            r11 = 1
            java.lang.String r5 = "g"
            java.lang.String r10 = "50_50_b"
            r2 = r3
            r3 = r5
            r5 = r10
            r10 = r19
            r1.setImage(r2, r3, r4, r5, r6, r7, r9, r10, r11)
            goto L_0x0299
        L_0x026e:
            org.telegram.messenger.ImageReceiver r3 = r0.imageReceiver
            org.telegram.tgnet.TLObject r4 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForObject(r1, r4)
            org.telegram.tgnet.TLObject r1 = r12.photoThumbsObject
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r2, r1)
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            r7 = 0
            r9 = 0
            r11 = 1
            java.lang.String r10 = "150_150"
            java.lang.String r16 = "50_50_b"
            r1 = r3
            r2 = r4
            r3 = r10
            r4 = r5
            r5 = r16
            r10 = r19
            r1.setImage(r2, r3, r4, r5, r6, r7, r9, r10, r11)
            goto L_0x0299
        L_0x0292:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x0299:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.messenger.MessageObject) r19)
            r2 = r2 ^ r13
            r1.setVisible(r2, r14)
            goto L_0x02b4
        L_0x02a4:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartLottieAnimation(r13)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r2 = 0
            r1.setDelegate(r2)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setImageBitmap((android.graphics.Bitmap) r2)
        L_0x02b4:
            android.view.View r1 = r0.rippleView
            int r2 = r12.type
            if (r2 != r15) goto L_0x02bb
            goto L_0x02bd
        L_0x02bb:
            r14 = 8
        L_0x02bd:
            r1.setVisibility(r14)
            r18.requestLayout()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.setMessageObject(org.telegram.messenger.MessageObject, boolean):void");
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public ImageReceiver getPhotoImage() {
        return this.imageReceiver;
    }

    public void setVisiblePart(float f, int i) {
        this.visiblePartSet = true;
        this.backgroundHeight = i;
        this.viewTop = f;
    }

    /* access modifiers changed from: protected */
    public boolean onLongPress() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null) {
            return chatActionCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View view = this.rippleView;
        RectF rectF = this.giftButtonRect;
        view.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.imageReceiver.onDetachedFromWindow();
        setStarsPaused(true);
        this.wasLayout = false;
        AnimatedEmojiSpan.release((View) this, this.animatedEmojiStack);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
        setStarsPaused(false);
        this.animatedEmojiStack = AnimatedEmojiSpan.update(0, (View) this, this.animatedEmojiStack, this.textLayout);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    private void setStarsPaused(boolean z) {
        StarParticlesView.Drawable drawable = this.starParticlesDrawable;
        if (z != drawable.paused) {
            drawable.paused = z;
            if (z) {
                drawable.pausedTime = System.currentTimeMillis();
                return;
            }
            for (int i = 0; i < this.starParticlesDrawable.particles.size(); i++) {
                this.starParticlesDrawable.particles.get(i).lifeTime += System.currentTimeMillis() - this.starParticlesDrawable.pausedTime;
            }
            invalidate();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:92:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r11) {
        /*
            r10 = this;
            org.telegram.messenger.MessageObject r0 = r10.currentMessageObject
            if (r0 != 0) goto L_0x0009
            boolean r11 = super.onTouchEvent(r11)
            return r11
        L_0x0009:
            float r1 = r11.getX()
            r10.lastTouchX = r1
            float r2 = r11.getY()
            r10.lastTouchY = r2
            int r3 = r11.getAction()
            r4 = 18
            r5 = 1
            r6 = 0
            if (r3 != 0) goto L_0x0053
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r3 = r10.delegate
            if (r3 == 0) goto L_0x00c8
            int r3 = r0.type
            r7 = 11
            if (r3 == r7) goto L_0x002b
            if (r3 != r4) goto L_0x0037
        L_0x002b:
            org.telegram.messenger.ImageReceiver r3 = r10.imageReceiver
            boolean r3 = r3.isInsideImage(r1, r2)
            if (r3 == 0) goto L_0x0037
            r10.imagePressed = r5
            r3 = 1
            goto L_0x0038
        L_0x0037:
            r3 = 0
        L_0x0038:
            int r7 = r0.type
            if (r7 != r4) goto L_0x004c
            android.graphics.RectF r4 = r10.giftButtonRect
            boolean r4 = r4.contains(r1, r2)
            if (r4 == 0) goto L_0x004c
            android.view.View r3 = r10.rippleView
            r10.giftButtonPressed = r5
            r3.setPressed(r5)
            r3 = 1
        L_0x004c:
            if (r3 == 0) goto L_0x00c9
            r10.startCheckLongPress()
            goto L_0x00c9
        L_0x0053:
            int r3 = r11.getAction()
            r7 = 2
            if (r3 == r7) goto L_0x005d
            r10.cancelCheckLongPress()
        L_0x005d:
            boolean r3 = r10.imagePressed
            r8 = 3
            if (r3 == 0) goto L_0x0090
            int r3 = r11.getAction()
            if (r3 == r5) goto L_0x007b
            if (r3 == r7) goto L_0x0070
            if (r3 == r8) goto L_0x006d
            goto L_0x00c8
        L_0x006d:
            r10.imagePressed = r6
            goto L_0x00c8
        L_0x0070:
            org.telegram.messenger.ImageReceiver r3 = r10.imageReceiver
            boolean r3 = r3.isInsideImage(r1, r2)
            if (r3 != 0) goto L_0x00c8
            r10.imagePressed = r6
            goto L_0x00c8
        L_0x007b:
            r10.imagePressed = r6
            int r3 = r0.type
            if (r3 != r4) goto L_0x0085
            r10.openPremiumGiftPreview()
            goto L_0x00c8
        L_0x0085:
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r3 = r10.delegate
            if (r3 == 0) goto L_0x00c8
            r3.didClickImage(r10)
            r10.playSoundEffect(r6)
            goto L_0x00c8
        L_0x0090:
            boolean r3 = r10.giftButtonPressed
            if (r3 == 0) goto L_0x00c8
            int r3 = r11.getAction()
            if (r3 == r5) goto L_0x00b7
            if (r3 == r7) goto L_0x00a7
            if (r3 == r8) goto L_0x009f
            goto L_0x00c8
        L_0x009f:
            android.view.View r3 = r10.rippleView
            r10.giftButtonPressed = r6
            r3.setPressed(r6)
            goto L_0x00c8
        L_0x00a7:
            android.graphics.RectF r3 = r10.giftButtonRect
            boolean r3 = r3.contains(r1, r2)
            if (r3 != 0) goto L_0x00c8
            android.view.View r3 = r10.rippleView
            r10.giftButtonPressed = r6
            r3.setPressed(r6)
            goto L_0x00c8
        L_0x00b7:
            android.view.View r3 = r10.rippleView
            r10.giftButtonPressed = r6
            r3.setPressed(r6)
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r3 = r10.delegate
            if (r3 == 0) goto L_0x00c8
            r10.playSoundEffect(r6)
            r10.openPremiumGiftPreview()
        L_0x00c8:
            r3 = 0
        L_0x00c9:
            if (r3 != 0) goto L_0x0154
            int r4 = r11.getAction()
            if (r4 == 0) goto L_0x00db
            android.text.style.URLSpan r4 = r10.pressedLink
            if (r4 == 0) goto L_0x0154
            int r4 = r11.getAction()
            if (r4 != r5) goto L_0x0154
        L_0x00db:
            int r4 = r10.textX
            float r7 = (float) r4
            r8 = 0
            int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r7 < 0) goto L_0x0152
            int r7 = r10.textY
            float r9 = (float) r7
            int r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0152
            int r9 = r10.textWidth
            int r4 = r4 + r9
            float r4 = (float) r4
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0152
            int r4 = r10.textHeight
            int r4 = r4 + r7
            float r4 = (float) r4
            int r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0152
            float r4 = (float) r7
            float r2 = r2 - r4
            int r4 = r10.textXLeft
            float r4 = (float) r4
            float r1 = r1 - r4
            android.text.StaticLayout r4 = r10.textLayout
            int r2 = (int) r2
            int r2 = r4.getLineForVertical(r2)
            android.text.StaticLayout r4 = r10.textLayout
            int r4 = r4.getOffsetForHorizontal(r2, r1)
            android.text.StaticLayout r7 = r10.textLayout
            float r7 = r7.getLineLeft(r2)
            int r9 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r9 > 0) goto L_0x014f
            android.text.StaticLayout r9 = r10.textLayout
            float r2 = r9.getLineWidth(r2)
            float r7 = r7 + r2
            int r1 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r1 < 0) goto L_0x014f
            java.lang.CharSequence r0 = r0.messageText
            boolean r1 = r0 instanceof android.text.Spannable
            if (r1 == 0) goto L_0x014f
            android.text.Spannable r0 = (android.text.Spannable) r0
            java.lang.Class<android.text.style.URLSpan> r1 = android.text.style.URLSpan.class
            java.lang.Object[] r0 = r0.getSpans(r4, r4, r1)
            android.text.style.URLSpan[] r0 = (android.text.style.URLSpan[]) r0
            int r1 = r0.length
            if (r1 == 0) goto L_0x014a
            int r1 = r11.getAction()
            if (r1 != 0) goto L_0x0140
            r0 = r0[r6]
            r10.pressedLink = r0
            goto L_0x014d
        L_0x0140:
            r0 = r0[r6]
            android.text.style.URLSpan r1 = r10.pressedLink
            if (r0 != r1) goto L_0x014c
            r10.openLink(r1)
            goto L_0x014d
        L_0x014a:
            r10.pressedLink = r8
        L_0x014c:
            r5 = r3
        L_0x014d:
            r3 = r5
            goto L_0x0154
        L_0x014f:
            r10.pressedLink = r8
            goto L_0x0154
        L_0x0152:
            r10.pressedLink = r8
        L_0x0154:
            if (r3 != 0) goto L_0x015a
            boolean r3 = super.onTouchEvent(r11)
        L_0x015a:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void openPremiumGiftPreview() {
        TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption = new TLRPC$TL_premiumGiftOption();
        TLRPC$MessageAction tLRPC$MessageAction = this.currentMessageObject.messageOwner.action;
        tLRPC$TL_premiumGiftOption.amount = tLRPC$MessageAction.amount;
        tLRPC$TL_premiumGiftOption.months = tLRPC$MessageAction.months;
        tLRPC$TL_premiumGiftOption.currency = tLRPC$MessageAction.currency;
        if (this.delegate != null) {
            AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda1(this, tLRPC$TL_premiumGiftOption));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openPremiumGiftPreview$1(TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption) {
        this.delegate.didOpenPremiumGift(this, tLRPC$TL_premiumGiftOption, false);
    }

    /* access modifiers changed from: private */
    public void openLink(CharacterStyle characterStyle) {
        if (this.delegate != null && (characterStyle instanceof URLSpan)) {
            String url = ((URLSpan) characterStyle).getURL();
            if (url.startsWith("invite")) {
                URLSpan uRLSpan = this.pressedLink;
                if (uRLSpan instanceof URLSpanNoUnderline) {
                    TLObject object = ((URLSpanNoUnderline) uRLSpan).getObject();
                    if (object instanceof TLRPC$TL_chatInviteExported) {
                        this.delegate.needOpenInviteLink((TLRPC$TL_chatInviteExported) object);
                        return;
                    }
                    return;
                }
            }
            if (url.startsWith("game")) {
                this.delegate.didPressReplyMessage(this, this.currentMessageObject.getReplyMsgId());
            } else if (url.startsWith("http")) {
                Browser.openUrl(getContext(), url);
            } else {
                this.delegate.needOpenUserProfile(Long.parseLong(url));
            }
        }
    }

    private void createLayout(CharSequence charSequence, int i) {
        int dp = i - AndroidUtilities.dp(30.0f);
        this.invalidatePath = true;
        this.textLayout = new StaticLayout(charSequence, (TextPaint) getThemedPaint("paintChatActionText"), dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.spoilersPool.addAll(this.spoilers);
        this.spoilers.clear();
        if (charSequence instanceof Spannable) {
            SpoilerEffect.addSpoilers(this, this.textLayout, (Spannable) charSequence, this.spoilersPool, this.spoilers);
        }
        int i2 = 0;
        this.animatedEmojiStack = AnimatedEmojiSpan.update(0, (View) this, this.animatedEmojiStack, this.textLayout);
        this.textHeight = 0;
        this.textWidth = 0;
        try {
            int lineCount = this.textLayout.getLineCount();
            while (i2 < lineCount) {
                try {
                    float lineWidth = this.textLayout.getLineWidth(i2);
                    float f = (float) dp;
                    if (lineWidth > f) {
                        lineWidth = f;
                    }
                    this.textHeight = (int) Math.max((double) this.textHeight, Math.ceil((double) this.textLayout.getLineBottom(i2)));
                    this.textWidth = (int) Math.max((double) this.textWidth, Math.ceil((double) lineWidth));
                    i2++;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return;
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        this.textX = (i - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (i - this.textLayout.getWidth()) / 2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int dp;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null && this.customText == null) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), this.textHeight + AndroidUtilities.dp(14.0f));
            return;
        }
        if (messageObject != null && messageObject.type == 18) {
            int min = Math.min((int) (((float) (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) * 0.6f), ((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(64.0f));
            this.giftRectSize = min;
            this.stickerSize = min - AndroidUtilities.dp(106.0f);
        }
        int max = Math.max(AndroidUtilities.dp(30.0f), View.MeasureSpec.getSize(i));
        if (this.previousWidth != max) {
            this.wasLayout = true;
            this.previousWidth = max;
            buildLayout();
        }
        int i4 = 0;
        if (messageObject != null) {
            int i5 = messageObject.type;
            if (i5 == 11) {
                i3 = AndroidUtilities.roundMessageSize;
                dp = AndroidUtilities.dp(10.0f);
            } else if (i5 == 18) {
                i3 = this.giftRectSize;
                dp = AndroidUtilities.dp(12.0f);
            }
            i4 = i3 + dp;
        }
        setMeasuredDimension(max, this.textHeight + i4 + AndroidUtilities.dp(14.0f));
        if (messageObject != null && messageObject.type == 18) {
            float dp2 = ((float) (this.textY + this.textHeight)) + (((float) this.giftRectSize) * 0.075f) + ((float) this.stickerSize) + ((float) AndroidUtilities.dp(4.0f)) + ((float) this.giftPremiumTitleLayout.getHeight()) + ((float) AndroidUtilities.dp(4.0f)) + ((float) this.giftPremiumSubtitleLayout.getHeight());
            float measuredHeight = dp2 + ((((((float) getMeasuredHeight()) - dp2) - ((float) this.giftPremiumButtonLayout.getHeight())) - ((float) AndroidUtilities.dp(8.0f))) / 2.0f);
            float f = (((float) this.previousWidth) - this.giftPremiumButtonWidth) / 2.0f;
            this.giftButtonRect.set(f - ((float) AndroidUtilities.dp(18.0f)), measuredHeight - ((float) AndroidUtilities.dp(8.0f)), f + this.giftPremiumButtonWidth + ((float) AndroidUtilities.dp(18.0f)), measuredHeight + ((float) this.giftPremiumButtonLayout.getHeight()) + ((float) AndroidUtilities.dp(8.0f)));
            int measuredWidth = getMeasuredWidth() << (getMeasuredHeight() + 16);
            this.starParticlesDrawable.rect.set(this.giftButtonRect);
            this.starParticlesDrawable.rect2.set(this.giftButtonRect);
            if (this.starsSize != measuredWidth) {
                this.starsSize = measuredWidth;
                this.starParticlesDrawable.resetPositions();
            }
        }
    }

    private void buildLayout() {
        CharSequence charSequence;
        TLRPC$MessageMedia tLRPC$MessageMedia;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message == null || (tLRPC$MessageMedia = tLRPC$Message.media) == null || tLRPC$MessageMedia.ttl_seconds == 0) {
                charSequence = AnimatedEmojiSpan.cloneSpans(messageObject.messageText);
            } else if (tLRPC$MessageMedia.photo instanceof TLRPC$TL_photoEmpty) {
                charSequence = LocaleController.getString("AttachPhotoExpired", R.string.AttachPhotoExpired);
            } else if (tLRPC$MessageMedia.document instanceof TLRPC$TL_documentEmpty) {
                charSequence = LocaleController.getString("AttachVideoExpired", R.string.AttachVideoExpired);
            } else {
                charSequence = AnimatedEmojiSpan.cloneSpans(messageObject.messageText);
            }
        } else {
            charSequence = this.customText;
        }
        createLayout(charSequence, this.previousWidth);
        if (messageObject != null) {
            int i = messageObject.type;
            if (i == 11) {
                int i2 = AndroidUtilities.roundMessageSize;
                this.imageReceiver.setImageCoords(((float) (this.previousWidth - AndroidUtilities.roundMessageSize)) / 2.0f, (float) (this.textHeight + AndroidUtilities.dp(19.0f)), (float) i2, (float) i2);
            } else if (i == 18) {
                createGiftPremiumLayouts(LocaleController.getString(R.string.ActionGiftPremiumTitle), LocaleController.formatString(R.string.ActionGiftPremiumSubtitle, LocaleController.formatPluralString("Months", messageObject.messageOwner.action.months, new Object[0])), LocaleController.getString(R.string.ActionGiftPremiumView), this.giftRectSize);
            }
        }
    }

    private void createGiftPremiumLayouts(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, int i) {
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
        valueOf.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, valueOf.length(), 33);
        this.giftPremiumTitleLayout = new StaticLayout(valueOf, this.giftTitlePaint, i, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.giftPremiumSubtitleLayout = new StaticLayout(charSequence2, this.giftSubtitlePaint, i, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        SpannableStringBuilder valueOf2 = SpannableStringBuilder.valueOf(charSequence3);
        valueOf2.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, valueOf2.length(), 33);
        StaticLayout staticLayout = new StaticLayout(valueOf2, (TextPaint) getThemedPaint("paintChatActionText"), i, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.giftPremiumButtonLayout = staticLayout;
        this.giftPremiumButtonWidth = measureLayoutWidth(staticLayout);
    }

    private float measureLayoutWidth(Layout layout) {
        float f = 0.0f;
        for (int i = 0; i < layout.getLineCount(); i++) {
            float ceil = (float) ((int) Math.ceil((double) layout.getLineWidth(i)));
            if (ceil > f) {
                f = ceil;
            }
        }
        return f;
    }

    public int getCustomDate() {
        return this.customDate;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        TextPaint textPaint2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 18) {
            int dp = this.giftRectSize - AndroidUtilities.dp(106.0f);
            this.stickerSize = dp;
            this.imageReceiver.setImageCoords(((float) (this.previousWidth - dp)) / 2.0f, ((float) (this.textY + this.textHeight)) + (((float) this.giftRectSize) * 0.075f), (float) dp, (float) dp);
            if (!(this.textPaint == null || (textPaint2 = this.giftTitlePaint) == null || this.giftSubtitlePaint == null)) {
                if (textPaint2.getColor() != this.textPaint.getColor()) {
                    this.giftTitlePaint.setColor(this.textPaint.getColor());
                }
                if (this.giftSubtitlePaint.getColor() != this.textPaint.getColor()) {
                    this.giftSubtitlePaint.setColor(this.textPaint.getColor());
                }
            }
        }
        if (messageObject != null && ((i = messageObject.type) == 11 || i == 18)) {
            this.imageReceiver.draw(canvas);
        }
        if (this.textLayout != null) {
            drawBackground(canvas, false);
            if (this.textPaint != null) {
                canvas.save();
                canvas.translate((float) this.textXLeft, (float) this.textY);
                if (this.textLayout.getPaint() != this.textPaint) {
                    buildLayout();
                }
                canvas.save();
                SpoilerEffect.clipOutCanvas(canvas, this.spoilers);
                AnimatedEmojiSpan.drawAnimatedEmojis(canvas, this.textLayout, this.animatedEmojiStack, 0.0f, this.spoilers, 0.0f, 0.0f, 0.0f, 1.0f);
                this.textLayout.draw(canvas);
                canvas.restore();
                for (SpoilerEffect next : this.spoilers) {
                    next.setColor(this.textLayout.getPaint().getColor());
                    next.draw(canvas);
                }
                canvas.restore();
            }
            if (messageObject != null && messageObject.type == 18) {
                canvas.save();
                int i2 = this.previousWidth;
                int i3 = this.giftRectSize;
                float f = ((float) (i2 - i3)) / 2.0f;
                float dp2 = ((float) (this.textY + this.textHeight)) + (((float) i3) * 0.075f) + ((float) this.stickerSize) + ((float) AndroidUtilities.dp(4.0f));
                canvas.translate(f, dp2);
                this.giftPremiumTitleLayout.draw(canvas);
                canvas.restore();
                float height = dp2 + ((float) this.giftPremiumTitleLayout.getHeight()) + ((float) AndroidUtilities.dp(4.0f));
                canvas.save();
                canvas.translate(f, height);
                this.giftPremiumSubtitleLayout.draw(canvas);
                canvas.restore();
                float height2 = height + ((float) this.giftPremiumSubtitleLayout.getHeight());
                float height3 = height2 + ((((((float) getHeight()) - height2) - ((float) this.giftPremiumButtonLayout.getHeight())) - ((float) AndroidUtilities.dp(8.0f))) / 2.0f);
                canvas.drawRoundRect(this.giftButtonRect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), getThemedPaint("paintChatActionBackground"));
                if (hasGradientService()) {
                    canvas.drawRoundRect(this.giftButtonRect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                }
                this.starsPath.rewind();
                this.starsPath.addRoundRect(this.giftButtonRect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.starsPath);
                this.starParticlesDrawable.onDraw(canvas);
                if (!this.starParticlesDrawable.paused) {
                    invalidate();
                }
                canvas.restore();
                canvas.save();
                canvas.translate(f, height3);
                this.giftPremiumButtonLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void drawBackground(Canvas canvas, boolean z) {
        Paint paint;
        Paint paint2;
        int i;
        Paint paint3;
        int i2;
        float f;
        int i3;
        int i4;
        int i5;
        int i6;
        float f2;
        float f3;
        int i7;
        Canvas canvas2 = canvas;
        if (this.canDrawInParent) {
            if (hasGradientService() && !z) {
                return;
            }
            if (!hasGradientService() && z) {
                return;
            }
        }
        Paint themedPaint = getThemedPaint("paintChatActionBackground");
        this.textPaint = (TextPaint) getThemedPaint("paintChatActionText");
        String str = this.overrideBackground;
        if (str != null) {
            int themedColor = getThemedColor(str);
            if (this.overrideBackgroundPaint == null) {
                Paint paint4 = new Paint(1);
                this.overrideBackgroundPaint = paint4;
                paint4.setColor(themedColor);
                TextPaint textPaint2 = new TextPaint(1);
                this.overrideTextPaint = textPaint2;
                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.overrideTextPaint.setTextSize((float) AndroidUtilities.dp((float) (Math.max(16, SharedConfig.fontSize) - 2)));
                this.overrideTextPaint.setColor(getThemedColor(this.overrideText));
            }
            themedPaint = this.overrideBackgroundPaint;
            this.textPaint = this.overrideTextPaint;
        }
        if (this.invalidatePath) {
            this.invalidatePath = false;
            this.lineWidths.clear();
            int lineCount = this.textLayout.getLineCount();
            int dp = AndroidUtilities.dp(11.0f);
            int dp2 = AndroidUtilities.dp(8.0f);
            int i8 = 0;
            for (int i9 = 0; i9 < lineCount; i9++) {
                int ceil = (int) Math.ceil((double) this.textLayout.getLineWidth(i9));
                if (i9 == 0 || (i7 = i8 - ceil) <= 0 || i7 > dp + dp2) {
                    i8 = ceil;
                }
                this.lineWidths.add(Integer.valueOf(i8));
            }
            for (int i10 = lineCount - 2; i10 >= 0; i10--) {
                int intValue = this.lineWidths.get(i10).intValue();
                int i11 = i8 - intValue;
                if (i11 <= 0 || i11 > dp + dp2) {
                    i8 = intValue;
                }
                this.lineWidths.set(i10, Integer.valueOf(i8));
            }
            int dp3 = AndroidUtilities.dp(4.0f);
            int measuredWidth = getMeasuredWidth() / 2;
            int dp4 = AndroidUtilities.dp(3.0f);
            int dp5 = AndroidUtilities.dp(6.0f);
            int i12 = dp - dp4;
            this.lineHeights.clear();
            this.backgroundPath.reset();
            float f4 = (float) measuredWidth;
            this.backgroundPath.moveTo(f4, (float) dp3);
            int i13 = 0;
            int i14 = 0;
            while (i13 < lineCount) {
                int intValue2 = this.lineWidths.get(i13).intValue();
                int lineBottom = this.textLayout.getLineBottom(i13);
                int i15 = dp2;
                int i16 = lineCount - 1;
                if (i13 < i16) {
                    paint3 = themedPaint;
                    i2 = this.lineWidths.get(i13 + 1).intValue();
                } else {
                    paint3 = themedPaint;
                    i2 = 0;
                }
                int i17 = lineBottom - i14;
                if (i13 == 0 || intValue2 > i8) {
                    f = 3.0f;
                    i17 += AndroidUtilities.dp(3.0f);
                } else {
                    f = 3.0f;
                }
                if (i13 == i16 || intValue2 > i2) {
                    i17 += AndroidUtilities.dp(f);
                }
                float f5 = (((float) intValue2) / 2.0f) + f4;
                int i18 = (i13 == i16 || intValue2 >= i2 || i13 == 0 || intValue2 >= i8) ? i15 : dp5;
                if (i13 == 0 || intValue2 > i8) {
                    f2 = f4;
                    i4 = lineCount;
                    i3 = i8;
                    i5 = measuredWidth;
                    i6 = lineBottom;
                    this.rect.set((f5 - ((float) dp4)) - ((float) dp), (float) dp3, ((float) i12) + f5, (float) ((dp * 2) + dp3));
                    this.backgroundPath.arcTo(this.rect, -90.0f, 90.0f);
                } else {
                    f2 = f4;
                    if (intValue2 < i8) {
                        i6 = lineBottom;
                        float f6 = ((float) i12) + f5;
                        i5 = measuredWidth;
                        i4 = lineCount;
                        int i19 = i18 * 2;
                        i3 = i8;
                        this.rect.set(f6, (float) dp3, ((float) i19) + f6, (float) (i19 + dp3));
                        this.backgroundPath.arcTo(this.rect, -90.0f, -90.0f);
                    } else {
                        i4 = lineCount;
                        i3 = i8;
                        i5 = measuredWidth;
                        i6 = lineBottom;
                    }
                }
                dp3 += i17;
                if (i13 == i16 || intValue2 >= i2) {
                    f3 = 3.0f;
                } else {
                    f3 = 3.0f;
                    dp3 -= AndroidUtilities.dp(3.0f);
                    i17 -= AndroidUtilities.dp(3.0f);
                }
                if (i13 != 0 && intValue2 < i3) {
                    dp3 -= AndroidUtilities.dp(f3);
                    i17 -= AndroidUtilities.dp(f3);
                }
                this.lineHeights.add(Integer.valueOf(i17));
                if (i13 == i16 || intValue2 > i2) {
                    this.rect.set((f5 - ((float) dp4)) - ((float) dp), (float) (dp3 - (dp * 2)), f5 + ((float) i12), (float) dp3);
                    this.backgroundPath.arcTo(this.rect, 0.0f, 90.0f);
                } else if (intValue2 < i2) {
                    float f7 = f5 + ((float) i12);
                    int i20 = i18 * 2;
                    this.rect.set(f7, (float) (dp3 - i20), ((float) i20) + f7, (float) dp3);
                    this.backgroundPath.arcTo(this.rect, 180.0f, -90.0f);
                }
                i13++;
                Canvas canvas3 = canvas;
                i8 = intValue2;
                dp2 = i15;
                themedPaint = paint3;
                f4 = f2;
                i14 = i6;
                measuredWidth = i5;
                lineCount = i4;
            }
            paint = themedPaint;
            int i21 = dp2;
            int i22 = measuredWidth;
            int i23 = lineCount - 1;
            int i24 = i23;
            while (i24 >= 0) {
                int intValue3 = i24 != 0 ? this.lineWidths.get(i24 - 1).intValue() : 0;
                int intValue4 = this.lineWidths.get(i24).intValue();
                int intValue5 = i24 != i23 ? this.lineWidths.get(i24 + 1).intValue() : 0;
                this.textLayout.getLineBottom(i24);
                float f8 = (float) (i22 - (intValue4 / 2));
                int i25 = (i24 == i23 || intValue4 >= intValue5 || i24 == 0 || intValue4 >= intValue3) ? i21 : dp5;
                if (i24 == i23 || intValue4 > intValue5) {
                    this.rect.set(f8 - ((float) i12), (float) (dp3 - (dp * 2)), ((float) dp4) + f8 + ((float) dp), (float) dp3);
                    this.backgroundPath.arcTo(this.rect, 90.0f, 90.0f);
                } else if (intValue4 < intValue5) {
                    float f9 = f8 - ((float) i12);
                    int i26 = i25 * 2;
                    this.rect.set(f9 - ((float) i26), (float) (dp3 - i26), f9, (float) dp3);
                    this.backgroundPath.arcTo(this.rect, 90.0f, -90.0f);
                }
                dp3 -= this.lineHeights.get(i24).intValue();
                if (i24 == 0 || intValue4 > intValue3) {
                    this.rect.set(f8 - ((float) i12), (float) dp3, f8 + ((float) dp4) + ((float) dp), (float) ((dp * 2) + dp3));
                    this.backgroundPath.arcTo(this.rect, 180.0f, 90.0f);
                } else if (intValue4 < intValue3) {
                    float var_ = f8 - ((float) i12);
                    int i27 = i25 * 2;
                    this.rect.set(var_ - ((float) i27), (float) dp3, var_, (float) (i27 + dp3));
                    this.backgroundPath.arcTo(this.rect, 0.0f, -90.0f);
                }
                i24--;
            }
            this.backgroundPath.close();
        } else {
            paint = themedPaint;
        }
        if (!this.visiblePartSet) {
            this.backgroundHeight = ((ViewGroup) getParent()).getMeasuredHeight();
        }
        ThemeDelegate themeDelegate2 = this.themeDelegate;
        if (themeDelegate2 != null) {
            themeDelegate2.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, 0.0f, this.viewTop + ((float) AndroidUtilities.dp(4.0f)));
        } else {
            Theme.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, 0.0f, this.viewTop + ((float) AndroidUtilities.dp(4.0f)));
        }
        int i28 = -1;
        if (!z || getAlpha() == 1.0f) {
            paint2 = paint;
            i = -1;
        } else {
            i28 = paint.getAlpha();
            i = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
            paint2 = paint;
            paint2.setAlpha((int) (((float) i28) * getAlpha()));
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (((float) i) * getAlpha()));
        }
        Canvas canvas4 = canvas;
        canvas4.drawPath(this.backgroundPath, paint2);
        if (hasGradientService()) {
            canvas4.drawPath(this.backgroundPath, Theme.chat_actionBackgroundGradientDarkenPaint);
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 18) {
            float width = ((float) (getWidth() - this.giftRectSize)) / 2.0f;
            float dp6 = (float) (this.textY + this.textHeight + AndroidUtilities.dp(12.0f));
            RectF rectF = AndroidUtilities.rectTmp;
            int i29 = this.giftRectSize;
            rectF.set(width, dp6, ((float) i29) + width, ((float) i29) + dp6);
            canvas4.drawRoundRect(rectF, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), paint2);
            if (hasGradientService()) {
                canvas4.drawRoundRect(rectF, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
        }
        if (i28 >= 0) {
            paint2.setAlpha(i28);
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(i);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.themeDelegate;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasGradientService() {
        /*
            r1 = this;
            android.graphics.Paint r0 = r1.overrideBackgroundPaint
            if (r0 != 0) goto L_0x0017
            org.telegram.ui.Cells.ChatActionCell$ThemeDelegate r0 = r1.themeDelegate
            if (r0 == 0) goto L_0x000f
            boolean r0 = r0.hasGradientService()
            if (r0 == 0) goto L_0x0017
            goto L_0x0015
        L_0x000f:
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasGradientService()
            if (r0 == 0) goto L_0x0017
        L_0x0015:
            r0 = 1
            goto L_0x0018
        L_0x0017:
            r0 = 0
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.hasGradientService():boolean");
    }

    public void onSuccessDownload(String str) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 11) {
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            int i = 0;
            int size = messageObject.photoThumbs.size();
            while (true) {
                if (i >= size) {
                    break;
                }
                TLRPC$PhotoSize tLRPC$PhotoSize2 = messageObject.photoThumbs.get(i);
                if (tLRPC$PhotoSize2 instanceof TLRPC$TL_photoStrippedSize) {
                    tLRPC$PhotoSize = tLRPC$PhotoSize2;
                    break;
                }
                i++;
            }
            this.imageReceiver.setImage(this.currentVideoLocation, "g", ImageLocation.getForObject(tLRPC$PhotoSize, messageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, messageObject, 1);
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        MessageObject messageObject = this.currentMessageObject;
        if (!TextUtils.isEmpty(this.customText) || messageObject != null) {
            if (this.accessibilityText == null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(!TextUtils.isEmpty(this.customText) ? this.customText : messageObject.messageText);
                for (final CharacterStyle characterStyle : (CharacterStyle[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ClickableSpan.class)) {
                    int spanStart = spannableStringBuilder.getSpanStart(characterStyle);
                    int spanEnd = spannableStringBuilder.getSpanEnd(characterStyle);
                    spannableStringBuilder.removeSpan(characterStyle);
                    spannableStringBuilder.setSpan(new ClickableSpan() {
                        public void onClick(View view) {
                            if (ChatActionCell.this.delegate != null) {
                                ChatActionCell.this.openLink(characterStyle);
                            }
                        }
                    }, spanStart, spanEnd, 33);
                }
                this.accessibilityText = spannableStringBuilder;
            }
            if (Build.VERSION.SDK_INT < 24) {
                accessibilityNodeInfo.setContentDescription(this.accessibilityText.toString());
            } else {
                accessibilityNodeInfo.setText(this.accessibilityText);
            }
            accessibilityNodeInfo.setEnabled(true);
        }
    }

    public void setInvalidateColors(boolean z) {
        if (this.invalidateColors != z) {
            this.invalidateColors = z;
            invalidate();
        }
    }

    private int getThemedColor(String str) {
        ThemeDelegate themeDelegate2 = this.themeDelegate;
        Integer color = themeDelegate2 != null ? themeDelegate2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Paint getThemedPaint(String str) {
        ThemeDelegate themeDelegate2 = this.themeDelegate;
        Paint paint = themeDelegate2 != null ? themeDelegate2.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }
}
