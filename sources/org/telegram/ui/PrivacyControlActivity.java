package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PrivacyControlActivity;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    public static final int TYPE_CONTACTS = 2;
    public static final int TYPE_EVERYBODY = 0;
    public static final int TYPE_NOBODY = 1;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int alwaysShareRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> currentMinus;
    /* access modifiers changed from: private */
    public ArrayList<Integer> currentPlus;
    /* access modifiers changed from: private */
    public int currentSubType;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int detailRow;
    private View doneButton;
    /* access modifiers changed from: private */
    public int everybodyRow;
    private ArrayList<Integer> initialMinus;
    private ArrayList<Integer> initialPlus;
    private int initialRulesSubType;
    private int initialRulesType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public MessageCell messageCell;
    /* access modifiers changed from: private */
    public int messageRow;
    /* access modifiers changed from: private */
    public int myContactsRow;
    /* access modifiers changed from: private */
    public int neverShareRow;
    /* access modifiers changed from: private */
    public int nobodyRow;
    /* access modifiers changed from: private */
    public int p2pDetailRow;
    /* access modifiers changed from: private */
    public int p2pRow;
    /* access modifiers changed from: private */
    public int p2pSectionRow;
    /* access modifiers changed from: private */
    public int phoneContactsRow;
    /* access modifiers changed from: private */
    public int phoneDetailRow;
    /* access modifiers changed from: private */
    public int phoneEverybodyRow;
    /* access modifiers changed from: private */
    public int phoneSectionRow;
    /* access modifiers changed from: private */
    public boolean prevSubtypeContacts;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int rulesType;
    /* access modifiers changed from: private */
    public int sectionRow;
    /* access modifiers changed from: private */
    public int shareDetailRow;
    /* access modifiers changed from: private */
    public int shareSectionRow;

    private class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        /* access modifiers changed from: private */
        public ChatMessageCell cell;
        /* access modifiers changed from: private */
        public HintView hintView;
        private final Runnable invalidateRunnable = new Runnable() {
            public final void run() {
                PrivacyControlActivity.MessageCell.this.invalidate();
            }
        };
        /* access modifiers changed from: private */
        public MessageObject messageObject;
        private Drawable shadowDrawable;

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean z) {
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public MessageCell(Context context) {
            super(context);
            setWillNotDraw(false);
            setClipToPadding(false);
            this.shadowDrawable = Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow");
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            TLRPC.User user = MessagesController.getInstance(PrivacyControlActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(PrivacyControlActivity.this.currentAccount).getClientUserId()));
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.message = LocaleController.getString("PrivacyForwardsMessageLine", NUM);
            tL_message.date = (((int) (System.currentTimeMillis() / 1000)) - 3600) + 60;
            tL_message.dialog_id = 1;
            tL_message.flags = 261;
            tL_message.from_id = 0;
            tL_message.id = 1;
            tL_message.fwd_from = new TLRPC.TL_messageFwdHeader();
            tL_message.fwd_from.from_name = ContactsController.formatName(user.first_name, user.last_name);
            tL_message.media = new TLRPC.TL_messageMediaEmpty();
            tL_message.out = false;
            tL_message.to_id = new TLRPC.TL_peerUser();
            tL_message.to_id.user_id = UserConfig.getInstance(PrivacyControlActivity.this.currentAccount).getClientUserId();
            this.messageObject = new MessageObject(PrivacyControlActivity.this.currentAccount, tL_message, true);
            MessageObject messageObject2 = this.messageObject;
            messageObject2.eventId = 1;
            messageObject2.resetLayout();
            this.cell = new ChatMessageCell(context);
            this.cell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate(PrivacyControlActivity.this) {
                public /* synthetic */ boolean canPerformActions() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                }

                public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                }

                public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell);
                }

                public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                }

                public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                }

                public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell, f, f2);
                }

                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                }

                public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                }

                public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressShare(this, chatMessageCell);
                }

                public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
                }

                public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                }

                public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell, str);
                }

                public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TLRPC.TL_pollAnswer> arrayList, int i, int i2, int i3) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                }

                public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                }

                public /* synthetic */ String getAdminRank(int i) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, i);
                }

                public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                }

                public /* synthetic */ boolean hasSelectedMessages() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                }

                public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                }

                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject);
                }

                public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                }

                public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                }

                public /* synthetic */ void videoTimerReached() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                }
            });
            ChatMessageCell chatMessageCell = this.cell;
            chatMessageCell.isChat = false;
            chatMessageCell.setFullyDraw(true);
            this.cell.setMessageObject(this.messageObject, (MessageObject.GroupedMessages) null, false, false);
            addView(this.cell, LayoutHelper.createLinear(-1, -2));
            this.hintView = new HintView(context, 1, true);
            addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
            if (!(cachedWallpaperNonBlocking == null || this.backgroundDrawable == cachedWallpaperNonBlocking)) {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            Drawable drawable = this.backgroundDrawable;
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof BackgroundGradientDrawable) {
                    this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable2).drawExactBoundsSize(canvas, this);
                } else {
                    drawable2.draw(canvas);
                }
            } else if (!(drawable instanceof BitmapDrawable)) {
                super.onDraw(canvas);
            } else if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                canvas.save();
                float f = 2.0f / AndroidUtilities.density;
                canvas.scale(f, f);
                this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                int measuredHeight = getMeasuredHeight();
                float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                float intrinsicHeight = ((float) measuredHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                if (measuredWidth < intrinsicHeight) {
                    measuredWidth = intrinsicHeight;
                }
                int ceil = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * measuredWidth));
                int ceil2 = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * measuredWidth));
                int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                int i = (measuredHeight - ceil2) / 2;
                canvas.save();
                canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                this.backgroundDrawable.setBounds(measuredWidth2, i, ceil + measuredWidth2, ceil2 + i);
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }
    }

    public PrivacyControlActivity(int i) {
        this(i, false);
    }

    public PrivacyControlActivity(int i, boolean z) {
        this.initialPlus = new ArrayList<>();
        this.initialMinus = new ArrayList<>();
        this.rulesType = i;
        if (z) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    public View createView(Context context) {
        if (this.rulesType == 5) {
            this.messageCell = new MessageCell(context);
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.rulesType;
        if (i == 6) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyPhone", NUM));
        } else if (i == 5) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyForwards", NUM));
        } else if (i == 4) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyProfilePhoto", NUM));
        } else if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyP2P", NUM));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (PrivacyControlActivity.this.checkDiscard()) {
                        PrivacyControlActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    PrivacyControlActivity.this.processDone();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        boolean hasChanges = hasChanges();
        float f = 1.0f;
        this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
        View view = this.doneButton;
        if (!hasChanges) {
            f = 0.0f;
        }
        view.setScaleY(f);
        this.doneButton.setEnabled(hasChanges);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PrivacyControlActivity.this.lambda$createView$2$PrivacyControlActivity(view, i);
            }
        });
        setMessageText();
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [int] */
    /* JADX WARNING: type inference failed for: r0v3 */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r0v11 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$2$PrivacyControlActivity(android.view.View r6, int r7) {
        /*
            r5 = this;
            int r6 = r5.nobodyRow
            r0 = 0
            r1 = 1
            if (r7 == r6) goto L_0x00a5
            int r6 = r5.everybodyRow
            if (r7 == r6) goto L_0x00a5
            int r6 = r5.myContactsRow
            if (r7 != r6) goto L_0x0010
            goto L_0x00a5
        L_0x0010:
            int r6 = r5.phoneContactsRow
            if (r7 == r6) goto L_0x008a
            int r6 = r5.phoneEverybodyRow
            if (r7 != r6) goto L_0x001a
            goto L_0x008a
        L_0x001a:
            int r6 = r5.neverShareRow
            if (r7 == r6) goto L_0x0032
            int r6 = r5.alwaysShareRow
            if (r7 != r6) goto L_0x0023
            goto L_0x0032
        L_0x0023:
            int r6 = r5.p2pRow
            if (r7 != r6) goto L_0x00c6
            org.telegram.ui.PrivacyControlActivity r6 = new org.telegram.ui.PrivacyControlActivity
            r7 = 3
            r6.<init>(r7)
            r5.presentFragment(r6)
            goto L_0x00c6
        L_0x0032:
            int r6 = r5.neverShareRow
            if (r7 != r6) goto L_0x0039
            java.util.ArrayList<java.lang.Integer> r6 = r5.currentMinus
            goto L_0x003b
        L_0x0039:
            java.util.ArrayList<java.lang.Integer> r6 = r5.currentPlus
        L_0x003b:
            boolean r2 = r6.isEmpty()
            if (r2 == 0) goto L_0x006d
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            int r2 = r5.neverShareRow
            if (r7 != r2) goto L_0x004d
            java.lang.String r2 = "isNeverShare"
            goto L_0x004f
        L_0x004d:
            java.lang.String r2 = "isAlwaysShare"
        L_0x004f:
            r6.putBoolean(r2, r1)
            int r2 = r5.rulesType
            if (r2 == 0) goto L_0x0057
            r0 = 1
        L_0x0057:
            java.lang.String r1 = "isGroup"
            r6.putBoolean(r1, r0)
            org.telegram.ui.GroupCreateActivity r0 = new org.telegram.ui.GroupCreateActivity
            r0.<init>(r6)
            org.telegram.ui.-$$Lambda$PrivacyControlActivity$gyVCUgP7-dxVWuYDNVL7ikSQ-s4 r6 = new org.telegram.ui.-$$Lambda$PrivacyControlActivity$gyVCUgP7-dxVWuYDNVL7ikSQ-s4
            r6.<init>(r7)
            r0.setDelegate((org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate) r6)
            r5.presentFragment(r0)
            goto L_0x00c6
        L_0x006d:
            org.telegram.ui.PrivacyUsersActivity r2 = new org.telegram.ui.PrivacyUsersActivity
            int r3 = r5.rulesType
            if (r3 == 0) goto L_0x0075
            r3 = 1
            goto L_0x0076
        L_0x0075:
            r3 = 0
        L_0x0076:
            int r4 = r5.alwaysShareRow
            if (r7 != r4) goto L_0x007b
            r0 = 1
        L_0x007b:
            r2.<init>(r6, r3, r0)
            org.telegram.ui.-$$Lambda$PrivacyControlActivity$Optb0rMT99Nhw1X8nsWqhFLsnHs r6 = new org.telegram.ui.-$$Lambda$PrivacyControlActivity$Optb0rMT99Nhw1X8nsWqhFLsnHs
            r6.<init>(r7)
            r2.setDelegate(r6)
            r5.presentFragment(r2)
            goto L_0x00c6
        L_0x008a:
            int r6 = r5.currentSubType
            int r2 = r5.phoneEverybodyRow
            if (r7 != r2) goto L_0x0092
            r6 = 0
            goto L_0x0097
        L_0x0092:
            int r0 = r5.phoneContactsRow
            if (r7 != r0) goto L_0x0097
            r6 = 1
        L_0x0097:
            int r7 = r5.currentSubType
            if (r6 != r7) goto L_0x009c
            return
        L_0x009c:
            r5.currentSubType = r6
            r5.updateDoneButton()
            r5.updateRows(r1)
            goto L_0x00c6
        L_0x00a5:
            int r6 = r5.currentType
            int r2 = r5.nobodyRow
            if (r7 != r2) goto L_0x00ad
            r0 = 1
            goto L_0x00b9
        L_0x00ad:
            int r2 = r5.everybodyRow
            if (r7 != r2) goto L_0x00b2
            goto L_0x00b9
        L_0x00b2:
            int r0 = r5.myContactsRow
            if (r7 != r0) goto L_0x00b8
            r0 = 2
            goto L_0x00b9
        L_0x00b8:
            r0 = r6
        L_0x00b9:
            int r6 = r5.currentType
            if (r0 != r6) goto L_0x00be
            return
        L_0x00be:
            r5.currentType = r0
            r5.updateDoneButton()
            r5.updateRows(r1)
        L_0x00c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.lambda$createView$2$PrivacyControlActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$null$0$PrivacyControlActivity(int i, ArrayList arrayList) {
        int i2 = 0;
        if (i == this.neverShareRow) {
            this.currentMinus = arrayList;
            while (i2 < this.currentMinus.size()) {
                this.currentPlus.remove(this.currentMinus.get(i2));
                i2++;
            }
        } else {
            this.currentPlus = arrayList;
            while (i2 < this.currentPlus.size()) {
                this.currentMinus.remove(this.currentPlus.get(i2));
                i2++;
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    public /* synthetic */ void lambda$null$1$PrivacyControlActivity(int i, ArrayList arrayList, boolean z) {
        int i2 = 0;
        if (i == this.neverShareRow) {
            this.currentMinus = arrayList;
            if (z) {
                while (i2 < this.currentMinus.size()) {
                    this.currentPlus.remove(this.currentMinus.get(i2));
                    i2++;
                }
            }
        } else {
            this.currentPlus = arrayList;
            if (z) {
                while (i2 < this.currentPlus.size()) {
                    this.currentMinus.remove(this.currentPlus.get(i2));
                    i2++;
                }
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageCell messageCell2;
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (i == NotificationCenter.emojiDidLoad) {
            this.listView.invalidateViews();
        } else if (i == NotificationCenter.didSetNewWallpapper && (messageCell2 = this.messageCell) != null) {
            messageCell2.invalidate();
        }
    }

    private void updateDoneButton() {
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        float f = 1.0f;
        ViewPropertyAnimator scaleX = this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f);
        if (!hasChanges) {
            f = 0.0f;
        }
        scaleX.scaleY(f).setDuration(180).start();
    }

    private void applyCurrentPrivacySettings() {
        TLRPC.InputUser inputUser;
        TLRPC.InputUser inputUser2;
        TLRPC.TL_account_setPrivacy tL_account_setPrivacy = new TLRPC.TL_account_setPrivacy();
        int i = this.rulesType;
        if (i == 6) {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TLRPC.TL_account_setPrivacy tL_account_setPrivacy2 = new TLRPC.TL_account_setPrivacy();
                tL_account_setPrivacy2.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    tL_account_setPrivacy2.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
                } else {
                    tL_account_setPrivacy2.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setPrivacy2, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(tLObject, tL_error);
                    }
                }, 2);
            }
        } else if (i == 5) {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyForwards();
        } else if (i == 4) {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
        } else if (i == 3) {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
        } else if (i == 2) {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
        } else if (i == 1) {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
        } else {
            tL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TLRPC.TL_inputPrivacyValueAllowUsers tL_inputPrivacyValueAllowUsers = new TLRPC.TL_inputPrivacyValueAllowUsers();
            TLRPC.TL_inputPrivacyValueAllowChatParticipants tL_inputPrivacyValueAllowChatParticipants = new TLRPC.TL_inputPrivacyValueAllowChatParticipants();
            for (int i2 = 0; i2 < this.currentPlus.size(); i2++) {
                int intValue = this.currentPlus.get(i2).intValue();
                if (intValue > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(intValue));
                    if (!(user == null || (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) == null)) {
                        tL_inputPrivacyValueAllowUsers.users.add(inputUser2);
                    }
                } else {
                    tL_inputPrivacyValueAllowChatParticipants.chats.add(Integer.valueOf(-intValue));
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowUsers);
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueAllowChatParticipants);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TLRPC.TL_inputPrivacyValueDisallowUsers tL_inputPrivacyValueDisallowUsers = new TLRPC.TL_inputPrivacyValueDisallowUsers();
            TLRPC.TL_inputPrivacyValueDisallowChatParticipants tL_inputPrivacyValueDisallowChatParticipants = new TLRPC.TL_inputPrivacyValueDisallowChatParticipants();
            for (int i3 = 0; i3 < this.currentMinus.size(); i3++) {
                int intValue2 = this.currentMinus.get(i3).intValue();
                if (intValue2 > 0) {
                    TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(intValue2));
                    if (!(user2 == null || (inputUser = getMessagesController().getInputUser(user2)) == null)) {
                        tL_inputPrivacyValueDisallowUsers.users.add(inputUser);
                    }
                } else {
                    tL_inputPrivacyValueDisallowChatParticipants.chats.add(Integer.valueOf(-intValue2));
                }
            }
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowUsers);
            tL_account_setPrivacy.rules.add(tL_inputPrivacyValueDisallowChatParticipants);
        }
        int i4 = this.currentType;
        if (i4 == 0) {
            tL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
        } else if (i4 == 1) {
            tL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
        } else if (i4 == 2) {
            tL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog alertDialog = null;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_setPrivacy, new RequestDelegate(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                PrivacyControlActivity.this.lambda$null$3$PrivacyControlActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$PrivacyControlActivity(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TLRPC.TL_account_privacyRules) tLObject).rules, 7);
        }
    }

    public /* synthetic */ void lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tL_error, tLObject) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PrivacyControlActivity.this.lambda$null$5$PrivacyControlActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$PrivacyControlActivity(AlertDialog alertDialog, TLRPC.TL_error tL_error, TLObject tLObject) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (tL_error == null) {
            TLRPC.TL_account_privacyRules tL_account_privacyRules = (TLRPC.TL_account_privacyRules) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_account_privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_account_privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(tL_account_privacyRules.rules, this.rulesType);
            finishFragment();
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PrivacyFloodControlError", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList<>();
        this.currentMinus = new ArrayList<>();
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            char c = 65535;
            for (int i = 0; i < privacyRules.size(); i++) {
                TLRPC.PrivacyRule privacyRule = privacyRules.get(i);
                if (privacyRule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants tL_privacyValueAllowChatParticipants = (TLRPC.TL_privacyValueAllowChatParticipants) privacyRule;
                    int size = tL_privacyValueAllowChatParticipants.chats.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        this.currentPlus.add(Integer.valueOf(-tL_privacyValueAllowChatParticipants.chats.get(i2).intValue()));
                    }
                } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants tL_privacyValueDisallowChatParticipants = (TLRPC.TL_privacyValueDisallowChatParticipants) privacyRule;
                    int size2 = tL_privacyValueDisallowChatParticipants.chats.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        this.currentMinus.add(Integer.valueOf(-tL_privacyValueDisallowChatParticipants.chats.get(i3).intValue()));
                    }
                } else if (privacyRule instanceof TLRPC.TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(((TLRPC.TL_privacyValueAllowUsers) privacyRule).users);
                } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(((TLRPC.TL_privacyValueDisallowUsers) privacyRule).users);
                } else if (c == 65535) {
                    if (privacyRule instanceof TLRPC.TL_privacyValueAllowAll) {
                        c = 0;
                    } else {
                        c = privacyRule instanceof TLRPC.TL_privacyValueDisallowAll ? (char) 1 : 2;
                    }
                }
            }
            if (c == 0 || (c == 65535 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (c == 2 || (c == 65535 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (c == 1 || (c == 65535 && this.currentPlus.size() > 0)) {
                this.currentType = 1;
            }
            View view = this.doneButton;
            if (view != null) {
                view.setAlpha(0.0f);
                this.doneButton.setScaleX(0.0f);
                this.doneButton.setScaleY(0.0f);
                this.doneButton.setEnabled(false);
            }
        }
        this.initialPlus.clear();
        this.initialMinus.clear();
        this.initialRulesType = this.currentType;
        this.initialPlus.addAll(this.currentPlus);
        this.initialMinus.addAll(this.currentMinus);
        if (this.rulesType == 6) {
            ArrayList<TLRPC.PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules2 != null && privacyRules2.size() != 0) {
                int i4 = 0;
                while (true) {
                    if (i4 >= privacyRules2.size()) {
                        break;
                    }
                    TLRPC.PrivacyRule privacyRule2 = privacyRules2.get(i4);
                    if (privacyRule2 instanceof TLRPC.TL_privacyValueAllowAll) {
                        this.currentSubType = 0;
                        break;
                    } else if (privacyRule2 instanceof TLRPC.TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else if (privacyRule2 instanceof TLRPC.TL_privacyValueAllowContacts) {
                        this.currentSubType = 1;
                        break;
                    } else {
                        i4++;
                    }
                }
            } else {
                this.currentSubType = 0;
            }
            this.initialRulesSubType = this.currentSubType;
        }
        updateRows(false);
    }

    private boolean hasChanges() {
        int i = this.initialRulesType;
        int i2 = this.currentType;
        if (i != i2) {
            return true;
        }
        if ((this.rulesType == 6 && i2 == 1 && this.initialRulesSubType != this.currentSubType) || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
            return true;
        }
        Collections.sort(this.initialPlus);
        Collections.sort(this.currentPlus);
        if (!this.initialPlus.equals(this.currentPlus)) {
            return true;
        }
        Collections.sort(this.initialMinus);
        Collections.sort(this.currentMinus);
        if (!this.initialMinus.equals(this.currentMinus)) {
            return true;
        }
        return false;
    }

    private void updateRows(boolean z) {
        RecyclerView.ViewHolder findContainingViewHolder;
        int i;
        int i2;
        int i3 = this.alwaysShareRow;
        int i4 = this.neverShareRow;
        int i5 = this.phoneDetailRow;
        int i6 = this.detailRow;
        boolean z2 = this.currentType == 1 && this.currentSubType == 1;
        this.rowCount = 0;
        if (this.rulesType == 5) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.messageRow = i7;
        } else {
            this.messageRow = -1;
        }
        int i8 = this.rowCount;
        this.rowCount = i8 + 1;
        this.sectionRow = i8;
        int i9 = this.rowCount;
        this.rowCount = i9 + 1;
        this.everybodyRow = i9;
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.myContactsRow = i10;
        int i11 = this.rulesType;
        if (i11 == 0 || i11 == 2 || i11 == 3 || i11 == 5 || i11 == 6) {
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.nobodyRow = i12;
        } else {
            this.nobodyRow = -1;
        }
        if (this.rulesType == 6 && this.currentType == 1) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.phoneDetailRow = i13;
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.phoneSectionRow = i14;
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.phoneEverybodyRow = i15;
            int i16 = this.rowCount;
            this.rowCount = i16 + 1;
            this.phoneContactsRow = i16;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        int i17 = this.rowCount;
        this.rowCount = i17 + 1;
        this.detailRow = i17;
        int i18 = this.rowCount;
        this.rowCount = i18 + 1;
        this.shareSectionRow = i18;
        int i19 = this.currentType;
        if (i19 == 1 || i19 == 2) {
            int i20 = this.rowCount;
            this.rowCount = i20 + 1;
            this.alwaysShareRow = i20;
        } else {
            this.alwaysShareRow = -1;
        }
        int i21 = this.currentType;
        if (i21 == 0 || i21 == 2) {
            int i22 = this.rowCount;
            this.rowCount = i22 + 1;
            this.neverShareRow = i22;
        } else {
            this.neverShareRow = -1;
        }
        int i23 = this.rowCount;
        this.rowCount = i23 + 1;
        this.shareDetailRow = i23;
        if (this.rulesType == 2) {
            int i24 = this.rowCount;
            this.rowCount = i24 + 1;
            this.p2pSectionRow = i24;
            int i25 = this.rowCount;
            this.rowCount = i25 + 1;
            this.p2pRow = i25;
            int i26 = this.rowCount;
            this.rowCount = i26 + 1;
            this.p2pDetailRow = i26;
        } else {
            this.p2pSectionRow = -1;
            this.p2pRow = -1;
            this.p2pDetailRow = -1;
        }
        setMessageText();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 == null) {
            return;
        }
        if (z) {
            int childCount = this.listView.getChildCount();
            for (int i27 = 0; i27 < childCount; i27++) {
                View childAt = this.listView.getChildAt(i27);
                if ((childAt instanceof RadioCell) && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) != null) {
                    int adapterPosition = findContainingViewHolder.getAdapterPosition();
                    RadioCell radioCell = (RadioCell) childAt;
                    if (adapterPosition == this.everybodyRow || adapterPosition == this.myContactsRow || adapterPosition == this.nobodyRow) {
                        if (adapterPosition == this.everybodyRow) {
                            i = 0;
                        } else {
                            i = adapterPosition == this.myContactsRow ? 2 : 1;
                        }
                        radioCell.setChecked(this.currentType == i, true);
                    } else {
                        if (adapterPosition == this.phoneContactsRow) {
                            i2 = 1;
                        } else {
                            int i28 = this.phoneEverybodyRow;
                            i2 = 0;
                        }
                        radioCell.setChecked(this.currentSubType == i2, true);
                    }
                }
            }
            if (this.prevSubtypeContacts != z2) {
                this.listAdapter.notifyItemChanged(i6);
            }
            if ((this.alwaysShareRow != -1 || i3 == -1 || this.neverShareRow == -1 || i4 != -1) && (this.alwaysShareRow == -1 || i3 != -1 || this.neverShareRow != -1 || i4 == -1)) {
                if (this.alwaysShareRow != -1 || i3 == -1) {
                    int i29 = this.alwaysShareRow;
                    if (i29 != -1 && i3 == -1) {
                        this.listAdapter.notifyItemInserted(i29);
                    }
                } else {
                    this.listAdapter.notifyItemRemoved(i3);
                }
                if (this.neverShareRow == -1 && i4 != -1) {
                    this.listAdapter.notifyItemRemoved(i4);
                    if (this.phoneDetailRow != -1 || i5 == -1) {
                        int i30 = this.phoneDetailRow;
                        if (i30 != -1 && i5 == -1) {
                            this.listAdapter.notifyItemRangeInserted(i30, 4);
                            return;
                        }
                        return;
                    }
                    this.listAdapter.notifyItemRangeRemoved(i5, 4);
                } else if (this.neverShareRow != -1 && i4 == -1) {
                    if (this.phoneDetailRow != -1 || i5 == -1) {
                        int i31 = this.phoneDetailRow;
                        if (i31 != -1 && i5 == -1) {
                            this.listAdapter.notifyItemRangeInserted(i31, 4);
                        }
                    } else {
                        this.listAdapter.notifyItemRangeRemoved(i5, 4);
                    }
                    this.listAdapter.notifyItemInserted(this.neverShareRow);
                }
            } else {
                ListAdapter listAdapter3 = this.listAdapter;
                if (this.alwaysShareRow != -1) {
                    i3 = i4;
                }
                listAdapter3.notifyItemChanged(i3);
                if (this.phoneDetailRow != -1 || i5 == -1) {
                    int i32 = this.phoneDetailRow;
                    if (i32 != -1 && i5 == -1) {
                        this.listAdapter.notifyItemRangeInserted(i32, 4);
                        return;
                    }
                    return;
                }
                this.listAdapter.notifyItemRangeRemoved(i5, 4);
            }
        } else {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void setMessageText() {
        MessageCell messageCell2 = this.messageCell;
        if (messageCell2 != null) {
            int i = this.currentType;
            if (i == 0) {
                messageCell2.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            } else if (i == 1) {
                messageCell2.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 0;
            } else {
                messageCell2.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id = 1;
            }
            this.messageCell.cell.forceResetMessageObject();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (getParentActivity() != null) {
            if (this.currentType != 0 && this.rulesType == 0) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if (!globalMainSettings.getBoolean("privacyAlertShowed", false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", NUM));
                    }
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(globalMainSettings) {
                        private final /* synthetic */ SharedPreferences f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            PrivacyControlActivity.this.lambda$processDone$7$PrivacyControlActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    public /* synthetic */ void lambda$processDone$7$PrivacyControlActivity(SharedPreferences sharedPreferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        sharedPreferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (this.doneButton.getAlpha() != 1.0f) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$8$PrivacyControlActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$9$PrivacyControlActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$8$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$9$PrivacyControlActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean canBeginSlide() {
        return checkDiscard();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PrivacyControlActivity.this.nobodyRow || adapterPosition == PrivacyControlActivity.this.everybodyRow || adapterPosition == PrivacyControlActivity.this.myContactsRow || adapterPosition == PrivacyControlActivity.this.neverShareRow || adapterPosition == PrivacyControlActivity.this.alwaysShareRow || (adapterPosition == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3));
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i != 0) {
                if (i == 1) {
                    view = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 2) {
                    view2 = new HeaderCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (i == 3) {
                    view2 = new RadioCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (i != 4) {
                    view = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                } else {
                    view = PrivacyControlActivity.this.messageCell;
                }
                return new RecyclerListView.Holder(view);
            }
            view2 = new TextSettingsCell(this.mContext);
            view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            view = view2;
            return new RecyclerListView.Holder(view);
        }

        private int getUsersCount(ArrayList<Integer> arrayList) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                int intValue = arrayList.get(i2).intValue();
                if (intValue > 0) {
                    i++;
                } else {
                    TLRPC.Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Integer.valueOf(-intValue));
                    if (chat != null) {
                        i += chat.participants_count;
                    }
                }
            }
            return i;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:149:0x0381, code lost:
            if (org.telegram.ui.PrivacyControlActivity.access$1900(r10.this$0) == 2) goto L_0x0391;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                r1 = -1
                r2 = 3
                r3 = 0
                r4 = 1
                if (r0 == 0) goto L_0x03b5
                r5 = 4
                r6 = 5
                r7 = 6
                r8 = 2
                if (r0 == r4) goto L_0x021f
                if (r0 == r8) goto L_0x013f
                if (r0 == r2) goto L_0x0016
                goto L_0x049e
            L_0x0016:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.RadioCell r11 = (org.telegram.ui.Cells.RadioCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.everybodyRow
                r5 = 2131625437(0x7f0e05dd, float:1.8878082E38)
                java.lang.String r6 = "LastSeenEverybody"
                if (r12 == r0) goto L_0x0072
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.myContactsRow
                if (r12 == r0) goto L_0x0072
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.nobodyRow
                if (r12 != r0) goto L_0x0038
                goto L_0x0072
            L_0x0038:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneContactsRow
                if (r12 != r0) goto L_0x0058
                r12 = 2131625431(0x7f0e05d7, float:1.887807E38)
                java.lang.String r0 = "LastSeenContacts"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != r4) goto L_0x0052
                goto L_0x0053
            L_0x0052:
                r4 = 0
            L_0x0053:
                r11.setText(r12, r4, r3)
                goto L_0x049e
            L_0x0058:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneEverybodyRow
                if (r12 != r0) goto L_0x049e
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != 0) goto L_0x006d
                r3 = 1
            L_0x006d:
                r11.setText(r12, r3, r4)
                goto L_0x049e
            L_0x0072:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.everybodyRow
                if (r12 != r0) goto L_0x00ab
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x0099
                r12 = 2131625933(0x7f0e07cd, float:1.8879088E38)
                java.lang.String r0 = "P2PEverybody"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x0094
                r3 = 1
            L_0x0094:
                r11.setText(r12, r3, r4)
                goto L_0x049e
            L_0x0099:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != 0) goto L_0x00a6
                r3 = 1
            L_0x00a6:
                r11.setText(r12, r3, r4)
                goto L_0x049e
            L_0x00ab:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.myContactsRow
                if (r12 != r0) goto L_0x00ff
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x00dd
                r12 = 2131625928(0x7f0e07c8, float:1.8879078E38)
                java.lang.String r0 = "P2PContacts"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r8) goto L_0x00ce
                r0 = 1
                goto L_0x00cf
            L_0x00ce:
                r0 = 0
            L_0x00cf:
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.nobodyRow
                if (r2 == r1) goto L_0x00d8
                r3 = 1
            L_0x00d8:
                r11.setText(r12, r0, r3)
                goto L_0x049e
            L_0x00dd:
                r12 = 2131625431(0x7f0e05d7, float:1.887807E38)
                java.lang.String r0 = "LastSeenContacts"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r8) goto L_0x00f0
                r0 = 1
                goto L_0x00f1
            L_0x00f0:
                r0 = 0
            L_0x00f1:
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.nobodyRow
                if (r2 == r1) goto L_0x00fa
                r3 = 1
            L_0x00fa:
                r11.setText(r12, r0, r3)
                goto L_0x049e
            L_0x00ff:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.nobodyRow
                if (r12 != r0) goto L_0x049e
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x0127
                r12 = 2131625935(0x7f0e07cf, float:1.8879092E38)
                java.lang.String r0 = "P2PNobody"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r4) goto L_0x0121
                goto L_0x0122
            L_0x0121:
                r4 = 0
            L_0x0122:
                r11.setText(r12, r4, r3)
                goto L_0x049e
            L_0x0127:
                r12 = 2131625440(0x7f0e05e0, float:1.8878088E38)
                java.lang.String r0 = "LastSeenNobody"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentType
                if (r0 != r4) goto L_0x0139
                goto L_0x013a
            L_0x0139:
                r4 = 0
            L_0x013a:
                r11.setText(r12, r4, r3)
                goto L_0x049e
            L_0x013f:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.HeaderCell r11 = (org.telegram.ui.Cells.HeaderCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.sectionRow
                if (r12 != r0) goto L_0x01dd
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r7) goto L_0x0161
                r12 = 2131626330(0x7f0e095a, float:1.8879893E38)
                java.lang.String r0 = "PrivacyPhoneTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x0161:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r6) goto L_0x0177
                r12 = 2131626317(0x7f0e094d, float:1.8879867E38)
                java.lang.String r0 = "PrivacyForwardsTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x0177:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r5) goto L_0x018d
                r12 = 2131626338(0x7f0e0962, float:1.887991E38)
                java.lang.String r0 = "PrivacyProfilePhotoTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x018d:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x01a3
                r12 = 2131625932(0x7f0e07cc, float:1.8879086E38)
                java.lang.String r0 = "P2PEnabledWith"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x01a3:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x01b9
                r12 = 2131627280(0x7f0e0d10, float:1.888182E38)
                java.lang.String r0 = "WhoCanCallMe"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x01b9:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r4) goto L_0x01cf
                r12 = 2131627275(0x7f0e0d0b, float:1.888181E38)
                java.lang.String r0 = "WhoCanAddMe"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x01cf:
                r12 = 2131625442(0x7f0e05e2, float:1.8878092E38)
                java.lang.String r0 = "LastSeenTitle"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x01dd:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.shareSectionRow
                if (r12 != r0) goto L_0x01f3
                r12 = 2131624115(0x7f0e00b3, float:1.88754E38)
                java.lang.String r0 = "AddExceptions"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x01f3:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pSectionRow
                if (r12 != r0) goto L_0x0209
                r12 = 2131626321(0x7f0e0951, float:1.8879875E38)
                java.lang.String r0 = "PrivacyP2PHeader"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x0209:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.phoneSectionRow
                if (r12 != r0) goto L_0x049e
                r12 = 2131626331(0x7f0e095b, float:1.8879895E38)
                java.lang.String r0 = "PrivacyPhoneTitle2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x049e
            L_0x021f:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r11 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.detailRow
                r1 = 2131165409(0x7var_e1, float:1.7945034E38)
                r9 = 2131165410(0x7var_e2, float:1.7945036E38)
                if (r12 != r0) goto L_0x02e8
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r7) goto L_0x026c
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r12.currentType
                if (r0 != r4) goto L_0x024a
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.currentSubType
                if (r0 != r4) goto L_0x024a
                r3 = 1
            L_0x024a:
                boolean r12 = r12.prevSubtypeContacts = r3
                if (r12 == 0) goto L_0x025e
                r12 = 2131626329(0x7f0e0959, float:1.8879891E38)
                java.lang.String r0 = "PrivacyPhoneInfo3"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x025e:
                r12 = 2131626327(0x7f0e0957, float:1.8879887E38)
                java.lang.String r0 = "PrivacyPhoneInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x026c:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r6) goto L_0x0282
                r12 = 2131626313(0x7f0e0949, float:1.8879859E38)
                java.lang.String r0 = "PrivacyForwardsInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x0282:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r5) goto L_0x0298
                r12 = 2131626336(0x7f0e0960, float:1.8879905E38)
                java.lang.String r0 = "PrivacyProfilePhotoInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x0298:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x02ae
                r12 = 2131626304(0x7f0e0940, float:1.887984E38)
                java.lang.String r0 = "PrivacyCallsP2PHelp"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x02ae:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x02c4
                r12 = 2131627281(0x7f0e0d11, float:1.8881822E38)
                java.lang.String r0 = "WhoCanCallMeInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x02c4:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r4) goto L_0x02da
                r12 = 2131627276(0x7f0e0d0c, float:1.8881812E38)
                java.lang.String r0 = "WhoCanAddMeInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x02da:
                r12 = 2131624814(0x7f0e036e, float:1.8876818E38)
                java.lang.String r0 = "CustomHelp"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0391
            L_0x02e8:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.shareDetailRow
                if (r12 != r0) goto L_0x0384
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r7) goto L_0x0306
                r12 = 2131626328(0x7f0e0958, float:1.887989E38)
                java.lang.String r0 = "PrivacyPhoneInfo2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x037b
            L_0x0306:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r6) goto L_0x031b
                r12 = 2131626314(0x7f0e094a, float:1.887986E38)
                java.lang.String r0 = "PrivacyForwardsInfo2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x037b
            L_0x031b:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r5) goto L_0x0330
                r12 = 2131626337(0x7f0e0961, float:1.8879907E38)
                java.lang.String r0 = "PrivacyProfilePhotoInfo2"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x037b
            L_0x0330:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r2) goto L_0x0345
                r12 = 2131624816(0x7f0e0370, float:1.8876822E38)
                java.lang.String r0 = "CustomP2PInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x037b
            L_0x0345:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x035a
                r12 = 2131624813(0x7f0e036d, float:1.8876816E38)
                java.lang.String r0 = "CustomCallInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x037b
            L_0x035a:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r4) goto L_0x036f
                r12 = 2131624817(0x7f0e0371, float:1.8876824E38)
                java.lang.String r0 = "CustomShareInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x037b
            L_0x036f:
                r12 = 2131624818(0x7f0e0372, float:1.8876826E38)
                java.lang.String r0 = "CustomShareSettingsHelp"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
            L_0x037b:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.rulesType
                if (r12 != r8) goto L_0x038c
                goto L_0x0391
            L_0x0384:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pDetailRow
                if (r12 != r0) goto L_0x0390
            L_0x038c:
                r1 = 2131165410(0x7var_e2, float:1.7945036E38)
                goto L_0x0391
            L_0x0390:
                r1 = 0
            L_0x0391:
                if (r1 == 0) goto L_0x049e
                android.content.Context r12 = r10.mContext
                java.lang.String r0 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r1, (java.lang.String) r0)
                org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
                java.lang.String r2 = "windowBackgroundGray"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.<init>(r2)
                r0.<init>(r1, r12)
                r0.setFullsize(r4)
                r11.setBackgroundDrawable(r0)
                goto L_0x049e
            L_0x03b5:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextSettingsCell r11 = (org.telegram.ui.Cells.TextSettingsCell) r11
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.alwaysShareRow
                java.lang.String r5 = "Users"
                r6 = 2131625017(0x7f0e0439, float:1.887723E38)
                java.lang.String r7 = "EmpryUsersPlaceholder"
                if (r12 != r0) goto L_0x041d
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentPlus
                int r12 = r12.size()
                if (r12 == 0) goto L_0x03e3
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentPlus
                int r12 = r10.getUsersCount(r12)
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12)
                goto L_0x03e7
            L_0x03e3:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r7, r6)
            L_0x03e7:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.rulesType
                if (r0 == 0) goto L_0x0406
                r0 = 2131624159(0x7f0e00df, float:1.887549E38)
                java.lang.String r2 = "AlwaysAllow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.neverShareRow
                if (r2 == r1) goto L_0x0401
                r3 = 1
            L_0x0401:
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x049e
            L_0x0406:
                r0 = 2131624160(0x7f0e00e0, float:1.8875492E38)
                java.lang.String r2 = "AlwaysShareWith"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
                org.telegram.ui.PrivacyControlActivity r2 = org.telegram.ui.PrivacyControlActivity.this
                int r2 = r2.neverShareRow
                if (r2 == r1) goto L_0x0418
                r3 = 1
            L_0x0418:
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x049e
            L_0x041d:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.neverShareRow
                if (r12 != r0) goto L_0x0466
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentMinus
                int r12 = r12.size()
                if (r12 == 0) goto L_0x0440
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                java.util.ArrayList r12 = r12.currentMinus
                int r12 = r10.getUsersCount(r12)
                java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r5, r12)
                goto L_0x0444
            L_0x0440:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r7, r6)
            L_0x0444:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.rulesType
                if (r0 == 0) goto L_0x0459
                r0 = 2131625653(0x7f0e06b5, float:1.887852E38)
                java.lang.String r1 = "NeverAllow"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x049e
            L_0x0459:
                r0 = 2131625654(0x7f0e06b6, float:1.8878522E38)
                java.lang.String r1 = "NeverShareWith"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x049e
            L_0x0466:
                org.telegram.ui.PrivacyControlActivity r0 = org.telegram.ui.PrivacyControlActivity.this
                int r0 = r0.p2pRow
                if (r12 != r0) goto L_0x049e
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.ContactsController r12 = org.telegram.messenger.ContactsController.getInstance(r12)
                boolean r12 = r12.getLoadingPrivicyInfo(r2)
                if (r12 == 0) goto L_0x0488
                r12 = 2131625478(0x7f0e0606, float:1.8878165E38)
                java.lang.String r0 = "Loading"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x0492
            L_0x0488:
                org.telegram.ui.PrivacyControlActivity r12 = org.telegram.ui.PrivacyControlActivity.this
                org.telegram.messenger.AccountInstance r12 = r12.getAccountInstance()
                java.lang.String r12 = org.telegram.ui.PrivacySettingsActivity.formatRulesString(r12, r2)
            L_0x0492:
                r0 = 2131626320(0x7f0e0950, float:1.8879873E38)
                java.lang.String r1 = "PrivacyP2P2"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
            L_0x049e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.p2pRow) {
                return 0;
            }
            if (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow || i == PrivacyControlActivity.this.p2pDetailRow) {
                return 1;
            }
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow || i == PrivacyControlActivity.this.p2pSectionRow || i == PrivacyControlActivity.this.phoneSectionRow) {
                return 2;
            }
            if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.phoneEverybodyRow || i == PrivacyControlActivity.this.phoneContactsRow) {
                return 3;
            }
            if (i == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            if (i == PrivacyControlActivity.this.phoneDetailRow) {
                return 5;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText")};
    }
}
