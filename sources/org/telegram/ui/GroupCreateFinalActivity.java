package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater = new AvatarUpdater();
    private int chatType = 0;
    private boolean createAfterUpload;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private EditTextBoldCursor editText;
    private FrameLayout editTextContainer;
    private RecyclerView listView;
    private String nameToSet;
    private ContextProgressView progressView;
    private int reqId;
    private ArrayList<Integer> selectedContacts;
    private InputFile uploadedAvatar;

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$4 */
    class C14084 implements OnClickListener {

        /* renamed from: org.telegram.ui.GroupCreateFinalActivity$4$1 */
        class C14071 implements DialogInterface.OnClickListener {
            C14071() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    GroupCreateFinalActivity.this.avatarUpdater.openCamera();
                } else if (i == 1) {
                    GroupCreateFinalActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    GroupCreateFinalActivity.this.avatar = null;
                    GroupCreateFinalActivity.this.uploadedAvatar = null;
                    GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                }
            }
        }

        C14084() {
        }

        public void onClick(View view) {
            if (GroupCreateFinalActivity.this.getParentActivity() != null) {
                view = new Builder(GroupCreateFinalActivity.this.getParentActivity());
                view.setItems(GroupCreateFinalActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("DeletePhoto", C0446R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley)}, new C14071());
                GroupCreateFinalActivity.this.showDialog(view.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$5 */
    class C14095 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C14095() {
        }

        public void afterTextChanged(Editable editable) {
            GroupCreateFinalActivity.this.avatarDrawable.setInfo(5, GroupCreateFinalActivity.this.editText.length() > 0 ? GroupCreateFinalActivity.this.editText.getText().toString() : null, null, false);
            GroupCreateFinalActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$2 */
    class C21432 extends ActionBarMenuOnItemClick {
        C21432() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                GroupCreateFinalActivity.this.finishFragment();
            } else if (i != 1 || GroupCreateFinalActivity.this.donePressed != 0) {
            } else {
                if (GroupCreateFinalActivity.this.editText.length() == 0) {
                    Vibrator vibrator = (Vibrator) GroupCreateFinalActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(GroupCreateFinalActivity.this.editText, 2.0f, 0);
                    return;
                }
                GroupCreateFinalActivity.this.donePressed = true;
                AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
                GroupCreateFinalActivity.this.editText.setEnabled(false);
                if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != 0) {
                    GroupCreateFinalActivity.this.createAfterUpload = true;
                } else {
                    GroupCreateFinalActivity.this.showEditDoneProgress(true);
                    GroupCreateFinalActivity.this.reqId = MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$6 */
    class C21446 extends OnScrollListener {
        C21446() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1) {
                AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$9 */
    class C21459 implements ThemeDescriptionDelegate {
        C21459() {
        }

        public void didSetColor() {
            if (GroupCreateFinalActivity.this.listView != null) {
                int childCount = GroupCreateFinalActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = GroupCreateFinalActivity.this.listView.getChildAt(i);
                    if (childAt instanceof GroupCreateUserCell) {
                        ((GroupCreateUserCell) childAt).update(0);
                    }
                }
                GroupCreateFinalActivity.this.avatarDrawable.setInfo(5, GroupCreateFinalActivity.this.editText.length() > 0 ? GroupCreateFinalActivity.this.editText.getText().toString() : null, null, false);
                GroupCreateFinalActivity.this.avatarImage.invalidate();
            }
        }
    }

    public class GroupCreateAdapter extends SelectionAdapter {
        private Context context;

        public int getItemViewType(int i) {
            return i != 0 ? 1 : 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public GroupCreateAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return 1 + GroupCreateFinalActivity.this.selectedContacts.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new GroupCreateUserCell(this.context, false);
            } else {
                viewGroup = new GroupCreateSectionCell(this.context);
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() != 0) {
                ((GroupCreateUserCell) viewHolder.itemView).setUser(MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(i - 1)), null, null);
            } else {
                ((GroupCreateSectionCell) viewHolder.itemView).setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 1) {
                ((GroupCreateUserCell) viewHolder.itemView).recycle();
            }
        }
    }

    public GroupCreateFinalActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.avatarDrawable = new AvatarDrawable();
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedContacts.size(); i++) {
            Integer num = (Integer) this.selectedContacts.get(i);
            if (MessagesController.getInstance(this.currentAccount).getUser(num) == null) {
                arrayList.add(num);
            }
        }
        if (!arrayList.isEmpty()) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final ArrayList arrayList2 = new ArrayList();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    arrayList2.addAll(MessagesStorage.getInstance(GroupCreateFinalActivity.this.currentAccount).getUsers(arrayList));
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m3e(e);
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

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.clear();
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        String str;
        int i;
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", C0446R.string.NewGroup));
        this.actionBar.setActionBarMenuOnItemClick(new C21432());
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context2, 1);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView.setVisibility(4);
        this.fragmentView = new LinearLayout(context2) {
            protected boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
                if (view == GroupCreateFinalActivity.this.listView) {
                    GroupCreateFinalActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight());
                }
                return j;
            }
        };
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.editTextContainer = new FrameLayout(context2);
        linearLayout.addView(this.editTextContainer, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, this.chatType == 1);
        r0.avatarImage.setImageDrawable(r0.avatarDrawable);
        int i3 = 3;
        r0.editTextContainer.addView(r0.avatarImage, LayoutHelper.createFrame(64, 64.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        r0.avatarDrawable.setDrawPhoto(true);
        r0.avatarImage.setOnClickListener(new C14084());
        r0.editText = new EditTextBoldCursor(context2);
        EditTextBoldCursor editTextBoldCursor = r0.editText;
        if (r0.chatType == 0) {
            str = "EnterGroupNamePlaceholder";
            i = C0446R.string.EnterGroupNamePlaceholder;
        } else {
            str = "EnterListName";
            i = C0446R.string.EnterListName;
        }
        editTextBoldCursor.setHint(LocaleController.getString(str, i));
        if (r0.nameToSet != null) {
            r0.editText.setText(r0.nameToSet);
            r0.nameToSet = null;
        }
        r0.editText.setMaxLines(4);
        EditTextBoldCursor editTextBoldCursor2 = r0.editText;
        if (LocaleController.isRTL) {
            i3 = 5;
        }
        editTextBoldCursor2.setGravity(16 | i3);
        r0.editText.setTextSize(1, 18.0f);
        r0.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.editText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.editText.setImeOptions(268435456);
        r0.editText.setInputType(MessagesController.UPDATE_MASK_CHAT_ADMINS);
        r0.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        r0.editText.setFilters(new InputFilter[]{new LengthFilter(100)});
        r0.editText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.editText.setCursorWidth(1.5f);
        r0.editTextContainer.addView(r0.editText, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 16.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 16.0f, 0.0f));
        r0.editText.addTextChangedListener(new C14095());
        LayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        r0.listView = new RecyclerListView(context2);
        RecyclerView recyclerView = r0.listView;
        Adapter groupCreateAdapter = new GroupCreateAdapter(context2);
        r0.adapter = groupCreateAdapter;
        recyclerView.setAdapter(groupCreateAdapter);
        r0.listView.setLayoutManager(linearLayoutManager);
        r0.listView.setVerticalScrollBarEnabled(false);
        RecyclerView recyclerView2 = r0.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerView2.setVerticalScrollbarPosition(i2);
        r0.listView.addItemDecoration(new GroupCreateDividerItemDecoration());
        linearLayout.addView(r0.listView, LayoutHelper.createLinear(-1, -1));
        r0.listView.setOnScrollListener(new C21446());
        return r0.fragmentView;
    }

    public void didUploadedPhoto(final InputFile inputFile, final PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                GroupCreateFinalActivity.this.uploadedAvatar = inputFile;
                GroupCreateFinalActivity.this.avatar = photoSize.location;
                GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                if (GroupCreateFinalActivity.this.createAfterUpload) {
                    MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
                }
            }
        });
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
            bundle.putString("path", this.avatarUpdater.currentPicturePath);
        }
        if (this.editText != null) {
            String obj = this.editText.getText().toString();
            if (obj != null && obj.length() != 0) {
                bundle.putString("nameTextView", obj);
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = bundle.getString("path");
        }
        bundle = bundle.getString("nameTextView");
        if (bundle == null) {
            return;
        }
        if (this.editText != null) {
            this.editText.setText(bundle);
        } else {
            this.nameToSet = bundle;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.editText.requestFocus();
            AndroidUtilities.showKeyboard(this.editText);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != 0) {
                i = ((Integer) objArr[0]).intValue();
                if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                    i2 = this.listView.getChildCount();
                    while (i3 < i2) {
                        objArr = this.listView.getChildAt(i3);
                        if (objArr instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) objArr).update(i);
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatDidFailCreate) {
            this.reqId = 0;
            this.donePressed = false;
            showEditDoneProgress(false);
            if (this.editText != 0) {
                this.editText.setEnabled(true);
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            this.reqId = 0;
            i = ((Integer) objArr[0]).intValue();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            i2 = new Bundle();
            i2.putInt("chat_id", i);
            presentFragment(new ChatActivity(i2), true);
            if (this.uploadedAvatar != 0) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(i, this.uploadedAvatar);
            }
        }
    }

    private void showEditDoneProgress(final boolean z) {
        if (this.doneItem != null) {
            if (this.doneItemAnimation != null) {
                this.doneItemAnimation.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (z) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.doneItem.getImageView().setVisibility(0);
                this.doneItem.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animator) != null) {
                        if (z == null) {
                            GroupCreateFinalActivity.this.progressView.setVisibility(4);
                        } else {
                            GroupCreateFinalActivity.this.doneItem.getImageView().setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animator) != null) {
                        GroupCreateFinalActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        C21459 c21459 = new C21459();
        r10 = new ThemeDescription[34];
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[11] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[12] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        r10[13] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        r10[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r10[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r10[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        r10[17] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        r10[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r10[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r10[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        r10[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        Class[] clsArr = new Class[]{GroupCreateUserCell.class};
        Drawable[] drawableArr = new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable};
        C21459 c214592 = c21459;
        r10[22] = new ThemeDescription(this.listView, 0, clsArr, null, drawableArr, c214592, Theme.key_avatar_text);
        r10[23] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundRed);
        r10[24] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundOrange);
        r10[25] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundViolet);
        r10[26] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundGreen);
        r10[27] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundCyan);
        r10[28] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundBlue);
        r10[29] = new ThemeDescription(null, 0, null, null, null, c214592, Theme.key_avatar_backgroundPink);
        r10[30] = new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2);
        r10[31] = new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2);
        r10[32] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[33] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        return r10;
    }
}
