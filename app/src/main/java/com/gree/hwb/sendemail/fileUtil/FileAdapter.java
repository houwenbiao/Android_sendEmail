package com.gree.hwb.sendemail.fileUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gree.hwb.sendemail.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/08/18.
 */
public class FileAdapter extends BaseAdapter
{
	private List<File> fileList;//填充数据的List
	private static HashMap<Integer,Boolean> isSelected;//用来控制CheckBox的选中状态
	private LayoutInflater inflater = null;//布局填充器
	private Context context;

	public FileAdapter(Context context, ArrayList<File> list)
	{
		this.context = context;
		this.fileList = list;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<>();
		for (int i = 0; i < list.size(); i++)
		{
			getIsSelected().put(i,false);
		}
	}
	public static HashMap<Integer,Boolean> getIsSelected()
	{
		return isSelected;
	}
	public static  void  setIsSelected(HashMap<Integer,Boolean> isSelected)
	{
		FileAdapter.isSelected = isSelected;
	}
	@Override
	public int getCount()
	{
		return fileList.size();
	}

	@Override
	public Object getItem(int i)
	{
		return fileList.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup)
	{
		ViewHolder holder = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
		if(convertView == null)
		{
			holder = new ViewHolder();//获取viewHolder对象
			convertView = inflater.inflate(R.layout.line,null);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_select);
			holder.fileName = (TextView) convertView.findViewById(R.id.file_name);
			holder.lastModified = (TextView) convertView.findViewById(R.id.tv_lastModified);
			holder.fileLength  = (TextView) convertView.findViewById(R.id.file_length);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			//为view设置标签
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		//设置list中的显示
		holder.checkBox.setChecked(getIsSelected().get(position));
		holder.fileName.setText(fileList.get(position).getName());
		holder.fileLength.setText(getFolderSize(fileList.get(position)));
		holder.lastModified.setText(format.format(fileList.get(position).lastModified()));
		if(fileList.get(position).isDirectory())
		{
			holder.icon.setImageResource(R.drawable.ic_folder_filetype);
		}
		else
		{
			holder.icon.setImageResource(R.drawable.ic_document_filetype);
		}
		return convertView;
	}

	public class ViewHolder
	{
		public CheckBox checkBox;
		public TextView fileName,lastModified,fileLength;
		public ImageView icon;
	}

	/**
	 * 获取文件大小
	 * @param file
	 * @return
	 */
	public static String getFolderSize(File file)
	{
		if(file.isDirectory())
		{
			return null;
		}
		else
		{
			if(file.length() < 1024)
			{
				return file.length() + " B";
			}
			else if(file.length() >= 1024 && file.length() < 1024*1024)
			{
				return file.length()/1024 + " KB";
			}
			else if(file.length() >= 1024*1024)
			{
				return file.length()/1024/1024 + " M";
			}
			else
			{
				return null;
			}
		}
	}

}
