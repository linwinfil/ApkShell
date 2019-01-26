package doupai.venus.venus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import androidx.core.app.ActivityCompat;

public class CommonUtils
{
	/*
	 ****************************************************************
	 * GPU加速
	 ****************************************************************
	 */
	public static void LaunchWindowGPU(Activity ac)
	{
		try
		{
			Field field = WindowManager.LayoutParams.class.getField("FLAG_HARDWARE_ACCELERATED");
			if(field != null)
			{
				int FLAG_HARDWARE_ACCELERATED = field.getInt(null);
				Method method = Window.class.getMethod("setFlags", int.class, int.class);
				if(method != null)
				{
					method.invoke(ac.getWindow(), new Object[]{FLAG_HARDWARE_ACCELERATED, FLAG_HARDWARE_ACCELERATED});
				}
			}
		}
		catch(Throwable e)
		{
		}
	}

	public static void CancelViewGPU(View v)
	{
		try
		{
			Field field = View.class.getField("LAYER_TYPE_SOFTWARE");
			if(field != null)
			{
				int LAYER_TYPE_SOFTWARE = field.getInt(null);
				Method method = View.class.getMethod("setLayerType", int.class, Paint.class);
				if(method != null)
				{
					method.invoke(v, new Object[]{LAYER_TYPE_SOFTWARE, null});
				}
			}
		}
		catch(Throwable e)
		{
		}
	}

	/**
	 * 只有APP开启了GPU加速才有效
	 *
	 * @param v
	 */
	public static void LaunchViewGPU(View v)
	{
		try
		{
			Field field = View.class.getField("LAYER_TYPE_HARDWARE");
			if(field != null)
			{
				int LAYER_TYPE_HARDWARE = field.getInt(null);
				Method method = View.class.getMethod("setLayerType", int.class, Paint.class);
				if(method != null)
				{
					method.invoke(v, new Object[]{LAYER_TYPE_HARDWARE, null});
				}
			}
		}
		catch(Throwable e)
		{
		}
	}

