package com.gree.hwb.sendemail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gree.hwb.sendemail.fileUtil.FileAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/08/17.
 */
public class FileActivity extends AppCompatActivity
{
	private FileAdapter fileAdapter;
	private ArrayList<Uri> uris;
	ListView listView;// 列出全部文件的ListView
	// 记录当前的父文件夹
	File currentParentFile;
	// 记录当前路径下的所有文件的文件数组
	File[] currentFiles;
	ImageView back;//返回上一级目录按钮
	FloatingActionButton sendEmail;

	private String address = "fendoubiaobiao@163.com";
	private String content = "附件是多联机接收到的历史数据";
	private ArrayList<File> arrayList;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		uris = new ArrayList<>();
		sendEmail = (FloatingActionButton) findViewById(R.id.sendEmail);
		back = (ImageView) findViewById(R.id.iv_back);
		listView = (ListView) findViewById(R.id.list);

		// 获取系统的SD卡的目录
		File root = new File(Environment.getExternalStorageDirectory().getPath() + "/Monitor/ReceiveData");
		// 如果 SD卡存在
		if(root.exists())
		{
			currentParentFile = root;
			currentFiles = root.listFiles();
			// 使用当前目录下的全部文件、文件夹来填充ListView
			inflateListView(currentFiles);
		}
		sendEmail.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				sendEmail(address,content);
				//邮件发送完成之后所有已选文件设置为不选择
				for (int i = 0; i < currentFiles.length; i++)
				{
					if(fileAdapter.getIsSelected().get(i))
					{
						fileAdapter.getIsSelected().put(i,false);
					}
				}
				fileAdapter.notifyDataSetChanged();
			}
		});

		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//点击返回键完成之后所有已选文件设置为不选择
				for (int i = 0; i < currentFiles.length; i++)
				{
					if(fileAdapter.getIsSelected().get(i))
					{
						fileAdapter.getIsSelected().put(i,false);
					}
				}
				try
				{
					if(!currentParentFile.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getPath()))
					{
						//获取上一级目录
						currentParentFile = currentParentFile.getParentFile();
						currentFiles = currentParentFile.listFiles();//获取目录下的所有文件
						inflateListView(currentFiles);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		// 为ListView的列表项的单击事件绑定监听器
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(arrayList.get(position).isFile())
				{
					Log.i("zx","currentFiles[position].isFile()");
					FileAdapter.ViewHolder holder = (FileAdapter.ViewHolder) view.getTag();
					holder.checkBox.toggle();//改变CheckBox的状态
					//将CheckBox的选中状况记录下来
					FileAdapter.getIsSelected().put(position,holder.checkBox.isChecked());
					if(holder.checkBox.isChecked() == true)
					{
						uris.add(Uri.fromFile(arrayList.get(position)));
					}
					else
					{
						uris.remove(Uri.fromFile(arrayList.get(position)));
					}
				}
				else
				{
					// 获取用户点击的文件夹下的所有文件
					File[] tmp = arrayList.get(position).listFiles();
					if(tmp == null || tmp.length == 0)
					{
						Toast.makeText(FileActivity.this,"空文件夹",Toast.LENGTH_SHORT).show();
					}
					else
					{
						Log.i("zx","currentFiles.length:"+currentFiles.length);
						// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
						currentParentFile = arrayList.get(position); //
						Log.i("zx","currentParentFileFileName:"+currentParentFile.getName());
						// 保存当前的父文件夹内的全部文件和文件夹
						currentFiles = tmp;
						// 再次更新ListView
						inflateListView(currentFiles);
					}
				}
			}
		});
	}

	/**
	 * 填充ListView
	 * @param files
	 */

	private void inflateListView(File[] files)
	{
		arrayList = new ArrayList<>();
		for (int i = 0; i < files.length; i++)
		{
			arrayList.add(files[i]);
		}
		Collections.sort(arrayList,new SortFileByTime());
		fileAdapter = new FileAdapter(FileActivity.this, arrayList);
		listView.setAdapter(fileAdapter);
		fileAdapter.notifyDataSetChanged();//通知ListView更新列表
	}

	/**
	 * 发送邮件
	 *
	 * @param address 默认发件人地址
	 * @param content 默认邮件内容
	 */
	public void sendEmail(String address, String content)
	{
		Intent data = new Intent(Intent.ACTION_SEND_MULTIPLE);
		data.setType("application/octet-stream");//邮件发送类型：带附件
		//		data.putExtra(Intent.EXTRA_EMAIL, address);//目标邮箱地址
		data.putExtra(Intent.EXTRA_CC, address);//为邮件设置抄送对象
		data.putExtra(Intent.EXTRA_SUBJECT, "接收到的多联机历史数据");//邮件主题
		data.putExtra(Intent.EXTRA_TEXT, content);//邮件内容文字
		Log.i("zx","uris.size():"+uris.size());
		data.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
		startActivity(Intent.createChooser(data, "请选择应用程序："));
		uris.clear();
	}

	/**
	 * 将之按照文件创建先后顺序排列
	 */
	class SortFileByTime implements Comparator
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			File file1 = (File) o1;
			File file2 = (File) o2;
			if(file1.lastModified() > file2.lastModified())
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
	}
}
