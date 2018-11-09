package com.maoxin.apkshell.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip
{

	public Zip()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 解压缩zip包
	 * @param zipFileString zip文件的全路径
	 * @param outPathString 解压后的文件保存的路径
	 * @param includeZipFileName 解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含
	 */
	public static void UnZipFolder(String zipFileString, String outPathString, boolean includeZipFileName) throws Exception {
		if (TextUtils.isEmpty(zipFileString) || TextUtils.isEmpty(outPathString))
		{
			return;
		}
		File zipFile = new File(zipFileString);
		//如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径
		if (includeZipFileName)
		{
			String fileName = zipFile.getName();
			if (!TextUtils.isEmpty(fileName))
			{
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
			outPathString = outPathString + File.separator + fileName;
		}
		//创建解压缩文件保存的路径
		File unzipFileDir = new File(outPathString);
		if (!unzipFileDir.exists() || !unzipFileDir.isDirectory())
		{
			unzipFileDir.mkdirs();
		}

		//开始解压
		ZipEntry entry = null;
		String entryFilePath = null, entryDirPath = null;
		File entryFile = null, entryDir = null;
		int index = 0, count = 0, bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>)zip.entries();
		//循环对压缩包里的每一个文件进行解压
		while(entries.hasMoreElements())
		{
			entry = entries.nextElement();
			//构建压缩包中一个文件解压后保存的文件全路径
			entryFilePath = outPathString + File.separator + entry.getName();
			//构建解压后保存的文件夹路径
			index = entryFilePath.lastIndexOf(File.separator);
			if (index != -1)
			{
				entryDirPath = entryFilePath.substring(0, index);
			}
			else
			{
				entryDirPath = "";
			}
			entryDir = new File(entryDirPath);
			//如果文件夹路径不存在，则创建文件夹
			if (!entryDir.exists() || !entryDir.isDirectory())
			{
				entryDir.mkdirs();
			}

			//创建解压文件
			entryFile = new File(entryFilePath);

			//判断文件全路径是否为文件夹,如果是上面已经创建,不需要解压
			if(entryFile.isDirectory()){
				continue;
			}
			if (entryFile.exists())
			{
				//检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
				SecurityManager securityManager = new SecurityManager();
				securityManager.checkDelete(entryFilePath);
				//删除已存在的目标文件
				entryFile.delete();
			}

			//写入文件
			bos = new BufferedOutputStream(new FileOutputStream(entryFile));
			bis = new BufferedInputStream(zip.getInputStream(entry));
			while ((count = bis.read(buffer, 0, bufferSize)) != -1)
			{
				bos.write(buffer, 0, count);
			}
			bos.flush();
			bos.close();
		}
	}

	/**
	 * 解压缩zip包
	 * @param zipFileString zip文件的全路径
	 * @param outPathString 解压后的文件保存的路径
	 * @param includeZipFileName 解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含
	 */
	public static void UnZipAssetsFolder(Context context, String zipFileString, String outPathString, boolean includeZipFileName) throws Exception {
		if (TextUtils.isEmpty(zipFileString) || TextUtils.isEmpty(outPathString))
		{
			return;
		}
		AssetManager am = context.getAssets();
		ZipInputStream zis = new ZipInputStream(am.open(zipFileString));
		//如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径
		if (includeZipFileName)
		{
			String fileName = zipFileString.substring(zipFileString.lastIndexOf("/"), zipFileString.lastIndexOf("."));
			outPathString = outPathString + File.separator + fileName;
		}
		//创建解压缩文件保存的路径
		File unzipFileDir = new File(outPathString);
		if (!unzipFileDir.exists() || !unzipFileDir.isDirectory())
		{
			unzipFileDir.mkdirs();
		}

		//开始解压
		ZipEntry entry = null;
		String entryFilePath = null, entryDirPath = null;
		File entryFile = null, entryDir = null;
		int index = 0, count = 0, bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		//循环对压缩包里的每一个文件进行解压
		while((entry = zis.getNextEntry()) != null)
		{
			//构建压缩包中一个文件解压后保存的文件全路径
			entryFilePath = outPathString + File.separator + entry.getName();
			//构建解压后保存的文件夹路径
			index = entryFilePath.lastIndexOf(File.separator);
			if (index != -1)
			{
				entryDirPath = entryFilePath.substring(0, index);
			}
			else
			{
				entryDirPath = "";
			}
			entryDir = new File(entryDirPath);
			//如果文件夹路径不存在，则创建文件夹
			if (!entryDir.exists() || !entryDir.isDirectory())
			{
				entryDir.mkdirs();
			}

			//创建解压文件
			entryFile = new File(entryFilePath);

			//判断文件全路径是否为文件夹,如果是上面已经创建,不需要解压
			if(entryFile.isDirectory()){
				continue;
			}
			if (entryFile.exists())
			{
				//检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
				SecurityManager securityManager = new SecurityManager();
				securityManager.checkDelete(entryFilePath);
				//删除已存在的目标文件
				entryFile.delete();
			}

			//写入文件
			bos = new BufferedOutputStream(new FileOutputStream(entryFile));
			while((count = zis.read(buffer, 0, bufferSize)) != -1)
			{
				bos.write(buffer, 0, count);
			}
			bos.flush();
			bos.close();
		}
	}

	/**
	 * Compress file and folder  
	 * @param srcFileString   file or folder to be Compress  
	 * @param zipFileString   the path name of result ZIP  
	 * @throws Exception
	 */
	public static void ZipFolder(String srcFileString, String zipFileString)throws Exception {
		//create ZIP   
		ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
		//create the file   
		File file = new File(srcFileString);
		//compress  
		ZipFiles(file.getParent()+File.separator, file.getName(), outZip);
		//finish and close  
		outZip.finish();
		outZip.close();
	}

	/**
	 * compress files  
	 * @param folderString
	 * @param fileString
	 * @param zipOutputSteam
	 * @throws Exception
	 */
	private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam)throws Exception{
		if(zipOutputSteam == null)
			return;
		File file = new File(folderString+fileString);
		if (file.isFile()) {
			ZipEntry zipEntry =  new ZipEntry(fileString);
			FileInputStream inputStream = new FileInputStream(file);
			zipOutputSteam.putNextEntry(zipEntry);
			int len;
			byte[] buffer = new byte[4096];
			while((len=inputStream.read(buffer)) != -1)
			{
				zipOutputSteam.write(buffer, 0, len);
			}
			zipOutputSteam.closeEntry();
		}
		else {
			//folder  
			String fileList[] = file.list();
			//no child file and compress    
			if (fileList.length <= 0) {
				ZipEntry zipEntry =  new ZipEntry(fileString+File.separator);
				zipOutputSteam.putNextEntry(zipEntry);
				zipOutputSteam.closeEntry();
			}
			//child files and recursion    
			for (int i = 0; i < fileList.length; i++) {
				ZipFiles(folderString, fileString+ File.separator+fileList[i], zipOutputSteam);
			}//end of for    
		}
	}

	/**
	 * return the InputStream of file in the ZIP  
	 * @param zipFileString  name of ZIP   
	 * @param fileString     name of file in the ZIP   
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream UpZip(String zipFileString, String fileString)throws Exception {
		ZipFile zipFile = new ZipFile(zipFileString);
		ZipEntry zipEntry = zipFile.getEntry(fileString);
		return zipFile.getInputStream(zipEntry);
	}

	/**
	 * return files list(file and folder) in the ZIP  
	 * @param zipFileString     ZIP name
	 * @return
	 * @throws Exception
	 */
	public static byte[] GetFileStream(Context context, String zipFileString, String fileName)
	{
		if(zipFileString == null || zipFileString.length() == 0)
		{
			return null;
		}
		File file = new File(zipFileString);
		byte[] bytes = null;
		if(file.exists())
		{
			bytes = readDataFromSDCard(zipFileString, fileName);
		}
		else
		{
			bytes = getDataFromAssets(context, zipFileString, fileName);
		}

		return bytes;
	}

	private static byte[] getDataFromAssets(Context context, String zipFileString, String fileName)
	{
		AssetManager am = context.getAssets();
		ZipInputStream zis = null;
		try
		{
			zis = new ZipInputStream(am.open(zipFileString));
			ZipEntry zipEntry = null;
			while((zipEntry = zis.getNextEntry()) != null)
			{
				if(zipEntry.getName().equals(fileName))
				{
					ByteArrayOutputStream outs = new ByteArrayOutputStream(100*1024);
					int len = 0;
					byte[] buf = new byte[8192];
					while((len = zis.read(buf)) != -1)
					{
						outs.write(buf, 0, len);
					}
					zis.close();
					return outs.toByteArray();
				}
			}
		}catch(Exception e)
		{
			if(zis != null)
			{
				try
				{
					zis.close();
				}
				catch(IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(zis != null)
					zis.close();
			}
			catch(IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}

	private static byte[] readDataFromSDCard(String zipFileString, String fileName)
	{
		InputStream ins = null;
		ZipFile zip = null;
		try
		{
			zip = new ZipFile(zipFileString);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while(entries.hasMoreElements())
			{
				ZipEntry ze = (ZipEntry)entries.nextElement();
				if(ze.getName().equals(fileName))
				{
					ByteArrayOutputStream outs = new ByteArrayOutputStream(100*1024);
					ins = zip.getInputStream(ze);
					int len = 0;
					byte[] buf = new byte[8192];
					while((len = ins.read(buf)) != -1)
					{
						outs.write(buf, 0, len);
					}
					ins.close();
					return outs.toByteArray();
				}
			}
		}catch(Exception e)
		{
			try
			{
				if(zip != null)
				{
					zip.close();
				}
				if(ins != null)
				{
					ins.close();
				}
			}
			catch(IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally
		{
			try
			{
				if(zip != null)
				{
					zip.close();
				}
				if(ins != null)
				{
					ins.close();
				}
			}
			catch(IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 修改zip包里面某一个文件的内容， 文件名不变
	 * @param zipFileString
	 * @param deletefileName 删除的文件名
	 * @param saveFileName	新增的文件名
	 * @param srcZipPath 源zip文件路径，目前支持asset和本地路径的zip文件
	 * @return
	 */
	public static boolean MotifyZipPartData(Context context, String zipFileString, String deletefileName, String saveFileName, String srcZipPath, String datas)
	{
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		InputStream ins = null;
		ZipInputStream zis = null;
		FileInputStream fis = null;
		ZipFile zip = null;
		if(TextUtils.isEmpty(srcZipPath))
			return false;
		File file = new File(srcZipPath);
		boolean flag = false;
		if(file.exists())
		{
			flag = true;
		}
		try
		{
			fos = new FileOutputStream(zipFileString);
			zos = new ZipOutputStream(fos);

			ZipEntry zipEntry =  new ZipEntry(saveFileName);
			zos.putNextEntry(zipEntry);
			zos.write(datas.getBytes());

			if(flag)
			{
				fis = new FileInputStream(srcZipPath);
				zis = new ZipInputStream(fis);
				ZipEntry ze;
				while((ze = zis.getNextEntry()) != null)
				{
					if(!ze.getName().equals(deletefileName) && !ze.getName().equals(zipEntry.getName()))
					{
						zos.putNextEntry(ze);
						int len = 0;
						byte[] buf = new byte[8192];
						while((len = zis.read(buf)) != -1)
						{
							zos.write(buf, 0, len);
						}
					}
				}
				/*zip = new ZipFile(srcZipPath);
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while(entries.hasMoreElements())
				{
					ZipEntry ze = entries.nextElement();
					if(!ze.getName().equals(deletefileName) && !ze.getName().equals(zipEntry.getName()))
					{
						zos.putNextEntry(ze);
						System.out.println("ze.getName(): " + ze.getName());

						ins = zip.getInputStream(ze);
						int len = 0;
						byte[] buf = new byte[8192];
						while((len = ins.read(buf)) != -1)
						{
							zos.write(buf, 0, len);
						}
						ins.close();
					}
				}*/
			}
			else
			{
				AssetManager am = context.getAssets();
				zis = new ZipInputStream(am.open(srcZipPath));
				ZipEntry ze;
				while((ze = zis.getNextEntry()) != null)
				{
					if(!ze.getName().equals(deletefileName) && !ze.getName().equals(zipEntry.getName()))
					{
						zos.putNextEntry(ze);

						int len = 0;
						byte[] buf = new byte[8192];
						while((len = zis.read(buf)) != -1)
						{
							zos.write(buf, 0, len);
						}
					}
				}
			}
			return true;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(zos != null)
				{
					zos.close();
				}
				if(fos != null)
				{
					fos.close();
				}
				if(zip != null)
				{
					zip.close();
				}
				if(ins != null)
				{
					ins.close();
				}
				if(zis != null)
				{
					zis.close();
				}
				if(fis != null)
				{
					fis.close();
				}
			}
			catch(IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}

}
