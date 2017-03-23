package com.gree.hwb.sendemail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{

	private Button select, send;
	private String filePath,filePath1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		filePath1 = getFilePath();
		Log.i("zx","filePath:"+filePath1);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT,Uri.parse(filePath1));
		startActivity(intent);





		select = (Button) findViewById(R.id.select);
		send = (Button) findViewById(R.id.send);

		select.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//				importExcel();
				/*filePath = getFilePath();
				Log.i("zx","filePath:"+filePath);
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(filePath));
				startActivity(intent);*/
			}
		});

		send.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent email = new Intent(android.content.Intent.ACTION_SEND);
				//		File file = new File(Environment.getExternalStorageDirectory().getPath()+ File.separator + "simplenote"+ File.separator+"note.xml");
				//邮件发送类型：带附件的邮件
				email.setType("*/*");
				//邮件接收者（数组，可以是多位接收者）
				String[] emailReciver = new String[]{"1508763090@qq.com"};

				String emailTitle = "标题";
				String emailContent = "内容";
				//设置邮件地址
				email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
				//设置邮件标题
				email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
				//设置发送的内容
				email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
				//设置抄送人
				email.putExtra(Intent.EXTRA_CC, emailReciver);
				//附件
				Log.i("zx", "file://" + filePath);
//				email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));
				//调用系统的邮件系统
				startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
			}
		});
	}

	public String getFilePath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if(sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
			Log.i("zx","sdDir.toString():"+sdDir.toString());
		}
		return /*"file://"+*/sdDir.toString()/*+"/Monitor/ReceiveData"*/;
	}

	private void importExcel()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try
		{
			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);
		}
		catch (ActivityNotFoundException ex)
		{
			Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();// 可以连接到下载文件管理器的连接让用户下载文件管理器
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == Activity.RESULT_OK)
		{
			Uri uri = data.getData();
			filePath = Uri.decode(data.getDataString());
			filePath = uri.getPath();
			Log.i("zx",filePath);
/*

			String[] proj = {MediaStore.Images.Media.DATA};
			Log.i("zx",proj.toString());
			Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String path = actualimagecursor.getString(actual_image_column_index);// 获取选择文件的路径
*/

			Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
