package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class GroupCreateActivity extends BaseFragment implements OnClickListener, NotificationCenterDelegate {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int chatId;
    private int chatType = 0;
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private View doneButton;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private boolean isNeverShare;
    private GroupCreateDividerItemDecoration itemDecoration;
    private RecyclerListView listView;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<GroupCreateSpan> selectedContacts = new SparseArray();
    private SpansContainer spansContainer;

    /* renamed from: org.telegram.ui.GroupCreateActivity$5 */
    class C13955 implements Callback {
        C13955() {
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$6 */
    class C13966 implements OnEditorActionListener {
        C13966() {
        }

        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return actionId == 6 && GroupCreateActivity.this.onDonePressed();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$7 */
    class C13977 implements OnKeyListener {
        private boolean wasEmpty;

        C13977() {
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 67) {
                boolean z = true;
                if (event.getAction() == 0) {
                    if (GroupCreateActivity.this.editText.length() != 0) {
                        z = false;
                    }
                    this.wasEmpty = z;
                } else if (event.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                    GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                    GroupCreateActivity.this.updateHint();
                    GroupCreateActivity.this.checkVisibleRows();
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$8 */
    class C13988 implements TextWatcher {
        C13988() {
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            if (GroupCreateActivity.this.editText.length() != 0) {
                GroupCreateActivity.this.searching = true;
                GroupCreateActivity.this.searchWas = true;
                GroupCreateActivity.this.adapter.setSearching(true);
                GroupCreateActivity.this.itemDecoration.setSearching(true);
                GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                GroupCreateActivity.this.listView.setFastScrollVisible(false);
                GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                return;
            }
            GroupCreateActivity.this.closeSearch();
        }
    }

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        /* renamed from: org.telegram.ui.GroupCreateActivity$SpansContainer$1 */
        class C14031 extends AnimatorListenerAdapter {
            C14031() {
            }

            public void onAnimationEnd(Animator animator) {
                SpansContainer.this.addingSpan = null;
                SpansContainer.this.currentAnimation = null;
                SpansContainer.this.animationStarted = false;
                GroupCreateActivity.this.editText.setAllowDrawCursor(true);
            }
        }

        public SpansContainer(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            SpansContainer spansContainer = this;
            int count = getChildCount();
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(32.0f);
            float f = 12.0f;
            int y = AndroidUtilities.dp(12.0f);
            int allY = AndroidUtilities.dp(12.0f);
            int allCurrentLineWidth = 0;
            int y2 = y;
            y = 0;
            int a = 0;
            while (a < count) {
                int x;
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                    if (child != spansContainer.removingSpan && child.getMeasuredWidth() + y > maxWidth) {
                        y2 += child.getMeasuredHeight() + AndroidUtilities.dp(f);
                        y = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(f);
                        allCurrentLineWidth = 0;
                    }
                    x = AndroidUtilities.dp(16.0f) + y;
                    if (!spansContainer.animationStarted) {
                        if (child == spansContainer.removingSpan) {
                            child.setTranslationX((float) (AndroidUtilities.dp(16.0f) + allCurrentLineWidth));
                            child.setTranslationY((float) allY);
                        } else if (spansContainer.removingSpan != null) {
                            if (child.getTranslationX() != ((float) x)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(child, "translationX", new float[]{(float) x}));
                            }
                            if (child.getTranslationY() != ((float) y2)) {
                                spansContainer.animators.add(ObjectAnimator.ofFloat(child, "translationY", new float[]{(float) y2}));
                            }
                        } else {
                            child.setTranslationX((float) x);
                            child.setTranslationY((float) y2);
                        }
                    }
                    if (child != spansContainer.removingSpan) {
                        y += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                a++;
                f = 12.0f;
            }
            if (AndroidUtilities.isTablet()) {
                a = AndroidUtilities.dp(366.0f) / 3;
            } else {
                a = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (maxWidth - y < a) {
                y = 0;
                y2 += AndroidUtilities.dp(44.0f);
            }
            if (maxWidth - allCurrentLineWidth < a) {
                allY += AndroidUtilities.dp(44.0f);
            }
            GroupCreateActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(maxWidth - y, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            int i;
            if (spansContainer.animationStarted) {
                i = maxWidth;
                if (!(spansContainer.currentAnimation == null || GroupCreateActivity.this.ignoreScrollEvent || spansContainer.removingSpan != null)) {
                    GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
                }
            } else {
                int currentHeight = AndroidUtilities.dp(44.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + y;
                GroupCreateActivity.this.fieldY = y2;
                if (spansContainer.currentAnimation != null) {
                    boolean z;
                    if (GroupCreateActivity.this.containerHeight != AndroidUtilities.dp(44.0f) + y2) {
                        spansContainer.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{x}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) fieldX)) {
                        spansContainer.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[]{(float) fieldX}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        ArrayList arrayList = spansContainer.animators;
                        float[] fArr = new float[1];
                        z = false;
                        fArr[0] = (float) GroupCreateActivity.this.fieldY;
                        arrayList.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", fArr));
                    } else {
                        z = false;
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    spansContainer.currentAnimation.playTogether(spansContainer.animators);
                    spansContainer.currentAnimation.start();
                    spansContainer.animationStarted = true;
                } else {
                    i = maxWidth;
                    GroupCreateActivity.this.containerHeight = currentHeight;
                    GroupCreateActivity.this.editText.setTranslationX((float) fieldX);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            }
            setMeasuredDimension(width, GroupCreateActivity.this.containerHeight);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            GroupCreateActivity.this.allSpans.add(span);
            GroupCreateActivity.this.selectedContacts.put(span.getUid(), span);
            GroupCreateActivity.this.editText.setHintVisible(false);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new C14031());
            this.currentAnimation.setDuration(150);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(span.getUid());
            GroupCreateActivity.this.allSpans.remove(span);
            span.setOnClickListener(null);
            if (this.currentAnimation != null) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$1 */
    class C21391 extends ActionBarMenuOnItemClick {
        C21391() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupCreateActivity.this.finishFragment();
            } else if (id == 1) {
                GroupCreateActivity.this.onDonePressed();
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupCreateActivity$9 */
    class C21419 implements OnItemClickListener {
        C21419() {
        }

        public void onItemClick(View view, int position) {
            if (view instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) view;
                User user = cell.getUser();
                if (user != null) {
                    boolean z = false;
                    boolean z2 = GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0;
                    boolean exists = z2;
                    if (z2) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.selectedContacts.get(user.id));
                    } else if (GroupCreateActivity.this.maxCount != 0 && GroupCreateActivity.this.selectedContacts.size() == GroupCreateActivity.this.maxCount) {
                        return;
                    } else {
                        if (GroupCreateActivity.this.chatType == 0 && GroupCreateActivity.this.selectedContacts.size() == MessagesController.getInstance(GroupCreateActivity.this.currentAccount).maxGroupCount) {
                            Builder builder = new Builder(GroupCreateActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("SoftUserLimitAlert", R.string.SoftUserLimitAlert));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            GroupCreateActivity.this.showDialog(builder.create());
                            return;
                        }
                        MessagesController.getInstance(GroupCreateActivity.this.currentAccount).putUser(user, GroupCreateActivity.this.searching ^ true);
                        GroupCreateSpan span = new GroupCreateSpan(GroupCreateActivity.this.editText.getContext(), user);
                        GroupCreateActivity.this.spansContainer.addSpan(span);
                        span.setOnClickListener(GroupCreateActivity.this);
                    }
                    GroupCreateActivity.this.updateHint();
                    if (!GroupCreateActivity.this.searching) {
                        if (!GroupCreateActivity.this.searchWas) {
                            if (!exists) {
                                z = true;
                            }
                            cell.setChecked(z, true);
                            if (GroupCreateActivity.this.editText.length() > 0) {
                                GroupCreateActivity.this.editText.setText(null);
                            }
                        }
                    }
                    AndroidUtilities.showKeyboard(GroupCreateActivity.this.editText);
                    if (GroupCreateActivity.this.editText.length() > 0) {
                        GroupCreateActivity.this.editText.setText(null);
                    }
                }
            }
        }
    }

    public class GroupCreateAdapter extends FastScrollAdapter {
        private ArrayList<User> contacts = new ArrayList();
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<User> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public GroupCreateAdapter(Context ctx) {
            this.context = ctx;
            ArrayList<TL_contact> arrayList = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
            for (int a = 0; a < arrayList.size(); a++) {
                User user = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(a)).user_id));
                if (!(user == null || user.self)) {
                    if (!user.deleted) {
                        this.contacts.add(user);
                    }
                }
            }
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(GroupCreateActivity.this) {
                public void onDataSetChanged() {
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }
            });
        }

        public void setSearching(boolean value) {
            if (this.searching != value) {
                this.searching = value;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int position) {
            if (position >= 0) {
                if (position < this.contacts.size()) {
                    User user = (User) this.contacts.get(position);
                    if (user == null) {
                        return null;
                    }
                    if (LocaleController.nameDisplayOrder == 1) {
                        if (!TextUtils.isEmpty(user.first_name)) {
                            return user.first_name.substring(0, 1).toUpperCase();
                        }
                        if (!TextUtils.isEmpty(user.last_name)) {
                            return user.last_name.substring(0, 1).toUpperCase();
                        }
                    } else if (!TextUtils.isEmpty(user.last_name)) {
                        return user.last_name.substring(0, 1).toUpperCase();
                    } else {
                        if (!TextUtils.isEmpty(user.first_name)) {
                            return user.first_name.substring(0, 1).toUpperCase();
                        }
                    }
                    return TtmlNode.ANONYMOUS_REGION_ID;
                }
            }
            return null;
        }

        public int getItemCount() {
            if (!this.searching) {
                return this.contacts.size();
            }
            int count = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            if (globalCount != 0) {
                count += globalCount + 1;
            }
            return count;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType != 0) {
                view = new GroupCreateUserCell(this.context, true);
            } else {
                view = new GroupCreateSectionCell(this.context);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            Exception exception;
            User user;
            GroupCreateAdapter groupCreateAdapter = this;
            ViewHolder viewHolder = holder;
            int i = position;
            if (holder.getItemViewType() != 0) {
                User user2;
                GroupCreateUserCell cell = viewHolder.itemView;
                CharSequence username = null;
                CharSequence name = null;
                if (groupCreateAdapter.searching) {
                    String charSequence;
                    StringBuilder stringBuilder;
                    SpannableStringBuilder spannableStringBuilder;
                    int indexOf;
                    int index;
                    CharSequence username2;
                    int localCount = groupCreateAdapter.searchResult.size();
                    int globalCount = groupCreateAdapter.searchAdapterHelper.getGlobalSearch().size();
                    if (i >= 0 && i < localCount) {
                        user2 = (User) groupCreateAdapter.searchResult.get(i);
                    } else if (i <= localCount || i > globalCount + localCount) {
                        user2 = null;
                        if (user2 != null) {
                            if (i >= localCount) {
                                name = (CharSequence) groupCreateAdapter.searchResultNames.get(i);
                                if (!(name == null || TextUtils.isEmpty(user2.username))) {
                                    charSequence = name.toString();
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("@");
                                    stringBuilder.append(user2.username);
                                    if (charSequence.startsWith(stringBuilder.toString())) {
                                        username = name;
                                        name = null;
                                    }
                                }
                            } else if (i > localCount && !TextUtils.isEmpty(user2.username)) {
                                charSequence = groupCreateAdapter.searchAdapterHelper.getLastFoundUsername();
                                if (charSequence.startsWith("@")) {
                                    charSequence = charSequence.substring(1);
                                }
                                try {
                                    spannableStringBuilder = new SpannableStringBuilder();
                                    spannableStringBuilder.append("@");
                                    spannableStringBuilder.append(user2.username);
                                    indexOf = user2.username.toLowerCase().indexOf(charSequence);
                                    index = indexOf;
                                    if (indexOf == -1) {
                                        indexOf = charSequence.length();
                                        if (index != 0) {
                                            indexOf++;
                                        } else {
                                            index++;
                                        }
                                        username2 = null;
                                        try {
                                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + indexOf, 33);
                                        } catch (Exception e) {
                                            exception = e;
                                            username = user2.username;
                                            user = user2;
                                            cell.setUser(user, name, username);
                                            cell.setChecked(GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0, false);
                                            return;
                                        }
                                    }
                                    username2 = null;
                                    username = spannableStringBuilder;
                                } catch (Exception e2) {
                                    username2 = null;
                                    exception = e2;
                                    username = user2.username;
                                    user = user2;
                                    cell.setUser(user, name, username);
                                    if (GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0) {
                                    }
                                    cell.setChecked(GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0, false);
                                    return;
                                }
                            }
                        }
                        username = null;
                    } else {
                        user2 = (User) groupCreateAdapter.searchAdapterHelper.getGlobalSearch().get((i - localCount) - 1);
                    }
                    if (user2 != null) {
                        if (i >= localCount) {
                            charSequence = groupCreateAdapter.searchAdapterHelper.getLastFoundUsername();
                            if (charSequence.startsWith("@")) {
                                charSequence = charSequence.substring(1);
                            }
                            spannableStringBuilder = new SpannableStringBuilder();
                            spannableStringBuilder.append("@");
                            spannableStringBuilder.append(user2.username);
                            indexOf = user2.username.toLowerCase().indexOf(charSequence);
                            index = indexOf;
                            if (indexOf == -1) {
                                username2 = null;
                            } else {
                                indexOf = charSequence.length();
                                if (index != 0) {
                                    index++;
                                } else {
                                    indexOf++;
                                }
                                username2 = null;
                                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + indexOf, 33);
                            }
                            username = spannableStringBuilder;
                        } else {
                            name = (CharSequence) groupCreateAdapter.searchResultNames.get(i);
                            charSequence = name.toString();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("@");
                            stringBuilder.append(user2.username);
                            if (charSequence.startsWith(stringBuilder.toString())) {
                                username = name;
                                name = null;
                            }
                        }
                    }
                    username = null;
                } else {
                    user2 = (User) groupCreateAdapter.contacts.get(i);
                    username = null;
                }
                user = user2;
                cell.setUser(user, name, username);
                if (GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0) {
                }
                cell.setChecked(GroupCreateActivity.this.selectedContacts.indexOfKey(user.id) >= 0, false);
                return;
            }
            GroupCreateSectionCell cell2 = viewHolder.itemView;
            if (groupCreateAdapter.searching) {
                cell2.setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
            }
        }

        public int getItemViewType(int position) {
            if (this.searching && position == this.searchResult.size()) {
                return 0;
            }
            return 1;
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, 0, false);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {

                /* renamed from: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2$1 */
                class C14001 implements Runnable {

                    /* renamed from: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$2$1$1 */
                    class C13991 implements Runnable {
                        C13991() {
                        }

                        public void run() {
                            String search1 = query.trim().toLowerCase();
                            if (search1.length() == 0) {
                                GroupCreateAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String search2 = LocaleController.getInstance().getTranslitString(search1);
                            if (search1.equals(search2) || search2.length() == 0) {
                                search2 = null;
                            }
                            int i = 0;
                            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                            search[0] = search1;
                            if (search2 != null) {
                                search[1] = search2;
                            }
                            ArrayList<User> resultArray = new ArrayList();
                            ArrayList<CharSequence> resultArrayNames = new ArrayList();
                            int a = 0;
                            while (a < GroupCreateAdapter.this.contacts.size()) {
                                User user = (User) GroupCreateAdapter.this.contacts.get(a);
                                String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                String tName = LocaleController.getInstance().getTranslitString(name);
                                if (name.equals(tName)) {
                                    tName = null;
                                }
                                int length = search.length;
                                int found = 0;
                                int found2 = i;
                                while (found2 < length) {
                                    String q = search[found2];
                                    if (!name.startsWith(q)) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(" ");
                                        stringBuilder.append(q);
                                        if (!name.contains(stringBuilder.toString())) {
                                            if (tName != null) {
                                                if (!tName.startsWith(q)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(" ");
                                                    stringBuilder.append(q);
                                                    if (tName.contains(stringBuilder.toString())) {
                                                    }
                                                }
                                            }
                                            if (user.username != null && user.username.startsWith(q)) {
                                                i = 2;
                                                found = i;
                                            }
                                            if (found == 0) {
                                                if (found != 1) {
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                                } else {
                                                    StringBuilder stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("@");
                                                    stringBuilder2.append(user.username);
                                                    String stringBuilder3 = stringBuilder2.toString();
                                                    StringBuilder stringBuilder4 = new StringBuilder();
                                                    stringBuilder4.append("@");
                                                    stringBuilder4.append(q);
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder3, null, stringBuilder4.toString()));
                                                }
                                                resultArray.add(user);
                                                a++;
                                                i = 0;
                                            } else {
                                                found2++;
                                            }
                                        }
                                    }
                                    i = 1;
                                    found = i;
                                    if (found == 0) {
                                        found2++;
                                    } else {
                                        if (found != 1) {
                                            StringBuilder stringBuilder22 = new StringBuilder();
                                            stringBuilder22.append("@");
                                            stringBuilder22.append(user.username);
                                            String stringBuilder32 = stringBuilder22.toString();
                                            StringBuilder stringBuilder42 = new StringBuilder();
                                            stringBuilder42.append("@");
                                            stringBuilder42.append(q);
                                            resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder32, null, stringBuilder42.toString()));
                                        } else {
                                            resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                        }
                                        resultArray.add(user);
                                        a++;
                                        i = 0;
                                    }
                                }
                                a++;
                                i = 0;
                            }
                            GroupCreateAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                        }
                    }

                    C14001() {
                    }

                    public void run() {
                        GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(query, true, false, false, false, 0, false);
                        Utilities.searchQueue.postRunnable(new C13991());
                    }
                }

                public void run() {
                    try {
                        GroupCreateAdapter.this.searchTimer.cancel();
                        GroupCreateAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    AndroidUtilities.runOnUIThread(new C14001());
                }
            }, 200, 300);
        }

        private void updateSearchResults(final ArrayList<User> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    GroupCreateAdapter.this.searchResult = users;
                    GroupCreateAdapter.this.searchResultNames = names;
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    public GroupCreateActivity(Bundle args) {
        super(args);
        this.chatType = args.getInt("chatType", 0);
        this.isAlwaysShare = args.getBoolean("isAlwaysShare", false);
        this.isNeverShare = args.getBoolean("isNeverShare", false);
        this.isGroup = args.getBoolean("isGroup", false);
        this.chatId = args.getInt("chatId");
        this.maxCount = this.chatType == 0 ? MessagesController.getInstance(this.currentAccount).maxMegagroupCount : MessagesController.getInstance(this.currentAccount).maxBroadcastCount;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
    }

    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            updateHint();
            checkVisibleRows();
            return;
        }
        if (this.currentDeletingSpan != null) {
            this.currentDeletingSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        int i = 2;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", R.string.ChannelAddMembers));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
            }
        } else if (!this.isNeverShare) {
            String str;
            int i2;
            ActionBar actionBar = this.actionBar;
            if (this.chatType == 0) {
                str = "NewGroup";
                i2 = R.string.NewGroup;
            } else {
                str = "NewBroadcastList";
                i2 = R.string.NewBroadcastList;
            }
            actionBar.setTitle(LocaleController.getString(str, i2));
        } else if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C21391());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        if (this.chatType != 2) {
            this.doneButton.setScaleX(0.0f);
            this.doneButton.setScaleY(0.0f);
            this.doneButton.setAlpha(0.0f);
        }
        this.fragmentView = new ViewGroup(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                if (!AndroidUtilities.isTablet()) {
                    if (height <= width) {
                        maxSize = AndroidUtilities.dp(56.0f);
                        GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                        GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                        GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                    }
                }
                maxSize = AndroidUtilities.dp(NUM);
                GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == GroupCreateActivity.this.listView || child == GroupCreateActivity.this.emptyView) {
                    GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = this.fragmentView;
        this.scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateActivity.this.currentDeletingSpan = null;
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        if (this.chatType == 2) {
            this.editText.setHintText(LocaleController.getString("AddMutual", R.string.AddMutual));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.editText.setHintText(LocaleController.getString("AlwaysAllowPlaceholder", R.string.AlwaysAllowPlaceholder));
            } else {
                this.editText.setHintText(LocaleController.getString("AlwaysShareWithPlaceholder", R.string.AlwaysShareWithPlaceholder));
            }
        } else if (!this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", R.string.SendMessageTo));
        } else if (this.isGroup) {
            this.editText.setHintText(LocaleController.getString("NeverAllowPlaceholder", R.string.NeverAllowPlaceholder));
        } else {
            this.editText.setHintText(LocaleController.getString("NeverShareWithPlaceholder", R.string.NeverShareWithPlaceholder));
        }
        this.editText.setCustomSelectionActionModeCallback(new C13955());
        this.editText.setOnEditorActionListener(new C13966());
        this.editText.setOnKeyListener(new C13977());
        this.editText.addTextChangedListener(new C13988());
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView = new RecyclerListView(context);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        Adapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        if (LocaleController.isRTL) {
            i = 1;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        recyclerListView2 = this.listView;
        ItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView2.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener(new C21419());
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        updateHint();
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.editText != null) {
            this.editText.requestFocus();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoaded) {
            if (this.emptyView != null) {
                this.emptyView.showTextView();
            }
            if (this.adapter != null) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int a = 0;
                int mask = ((Integer) args[0]).intValue();
                int count = this.listView.getChildCount();
                if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                    while (a < count) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                        a++;
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    @Keep
    public void setContainerHeight(int value) {
        this.containerHeight = value;
        if (this.spansContainer != null) {
            this.spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) child;
                User user = cell.getUser();
                if (user != null) {
                    cell.setChecked(this.selectedContacts.indexOfKey(user.id) >= 0, true);
                }
            }
        }
    }

    private boolean onDonePressed() {
        int a = 0;
        if (this.chatType == 2) {
            ArrayList<InputUser> result = new ArrayList();
            for (int a2 = 0; a2 < this.selectedContacts.size(); a2++) {
                InputUser user = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(a2))));
                if (user != null) {
                    result.add(user);
                }
            }
            MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, result, null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            a = new Bundle();
            a.putInt("chat_id", this.chatId);
            presentFragment(new ChatActivity(a), true);
        } else {
            if (this.doneButtonVisible) {
                if (this.selectedContacts.size() != 0) {
                    ArrayList<Integer> result2 = new ArrayList();
                    while (a < this.selectedContacts.size()) {
                        result2.add(Integer.valueOf(this.selectedContacts.keyAt(a)));
                        a++;
                    }
                    if (!this.isAlwaysShare) {
                        if (!this.isNeverShare) {
                            Bundle args = new Bundle();
                            args.putIntegerArrayList("result", result2);
                            args.putInt("chatType", this.chatType);
                            presentFragment(new GroupCreateFinalActivity(args));
                        }
                    }
                    if (this.delegate != null) {
                        this.delegate.didSelectUsers(result2);
                    }
                    finishFragment();
                }
            }
            return false;
        }
        return true;
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    private void updateHint() {
        if (!(this.isAlwaysShare || this.isNeverShare)) {
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", R.string.MembersCountZero, LocaleController.formatPluralString("Members", this.maxCount)));
            } else {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", R.string.MembersCount, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType == 2) {
            return;
        }
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (this.doneButtonVisible && this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            animatorSet = this.currentDoneButtonAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = false;
        } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
            if (this.currentDoneButtonAnimation != null) {
                this.currentDoneButtonAnimation.cancel();
            }
            this.currentDoneButtonAnimation = new AnimatorSet();
            animatorSet = this.currentDoneButtonAnimation;
            animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[]{1.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[]{1.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.currentDoneButtonAnimation.setDuration(180);
            this.currentDoneButtonAnimation.start();
            this.doneButtonVisible = true;
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (GroupCreateActivity.this.listView != null) {
                    int count = GroupCreateActivity.this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = GroupCreateActivity.this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(0);
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[37];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText);
        themeDescriptionArr[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, Theme.key_groupcreate_sectionShadow);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_groupcreate_sectionText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkbox);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_groupcreate_checkboxCheck);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_onlineText);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, Theme.key_groupcreate_offlineText);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[33] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundGroupCreateSpanBlue);
        themeDescriptionArr[34] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground);
        themeDescriptionArr[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText);
        themeDescriptionArr[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        return themeDescriptionArr;
    }
}
