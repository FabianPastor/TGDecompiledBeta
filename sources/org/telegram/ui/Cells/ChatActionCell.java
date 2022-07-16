package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_premiumGiftOption;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    private SpannableStringBuilder accessibilityText;
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        if (i == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
        } else if (i == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
        } else if (i == NotificationCenter.didUpdatePremiumGiftStickers && (messageObject = this.currentMessageObject) != null) {
            setMessageObject(messageObject);
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
            if (messageObject == null || !messageObject.wasUnread) {
                lottieAnimation.stop();
                lottieAnimation.setCurrentFrame(lottieAnimation.getFramesCount() - 1, false);
                return;
            }
            messageObject.wasUnread = false;
            try {
                performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (getContext() instanceof LaunchActivity) {
                ((LaunchActivity) getContext()).getFireworksOverlay().start();
            }
            TLRPC$VideoSize tLRPC$VideoSize = this.giftEffectAnimation;
            if (!(tLRPC$VideoSize == null || (chatActionCellDelegate = this.delegate) == null)) {
                chatActionCellDelegate.needShowEffectOverlay(this, this.giftSticker, tLRPC$VideoSize);
            }
            lottieAnimation.setCurrentFrame(0, false);
            AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda2(lottieAnimation));
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
                str = LocaleController.getString("MessageScheduledUntilOnline", NUM);
            } else {
                str = LocaleController.formatString("MessageScheduledOn", NUM, LocaleController.formatDateChat((long) i));
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
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$Document tLRPC$Document;
        StaticLayout staticLayout;
        MessageObject messageObject2 = messageObject;
        if (this.currentMessageObject != messageObject2 || (((staticLayout = this.textLayout) != null && !TextUtils.equals(staticLayout.getText(), messageObject2.messageText)) || (!this.hasReplyMessage && messageObject2.replyMessageObject != null))) {
            TLRPC$VideoSize tLRPC$VideoSize = null;
            this.accessibilityText = null;
            this.currentMessageObject = messageObject2;
            int i = 0;
            this.hasReplyMessage = messageObject2.replyMessageObject != null;
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.previousWidth = 0;
            int i2 = this.currentMessageObject.type;
            if (i2 == 18) {
                this.imageReceiver.setRoundRadius(0);
                String str = UserConfig.getInstance(this.currentAccount).premiumGiftsStickerPack;
                TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(str);
                if (stickerSetByName == null) {
                    stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(str);
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSetByName;
                if (tLRPC$TL_messages_stickerSet != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i3 = messageObject2.messageOwner.action.months; i3 > 0; i3 /= 10) {
                        sb.insert(0, (i3 % 10) + "⃣");
                    }
                    String sb2 = sb.toString();
                    Iterator<TLRPC$TL_stickerPack> it = tLRPC$TL_messages_stickerSet.packs.iterator();
                    tLRPC$Document = null;
                    while (it.hasNext()) {
                        TLRPC$TL_stickerPack next = it.next();
                        if (ObjectsCompat$$ExternalSyntheticBackport0.m(next.emoticon, sb2)) {
                            Iterator<Long> it2 = next.documents.iterator();
                            while (it2.hasNext()) {
                                long longValue = it2.next().longValue();
                                Iterator<TLRPC$Document> it3 = tLRPC$TL_messages_stickerSet.documents.iterator();
                                while (true) {
                                    if (!it3.hasNext()) {
                                        break;
                                    }
                                    TLRPC$Document next2 = it3.next();
                                    if (next2.id == longValue) {
                                        tLRPC$Document = next2;
                                        continue;
                                        break;
                                    }
                                }
                                if (tLRPC$Document != null) {
                                    continue;
                                    break;
                                }
                            }
                            continue;
                        }
                        if (tLRPC$Document != null) {
                            break;
                        }
                    }
                    if (tLRPC$Document == null && !tLRPC$TL_messages_stickerSet.documents.isEmpty()) {
                        tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(0);
                    }
                } else {
                    tLRPC$Document = null;
                }
                this.giftSticker = tLRPC$Document;
                if (tLRPC$Document != null) {
                    this.imageReceiver.setAllowStartLottieAnimation(false);
                    this.imageReceiver.setDelegate(this.giftStickerDelegate);
                    this.giftEffectAnimation = null;
                    int i4 = 0;
                    while (true) {
                        if (i4 >= tLRPC$Document.video_thumbs.size()) {
                            break;
                        } else if ("f".equals(tLRPC$Document.video_thumbs.get(i4).type)) {
                            this.giftEffectAnimation = tLRPC$Document.video_thumbs.get(i4);
                            break;
                        } else {
                            i4++;
                        }
                    }
                    SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document.thumbs, "emptyListPlaceholder", 0.2f);
                    if (svgThumb != null) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                    this.imageReceiver.setAutoRepeat(0);
                    this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), messageObject.getId() + "_130_130", svgThumb, "tgs", tLRPC$TL_messages_stickerSet, 1);
                }
            } else if (i2 == 11) {
                this.imageReceiver.setAllowStartLottieAnimation(true);
                this.imageReceiver.setDelegate((ImageReceiver.ImageReceiverDelegate) null);
                this.imageReceiver.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
                this.avatarDrawable.setInfo(messageObject.getDialogId(), (String) null, (String) null);
                MessageObject messageObject3 = this.currentMessageObject;
                if (messageObject3.messageOwner.action instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
                    this.imageReceiver.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, messageObject3, 0);
                } else {
                    int size = messageObject3.photoThumbs.size();
                    int i5 = 0;
                    while (true) {
                        if (i5 >= size) {
                            tLRPC$PhotoSize = null;
                            break;
                        }
                        tLRPC$PhotoSize = this.currentMessageObject.photoThumbs.get(i5);
                        if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
                            break;
                        }
                        i5++;
                    }
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, 640);
                    if (closestPhotoSizeWithSize != null) {
                        TLRPC$Photo tLRPC$Photo = messageObject2.messageOwner.action.photo;
                        if (!tLRPC$Photo.video_sizes.isEmpty() && SharedConfig.autoplayGifs) {
                            TLRPC$VideoSize tLRPC$VideoSize2 = tLRPC$Photo.video_sizes.get(0);
                            if (messageObject2.mediaExists || DownloadController.getInstance(this.currentAccount).canDownloadMedia(4, (long) tLRPC$VideoSize2.size)) {
                                tLRPC$VideoSize = tLRPC$VideoSize2;
                            } else {
                                this.currentVideoLocation = ImageLocation.getForPhoto(tLRPC$VideoSize2, tLRPC$Photo);
                                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$VideoSize2), this.currentMessageObject, this);
                            }
                        }
                        if (tLRPC$VideoSize != null) {
                            this.imageReceiver.setImage(ImageLocation.getForPhoto(tLRPC$VideoSize, tLRPC$Photo), "g", ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
                        } else {
                            this.imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, this.currentMessageObject.photoThumbsObject), "150_150", ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
                        }
                    } else {
                        this.imageReceiver.setImageBitmap((Drawable) this.avatarDrawable);
                    }
                }
                this.imageReceiver.setVisible(!PhotoViewer.isShowingImage(this.currentMessageObject), false);
            } else {
                this.imageReceiver.setAllowStartLottieAnimation(true);
                this.imageReceiver.setDelegate((ImageReceiver.ImageReceiverDelegate) null);
                this.imageReceiver.setImageBitmap((Bitmap) null);
            }
            View view = this.rippleView;
            if (messageObject2.type != 18) {
                i = 8;
            }
            view.setVisibility(i);
            requestLayout();
        }
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
        setStarsPaused(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
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

    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:92:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r10) {
        /*
            r9 = this;
            org.telegram.messenger.MessageObject r0 = r9.currentMessageObject
            if (r0 != 0) goto L_0x0009
            boolean r10 = super.onTouchEvent(r10)
            return r10
        L_0x0009:
            float r0 = r10.getX()
            r9.lastTouchX = r0
            float r1 = r10.getY()
            r9.lastTouchY = r1
            int r2 = r10.getAction()
            r3 = 18
            r4 = 1
            r5 = 0
            if (r2 != 0) goto L_0x0057
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r2 = r9.delegate
            if (r2 == 0) goto L_0x00ce
            org.telegram.messenger.MessageObject r2 = r9.currentMessageObject
            int r2 = r2.type
            r6 = 11
            if (r2 == r6) goto L_0x002d
            if (r2 != r3) goto L_0x0039
        L_0x002d:
            org.telegram.messenger.ImageReceiver r2 = r9.imageReceiver
            boolean r2 = r2.isInsideImage(r0, r1)
            if (r2 == 0) goto L_0x0039
            r9.imagePressed = r4
            r2 = 1
            goto L_0x003a
        L_0x0039:
            r2 = 0
        L_0x003a:
            org.telegram.messenger.MessageObject r6 = r9.currentMessageObject
            int r6 = r6.type
            if (r6 != r3) goto L_0x0050
            android.graphics.RectF r3 = r9.giftButtonRect
            boolean r3 = r3.contains(r0, r1)
            if (r3 == 0) goto L_0x0050
            android.view.View r2 = r9.rippleView
            r9.giftButtonPressed = r4
            r2.setPressed(r4)
            r2 = 1
        L_0x0050:
            if (r2 == 0) goto L_0x00cf
            r9.startCheckLongPress()
            goto L_0x00cf
        L_0x0057:
            int r2 = r10.getAction()
            r6 = 2
            if (r2 == r6) goto L_0x0061
            r9.cancelCheckLongPress()
        L_0x0061:
            boolean r2 = r9.imagePressed
            r7 = 3
            if (r2 == 0) goto L_0x0096
            int r2 = r10.getAction()
            if (r2 == r4) goto L_0x007f
            if (r2 == r6) goto L_0x0074
            if (r2 == r7) goto L_0x0071
            goto L_0x00ce
        L_0x0071:
            r9.imagePressed = r5
            goto L_0x00ce
        L_0x0074:
            org.telegram.messenger.ImageReceiver r2 = r9.imageReceiver
            boolean r2 = r2.isInsideImage(r0, r1)
            if (r2 != 0) goto L_0x00ce
            r9.imagePressed = r5
            goto L_0x00ce
        L_0x007f:
            r9.imagePressed = r5
            org.telegram.messenger.MessageObject r2 = r9.currentMessageObject
            int r2 = r2.type
            if (r2 != r3) goto L_0x008b
            r9.openGift()
            goto L_0x00ce
        L_0x008b:
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r2 = r9.delegate
            if (r2 == 0) goto L_0x00ce
            r2.didClickImage(r9)
            r9.playSoundEffect(r5)
            goto L_0x00ce
        L_0x0096:
            boolean r2 = r9.giftButtonPressed
            if (r2 == 0) goto L_0x00ce
            int r2 = r10.getAction()
            if (r2 == r4) goto L_0x00bd
            if (r2 == r6) goto L_0x00ad
            if (r2 == r7) goto L_0x00a5
            goto L_0x00ce
        L_0x00a5:
            android.view.View r2 = r9.rippleView
            r9.giftButtonPressed = r5
            r2.setPressed(r5)
            goto L_0x00ce
        L_0x00ad:
            android.graphics.RectF r2 = r9.giftButtonRect
            boolean r2 = r2.contains(r0, r1)
            if (r2 != 0) goto L_0x00ce
            android.view.View r2 = r9.rippleView
            r9.giftButtonPressed = r5
            r2.setPressed(r5)
            goto L_0x00ce
        L_0x00bd:
            android.view.View r2 = r9.rippleView
            r9.giftButtonPressed = r5
            r2.setPressed(r5)
            org.telegram.ui.Cells.ChatActionCell$ChatActionCellDelegate r2 = r9.delegate
            if (r2 == 0) goto L_0x00ce
            r9.playSoundEffect(r5)
            r9.openGift()
        L_0x00ce:
            r2 = 0
        L_0x00cf:
            if (r2 != 0) goto L_0x015c
            int r3 = r10.getAction()
            if (r3 == 0) goto L_0x00e1
            android.text.style.URLSpan r3 = r9.pressedLink
            if (r3 == 0) goto L_0x015c
            int r3 = r10.getAction()
            if (r3 != r4) goto L_0x015c
        L_0x00e1:
            int r3 = r9.textX
            float r6 = (float) r3
            r7 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 < 0) goto L_0x015a
            int r6 = r9.textY
            float r8 = (float) r6
            int r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x015a
            int r8 = r9.textWidth
            int r3 = r3 + r8
            float r3 = (float) r3
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x015a
            int r3 = r9.textHeight
            int r3 = r3 + r6
            float r3 = (float) r3
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x015a
            float r3 = (float) r6
            float r1 = r1 - r3
            int r3 = r9.textXLeft
            float r3 = (float) r3
            float r0 = r0 - r3
            android.text.StaticLayout r3 = r9.textLayout
            int r1 = (int) r1
            int r1 = r3.getLineForVertical(r1)
            android.text.StaticLayout r3 = r9.textLayout
            int r3 = r3.getOffsetForHorizontal(r1, r0)
            android.text.StaticLayout r6 = r9.textLayout
            float r6 = r6.getLineLeft(r1)
            int r8 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r8 > 0) goto L_0x0157
            android.text.StaticLayout r8 = r9.textLayout
            float r1 = r8.getLineWidth(r1)
            float r6 = r6 + r1
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x0157
            org.telegram.messenger.MessageObject r0 = r9.currentMessageObject
            java.lang.CharSequence r0 = r0.messageText
            boolean r1 = r0 instanceof android.text.Spannable
            if (r1 == 0) goto L_0x0157
            android.text.Spannable r0 = (android.text.Spannable) r0
            java.lang.Class<android.text.style.URLSpan> r1 = android.text.style.URLSpan.class
            java.lang.Object[] r0 = r0.getSpans(r3, r3, r1)
            android.text.style.URLSpan[] r0 = (android.text.style.URLSpan[]) r0
            int r1 = r0.length
            if (r1 == 0) goto L_0x0152
            int r1 = r10.getAction()
            if (r1 != 0) goto L_0x0148
            r0 = r0[r5]
            r9.pressedLink = r0
            goto L_0x0155
        L_0x0148:
            r0 = r0[r5]
            android.text.style.URLSpan r1 = r9.pressedLink
            if (r0 != r1) goto L_0x0154
            r9.openLink(r1)
            goto L_0x0155
        L_0x0152:
            r9.pressedLink = r7
        L_0x0154:
            r4 = r2
        L_0x0155:
            r2 = r4
            goto L_0x015c
        L_0x0157:
            r9.pressedLink = r7
            goto L_0x015c
        L_0x015a:
            r9.pressedLink = r7
        L_0x015c:
            if (r2 != 0) goto L_0x0162
            boolean r2 = super.onTouchEvent(r10)
        L_0x0162:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void openGift() {
        if (this.imageReceiver.getLottieAnimation() == null || !this.imageReceiver.getLottieAnimation().isRunning()) {
            openPremiumGiftPreview();
        }
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
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null) {
            int i5 = messageObject2.type;
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
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null && messageObject3.type == 18) {
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
                charSequence = messageObject.messageText;
            } else if (tLRPC$MessageMedia.photo instanceof TLRPC$TL_photoEmpty) {
                charSequence = LocaleController.getString("AttachPhotoExpired", NUM);
            } else if (tLRPC$MessageMedia.document instanceof TLRPC$TL_documentEmpty) {
                charSequence = LocaleController.getString("AttachVideoExpired", NUM);
            } else {
                charSequence = messageObject.messageText;
            }
        } else {
            charSequence = this.customText;
        }
        createLayout(charSequence, this.previousWidth);
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null) {
            int i = messageObject2.type;
            if (i == 11) {
                int i2 = AndroidUtilities.roundMessageSize;
                this.imageReceiver.setImageCoords(((float) (this.previousWidth - AndroidUtilities.roundMessageSize)) / 2.0f, (float) (this.textHeight + AndroidUtilities.dp(19.0f)), (float) i2, (float) i2);
            } else if (i == 18) {
                createGiftPremiumLayouts(LocaleController.getString(NUM), LocaleController.formatString(NUM, LocaleController.formatPluralString("Months", this.currentMessageObject.messageOwner.action.months, new Object[0])), LocaleController.getString(NUM), this.giftRectSize);
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
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && ((i = messageObject2.type) == 11 || i == 18)) {
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
                this.textLayout.draw(canvas);
                canvas.restore();
                for (SpoilerEffect next : this.spoilers) {
                    next.setColor(this.textLayout.getPaint().getColor());
                    next.draw(canvas);
                }
                canvas.restore();
            }
            MessageObject messageObject3 = this.currentMessageObject;
            if (messageObject3 != null && messageObject3.type == 18) {
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
                TLRPC$PhotoSize tLRPC$PhotoSize2 = this.currentMessageObject.photoThumbs.get(i);
                if (tLRPC$PhotoSize2 instanceof TLRPC$TL_photoStrippedSize) {
                    tLRPC$PhotoSize = tLRPC$PhotoSize2;
                    break;
                }
                i++;
            }
            this.imageReceiver.setImage(this.currentVideoLocation, "g", ImageLocation.getForObject(tLRPC$PhotoSize, this.currentMessageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0, (String) null, this.currentMessageObject, 1);
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (!TextUtils.isEmpty(this.customText) || this.currentMessageObject != null) {
            if (this.accessibilityText == null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(!TextUtils.isEmpty(this.customText) ? this.customText : this.currentMessageObject.messageText);
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
