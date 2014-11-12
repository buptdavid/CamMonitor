package org.buptdavid.cammonitor;

import org.buptdavid.cammonitor.util.ActivtyUtil;
import org.buptdavid.cammonitor.util.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 初始Activity
 * @author weijielu
 *
 */
public class CamMonitorClient extends Activity {

	public final static String TAG = "CamMonitorClient";

	protected TextView view;
	protected DatabaseHelper helper;
	protected Spinner spinner;

	protected Button btnAdd;
	protected Button btnModify;
	protected Button btnDelete;
	protected Button btnConnect;
	private SimpleCursorAdapter adapter;
	private Cursor cursor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			// 获取所有View
			findView();
			// 填充数据
			fillDataWithCursor();
			// 绑定监听器
			setListenner();
		} catch (Exception e) {
			ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "ȷ��");
		}
	}
	
	/**
	 * 填充数据
	 * @throws Exception
	 */
	private void fillDataWithCursor() throws Exception {
		DatabaseHelper helper = new DatabaseHelper(this);
		
		cursor = helper.loadAllName();
		int count = cursor.getCount();
		cursor.moveToFirst();
		
		adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[] { "name" }, new int[] { android.R.id.text1 });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(adapter);
		spinner.refreshDrawableState();
		// close db
		helper.close();
		
		// 根据count来enable或disable button
		if (count <= 0) {
			btnConnect.setEnabled(false);
			btnDelete.setEnabled(false);
			btnModify.setEnabled(false);
			spinner.setEnabled(false);
		} else {
			btnConnect.setEnabled(true);
			btnDelete.setEnabled(true);
			btnModify.setEnabled(true);
			spinner.setEnabled(true);
		}
	}
	
	/**
	 * 获取Views
	 */
	private void findView() {
		spinner = (Spinner) findViewById(R.id.SpinnerLdapConfig);
		btnAdd = (Button) findViewById(R.id.BtnNew);
		btnConnect = (Button) findViewById(R.id.BtnConnect);
		btnModify = (Button) findViewById(R.id.BtnModify);
		btnDelete = (Button) findViewById(R.id.BtnDelete);
	}
	
	private void setListenner() {
		btnAdd.setOnClickListener(btnAddListener);
		btnModify.setOnClickListener(btnModifyListener);
		btnDelete.setOnClickListener(btnDeleteListener);
		btnConnect.setOnClickListener(btnConnectListener);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					fillDataWithCursor();
				} catch (Exception e) {
					ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "确定");
				}
			}
		}
	}
	
	/**
	 * 连接按钮Listener
	 */
	private View.OnClickListener btnConnectListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Cursor cc = (Cursor) spinner.getSelectedItem();
				
				int id = cc.getInt(0);
				Intent intent = new Intent(CamMonitorClient.this, ServerAct.class);
				intent.putExtra("id", id);
				
				startActivity(intent);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "确定");
			}
		}

	};

	/**
	 * 增加按钮Listener
	 */
	private View.OnClickListener btnAddListener = new View.OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent(CamMonitorClient.this, CamMonitorConfigActivity.class);
			startActivityForResult(intent, 0);
		}

	};

	/**
	 * 修改按钮监听器
	 */
	private View.OnClickListener btnModifyListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Cursor cc = (Cursor) spinner.getSelectedItem();
				int id = cc.getInt(0);
				Intent intent = new Intent(CamMonitorClient.this, CamMonitorConfigActivity.class);
				intent.putExtra("id", id);
				startActivityForResult(intent, 1);
				// DatabaseHelper.testInsert(CamMonitorClient.this);
				// fillData();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
				ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "ȷ��");
			}
		}
	};

	/**
	 * 删除按钮监听器
	 */
	private View.OnClickListener btnDeleteListener = new View.OnClickListener() {

		public void onClick(View v) {
			try {
				Cursor cc = (Cursor) spinner.getSelectedItem();
				final int id = cc.getInt(0);
				String name = cc.getString(1);
				
				new AlertDialog.Builder(CamMonitorClient.this).setTitle("确定删除吗？")
						.setMessage("确定删除" + name + "吗？").setPositiveButton(
								"确定", new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog, int which) {
										
										try {
											DatabaseHelper.delete(CamMonitorClient.this, id);
											// 删除后再次填充
											fillDataWithCursor();
											ActivtyUtil.openToast(CamMonitorClient.this, "删除成功!");
										} catch (Exception e) {
											Log.e(TAG, e.getMessage(), e);
											ActivtyUtil.openToast(CamMonitorClient.this, "删除失败：" + e.getMessage());
										}
									}
								}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int which) {

									}
								}).show();
				// DatabaseHelper.Delete(CamMonitorClient.this, name);
			} catch (Exception e) {
				ActivtyUtil.showAlert(CamMonitorClient.this, "Error", e.getMessage(), "确定");
			}
		}
	};
}
