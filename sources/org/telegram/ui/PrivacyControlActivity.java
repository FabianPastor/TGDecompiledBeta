package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
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
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateActivity;

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
    public ArrayList<Long> currentMinus;
    /* access modifiers changed from: private */
    public ArrayList<Long> currentPlus;
    /* access modifiers changed from: private */
    public int currentSubType;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int detailRow;
    private View doneButton;
    /* access modifiers changed from: private */
    public int everybodyRow;
    private ArrayList<Long> initialMinus;
    private ArrayList<Long> initialPlus;
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
        private final Runnable invalidateRunnable = new PrivacyControlActivity$MessageCell$$ExternalSyntheticLambda0(this);
        /* access modifiers changed from: private */
        public MessageObject messageObject;
        private Drawable shadowDrawable;
        final /* synthetic */ PrivacyControlActivity this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public MessageCell(org.telegram.ui.PrivacyControlActivity r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.this$0 = r1
                r0.<init>(r2)
                org.telegram.ui.PrivacyControlActivity$MessageCell$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.PrivacyControlActivity$MessageCell$$ExternalSyntheticLambda0
                r3.<init>(r0)
                r0.invalidateRunnable = r3
                r3 = 0
                r0.setWillNotDraw(r3)
                r0.setClipToPadding(r3)
                r4 = 2131165484(0x7var_c, float:1.7945186E38)
                java.lang.String r5 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r4, (java.lang.String) r5)
                r0.shadowDrawable = r4
                r4 = 1093664768(0x41300000, float:11.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r0.setPadding(r3, r5, r3, r4)
                long r4 = java.lang.System.currentTimeMillis()
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 / r6
                int r5 = (int) r4
                int r5 = r5 + -3600
                int r4 = r18.currentAccount
                org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                int r6 = r18.currentAccount
                org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
                long r6 = r6.getClientUserId()
                java.lang.Long r6 = java.lang.Long.valueOf(r6)
                org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r6)
                org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
                r6.<init>()
                java.lang.String r7 = "PrivacyForwardsMessageLine"
                r8 = 2131627516(0x7f0e0dfc, float:1.8882299E38)
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)
                r6.message = r7
                int r7 = r5 + 60
                r6.date = r7
                r7 = 1
                r6.dialog_id = r7
                r9 = 261(0x105, float:3.66E-43)
                r6.flags = r9
                org.telegram.tgnet.TLRPC$TL_peerUser r9 = new org.telegram.tgnet.TLRPC$TL_peerUser
                r9.<init>()
                r6.from_id = r9
                r9 = 1
                r6.id = r9
                org.telegram.tgnet.TLRPC$TL_messageFwdHeader r10 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
                r10.<init>()
                r6.fwd_from = r10
                org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r6.fwd_from
                java.lang.String r11 = r4.first_name
                java.lang.String r12 = r4.last_name
                java.lang.String r11 = org.telegram.messenger.ContactsController.formatName(r11, r12)
                r10.from_name = r11
                org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r10 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
                r10.<init>()
                r6.media = r10
                r6.out = r3
                org.telegram.tgnet.TLRPC$TL_peerUser r10 = new org.telegram.tgnet.TLRPC$TL_peerUser
                r10.<init>()
                r6.peer_id = r10
                org.telegram.tgnet.TLRPC$Peer r10 = r6.peer_id
                int r11 = r18.currentAccount
                org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
                long r11 = r11.getClientUserId()
                r10.user_id = r11
                org.telegram.messenger.MessageObject r10 = new org.telegram.messenger.MessageObject
                int r11 = r18.currentAccount
                r10.<init>(r11, r6, r9, r3)
                r0.messageObject = r10
                r10.eventId = r7
                org.telegram.messenger.MessageObject r7 = r0.messageObject
                r7.resetLayout()
                org.telegram.ui.Cells.ChatMessageCell r7 = new org.telegram.ui.Cells.ChatMessageCell
                r7.<init>(r2)
                r0.cell = r7
                org.telegram.ui.PrivacyControlActivity$MessageCell$1 r8 = new org.telegram.ui.PrivacyControlActivity$MessageCell$1
                r8.<init>(r1)
                r7.setDelegate(r8)
                org.telegram.ui.Cells.ChatMessageCell r1 = r0.cell
                r1.isChat = r3
                org.telegram.ui.Cells.ChatMessageCell r1 = r0.cell
                r1.setFullyDraw(r9)
                org.telegram.ui.Cells.ChatMessageCell r1 = r0.cell
                org.telegram.messenger.MessageObject r7 = r0.messageObject
                r8 = 0
                r1.setMessageObject(r7, r8, r3, r3)
                org.telegram.ui.Cells.ChatMessageCell r1 = r0.cell
                r3 = -1
                r7 = -2
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r7)
                r0.addView(r1, r3)
                org.telegram.ui.Components.HintView r1 = new org.telegram.ui.Components.HintView
                r1.<init>((android.content.Context) r2, (int) r9, (boolean) r9)
                r0.hintView = r1
                r10 = -2
                r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r12 = 51
                r13 = 1100480512(0x41980000, float:19.0)
                r14 = 0
                r15 = 1100480512(0x41980000, float:19.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r1, r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.MessageCell.<init>(org.telegram.ui.PrivacyControlActivity, android.content.Context):void");
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
            if (!(newDrawable == null || this.backgroundDrawable == newDrawable)) {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                this.backgroundDrawable = newDrawable;
            }
            Drawable drawable = this.backgroundDrawable;
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof BackgroundGradientDrawable) {
                    this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable2).drawExactBoundsSize(canvas, this);
                } else {
                    drawable2.draw(canvas);
                }
            } else if (drawable instanceof BitmapDrawable) {
                if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                    canvas.save();
                    float scale = 2.0f / AndroidUtilities.density;
                    canvas.scale(scale, scale);
                    this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / scale)));
                } else {
                    int viewHeight = getMeasuredHeight();
                    float scale2 = Math.max(((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth()), ((float) viewHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight()));
                    int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale2));
                    int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale2));
                    int x = (getMeasuredWidth() - width) / 2;
                    int y = (viewHeight - height) / 2;
                    canvas.save();
                    canvas.clipRect(0, 0, width, getMeasuredHeight());
                    this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                }
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                super.onDraw(canvas);
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

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean pressed) {
        }

        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }
    }

    public PrivacyControlActivity(int type) {
        this(type, false);
    }

    public PrivacyControlActivity(int type, boolean load) {
        this.initialPlus = new ArrayList<>();
        this.initialMinus = new ArrayList<>();
        this.rulesType = type;
        if (load) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public View createView(Context context) {
        if (this.rulesType == 5) {
            this.messageCell = new MessageCell(this, context);
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
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PrivacyControlActivity.this.checkDiscard()) {
                        PrivacyControlActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    PrivacyControlActivity.this.processDone();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
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
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PrivacyControlActivity$$ExternalSyntheticLambda7(this));
        setMessageText();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3012lambda$createView$2$orgtelegramuiPrivacyControlActivity(View view, int position) {
        int newType;
        int newType2;
        ArrayList<Long> createFromArray;
        int i = this.nobodyRow;
        boolean z = true;
        if (position == i || position == this.everybodyRow || position == this.myContactsRow) {
            if (position == i) {
                newType = 1;
            } else if (position == this.everybodyRow) {
                newType = 0;
            } else {
                newType = 2;
            }
            if (newType != this.currentType) {
                this.currentType = newType;
                updateDoneButton();
                updateRows(true);
            }
        } else if (position == this.phoneContactsRow || position == this.phoneEverybodyRow) {
            if (position == this.phoneEverybodyRow) {
                newType2 = 0;
            } else {
                newType2 = 1;
            }
            if (newType2 != this.currentSubType) {
                this.currentSubType = newType2;
                updateDoneButton();
                updateRows(true);
            }
        } else {
            int i2 = this.neverShareRow;
            if (position == i2 || position == this.alwaysShareRow) {
                if (position == i2) {
                    createFromArray = this.currentMinus;
                } else {
                    createFromArray = this.currentPlus;
                }
                if (createFromArray.isEmpty()) {
                    Bundle args = new Bundle();
                    args.putBoolean(position == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                    if (this.rulesType == 0) {
                        z = false;
                    }
                    args.putInt("chatAddType", z ? 1 : 0);
                    GroupCreateActivity fragment = new GroupCreateActivity(args);
                    fragment.setDelegate((GroupCreateActivity.GroupCreateActivityDelegate) new PrivacyControlActivity$$ExternalSyntheticLambda8(this, position));
                    presentFragment(fragment);
                    return;
                }
                boolean z2 = this.rulesType != 0;
                if (position != this.alwaysShareRow) {
                    z = false;
                }
                PrivacyUsersActivity fragment2 = new PrivacyUsersActivity(0, createFromArray, z2, z);
                fragment2.setDelegate(new PrivacyControlActivity$$ExternalSyntheticLambda9(this, position));
                presentFragment(fragment2);
            } else if (position == this.p2pRow) {
                presentFragment(new PrivacyControlActivity(3));
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3010lambda$createView$0$orgtelegramuiPrivacyControlActivity(int position, ArrayList ids) {
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            for (int a = 0; a < this.currentMinus.size(); a++) {
                this.currentPlus.remove(this.currentMinus.get(a));
            }
        } else {
            this.currentPlus = ids;
            for (int a2 = 0; a2 < this.currentPlus.size(); a2++) {
                this.currentMinus.remove(this.currentPlus.get(a2));
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3011lambda$createView$1$orgtelegramuiPrivacyControlActivity(int position, ArrayList ids, boolean added) {
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            if (added) {
                for (int a = 0; a < this.currentMinus.size(); a++) {
                    this.currentPlus.remove(this.currentMinus.get(a));
                }
            }
        } else {
            this.currentPlus = ids;
            if (added) {
                for (int a2 = 0; a2 < this.currentPlus.size(); a2++) {
                    this.currentMinus.remove(this.currentPlus.get(a2));
                }
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        MessageCell messageCell2;
        if (id == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (id == NotificationCenter.emojiLoaded) {
            this.listView.invalidateViews();
        } else if (id == NotificationCenter.didSetNewWallpapper && (messageCell2 = this.messageCell) != null) {
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
        TLRPC.TL_account_setPrivacy req = new TLRPC.TL_account_setPrivacy();
        int i = this.rulesType;
        if (i == 6) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TLRPC.TL_account_setPrivacy req2 = new TLRPC.TL_account_setPrivacy();
                req2.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
                } else {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new PrivacyControlActivity$$ExternalSyntheticLambda5(this), 2);
            }
        } else if (i == 5) {
            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
        } else if (i == 4) {
            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
        } else if (i == 3) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
        } else if (i == 2) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
        } else if (i == 1) {
            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
        } else {
            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TLRPC.TL_inputPrivacyValueAllowUsers usersRule = new TLRPC.TL_inputPrivacyValueAllowUsers();
            TLRPC.TL_inputPrivacyValueAllowChatParticipants chatsRule = new TLRPC.TL_inputPrivacyValueAllowChatParticipants();
            for (int a = 0; a < this.currentPlus.size(); a++) {
                long id = this.currentPlus.get(a).longValue();
                if (DialogObject.isUserDialog(id)) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(id));
                    if (!(user == null || (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) == null)) {
                        usersRule.users.add(inputUser2);
                    }
                } else {
                    chatsRule.chats.add(Long.valueOf(-id));
                }
            }
            req.rules.add(usersRule);
            req.rules.add(chatsRule);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TLRPC.TL_inputPrivacyValueDisallowUsers usersRule2 = new TLRPC.TL_inputPrivacyValueDisallowUsers();
            TLRPC.TL_inputPrivacyValueDisallowChatParticipants chatsRule2 = new TLRPC.TL_inputPrivacyValueDisallowChatParticipants();
            for (int a2 = 0; a2 < this.currentMinus.size(); a2++) {
                long id2 = this.currentMinus.get(a2).longValue();
                if (DialogObject.isUserDialog(id2)) {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(id2));
                    if (!(user2 == null || (inputUser = getMessagesController().getInputUser(user2)) == null)) {
                        usersRule2.users.add(inputUser);
                    }
                } else {
                    chatsRule2.chats.add(Long.valueOf(-id2));
                }
            }
            req.rules.add(usersRule2);
            req.rules.add(chatsRule2);
        }
        int i2 = this.currentType;
        if (i2 == 0) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
        } else if (i2 == 1) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
        } else if (i2 == 2) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PrivacyControlActivity$$ExternalSyntheticLambda6(this, progressDialog), 2);
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$4$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3005xd2CLASSNAMEb(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacyControlActivity$$ExternalSyntheticLambda3(this, error, response));
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$3$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3004xb60e266c(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TLRPC.TL_account_privacyRules) response).rules, 7);
        }
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$6$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3007xbb67var_(AlertDialog progressDialogFinal, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PrivacyControlActivity$$ExternalSyntheticLambda4(this, progressDialogFinal, error, response));
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$5$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3006x644a082a(AlertDialog progressDialogFinal, TLRPC.TL_error error, TLObject response) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (error == null) {
            TLRPC.TL_account_privacyRules privacyRules = (TLRPC.TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyRules.rules, this.rulesType);
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
            int type = -1;
            for (int a = 0; a < privacyRules.size(); a++) {
                TLRPC.PrivacyRule rule = privacyRules.get(a);
                if (rule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants privacyValueAllowChatParticipants = (TLRPC.TL_privacyValueAllowChatParticipants) rule;
                    int N = privacyValueAllowChatParticipants.chats.size();
                    for (int b = 0; b < N; b++) {
                        this.currentPlus.add(Long.valueOf(-privacyValueAllowChatParticipants.chats.get(b).longValue()));
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants privacyValueDisallowChatParticipants = (TLRPC.TL_privacyValueDisallowChatParticipants) rule;
                    int N2 = privacyValueDisallowChatParticipants.chats.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        this.currentMinus.add(Long.valueOf(-privacyValueDisallowChatParticipants.chats.get(b2).longValue()));
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueAllowUsers) {
                    this.currentPlus.addAll(((TLRPC.TL_privacyValueAllowUsers) rule).users);
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                    this.currentMinus.addAll(((TLRPC.TL_privacyValueDisallowUsers) rule).users);
                } else if (type == -1) {
                    if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                        type = 0;
                    } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }
            if (type == 0 || (type == -1 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (type == 2 || (type == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (type == 1 || (type == -1 && this.currentPlus.size() > 0)) {
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
                int a2 = 0;
                while (true) {
                    if (a2 >= privacyRules2.size()) {
                        break;
                    }
                    TLRPC.PrivacyRule rule2 = privacyRules2.get(a2);
                    if (rule2 instanceof TLRPC.TL_privacyValueAllowAll) {
                        this.currentSubType = 0;
                        break;
                    } else if (rule2 instanceof TLRPC.TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else if (rule2 instanceof TLRPC.TL_privacyValueAllowContacts) {
                        this.currentSubType = 1;
                        break;
                    } else {
                        a2++;
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

    private void updateRows(boolean animated) {
        RecyclerView.ViewHolder holder;
        int checkedType;
        int checkedType2;
        int prevAlwaysShareRow = this.alwaysShareRow;
        int prevNeverShareRow = this.neverShareRow;
        int prevPhoneDetailRow = this.phoneDetailRow;
        int prevDetailRow = this.detailRow;
        int i = this.currentType;
        boolean newSubtype = i == 1 && this.currentSubType == 1;
        this.rowCount = 0;
        int i2 = this.rulesType;
        if (i2 == 5) {
            this.rowCount = 0 + 1;
            this.messageRow = 0;
        } else {
            this.messageRow = -1;
        }
        int i3 = this.rowCount;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.sectionRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.everybodyRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.myContactsRow = i5;
        if (i2 == 0 || i2 == 2 || i2 == 3 || i2 == 5 || i2 == 6) {
            this.rowCount = i6 + 1;
            this.nobodyRow = i6;
        } else {
            this.nobodyRow = -1;
        }
        if (i2 == 6 && i == 1) {
            int i7 = this.rowCount;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.phoneDetailRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.phoneSectionRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.phoneEverybodyRow = i9;
            this.rowCount = i10 + 1;
            this.phoneContactsRow = i10;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        int i11 = this.rowCount;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.detailRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.shareSectionRow = i12;
        if (i == 1 || i == 2) {
            this.rowCount = i13 + 1;
            this.alwaysShareRow = i13;
        } else {
            this.alwaysShareRow = -1;
        }
        if (i == 0 || i == 2) {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.neverShareRow = i14;
        } else {
            this.neverShareRow = -1;
        }
        int i15 = this.rowCount;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.shareDetailRow = i15;
        if (i2 == 2) {
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.p2pSectionRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.p2pRow = i17;
            this.rowCount = i18 + 1;
            this.p2pDetailRow = i18;
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
        if (animated) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if ((child instanceof RadioCell) && (holder = this.listView.findContainingViewHolder(child)) != null) {
                    int position = holder.getAdapterPosition();
                    RadioCell radioCell = (RadioCell) child;
                    int i19 = this.everybodyRow;
                    if (position == i19 || position == this.myContactsRow || position == this.nobodyRow) {
                        if (position == i19) {
                            checkedType = 0;
                        } else if (position == this.myContactsRow) {
                            checkedType = 2;
                        } else {
                            checkedType = 1;
                        }
                        radioCell.setChecked(this.currentType == checkedType, true);
                    } else {
                        if (position == this.phoneContactsRow) {
                            checkedType2 = 1;
                        } else {
                            checkedType2 = 0;
                        }
                        radioCell.setChecked(this.currentSubType == checkedType2, true);
                    }
                }
            }
            if (this.prevSubtypeContacts != newSubtype) {
                this.listAdapter.notifyItemChanged(prevDetailRow);
            }
            int i20 = this.alwaysShareRow;
            if ((i20 != -1 || prevAlwaysShareRow == -1 || this.neverShareRow == -1 || prevNeverShareRow != -1) && (i20 == -1 || prevAlwaysShareRow != -1 || this.neverShareRow != -1 || prevNeverShareRow == -1)) {
                if (i20 == -1 && prevAlwaysShareRow != -1) {
                    this.listAdapter.notifyItemRemoved(prevAlwaysShareRow);
                } else if (i20 != -1 && prevAlwaysShareRow == -1) {
                    this.listAdapter.notifyItemInserted(i20);
                }
                int i21 = this.neverShareRow;
                if (i21 == -1 && prevNeverShareRow != -1) {
                    this.listAdapter.notifyItemRemoved(prevNeverShareRow);
                    int i22 = this.phoneDetailRow;
                    if (i22 == -1 && prevPhoneDetailRow != -1) {
                        this.listAdapter.notifyItemRangeRemoved(prevPhoneDetailRow, 4);
                    } else if (i22 != -1 && prevPhoneDetailRow == -1) {
                        this.listAdapter.notifyItemRangeInserted(i22, 4);
                    }
                } else if (i21 != -1 && prevNeverShareRow == -1) {
                    int i23 = this.phoneDetailRow;
                    if (i23 == -1 && prevPhoneDetailRow != -1) {
                        this.listAdapter.notifyItemRangeRemoved(prevPhoneDetailRow, 4);
                    } else if (i23 != -1 && prevPhoneDetailRow == -1) {
                        this.listAdapter.notifyItemRangeInserted(i23, 4);
                    }
                    this.listAdapter.notifyItemInserted(this.neverShareRow);
                }
            } else {
                this.listAdapter.notifyItemChanged(i20 == -1 ? prevAlwaysShareRow : prevNeverShareRow);
                int i24 = this.phoneDetailRow;
                if (i24 == -1 && prevPhoneDetailRow != -1) {
                    this.listAdapter.notifyItemRangeRemoved(prevPhoneDetailRow, 4);
                } else if (i24 != -1 && prevPhoneDetailRow == -1) {
                    this.listAdapter.notifyItemRangeInserted(i24, 4);
                }
            }
        } else {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void setMessageText() {
        MessageCell messageCell2 = this.messageCell;
        if (messageCell2 != null) {
            messageCell2.messageObject.messageOwner.fwd_from.from_id = new TLRPC.TL_peerUser();
            int i = this.currentType;
            if (i == 0) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 1;
            } else if (i == 1) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 0;
            } else {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", NUM));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 1;
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
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (!preferences.getBoolean("privacyAlertShowed", false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.rulesType == 1) {
                        builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("CustomHelp", NUM));
                    }
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new PrivacyControlActivity$$ExternalSyntheticLambda2(this, preferences));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                    return;
                }
            }
            applyCurrentPrivacySettings();
        }
    }

    /* renamed from: lambda$processDone$7$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3013lambda$processDone$7$orgtelegramuiPrivacyControlActivity(SharedPreferences preferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        preferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        if (this.doneButton.getAlpha() != 1.0f) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", NUM));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new PrivacyControlActivity$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new PrivacyControlActivity$$ExternalSyntheticLambda1(this));
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$8$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3008lambda$checkDiscard$8$orgtelegramuiPrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$9$org-telegram-ui-PrivacyControlActivity  reason: not valid java name */
    public /* synthetic */ void m3009lambda$checkDiscard$9$orgtelegramuiPrivacyControlActivity(DialogInterface dialog, int which) {
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.alwaysShareRow || (position == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3));
        }

        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 3:
                    View view4 = new RadioCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view4;
                    break;
                case 4:
                    view = PrivacyControlActivity.this.messageCell;
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        private int getUsersCount(ArrayList<Long> arrayList) {
            int count = 0;
            for (int a = 0; a < arrayList.size(); a++) {
                long id = arrayList.get(a).longValue();
                if (id > 0) {
                    count++;
                } else {
                    TLRPC.Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Long.valueOf(-id));
                    if (chat != null) {
                        count += chat.participants_count;
                    }
                }
            }
            return count;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            String value3;
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == PrivacyControlActivity.this.alwaysShareRow) {
                        if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                            value3 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentPlus));
                        } else {
                            value3 = LocaleController.getString("EmpryUsersPlaceholder", NUM);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            String string = LocaleController.getString("AlwaysAllow", NUM);
                            if (PrivacyControlActivity.this.neverShareRow != -1) {
                                z = true;
                            }
                            textCell.setTextAndValue(string, value3, z);
                            return;
                        }
                        String string2 = LocaleController.getString("AlwaysShareWith", NUM);
                        if (PrivacyControlActivity.this.neverShareRow != -1) {
                            z = true;
                        }
                        textCell.setTextAndValue(string2, value3, z);
                        return;
                    } else if (position == PrivacyControlActivity.this.neverShareRow) {
                        if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                            value2 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentMinus));
                        } else {
                            value2 = LocaleController.getString("EmpryUsersPlaceholder", NUM);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            textCell.setTextAndValue(LocaleController.getString("NeverAllow", NUM), value2, false);
                            return;
                        } else {
                            textCell.setTextAndValue(LocaleController.getString("NeverShareWith", NUM), value2, false);
                            return;
                        }
                    } else if (position == PrivacyControlActivity.this.p2pRow) {
                        if (ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3)) {
                            value = LocaleController.getString("Loading", NUM);
                        } else {
                            value = PrivacySettingsActivity.formatRulesString(PrivacyControlActivity.this.getAccountInstance(), 3);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyP2P2", NUM), value, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    int backgroundResId = 0;
                    if (position == PrivacyControlActivity.this.detailRow) {
                        if (PrivacyControlActivity.this.rulesType == 6) {
                            PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
                            if (privacyControlActivity.prevSubtypeContacts = privacyControlActivity.currentType == 1 && PrivacyControlActivity.this.currentSubType == 1) {
                                privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo3", NUM));
                            } else {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                final String phoneLinkStr = String.format(Locale.ENGLISH, "https://t.me/+%s", new Object[]{PrivacyControlActivity.this.getUserConfig().getClientPhone()});
                                SpannableString phoneLink = new SpannableString(phoneLinkStr);
                                phoneLink.setSpan(new ClickableSpan() {
                                    public void onClick(View view) {
                                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", phoneLinkStr));
                                        if (Build.VERSION.SDK_INT < 31) {
                                            BulletinFactory.of(PrivacyControlActivity.this).createCopyBulletin(LocaleController.getString("PhoneCopied", NUM)).show();
                                        }
                                    }
                                }, 0, phoneLinkStr.length(), 33);
                                spannableStringBuilder.append(LocaleController.getString("PrivacyPhoneInfo", NUM)).append("\n\n").append(LocaleController.getString("PrivacyPhoneInfo4", NUM)).append("\n").append(phoneLink);
                                privacyCell.setText(spannableStringBuilder);
                            }
                        } else if (PrivacyControlActivity.this.rulesType == 5) {
                            privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            privacyCell.setText(LocaleController.getString("PrivacyCallsP2PHelp", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            privacyCell.setText(LocaleController.getString("WhoCanCallMeInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            privacyCell.setText(LocaleController.getString("WhoCanAddMeInfo", NUM));
                        } else {
                            privacyCell.setText(LocaleController.getString("CustomHelp", NUM));
                        }
                        backgroundResId = NUM;
                    } else if (position == PrivacyControlActivity.this.shareDetailRow) {
                        if (PrivacyControlActivity.this.rulesType == 6) {
                            privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo2", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 5) {
                            privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo2", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo2", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            privacyCell.setText(LocaleController.getString("CustomP2PInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            privacyCell.setText(LocaleController.getString("CustomCallInfo", NUM));
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            privacyCell.setText(LocaleController.getString("CustomShareInfo", NUM));
                        } else {
                            privacyCell.setText(LocaleController.getString("CustomShareSettingsHelp", NUM));
                        }
                        if (PrivacyControlActivity.this.rulesType == 2) {
                            backgroundResId = NUM;
                        } else {
                            backgroundResId = NUM;
                        }
                    } else if (position == PrivacyControlActivity.this.p2pDetailRow) {
                        backgroundResId = NUM;
                    }
                    if (backgroundResId != 0) {
                        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, backgroundResId, "windowBackgroundGrayShadow"));
                        combinedDrawable.setFullsize(true);
                        privacyCell.setBackgroundDrawable(combinedDrawable);
                        return;
                    }
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == PrivacyControlActivity.this.sectionRow) {
                        if (PrivacyControlActivity.this.rulesType == 6) {
                            headerCell.setText(LocaleController.getString("PrivacyPhoneTitle", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 5) {
                            headerCell.setText(LocaleController.getString("PrivacyForwardsTitle", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 4) {
                            headerCell.setText(LocaleController.getString("PrivacyProfilePhotoTitle", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            headerCell.setText(LocaleController.getString("P2PEnabledWith", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 2) {
                            headerCell.setText(LocaleController.getString("WhoCanCallMe", NUM));
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 1) {
                            headerCell.setText(LocaleController.getString("WhoCanAddMe", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("LastSeenTitle", NUM));
                            return;
                        }
                    } else if (position == PrivacyControlActivity.this.shareSectionRow) {
                        headerCell.setText(LocaleController.getString("AddExceptions", NUM));
                        return;
                    } else if (position == PrivacyControlActivity.this.p2pSectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyP2PHeader", NUM));
                        return;
                    } else if (position == PrivacyControlActivity.this.phoneSectionRow) {
                        headerCell.setText(LocaleController.getString("PrivacyPhoneTitle2", NUM));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.nobodyRow) {
                        if (position == PrivacyControlActivity.this.everybodyRow) {
                            if (PrivacyControlActivity.this.rulesType == 3) {
                                String string3 = LocaleController.getString("P2PEverybody", NUM);
                                if (PrivacyControlActivity.this.currentType == 0) {
                                    z = true;
                                }
                                radioCell.setText(string3, z, true);
                                return;
                            }
                            String string4 = LocaleController.getString("LastSeenEverybody", NUM);
                            if (PrivacyControlActivity.this.currentType == 0) {
                                z = true;
                            }
                            radioCell.setText(string4, z, true);
                            return;
                        } else if (position == PrivacyControlActivity.this.myContactsRow) {
                            if (PrivacyControlActivity.this.rulesType == 3) {
                                String string5 = LocaleController.getString("P2PContacts", NUM);
                                boolean z3 = PrivacyControlActivity.this.currentType == 2;
                                if (PrivacyControlActivity.this.nobodyRow != -1) {
                                    z = true;
                                }
                                radioCell.setText(string5, z3, z);
                                return;
                            }
                            String string6 = LocaleController.getString("LastSeenContacts", NUM);
                            boolean z4 = PrivacyControlActivity.this.currentType == 2;
                            if (PrivacyControlActivity.this.nobodyRow != -1) {
                                z = true;
                            }
                            radioCell.setText(string6, z4, z);
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            String string7 = LocaleController.getString("P2PNobody", NUM);
                            if (PrivacyControlActivity.this.currentType != 1) {
                                z2 = false;
                            }
                            radioCell.setText(string7, z2, false);
                            return;
                        } else {
                            String string8 = LocaleController.getString("LastSeenNobody", NUM);
                            if (PrivacyControlActivity.this.currentType != 1) {
                                z2 = false;
                            }
                            radioCell.setText(string8, z2, false);
                            return;
                        }
                    } else if (position == PrivacyControlActivity.this.phoneContactsRow) {
                        String string9 = LocaleController.getString("LastSeenContacts", NUM);
                        if (PrivacyControlActivity.this.currentSubType != 1) {
                            z2 = false;
                        }
                        radioCell.setText(string9, z2, false);
                        return;
                    } else if (position == PrivacyControlActivity.this.phoneEverybodyRow) {
                        String string10 = LocaleController.getString("LastSeenEverybody", NUM);
                        if (PrivacyControlActivity.this.currentSubType == 0) {
                            z = true;
                        }
                        radioCell.setText(string10, z, true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacyControlActivity.this.alwaysShareRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.p2pRow) {
                return 0;
            }
            if (position == PrivacyControlActivity.this.shareDetailRow || position == PrivacyControlActivity.this.detailRow || position == PrivacyControlActivity.this.p2pDetailRow) {
                return 1;
            }
            if (position == PrivacyControlActivity.this.sectionRow || position == PrivacyControlActivity.this.shareSectionRow || position == PrivacyControlActivity.this.p2pSectionRow || position == PrivacyControlActivity.this.phoneSectionRow) {
                return 2;
            }
            if (position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.phoneEverybodyRow || position == PrivacyControlActivity.this.phoneContactsRow) {
                return 3;
            }
            if (position == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            if (position == PrivacyControlActivity.this.phoneDetailRow) {
                return 5;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        return themeDescriptions;
    }
}
