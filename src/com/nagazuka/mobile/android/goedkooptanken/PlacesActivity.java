package com.nagazuka.mobile.android.goedkooptanken;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class PlacesActivity extends Activity {

	private static final int DIALOG_PROGRESS = 1;
	private static int MAX_PROGRESS = 100;

	private int mProgress;
	private ProgressDialog mProgressDialog;
	private Handler mProgressHandler;

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_PROGRESS:
			mProgressDialog = new ProgressDialog(PlacesActivity.this);
			mProgressDialog.setIcon(R.drawable.icon);
			mProgressDialog.setTitle(R.string.progressdialog_title);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(MAX_PROGRESS);
			mProgressDialog.setButton2(getText(R.string.progressdialog_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							/* User clicked No so do some stuff */
						}
					});
			dialog = mProgressDialog;
		}
		return dialog;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.places);

		showDialog(DIALOG_PROGRESS);
		mProgress = 0;
		mProgressDialog.setProgress(mProgress);
		
		
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgress >= MAX_PROGRESS) {
                    mProgressDialog.dismiss();
                } else {
                    mProgress++;
                    mProgressDialog.incrementProgressBy(1);
                    mProgressHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };
		
		mProgressHandler.sendEmptyMessage(0);
	}

}
