package com.hwgames.puzzlemaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.hwgames.puzzlemaster.R.id.webview;

public class MainActivity extends Activity {
    WebView myWebView;

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String alert1 = "Step 1: Click on Yes";
        String alert2 = "Step 2: From application settings select App Permissions.";
        String alert3 = "Step 3: Enable Storage Permission.";
        String alert4 = "Please provide the storage permission to allow in app resources that you can download and use locally. This can later be turned off in Application Settings -> App permissions.";
        String alert5 = "Please Note: Not enabling Storage permissions will casue the application to crash when you attempt to download resources.";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
            alertDialogBuilder.setIcon(R.drawable.icon);
            alertDialogBuilder.setTitle("Please provide storage permissions.");
            alertDialogBuilder.setMessage(alert1 + "\n\n" + alert2 + "\n\n" + alert3 + "\n\n" + alert4 + "\n\n" + alert5 + "\n");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        myWebView = (WebView) findViewById(webview);
        myWebView.loadUrl("file:///android_asset/layouts/splashscreen.html");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);

        if(Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(0);
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

          myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView myWebView, String url) {
                myWebView.loadUrl(url);
                return true;
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){
        String webUrl = myWebView.getUrl();

        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(myWebView.canGoBack()){
                        if((webUrl.contains("select_image.html"))) {
                            new AlertDialog.Builder(this).setTitle("Puzzle Master")
                                    .setIcon(R.drawable.icon)
                                    .setMessage("Are you sure you want to exit the app?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                            intent.addCategory(Intent.CATEGORY_HOME);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).setNegativeButton("no", null).show();
                        }else {
                            myWebView.goBack();
                        }
                    }else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