	/**
	 * 打开系统浏览器/打开某应用
	 */
	public static void OpenBrowser(Context context, String url, boolean newTask)
	{
		if(context != null && url != null && url.length() > 0)
		{
			try
			{
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				if(newTask)
				{
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				context.startActivity(intent);
			}
			catch(Throwable e)
			{
				Toast.makeText(context.getApplicationContext(), "打开浏览器失败！", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}

	public static void OpenBrowser(Context context, String url)
	{
		OpenBrowser(context, url, false);
	}

	//<?xml version="1.0" encoding="utf-8"?>
	//<selector xmlns:android="http://schemas.android.com/apk/res/android" >
	//    <item android:drawable="@drawable/beautify_compare_btn_over" android:state_pressed="true"/>
	//    <item android:drawable="@drawable/beautify_compare_btn_out" android:state_pressed="false"/>
	//</selector>


	/**
	 * 解压ZIP
	 *
	 * @param zipFile ZIP数据流
	 * @param outPath 目标目录(结尾无需加"/")
	 */
	public static boolean UnZip(InputStream zipFile, String outPath)
	{
		boolean out = false;

		if(zipFile != null && outPath != null && outPath.length() > 0)
		{
			out = true;

			File path = new File(outPath);
			path.mkdirs();

			ZipInputStream zip = null;
			FileOutputStream fos = null;
			try
			{
				zip = new ZipInputStream(zipFile);
				ZipEntry ze;
				byte[] buf = new byte[8192];
				while((ze = zip.getNextEntry()) != null)
				{
					//System.out.println(ze.getName());
					if(ze.isDirectory())
					{
						File file = new File(outPath + File.separator + ze.getName());
						file.mkdirs();
						continue;
					}
					File file = new File(outPath + File.separator + ze.getName());
					File temp = file.getParentFile();
					if(temp != null)
					{
						temp.mkdirs();
					}
					fos = new FileOutputStream(file);
					int len = 0;
					while((len = zip.read(buf, 0, buf.length)) > -1)
					{
						fos.write(buf, 0, len);
					}
					fos.flush();
					fos.close();
					fos = null;
				}
			}
			catch(Throwable e)
			{
				out = false;

				e.printStackTrace();
			}
			finally
			{
				if(zip != null)
				{
					try
					{
						if(fos != null)
						{
							fos.close();
							fos = null;
						}
						zip.close();
						zip = null;
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return out;
	}

	/**
	 * 解压ZIP
	 *
	 * @param zipFile ZIP路径
	 * @param outPath 目标目录(结尾无需加"/")
	 * @return
	 */
	public static boolean UnZip(String zipFile, String outPath)
	{
		boolean out = false;

		if(zipFile != null && zipFile.length() > 0 && outPath != null && outPath.length() > 0)
		{
			out = true;

			File path = new File(outPath);
			path.mkdirs();

			ZipFile zip = null;
			InputStream ins = null;
			FileOutputStream fos = null;
			try
			{
				zip = new ZipFile(zipFile);
				Enumeration<? extends ZipEntry> entries = zip.entries();
				byte[] buf = new byte[8192];
				while(entries.hasMoreElements())
				{
					ZipEntry ze = (ZipEntry)entries.nextElement();
					//System.out.println(ze.getName());
					if(ze.isDirectory())
					{
						File file = new File(outPath + File.separator + ze.getName());
						file.mkdirs();
						continue;
					}
					File file = new File(outPath + File.separator + ze.getName());
					File temp = file.getParentFile();
					if(temp != null)
					{
						temp.mkdirs();
					}
					fos = new FileOutputStream(file);
					ins = zip.getInputStream(ze);
					int len = 0;
					while((len = ins.read(buf)) != -1)
					{
						fos.write(buf, 0, len);
					}
					fos.flush();
					fos.close();
					fos = null;
					ins.close();
					ins = null;
				}
			}
			catch(Throwable e)
			{
				out = false;

				e.printStackTrace();
			}
			finally
			{
				if(zip != null)
				{
					try
					{
						if(fos != null)
						{
							fos.close();
							fos = null;
						}
						if(ins != null)
						{
							ins.close();
							ins = null;
						}
						zip.close();
						zip = null;
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return out;
	}

	/*
	 ****************************************************************
	 * 文件读取/文件夹创建
	 ****************************************************************
	 */
	public static byte[] ReadData(InputStream is) throws Throwable
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream(2048);

		byte[] buf = new byte[1024];
		int readSize = 0;
		while((readSize = is.read(buf)) > -1)
		{
			os.write(buf, 0, readSize);
		}

		return os.toByteArray();
	}

	/**
	 * 读数据
	 *
	 * @param size
	 * @param is
	 * @return
	 * @throws Throwable
	 */
	public static byte[] ReadData(int size, InputStream is) throws Throwable
	{
		byte[] out = new byte[size];

		int currentSize = 0;
		int readSize = 0;
		while((readSize = is.read(out, currentSize, size - currentSize)) > -1)
		{
			currentSize += readSize;
			if(currentSize >= size)
			{
				break;
			}
		}

		return out;
	}

	/**
	 * 加载本地文件
	 *
	 * @param path
	 * @return
	 */
	public static byte[] ReadFile(String path)
	{
		byte[] out = null;

		File file = new File(path);
		if(file.exists())
		{
			int totalSize = (int)file.length();
			if(totalSize >= 0)
			{
				FileInputStream is = null;
				try
				{
					is = new FileInputStream(file);
					out = ReadData(totalSize, is);
				}
				catch(Throwable e)
				{
					out = null;
					e.printStackTrace();
				}
				finally
				{
					if(is != null)
					{
						try
						{
							is.close();
							is = null;
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

		return out;
	}

	public static void MakeFolder(String path)
	{
		try
		{
			if(path != null)
			{
				File file = new File(path);
				if(!(file.exists() && file.isDirectory()))
				{
					file.mkdirs();
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public static boolean MakeParentFolder(String path)
	{
		boolean out = false;

		if(path != null)
		{
			File file = new File(path).getParentFile();
			if(file != null)
			{
				if(file.exists())
				{
					out = true;
				}
				else
				{
					if(file.mkdirs())
					{
						out = true;
					}
				}
			}
		}

		return out;
	}

	public static boolean SaveFile(String path, byte[] data)
	{
		boolean out = false;

		FileOutputStream fos = null;
		try
		{
			if(path != null && data != null)
			{
				if(MakeParentFolder(path))
				{
					fos = new FileOutputStream(path);
					fos.write(data);
					fos.close();
					fos = null;
					out = true;
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(Throwable e)
				{
				}
				fos = null;
			}
		}

		return out;
	}

	public static boolean SaveFile(String path, InputStream is)
	{
		boolean out = false;

		FileOutputStream fos = null;
		try
		{
			if(path != null && is != null)
			{
				if(MakeParentFolder(path))
				{
					fos = new FileOutputStream(path);
					final int BUF_SIZE = 8192;
					int len;
					byte[] buffer = new byte[BUF_SIZE];
					while((len = is.read(buffer, 0, BUF_SIZE)) != -1)
					{
						fos.write(buffer, 0, len);
					}
					fos.close();
					fos = null;
					out = true;
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(Throwable e)
				{
				}
				fos = null;
			}
		}

		return out;
	}

	/*
	 **************************************************************
	 * EXIF常用功能
	 **************************************************************
	 */



	//@RequiresPermission(allOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
	@SuppressLint("MissingPermission")
	public static void AddJpgExifInfo(Context context, String path)
	{
		if(path != null)
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			if(opts.outMimeType != null && opts.outMimeType.equals("image/jpeg"))
			{
				try
				{
					ExifInterface exif = new ExifInterface(path);

					LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
					Location location = null;
					if(mgr != null)
					{
						if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
						{
							location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						}
					}
					if(location != null)
					{
						double lat = location.getLatitude();
						exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GpsInfoConvert(lat));
						exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat > 0 ? "N" : "S");
						double lon = location.getLongitude();
						exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GpsInfoConvert(lon));
						exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon > 0 ? "E" : "W");
					}
					exif.setAttribute(ExifInterface.TAG_MODEL, android.os.Build.MODEL);
					exif.setAttribute(ExifInterface.TAG_MAKE, android.os.Build.MANUFACTURER);
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.CHINA);
					exif.setAttribute(ExifInterface.TAG_DATETIME, formatter.format(new Date()));
					exif.saveAttributes();
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static String GpsInfoConvert(double gpsInfo)
	{
		String out = null;

		gpsInfo = Math.abs(gpsInfo);
		String dms = Location.convert(gpsInfo, Location.FORMAT_SECONDS);
		if(dms != null)
		{
			String[] splits = dms.split(":");
			if(splits.length > 2)
			{
				String[] arr = (splits[2]).split("\\.");
				String seconds;
				if(arr.length == 0)
				{
					seconds = splits[2];
				}
				else
				{
					seconds = arr[0];
				}
				out = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
			}
		}

		return out;
	}


	/*
	 ***************************************************************
	 * 加密,MD5等
	 ***************************************************************
	 */
	private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	private static String ToHexStr(byte[] data)
	{
		StringBuffer buf = new StringBuffer(data.length << 1);

		for(int i = 0; i < data.length; i++)
		{
			buf.append(HEX_DIGITS[(data[i] >>> 4) & 0x0f]);
			buf.append(HEX_DIGITS[data[i] & 0x0f]);
		}

		return buf.toString();
	}

	public static String Encrypt(String algorithm, byte[] data)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(data);
			return ToHexStr(md.digest());
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String Encrypt(String algorithm, String data)
	{
		try
		{
			return Encrypt(algorithm, data.getBytes());
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
	 *
	 * @param encryptText 被签名的字符串
	 * @param encryptKey  密钥
	 */
	public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey)
	{
		byte[] out = null;

		try
		{
			byte[] data = encryptKey.getBytes("UTF-8");
			//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
			SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
			//生成一个指定 Mac 算法 的 Mac 对象
			Mac mac = Mac.getInstance("HmacSHA1");
			//用给定密钥初始化 Mac 对象
			mac.init(secretKey);

			byte[] text = encryptText.getBytes("UTF-8");
			//完成 Mac 操作
			out = mac.doFinal(text);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	/*
	 ******************************************************************
	 * SharedPreferences的常用功能
	 ******************************************************************
	 */
	public static void SP_SaveMap(Context context, String spName, HashMap<String, String> map)
	{
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if(sp != null)
		{
			SharedPreferences.Editor editor = sp.edit();
			if(editor != null)
			{
				editor.clear();
				for(Map.Entry<String, String> entry : map.entrySet())
				{
					editor.putString(entry.getKey(), entry.getValue());
				}
				//if(android.os.Build.VERSION.SDK_INT >= 9)
				//{
				//editor.apply();
				//}
				//else
				//{
				editor.commit();//杀进程必须用这个
				//}
			}
		}
	}

	public static HashMap<String, String> SP_ReadSP(Context context, String spName, HashMap<String, String> outMap)
	{
		HashMap<String, String> out = outMap;
		if(out == null)
		{
			out = new HashMap<String, String>();
		}
		else
		{
			out.clear();
		}

		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if(sp != null)
		{
			Map<String, ?> temp = sp.getAll();
			Object obj;
			for(Map.Entry<String, ?> entry : temp.entrySet())
			{
				obj = entry.getValue();
				if(obj != null)
				{
					out.put(entry.getKey(), obj.toString());
				}
			}
		}

		return out;
	}

	public static void SP_AddKeyValue(Context context, String spName, String key, String value)
	{
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if(sp != null)
		{
			SharedPreferences.Editor editor = sp.edit();
			if(editor != null)
			{
				editor.putString(key, value);
				//if(android.os.Build.VERSION.SDK_INT >= 9)
				//{
				editor.apply();
				//}
				//else
				//{
				//	editor.commit();
				//}
			}
		}
	}

	public static String SP_ReadValue(Context context, String spName, String key)
	{
		String out = null;

		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if(sp != null)
		{
			out = sp.getString(key, null);
		}

		return out;
	}

	public static void SP_RemoveKey(Context context, String spName, String key)
	{
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		if(sp != null)
		{
			SharedPreferences.Editor editor = sp.edit();
			if(editor != null)
			{
				editor.remove(key);
				//if(android.os.Build.VERSION.SDK_INT >= 9)
				//{
				editor.apply();
				//}
				//else
				//{
				//	editor.commit();
				//}
			}
		}
	}

	/*
	 **************************************************************************************
	 * 获取机器信息
	 **************************************************************************************
	 */

	private static final String USER_ID = "common_utils_user_id";
	private static final String USER_ID_KEY = "id";

	/**
	 * 获取IMEI
	 *
	 * @param context
	 * @return
	 */
	//@RequiresPermission(allOf = {Manifest.permission.READ_PHONE_STATE})
	@SuppressLint("MissingPermission")
	public static String GetIMEI(Context context)
	{
		String out = null;
		if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
		{
			out = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		}
		if(out == null || out.length() < 2)
		{
			try
			{
				SharedPreferences sp = context.getSharedPreferences(USER_ID, Context.MODE_PRIVATE);
				out = sp.getString(USER_ID_KEY, null);
				if(out == null)
				{
					out = UUID.randomUUID().toString();
					SharedPreferences.Editor editor = sp.edit();
					editor.putString(USER_ID_KEY, out);
					editor.apply();
				}
			}
			catch(Throwable e)
			{
				out = "357537083442048";
			}
		}
		return out;
	}



	public static byte[] GetLocalMacAddress4Ip()
	{
		byte[] out = null;

		try
		{
			out = NetworkInterface.getByInetAddress(InetAddress.getByName(GetLocalIpAddress())).getHardwareAddress();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * 获取MAC地址
	 *
	 * @param context
	 * @return
	 */
	public static String GetLocalMacAddress(Context context)
	{
		String out = null;
		byte[] mac = GetLocalMacAddress4Ip();
		if(mac != null)
		{
			out = ToHexStr(mac);
		}
		if(out == null)
		{
			try
			{
				NetworkInterface networkInterface = NetworkInterface.getByName("eth1");
				if(networkInterface == null)
				{
					networkInterface = NetworkInterface.getByName("wlan0");
				}
				if(networkInterface == null)
				{
					return "020000000000";
				}
				mac = networkInterface.getHardwareAddress();
				if(mac != null)
				{
					out = ToHexStr(mac);
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		return out;
	}

	/**
	 * 每2字符添加:分割
	 */
	public static String AddColon(String str)
	{
		int len;
		if(str != null && (len = str.length()) > 0)
		{
			StringBuilder out = new StringBuilder(32);
			for(int i = 0; i < len; i++)
			{
				out.append(str.charAt(i));
				if(i + 1 < len && (i % 2) != 0)
				{
					out.append(':');
				}
			}
			return out.toString();
		}
		return str;
	}

	/**
	 * 获取MAC地址(带:)
	 */
	public static String GetLocalMacAddressHasColon(Context context)
	{
		return AddColon(GetLocalMacAddress(context));
	}

	/**
	 * 获取IP地址
	 *
	 * @return
	 */
	public static String GetLocalIpAddress()
	{
		try
		{
			String ipv4;
			List<NetworkInterface> niList = Collections.list(NetworkInterface.getNetworkInterfaces());
			for(NetworkInterface ni : niList)
			{
				List<InetAddress> iaList = Collections.list(ni.getInetAddresses());
				for(InetAddress address : iaList)
				{
					if(!address.isLoopbackAddress() && address instanceof Inet4Address)
					{
						ipv4 = address.getHostAddress();
						return ipv4;
					}
				}
			}
		}
		catch(Throwable ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取真实版本号
	 */
	public static String GetAppVer(Context context)
	{
		String out = null;

		try
		{
			PackageManager pm = context.getApplicationContext().getPackageManager();
			if(pm != null)
			{
				PackageInfo pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
				if(pi != null)
				{
					out = pi.versionName;
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * 获取真实版本号
	 */
	public static int GetAppVerCode(Context context)
	{
		int out = 0;

		try
		{
			PackageManager pm = context.getApplicationContext().getPackageManager();
			if(pm != null)
			{
				PackageInfo pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
				if(pi != null)
				{
					out = pi.versionCode;
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public static Bundle getApplicationMetaData(Context context)
	{
		ApplicationInfo info = null;
		try
		{
			info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		}
		catch(PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		if(info != null)
		{
			return info.metaData;
		}
		return null;
	}

	public static Bundle getActivityMetaData(Context context)
	{
		ActivityInfo info = null;
		try
		{
			info = context.getPackageManager().getActivityInfo(((Activity)context).getComponentName(), PackageManager.GET_META_DATA);
		}
		catch(PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		if(info != null)
		{
			return info.metaData;
		}
		return null;
	}

	public static Bundle getServiceMetaData(Context context, Class<? extends Service> clazz)
	{
		ComponentName componentName = new ComponentName(context, clazz);
		ServiceInfo info = null;
		try
		{
			info = context.getPackageManager().getServiceInfo(componentName, PackageManager.GET_META_DATA);
		}
		catch(PackageManager.NameNotFoundException e)
		{
			e.printStackTrace();
		}
		if(info != null)
		{
			return info.metaData;
		}
		return null;
	}

	/**
	 * 用于EditText字数限制
	 */
	public static class MyTextWatcher implements TextWatcher
	{
		protected int mMaxWordNum; //最大字数
		protected int mMaxByteNum; //最大字节数
		protected TextWatcherCallback mCB;

		public MyTextWatcher(int maxWordNum, int maxByteNum, TextWatcherCallback cb)
		{
			mMaxWordNum = maxWordNum;
			mMaxByteNum = maxByteNum;
			mCB = cb;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			String str = s.toString();
			//System.out.println(str + " |s " + str.length() + " |b " + str.getBytes().length + " |c " + str.toCharArray().length);
			boolean change = false;
			if(str.length() > mMaxWordNum)
			{
				str = str.substring(0, mMaxWordNum);
				change = true;
			}
			if(str.getBytes().length > mMaxByteNum)
			{
				while(str.getBytes().length > mMaxByteNum)
				{
					str = str.substring(0, str.length() - 1);
				}
				change = true;
			}
			if(change)
			{
				//修正
				char[] chars = str.toCharArray();
				if(chars.length > 0)
				{
					int count = 0;
					for(int i = chars.length - 1; i >= 0; i--)
					{
						if(Character.getType(chars[i]) != Character.SURROGATE)
						{
							break;
						}
						count++;
					}
					if(count % 2 != 0)
					{
						str = str.substring(0, str.length() - 1);
					}
				}

				s.clear();
				s.append(str);

				if(mCB != null)
				{
					mCB.OutOfBounds();
				}
			}
		}

		public void ClearAll()
		{
			mCB = null;
		}
	}

	public interface TextWatcherCallback
	{
		void OutOfBounds();
	}

	/**
	 * 获取屏幕截屏
	 */
	public static Bitmap GetScreenBmp(View view, int w, int h)
	{
		if(view != null && view.getWidth() > 0 && view.getHeight() > 0)
		{
			Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(out);
			view.draw(canvas);
			return out;
		}
		return null;
	}

	/**
	 * 获取屏幕截屏
	 */
	public static Bitmap GetScreenBmp(View view)
	{
		if(view != null)
		{
			return GetScreenBmp(view, view.getWidth(), view.getHeight());
		}
		return null;
	}




	public static boolean IsInstalled(Context context, String pkgName)
	{
		boolean result = false;
		if(context != null)
		{
			try
			{
				PackageInfo info = context.getPackageManager().getPackageInfo(pkgName, 0);
				if(info != null)
				{
					result = true;
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}

	public static boolean IsUiThread()
	{
		return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
	}

	private static String USER_AGENT = null;

	public static String GetUserAgent(Context context, String def)
	{
		if(USER_AGENT == null)
		{
			USER_AGENT = def;
			if(TextUtils.isEmpty(USER_AGENT))
			{
				try
				{
					USER_AGENT = WebSettings.getDefaultUserAgent(context);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			if(TextUtils.isEmpty(USER_AGENT))
			{
				try
				{
					USER_AGENT = System.getProperty("http.agent");
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			if(TextUtils.isEmpty(USER_AGENT))
			{
				USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; ZUK Z2131 Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36";
			}
		}
		return USER_AGENT;
	}

	public static String GetUserAgent(Context context)
	{
		return GetUserAgent(context, null);
	}
}
