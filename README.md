[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AndroidTabbedDialog-green.svg?style=true)](https://android-arsenal.com/details/1/3318)  <a href="https://opensource.org/licenses/Apache-2.0" target="_blank"><img src="https://img.shields.io/badge/License-Apache_v2.0-blue.svg?style=flat"/></a> 

# AndroidTabbedDialog


![](https://raw.githubusercontent.com/ashishbhandari/AndroidTabbedDialog/master/screenshots/test.gif)


## How to create tab dialogs:

```java
TabDialogFragment.createBuilder(MainActivity.this, getSupportFragmentManager())
                        .setTitle("hello")
                        .setSubTitle("subtitle")
                        .setTabButtonText(new CharSequence[]{"Tab1", "Tab2"})
                        .setPositiveButtonText("Love")
                        .setNegativeButtonText("Hate")
                        .setNeutralButtonText("WTF?")
                        .setRequestCode(REQUEST_SIMPLE_DIALOG)
                        .show();
```

### How to react on button press in your Activity/Fragment:
Simply implement interface `ISimpleDialogListener` in your Activity/Fragment. Listener's callbacks have `requestCode` parameter - you can use it if you have more dialogs in one Activity/Fragment.

### How to render tab view inside your dialog:
Simply implement interface `IFragmentListener` in your Activity/Fragment. Listener's callbacks have:-

`onFragmentViewCreated(Fragment fragment)`
`onFragmentAttached(Fragment fragment)`
`onFragmentDetached(Fragment fragment)`

## License
Copyright (c) 2016 Ashish Bhandari

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
