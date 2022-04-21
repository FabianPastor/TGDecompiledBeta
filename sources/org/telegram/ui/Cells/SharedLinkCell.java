package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class SharedLinkCell extends FrameLayout {
    private static final int SPOILER_TYPE_DESCRIPTION = 1;
    private static final int SPOILER_TYPE_DESCRIPTION2 = 2;
    private static final int SPOILER_TYPE_LINK = 0;
    public static final int VIEW_TYPE_DEFAULT = 0;
    public static final int VIEW_TYPE_GLOBAL_SEARCH = 1;
    private StaticLayout captionLayout;
    private TextPaint captionTextPaint;
    private int captionY;
    private CheckBox2 checkBox;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress;
    private StaticLayout dateLayout;
    private int dateLayoutX;
    /* access modifiers changed from: private */
    public SharedLinkCellDelegate delegate;
    private TextPaint description2TextPaint;
    private int description2Y;
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private List<SpoilerEffect> descriptionLayout2Spoilers;
    private List<SpoilerEffect> descriptionLayoutSpoilers;
    private TextPaint descriptionTextPaint;
    private int descriptionY;
    private boolean drawLinkImageView;
    private StaticLayout fromInfoLayout;
    private int fromInfoLayoutY;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout;
    private boolean linkPreviewPressed;
    private SparseArray<List<SpoilerEffect>> linkSpoilers;
    private int linkY;
    ArrayList<CharSequence> links;
    private MessageObject message;
    private boolean needDivider;
    private AtomicReference<Layout> patchedDescriptionLayout;
    private AtomicReference<Layout> patchedDescriptionLayout2;
    private Path path;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress;
    private CheckForTap pendingCheckForTap;
    /* access modifiers changed from: private */
    public int pressCount;
    /* access modifiers changed from: private */
    public int pressedLink;
    private SpoilerEffect spoilerPressed;
    private int spoilerTypePressed;
    private Stack<SpoilerEffect> spoilersPool;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY;
    private LinkPath urlPath;
    private int viewType;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC.WebPage webPage, MessageObject messageObject);

        void onLinkPress(String str, boolean z);
    }

    static /* synthetic */ int access$104(SharedLinkCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                CheckForLongPress unused = SharedLinkCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell sharedLinkCell = SharedLinkCell.this;
            sharedLinkCell.postDelayed(sharedLinkCell.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                boolean unused = SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCell.this.delegate.onLinkPress(SharedLinkCell.this.links.get(SharedLinkCell.this.pressedLink).toString(), true);
                }
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(event);
                event.recycle();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }

    public SharedLinkCell(Context context) {
        this(context, 0);
    }

    public SharedLinkCell(Context context, int viewType2) {
        super(context);
        this.checkingForLongPress = false;
        this.pendingCheckForLongPress = null;
        this.pressCount = 0;
        this.pendingCheckForTap = null;
        this.links = new ArrayList<>();
        this.linkLayout = new ArrayList<>();
        this.linkSpoilers = new SparseArray<>();
        this.descriptionLayoutSpoilers = new ArrayList();
        this.descriptionLayout2Spoilers = new ArrayList();
        this.spoilersPool = new Stack<>();
        this.path = new Path();
        this.spoilerTypePressed = -1;
        this.titleY = AndroidUtilities.dp(10.0f);
        this.descriptionY = AndroidUtilities.dp(30.0f);
        this.patchedDescriptionLayout = new AtomicReference<>();
        this.description2Y = AndroidUtilities.dp(30.0f);
        this.patchedDescriptionLayout2 = new AtomicReference<>();
        this.captionY = AndroidUtilities.dp(30.0f);
        this.fromInfoLayoutY = AndroidUtilities.dp(30.0f);
        this.viewType = viewType2;
        setFocusable(true);
        LinkPath linkPath = new LinkPath();
        this.urlPath = linkPath;
        linkPath.setUseRoundRect(true);
        TextPaint textPaint = new TextPaint(1);
        this.titleTextPaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
        if (viewType2 == 1) {
            TextPaint textPaint2 = new TextPaint(1);
            this.description2TextPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
        }
        TextPaint textPaint3 = new TextPaint(1);
        this.captionTextPaint = textPaint3;
        textPaint3.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x028f A[Catch:{ Exception -> 0x02fe }] */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0304  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x012f A[Catch:{ Exception -> 0x0313 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01a1 A[Catch:{ Exception -> 0x0313 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b4 A[Catch:{ Exception -> 0x0313 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01ce A[Catch:{ Exception -> 0x0310 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01d5 A[Catch:{ Exception -> 0x0310 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0232  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r38, int r39) {
        /*
            r37 = this;
            r1 = r37
            r2 = 0
            r1.drawLinkImageView = r2
            r3 = 0
            r1.descriptionLayout = r3
            r1.titleLayout = r3
            r1.descriptionLayout2 = r3
            r1.captionLayout = r3
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout
            r0.clear()
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            r0.clear()
            int r0 = android.view.View.MeasureSpec.getSize(r38)
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r0 - r5
            r0 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            r14 = 1
            if (r10 == 0) goto L_0x007b
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            org.telegram.tgnet.TLRPC$WebPage r10 = r10.webpage
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_webPage
            if (r10 == 0) goto L_0x007b
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            org.telegram.tgnet.TLRPC$WebPage r10 = r10.webpage
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.photoThumbs
            if (r11 != 0) goto L_0x005f
            org.telegram.tgnet.TLRPC$Photo r11 = r10.photo
            if (r11 == 0) goto L_0x005f
            org.telegram.messenger.MessageObject r11 = r1.message
            r11.generateThumbs(r14)
        L_0x005f:
            org.telegram.tgnet.TLRPC$Photo r11 = r10.photo
            if (r11 == 0) goto L_0x006b
            org.telegram.messenger.MessageObject r11 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.photoThumbs
            if (r11 == 0) goto L_0x006b
            r11 = 1
            goto L_0x006c
        L_0x006b:
            r11 = 0
        L_0x006c:
            r9 = r11
            java.lang.String r0 = r10.title
            if (r0 != 0) goto L_0x0073
            java.lang.String r0 = r10.site_name
        L_0x0073:
            java.lang.String r6 = r10.description
            java.lang.String r8 = r10.url
            r15 = r8
            r16 = r9
            goto L_0x007e
        L_0x007b:
            r15 = r8
            r16 = r9
        L_0x007e:
            org.telegram.messenger.MessageObject r8 = r1.message
            if (r8 == 0) goto L_0x0325
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x0325
            r8 = 0
            r36 = r6
            r6 = r0
            r0 = r7
            r7 = r36
        L_0x0093:
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r9.entities
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x0321
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r9.entities
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$MessageEntity r9 = (org.telegram.tgnet.TLRPC.MessageEntity) r9
            int r10 = r9.length
            if (r10 <= 0) goto L_0x0318
            int r10 = r9.offset
            if (r10 < 0) goto L_0x0318
            int r10 = r9.offset
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 < r11) goto L_0x00c3
            goto L_0x0318
        L_0x00c3:
            int r10 = r9.offset
            int r11 = r9.length
            int r10 = r10 + r11
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 <= r11) goto L_0x00e3
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            int r10 = r10.length()
            int r11 = r9.offset
            int r10 = r10 - r11
            r9.length = r10
        L_0x00e3:
            if (r8 != 0) goto L_0x0129
            if (r15 == 0) goto L_0x0129
            int r10 = r9.offset
            if (r10 != 0) goto L_0x00f9
            int r10 = r9.length
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 == r11) goto L_0x0129
        L_0x00f9:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            int r10 = r10.size()
            if (r10 != r14) goto L_0x0118
            if (r7 != 0) goto L_0x0129
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r10)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r11, r10)
            r0 = r10
            goto L_0x012a
        L_0x0118:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r10)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r11, r10)
            r0 = r10
            goto L_0x012a
        L_0x0129:
            r10 = r0
        L_0x012a:
            r0 = 0
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl     // Catch:{ Exception -> 0x0313 }
            if (r11 != 0) goto L_0x019d
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl     // Catch:{ Exception -> 0x0313 }
            if (r11 == 0) goto L_0x0134
            goto L_0x019d
        L_0x0134:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityEmail     // Catch:{ Exception -> 0x0313 }
            if (r11 == 0) goto L_0x0230
            if (r6 == 0) goto L_0x0140
            int r11 = r6.length()     // Catch:{ Exception -> 0x0313 }
            if (r11 != 0) goto L_0x0230
        L_0x0140:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0313 }
            r11.<init>()     // Catch:{ Exception -> 0x0313 }
            java.lang.String r12 = "mailto:"
            r11.append(r12)     // Catch:{ Exception -> 0x0313 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x0313 }
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner     // Catch:{ Exception -> 0x0313 }
            java.lang.String r12 = r12.message     // Catch:{ Exception -> 0x0313 }
            int r13 = r9.offset     // Catch:{ Exception -> 0x0313 }
            int r3 = r9.offset     // Catch:{ Exception -> 0x0313 }
            int r4 = r9.length     // Catch:{ Exception -> 0x0313 }
            int r3 = r3 + r4
            java.lang.String r3 = r12.substring(r13, r3)     // Catch:{ Exception -> 0x0313 }
            r11.append(r3)     // Catch:{ Exception -> 0x0313 }
            java.lang.String r3 = r11.toString()     // Catch:{ Exception -> 0x0313 }
            r0 = r3
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x0313 }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x0313 }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x0313 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x0313 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x0313 }
            int r12 = r9.length     // Catch:{ Exception -> 0x0313 }
            int r11 = r11 + r12
            java.lang.String r3 = r3.substring(r4, r11)     // Catch:{ Exception -> 0x0313 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x0310 }
            if (r4 != 0) goto L_0x018a
            int r4 = r9.length     // Catch:{ Exception -> 0x0310 }
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x0310 }
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner     // Catch:{ Exception -> 0x0310 }
            java.lang.String r6 = r6.message     // Catch:{ Exception -> 0x0310 }
            int r6 = r6.length()     // Catch:{ Exception -> 0x0310 }
            if (r4 == r6) goto L_0x0187
            goto L_0x018a
        L_0x0187:
            r6 = r3
            goto L_0x0230
        L_0x018a:
            org.telegram.messenger.MessageObject r4 = r1.message     // Catch:{ Exception -> 0x0310 }
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner     // Catch:{ Exception -> 0x0310 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0310 }
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r4)     // Catch:{ Exception -> 0x0310 }
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x0310 }
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r6, r4)     // Catch:{ Exception -> 0x0310 }
            r7 = r4
            r6 = r3
            goto L_0x0230
        L_0x019d:
            boolean r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl     // Catch:{ Exception -> 0x0313 }
            if (r3 == 0) goto L_0x01b4
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x0313 }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x0313 }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x0313 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x0313 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x0313 }
            int r12 = r9.length     // Catch:{ Exception -> 0x0313 }
            int r11 = r11 + r12
            java.lang.String r3 = r3.substring(r4, r11)     // Catch:{ Exception -> 0x0313 }
            r0 = r3
            goto L_0x01b7
        L_0x01b4:
            java.lang.String r3 = r9.url     // Catch:{ Exception -> 0x0313 }
            r0 = r3
        L_0x01b7:
            if (r6 == 0) goto L_0x01bf
            int r3 = r6.length()     // Catch:{ Exception -> 0x0313 }
            if (r3 != 0) goto L_0x0230
        L_0x01bf:
            java.lang.String r3 = r0.toString()     // Catch:{ Exception -> 0x0313 }
            android.net.Uri r4 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0310 }
            java.lang.String r6 = r4.getHost()     // Catch:{ Exception -> 0x0310 }
            r3 = r6
            if (r3 != 0) goto L_0x01d3
            java.lang.String r6 = r0.toString()     // Catch:{ Exception -> 0x0310 }
            r3 = r6
        L_0x01d3:
            if (r3 == 0) goto L_0x020d
            r6 = 46
            int r11 = r3.lastIndexOf(r6)     // Catch:{ Exception -> 0x0310 }
            r12 = r11
            if (r11 < 0) goto L_0x020d
            java.lang.String r11 = r3.substring(r2, r12)     // Catch:{ Exception -> 0x0310 }
            r3 = r11
            int r6 = r3.lastIndexOf(r6)     // Catch:{ Exception -> 0x0310 }
            r11 = r6
            if (r6 < 0) goto L_0x01f1
            int r6 = r11 + 1
            java.lang.String r6 = r3.substring(r6)     // Catch:{ Exception -> 0x0310 }
            r3 = r6
        L_0x01f1:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0310 }
            r6.<init>()     // Catch:{ Exception -> 0x0310 }
            java.lang.String r12 = r3.substring(r2, r14)     // Catch:{ Exception -> 0x0310 }
            java.lang.String r12 = r12.toUpperCase()     // Catch:{ Exception -> 0x0310 }
            r6.append(r12)     // Catch:{ Exception -> 0x0310 }
            java.lang.String r12 = r3.substring(r14)     // Catch:{ Exception -> 0x0310 }
            r6.append(r12)     // Catch:{ Exception -> 0x0310 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0310 }
            r3 = r6
        L_0x020d:
            int r6 = r9.offset     // Catch:{ Exception -> 0x0310 }
            if (r6 != 0) goto L_0x021f
            int r6 = r9.length     // Catch:{ Exception -> 0x0310 }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x0310 }
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner     // Catch:{ Exception -> 0x0310 }
            java.lang.String r11 = r11.message     // Catch:{ Exception -> 0x0310 }
            int r11 = r11.length()     // Catch:{ Exception -> 0x0310 }
            if (r6 == r11) goto L_0x022f
        L_0x021f:
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x0310 }
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner     // Catch:{ Exception -> 0x0310 }
            java.lang.String r6 = r6.message     // Catch:{ Exception -> 0x0310 }
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r6)     // Catch:{ Exception -> 0x0310 }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x0310 }
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r11, r6)     // Catch:{ Exception -> 0x0310 }
            r7 = r6
        L_0x022f:
            r6 = r3
        L_0x0230:
            if (r0 == 0) goto L_0x0304
            r3 = 0
            java.lang.String r4 = "://"
            boolean r4 = org.telegram.messenger.AndroidUtilities.charSequenceContains(r0, r4)     // Catch:{ Exception -> 0x02fe }
            if (r4 != 0) goto L_0x0272
            java.lang.String r4 = r0.toString()     // Catch:{ Exception -> 0x0313 }
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x0313 }
            java.lang.String r11 = "http"
            int r4 = r4.indexOf(r11)     // Catch:{ Exception -> 0x0313 }
            if (r4 == 0) goto L_0x0272
            java.lang.String r4 = r0.toString()     // Catch:{ Exception -> 0x0313 }
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x0313 }
            java.lang.String r11 = "mailto"
            int r4 = r4.indexOf(r11)     // Catch:{ Exception -> 0x0313 }
            if (r4 == 0) goto L_0x0272
            java.lang.String r4 = "http://"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0313 }
            r11.<init>()     // Catch:{ Exception -> 0x0313 }
            r11.append(r4)     // Catch:{ Exception -> 0x0313 }
            r11.append(r0)     // Catch:{ Exception -> 0x0313 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0313 }
            int r12 = r4.length()     // Catch:{ Exception -> 0x0313 }
            int r3 = r3 + r12
            goto L_0x0274
        L_0x0272:
            r4 = r0
            r11 = r4
        L_0x0274:
            android.text.SpannableString r4 = android.text.SpannableString.valueOf(r11)     // Catch:{ Exception -> 0x02fe }
            int r12 = r9.offset     // Catch:{ Exception -> 0x02fe }
            int r13 = r9.offset     // Catch:{ Exception -> 0x02fe }
            int r2 = r9.length     // Catch:{ Exception -> 0x02fe }
            int r13 = r13 + r2
            org.telegram.messenger.MessageObject r2 = r1.message     // Catch:{ Exception -> 0x02fe }
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner     // Catch:{ Exception -> 0x02fe }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities     // Catch:{ Exception -> 0x02fe }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Exception -> 0x02fe }
        L_0x0289:
            boolean r19 = r2.hasNext()     // Catch:{ Exception -> 0x02fe }
            if (r19 == 0) goto L_0x02ec
            java.lang.Object r19 = r2.next()     // Catch:{ Exception -> 0x02fe }
            org.telegram.tgnet.TLRPC$MessageEntity r19 = (org.telegram.tgnet.TLRPC.MessageEntity) r19     // Catch:{ Exception -> 0x02fe }
            r20 = r19
            r14 = r20
            r20 = r0
            int r0 = r14.offset     // Catch:{ Exception -> 0x02fe }
            r21 = r2
            int r2 = r14.offset     // Catch:{ Exception -> 0x02fe }
            r22 = r6
            int r6 = r14.length     // Catch:{ Exception -> 0x02e6 }
            int r2 = r2 + r6
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntitySpoiler     // Catch:{ Exception -> 0x02e6 }
            if (r6 == 0) goto L_0x02d6
            if (r12 > r2) goto L_0x02d6
            if (r13 < r0) goto L_0x02d6
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r6 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun     // Catch:{ Exception -> 0x02e6 }
            r6.<init>()     // Catch:{ Exception -> 0x02e6 }
            r23 = r7
            int r7 = r6.flags     // Catch:{ Exception -> 0x02f8 }
            r7 = r7 | 256(0x100, float:3.59E-43)
            r6.flags = r7     // Catch:{ Exception -> 0x02f8 }
            org.telegram.ui.Components.TextStyleSpan r7 = new org.telegram.ui.Components.TextStyleSpan     // Catch:{ Exception -> 0x02f8 }
            r7.<init>(r6)     // Catch:{ Exception -> 0x02f8 }
            r24 = r6
            int r6 = java.lang.Math.max(r12, r0)     // Catch:{ Exception -> 0x02f8 }
            int r25 = java.lang.Math.min(r13, r2)     // Catch:{ Exception -> 0x02f8 }
            r26 = r0
            int r0 = r25 + r3
            r25 = r2
            r2 = 33
            r4.setSpan(r7, r6, r0, r2)     // Catch:{ Exception -> 0x02f8 }
            goto L_0x02dc
        L_0x02d6:
            r26 = r0
            r25 = r2
            r23 = r7
        L_0x02dc:
            r0 = r20
            r2 = r21
            r6 = r22
            r7 = r23
            r14 = 1
            goto L_0x0289
        L_0x02e6:
            r0 = move-exception
            r23 = r7
            r6 = r22
            goto L_0x0314
        L_0x02ec:
            r20 = r0
            r22 = r6
            r23 = r7
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links     // Catch:{ Exception -> 0x02f8 }
            r0.add(r4)     // Catch:{ Exception -> 0x02f8 }
            goto L_0x030a
        L_0x02f8:
            r0 = move-exception
            r6 = r22
            r7 = r23
            goto L_0x0314
        L_0x02fe:
            r0 = move-exception
            r22 = r6
            r23 = r7
            goto L_0x0314
        L_0x0304:
            r20 = r0
            r22 = r6
            r23 = r7
        L_0x030a:
            r0 = r10
            r6 = r22
            r7 = r23
            goto L_0x0318
        L_0x0310:
            r0 = move-exception
            r6 = r3
            goto L_0x0314
        L_0x0313:
            r0 = move-exception
        L_0x0314:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r10
        L_0x0318:
            int r8 = r8 + 1
            r2 = 0
            r3 = 0
            r4 = 1090519040(0x41000000, float:8.0)
            r14 = 1
            goto L_0x0093
        L_0x0321:
            r2 = r6
            r6 = r7
            r7 = r0
            goto L_0x0326
        L_0x0325:
            r2 = r0
        L_0x0326:
            if (r15 == 0) goto L_0x0335
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0335
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            r0.add(r15)
        L_0x0335:
            r0 = 0
            int r3 = r1.viewType
            r4 = 1
            if (r3 != r4) goto L_0x0374
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            int r3 = r3.date
            long r3 = (long) r3
            java.lang.String r3 = org.telegram.messenger.LocaleController.stringForMessageListDate(r3)
            android.text.TextPaint r4 = r1.description2TextPaint
            float r4 = r4.measureText(r3)
            double r8 = (double) r4
            double r8 = java.lang.Math.ceil(r8)
            int r4 = (int) r8
            android.text.TextPaint r9 = r1.description2TextPaint
            r12 = 0
            r13 = 1
            r8 = r3
            r10 = r4
            r11 = r4
            android.text.StaticLayout r8 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r8, r9, r10, r11, r12, r13)
            r1.dateLayout = r8
            int r8 = r5 - r4
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r8 - r9
            r1.dateLayoutX = r8
            r8 = 1094713344(0x41400000, float:12.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r0 = r4 + r8
            r3 = r0
            goto L_0x0375
        L_0x0374:
            r3 = r0
        L_0x0375:
            r4 = 1082130432(0x40800000, float:4.0)
            if (r2 == 0) goto L_0x03c7
            r0 = r2
            org.telegram.messenger.MessageObject r8 = r1.message     // Catch:{ Exception -> 0x03be }
            java.util.ArrayList<java.lang.String> r8 = r8.highlightedWords     // Catch:{ Exception -> 0x03be }
            r9 = 0
            java.lang.CharSequence r8 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r8, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r9)     // Catch:{ Exception -> 0x03be }
            if (r8 == 0) goto L_0x0386
            r0 = r8
        L_0x0386:
            android.text.TextPaint r10 = r1.titleTextPaint     // Catch:{ Exception -> 0x03be }
            int r9 = r5 - r3
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03be }
            int r11 = r9 - r11
            int r9 = r5 - r3
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03be }
            int r12 = r9 - r12
            r13 = 0
            r14 = 3
            r9 = r0
            android.text.StaticLayout r9 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x03be }
            r1.titleLayout = r9     // Catch:{ Exception -> 0x03be }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x03be }
            if (r9 <= 0) goto L_0x03bd
            int r9 = r1.titleY     // Catch:{ Exception -> 0x03be }
            android.text.StaticLayout r10 = r1.titleLayout     // Catch:{ Exception -> 0x03be }
            int r11 = r10.getLineCount()     // Catch:{ Exception -> 0x03be }
            r12 = 1
            int r11 = r11 - r12
            int r10 = r10.getLineBottom(r11)     // Catch:{ Exception -> 0x03be }
            int r9 = r9 + r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x03be }
            int r9 = r9 + r10
            r1.descriptionY = r9     // Catch:{ Exception -> 0x03be }
        L_0x03bd:
            goto L_0x03c2
        L_0x03be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c2:
            org.telegram.ui.Components.LetterDrawable r0 = r1.letterDrawable
            r0.setTitle(r2)
        L_0x03c7:
            int r0 = r1.descriptionY
            r1.description2Y = r0
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x03d4
            int r0 = r0.getLineCount()
            goto L_0x03d5
        L_0x03d4:
            r0 = 0
        L_0x03d5:
            int r0 = 4 - r0
            r8 = 1
            int r14 = java.lang.Math.max(r8, r0)
            int r0 = r1.viewType
            if (r0 != r8) goto L_0x03e7
            r6 = 0
            r7 = 0
            r18 = r6
            r20 = r7
            goto L_0x03eb
        L_0x03e7:
            r18 = r6
            r20 = r7
        L_0x03eb:
            r21 = 1084227584(0x40a00000, float:5.0)
            if (r18 == 0) goto L_0x0439
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0435 }
            r10 = 0
            r6 = r18
            r8 = r5
            r9 = r5
            r11 = r14
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0435 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x0435 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0435 }
            if (r0 <= 0) goto L_0x0419
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x0435 }
            android.text.StaticLayout r6 = r1.descriptionLayout     // Catch:{ Exception -> 0x0435 }
            int r7 = r6.getLineCount()     // Catch:{ Exception -> 0x0435 }
            r8 = 1
            int r7 = r7 - r8
            int r6 = r6.getLineBottom(r7)     // Catch:{ Exception -> 0x0435 }
            int r0 = r0 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r21)     // Catch:{ Exception -> 0x0435 }
            int r0 = r0 + r6
            r1.description2Y = r0     // Catch:{ Exception -> 0x0435 }
        L_0x0419:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x0435 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.descriptionLayoutSpoilers     // Catch:{ Exception -> 0x0435 }
            r0.addAll(r6)     // Catch:{ Exception -> 0x0435 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.descriptionLayoutSpoilers     // Catch:{ Exception -> 0x0435 }
            r0.clear()     // Catch:{ Exception -> 0x0435 }
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x0435 }
            boolean r0 = r0.isSpoilersRevealed     // Catch:{ Exception -> 0x0435 }
            if (r0 != 0) goto L_0x0434
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x0435 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.spoilersPool     // Catch:{ Exception -> 0x0435 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r1.descriptionLayoutSpoilers     // Catch:{ Exception -> 0x0435 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r6, r7)     // Catch:{ Exception -> 0x0435 }
        L_0x0434:
            goto L_0x0439
        L_0x0435:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0439:
            r22 = 1092616192(0x41200000, float:10.0)
            if (r20 == 0) goto L_0x0478
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0474 }
            r10 = 0
            r6 = r20
            r8 = r5
            r9 = r5
            r11 = r14
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0474 }
            r1.descriptionLayout2 = r0     // Catch:{ Exception -> 0x0474 }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x0474 }
            if (r0 == 0) goto L_0x0458
            int r0 = r1.description2Y     // Catch:{ Exception -> 0x0474 }
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)     // Catch:{ Exception -> 0x0474 }
            int r0 = r0 + r6
            r1.description2Y = r0     // Catch:{ Exception -> 0x0474 }
        L_0x0458:
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.spoilersPool     // Catch:{ Exception -> 0x0474 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.descriptionLayout2Spoilers     // Catch:{ Exception -> 0x0474 }
            r0.addAll(r6)     // Catch:{ Exception -> 0x0474 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r1.descriptionLayout2Spoilers     // Catch:{ Exception -> 0x0474 }
            r0.clear()     // Catch:{ Exception -> 0x0474 }
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x0474 }
            boolean r0 = r0.isSpoilersRevealed     // Catch:{ Exception -> 0x0474 }
            if (r0 != 0) goto L_0x0473
            android.text.StaticLayout r0 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0474 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.spoilersPool     // Catch:{ Exception -> 0x0474 }
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r1.descriptionLayout2Spoilers     // Catch:{ Exception -> 0x0474 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r0, r6, r7)     // Catch:{ Exception -> 0x0474 }
        L_0x0473:
            goto L_0x0478
        L_0x0474:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0478:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x04f2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x04f2
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            java.lang.String r6 = "\n"
            java.lang.String r7 = " "
            java.lang.String r0 = r0.replace(r6, r7)
            java.lang.String r6 = " +"
            java.lang.String r0 = r0.replaceAll(r6, r7)
            java.lang.String r0 = r0.trim()
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            r7 = 1101004800(0x41a00000, float:20.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r6, r7, r8)
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<java.lang.String> r6 = r6.highlightedWords
            r7 = 0
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
            if (r6 == 0) goto L_0x04f2
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<java.lang.String> r7 = r7.highlightedWords
            java.lang.Object r7 = r7.get(r8)
            java.lang.String r7 = (java.lang.String) r7
            android.text.TextPaint r8 = r1.captionTextPaint
            r9 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r7 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r6, r7, r5, r8, r9)
            android.text.TextPaint r8 = r1.captionTextPaint
            float r9 = (float) r5
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r7, r8, r9, r10)
            android.text.StaticLayout r7 = new android.text.StaticLayout
            android.text.TextPaint r8 = r1.captionTextPaint
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r26 = r5 + r9
            android.text.Layout$Alignment r27 = android.text.Layout.Alignment.ALIGN_NORMAL
            r28 = 1065353216(0x3var_, float:1.0)
            r29 = 0
            r30 = 0
            r23 = r7
            r24 = r6
            r25 = r8
            r23.<init>(r24, r25, r26, r27, r28, r29, r30)
            r1.captionLayout = r7
        L_0x04f2:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 == 0) goto L_0x050e
            int r6 = r1.descriptionY
            r1.captionY = r6
            int r7 = r0.getLineCount()
            r8 = 1
            int r7 = r7 - r8
            int r0 = r0.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 + r7
            int r6 = r6 + r0
            r1.descriptionY = r6
            r1.description2Y = r6
        L_0x050e:
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x05ea
            r0 = 0
        L_0x0517:
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r6 = r1.linkSpoilers
            int r6 = r6.size()
            if (r0 >= r6) goto L_0x052f
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r6 = r1.spoilersPool
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r7 = r1.linkSpoilers
            java.lang.Object r7 = r7.get(r0)
            java.util.Collection r7 = (java.util.Collection) r7
            r6.addAll(r7)
            int r0 = r0 + 1
            goto L_0x0517
        L_0x052f:
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r0 = r1.linkSpoilers
            r0.clear()
            r0 = 0
            r13 = r0
        L_0x0536:
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links
            int r0 = r0.size()
            if (r13 >= r0) goto L_0x05e6
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.links     // Catch:{ Exception -> 0x05d7 }
            java.lang.Object r0 = r0.get(r13)     // Catch:{ Exception -> 0x05d7 }
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0     // Catch:{ Exception -> 0x05d7 }
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x05d7 }
            int r7 = r0.length()     // Catch:{ Exception -> 0x05d7 }
            r8 = 0
            float r6 = r6.measureText(r0, r8, r7)     // Catch:{ Exception -> 0x05d7 }
            double r6 = (double) r6     // Catch:{ Exception -> 0x05d7 }
            double r6 = java.lang.Math.ceil(r6)     // Catch:{ Exception -> 0x05d7 }
            int r12 = (int) r6     // Catch:{ Exception -> 0x05d7 }
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r0)     // Catch:{ Exception -> 0x05d7 }
            java.lang.CharSequence r6 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r6)     // Catch:{ Exception -> 0x05d7 }
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x05d7 }
            int r8 = java.lang.Math.min(r12, r5)     // Catch:{ Exception -> 0x05d7 }
            float r8 = (float) r8     // Catch:{ Exception -> 0x05d7 }
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x05d7 }
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r6, r7, r8, r9)     // Catch:{ Exception -> 0x05d7 }
            r11 = r6
            android.text.StaticLayout r17 = new android.text.StaticLayout     // Catch:{ Exception -> 0x05d7 }
            android.text.TextPaint r8 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x05d7 }
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x05d7 }
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            r6 = r17
            r7 = r11
            r9 = r5
            r4 = r11
            r11 = r23
            r23 = r12
            r12 = r24
            r24 = r2
            r2 = r13
            r13 = r25
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x05d5 }
            r6 = r17
            int r7 = r1.description2Y     // Catch:{ Exception -> 0x05d5 }
            r1.linkY = r7     // Catch:{ Exception -> 0x05d5 }
            android.text.StaticLayout r7 = r1.descriptionLayout2     // Catch:{ Exception -> 0x05d5 }
            if (r7 == 0) goto L_0x05b2
            int r7 = r7.getLineCount()     // Catch:{ Exception -> 0x05d5 }
            if (r7 == 0) goto L_0x05b2
            int r7 = r1.linkY     // Catch:{ Exception -> 0x05d5 }
            android.text.StaticLayout r8 = r1.descriptionLayout2     // Catch:{ Exception -> 0x05d5 }
            int r9 = r8.getLineCount()     // Catch:{ Exception -> 0x05d5 }
            r10 = 1
            int r9 = r9 - r10
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x05d5 }
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r21)     // Catch:{ Exception -> 0x05d5 }
            int r8 = r8 + r9
            int r7 = r7 + r8
            r1.linkY = r7     // Catch:{ Exception -> 0x05d5 }
        L_0x05b2:
            org.telegram.messenger.MessageObject r7 = r1.message     // Catch:{ Exception -> 0x05d5 }
            boolean r7 = r7.isSpoilersRevealed     // Catch:{ Exception -> 0x05d5 }
            if (r7 != 0) goto L_0x05ce
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x05d5 }
            r7.<init>()     // Catch:{ Exception -> 0x05d5 }
            boolean r8 = r4 instanceof android.text.Spannable     // Catch:{ Exception -> 0x05d5 }
            if (r8 == 0) goto L_0x05c9
            r8 = r4
            android.text.Spannable r8 = (android.text.Spannable) r8     // Catch:{ Exception -> 0x05d5 }
            java.util.Stack<org.telegram.ui.Components.spoilers.SpoilerEffect> r9 = r1.spoilersPool     // Catch:{ Exception -> 0x05d5 }
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r1, r6, r8, r9, r7)     // Catch:{ Exception -> 0x05d5 }
        L_0x05c9:
            android.util.SparseArray<java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect>> r8 = r1.linkSpoilers     // Catch:{ Exception -> 0x05d5 }
            r8.put(r2, r7)     // Catch:{ Exception -> 0x05d5 }
        L_0x05ce:
            java.util.ArrayList<android.text.StaticLayout> r7 = r1.linkLayout     // Catch:{ Exception -> 0x05d5 }
            r7.add(r6)     // Catch:{ Exception -> 0x05d5 }
            goto L_0x05de
        L_0x05d5:
            r0 = move-exception
            goto L_0x05db
        L_0x05d7:
            r0 = move-exception
            r24 = r2
            r2 = r13
        L_0x05db:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05de:
            int r13 = r2 + 1
            r2 = r24
            r4 = 1082130432(0x40800000, float:4.0)
            goto L_0x0536
        L_0x05e6:
            r24 = r2
            r2 = r13
            goto L_0x05ec
        L_0x05ea:
            r24 = r2
        L_0x05ec:
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0601
            int r2 = android.view.View.MeasureSpec.getSize(r38)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r2 = r2 - r4
            int r2 = r2 - r0
            goto L_0x0605
        L_0x0601:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0605:
            org.telegram.ui.Components.LetterDrawable r4 = r1.letterDrawable
            r6 = 1093664768(0x41300000, float:11.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r2 + r0
            r9 = 1115422720(0x427CLASSNAME, float:63.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setBounds(r2, r7, r8, r9)
            if (r16 == 0) goto L_0x06a0
            org.telegram.messenger.MessageObject r4 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.photoThumbs
            r7 = 1
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r0, r7)
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.photoThumbs
            r8 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            if (r7 != r4) goto L_0x0630
            r7 = 0
        L_0x0630:
            r8 = -1
            r4.size = r8
            if (r7 == 0) goto L_0x0637
            r7.size = r8
        L_0x0637:
            org.telegram.messenger.ImageReceiver r8 = r1.linkImageView
            float r9 = (float) r2
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r10 = (float) r0
            float r11 = (float) r0
            r8.setImageCoords(r9, r6, r10, r11)
            java.lang.String r6 = org.telegram.messenger.FileLoader.getAttachFileName(r4)
            java.util.Locale r8 = java.util.Locale.US
            r9 = 2
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r12 = 0
            r10[r12] = r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r13 = 1
            r10[r13] = r11
            java.lang.String r11 = "%d_%d"
            java.lang.String r8 = java.lang.String.format(r8, r11, r10)
            java.util.Locale r10 = java.util.Locale.US
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r9[r12] = r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r9[r13] = r11
            java.lang.String r11 = "%d_%d_b"
            java.lang.String r9 = java.lang.String.format(r10, r11, r9)
            org.telegram.messenger.ImageReceiver r10 = r1.linkImageView
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLObject r11 = r11.photoThumbsObject
            org.telegram.messenger.ImageLocation r28 = org.telegram.messenger.ImageLocation.getForObject(r4, r11)
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLObject r11 = r11.photoThumbsObject
            org.telegram.messenger.ImageLocation r30 = org.telegram.messenger.ImageLocation.getForObject(r7, r11)
            r32 = 0
            r33 = 0
            org.telegram.messenger.MessageObject r11 = r1.message
            r35 = 0
            r27 = r10
            r29 = r8
            r31 = r9
            r34 = r11
            r27.setImage(r28, r29, r30, r31, r32, r33, r34, r35)
            r10 = 1
            r1.drawLinkImageView = r10
            goto L_0x06a1
        L_0x06a0:
            r10 = 1
        L_0x06a1:
            int r4 = r1.viewType
            if (r4 != r10) goto L_0x06b7
            org.telegram.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r6 = org.telegram.ui.FilteredSearchView.createFromInfoString(r4)
            android.text.TextPaint r7 = r1.description2TextPaint
            r10 = 0
            r8 = r5
            r9 = r5
            r11 = r14
            android.text.StaticLayout r4 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)
            r1.fromInfoLayout = r4
        L_0x06b7:
            r4 = 0
            android.text.StaticLayout r6 = r1.titleLayout
            if (r6 == 0) goto L_0x06d6
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x06d6
            android.text.StaticLayout r6 = r1.titleLayout
            int r7 = r6.getLineCount()
            r8 = 1
            int r7 = r7 - r8
            int r6 = r6.getLineBottom(r7)
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r7
            int r4 = r4 + r6
        L_0x06d6:
            android.text.StaticLayout r6 = r1.captionLayout
            if (r6 == 0) goto L_0x06f2
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x06f2
            android.text.StaticLayout r6 = r1.captionLayout
            int r7 = r6.getLineCount()
            r8 = 1
            int r7 = r7 - r8
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r7
            int r4 = r4 + r6
        L_0x06f2:
            android.text.StaticLayout r6 = r1.descriptionLayout
            if (r6 == 0) goto L_0x070e
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x070e
            android.text.StaticLayout r6 = r1.descriptionLayout
            int r7 = r6.getLineCount()
            r8 = 1
            int r7 = r7 - r8
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r7
            int r4 = r4 + r6
        L_0x070e:
            android.text.StaticLayout r6 = r1.descriptionLayout2
            if (r6 == 0) goto L_0x0733
            int r6 = r6.getLineCount()
            if (r6 == 0) goto L_0x0733
            android.text.StaticLayout r6 = r1.descriptionLayout2
            int r7 = r6.getLineCount()
            r8 = 1
            int r7 = r7 - r8
            int r6 = r6.getLineBottom(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r6 = r6 + r7
            int r4 = r4 + r6
            android.text.StaticLayout r6 = r1.descriptionLayout
            if (r6 == 0) goto L_0x0733
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)
            int r4 = r4 + r6
        L_0x0733:
            r6 = 0
            r7 = 0
        L_0x0735:
            java.util.ArrayList<android.text.StaticLayout> r8 = r1.linkLayout
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x0759
            java.util.ArrayList<android.text.StaticLayout> r8 = r1.linkLayout
            java.lang.Object r8 = r8.get(r7)
            android.text.StaticLayout r8 = (android.text.StaticLayout) r8
            int r9 = r8.getLineCount()
            if (r9 <= 0) goto L_0x0756
            int r9 = r8.getLineCount()
            r10 = 1
            int r9 = r9 - r10
            int r9 = r8.getLineBottom(r9)
            int r6 = r6 + r9
        L_0x0756:
            int r7 = r7 + 1
            goto L_0x0735
        L_0x0759:
            int r4 = r4 + r6
            android.text.StaticLayout r7 = r1.fromInfoLayout
            if (r7 == 0) goto L_0x077a
            int r7 = r1.linkY
            int r7 = r7 + r6
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r7 + r8
            r1.fromInfoLayoutY = r7
            android.text.StaticLayout r7 = r1.fromInfoLayout
            int r8 = r7.getLineCount()
            r9 = 1
            int r8 = r8 - r9
            int r7 = r7.getLineBottom(r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r7 = r7 + r8
            int r4 = r4 + r7
        L_0x077a:
            org.telegram.ui.Components.CheckBox2 r7 = r1.checkBox
            r8 = 1103101952(0x41CLASSNAME, float:24.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r10 = 1073741824(0x40000000, float:2.0)
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r10)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r10)
            r7.measure(r9, r8)
            int r7 = android.view.View.MeasureSpec.getSize(r38)
            r8 = 1117257728(0x42980000, float:76.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1099431936(0x41880000, float:17.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r4
            int r8 = java.lang.Math.max(r8, r9)
            boolean r9 = r1.needDivider
            int r8 = r8 + r9
            r1.setMeasuredDimension(r7, r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onMeasure(int, int):void");
    }

    public void setLink(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }

    public ImageReceiver getLinkImageView() {
        return this.linkImageView;
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        SharedLinkCellDelegate sharedLinkCellDelegate;
        int i;
        int i2;
        boolean result = false;
        boolean z = true;
        if (this.message == null || this.linkLayout.isEmpty() || (sharedLinkCellDelegate = this.delegate) == null || !sharedLinkCellDelegate.canPerformActions()) {
            resetPressedLink();
        } else if (event.getAction() == 0 || ((this.linkPreviewPressed || this.spoilerPressed != null) && event.getAction() == 1)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean ok = false;
            int a = 0;
            int offset = 0;
            while (true) {
                if (a >= this.linkLayout.size()) {
                    break;
                }
                StaticLayout layout = this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    int height = layout.getLineBottom(layout.getLineCount() - (z ? 1 : 0));
                    int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
                    if (((float) x) >= ((float) linkPosX) + layout.getLineLeft(0) && ((float) x) <= ((float) linkPosX) + layout.getLineWidth(0)) {
                        int i3 = this.linkY;
                        if (y >= i3 + offset && y <= i3 + offset + height) {
                            ok = true;
                            TLRPC.WebPage webPage = null;
                            if (event.getAction() == 0) {
                                resetPressedLink();
                                this.spoilerPressed = null;
                                if (this.linkSpoilers.get(a, (Object) null) != null) {
                                    Iterator it = this.linkSpoilers.get(a).iterator();
                                    while (true) {
                                        if (!it.hasNext()) {
                                            break;
                                        }
                                        SpoilerEffect eff = (SpoilerEffect) it.next();
                                        if (eff.getBounds().contains(x - linkPosX, (y - this.linkY) - offset)) {
                                            this.spoilerPressed = eff;
                                            this.spoilerTypePressed = 0;
                                            break;
                                        }
                                    }
                                }
                                if (this.spoilerPressed != null) {
                                    result = true;
                                } else {
                                    this.pressedLink = a;
                                    this.linkPreviewPressed = true;
                                    startCheckLongPress();
                                    try {
                                        this.urlPath.setCurrentLayout(layout, 0, 0.0f);
                                        layout.getSelectionPath(0, layout.getText().length(), this.urlPath);
                                    } catch (Exception e) {
                                        FileLog.e((Throwable) e);
                                    }
                                    result = true;
                                }
                            } else if (this.linkPreviewPressed) {
                                try {
                                    if (this.pressedLink == 0 && this.message.messageOwner.media != null) {
                                        webPage = this.message.messageOwner.media.webpage;
                                    }
                                    TLRPC.WebPage webPage2 = webPage;
                                    if (webPage2 == null || webPage2.embed_url == null || webPage2.embed_url.length() == 0) {
                                        this.delegate.onLinkPress(this.links.get(this.pressedLink).toString(), false);
                                    } else {
                                        this.delegate.needOpenWebView(webPage2, this.message);
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                                resetPressedLink();
                                result = true;
                            } else if (this.spoilerPressed != null) {
                                startSpoilerRipples(x, y, offset);
                                result = true;
                            }
                        }
                    }
                    offset += height;
                }
                a++;
                z = true;
            }
            if (event.getAction() == 0) {
                int offX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
                StaticLayout staticLayout = this.descriptionLayout;
                if (staticLayout != null && x >= offX && x <= staticLayout.getWidth() + offX && y >= (i2 = this.descriptionY) && y <= i2 + this.descriptionLayout.getHeight()) {
                    Iterator<SpoilerEffect> it2 = this.descriptionLayoutSpoilers.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        SpoilerEffect eff2 = it2.next();
                        if (eff2.getBounds().contains(x - offX, y - this.descriptionY)) {
                            this.spoilerPressed = eff2;
                            this.spoilerTypePressed = 1;
                            ok = true;
                            result = true;
                            break;
                        }
                    }
                }
                StaticLayout staticLayout2 = this.descriptionLayout2;
                if (staticLayout2 != null && x >= offX && x <= staticLayout2.getWidth() + offX && y >= (i = this.description2Y) && y <= i + this.descriptionLayout2.getHeight()) {
                    Iterator<SpoilerEffect> it3 = this.descriptionLayout2Spoilers.iterator();
                    while (true) {
                        if (!it3.hasNext()) {
                            break;
                        }
                        SpoilerEffect eff3 = it3.next();
                        if (eff3.getBounds().contains(x - offX, y - this.description2Y)) {
                            this.spoilerPressed = eff3;
                            this.spoilerTypePressed = 2;
                            result = true;
                            ok = true;
                            break;
                        }
                    }
                }
                z = true;
            } else {
                z = true;
                if (event.getAction() == 1 && this.spoilerPressed != null) {
                    startSpoilerRipples(x, y, 0);
                    ok = true;
                    result = true;
                }
            }
            if (!ok) {
                resetPressedLink();
            }
        } else if (event.getAction() == 3) {
            resetPressedLink();
        }
        if (result || super.onTouchEvent(event)) {
            return z;
        }
        return false;
    }

    private void startSpoilerRipples(int x, int y, int offset) {
        int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
        resetPressedLink();
        this.spoilerPressed.setOnRippleEndCallback(new SharedLinkCell$$ExternalSyntheticLambda1(this));
        int nx = x - linkPosX;
        float rad = (float) Math.sqrt(Math.pow((double) getWidth(), 2.0d) + Math.pow((double) getHeight(), 2.0d));
        float offY = 0.0f;
        switch (this.spoilerTypePressed) {
            case 0:
                for (int i = 0; i < this.linkLayout.size(); i++) {
                    Layout lt = this.linkLayout.get(i);
                    offY += (float) lt.getLineBottom(lt.getLineCount() - 1);
                    for (SpoilerEffect e : this.linkSpoilers.get(i)) {
                        e.startRipple((float) nx, ((float) ((y - getYOffsetForType(0)) - offset)) + offY, rad);
                    }
                }
                break;
            case 1:
                for (SpoilerEffect sp : this.descriptionLayoutSpoilers) {
                    sp.startRipple((float) nx, (float) (y - getYOffsetForType(1)), rad);
                }
                break;
            case 2:
                for (SpoilerEffect sp2 : this.descriptionLayout2Spoilers) {
                    sp2.startRipple((float) nx, (float) (y - getYOffsetForType(2)), rad);
                }
                break;
        }
        for (int i2 = 0; i2 <= 2; i2++) {
            if (i2 != this.spoilerTypePressed) {
                switch (i2) {
                    case 0:
                        for (int j = 0; j < this.linkLayout.size(); j++) {
                            Layout lt2 = this.linkLayout.get(j);
                            offY += (float) lt2.getLineBottom(lt2.getLineCount() - 1);
                            for (SpoilerEffect e2 : this.linkSpoilers.get(j)) {
                                e2.startRipple((float) e2.getBounds().centerX(), (float) e2.getBounds().centerY(), rad);
                            }
                        }
                        break;
                    case 1:
                        for (SpoilerEffect sp3 : this.descriptionLayoutSpoilers) {
                            sp3.startRipple((float) sp3.getBounds().centerX(), (float) sp3.getBounds().centerY(), rad);
                        }
                        break;
                    case 2:
                        for (SpoilerEffect sp4 : this.descriptionLayout2Spoilers) {
                            sp4.startRipple((float) sp4.getBounds().centerX(), (float) sp4.getBounds().centerY(), rad);
                        }
                        break;
                }
            }
        }
        this.spoilerTypePressed = -1;
        this.spoilerPressed = null;
    }

    /* renamed from: lambda$startSpoilerRipples$1$org-telegram-ui-Cells-SharedLinkCell  reason: not valid java name */
    public /* synthetic */ void m1520xd74f5b04() {
        post(new SharedLinkCell$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$startSpoilerRipples$0$org-telegram-ui-Cells-SharedLinkCell  reason: not valid java name */
    public /* synthetic */ void m1519x75fcbe65() {
        this.message.isSpoilersRevealed = true;
        this.linkSpoilers.clear();
        this.descriptionLayoutSpoilers.clear();
        this.descriptionLayout2Spoilers.clear();
        invalidate();
    }

    private int getYOffsetForType(int type) {
        switch (type) {
            case 1:
                return this.descriptionY;
            case 2:
                return this.description2Y;
            default:
                return this.linkY;
        }
    }

    public String getLink(int num) {
        if (num < 0 || num >= this.links.size()) {
            return null;
        }
        return this.links.get(num).toString();
    }

    /* access modifiers changed from: protected */
    public void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        }
        if (this.dateLayout != null) {
            canvas.save();
            canvas2.translate((float) (AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX)), (float) this.titleY);
            this.dateLayout.draw(canvas2);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            canvas.save();
            float x = (float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL) {
                StaticLayout staticLayout = this.dateLayout;
                x += staticLayout == null ? 0.0f : (float) (staticLayout.getWidth() + AndroidUtilities.dp(4.0f));
            }
            canvas2.translate(x, (float) this.titleY);
            this.titleLayout.draw(canvas2);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.captionY);
            this.captionLayout.draw(canvas2);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            SpoilerEffect.renderWithRipple(this, false, this.descriptionTextPaint.getColor(), -AndroidUtilities.dp(2.0f), this.patchedDescriptionLayout, this.descriptionLayout, this.descriptionLayoutSpoilers, canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            SpoilerEffect.renderWithRipple(this, false, this.descriptionTextPaint.getColor(), -AndroidUtilities.dp(2.0f), this.patchedDescriptionLayout2, this.descriptionLayout2, this.descriptionLayout2Spoilers, canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            int offset = 0;
            for (int a = 0; a < this.linkLayout.size(); a++) {
                StaticLayout layout = this.linkLayout.get(a);
                List<SpoilerEffect> spoilers = this.linkSpoilers.get(a);
                if (layout.getLineCount() > 0) {
                    canvas.save();
                    canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + offset));
                    this.path.rewind();
                    if (spoilers != null) {
                        for (SpoilerEffect eff : spoilers) {
                            Rect b = eff.getBounds();
                            this.path.addRect((float) b.left, (float) b.top, (float) b.right, (float) b.bottom, Path.Direction.CW);
                        }
                    }
                    canvas.save();
                    canvas2.clipPath(this.path, Region.Op.DIFFERENCE);
                    if (this.pressedLink == a) {
                        canvas2.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    layout.draw(canvas2);
                    canvas.restore();
                    canvas.save();
                    canvas2.clipPath(this.path);
                    this.path.rewind();
                    if (spoilers != null && !spoilers.isEmpty()) {
                        spoilers.get(0).getRipplePath(this.path);
                    }
                    canvas2.clipPath(this.path);
                    if (this.pressedLink == a) {
                        canvas2.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    layout.draw(canvas2);
                    canvas.restore();
                    if (spoilers != null) {
                        for (SpoilerEffect eff2 : spoilers) {
                            eff2.draw(canvas2);
                        }
                    }
                    canvas.restore();
                    offset += layout.getLineBottom(layout.getLineCount() - 1);
                }
            }
        }
        if (this.fromInfoLayout != null) {
            canvas.save();
            canvas2.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.fromInfoLayoutY);
            this.fromInfoLayout.draw(canvas2);
            canvas.restore();
        }
        this.letterDrawable.draw(canvas2);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas2);
        }
        if (!this.needDivider) {
            return;
        }
        if (LocaleController.isRTL) {
            canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        } else {
            canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.titleLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.descriptionLayout != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout2.getText());
        }
        info.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            info.setChecked(true);
            info.setCheckable(true);
        }
    }
}
