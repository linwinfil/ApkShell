package com.maoxin.apkshell.cmd;

import android.content.Context;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class BannerCore3
{
	public static class CmdStruct
	{
		/**
		 * 请求行为
		 */
		public String m_cmd;

		/**
		 * 请求行为参数
		 */
		public String[] m_params;

		public static HashMap<String, String> DecodeParams(String[] params)
		{
			HashMap<String, String> out = new HashMap<>();

			if(params != null)
			{
				String[] pair;
				for(String p : params)
				{
					pair = p.split("=");
					if(pair.length == 2)
					{
						out.put(pair[0], pair[1]);
					}
				}
			}

			return out;
		}

		public HashMap<String, String> GetMap()
		{
			return DecodeParams(m_params);
		}
	}

	public static CmdStruct GetCmdStruct(Uri uri)
	{
		CmdStruct out = null;

		if(uri != null)
		{
			String cmd = uri.getHost();
			String[] params = null;
			String temp = uri.getQuery();
			if(temp != null)
			{
				params = temp.split("&");
			}

			out = new CmdStruct();
			out.m_cmd = cmd;
			out.m_params = params;
		}

		return out;
	}

	/**
	 * 协议解析
	 *
	 * @param context
	 * @param cmdStr  协议文本
	 * @param cb      回调
	 * @param args
	 */
	public static void ExecuteCommand(Context context, String cmdStr, CmdCallback cb, Object... args)
	{
		try
		{
			if(cmdStr != null && cb != null)
			{
				if (cmdStr.startsWith("http"))
				{
					cb.OpenSystemWeb(context, cmdStr, null);
				}
				else
				{
					Uri uri = Uri.parse(cmdStr);
					CmdStruct struct = GetCmdStruct(uri);
					if (struct != null)
					{
						String scheme = uri.getScheme();
						if (scheme != null)
						{
							switch (scheme)
							{
								case "camera21":
								{
									if (struct.m_cmd != null)
									{
										switch (struct.m_cmd)
										{
											case "page":
											{
												//打开page页面
												HashMap<String, String> map = struct.GetMap();
												String value;
												if ((value = map.get("open")) != null)
												{
													String[] strArr = new String[map.size() - 1];
													int i = 0;
													for (Map.Entry<String, String> entry : map.entrySet())
													{
														String k = entry.getKey();
														if (k != null && !k.equals("open"))
														{
															String v = entry.getValue();
															if (v == null)
															{
																v = "";
															}
															strArr[i] = k + '=' + v;
															i++;
														}
													}
													cb.OpenPage(context, Integer.parseInt(value), strArr);
												}
												break;
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

    // public static void OpenUrl(Context context, String protocolUrl, OpenUrlCallback cb)
    // {
    //     try
    //     {
    //         BannerCore3.CmdStruct struct = BannerCore3.GetCmdStruct(protocolUrl);
    //         if (struct != null && struct.m_cmd != null)
    //         {
    //             String cmd = struct.m_cmd.toLowerCase(Locale.ENGLISH);
    //             if ((cmd.equals("action_insideweb") || cmd.equals("action_externalweb")) && struct.m_params != null && struct.m_params.length > 0)
    //             {
    //                 String param = struct.m_params[0];
    //                 if (param != null)
    //                 {
    //                     String[] pair = param.split("=");
    //                     if (pair.length >= 2)
    //                     {
    //                         String url = URLDecoder.decode(pair[1], "UTF-8");
    //                         if (cmd.equals("action_insideweb"))
    //                         {
    //                             cb.OpenMyWeb(context, url);
    //                         }
    //                         else
    //                         {
    //                             cb.OpenSystemWeb(context, url);
    //                         }
    //                         return;
    //                     }
    //                 }
    //             }
    //         }
    //
    //         String url = protocolUrl;
    //         if (url.contains(".poco.cn"))
    //         {
    //             cb.OpenMyWeb(context, url);
    //         }
    //         else
    //         {
    //             cb.OpenSystemWeb(context, url);
    //         }
    //     }
    //     catch (Throwable e)
    //     {
    //         e.printStackTrace();
    //     }
    // }

	public static String GetValue(String[] args, String key)
	{
		String out = null;

		try
		{
			if(args != null)
			{
				for(String temp : args)
				{
					if(temp.startsWith(key))
					{
						String[] arr = temp.split("=");
						out = arr[1];
						break;
					}
				}
			}
		}
		catch(Throwable e)
		{
		}

		return out;
	}

	public static int GetIntValue(String[] args, String key)
	{
		int out = 0;

		try
		{
			String v = GetValue(args, key);
			if(v != null)
			{
				out = Integer.parseInt(v);
			}
		}
		catch(Throwable e)
		{
		}

		return out;
	}

	public interface CmdCallback
	{
		/**
		 * @param code 0:镜头
		 *             1:人像
		 *             2:构图挑战
		 *             3:潮流攻略
		 *             4:浏览器打开链接，可带分享功能
		 * @param args 参数可以通过{@link #GetValue(String[], String)}或{@link #GetIntValue(String[], String)}获取
		 */
		void OpenPage(Context context, int code, @Nullable String... args);

		/**
		 * 打开外部浏览器
		 *
		 * @param context
		 * @param url     url请求地址
		 * @param params  协议参数
		 */
		void OpenSystemWeb(Context context, @Nullable String url, @Nullable HashMap<String, Object> params);

        /**
         * 打开内部浏览器
         *
         * @param context
         * @param url     url请求地址
         * @param params  协议参数
         */
        void OpenInnerWeb(Context context, @Nullable String url, @Nullable HashMap<String, Object> params);

		void GoToShare(Context context, @Nullable String... args);
	}


	public interface OpenUrlCallback
    {
        public void OpenMyWeb(Context context, String url);

        public void OpenSystemWeb(Context context, String url);
    }
}
