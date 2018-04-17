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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
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
    class C14024 implements OnClickListener {

        /* renamed from: org.telegram.ui.GroupCreateFinalActivity$4$1 */
        class C14011 implements DialogInterface.OnClickListener {
            C14011() {
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

        C14024() {
        }

        public void onClick(View view) {
            if (GroupCreateFinalActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(GroupCreateFinalActivity.this.getParentActivity());
                builder.setItems(GroupCreateFinalActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)}, new C14011());
                GroupCreateFinalActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$5 */
    class C14035 implements TextWatcher {
        C14035() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            GroupCreateFinalActivity.this.avatarDrawable.setInfo(5, GroupCreateFinalActivity.this.editText.length() > 0 ? GroupCreateFinalActivity.this.editText.getText().toString() : null, null, false);
            GroupCreateFinalActivity.this.avatarImage.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$2 */
    class C21372 extends ActionBarMenuOnItemClick {
        C21372() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupCreateFinalActivity.this.finishFragment();
            } else if (id == 1 && !GroupCreateFinalActivity.this.donePressed) {
                if (GroupCreateFinalActivity.this.editText.length() == 0) {
                    Vibrator v = (Vibrator) GroupCreateFinalActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(GroupCreateFinalActivity.this.editText, 2.0f, 0);
                    return;
                }
                GroupCreateFinalActivity.this.donePressed = true;
                AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
                GroupCreateFinalActivity.this.editText.setEnabled(false);
                if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != null) {
                    GroupCreateFinalActivity.this.createAfterUpload = true;
                } else {
                    GroupCreateFinalActivity.this.showEditDoneProgress(true);
                    GroupCreateFinalActivity.this.reqId = MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$6 */
    class C21386 extends OnScrollListener {
        C21386() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateFinalActivity$9 */
    class C21399 implements ThemeDescriptionDelegate {
        C21399() {
        }

        public void didSetColor() {
            if (GroupCreateFinalActivity.this.listView != null) {
                int count = GroupCreateFinalActivity.this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = GroupCreateFinalActivity.this.listView.getChildAt(a);
                    if (child instanceof GroupCreateUserCell) {
                        ((GroupCreateUserCell) child).update(0);
                    }
                }
                GroupCreateFinalActivity.this.avatarDrawable.setInfo(5, GroupCreateFinalActivity.this.editText.length() > 0 ? GroupCreateFinalActivity.this.editText.getText().toString() : null, null, false);
                GroupCreateFinalActivity.this.avatarImage.invalidate();
            }
        }
    }

    public class GroupCreateAdapter extends SelectionAdapter {
        private Context context;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
        }

        public int getItemCount() {
            return 1 + GroupCreateFinalActivity.this.selectedContacts.size();
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new GroupCreateUserCell(this.context, false);
            } else {
                view = new GroupCreateSectionCell(this.context);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() != 0) {
                holder.itemView.setUser(MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(position - 1)), null, null);
            } else {
                holder.itemView.setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
            }
        }

        public int getItemViewType(int position) {
            if (position != 0) {
                return 1;
            }
            return 0;
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.getItemViewType() == 1) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }
    }

    public GroupCreateFinalActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.avatarDrawable = new AvatarDrawable();
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        final ArrayList<Integer> usersToLoad = new ArrayList();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            Integer uid = (Integer) this.selectedContacts.get(a);
            if (MessagesController.getInstance(this.currentAccount).getUser(uid) == null) {
                usersToLoad.add(uid);
            }
        }
        if (!usersToLoad.isEmpty()) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final ArrayList<User> users = new ArrayList();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                public void run() {
                    users.addAll(MessagesStorage.getInstance(GroupCreateFinalActivity.this.currentAccount).getUsers(usersToLoad));
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (usersToLoad.size() != users.size() || users.isEmpty()) {
                return false;
            }
            Iterator it = users.iterator();
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NewGroup", R.string.NewGroup));
        this.actionBar.setActionBarMenuOnItemClick(new C21372());
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context2, 1);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView.setVisibility(4);
        this.fragmentView = new LinearLayout(context2) {
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == GroupCreateFinalActivity.this.listView) {
                    GroupCreateFinalActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight());
                }
                return result;
            }
        };
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(1);
        this.editTextContainer = new FrameLayout(context2);
        linearLayout.addView(this.editTextContainer, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, this.chatType == 1);
        r0.avatarImage.setImageDrawable(r0.avatarDrawable);
        int i3 = 3;
        float f = 16.0f;
        r0.editTextContainer.addView(r0.avatarImage, LayoutHelper.createFrame(64, 64.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 0.0f : 16.0f, 16.0f, LocaleController.isRTL ? 16.0f : 0.0f, 16.0f));
        r0.avatarDrawable.setDrawPhoto(true);
        r0.avatarImage.setOnClickListener(new C14024());
        r0.editText = new EditTextBoldCursor(context2);
        EditTextBoldCursor editTextBoldCursor = r0.editText;
        if (r0.chatType == 0) {
            str = "EnterGroupNamePlaceholder";
            i = R.string.EnterGroupNamePlaceholder;
        } else {
            str = "EnterListName";
            i = R.string.EnterListName;
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
        FrameLayout frameLayout = r0.editTextContainer;
        View view = r0.editText;
        float f2 = LocaleController.isRTL ? 16.0f : 96.0f;
        if (LocaleController.isRTL) {
            f = 96.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 16, f2, 0.0f, f, 0.0f));
        r0.editText.addTextChangedListener(new C14035());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        r0.listView = new RecyclerListView(context2);
        RecyclerView recyclerView = r0.listView;
        Adapter groupCreateAdapter = new GroupCreateAdapter(context2);
        r0.adapter = groupCreateAdapter;
        recyclerView.setAdapter(groupCreateAdapter);
        r0.listView.setLayoutManager(linearLayoutManager);
        r0.listView.setVerticalScrollBarEnabled(false);
        recyclerView = r0.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerView.setVerticalScrollbarPosition(i2);
        r0.listView.addItemDecoration(new GroupCreateDividerItemDecoration());
        linearLayout.addView(r0.listView, LayoutHelper.createLinear(-1, -1));
        r0.listView.setOnScrollListener(new C21386());
        return r0.fragmentView;
    }

    public void didUploadedPhoto(final InputFile file, final PhotoSize small, PhotoSize big) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                GroupCreateFinalActivity.this.uploadedAvatar = file;
                GroupCreateFinalActivity.this.avatar = small.location;
                GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                if (GroupCreateFinalActivity.this.createAfterUpload) {
                    MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
                }
            }
        });
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
        if (this.editText != null) {
            String text = this.editText.getText().toString();
            if (text != null && text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
        String text = args.getString("nameTextView");
        if (text == null) {
            return;
        }
        if (this.editText != null) {
            this.editText.setText(text);
        } else {
            this.nameToSet = text;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.editText.requestFocus();
            AndroidUtilities.showKeyboard(this.editText);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int a = 0;
        int mask;
        if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                mask = ((Integer) args[0]).intValue();
                if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                    int count = this.listView.getChildCount();
                    while (a < count) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                        a++;
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidFailCreate) {
            this.reqId = 0;
            this.donePressed = false;
            showEditDoneProgress(false);
            if (this.editText != null) {
                this.editText.setEnabled(true);
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            this.reqId = 0;
            mask = ((Integer) args[0]).intValue();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle args2 = new Bundle();
            args2.putInt("chat_id", mask);
            presentFragment(new ChatActivity(args2), true);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(mask, this.uploadedAvatar);
            }
        }
    }

    private void showEditDoneProgress(final boolean show) {
        if (this.doneItem != null) {
            if (this.doneItemAnimation != null) {
                this.doneItemAnimation.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (show) {
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
                public void onAnimationEnd(Animator animation) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animation)) {
                        if (show) {
                            GroupCreateFinalActivity.this.doneItem.getImageView().setVisibility(4);
                        } else {
                            GroupCreateFinalActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (GroupCreateFinalActivity.this.doneItemAnimation != null && GroupCreateFinalActivity.this.doneItemAnimation.equals(animation)) {
                        GroupCreateFinalActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new C21399();
        r9 = new ThemeDescription[34];
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[11] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[12] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        r9[13] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        r9[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        r9[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        r9[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        r9[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r9[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        r9[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        r9[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, сellDelegate, Theme.key_avatar_text);
        r9[23] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        r9[24] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        r9[25] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        r9[26] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        r9[27] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        r9[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        r9[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        r9[30] = new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2);
        r9[31] = new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2);
        r9[32] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[33] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        return r9;
    }
}
