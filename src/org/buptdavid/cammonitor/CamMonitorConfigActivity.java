package org.buptdavid.cammonitor;

import org.buptdavid.cammonitor.util.ActivtyUtil;
import org.buptdavid.cammonitor.util.DatabaseHelper;
import org.buptdavid.cammonitor.util.EditDialog;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * 配置监控Activity
 * @author weijielu
 *
 */
public class CamMonitorConfigActivity extends Activity{
	
	private static final String TAG="CamMonitorConfigActivity";

	private Spinner spinner;
	
//	private Button btnTest;
	private Button btnCancle;
	private Button btnSave;
	
	private EditText editIp;
	private EditText editPort;
	private EditText editUsername;
	private EditText editPassword;
	private EditText editClientDir;
	private String name;
	// 是否修改标志
	private boolean isModify = false;
	private int id ;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.add);
			
			findView();
			fillView();
			setListener();
		}catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	protected void setListener(){
//		btnTest.setOnClickListener(btnTestListener);
		btnSave.setOnClickListener(btnSaveListener);
		btnCancle.setOnClickListener(btnCancleListener);
	}
	
	private void findView(){
		spinner = (Spinner) findViewById(R.id.SpinnerFtpType);
//		btnTest = (Button) findViewById(R.id.BtnTest);
		btnSave = (Button) findViewById(R.id.BtnSave);
		btnCancle = (Button) findViewById(R.id.BtnCancle);
		editClientDir = (EditText) findViewById(R.id.FtpLocalDir);
		editIp = (EditText) findViewById(R.id.FtpIp);
		editPassword =(EditText) findViewById(R.id.FtpPassword);
		editPort=(EditText) findViewById(R.id.FtpPort);
		editUsername=(EditText) findViewById(R.id.FtpUsername);
		
	}
	
	private void fillView(){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Socket","HTTP"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		// 填充内容
		if(this.getIntent().getExtras()!= null && this.getIntent().getExtras().containsKey("id")){
			try{
				isModify = true;
				int id = this.getIntent().getExtras().getInt("id");
				this.id = id;
				//根据id获取其它属性进行填充
				DatabaseHelper helper = new DatabaseHelper(this);
				Cursor cursor = helper.query(id);
				cursor.moveToFirst();
				
				this.name = cursor.getString(1);
				editIp.setText(cursor.getString(2));
				editPassword.setText(String.valueOf(cursor.getInt(3)));
				editUsername.setText(cursor.getString(4));
				editPassword.setText(cursor.getString(5));
				editClientDir.setText(cursor.getString(6));
				helper.close();
				
			}catch (Exception e) {
				Log.e(CamMonitorConfigActivity.TAG, e.getMessage(), e);
				ActivtyUtil.openToast(CamMonitorConfigActivity.this, "错误，错误原因:"+e.getMessage());
			}
		}
	}

	/**
	 * 测试Button Listener
	 */
	private View.OnClickListener btnTestListener = new View.OnClickListener(){
		public void onClick(View v) {
			
		}
	};
	
	private EditDialog.OnDataSetListener saveListener = new EditDialog.OnDataSetListener(){
		public void onDataSet(String text) {
			try {
					ContentValues contentValue = new ContentValues();
					contentValue.put("name", text);
					contentValue.put("ip", editIp.getText().toString());
					contentValue.put("port",Integer.parseInt(editPort.getText().toString()));
					contentValue.put("username", editUsername.getText().toString());
					contentValue.put("password", editPassword.getText().toString());
					contentValue.put("client_dir", editClientDir.getText().toString());
					
					if(isModify){
						DatabaseHelper.update(CamMonitorConfigActivity.this, "tb_cammonitor_configs", contentValue, id);
					}else{
						DatabaseHelper.insert(CamMonitorConfigActivity.this, "tb_cammonitor_configs", contentValue);
					}
					
					setResult(Activity.RESULT_OK);
					finish();
					
					ActivtyUtil.openToast(CamMonitorConfigActivity.this, "保存成功");
				} catch (Exception e) {
					Log.e(CamMonitorConfigActivity.TAG, e.getMessage(),e);
					ActivtyUtil.openToast(CamMonitorConfigActivity.this, "错误:"+e.getMessage());
				}
			
		}
		
	};
	
	/**
	 * 保存配置Listener
	 */
	private View.OnClickListener btnSaveListener = new View.OnClickListener(){

		public void onClick(View v) {
			if(editIp.getText().toString().trim().length()<=0){
				ActivtyUtil.showAlert(CamMonitorConfigActivity.this, getText(R.string.error), getText(R.string.error_ip), getText(R.string.btn_ok));
				return;
			}
			
			if(editPort.getText().toString().trim().length()<=0){
				ActivtyUtil.showAlert(CamMonitorConfigActivity.this, getText(R.string.error), getText(R.string.error_port), getText(R.string.btn_ok));
				return;
			}
			
			String cfgname = editIp.getText().toString();
			if(isModify){
				cfgname = CamMonitorConfigActivity.this.name;
			}
			
			EditDialog dialog = new EditDialog(CamMonitorConfigActivity.this, getText(R.string.CamMonitorConfigActivty_Cfg_Name).toString(), cfgname,saveListener);
			dialog.show();
		}
		
	};
	
	
	private View.OnClickListener btnCancleListener = new View.OnClickListener(){

		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		
	};
	
}
