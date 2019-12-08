package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private FileLocation avatarBig;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageView avatarEditor;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private int chatType;
    private boolean createAfterUpload;
    private String currentGroupCreateAddress;
    private Location currentGroupCreateLocation;
    private GroupCreateFinalActivityDelegate delegate;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private EditTextEmoji editText;
    private FrameLayout editTextContainer;
    private FrameLayout floatingButtonContainer;
    private ImageView floatingButtonIcon;
    private ImageUpdater imageUpdater;
    private RecyclerListView listView;
    private String nameToSet;
    private ContextProgressView progressView;
    private int reqId;
    private ArrayList<Integer> selectedContacts;
    private Drawable shadowDrawable;
    private InputFile uploadedAvatar;

    public interface GroupCreateFinalActivityDelegate {
        void didFailChatCreation();

        void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, int i);

        void didStartChatCreation();
    }

    public class GroupCreateAdapter extends SelectionAdapter {
        private Context context;
        private int usersStartRow;

        public GroupCreateAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            int size = GroupCreateFinalActivity.this.selectedContacts.size() + 2;
            return GroupCreateFinalActivity.this.currentGroupCreateAddress != null ? size + 3 : size;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 3;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                View shadowSectionCell = new ShadowSectionCell(this.context);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                view = shadowSectionCell;
            } else if (i != 1) {
                view = i != 2 ? new TextSettingsCell(this.context) : new GroupCreateUserCell(this.context, false, 3);
            } else {
                view = new HeaderCell(this.context);
                view.setHeight(46);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null || i != 1) {
                    headerCell.setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
                    return;
                }
                headerCell.setText(LocaleController.getString("AttachLocation", NUM));
            } else if (itemViewType == 2) {
                ((GroupCreateUserCell) viewHolder.itemView).setObject(MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(i - this.usersStartRow)), null, null);
            } else if (itemViewType == 3) {
                ((TextSettingsCell) viewHolder.itemView).setText(GroupCreateFinalActivity.this.currentGroupCreateAddress, false);
            }
        }

        public int getItemViewType(int i) {
            if (GroupCreateFinalActivity.this.currentGroupCreateAddress == null) {
                this.usersStartRow = 2;
            } else if (i == 0) {
                return 0;
            } else {
                if (i == 1) {
                    return 1;
                }
                if (i == 2) {
                    return 3;
                }
                i -= 3;
                this.usersStartRow = 5;
            }
            if (i != 0) {
                return i != 1 ? 2 : 1;
            } else {
                return 0;
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                ((GroupCreateUserCell) viewHolder.itemView).recycle();
            }
        }
    }

    public GroupCreateFinalActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.currentGroupCreateAddress = bundle.getString("address");
        this.currentGroupCreateLocation = (Location) bundle.getParcelable("location");
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        this.imageUpdater = new ImageUpdater();
        ImageUpdater imageUpdater = this.imageUpdater;
        imageUpdater.parentFragment = this;
        imageUpdater.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedContacts.size(); i++) {
            Integer num = (Integer) this.selectedContacts.get(i);
            if (MessagesController.getInstance(this.currentAccount).getUser(num) == null) {
                arrayList.add(num);
            }
        }
        if (!arrayList.isEmpty()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ArrayList arrayList2 = new ArrayList();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$GroupCreateFinalActivity$eKyq7cJMgDwCwGYOq_2k3JE_l-A(this, arrayList2, arrayList, countDownLatch));
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (arrayList.size() != arrayList2.size() || arrayList2.isEmpty()) {
                return false;
            }
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                MessagesController.getInstance(this.currentAccount).putUser((User) it.next(), true);
            }
        }
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$GroupCreateFinalActivity(ArrayList arrayList, ArrayList arrayList2, CountDownLatch countDownLatch) {
        arrayList.addAll(MessagesStorage.getInstance(this.currentAccount).getUsers(arrayList2));
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        this.imageUpdater.clear();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        GroupCreateAdapter groupCreateAdapter = this.adapter;
        if (groupCreateAdapter != null) {
            groupCreateAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.editText.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        String str;
        Drawable combinedDrawable;
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupCreateFinalActivity.this.finishFragment();
                }
            }
        });
        AnonymousClass2 anonymousClass2 = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                size2 -= getPaddingTop();
                measureChildWithMargins(GroupCreateFinalActivity.this.actionBar, i, 0, i2, 0);
                int i3 = 0;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    GroupCreateFinalActivity.this.editText.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == GroupCreateFinalActivity.this.actionBar)) {
                        if (GroupCreateFinalActivity.this.editText == null || !GroupCreateFinalActivity.this.editText.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x008c  */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00bc  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r10 = r9.getChildCount();
                r0 = r9.getKeyboardHeight();
                r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r2 = 0;
                if (r0 > r1) goto L_0x0026;
            L_0x0011:
                r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r0 != 0) goto L_0x0026;
            L_0x0015:
                r0 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r0 != 0) goto L_0x0026;
            L_0x001b:
                r0 = org.telegram.ui.GroupCreateFinalActivity.this;
                r0 = r0.editText;
                r0 = r0.getEmojiPadding();
                goto L_0x0027;
            L_0x0026:
                r0 = 0;
            L_0x0027:
                r9.setBottomClip(r0);
            L_0x002a:
                if (r2 >= r10) goto L_0x00d3;
            L_0x002c:
                r1 = r9.getChildAt(r2);
                r3 = r1.getVisibility();
                r4 = 8;
                if (r3 != r4) goto L_0x003a;
            L_0x0038:
                goto L_0x00cf;
            L_0x003a:
                r3 = r1.getLayoutParams();
                r3 = (android.widget.FrameLayout.LayoutParams) r3;
                r4 = r1.getMeasuredWidth();
                r5 = r1.getMeasuredHeight();
                r6 = r3.gravity;
                r7 = -1;
                if (r6 != r7) goto L_0x004f;
            L_0x004d:
                r6 = 51;
            L_0x004f:
                r7 = r6 & 7;
                r6 = r6 & 112;
                r7 = r7 & 7;
                r8 = 1;
                if (r7 == r8) goto L_0x0063;
            L_0x0058:
                r8 = 5;
                if (r7 == r8) goto L_0x005e;
            L_0x005b:
                r7 = r3.leftMargin;
                goto L_0x006e;
            L_0x005e:
                r7 = r13 - r4;
                r8 = r3.rightMargin;
                goto L_0x006d;
            L_0x0063:
                r7 = r13 - r11;
                r7 = r7 - r4;
                r7 = r7 / 2;
                r8 = r3.leftMargin;
                r7 = r7 + r8;
                r8 = r3.rightMargin;
            L_0x006d:
                r7 = r7 - r8;
            L_0x006e:
                r8 = 16;
                if (r6 == r8) goto L_0x008c;
            L_0x0072:
                r8 = 48;
                if (r6 == r8) goto L_0x0084;
            L_0x0076:
                r8 = 80;
                if (r6 == r8) goto L_0x007d;
            L_0x007a:
                r3 = r3.topMargin;
                goto L_0x0099;
            L_0x007d:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r3 = r3.bottomMargin;
                goto L_0x0097;
            L_0x0084:
                r3 = r3.topMargin;
                r6 = r9.getPaddingTop();
                r3 = r3 + r6;
                goto L_0x0099;
            L_0x008c:
                r6 = r14 - r0;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r6 = r6 / 2;
                r8 = r3.topMargin;
                r6 = r6 + r8;
                r3 = r3.bottomMargin;
            L_0x0097:
                r3 = r6 - r3;
            L_0x0099:
                r6 = org.telegram.ui.GroupCreateFinalActivity.this;
                r6 = r6.editText;
                if (r6 == 0) goto L_0x00ca;
            L_0x00a1:
                r6 = org.telegram.ui.GroupCreateFinalActivity.this;
                r6 = r6.editText;
                r6 = r6.isPopupView(r1);
                if (r6 == 0) goto L_0x00ca;
            L_0x00ad:
                r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r3 == 0) goto L_0x00bc;
            L_0x00b3:
                r3 = r9.getMeasuredHeight();
                r6 = r1.getMeasuredHeight();
                goto L_0x00c9;
            L_0x00bc:
                r3 = r9.getMeasuredHeight();
                r6 = r9.getKeyboardHeight();
                r3 = r3 + r6;
                r6 = r1.getMeasuredHeight();
            L_0x00c9:
                r3 = r3 - r6;
            L_0x00ca:
                r4 = r4 + r7;
                r5 = r5 + r3;
                r1.layout(r7, r3, r4, r5);
            L_0x00cf:
                r2 = r2 + 1;
                goto L_0x002a;
            L_0x00d3:
                r9.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateFinalActivity$AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.fragmentView = anonymousClass2;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        this.fragmentView.setOnTouchListener(-$$Lambda$GroupCreateFinalActivity$zr63HmKznA-wKqWBGncMkUB2788.INSTANCE);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        AnonymousClass3 anonymousClass3 = new LinearLayout(context2) {
            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == GroupCreateFinalActivity.this.listView && GroupCreateFinalActivity.this.shadowDrawable != null) {
                    int measuredHeight = GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight();
                    GroupCreateFinalActivity.this.shadowDrawable.setBounds(0, measuredHeight, getMeasuredWidth(), GroupCreateFinalActivity.this.shadowDrawable.getIntrinsicHeight() + measuredHeight);
                    GroupCreateFinalActivity.this.shadowDrawable.draw(canvas);
                }
                return drawChild;
            }
        };
        anonymousClass3.setOrientation(1);
        anonymousClass2.addView(anonymousClass3, LayoutHelper.createFrame(-1, -1.0f));
        this.editTextContainer = new FrameLayout(context2);
        anonymousClass3.addView(this.editTextContainer, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2) {
            public void invalidate() {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            public void invalidate(int i, int i2, int i3, int i4) {
                if (GroupCreateFinalActivity.this.avatarOverlay != null) {
                    GroupCreateFinalActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(i, i2, i3, i4);
            }
        };
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        this.avatarImage.setContentDescription(LocaleController.getString("ChoosePhoto", NUM));
        int i = 3;
        this.editTextContainer.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        this.avatarOverlay = new View(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                if (GroupCreateFinalActivity.this.avatarImage != null && GroupCreateFinalActivity.this.avatarProgressView.getVisibility() == 0) {
                    paint.setAlpha((int) ((GroupCreateFinalActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f) * GroupCreateFinalActivity.this.avatarProgressView.getAlpha()));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(32.0f), paint);
                }
            }
        };
        this.editTextContainer.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        this.avatarOverlay.setOnClickListener(new -$$Lambda$GroupCreateFinalActivity$PfZeWUefXyO_B3uVzcUzv8fJmaM(this));
        this.avatarEditor = new ImageView(context2) {
            public void invalidate(int i, int i2, int i3, int i4) {
                super.invalidate(i, i2, i3, i4);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }

            public void invalidate() {
                super.invalidate();
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarEditor.setScaleType(ScaleType.CENTER);
        this.avatarEditor.setImageResource(NUM);
        this.avatarEditor.setEnabled(false);
        this.avatarEditor.setClickable(false);
        this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
        this.editTextContainer.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        this.avatarProgressView = new RadialProgressView(context2) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                GroupCreateFinalActivity.this.avatarOverlay.invalidate();
            }
        };
        this.avatarProgressView.setSize(AndroidUtilities.dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.editTextContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        showAvatarProgress(false, false);
        this.editText = new EditTextEmoji(context2, anonymousClass2, this, 0);
        EditTextEmoji editTextEmoji2 = this.editText;
        int i2 = this.chatType;
        if (i2 == 0 || i2 == 4) {
            i2 = NUM;
            str = "EnterGroupNamePlaceholder";
        } else {
            i2 = NUM;
            str = "EnterListName";
        }
        editTextEmoji2.setHint(LocaleController.getString(str, i2));
        String str2 = this.nameToSet;
        if (str2 != null) {
            this.editText.setText(str2);
            this.nameToSet = null;
        }
        this.editText.setFilters(new InputFilter[]{new LengthFilter(100)});
        this.editTextContainer.addView(this.editText, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.listView = new RecyclerListView(context2);
        RecyclerListView recyclerListView = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context2);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        groupCreateDividerItemDecoration.setSkipRows(this.currentGroupCreateAddress != null ? 5 : 2);
        this.listView.addItemDecoration(groupCreateDividerItemDecoration);
        anonymousClass3.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
                }
            }
        });
        this.listView.setOnItemClickListener(new -$$Lambda$GroupCreateFinalActivity$PCsAqUjRC9rH-X9j5r1-vaqTngI(this));
        this.floatingButtonContainer = new FrameLayout(context2);
        float f = 56.0f;
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        } else {
            combinedDrawable = createSimpleSelectorCircleDrawable;
        }
        this.floatingButtonContainer.setBackgroundDrawable(combinedDrawable);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(stateListAnimator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        FrameLayout frameLayout = this.floatingButtonContainer;
        int i3 = VERSION.SDK_INT >= 21 ? 56 : 60;
        float f2 = VERSION.SDK_INT >= 21 ? 56.0f : 60.0f;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        anonymousClass2.addView(frameLayout, LayoutHelper.createFrame(i3, f2, i | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButtonContainer.setOnClickListener(new -$$Lambda$GroupCreateFinalActivity$0NqpPulP1O624fCYT4-zY4Rvar_Q(this));
        this.floatingButtonIcon = new ImageView(context2);
        this.floatingButtonIcon.setScaleType(ScaleType.CENTER);
        this.floatingButtonIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
        this.floatingButtonIcon.setImageResource(NUM);
        this.floatingButtonIcon.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString("Done", NUM));
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        ImageView imageView = this.floatingButtonIcon;
        int i4 = VERSION.SDK_INT >= 21 ? 56 : 60;
        if (VERSION.SDK_INT < 21) {
            f = 60.0f;
        }
        frameLayout2.addView(imageView, LayoutHelper.createFrame(i4, f));
        this.progressView = new ContextProgressView(context2, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.floatingButtonContainer.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$GroupCreateFinalActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new -$$Lambda$GroupCreateFinalActivity$oecJUKdxYSEn20J67Ur1QheSsBc(this));
    }

    public /* synthetic */ void lambda$null$2$GroupCreateFinalActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage(null, null, this.avatarDrawable, null);
        this.avatarEditor.setImageResource(NUM);
    }

    public /* synthetic */ void lambda$createView$5$GroupCreateFinalActivity(View view, int i) {
        if ((view instanceof TextSettingsCell) && AndroidUtilities.isGoogleMapsInstalled(this)) {
            LocationActivity locationActivity = new LocationActivity(4);
            locationActivity.setDialogId(0);
            locationActivity.setDelegate(new -$$Lambda$GroupCreateFinalActivity$vTqL6hCSuWIBlqg728_wrtqzHeA(this));
            presentFragment(locationActivity);
        }
    }

    public /* synthetic */ void lambda$null$4$GroupCreateFinalActivity(MessageMedia messageMedia, int i, boolean z, int i2) {
        this.currentGroupCreateLocation.setLatitude(messageMedia.geo.lat);
        this.currentGroupCreateLocation.setLongitude(messageMedia.geo._long);
        this.currentGroupCreateAddress = messageMedia.address;
    }

    public /* synthetic */ void lambda$createView$6$GroupCreateFinalActivity(View view) {
        if (!this.donePressed) {
            if (this.editText.length() == 0) {
                Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(this.editText, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            AndroidUtilities.hideKeyboard(this.editText);
            this.editText.setEnabled(false);
            if (this.imageUpdater.uploadingImage != null) {
                this.createAfterUpload = true;
            } else {
                showEditDoneProgress(true);
                this.reqId = MessagesController.getInstance(this.currentAccount).createChat(this.editText.getText().toString(), this.selectedContacts, null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
            }
        }
    }

    public void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$GroupCreateFinalActivity$JwhV27OkXZpe1BVw5I-Oh-G-zC4(this, inputFile, photoSize2, photoSize));
    }

    public /* synthetic */ void lambda$didUploadPhoto$7$GroupCreateFinalActivity(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        if (inputFile != null) {
            this.uploadedAvatar = inputFile;
            if (this.createAfterUpload) {
                GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
                if (groupCreateFinalActivityDelegate != null) {
                    groupCreateFinalActivityDelegate.didStartChatCreation();
                }
                MessagesController.getInstance(this.currentAccount).createChat(this.editText.getText().toString(), this.selectedContacts, null, this.chatType, this.currentGroupCreateLocation, this.currentGroupCreateAddress, this);
            }
            showAvatarProgress(false, true);
            this.avatarEditor.setImageDrawable(null);
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, null);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.editText.getText().toString();
    }

    public void setDelegate(GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate) {
        this.delegate = groupCreateFinalActivityDelegate;
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                this.avatarAnimation = new AnimatorSet();
                AnimatorSet animatorSet2;
                Animator[] animatorArr;
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    this.avatarEditor.setVisibility(0);
                    animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (GroupCreateFinalActivity.this.avatarAnimation != null && GroupCreateFinalActivity.this.avatarEditor != null) {
                            if (z) {
                                GroupCreateFinalActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                GroupCreateFinalActivity.this.avatarProgressView.setVisibility(4);
                            }
                            GroupCreateFinalActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        GroupCreateFinalActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (z) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.imageUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        String str;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            str = imageUpdater.currentPicturePath;
            if (str != null) {
                bundle.putString("path", str);
            }
        }
        EditTextEmoji editTextEmoji = this.editText;
        if (editTextEmoji != null) {
            str = editTextEmoji.getText().toString();
            if (str != null && str.length() != 0) {
                bundle.putString("nameTextView", str);
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = bundle.getString("path");
        }
        String string = bundle.getString("nameTextView");
        if (string != null) {
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setText(string);
            } else {
                this.nameToSet = string;
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.editText.openKeyboard();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                i = ((Integer) objArr[0]).intValue();
                if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                    i2 = this.listView.getChildCount();
                    while (i3 < i2) {
                        View childAt = this.listView.getChildAt(i3);
                        if (childAt instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) childAt).update(i);
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatDidFailCreate) {
            this.reqId = 0;
            this.donePressed = false;
            showEditDoneProgress(false);
            EditTextEmoji editTextEmoji = this.editText;
            if (editTextEmoji != null) {
                editTextEmoji.setEnabled(true);
            }
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate = this.delegate;
            if (groupCreateFinalActivityDelegate != null) {
                groupCreateFinalActivityDelegate.didFailChatCreation();
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            this.reqId = 0;
            i = ((Integer) objArr[0]).intValue();
            GroupCreateFinalActivityDelegate groupCreateFinalActivityDelegate2 = this.delegate;
            if (groupCreateFinalActivityDelegate2 != null) {
                groupCreateFinalActivityDelegate2.didFinishChatCreation(this, i);
            } else {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle bundle = new Bundle();
                bundle.putInt("chat_id", i);
                presentFragment(new ChatActivity(bundle), true);
            }
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(i, this.uploadedAvatar, this.avatar, this.avatarBig);
            }
        }
    }

    private void showEditDoneProgress(boolean z) {
        final boolean z2 = z;
        if (this.floatingButtonIcon != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            String str = "alpha";
            String str2 = "scaleY";
            String str3 = "scaleX";
            if (z2) {
                this.progressView.setVisibility(0);
                this.floatingButtonContainer.setEnabled(false);
                AnimatorSet animatorSet2 = this.doneItemAnimation;
                Animator[] animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButtonIcon, str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButtonIcon, str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButtonIcon, str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.floatingButtonIcon.setVisibility(0);
                this.floatingButtonContainer.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                Animator[] animatorArr2 = new Animator[6];
                animatorArr2[0] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{0.1f});
                animatorArr2[1] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{0.1f});
                animatorArr2[2] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{0.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(this.floatingButtonIcon, str3, new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(this.floatingButtonIcon, str2, new float[]{1.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(this.floatingButtonIcon, str, new float[]{1.0f});
                animatorSet.playTogether(animatorArr2);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animator)) {
                        if (z2) {
                            GroupCreateFinalActivity.this.floatingButtonIcon.setVisibility(4);
                        } else {
                            GroupCreateFinalActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animator)) {
                        GroupCreateFinalActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$GroupCreateFinalActivity$VPEySrwsnDav95hCH-qJfgjLqoE -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe = new -$$Lambda$GroupCreateFinalActivity$VPEySrwsnDav95hCH-qJfgjLqoE(this);
        r10 = new ThemeDescription[35];
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[11] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[12] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
        r10[13] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
        r10[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        r10[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        r10[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r10[18] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        r10[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, "groupcreate_sectionText");
        view = this.listView;
        int i = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "statusTextView";
        r10[20] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueText");
        r10[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, "windowBackgroundWhiteGrayText");
        -$$Lambda$GroupCreateFinalActivity$VPEySrwsnDav95hCH-qJfgjLqoE -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2 = -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe;
        r10[22] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_text");
        r10[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundRed");
        r10[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundOrange");
        r10[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundViolet");
        r10[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundGreen");
        r10[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundCyan");
        r10[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundBlue");
        r10[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreatefinalactivity_vpeysrwsndav95hch-qjfgjlqoe2, "avatar_backgroundPink");
        r10[30] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[31] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2");
        r10[32] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2");
        r10[33] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r10[34] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        return r10;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$8$GroupCreateFinalActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
    }
}
