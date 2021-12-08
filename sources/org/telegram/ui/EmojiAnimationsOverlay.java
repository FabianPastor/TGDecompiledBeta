package org.telegram.ui;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerSetBulletinLayout;

public class EmojiAnimationsOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final HashSet<String> excludeEmojiFromPack;
    private static final HashSet<String> supportedEmoji = new HashSet<>();
    private final int ANIMATION_JSON_VERSION = 1;
    private final String INTERACTIONS_STICKER_PACK = "EmojiAnimations";
    ArrayList<Integer> animationIndexes = new ArrayList<>();
    private boolean attached;
    ChatActivity chatActivity;
    FrameLayout contentLayout;
    int currentAccount;
    long dialogId;
    ArrayList<DrawingObject> drawingObjects = new ArrayList<>();
    HashMap<String, ArrayList<TLRPC.Document>> emojiInteractionsStickersMap = new HashMap<>();
    Runnable hintRunnable;
    boolean inited = false;
    HashMap<Long, Integer> lastAnimationIndex = new HashMap<>();
    String lastTappedEmoji;
    int lastTappedMsgId = -1;
    long lastTappedTime = 0;
    RecyclerListView listView;
    Random random = new Random();
    Runnable sentInteractionsRunnable;
    TLRPC.TL_messages_stickerSet set;
    int threadMsgId;
    ArrayList<Long> timeIntervals = new ArrayList<>();

    static {
        HashSet<String> hashSet = new HashSet<>();
        excludeEmojiFromPack = hashSet;
        hashSet.add("0⃣");
        hashSet.add("1⃣");
        hashSet.add("2⃣");
        hashSet.add("3⃣");
        hashSet.add("4⃣");
        hashSet.add("5⃣");
        hashSet.add("6⃣");
        hashSet.add("7⃣");
        hashSet.add("8⃣");
        hashSet.add("9⃣");
    }

    public EmojiAnimationsOverlay(ChatActivity chatActivity2, FrameLayout frameLayout, RecyclerListView chatListView, int currentAccount2, long dialogId2, int threadMsgId2) {
        this.chatActivity = chatActivity2;
        this.contentLayout = frameLayout;
        this.listView = chatListView;
        this.currentAccount = currentAccount2;
        this.dialogId = dialogId2;
        this.threadMsgId = threadMsgId2;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.attached = true;
        checkStickerPack();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.attached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void checkStickerPack() {
        if (!this.inited) {
            TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("EmojiAnimations");
            this.set = stickerSetByName;
            if (stickerSetByName == null) {
                this.set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("EmojiAnimations");
            }
            if (this.set == null) {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("EmojiAnimations", false, true);
            }
            if (this.set != null) {
                HashMap<Long, TLRPC.Document> stickersMap = new HashMap<>();
                for (int i = 0; i < this.set.documents.size(); i++) {
                    stickersMap.put(Long.valueOf(((TLRPC.Document) this.set.documents.get(i)).id), (TLRPC.Document) this.set.documents.get(i));
                }
                for (int i2 = 0; i2 < this.set.packs.size(); i2++) {
                    TLRPC.TL_stickerPack pack = (TLRPC.TL_stickerPack) this.set.packs.get(i2);
                    if (!excludeEmojiFromPack.contains(pack.emoticon) && pack.documents.size() > 0) {
                        supportedEmoji.add(pack.emoticon);
                        ArrayList<TLRPC.Document> stickers = new ArrayList<>();
                        this.emojiInteractionsStickersMap.put(pack.emoticon, stickers);
                        for (int j = 0; j < pack.documents.size(); j++) {
                            stickers.add(stickersMap.get(pack.documents.get(j)));
                        }
                        if (pack.emoticon.equals("❤")) {
                            for (String heart : new String[]{"🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎"}) {
                                supportedEmoji.add(heart);
                                this.emojiInteractionsStickersMap.put(heart, stickers);
                            }
                        }
                    }
                }
                this.inited = true;
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        EmojiAnimationsOverlay emojiAnimationsOverlay = this;
        int i = id;
        if (i == NotificationCenter.diceStickersDidLoad) {
            if ("EmojiAnimations".equals(args[0])) {
                checkStickerPack();
            }
        } else if (i == NotificationCenter.onEmojiInteractionsReceived) {
            long dialogId2 = args[0].longValue();
            int i2 = 1;
            TLRPC.TL_sendMessageEmojiInteraction action = args[1];
            if (dialogId2 == emojiAnimationsOverlay.dialogId && supportedEmoji.contains(action.emoticon)) {
                final int messageId = action.msg_id;
                if (action.interaction.data != null) {
                    try {
                        JSONArray array = new JSONObject(action.interaction.data).getJSONArray("a");
                        int i3 = 0;
                        while (i3 < array.length()) {
                            JSONObject actionObject = array.getJSONObject(i3);
                            final int animation = actionObject.optInt("i", i2) - i2;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    EmojiAnimationsOverlay.this.findViewAndShowAnimation(messageId, animation);
                                }
                            }, (long) (actionObject.optDouble("t", 0.0d) * 1000.0d));
                            i3++;
                            i2 = 1;
                            emojiAnimationsOverlay = this;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            Integer printingType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId);
            if (printingType != null && printingType.intValue() == 5) {
                cancelHintRunnable();
            }
        }
    }

    /* JADX WARNING: type inference failed for: r2v4, types: [android.view.View] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void findViewAndShowAnimation(int r6, int r7) {
        /*
            r5 = this;
            boolean r0 = r5.attached
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r0 = 0
            r1 = 0
        L_0x0007:
            org.telegram.ui.Components.RecyclerListView r2 = r5.listView
            int r2 = r2.getChildCount()
            if (r1 >= r2) goto L_0x003f
            org.telegram.ui.Components.RecyclerListView r2 = r5.listView
            android.view.View r2 = r2.getChildAt(r1)
            boolean r3 = r2 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r3 == 0) goto L_0x003c
            r3 = r2
            org.telegram.ui.Cells.ChatMessageCell r3 = (org.telegram.ui.Cells.ChatMessageCell) r3
            org.telegram.messenger.ImageReceiver r4 = r3.getPhotoImage()
            boolean r4 = r4.hasNotThumb()
            if (r4 == 0) goto L_0x003c
            org.telegram.messenger.MessageObject r4 = r3.getMessageObject()
            java.lang.String r4 = r4.getStickerEmoji()
            if (r4 == 0) goto L_0x003c
            org.telegram.messenger.MessageObject r4 = r3.getMessageObject()
            int r4 = r4.getId()
            if (r4 != r6) goto L_0x003c
            r0 = r3
            goto L_0x003f
        L_0x003c:
            int r1 = r1 + 1
            goto L_0x0007
        L_0x003f:
            if (r0 == 0) goto L_0x005d
            org.telegram.ui.ChatActivity r1 = r5.chatActivity
            r1.restartSticker(r0)
            org.telegram.messenger.MessageObject r1 = r0.getMessageObject()
            java.lang.String r1 = r1.getStickerEmoji()
            boolean r1 = org.telegram.messenger.EmojiData.hasEmojiSupportVibration(r1)
            if (r1 != 0) goto L_0x0058
            r1 = 3
            r0.performHapticFeedback(r1)
        L_0x0058:
            r1 = 0
            r2 = 1
            r5.showAnimationForCell(r0, r7, r1, r2)
        L_0x005d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EmojiAnimationsOverlay.findViewAndShowAnimation(int, int):void");
    }

    public void draw(Canvas canvas) {
        float viewX;
        if (!this.drawingObjects.isEmpty()) {
            int i = 0;
            while (i < this.drawingObjects.size()) {
                DrawingObject drawingObject = this.drawingObjects.get(i);
                drawingObject.viewFound = false;
                int k = 0;
                while (true) {
                    if (k >= this.listView.getChildCount()) {
                        break;
                    }
                    View child = this.listView.getChildAt(k);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) child;
                        if (cell.getMessageObject().getId() == drawingObject.messageId) {
                            drawingObject.viewFound = true;
                            float viewX2 = this.listView.getX() + child.getX() + cell.getPhotoImage().getImageX();
                            float viewY = this.listView.getY() + child.getY() + cell.getPhotoImage().getImageY();
                            if (drawingObject.isOut) {
                                viewX = viewX2 + ((-cell.getPhotoImage().getImageWidth()) * 2.0f) + ((float) AndroidUtilities.dp(24.0f));
                            } else {
                                viewX = viewX2 + ((float) (-AndroidUtilities.dp(24.0f)));
                            }
                            drawingObject.lastX = viewX;
                            drawingObject.lastY = viewY - cell.getPhotoImage().getImageWidth();
                            drawingObject.lastW = cell.getPhotoImage().getImageWidth();
                        }
                    }
                    k++;
                }
                drawingObject.imageReceiver.setImageCoords(drawingObject.lastX + drawingObject.randomOffsetX, drawingObject.lastY + drawingObject.randomOffsetY, drawingObject.lastW * 3.0f, drawingObject.lastW * 3.0f);
                if (!drawingObject.isOut) {
                    canvas.save();
                    canvas.scale(-1.0f, 1.0f, drawingObject.imageReceiver.getCenterX(), drawingObject.imageReceiver.getCenterY());
                    drawingObject.imageReceiver.draw(canvas);
                    canvas.restore();
                } else {
                    drawingObject.imageReceiver.draw(canvas);
                }
                if (drawingObject.wasPlayed && drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().getCurrentFrame() == drawingObject.imageReceiver.getLottieAnimation().getFramesCount() - 2) {
                    this.drawingObjects.remove(i);
                    i--;
                } else if (drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().isRunning()) {
                    drawingObject.wasPlayed = true;
                } else if (drawingObject.imageReceiver.getLottieAnimation() != null && !drawingObject.imageReceiver.getLottieAnimation().isRunning()) {
                    drawingObject.imageReceiver.getLottieAnimation().setCurrentFrame(0, true);
                    drawingObject.imageReceiver.getLottieAnimation().start();
                }
                i++;
            }
            this.contentLayout.invalidate();
        }
    }

    public void onTapItem(ChatMessageCell view, ChatActivity chatActivity2) {
        ChatMessageCell chatMessageCell = view;
        ChatActivity chatActivity3 = chatActivity2;
        if (chatActivity3.currentUser != null && !chatActivity2.isSecretChat() && view.getMessageObject() != null && view.getMessageObject().getId() >= 0) {
            boolean show = showAnimationForCell(chatMessageCell, -1, true, false);
            if (show && !EmojiData.hasEmojiSupportVibration(view.getMessageObject().getStickerEmoji())) {
                chatMessageCell.performHapticFeedback(3);
            }
            Integer printingType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId);
            boolean canShowHint = true;
            if (printingType != null && printingType.intValue() == 5) {
                canShowHint = false;
            }
            if (canShowHint && this.hintRunnable == null && show) {
                if ((Bulletin.getVisibleBulletin() == null || !Bulletin.getVisibleBulletin().isShowing()) && SharedConfig.emojiInteractionsHintCount > 0 && UserConfig.getInstance(this.currentAccount).getClientUserId() != chatActivity3.currentUser.id) {
                    SharedConfig.updateEmojiInteractionsHintCount(SharedConfig.emojiInteractionsHintCount - 1);
                    StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(chatActivity2.getParentActivity(), (TLObject) null, -1, MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(view.getMessageObject().getStickerEmoji()), chatActivity2.getResourceProvider());
                    stickerSetBulletinLayout.subtitleTextView.setVisibility(8);
                    stickerSetBulletinLayout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EmojiInteractionTapHint", NUM, chatActivity3.currentUser.first_name)));
                    stickerSetBulletinLayout.titleTextView.setTypeface((Typeface) null);
                    stickerSetBulletinLayout.titleTextView.setMaxLines(3);
                    stickerSetBulletinLayout.titleTextView.setSingleLine(false);
                    final Bulletin bulletin = Bulletin.make((BaseFragment) chatActivity3, (Bulletin.Layout) stickerSetBulletinLayout, 2750);
                    AnonymousClass2 r5 = new Runnable() {
                        public void run() {
                            bulletin.show();
                            EmojiAnimationsOverlay.this.hintRunnable = null;
                        }
                    };
                    this.hintRunnable = r5;
                    AndroidUtilities.runOnUIThread(r5, 1500);
                }
            }
        }
    }

    public void cancelHintRunnable() {
        Runnable runnable = this.hintRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.hintRunnable = null;
    }

    private boolean showAnimationForCell(ChatMessageCell view, int animation, boolean sendTap, boolean sendSeen) {
        String emoji;
        ArrayList<TLRPC.Document> arrayList;
        Runnable runnable;
        int animation2 = animation;
        if (this.drawingObjects.size() > 12 || !view.getPhotoImage().hasNotThumb() || (emoji = view.getMessageObject().getStickerEmoji()) == null) {
            return false;
        }
        float imageH = view.getPhotoImage().getImageHeight();
        float imageW = view.getPhotoImage().getImageWidth();
        if (imageH <= 0.0f || imageW <= 0.0f || !supportedEmoji.contains(emoji) || (arrayList = this.emojiInteractionsStickersMap.get(view.getMessageObject().getStickerEmoji())) == null || arrayList.isEmpty()) {
            return false;
        }
        int sameAnimationsCount = 0;
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            if (this.drawingObjects.get(i).messageId == view.getMessageObject().getId()) {
                sameAnimationsCount++;
                if (this.drawingObjects.get(i).imageReceiver.getLottieAnimation() == null || this.drawingObjects.get(i).imageReceiver.getLottieAnimation().isGeneratingCache()) {
                    return false;
                }
            }
        }
        if (sameAnimationsCount >= 4) {
            return false;
        }
        if (animation2 < 0 || animation2 > arrayList.size() - 1) {
            animation2 = Math.abs(this.random.nextInt()) % arrayList.size();
        }
        TLRPC.Document document = arrayList.get(animation2);
        DrawingObject drawingObject = new DrawingObject();
        drawingObject.randomOffsetX = (imageW / 4.0f) * (((float) (this.random.nextInt() % 101)) / 100.0f);
        drawingObject.randomOffsetY = (imageH / 4.0f) * (((float) (this.random.nextInt() % 101)) / 100.0f);
        drawingObject.messageId = view.getMessageObject().getId();
        drawingObject.document = document;
        drawingObject.isOut = view.getMessageObject().isOutOwner();
        Integer lastIndex = this.lastAnimationIndex.get(Long.valueOf(document.id));
        int currentIndex = lastIndex == null ? 0 : lastIndex.intValue();
        this.lastAnimationIndex.put(Long.valueOf(document.id), Integer.valueOf((currentIndex + 1) % 4));
        ImageLocation imageLocation = ImageLocation.getForDocument(document);
        drawingObject.imageReceiver.setUniqKeyPrefix(currentIndex + "_" + drawingObject.messageId + "_");
        int w = (int) ((2.0f * imageW) / AndroidUtilities.density);
        drawingObject.imageReceiver.setImage(imageLocation, w + "_" + w + "_pcache", (Drawable) null, "tgs", this.set, 1);
        drawingObject.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        drawingObject.imageReceiver.setAllowStartAnimation(true);
        drawingObject.imageReceiver.setAutoRepeat(0);
        if (drawingObject.imageReceiver.getLottieAnimation() != null) {
            drawingObject.imageReceiver.getLottieAnimation().start();
        }
        this.drawingObjects.add(drawingObject);
        drawingObject.imageReceiver.onAttachedToWindow();
        drawingObject.imageReceiver.setParentView(this.contentLayout);
        this.contentLayout.invalidate();
        if (sendTap) {
            int i2 = this.lastTappedMsgId;
            if (!(i2 == 0 || i2 == view.getMessageObject().getId() || (runnable = this.sentInteractionsRunnable) == null)) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.sentInteractionsRunnable.run();
            }
            this.lastTappedMsgId = view.getMessageObject().getId();
            this.lastTappedEmoji = emoji;
            if (this.lastTappedTime == 0) {
                this.lastTappedTime = System.currentTimeMillis();
                this.timeIntervals.clear();
                this.animationIndexes.clear();
                this.timeIntervals.add(0L);
                this.animationIndexes.add(Integer.valueOf(animation2));
                int i3 = w;
            } else {
                int i4 = w;
                this.timeIntervals.add(Long.valueOf(System.currentTimeMillis() - this.lastTappedTime));
                this.animationIndexes.add(Integer.valueOf(animation2));
            }
            Runnable runnable2 = this.sentInteractionsRunnable;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                this.sentInteractionsRunnable = null;
            }
            EmojiAnimationsOverlay$$ExternalSyntheticLambda0 emojiAnimationsOverlay$$ExternalSyntheticLambda0 = new EmojiAnimationsOverlay$$ExternalSyntheticLambda0(this);
            this.sentInteractionsRunnable = emojiAnimationsOverlay$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(emojiAnimationsOverlay$$ExternalSyntheticLambda0, 500);
        }
        if (sendSeen) {
            int i5 = currentIndex;
            DrawingObject drawingObject2 = drawingObject;
            TLRPC.Document document2 = document;
            MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadMsgId, 11, emoji, 0);
        } else {
            DrawingObject drawingObject3 = drawingObject;
            TLRPC.Document document3 = document;
        }
        return true;
    }

    /* renamed from: lambda$showAnimationForCell$0$org-telegram-ui-EmojiAnimationsOverlay  reason: not valid java name */
    public /* synthetic */ void m2893x4149e228() {
        sendCurrentTaps();
        this.sentInteractionsRunnable = null;
    }

    private void sendCurrentTaps() {
        if (this.lastTappedMsgId != 0) {
            TLRPC.TL_sendMessageEmojiInteraction interaction = new TLRPC.TL_sendMessageEmojiInteraction();
            interaction.msg_id = this.lastTappedMsgId;
            interaction.emoticon = this.lastTappedEmoji;
            interaction.interaction = new TLRPC.TL_dataJSON();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("v", 1);
                JSONArray array = new JSONArray();
                for (int i = 0; i < this.timeIntervals.size(); i++) {
                    JSONObject action = new JSONObject();
                    action.put("i", this.animationIndexes.get(i).intValue() + 1);
                    action.put("t", (double) (((float) this.timeIntervals.get(i).longValue()) / 1000.0f));
                    array.put(i, action);
                }
                jsonObject.put("a", array);
                interaction.interaction.data = jsonObject.toString();
                TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
                int i2 = this.threadMsgId;
                if (i2 != 0) {
                    req.top_msg_id = i2;
                    req.flags = 1 | req.flags;
                }
                req.action = interaction;
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, (RequestDelegate) null);
                clearSendingInfo();
            } catch (JSONException e) {
                clearSendingInfo();
                FileLog.e((Throwable) e);
            }
        }
    }

    private void clearSendingInfo() {
        this.lastTappedMsgId = 0;
        this.lastTappedEmoji = null;
        this.lastTappedTime = 0;
        this.timeIntervals.clear();
        this.animationIndexes.clear();
    }

    public void onScrolled(int dy) {
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            if (!this.drawingObjects.get(i).viewFound) {
                this.drawingObjects.get(i).lastY -= (float) dy;
            }
        }
    }

    private class DrawingObject {
        TLRPC.Document document;
        ImageReceiver imageReceiver;
        boolean isOut;
        public float lastW;
        public float lastX;
        public float lastY;
        int messageId;
        public float randomOffsetX;
        public float randomOffsetY;
        public boolean viewFound;
        boolean wasPlayed;

        private DrawingObject() {
            this.imageReceiver = new ImageReceiver();
        }
    }
}
