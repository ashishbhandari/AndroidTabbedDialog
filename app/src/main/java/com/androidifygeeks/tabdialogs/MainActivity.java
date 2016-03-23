package com.androidifygeeks.tabdialogs;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidifygeeks.library.fragment.PageFragment;
import com.androidifygeeks.library.fragment.TabDialogFragment;
import com.androidifygeeks.library.iface.IFragmentListener;
import com.androidifygeeks.library.iface.ISimpleDialogCancelListener;
import com.androidifygeeks.library.iface.ISimpleDialogListener;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ISimpleDialogListener, ISimpleDialogCancelListener, IFragmentListener {

    private static final int REQUEST_TABBED_DIALOG = 42;

    private static final String TAG = MainActivity.class.getSimpleName();

    private Set<Fragment> mMyScheduleFragments = new HashSet<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.testbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabDialogFragment.createBuilder(MainActivity.this, getSupportFragmentManager())
                        .setTitle("Title")
                        .setSubTitle("Subtitle")
                        .setTabButtonText(new CharSequence[]{"Tab1", "Tab2"})
                        .setPositiveButtonText("Ok")
                        .setNegativeButtonText("Cancel")
                        .setNeutralButtonText("Neutral")
                        .setRequestCode(REQUEST_TABBED_DIALOG)
                        .show();
            }
        });
    }


    @Override
    public void onCancelled(int requestCode) {
        switch (requestCode) {
            case REQUEST_TABBED_DIALOG:
                Toast.makeText(MainActivity.this, "Dialog cancelled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (requestCode == REQUEST_TABBED_DIALOG) {
            Toast.makeText(MainActivity.this, "Negative button clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {
        if (requestCode == REQUEST_TABBED_DIALOG) {
            Toast.makeText(MainActivity.this, "Neutral button clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == REQUEST_TABBED_DIALOG) {
            Toast.makeText(MainActivity.this, "Positive button clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFragmentViewCreated(Fragment fragment) {
        int selectedTabPosition = fragment.getArguments().getInt(PageFragment.ARG_DAY_INDEX, 0);
        View rootContainer = fragment.getView().findViewById(R.id.root_container);
        Log.i(TAG, "Position: " + selectedTabPosition);

        switch (selectedTabPosition) {
            case 0:
                // add view in container for first tab
                View tabProductDetailLayout = getLayoutInflater().inflate(R.layout.tab_one_layout, (ViewGroup) rootContainer);

                TextView textView = (TextView) tabProductDetailLayout.findViewById(R.id.text_view);
                textView.setText("hello: tab1");
                break;
            case 1:
                // add view in container for second tab
                View tabProductDetailLayout2 = getLayoutInflater().inflate(R.layout.tab_one_layout, (ViewGroup) rootContainer);

                TextView textView1 = (TextView) tabProductDetailLayout2.findViewById(R.id.text_view);
                textView1.setText("hello: tab2");
                break;
        }


    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        mMyScheduleFragments.add(fragment);
    }

    @Override
    public void onFragmentDetached(Fragment fragment) {
        mMyScheduleFragments.remove(fragment);
    }
}
