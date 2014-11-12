package org.buptdavid.cammonitor.util;

import org.buptdavid.cammonitor.CamMonitorClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.buptdavid.cammonitor.R;

/**
	*
	*
	*
	*/
public class EditDialog extends AlertDialog implements DialogInterface.OnClickListener{

	 private String text = "";
	 private EditText edit;
	 private OnDataSetListener mCallback;
	 private LinearLayout layout;
	 
	 /**
	  *
	  */
	public interface OnDataSetListener{
		void onDataSet(String text);
	}
	
	public EditDialog(Context context,String title,String value,OnDataSetListener callback) {
			super(context);
			
	        mCallback = callback;
	        TextView label = new TextView(context);
	        label.setText("hint");
	        edit = new EditText(context);
	        edit.setText(value);
	        edit.setPadding(30, 0, 0, 0);
	        layout = new LinearLayout(context);
	        layout.setOrientation(LinearLayout.VERTICAL);
	        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(200,50);
	        param2.leftMargin=30;
	        layout.addView(edit, param2);
	        setView(layout);
	        setTitle(title);
	        setButton(context.getText(R.string.btn_ok), this);
	        setButton2(context.getText(R.string.btn_cancle), (OnClickListener) null);
	}

	public void onClick(DialogInterface dialog, int which) {
		 text = edit.getText().toString();
	     Log.d(CamMonitorClient.TAG, "U click text=" + text);
	     if (mCallback != null)
	          mCallback.onDataSet(text);
		
	}

}
