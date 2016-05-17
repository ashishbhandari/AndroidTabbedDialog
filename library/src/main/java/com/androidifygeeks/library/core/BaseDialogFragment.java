package com.androidifygeeks.library.core;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.androidifygeeks.library.R;
import com.androidifygeeks.library.fragment.PageFragment;
import com.androidifygeeks.library.iface.ISimpleDialogCancelListener;
import com.androidifygeeks.library.util.MultiSwipeRefreshLayout;
import com.androidifygeeks.library.util.TypefaceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base dialog fragment for all your dialogs, styleable and same design on Android 2.2+.
 * <p/>
 *
 * @author David Vávra (david@inmite.eu)
 *         <p/>
 *         modified by b_ashish on 21-Mar-16.
 */
public abstract class BaseDialogFragment extends DialogFragment implements DialogInterface.OnShowListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback {


    private static final String TAG = BaseDialogFragment.class.getSimpleName();
    protected int mRequestCode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            dialog.setCanceledOnTouchOutside(
                    args.getBoolean(BaseDialogBuilder.ARG_CANCELABLE_ON_TOUCH_OUTSIDE));
        }
        /*
        * disable the actual title of a dialog cause custom dialog title is rendering through custom layout
         */
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity(), inflater, container);
        return build(builder).create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            mRequestCode = getTargetRequestCode();
        } else {
            Bundle args = getArguments();
            if (args != null) {
                mRequestCode = args.getInt(BaseDialogBuilder.ARG_REQUEST_CODE, 0);
            }
        }
    }

    /**
     * Key method for using { com.avast.android.dialogs.core.BaseDialogFragment}.
     * Customized dialogs need to be set up via provided builder.
     *
     * @param initialBuilder Provided builder for setting up customized dialog
     * @return Updated builder
     */
    protected abstract Builder build(Builder initialBuilder);

    @Override
    public void onDestroyView() {
        // bug in the compatibility library
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (getView() != null) {
//            ScrollView vMessageScrollView = (ScrollView) getView().findViewById(R.id.sdl_message_scrollview);
//            ListView vListView = (ListView) getView().findViewById(R.id.sdl_list);
//            FrameLayout vCustomViewNoScrollView = (FrameLayout) getView().findViewById(R.id.sdl_custom);
//            boolean customViewNoScrollViewScrollable = false;
//            if (vCustomViewNoScrollView.getChildCount() > 0) {
//                View firstChild = vCustomViewNoScrollView.getChildAt(0);
//                if (firstChild instanceof ViewGroup) {
//                    customViewNoScrollViewScrollable = isScrollable((ViewGroup) firstChild);
//                }
//            }
//            boolean listViewScrollable = isScrollable(vListView);
//            boolean messageScrollable = isScrollable(vMessageScrollView);
//            boolean scrollable = listViewScrollable || messageScrollable || customViewNoScrollViewScrollable;
//            modifyButtonsBasedOnScrollableContent(scrollable);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        for (ISimpleDialogCancelListener listener : getCancelListeners()) {
            listener.onCancelled(mRequestCode);
        }
    }

    /**
     * Get dialog cancel listeners.
     * There might be more than one cancel listener.
     *
     * @return Dialog cancel listeners
     * @since 2.1.0
     */
    protected List<ISimpleDialogCancelListener> getCancelListeners() {
        return getDialogListeners(ISimpleDialogCancelListener.class);
    }

    /**
     * Utility method for acquiring all listeners of some type for current instance of DialogFragment
     *
     * @param listenerInterface Interface of the desired listeners
     * @return Unmodifiable list of listeners
     * @since 2.1.0
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getDialogListeners(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        List<T> listeners = new ArrayList<T>(2);
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            listeners.add((T) targetFragment);
        }
        if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            listeners.add((T) getActivity());
        }
        return Collections.unmodifiableList(listeners);
    }

    private boolean isScrollable(ViewGroup listView) {
        int totalHeight = 0;
        for (int i = 0; i < listView.getChildCount(); i++) {
            totalHeight += listView.getChildAt(i).getMeasuredHeight();
        }
        return listView.getMeasuredHeight() < totalHeight;
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return false;
    }


    /**
     * Custom dialog builder
     */
    protected static class Builder {

        private final Context mContext;

        private final ViewGroup mContainer;

        private final LayoutInflater mInflater;

        private CharSequence mTitle = null;

        private CharSequence mSubTitle = null;

        private CharSequence mPositiveButtonText;

        private View.OnClickListener mPositiveButtonListener;

        private CharSequence mNegativeButtonText;

        private View.OnClickListener mNegativeButtonListener;

        private CharSequence mNeutralButtonText;

        private View.OnClickListener mNeutralButtonListener;

        private CharSequence mMessage;

        private View mCustomView;

        private ListAdapter mListAdapter;

        private TabViewPagerAdapter mTabAdapter;

        private CharSequence[] mTabItems;

        private int mListCheckedItemIdx;

        private int mChoiceMode;

        private int[] mListCheckedItemMultipleIds;

        private AdapterView.OnItemClickListener mOnItemClickListener;

        private int contentHeight;

        private int mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;

        private int baseTabViewId = 12345;


        public Builder(Context context, LayoutInflater inflater, ViewGroup container) {
            this.mContext = context;
            this.mContainer = container;
            this.mInflater = inflater;
        }

        public LayoutInflater getLayoutInflater() {
            return mInflater;
        }

        public Builder setContentPaneHeight(int height) {
            this.contentHeight = height;
            return this;
        }

        public Builder setTitle(int titleId) {
            this.mTitle = mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        }

        public Builder setSubTitle(int subTitleId) {
            this.mSubTitle = mContext.getText(subTitleId);
            return this;
        }

        public Builder setSubTitle(CharSequence subTitle) {
            this.mSubTitle = subTitle;
            return this;
        }

        public Builder setPositiveButton(int textId, final View.OnClickListener listener) {
            mPositiveButtonText = mContext.getText(textId);
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final View.OnClickListener listener) {
            mNegativeButtonText = mContext.getText(textId);
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, final View.OnClickListener listener) {
            mNeutralButtonText = mContext.getText(textId);
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, final View.OnClickListener listener) {
            mNeutralButtonText = text;
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder setMessage(int messageId) {
            mMessage = mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setItems(ListAdapter listAdapter, int[] checkedItemIds, int choiceMode, final AdapterView.OnItemClickListener listener) {
            mListAdapter = listAdapter;
            mListCheckedItemMultipleIds = checkedItemIds;
            mOnItemClickListener = listener;
            mChoiceMode = choiceMode;
            mListCheckedItemIdx = -1;
            return this;
        }

        public Builder setTabItems(TabViewPagerAdapter tabAdapter, CharSequence[] tabItems) {
            mTabAdapter = tabAdapter;
            mTabItems = tabItems;
            return this;
        }

        /**
         * Set list
         *
         * @param checkedItemIdx Item check by default, -1 if no item should be checked
         */
        public Builder setItems(ListAdapter listAdapter, int checkedItemIdx,
                                final AdapterView.OnItemClickListener listener) {
            mListAdapter = listAdapter;
            mOnItemClickListener = listener;
            mListCheckedItemIdx = checkedItemIdx;
            mChoiceMode = AbsListView.CHOICE_MODE_NONE;
            return this;
        }

        public Builder setView(View view) {
            mCustomView = view;
            return this;
        }

        public View create() {

            final LinearLayout content = (LinearLayout) mInflater.inflate(R.layout.tdl_dialog, mContainer, false);
            TextView vTitle = (TextView) content.findViewById(R.id.tdl_title_text);
            TextView vSubTitle = (TextView) content.findViewById(R.id.tdl_subtitle_text);

            MultiSwipeRefreshLayout vMultiSwipeRefreshLayout = (MultiSwipeRefreshLayout) content.findViewById(R.id.tdl_swipe_refresh_layout);
            setContentHeight(vMultiSwipeRefreshLayout);
            final ViewPager vViewPager = (ViewPager) content.findViewById(R.id.tdl_view_pager);

            final TabLayout vTabLayout = (TabLayout) content.findViewById(R.id.tdl_sliding_tabs);

//            TextView vMessage = (TextView) content.findViewById(R.id.tdl_message);
//            FrameLayout vCustomView = (FrameLayout) content.findViewById(R.id.sdl_custom);
            Button vPositiveButton = (Button) content.findViewById(R.id.tdl_button_positive);
            Button vNegativeButton = (Button) content.findViewById(R.id.tdl_button_negative);
            Button vNeutralButton = (Button) content.findViewById(R.id.tdl_button_neutral);

            Typeface regularFont = TypefaceHelper.get(mContext, "Roboto-Regular");
            Typeface mediumFont = TypefaceHelper.get(mContext, "Roboto-Medium");

            set(vTitle, mTitle, mediumFont);
            set(vSubTitle, mSubTitle, mediumFont);
//            set(vMessage, mMessage, regularFont);
//            setPaddingOfTitleAndMessage(vTitle, vMessage);


            //---- for tab rendering----//

            if (mTabAdapter != null) {
                vViewPager.setAdapter(mTabAdapter);
                vTabLayout.setTabsFromPagerAdapter(mTabAdapter);
                vTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        vViewPager.setCurrentItem(tab.getPosition(), true);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        // Do nothing
                    }
                });
                vViewPager.setPageMargin(mContext.getResources().getDimensionPixelSize(R.dimen.my_tab_view_page_margin));
                vViewPager.setPageMarginDrawable(R.drawable.page_margin);
                vViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        mViewPagerScrollState = ViewPager.SCROLL_STATE_DRAGGING;
                    }

                    @Override
                    public void onPageSelected(int position) {
                        TabLayout.Tab tab = vTabLayout.getTabAt(position);
                        tab.select();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;
                    }
                });

                for (int i = 0, count = mTabItems.length; i < count; i++) {
                    vTabLayout.setContentDescription(mTabItems[i]);
                }
                // help you to select default tab
                TabLayout.Tab tab = vTabLayout.getTabAt(0);
                tab.select();
            }

            //------end here--------//


            set(vPositiveButton, mPositiveButtonText, mediumFont, mPositiveButtonListener);
            set(vNegativeButton, mNegativeButtonText, mediumFont, mNegativeButtonListener);
            set(vNeutralButton, mNeutralButtonText, mediumFont, mNeutralButtonListener);


            return content;
        }

        /*
        * defining the height of a content layout
         */
        private void setContentHeight(MultiSwipeRefreshLayout swipeRefreshLayout) {
            if (swipeRefreshLayout != null) {
                if (contentHeight <= 0) {
                    swipeRefreshLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.dialog_main_pane_height)));
                } else {
                    swipeRefreshLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentHeight));
                }
            }
        }

        /**
         * Padding is different if there is only title, only message or both.
         */
        private void setPaddingOfTitleAndMessage(TextView vTitle, TextView vMessage) {
//            int grid6 = mContext.getResources().getDimensionPixelSize(R.dimen.grid_6);
//            int grid4 = mContext.getResources().getDimensionPixelSize(R.dimen.grid_4);
//            if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mMessage)) {
//                vTitle.setPadding(grid6, grid6, grid6, grid4);
//                vMessage.setPadding(grid6, 0, grid6, grid4);
//            } else if (TextUtils.isEmpty(mTitle)) {
//                vMessage.setPadding(grid6, grid4, grid6, grid4);
//            } else if (TextUtils.isEmpty(mMessage)) {
//                vTitle.setPadding(grid6, grid6, grid6, grid4);
//            }
        }

        private boolean shouldStackButtons() {
            return shouldStackButton(mPositiveButtonText) || shouldStackButton(mNegativeButtonText)
                    || shouldStackButton(mNeutralButtonText);
        }

        private boolean shouldStackButton(CharSequence text) {
            final int MAX_BUTTON_CHARS = 12; // based on observation, could be done better with measuring widths
            return text != null && text.length() > MAX_BUTTON_CHARS;
        }

        private void set(Button button, CharSequence text, Typeface font, View.OnClickListener listener) {
            set(button, text, font);
            if (listener != null) {
                button.setOnClickListener(listener);
            }
        }

        private void set(TextView textView, CharSequence text) {
            if (text != null) {
                textView.setText(text);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private void set(TextView textView, CharSequence text, Typeface font) {
            if (text != null) {
                textView.setText(text);
                textView.setTypeface(font);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    public class TabViewPagerAdapter extends FragmentPagerAdapter {

        private final CharSequence[] tabItems;
        private Fragment mCurrentFragment;

        public TabViewPagerAdapter(FragmentManager fm, CharSequence[] tabItems) {
            super(fm);
            this.tabItems = tabItems;
        }


        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "Creating fragment #" + position);
            PageFragment frag = new PageFragment();
            Bundle args = new Bundle();
            args.putInt(PageFragment.ARG_DAY_INDEX, position);
            String parentTag = getTag();
            args.putString(PageFragment.PARENT_TAG, parentTag);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public int getCount() {
            return tabItems.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabItems[position];
        }
    }
}
