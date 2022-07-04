package org.telegram.ui;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextUtils;
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
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersAlert;

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
                float childY = 0.0f;
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
                            float viewX2 = this.listView.getX() + child.getX();
                            float viewY = this.listView.getY() + child.getY();
                            childY = child.getY();
                            if (drawingObject.isPremiumSticker) {
                                drawingObject.lastX = cell.getPhotoImage().getImageX() + viewX2;
                                drawingObject.lastY = cell.getPhotoImage().getImageY() + viewY;
                            } else {
                                float viewX3 = viewX2 + cell.getPhotoImage().getImageX();
                                float viewY2 = viewY + cell.getPhotoImage().getImageY();
                                if (drawingObject.isOut) {
                                    viewX = viewX3 + ((-cell.getPhotoImage().getImageWidth()) * 2.0f) + ((float) AndroidUtilities.dp(24.0f));
                                } else {
                                    viewX = viewX3 + ((float) (-AndroidUtilities.dp(24.0f)));
                                }
                                drawingObject.lastX = viewX;
                                drawingObject.lastY = viewY2 - cell.getPhotoImage().getImageWidth();
                            }
                            drawingObject.lastW = cell.getPhotoImage().getImageWidth();
                            drawingObject.lastH = cell.getPhotoImage().getImageHeight();
                        }
                    }
                    k++;
                }
                if (drawingObject.viewFound == 0 || drawingObject.lastH + childY < this.chatActivity.getChatListViewPadding() || childY > ((float) (this.listView.getMeasuredHeight() - this.chatActivity.blurredViewBottomOffset))) {
                    drawingObject.removing = true;
                }
                if (drawingObject.removing && drawingObject.removeProgress != 1.0f) {
                    drawingObject.removeProgress = Utilities.clamp(drawingObject.removeProgress + 0.10666667f, 1.0f, 0.0f);
                    drawingObject.imageReceiver.setAlpha(1.0f - drawingObject.removeProgress);
                    this.chatActivity.contentView.invalidate();
                }
                if (drawingObject.isPremiumSticker) {
                    float size = drawingObject.lastH * 1.49926f;
                    float paddingHorizontal = 0.0546875f * size;
                    float top = ((drawingObject.lastY + (drawingObject.lastH / 2.0f)) - (size / 2.0f)) - (0.00279f * size);
                    if (!drawingObject.isOut) {
                        drawingObject.imageReceiver.setImageCoords(drawingObject.lastX - paddingHorizontal, top, size, size);
                    } else {
                        drawingObject.imageReceiver.setImageCoords(((drawingObject.lastX + drawingObject.lastW) - size) + paddingHorizontal, top, size, size);
                    }
                    if (!drawingObject.isOut) {
                        canvas.save();
                        canvas.scale(-1.0f, 1.0f, drawingObject.imageReceiver.getCenterX(), drawingObject.imageReceiver.getCenterY());
                        drawingObject.imageReceiver.draw(canvas);
                        canvas.restore();
                    } else {
                        drawingObject.imageReceiver.draw(canvas);
                    }
                } else {
                    drawingObject.imageReceiver.setImageCoords(drawingObject.lastX + drawingObject.randomOffsetX, drawingObject.lastY + drawingObject.randomOffsetY, drawingObject.lastW * 3.0f, drawingObject.lastW * 3.0f);
                    if (!drawingObject.isOut) {
                        canvas.save();
                        canvas.scale(-1.0f, 1.0f, drawingObject.imageReceiver.getCenterX(), drawingObject.imageReceiver.getCenterY());
                        drawingObject.imageReceiver.draw(canvas);
                        canvas.restore();
                    } else {
                        drawingObject.imageReceiver.draw(canvas);
                    }
                }
                if (drawingObject.removeProgress == 1.0f || (drawingObject.wasPlayed && drawingObject.imageReceiver.getLottieAnimation() != null && drawingObject.imageReceiver.getLottieAnimation().getCurrentFrame() == drawingObject.imageReceiver.getLottieAnimation().getFramesCount() - 2)) {
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
            if (this.drawingObjects.isEmpty()) {
                onAllEffectsEnd();
            }
            this.contentLayout.invalidate();
        }
    }

    public void onAllEffectsEnd() {
    }

    public boolean onTapItem(ChatMessageCell view, ChatActivity chatActivity2) {
        ChatMessageCell chatMessageCell = view;
        ChatActivity chatActivity3 = chatActivity2;
        if (chatActivity2.isSecretChat() || view.getMessageObject() == null || view.getMessageObject().getId() < 0) {
            return false;
        }
        if (!view.getMessageObject().isPremiumSticker() && chatActivity3.currentUser == null) {
            return false;
        }
        boolean show = showAnimationForCell(chatMessageCell, -1, true, false);
        if (show && (!EmojiData.hasEmojiSupportVibration(view.getMessageObject().getStickerEmoji()) || view.getMessageObject().isPremiumSticker())) {
            chatMessageCell.performHapticFeedback(3);
        }
        if (view.getMessageObject().isPremiumSticker()) {
            view.getMessageObject().forcePlayEffect = false;
            view.getMessageObject().messageOwner.premiumEffectWasPlayed = true;
            chatActivity2.getMessagesStorage().updateMessageCustomParams(this.dialogId, view.getMessageObject().messageOwner);
            return show;
        }
        Integer printingType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId);
        boolean canShowHint = true;
        if (printingType != null && printingType.intValue() == 5) {
            canShowHint = false;
        }
        if (canShowHint && this.hintRunnable == null && show && ((Bulletin.getVisibleBulletin() == null || !Bulletin.getVisibleBulletin().isShowing()) && SharedConfig.emojiInteractionsHintCount > 0 && UserConfig.getInstance(this.currentAccount).getClientUserId() != chatActivity3.currentUser.id)) {
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
        return show;
    }

    public void cancelHintRunnable() {
        Runnable runnable = this.hintRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.hintRunnable = null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean showAnimationForCell(org.telegram.ui.Cells.ChatMessageCell r31, int r32, boolean r33, boolean r34) {
        /*
            r30 = this;
            r0 = r30
            r1 = r32
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r2 = r0.drawingObjects
            int r2 = r2.size()
            r3 = 0
            r4 = 12
            if (r2 <= r4) goto L_0x0010
            return r3
        L_0x0010:
            org.telegram.messenger.ImageReceiver r2 = r31.getPhotoImage()
            boolean r2 = r2.hasNotThumb()
            if (r2 != 0) goto L_0x001b
            return r3
        L_0x001b:
            org.telegram.messenger.MessageObject r2 = r31.getMessageObject()
            java.lang.String r4 = r2.getStickerEmoji()
            if (r4 != 0) goto L_0x0026
            return r3
        L_0x0026:
            org.telegram.messenger.ImageReceiver r5 = r31.getPhotoImage()
            float r5 = r5.getImageHeight()
            org.telegram.messenger.ImageReceiver r6 = r31.getPhotoImage()
            float r6 = r6.getImageWidth()
            r7 = 0
            int r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r8 <= 0) goto L_0x03eb
            int r7 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r7 > 0) goto L_0x0044
            r20 = r5
            r15 = r6
            goto L_0x03ee
        L_0x0044:
            java.lang.String r4 = r0.unwrapEmoji(r4)
            boolean r7 = r2.isPremiumSticker()
            java.util.HashSet<java.lang.String> r8 = supportedEmoji
            boolean r8 = r8.contains(r4)
            if (r8 != 0) goto L_0x005c
            if (r7 == 0) goto L_0x0057
            goto L_0x005c
        L_0x0057:
            r20 = r5
            r15 = r6
            goto L_0x03e9
        L_0x005c:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r8 = r0.emojiInteractionsStickersMap
            java.lang.Object r8 = r8.get(r4)
            r15 = r8
            java.util.ArrayList r15 = (java.util.ArrayList) r15
            if (r15 == 0) goto L_0x006d
            boolean r8 = r15.isEmpty()
            if (r8 == 0) goto L_0x006f
        L_0x006d:
            if (r7 == 0) goto L_0x03e4
        L_0x006f:
            r8 = 0
            r9 = 0
            r10 = 0
            r14 = r8
            r16 = r9
        L_0x0075:
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r8 = r0.drawingObjects
            int r8 = r8.size()
            if (r10 >= r8) goto L_0x00ed
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r8 = r0.drawingObjects
            java.lang.Object r8 = r8.get(r10)
            org.telegram.ui.EmojiAnimationsOverlay$DrawingObject r8 = (org.telegram.ui.EmojiAnimationsOverlay.DrawingObject) r8
            int r8 = r8.messageId
            org.telegram.messenger.MessageObject r9 = r31.getMessageObject()
            int r9 = r9.getId()
            if (r8 != r9) goto L_0x00b8
            int r14 = r14 + 1
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r8 = r0.drawingObjects
            java.lang.Object r8 = r8.get(r10)
            org.telegram.ui.EmojiAnimationsOverlay$DrawingObject r8 = (org.telegram.ui.EmojiAnimationsOverlay.DrawingObject) r8
            org.telegram.messenger.ImageReceiver r8 = r8.imageReceiver
            org.telegram.ui.Components.RLottieDrawable r8 = r8.getLottieAnimation()
            if (r8 == 0) goto L_0x00b7
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r8 = r0.drawingObjects
            java.lang.Object r8 = r8.get(r10)
            org.telegram.ui.EmojiAnimationsOverlay$DrawingObject r8 = (org.telegram.ui.EmojiAnimationsOverlay.DrawingObject) r8
            org.telegram.messenger.ImageReceiver r8 = r8.imageReceiver
            org.telegram.ui.Components.RLottieDrawable r8 = r8.getLottieAnimation()
            boolean r8 = r8.isGeneratingCache()
            if (r8 == 0) goto L_0x00b8
        L_0x00b7:
            return r3
        L_0x00b8:
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r8 = r0.drawingObjects
            java.lang.Object r8 = r8.get(r10)
            org.telegram.ui.EmojiAnimationsOverlay$DrawingObject r8 = (org.telegram.ui.EmojiAnimationsOverlay.DrawingObject) r8
            org.telegram.tgnet.TLRPC$Document r8 = r8.document
            if (r8 == 0) goto L_0x00ea
            org.telegram.messenger.MessageObject r8 = r31.getMessageObject()
            org.telegram.tgnet.TLRPC$Document r8 = r8.getDocument()
            if (r8 == 0) goto L_0x00ea
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r8 = r0.drawingObjects
            java.lang.Object r8 = r8.get(r10)
            org.telegram.ui.EmojiAnimationsOverlay$DrawingObject r8 = (org.telegram.ui.EmojiAnimationsOverlay.DrawingObject) r8
            org.telegram.tgnet.TLRPC$Document r8 = r8.document
            long r8 = r8.id
            org.telegram.messenger.MessageObject r11 = r31.getMessageObject()
            org.telegram.tgnet.TLRPC$Document r11 = r11.getDocument()
            long r11 = r11.id
            int r13 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x00ea
            int r16 = r16 + 1
        L_0x00ea:
            int r10 = r10 + 1
            goto L_0x0075
        L_0x00ed:
            if (r33 == 0) goto L_0x0145
            if (r7 == 0) goto L_0x0145
            if (r14 <= 0) goto L_0x0145
            org.telegram.ui.Components.Bulletin r8 = org.telegram.ui.Components.Bulletin.getVisibleBulletin()
            if (r8 == 0) goto L_0x0106
            org.telegram.ui.Components.Bulletin r8 = org.telegram.ui.Components.Bulletin.getVisibleBulletin()
            int r8 = r8.hash
            int r9 = r2.getId()
            if (r8 != r9) goto L_0x0106
            return r3
        L_0x0106:
            org.telegram.tgnet.TLRPC$InputStickerSet r8 = r2.getInputStickerSet()
            r9 = 0
            java.lang.String r10 = r8.short_name
            if (r10 == 0) goto L_0x011b
            int r10 = r0.currentAccount
            org.telegram.messenger.MediaDataController r10 = org.telegram.messenger.MediaDataController.getInstance(r10)
            java.lang.String r11 = r8.short_name
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = r10.getStickerSetByName(r11)
        L_0x011b:
            if (r9 != 0) goto L_0x0129
            int r10 = r0.currentAccount
            org.telegram.messenger.MediaDataController r10 = org.telegram.messenger.MediaDataController.getInstance(r10)
            long r11 = r8.id
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r9 = r10.getStickerSetById(r11)
        L_0x0129:
            if (r9 != 0) goto L_0x0141
            org.telegram.tgnet.TLRPC$TL_messages_getStickerSet r10 = new org.telegram.tgnet.TLRPC$TL_messages_getStickerSet
            r10.<init>()
            r10.stickerset = r8
            int r11 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11)
            org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda3 r12 = new org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda3
            r12.<init>(r0, r2)
            r11.sendRequest(r10, r12)
            goto L_0x0144
        L_0x0141:
            r0.m3442x4149e228(r9, r2)
        L_0x0144:
            return r3
        L_0x0145:
            r8 = 4
            if (r14 < r8) goto L_0x0149
            return r3
        L_0x0149:
            r9 = 0
            r10 = 0
            r13 = 1
            if (r7 == 0) goto L_0x0155
            org.telegram.tgnet.TLRPC$VideoSize r10 = r2.getPremiumStickerAnimation()
            r12 = r9
            r11 = r10
            goto L_0x0177
        L_0x0155:
            if (r1 < 0) goto L_0x015e
            int r11 = r15.size()
            int r11 = r11 - r13
            if (r1 <= r11) goto L_0x016e
        L_0x015e:
            java.util.Random r11 = r0.random
            int r11 = r11.nextInt()
            int r11 = java.lang.Math.abs(r11)
            int r12 = r15.size()
            int r1 = r11 % r12
        L_0x016e:
            java.lang.Object r11 = r15.get(r1)
            r9 = r11
            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC.Document) r9
            r12 = r9
            r11 = r10
        L_0x0177:
            if (r12 != 0) goto L_0x017c
            if (r11 != 0) goto L_0x017c
            return r3
        L_0x017c:
            org.telegram.ui.EmojiAnimationsOverlay$DrawingObject r9 = new org.telegram.ui.EmojiAnimationsOverlay$DrawingObject
            r10 = 0
            r9.<init>()
            boolean r10 = r2.isPremiumSticker()
            r9.isPremiumSticker = r10
            r10 = 1082130432(0x40800000, float:4.0)
            float r17 = r6 / r10
            java.util.Random r3 = r0.random
            int r3 = r3.nextInt()
            int r3 = r3 % 101
            float r3 = (float) r3
            r19 = 1120403456(0x42CLASSNAME, float:100.0)
            float r3 = r3 / r19
            float r3 = r3 * r17
            r9.randomOffsetX = r3
            float r3 = r5 / r10
            java.util.Random r10 = r0.random
            int r10 = r10.nextInt()
            int r10 = r10 % 101
            float r10 = (float) r10
            float r10 = r10 / r19
            float r3 = r3 * r10
            r9.randomOffsetY = r3
            org.telegram.messenger.MessageObject r3 = r31.getMessageObject()
            int r3 = r3.getId()
            r9.messageId = r3
            r9.document = r12
            org.telegram.messenger.MessageObject r3 = r31.getMessageObject()
            boolean r3 = r3.isOutOwner()
            r9.isOut = r3
            org.telegram.messenger.ImageReceiver r3 = r9.imageReceiver
            r3.setAllowStartAnimation(r13)
            java.lang.String r3 = "_"
            if (r12 == 0) goto L_0x025b
            r10 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 * r6
            float r17 = org.telegram.messenger.AndroidUtilities.density
            float r10 = r10 / r17
            int r10 = (int) r10
            java.util.HashMap<java.lang.Long, java.lang.Integer> r13 = r0.lastAnimationIndex
            r20 = r9
            long r8 = r12.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            java.lang.Object r8 = r13.get(r8)
            java.lang.Integer r8 = (java.lang.Integer) r8
            if (r8 != 0) goto L_0x01ea
            r9 = 0
            goto L_0x01ee
        L_0x01ea:
            int r9 = r8.intValue()
        L_0x01ee:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r13 = r0.lastAnimationIndex
            r22 = r14
            r21 = r15
            long r14 = r12.id
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            int r15 = r9 + 1
            r19 = 4
            int r15 = r15 % 4
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            r13.put(r14, r15)
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForDocument(r12)
            r14 = r20
            org.telegram.messenger.ImageReceiver r15 = r14.imageReceiver
            r20 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            r5.append(r3)
            r19 = r8
            int r8 = r14.messageId
            r5.append(r8)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            r15.setUniqKeyPrefix(r5)
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r10)
            r8.append(r3)
            r8.append(r10)
            java.lang.String r3 = "_pcache"
            r8.append(r3)
            java.lang.String r25 = r8.toString()
            r26 = 0
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r0.set
            r29 = 1
            java.lang.String r27 = "tgs"
            r23 = r5
            r24 = r13
            r28 = r3
            r23.setImage(r24, r25, r26, r27, r28, r29)
            r15 = r6
            r3 = r10
            goto L_0x02f0
        L_0x025b:
            r20 = r5
            r22 = r14
            r21 = r15
            r14 = r9
            r5 = 1069547520(0x3fCLASSNAME, float:1.5)
            float r5 = r5 * r6
            float r8 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 / r8
            int r10 = (int) r5
            if (r16 <= 0) goto L_0x02bd
            java.util.HashMap<java.lang.Long, java.lang.Integer> r5 = r0.lastAnimationIndex
            org.telegram.tgnet.TLRPC$Document r8 = r2.getDocument()
            long r8 = r8.id
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            java.lang.Object r5 = r5.get(r8)
            java.lang.Integer r5 = (java.lang.Integer) r5
            if (r5 != 0) goto L_0x0282
            r8 = 0
            goto L_0x0286
        L_0x0282:
            int r8 = r5.intValue()
        L_0x0286:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r9 = r0.lastAnimationIndex
            org.telegram.tgnet.TLRPC$Document r13 = r2.getDocument()
            r23 = r5
            r15 = r6
            long r5 = r13.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            int r6 = r8 + 1
            r13 = 4
            int r6 = r6 % r13
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r9.put(r5, r6)
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r3)
            int r9 = r14.messageId
            r6.append(r9)
            r6.append(r3)
            java.lang.String r6 = r6.toString()
            r5.setUniqKeyPrefix(r6)
            goto L_0x02be
        L_0x02bd:
            r15 = r6
        L_0x02be:
            org.telegram.tgnet.TLRPC$Document r5 = r2.getDocument()
            r14.document = r5
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            org.telegram.tgnet.TLRPC$Document r6 = r2.getDocument()
            org.telegram.messenger.ImageLocation r24 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.VideoSize) r11, (org.telegram.tgnet.TLRPC.Document) r6)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r10)
            r6.append(r3)
            r6.append(r10)
            java.lang.String r25 = r6.toString()
            r26 = 0
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = r0.set
            r29 = 1
            java.lang.String r27 = "tgs"
            r23 = r5
            r28 = r3
            r23.setImage(r24, r25, r26, r27, r28, r29)
            r3 = r10
        L_0x02f0:
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            r6 = 2147483647(0x7fffffff, float:NaN)
            r5.setLayerNum(r6)
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            r6 = 0
            r5.setAutoRepeat(r6)
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            org.telegram.ui.Components.RLottieDrawable r5 = r5.getLottieAnimation()
            if (r5 == 0) goto L_0x0320
            boolean r5 = r14.isPremiumSticker
            if (r5 == 0) goto L_0x0315
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            org.telegram.ui.Components.RLottieDrawable r5 = r5.getLottieAnimation()
            r13 = 1
            r5.setCurrentFrame(r6, r6, r13)
            goto L_0x0316
        L_0x0315:
            r13 = 1
        L_0x0316:
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            org.telegram.ui.Components.RLottieDrawable r5 = r5.getLottieAnimation()
            r5.start()
            goto L_0x0321
        L_0x0320:
            r13 = 1
        L_0x0321:
            java.util.ArrayList<org.telegram.ui.EmojiAnimationsOverlay$DrawingObject> r5 = r0.drawingObjects
            r5.add(r14)
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            r5.onAttachedToWindow()
            org.telegram.messenger.ImageReceiver r5 = r14.imageReceiver
            android.widget.FrameLayout r6 = r0.contentLayout
            r5.setParentView(r6)
            android.widget.FrameLayout r5 = r0.contentLayout
            r5.invalidate()
            if (r33 == 0) goto L_0x03be
            if (r7 != 0) goto L_0x03be
            int r5 = r0.lastTappedMsgId
            if (r5 == 0) goto L_0x0355
            org.telegram.messenger.MessageObject r6 = r31.getMessageObject()
            int r6 = r6.getId()
            if (r5 == r6) goto L_0x0355
            java.lang.Runnable r5 = r0.sentInteractionsRunnable
            if (r5 == 0) goto L_0x0355
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            java.lang.Runnable r5 = r0.sentInteractionsRunnable
            r5.run()
        L_0x0355:
            org.telegram.messenger.MessageObject r5 = r31.getMessageObject()
            int r5 = r5.getId()
            r0.lastTappedMsgId = r5
            r0.lastTappedEmoji = r4
            long r5 = r0.lastTappedTime
            r8 = 0
            int r10 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x038d
            long r5 = java.lang.System.currentTimeMillis()
            r0.lastTappedTime = r5
            java.util.ArrayList<java.lang.Long> r5 = r0.timeIntervals
            r5.clear()
            java.util.ArrayList<java.lang.Integer> r5 = r0.animationIndexes
            r5.clear()
            java.util.ArrayList<java.lang.Long> r5 = r0.timeIntervals
            java.lang.Long r6 = java.lang.Long.valueOf(r8)
            r5.add(r6)
            java.util.ArrayList<java.lang.Integer> r5 = r0.animationIndexes
            java.lang.Integer r6 = java.lang.Integer.valueOf(r1)
            r5.add(r6)
            r6 = r14
            goto L_0x03a7
        L_0x038d:
            java.util.ArrayList<java.lang.Long> r5 = r0.timeIntervals
            long r8 = java.lang.System.currentTimeMillis()
            r6 = r14
            long r13 = r0.lastTappedTime
            long r8 = r8 - r13
            java.lang.Long r8 = java.lang.Long.valueOf(r8)
            r5.add(r8)
            java.util.ArrayList<java.lang.Integer> r5 = r0.animationIndexes
            java.lang.Integer r8 = java.lang.Integer.valueOf(r1)
            r5.add(r8)
        L_0x03a7:
            java.lang.Runnable r5 = r0.sentInteractionsRunnable
            if (r5 == 0) goto L_0x03b1
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            r5 = 0
            r0.sentInteractionsRunnable = r5
        L_0x03b1:
            org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.EmojiAnimationsOverlay$$ExternalSyntheticLambda0
            r5.<init>(r0)
            r0.sentInteractionsRunnable = r5
            r8 = 500(0x1f4, double:2.47E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r5, r8)
            goto L_0x03bf
        L_0x03be:
            r6 = r14
        L_0x03bf:
            if (r34 == 0) goto L_0x03dc
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r9 = r0.dialogId
            int r5 = r0.threadMsgId
            r13 = 11
            r14 = 0
            r18 = r11
            r11 = r5
            r5 = r12
            r12 = r13
            r17 = 1
            r13 = r4
            r19 = r22
            r8.sendTyping(r9, r11, r12, r13, r14)
            goto L_0x03e3
        L_0x03dc:
            r18 = r11
            r5 = r12
            r19 = r22
            r17 = 1
        L_0x03e3:
            return r17
        L_0x03e4:
            r20 = r5
            r21 = r15
            r15 = r6
        L_0x03e9:
            r3 = 0
            return r3
        L_0x03eb:
            r20 = r5
            r15 = r6
        L_0x03ee:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EmojiAnimationsOverlay.showAnimationForCell(org.telegram.ui.Cells.ChatMessageCell, int, boolean, boolean):boolean");
    }

    /* renamed from: lambda$showAnimationForCell$1$org-telegram-ui-EmojiAnimationsOverlay  reason: not valid java name */
    public /* synthetic */ void m3443x9867d307(MessageObject messageObject, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new EmojiAnimationsOverlay$$ExternalSyntheticLambda2(this, response, messageObject));
    }

    /* renamed from: lambda$showAnimationForCell$2$org-telegram-ui-EmojiAnimationsOverlay  reason: not valid java name */
    public /* synthetic */ void m3444xevar_c3e6() {
        sendCurrentTaps();
        this.sentInteractionsRunnable = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: showStickerSetBulletin */
    public void m3442x4149e228(TLRPC.TL_messages_stickerSet stickerSet, MessageObject messageObject) {
        if (!MessagesController.getInstance(this.currentAccount).premiumLocked && this.chatActivity.getParentActivity() != null) {
            StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(this.contentLayout.getContext(), (TLObject) null, -1, messageObject.getDocument(), this.chatActivity.getResourceProvider());
            stickerSetBulletinLayout.titleTextView.setText(stickerSet.set.title);
            stickerSetBulletinLayout.subtitleTextView.setText(LocaleController.getString("PremiumStickerTooltip", NUM));
            Bulletin.UndoButton viewButton = new Bulletin.UndoButton(this.chatActivity.getParentActivity(), true, this.chatActivity.getResourceProvider());
            stickerSetBulletinLayout.setButton(viewButton);
            viewButton.setUndoAction(new EmojiAnimationsOverlay$$ExternalSyntheticLambda1(this, messageObject));
            viewButton.setText(LocaleController.getString("ViewAction", NUM));
            Bulletin bulletin = Bulletin.make((BaseFragment) this.chatActivity, (Bulletin.Layout) stickerSetBulletinLayout, 2750);
            bulletin.hash = messageObject.getId();
            bulletin.show();
        }
    }

    /* renamed from: lambda$showStickerSetBulletin$3$org-telegram-ui-EmojiAnimationsOverlay  reason: not valid java name */
    public /* synthetic */ void m3445xCLASSNAMEe9be0(MessageObject messageObject) {
        StickersAlert alert = new StickersAlert(this.chatActivity.getParentActivity(), this.chatActivity, messageObject.getInputStickerSet(), (TLRPC.TL_messages_stickerSet) null, this.chatActivity.chatActivityEnterView, this.chatActivity.getResourceProvider());
        alert.setCalcMandatoryInsets(this.chatActivity.isKeyboardVisible());
        this.chatActivity.showDialog(alert);
    }

    private String unwrapEmoji(String emoji) {
        CharSequence fixedEmoji = emoji;
        int length = emoji.length();
        int a = 0;
        while (a < length) {
            if (a < length - 1 && ((fixedEmoji.charAt(a) == 55356 && fixedEmoji.charAt(a + 1) >= 57339 && fixedEmoji.charAt(a + 1) <= 57343) || (fixedEmoji.charAt(a) == 8205 && (fixedEmoji.charAt(a + 1) == 9792 || fixedEmoji.charAt(a + 1) == 9794)))) {
                fixedEmoji = TextUtils.concat(new CharSequence[]{fixedEmoji.subSequence(0, a), fixedEmoji.subSequence(a + 2, fixedEmoji.length())});
                length -= 2;
                a--;
            } else if (fixedEmoji.charAt(a) == 65039) {
                fixedEmoji = TextUtils.concat(new CharSequence[]{fixedEmoji.subSequence(0, a), fixedEmoji.subSequence(a + 1, fixedEmoji.length())});
                length--;
                a--;
            }
            a++;
        }
        return fixedEmoji.toString();
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

    public boolean isIdle() {
        return this.drawingObjects.isEmpty();
    }

    public boolean checkPosition(ChatMessageCell messageCell, float chatListViewPaddingTop, int bottom) {
        float y = messageCell.getY() + messageCell.getPhotoImage().getCenterY();
        if (y <= chatListViewPaddingTop || y >= ((float) bottom)) {
            return false;
        }
        return true;
    }

    private class DrawingObject {
        TLRPC.Document document;
        ImageReceiver imageReceiver;
        boolean isOut;
        public boolean isPremiumSticker;
        public float lastH;
        public float lastW;
        public float lastX;
        public float lastY;
        int messageId;
        public float randomOffsetX;
        public float randomOffsetY;
        float removeProgress;
        boolean removing;
        public boolean viewFound;
        boolean wasPlayed;

        private DrawingObject() {
            this.imageReceiver = new ImageReceiver();
        }
    }
}
