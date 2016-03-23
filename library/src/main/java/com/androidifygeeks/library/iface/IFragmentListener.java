package com.androidifygeeks.library.iface;

import android.support.v4.app.Fragment;

/**
 * Created by b_ashish on 23-Mar-16.
 */
public interface IFragmentListener {

    void onFragmentViewCreated(Fragment fragment);

    void onFragmentAttached(Fragment fragment);

    void onFragmentDetached(Fragment fragment);
}
