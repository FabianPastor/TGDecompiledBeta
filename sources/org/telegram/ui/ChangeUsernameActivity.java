package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_checkUsername;
import org.telegram.tgnet.TLRPC$TL_account_reorderUsernames;
import org.telegram.tgnet.TLRPC$TL_account_toggleUsername;
import org.telegram.tgnet.TLRPC$TL_account_updateUsername;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_username;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChangeUsernameActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class ChangeUsernameActivity extends BaseFragment {
    private Adapter adapter;
    private int checkReqId;
    private Runnable checkRunnable;
    private View doneButton;
    private UsernameCell editableUsernameCell;
    private UsernameHelpCell helpCell;
    private boolean ignoreCheck;
    private InputCell inputCell;
    private ItemTouchHelper itemTouchHelper;
    private String lastCheckName;
    private RecyclerListView listView;
    private boolean needReorder;
    private TextView statusTextView;
    private static Paint linkBackgroundActive = new Paint(1);
    private static Paint linkBackgroundInactive = new Paint(1);
    private static Paint dragPaint = new Paint(1);
    private String username = "";
    private ArrayList<TLRPC$TL_username> notEditableUsernames = new ArrayList<>();
    private ArrayList<TLRPC$TL_username> usernames = new ArrayList<>();
    private ArrayList<String> loadingUsernames = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    /* loaded from: classes3.dex */
    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String str) {
            this.url = str;
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }

        @Override // android.text.style.ClickableSpan
        public void onClick(View view) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                if (!BulletinFactory.canShowBulletin(ChangeUsernameActivity.this)) {
                    return;
                }
                BulletinFactory.createCopyLinkBulletin(ChangeUsernameActivity.this).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        String str;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", R.string.Username));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChangeUsernameActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    ChangeUsernameActivity.this.finishFragment();
                } else if (i != 1) {
                } else {
                    ChangeUsernameActivity.this.sendReorder();
                    ChangeUsernameActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            this.username = null;
            if (user.usernames != null) {
                int i = 0;
                while (true) {
                    if (i >= user.usernames.size()) {
                        break;
                    }
                    TLRPC$TL_username tLRPC$TL_username = user.usernames.get(i);
                    if (tLRPC$TL_username != null && tLRPC$TL_username.editable) {
                        this.username = tLRPC$TL_username.username;
                        break;
                    }
                    i++;
                }
            }
            if (this.username == null && (str = user.username) != null) {
                this.username = str;
            }
            if (this.username == null) {
                this.username = "";
            }
            this.notEditableUsernames.clear();
            this.usernames.clear();
            for (int i2 = 0; i2 < user.usernames.size(); i2++) {
                if (user.usernames.get(i2).active) {
                    this.usernames.add(user.usernames.get(i2));
                }
            }
            for (int i3 = 0; i3 < user.usernames.size(); i3++) {
                if (!user.usernames.get(i3).active) {
                    this.usernames.add(user.usernames.get(i3));
                }
            }
        }
        this.fragmentView = new FrameLayout(context);
        this.listView = new RecyclerListView(context) { // from class: org.telegram.ui.ChangeUsernameActivity.2
            private Paint backgroundPaint = new Paint(1);

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                int childAdapterPosition;
                int size = (ChangeUsernameActivity.this.usernames.size() + 4) - 1;
                int i4 = Integer.MAX_VALUE;
                int i5 = Integer.MIN_VALUE;
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt = getChildAt(i6);
                    if (childAt != null && (childAdapterPosition = getChildAdapterPosition(childAt)) >= 4 && childAdapterPosition <= size) {
                        i4 = Math.min(childAt.getTop(), i4);
                        i5 = Math.max(childAt.getBottom(), i5);
                    }
                }
                if (i4 < i5) {
                    this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite", this.resourcesProvider));
                    canvas.drawRect(0.0f, i4, getWidth(), i5, this.backgroundPaint);
                }
                super.dispatchDraw(canvas);
            }
        };
        this.fragmentView.setBackgroundColor(getThemedColor("windowBackgroundGray"));
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.listView);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView.setOnTouchListener(ChangeUsernameActivity$$ExternalSyntheticLambda1.INSTANCE);
        this.listView.setOnItemClickListener(new AnonymousClass3());
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChangeUsernameActivity.this.lambda$createView$1();
            }
        }, 40L);
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChangeUsernameActivity$3  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass3 implements RecyclerListView.OnItemClickListener {
        AnonymousClass3() {
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
        public void onItemClick(final View view, final int i) {
            int i2;
            String str;
            int i3;
            String str2;
            int i4;
            String str3;
            if (view instanceof UsernameCell) {
                UsernameCell usernameCell = (UsernameCell) view;
                final TLRPC$TL_username tLRPC$TL_username = usernameCell.currentUsername;
                if (tLRPC$TL_username == null || usernameCell.loading) {
                    return;
                }
                if (tLRPC$TL_username.editable) {
                    ChangeUsernameActivity.this.listView.smoothScrollToPosition(0);
                    ChangeUsernameActivity.this.focusUsernameField(true);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUsernameActivity.this.getContext(), ChangeUsernameActivity.this.getResourceProvider());
                if (tLRPC$TL_username.active) {
                    i2 = R.string.UsernameDeactivateLink;
                    str = "UsernameDeactivateLink";
                } else {
                    i2 = R.string.UsernameActivateLink;
                    str = "UsernameActivateLink";
                }
                AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i2));
                if (tLRPC$TL_username.active) {
                    i3 = R.string.UsernameDeactivateLinkProfileMessage;
                    str2 = "UsernameDeactivateLinkProfileMessage";
                } else {
                    i3 = R.string.UsernameActivateLinkProfileMessage;
                    str2 = "UsernameActivateLinkProfileMessage";
                }
                AlertDialog.Builder message = title.setMessage(LocaleController.getString(str2, i3));
                if (tLRPC$TL_username.active) {
                    i4 = R.string.Hide;
                    str3 = "Hide";
                } else {
                    i4 = R.string.Show;
                    str3 = "Show";
                }
                message.setPositiveButton(LocaleController.getString(str3, i4), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChangeUsernameActivity$3$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i5) {
                        ChangeUsernameActivity.AnonymousClass3.this.lambda$onItemClick$3(tLRPC$TL_username, i, view, dialogInterface, i5);
                    }
                }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), ChangeUsernameActivity$3$$ExternalSyntheticLambda2.INSTANCE).show();
            } else if (!(view instanceof InputCell)) {
            } else {
                ChangeUsernameActivity.this.focusUsernameField(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$3(final TLRPC$TL_username tLRPC$TL_username, final int i, View view, DialogInterface dialogInterface, int i2) {
            final TLRPC$TL_account_toggleUsername tLRPC$TL_account_toggleUsername = new TLRPC$TL_account_toggleUsername();
            tLRPC$TL_account_toggleUsername.username = tLRPC$TL_username.username;
            final boolean z = tLRPC$TL_username.active;
            tLRPC$TL_account_toggleUsername.active = !z;
            ChangeUsernameActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_account_toggleUsername, new RequestDelegate() { // from class: org.telegram.ui.ChangeUsernameActivity$3$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChangeUsernameActivity.AnonymousClass3.this.lambda$onItemClick$2(tLRPC$TL_account_toggleUsername, i, tLRPC$TL_username, z, tLObject, tLRPC$TL_error);
                }
            });
            ChangeUsernameActivity.this.loadingUsernames.add(tLRPC$TL_username.username);
            ((UsernameCell) view).setLoading(true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$2(final TLRPC$TL_account_toggleUsername tLRPC$TL_account_toggleUsername, final int i, final TLRPC$TL_username tLRPC$TL_username, final boolean z, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$3$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChangeUsernameActivity.AnonymousClass3.this.lambda$onItemClick$1(tLRPC$TL_account_toggleUsername, tLObject, i, tLRPC$TL_error, tLRPC$TL_username, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$1(TLRPC$TL_account_toggleUsername tLRPC$TL_account_toggleUsername, TLObject tLObject, int i, TLRPC$TL_error tLRPC$TL_error, final TLRPC$TL_username tLRPC$TL_username, final boolean z) {
            ChangeUsernameActivity.this.loadingUsernames.remove(tLRPC$TL_account_toggleUsername.username);
            if (tLObject instanceof TLRPC$TL_boolTrue) {
                ChangeUsernameActivity.this.toggleUsername(i, tLRPC$TL_account_toggleUsername.active);
            } else if (tLRPC$TL_error != null && "USERNAMES_ACTIVE_TOO_MUCH".equals(tLRPC$TL_error.text)) {
                boolean z2 = tLRPC$TL_account_toggleUsername.active;
                tLRPC$TL_username.active = z2;
                ChangeUsernameActivity.this.toggleUsername(i, z2);
                new AlertDialog.Builder(ChangeUsernameActivity.this.getContext(), ChangeUsernameActivity.this.getResourceProvider()).setTitle(LocaleController.getString("UsernameActivateErrorTitle", R.string.UsernameActivateErrorTitle)).setMessage(LocaleController.getString("UsernameActivateErrorMessage", R.string.UsernameActivateErrorMessage)).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChangeUsernameActivity$3$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ChangeUsernameActivity.AnonymousClass3.this.lambda$onItemClick$0(tLRPC$TL_username, z, dialogInterface, i2);
                    }
                }).show();
            } else {
                ChangeUsernameActivity.this.toggleUsername(tLRPC$TL_username, z, true);
            }
            ChangeUsernameActivity.this.getMessagesController().updateUsernameActiveness(MessagesController.getInstance(((BaseFragment) ChangeUsernameActivity.this).currentAccount).getUser(Long.valueOf(UserConfig.getInstance(((BaseFragment) ChangeUsernameActivity.this).currentAccount).getClientUserId())), tLRPC$TL_username.username, tLRPC$TL_username.active);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(TLRPC$TL_username tLRPC$TL_username, boolean z, DialogInterface dialogInterface, int i) {
            ChangeUsernameActivity.this.toggleUsername(tLRPC$TL_username, z, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1() {
        String str = this.username;
        if (str == null || str.length() > 0) {
            boolean z = true;
            this.ignoreCheck = true;
            if (this.usernames.size() > 0) {
                z = false;
            }
            focusUsernameField(z);
            this.ignoreCheck = false;
        }
    }

    public void toggleUsername(TLRPC$TL_username tLRPC$TL_username, boolean z, boolean z2) {
        for (int i = 0; i < this.usernames.size(); i++) {
            if (this.usernames.get(i) == tLRPC$TL_username) {
                toggleUsername(i + 4, z, z2);
                return;
            }
        }
    }

    public void toggleUsername(int i, boolean z) {
        toggleUsername(i, z, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0072 A[LOOP:1: B:34:0x0072->B:43:0x00a2, LOOP_START, PHI: r2 
      PHI: (r2v1 int) = (r2v0 int), (r2v2 int) binds: [B:33:0x0070, B:43:0x00a2] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void toggleUsername(int r6, boolean r7, boolean r8) {
        /*
            r5 = this;
            int r0 = r6 + (-4)
            if (r0 < 0) goto Lae
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r1 = r5.usernames
            int r1 = r1.size()
            if (r0 < r1) goto Le
            goto Lae
        Le:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r1 = r5.usernames
            java.lang.Object r0 = r1.get(r0)
            org.telegram.tgnet.TLRPC$TL_username r0 = (org.telegram.tgnet.TLRPC$TL_username) r0
            if (r0 != 0) goto L19
            return
        L19:
            r0.active = r7
            r1 = -1
            r2 = 0
            if (r7 == 0) goto L42
            r7 = 0
        L20:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r3 = r5.usernames
            int r3 = r3.size()
            if (r7 >= r3) goto L38
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r3 = r5.usernames
            java.lang.Object r3 = r3.get(r7)
            org.telegram.tgnet.TLRPC$TL_username r3 = (org.telegram.tgnet.TLRPC$TL_username) r3
            boolean r3 = r3.active
            if (r3 != 0) goto L35
            goto L39
        L35:
            int r7 = r7 + 1
            goto L20
        L38:
            r7 = -1
        L39:
            if (r7 < 0) goto L6e
            int r7 = r7 + (-1)
            int r7 = java.lang.Math.max(r2, r7)
            goto L6c
        L42:
            r7 = 0
            r3 = -1
        L44:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r4 = r5.usernames
            int r4 = r4.size()
            if (r7 >= r4) goto L5c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r4 = r5.usernames
            java.lang.Object r4 = r4.get(r7)
            org.telegram.tgnet.TLRPC$TL_username r4 = (org.telegram.tgnet.TLRPC$TL_username) r4
            boolean r4 = r4.active
            if (r4 == 0) goto L59
            r3 = r7
        L59:
            int r7 = r7 + 1
            goto L44
        L5c:
            if (r3 < 0) goto L6e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_username> r7 = r5.usernames
            int r7 = r7.size()
            int r7 = r7 + (-1)
            int r3 = r3 + 1
            int r7 = java.lang.Math.min(r7, r3)
        L6c:
            int r1 = r7 + 4
        L6e:
            org.telegram.ui.Components.RecyclerListView r7 = r5.listView
            if (r7 == 0) goto La5
        L72:
            org.telegram.ui.Components.RecyclerListView r7 = r5.listView
            int r7 = r7.getChildCount()
            if (r2 >= r7) goto La5
            org.telegram.ui.Components.RecyclerListView r7 = r5.listView
            android.view.View r7 = r7.getChildAt(r2)
            org.telegram.ui.Components.RecyclerListView r3 = r5.listView
            int r3 = r3.getChildAdapterPosition(r7)
            if (r3 != r6) goto La2
            if (r8 == 0) goto L8d
            org.telegram.messenger.AndroidUtilities.shakeView(r7)
        L8d:
            boolean r8 = r7 instanceof org.telegram.ui.ChangeUsernameActivity.UsernameCell
            if (r8 == 0) goto La5
            org.telegram.ui.ChangeUsernameActivity$UsernameCell r7 = (org.telegram.ui.ChangeUsernameActivity.UsernameCell) r7
            java.util.ArrayList<java.lang.String> r8 = r5.loadingUsernames
            java.lang.String r0 = r0.username
            boolean r8 = r8.contains(r0)
            r7.setLoading(r8)
            r7.update()
            goto La5
        La2:
            int r2 = r2 + 1
            goto L72
        La5:
            if (r1 < 0) goto Lae
            if (r6 == r1) goto Lae
            org.telegram.ui.ChangeUsernameActivity$Adapter r7 = r5.adapter
            r7.moveElement(r6, r1)
        Lae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChangeUsernameActivity.toggleUsername(int, boolean, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void focusUsernameField(boolean z) {
        InputCell inputCell = this.inputCell;
        if (inputCell != null) {
            if (!inputCell.field.isFocused()) {
                EditTextBoldCursor editTextBoldCursor = this.inputCell.field;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
            }
            this.inputCell.field.requestFocus();
            if (!z) {
                return;
            }
            AndroidUtilities.showKeyboard(this.inputCell.field);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1822onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(ChangeUsernameActivity.this.getContext());
                headerCell.setBackgroundColor(ChangeUsernameActivity.this.getThemedColor("windowBackgroundWhite"));
                return new RecyclerListView.Holder(headerCell);
            } else if (i == 1) {
                ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                return new RecyclerListView.Holder(new UsernameHelpCell(changeUsernameActivity, changeUsernameActivity.getContext()));
            } else if (i == 2) {
                return new RecyclerListView.Holder(new TextInfoPrivacyCell(ChangeUsernameActivity.this.getContext()));
            } else {
                if (i == 3) {
                    ChangeUsernameActivity changeUsernameActivity2 = ChangeUsernameActivity.this;
                    return new RecyclerListView.Holder(new InputCell(changeUsernameActivity2.getContext()));
                } else if (i == 4) {
                    return new RecyclerListView.Holder(new UsernameCell(ChangeUsernameActivity.this.getContext(), ChangeUsernameActivity.this.getResourceProvider()) { // from class: org.telegram.ui.ChangeUsernameActivity.Adapter.1
                        {
                            this.isProfile = true;
                        }

                        @Override // org.telegram.ui.ChangeUsernameActivity.UsernameCell
                        protected String getUsernameEditable() {
                            return ChangeUsernameActivity.this.username;
                        }
                    });
                } else {
                    return null;
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == 0) {
                    i2 = R.string.SetUsernameHeader;
                    str = "SetUsernameHeader";
                } else {
                    i2 = R.string.UsernamesProfileHeader;
                    str = "UsernamesProfileHeader";
                }
                headerCell.setText(LocaleController.getString(str, i2));
            } else if (itemViewType == 2) {
                ((TextInfoPrivacyCell) viewHolder.itemView).setText(LocaleController.getString("UsernamesProfileHelp", R.string.UsernamesProfileHelp));
                ((TextInfoPrivacyCell) viewHolder.itemView).setBackgroundDrawable(Theme.getThemedDrawable(ChangeUsernameActivity.this.getContext(), R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            } else {
                boolean z = true;
                if (itemViewType == 3) {
                    ChangeUsernameActivity.this.ignoreCheck = true;
                    ChangeUsernameActivity.this.inputCell = (InputCell) viewHolder.itemView.field.setText(ChangeUsernameActivity.this.username);
                    ChangeUsernameActivity.this.ignoreCheck = false;
                } else if (itemViewType != 4) {
                } else {
                    TLRPC$TL_username tLRPC$TL_username = (TLRPC$TL_username) ChangeUsernameActivity.this.usernames.get(i - 4);
                    UsernameCell usernameCell = (UsernameCell) viewHolder.itemView;
                    if (tLRPC$TL_username.editable) {
                        ChangeUsernameActivity.this.editableUsernameCell = usernameCell;
                    } else if (ChangeUsernameActivity.this.editableUsernameCell == usernameCell) {
                        ChangeUsernameActivity.this.editableUsernameCell = null;
                    }
                    if (i >= getItemCount() - 2) {
                        z = false;
                    }
                    usernameCell.set(tLRPC$TL_username, z, false);
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return (ChangeUsernameActivity.this.usernames.size() > 0 ? ChangeUsernameActivity.this.usernames.size() + 1 + 1 : 0) + 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            if (i == 1) {
                return 3;
            }
            if (i == 2) {
                return 1;
            }
            if (i == 3) {
                return 0;
            }
            return i != getItemCount() - 1 ? 4 : 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 4;
        }

        public void swapElements(int i, int i2) {
            int i3 = i - 4;
            int i4 = i2 - 4;
            if (i3 >= ChangeUsernameActivity.this.usernames.size() || i4 >= ChangeUsernameActivity.this.usernames.size()) {
                return;
            }
            if (i != i2) {
                ChangeUsernameActivity.this.needReorder = true;
            }
            swapListElements(ChangeUsernameActivity.this.usernames, i3, i4);
            notifyItemMoved(i, i2);
            int size = (ChangeUsernameActivity.this.usernames.size() + 4) - 1;
            if (i != size && i2 != size) {
                return;
            }
            notifyItemChanged(i, 3);
            notifyItemChanged(i2, 3);
        }

        private void swapListElements(List<TLRPC$TL_username> list, int i, int i2) {
            list.set(i, list.get(i2));
            list.set(i2, list.get(i));
        }

        public void moveElement(int i, int i2) {
            int i3 = i - 4;
            int i4 = i2 - 4;
            if (i3 >= ChangeUsernameActivity.this.usernames.size() || i4 >= ChangeUsernameActivity.this.usernames.size()) {
                return;
            }
            ChangeUsernameActivity.this.usernames.add(i4, (TLRPC$TL_username) ChangeUsernameActivity.this.usernames.remove(i3));
            notifyItemMoved(i, i2);
            for (int i5 = 0; i5 < ChangeUsernameActivity.this.usernames.size(); i5++) {
                notifyItemChanged(i5 + 4);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendReorder() {
        if (!this.needReorder) {
            return;
        }
        this.needReorder = false;
        TLRPC$TL_account_reorderUsernames tLRPC$TL_account_reorderUsernames = new TLRPC$TL_account_reorderUsernames();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < this.notEditableUsernames.size(); i++) {
            if (this.notEditableUsernames.get(i).active) {
                arrayList.add(this.notEditableUsernames.get(i).username);
            }
        }
        for (int i2 = 0; i2 < this.usernames.size(); i2++) {
            if (this.usernames.get(i2).active) {
                arrayList.add(this.usernames.get(i2).username);
            }
        }
        tLRPC$TL_account_reorderUsernames.order = arrayList;
        getConnectionsManager().sendRequest(tLRPC$TL_account_reorderUsernames, ChangeUsernameActivity$$ExternalSyntheticLambda10.INSTANCE);
        updateUser();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendReorder$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        boolean z = tLObject instanceof TLRPC$TL_boolTrue;
    }

    private void updateUser() {
        ArrayList<TLRPC$TL_username> arrayList = new ArrayList<>();
        arrayList.addAll(this.notEditableUsernames);
        arrayList.addAll(this.usernames);
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        user.usernames = arrayList;
        MessagesController.getInstance(this.currentAccount).putUser(user, false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class UsernameHelpCell extends FrameLayout {
        private Integer height;
        private ValueAnimator heightUpdateAnimator;
        private TextView text1View;
        private TextView text2View;

        public UsernameHelpCell(ChangeUsernameActivity changeUsernameActivity, Context context) {
            super(context);
            changeUsernameActivity.helpCell = this;
            setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(17.0f));
            setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            setClipChildren(false);
            TextView textView = new TextView(context);
            this.text1View = textView;
            textView.setTextSize(1, 15.0f);
            this.text1View.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            int i = 5;
            this.text1View.setGravity(LocaleController.isRTL ? 5 : 3);
            this.text1View.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            this.text1View.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
            TextView textView2 = changeUsernameActivity.statusTextView = new TextView(context);
            this.text2View = textView2;
            textView2.setTextSize(1, 15.0f);
            this.text2View.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.text2View.setGravity(!LocaleController.isRTL ? 3 : i);
            this.text2View.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            this.text2View.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
            addView(this.text1View, LayoutHelper.createFrame(-1, -2, 48));
            addView(this.text2View, LayoutHelper.createFrame(-1, -2, 48));
            this.text1View.setText(AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", R.string.UsernameHelp)));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            Integer num = this.height;
            if (num != null) {
                i2 = View.MeasureSpec.makeMeasureSpec(num.intValue(), NUM);
            }
            super.onMeasure(i, i2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void update() {
            if (this.text2View.getVisibility() == 0) {
                this.text2View.measure(View.MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(9999999, Integer.MIN_VALUE));
            }
            ValueAnimator valueAnimator = this.heightUpdateAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            Integer num = this.height;
            final int measuredHeight = num == null ? getMeasuredHeight() : num.intValue();
            final int dp = AndroidUtilities.dp(27.0f) + this.text1View.getHeight() + ((this.text2View.getVisibility() != 0 || TextUtils.isEmpty(this.text2View.getText())) ? 0 : this.text2View.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
            final float translationY = this.text1View.getTranslationY();
            final float measuredHeight2 = (this.text2View.getVisibility() != 0 || TextUtils.isEmpty(this.text2View.getText())) ? 0.0f : this.text2View.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.heightUpdateAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChangeUsernameActivity$UsernameHelpCell$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChangeUsernameActivity.UsernameHelpCell.this.lambda$update$0(translationY, measuredHeight2, measuredHeight, dp, valueAnimator2);
                }
            });
            this.heightUpdateAnimator.setDuration(200L);
            this.heightUpdateAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.heightUpdateAnimator.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$update$0(float f, float f2, int i, int i2, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.text1View.setTranslationY(AndroidUtilities.lerp(f, f2, floatValue));
            this.height = Integer.valueOf(AndroidUtilities.lerp(i, i2, floatValue));
            requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class InputCell extends FrameLayout {
        public EditTextBoldCursor field;
        public TextView tme;

        public InputCell(Context context) {
            super(context);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(0);
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext());
            this.field = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 17.0f);
            this.field.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.field.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.field.setBackgroundDrawable(null);
            this.field.setMaxLines(1);
            this.field.setLines(1);
            this.field.setPadding(0, 0, 0, 0);
            this.field.setSingleLine(true);
            int i = 5;
            this.field.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.field.setInputType(180224);
            this.field.setImeOptions(6);
            this.field.setHint(LocaleController.getString("UsernameLinkPlaceholder", R.string.UsernameLinkPlaceholder));
            this.field.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.field.setCursorSize(AndroidUtilities.dp(19.0f));
            this.field.setCursorWidth(1.5f);
            this.field.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChangeUsernameActivity$InputCell$$ExternalSyntheticLambda0
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                    boolean lambda$new$0;
                    lambda$new$0 = ChangeUsernameActivity.InputCell.this.lambda$new$0(textView, i2, keyEvent);
                    return lambda$new$0;
                }
            });
            this.field.setText(ChangeUsernameActivity.this.username);
            this.field.addTextChangedListener(new TextWatcher(ChangeUsernameActivity.this) { // from class: org.telegram.ui.ChangeUsernameActivity.InputCell.1
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    String str = ChangeUsernameActivity.this.username;
                    ChangeUsernameActivity.this.username = charSequence == null ? "" : charSequence.toString();
                    updateUsernameCell(str);
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    String str = ChangeUsernameActivity.this.username;
                    ChangeUsernameActivity.this.username = charSequence == null ? "" : charSequence.toString();
                    updateUsernameCell(str);
                    if (ChangeUsernameActivity.this.ignoreCheck) {
                        return;
                    }
                    ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                    changeUsernameActivity.checkUserName(changeUsernameActivity.username, false);
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    if (ChangeUsernameActivity.this.username.startsWith("@")) {
                        ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                        changeUsernameActivity.username = changeUsernameActivity.username.substring(1);
                    }
                    if (ChangeUsernameActivity.this.username.length() > 0) {
                        String str = "https://" + MessagesController.getInstance(((BaseFragment) ChangeUsernameActivity.this).currentAccount).linkPrefix + "/" + ChangeUsernameActivity.this.username;
                        String formatString = LocaleController.formatString("UsernameHelpLink", R.string.UsernameHelpLink, str);
                        int indexOf = formatString.indexOf(str);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                        if (indexOf < 0) {
                            return;
                        }
                        spannableStringBuilder.setSpan(new LinkSpan(str), indexOf, str.length() + indexOf, 33);
                    }
                }

                private void updateUsernameCell(String str) {
                    if (ChangeUsernameActivity.this.editableUsernameCell == null || str == null) {
                        return;
                    }
                    ChangeUsernameActivity.this.editableUsernameCell.updateUsername(ChangeUsernameActivity.this.username);
                }
            });
            TextView textView = new TextView(getContext());
            this.tme = textView;
            textView.setMaxLines(1);
            this.tme.setLines(1);
            this.tme.setPadding(0, 0, 0, 0);
            this.tme.setSingleLine(true);
            TextView textView2 = this.tme;
            textView2.setText(ChangeUsernameActivity.this.getMessagesController().linkPrefix + "/");
            this.tme.setTextSize(1, 17.0f);
            this.tme.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.tme.setGravity((!LocaleController.isRTL ? 3 : i) | 48);
            this.tme.setTranslationY(-AndroidUtilities.dp(3.0f));
            linearLayout.addView(this.tme, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 21, 15, 0, 15));
            linearLayout.addView(this.field, LayoutHelper.createLinear(-2, -2, 1.0f, 16, 0, 15, 21, 15));
            addView(linearLayout, LayoutHelper.createFrame(-1, -1, 48));
            setBackgroundColor(ChangeUsernameActivity.this.getThemedColor("windowBackgroundWhite"));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeUsernameActivity.this.doneButton == null) {
                return false;
            }
            ChangeUsernameActivity.this.doneButton.performClick();
            return true;
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }
    }

    /* loaded from: classes3.dex */
    public static class UsernameCell extends FrameLayout {
        public boolean active;
        private AnimatedFloat activeFloat;
        private AnimatedTextView activeView;
        private ValueAnimator activeViewTextColorAnimator;
        private float activeViewTextColorT;
        public TLRPC$TL_username currentUsername;
        public boolean editable;
        public boolean isProfile;
        private Drawable[] linkDrawables;
        public boolean loading;
        public ValueAnimator loadingAnimator;
        private CircularProgressDrawable loadingDrawable;
        public float loadingFloat;
        private ImageView loadingView;
        private Theme.ResourcesProvider resourcesProvider;
        private boolean useDivider;
        private AnimatedFloat useDividerAlpha;
        private SimpleTextView usernameView;

        protected String getUsernameEditable() {
            return null;
        }

        public UsernameCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.isProfile = false;
            this.useDividerAlpha = new AnimatedFloat(this, 300L, CubicBezierInterpolator.DEFAULT);
            this.activeFloat = new AnimatedFloat(this, 400L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.resourcesProvider = resourcesProvider;
            setBackgroundColor(Theme.getColor("windowBackgroundWhite", resourcesProvider));
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.usernameView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.usernameView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider));
            this.usernameView.setEllipsizeByGradient(true);
            addView(this.usernameView, LayoutHelper.createFrame(-1, -2.0f, 48, 70.0f, 9.0f, 0.0f, 50.0f));
            this.loadingView = new ImageView(getContext());
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(1.35f), Theme.getColor("windowBackgroundWhiteBlueText", resourcesProvider));
            this.loadingDrawable = circularProgressDrawable;
            this.loadingView.setImageDrawable(circularProgressDrawable);
            this.loadingView.setAlpha(0.0f);
            this.loadingView.setVisibility(0);
            this.loadingDrawable.setBounds(0, 0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
            addView(this.loadingView, LayoutHelper.createFrame(14, 14.0f, 48, 70.0f, 35.0f, 0.0f, 0.0f));
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), false, true, true);
            this.activeView = animatedTextView;
            animatedTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider));
            this.activeView.setAnimationProperties(0.4f, 0L, 120L, CubicBezierInterpolator.EASE_OUT);
            this.activeView.setTextSize(AndroidUtilities.dp(13.0f));
            addView(this.activeView, LayoutHelper.createFrame(-1, -2.0f, 48, 70.0f, 23.0f, 0.0f, 0.0f));
            Drawable[] drawableArr = {ContextCompat.getDrawable(context, R.drawable.msg_link_1).mutate(), ContextCompat.getDrawable(context, R.drawable.msg_link_2).mutate()};
            this.linkDrawables = drawableArr;
            drawableArr[0].setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.linkDrawables[1].setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            ChangeUsernameActivity.linkBackgroundActive.setColor(Theme.getColor("featuredStickers_addButton", resourcesProvider));
            ChangeUsernameActivity.linkBackgroundInactive.setColor(Theme.getColor("chats_unreadCounterMuted", resourcesProvider));
        }

        public void setLoading(final boolean z) {
            if (this.loading != z) {
                this.loading = z;
                ValueAnimator valueAnimator = this.loadingAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.loadingView.setVisibility(0);
                float[] fArr = new float[2];
                fArr[0] = this.loadingFloat;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.loadingAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChangeUsernameActivity$UsernameCell$$ExternalSyntheticLambda1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChangeUsernameActivity.UsernameCell.this.lambda$setLoading$0(valueAnimator2);
                    }
                });
                this.loadingAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChangeUsernameActivity.UsernameCell.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        UsernameCell.this.loadingView.setVisibility(z ? 0 : 8);
                    }
                });
                this.loadingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.loadingAnimator.setDuration(200L);
                this.loadingAnimator.start();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setLoading$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.loadingFloat = floatValue;
            this.activeView.setTranslationX(floatValue * AndroidUtilities.dp(16.0f));
            this.loadingView.setAlpha(this.loadingFloat);
        }

        public void set(TLRPC$TL_username tLRPC$TL_username, boolean z, boolean z2) {
            int i;
            String str;
            int i2;
            String str2;
            this.currentUsername = tLRPC$TL_username;
            this.useDivider = z;
            invalidate();
            boolean z3 = false;
            if (this.currentUsername == null) {
                this.active = false;
                this.editable = false;
                return;
            }
            this.active = tLRPC$TL_username.active;
            this.editable = tLRPC$TL_username.editable;
            updateUsername(tLRPC$TL_username.username);
            if (this.isProfile) {
                AnimatedTextView animatedTextView = this.activeView;
                if (this.editable) {
                    i2 = R.string.UsernameProfileLinkEditable;
                    str2 = "UsernameProfileLinkEditable";
                } else if (this.active) {
                    i2 = R.string.UsernameProfileLinkActive;
                    str2 = "UsernameProfileLinkActive";
                } else {
                    i2 = R.string.UsernameProfileLinkInactive;
                    str2 = "UsernameProfileLinkInactive";
                }
                animatedTextView.setText(LocaleController.getString(str2, i2), z2, !this.active);
            } else {
                AnimatedTextView animatedTextView2 = this.activeView;
                if (this.editable) {
                    i = R.string.UsernameLinkEditable;
                    str = "UsernameLinkEditable";
                } else if (this.active) {
                    i = R.string.UsernameLinkActive;
                    str = "UsernameLinkActive";
                } else {
                    i = R.string.UsernameLinkInactive;
                    str = "UsernameLinkInactive";
                }
                animatedTextView2.setText(LocaleController.getString(str, i), z2, !this.active);
            }
            if (this.active || this.editable) {
                z3 = true;
            }
            animateValueTextColor(z3, z2);
        }

        public void updateUsername(String str) {
            if (this.editable) {
                str = getUsernameEditable();
            }
            if (TextUtils.isEmpty(str)) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("@");
                SpannableString spannableString = new SpannableString(LocaleController.getString("UsernameLinkPlaceholder", R.string.UsernameLinkPlaceholder));
                spannableString.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteHintText", this.resourcesProvider)), 0, spannableString.length(), 33);
                spannableStringBuilder.append((CharSequence) spannableString);
                this.usernameView.setText(spannableStringBuilder);
                return;
            }
            SimpleTextView simpleTextView = this.usernameView;
            simpleTextView.setText("@" + str);
        }

        private void animateValueTextColor(boolean z, boolean z2) {
            ValueAnimator valueAnimator = this.activeViewTextColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.activeViewTextColorAnimator = null;
            }
            float f = 1.0f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.activeViewTextColorT;
                if (!z) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.activeViewTextColorAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChangeUsernameActivity$UsernameCell$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChangeUsernameActivity.UsernameCell.this.lambda$animateValueTextColor$1(valueAnimator2);
                    }
                });
                this.activeViewTextColorAnimator.setDuration(120L);
                this.activeViewTextColorAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.activeViewTextColorAnimator.start();
                return;
            }
            if (!z) {
                f = 0.0f;
            }
            this.activeViewTextColorT = f;
            int blendARGB = ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteGrayText2", this.resourcesProvider), Theme.getColor("windowBackgroundWhiteBlueText", this.resourcesProvider), this.activeViewTextColorT);
            this.loadingDrawable.setColor(blendARGB);
            this.activeView.setTextColor(blendARGB);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateValueTextColor$1(ValueAnimator valueAnimator) {
            this.activeViewTextColorT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            int blendARGB = ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteGrayText2", this.resourcesProvider), Theme.getColor("windowBackgroundWhiteBlueText", this.resourcesProvider), this.activeViewTextColorT);
            this.loadingDrawable.setColor(blendARGB);
            this.activeView.setTextColor(blendARGB);
        }

        public void update() {
            TLRPC$TL_username tLRPC$TL_username = this.currentUsername;
            if (tLRPC$TL_username != null) {
                set(tLRPC$TL_username, this.useDivider, true);
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float f = 1.0f;
            float f2 = this.activeFloat.set(this.active ? 1.0f : 0.0f);
            if (f2 < 1.0f) {
                canvas.drawCircle(AndroidUtilities.dp(35.0f), AndroidUtilities.dp(29.0f), AndroidUtilities.dp(16.0f), ChangeUsernameActivity.linkBackgroundInactive);
                this.linkDrawables[1].setAlpha((int) ((1.0f - f2) * 255.0f));
                this.linkDrawables[1].setBounds(AndroidUtilities.dp(35.0f) - (this.linkDrawables[1].getIntrinsicWidth() / 2), AndroidUtilities.dp(29.0f) - (this.linkDrawables[1].getIntrinsicHeight() / 2), AndroidUtilities.dp(35.0f) + (this.linkDrawables[1].getIntrinsicWidth() / 2), AndroidUtilities.dp(29.0f) + (this.linkDrawables[1].getIntrinsicHeight() / 2));
                this.linkDrawables[1].draw(canvas);
            }
            if (f2 > 0.0f) {
                int i = (int) (255.0f * f2);
                ChangeUsernameActivity.linkBackgroundActive.setAlpha(i);
                canvas.drawCircle(AndroidUtilities.dp(35.0f), AndroidUtilities.dp(29.0f), AndroidUtilities.dp(16.0f) * f2, ChangeUsernameActivity.linkBackgroundActive);
                this.linkDrawables[0].setAlpha(i);
                this.linkDrawables[0].setBounds(AndroidUtilities.dp(35.0f) - (this.linkDrawables[0].getIntrinsicWidth() / 2), AndroidUtilities.dp(29.0f) - (this.linkDrawables[0].getIntrinsicHeight() / 2), AndroidUtilities.dp(35.0f) + (this.linkDrawables[0].getIntrinsicWidth() / 2), AndroidUtilities.dp(29.0f) + (this.linkDrawables[0].getIntrinsicHeight() / 2));
                this.linkDrawables[0].draw(canvas);
            }
            AnimatedFloat animatedFloat = this.useDividerAlpha;
            if (!this.useDivider) {
                f = 0.0f;
            }
            float f3 = animatedFloat.set(f);
            if (f3 > 0.0f) {
                int alpha = Theme.dividerPaint.getAlpha();
                Theme.dividerPaint.setAlpha((int) (alpha * f3));
                canvas.drawRect(AndroidUtilities.dp(70.0f), getHeight() - 1, getWidth(), getHeight(), Theme.dividerPaint);
                Theme.dividerPaint.setAlpha(alpha);
            }
            ChangeUsernameActivity.dragPaint.setColor(Theme.getColor("stickers_menu"));
            ChangeUsernameActivity.dragPaint.setAlpha((int) (ChangeUsernameActivity.dragPaint.getAlpha() * f2));
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(getWidth() - AndroidUtilities.dp(37.0f), AndroidUtilities.dp(25.0f), getWidth() - AndroidUtilities.dp(21.0f), AndroidUtilities.dp(27.0f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(0.3f), AndroidUtilities.dp(0.3f), ChangeUsernameActivity.dragPaint);
            rectF.set(getWidth() - AndroidUtilities.dp(37.0f), AndroidUtilities.dp(31.0f), getWidth() - AndroidUtilities.dp(21.0f), AndroidUtilities.dp(33.0f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(0.3f), AndroidUtilities.dp(0.3f), ChangeUsernameActivity.dragPaint);
        }
    }

    /* loaded from: classes3.dex */
    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 4 || !((UsernameCell) viewHolder.itemView).active) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() == viewHolder2.getItemViewType()) {
                View view = viewHolder2.itemView;
                if ((view instanceof UsernameCell) && !((UsernameCell) view).active) {
                    return false;
                }
                ChangeUsernameActivity.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i == 0) {
                ChangeUsernameActivity.this.sendReorder();
            } else {
                ChangeUsernameActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            focusUsernameField(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkUserName(final String str, boolean z) {
        if (str != null && str.startsWith("@")) {
            str = str.substring(1);
        }
        TextView textView = this.statusTextView;
        if (textView != null) {
            textView.setVisibility(!TextUtils.isEmpty(str) ? 0 : 8);
            UsernameHelpCell usernameHelpCell = this.helpCell;
            if (usernameHelpCell != null) {
                usernameHelpCell.update();
            }
        }
        if (!z || str.length() != 0) {
            Runnable runnable = this.checkRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.checkRunnable = null;
                this.lastCheckName = null;
                if (this.checkReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
                }
            }
            if (str != null) {
                if (str.startsWith("_") || str.endsWith("_")) {
                    TextView textView2 = this.statusTextView;
                    if (textView2 != null) {
                        textView2.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                        this.statusTextView.setTag("windowBackgroundWhiteRedText4");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                        UsernameHelpCell usernameHelpCell2 = this.helpCell;
                        if (usernameHelpCell2 != null) {
                            usernameHelpCell2.update();
                        }
                    }
                    return false;
                }
                for (int i = 0; i < str.length(); i++) {
                    char charAt = str.charAt(i);
                    if (i == 0 && charAt >= '0' && charAt <= '9') {
                        if (z) {
                            AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                        } else {
                            TextView textView3 = this.statusTextView;
                            if (textView3 != null) {
                                textView3.setText(LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                                this.statusTextView.setTag("windowBackgroundWhiteRedText4");
                                this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                                UsernameHelpCell usernameHelpCell3 = this.helpCell;
                                if (usernameHelpCell3 != null) {
                                    usernameHelpCell3.update();
                                }
                            }
                        }
                        return false;
                    } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                        if (z) {
                            AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                        } else {
                            TextView textView4 = this.statusTextView;
                            if (textView4 != null) {
                                textView4.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                                this.statusTextView.setTag("windowBackgroundWhiteRedText4");
                                this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                                UsernameHelpCell usernameHelpCell4 = this.helpCell;
                                if (usernameHelpCell4 != null) {
                                    usernameHelpCell4.update();
                                }
                            }
                        }
                        return false;
                    }
                }
            }
            if (str == null || str.length() < 5) {
                if (z) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
                } else {
                    TextView textView5 = this.statusTextView;
                    if (textView5 != null) {
                        textView5.setText(LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
                        this.statusTextView.setTag("windowBackgroundWhiteRedText4");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                        UsernameHelpCell usernameHelpCell5 = this.helpCell;
                        if (usernameHelpCell5 != null) {
                            usernameHelpCell5.update();
                        }
                    }
                }
                return false;
            } else if (str.length() > 32) {
                if (z) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
                } else {
                    TextView textView6 = this.statusTextView;
                    if (textView6 != null) {
                        textView6.setText(LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
                        this.statusTextView.setTag("windowBackgroundWhiteRedText4");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                        UsernameHelpCell usernameHelpCell6 = this.helpCell;
                        if (usernameHelpCell6 != null) {
                            usernameHelpCell6.update();
                        }
                    }
                }
                return false;
            } else {
                if (!z) {
                    String str2 = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                    if (str2 == null) {
                        str2 = "";
                    }
                    if (str.equals(str2)) {
                        TextView textView7 = this.statusTextView;
                        if (textView7 != null) {
                            textView7.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, str));
                            this.statusTextView.setTag("windowBackgroundWhiteGreenText");
                            this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
                            UsernameHelpCell usernameHelpCell7 = this.helpCell;
                            if (usernameHelpCell7 != null) {
                                usernameHelpCell7.update();
                            }
                        }
                        return true;
                    }
                    TextView textView8 = this.statusTextView;
                    if (textView8 != null) {
                        textView8.setText(LocaleController.getString("UsernameChecking", R.string.UsernameChecking));
                        this.statusTextView.setTag("windowBackgroundWhiteGrayText8");
                        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
                        UsernameHelpCell usernameHelpCell8 = this.helpCell;
                        if (usernameHelpCell8 != null) {
                            usernameHelpCell8.update();
                        }
                    }
                    this.lastCheckName = str;
                    Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChangeUsernameActivity.this.lambda$checkUserName$5(str);
                        }
                    };
                    this.checkRunnable = runnable2;
                    AndroidUtilities.runOnUIThread(runnable2, 300L);
                }
                return true;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUserName$5(final String str) {
        TLRPC$TL_account_checkUsername tLRPC$TL_account_checkUsername = new TLRPC$TL_account_checkUsername();
        tLRPC$TL_account_checkUsername.username = str;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_checkUsername, new RequestDelegate() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChangeUsernameActivity.this.lambda$checkUserName$4(str, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUserName$4(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ChangeUsernameActivity.this.lambda$checkUserName$3(str, tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkUserName$3(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 == null || !str2.equals(str)) {
            return;
        }
        if (tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_boolTrue)) {
            TextView textView = this.statusTextView;
            if (textView == null) {
                return;
            }
            textView.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, str));
            this.statusTextView.setTag("windowBackgroundWhiteGreenText");
            this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
            UsernameHelpCell usernameHelpCell = this.helpCell;
            if (usernameHelpCell == null) {
                return;
            }
            usernameHelpCell.update();
            return;
        }
        TextView textView2 = this.statusTextView;
        if (textView2 == null) {
            return;
        }
        textView2.setText(LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
        this.statusTextView.setTag("windowBackgroundWhiteRedText4");
        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        UsernameHelpCell usernameHelpCell2 = this.helpCell;
        if (usernameHelpCell2 == null) {
            return;
        }
        usernameHelpCell2.update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveName() {
        if (this.username.startsWith("@")) {
            this.username = this.username.substring(1);
        }
        if (!this.username.isEmpty() && !checkUserName(this.username, false)) {
            shakeIfOff();
            return;
        }
        TLRPC$User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (getParentActivity() == null || currentUser == null) {
            return;
        }
        String publicUsername = UserObject.getPublicUsername(currentUser);
        if (publicUsername == null) {
            publicUsername = "";
        }
        if (publicUsername.equals(this.username)) {
            finishFragment();
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        final TLRPC$TL_account_updateUsername tLRPC$TL_account_updateUsername = new TLRPC$TL_account_updateUsername();
        tLRPC$TL_account_updateUsername.username = this.username;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updateUsername, new RequestDelegate() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChangeUsernameActivity.this.lambda$saveName$9(alertDialog, tLRPC$TL_account_updateUsername, tLObject, tLRPC$TL_error);
            }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChangeUsernameActivity.this.lambda$saveName$10(sendRequest, dialogInterface);
            }
        });
        alertDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$9(final AlertDialog alertDialog, final TLRPC$TL_account_updateUsername tLRPC$TL_account_updateUsername, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            final TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ChangeUsernameActivity.this.lambda$saveName$6(alertDialog, tLRPC$User);
                }
            });
        } else if ("USERNAME_NOT_MODIFIED".equals(tLRPC$TL_error.text)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ChangeUsernameActivity.this.lambda$saveName$7(alertDialog);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeUsernameActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ChangeUsernameActivity.this.lambda$saveName$8(alertDialog, tLRPC$TL_error, tLRPC$TL_account_updateUsername);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$6(AlertDialog alertDialog, TLRPC$User tLRPC$User) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        ArrayList<TLRPC$User> arrayList = new ArrayList<>();
        arrayList.add(tLRPC$User);
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$7(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$8(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_updateUsername tLRPC$TL_account_updateUsername) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_updateUsername, new Object[0]);
        shakeIfOff();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$10(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            focusUsernameField(false);
        }
    }

    public void shakeIfOff() {
        if (this.listView == null) {
            return;
        }
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if ((childAt instanceof HeaderCell) && i == 0) {
                AndroidUtilities.shakeViewSpring(((HeaderCell) childAt).getTextView());
            } else if (childAt instanceof UsernameHelpCell) {
                AndroidUtilities.shakeViewSpring(childAt);
            } else if (childAt instanceof InputCell) {
                InputCell inputCell = (InputCell) childAt;
                AndroidUtilities.shakeViewSpring(inputCell.field);
                AndroidUtilities.shakeViewSpring(inputCell.tme);
            }
        }
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        return arrayList;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout == null || iNavigationLayout.getDrawerLayoutContainer() == null) {
            return;
        }
        this.parentLayout.getDrawerLayoutContainer().setBehindKeyboardColor(getThemedColor("windowBackgroundGray"));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        super.onBecomeFullyHidden();
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout == null || iNavigationLayout.getDrawerLayoutContainer() == null) {
            return;
        }
        this.parentLayout.getDrawerLayoutContainer().setBehindKeyboardColor(Theme.getColor("windowBackgroundWhite"));
    }
}
