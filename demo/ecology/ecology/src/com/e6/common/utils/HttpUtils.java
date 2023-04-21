/**
* @Title: HttpUtils
* @Description: 
* @author: HM
* @date 2022年6月28日 上午11:08:01
*/
package com.e6.common.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.e6.constant.ConstantUtil;

/**
 * 描述:接口工具
 * 
 * @author HM
 * @date 2022年6月28日
 *
 */
public class HttpUtils {
	private static Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
	 *	更新接口响应参数封装
	 * Success	执行是否成功。true为成功，false为失败	Bool	是
		Message	提示信息。成功返回空字符串或null，失败返回失败原因	VARCHAR	当Success为false时必填
		PostData	请求数据。失败时返回服务接收到的请求数据	JSON对象	当Success为false时必填
		Data	成功时为返回的数据	JSON对象	否
		FlowCode	流程编号。原样返回请求时传输的值，仅作校验用途	VARCHAR	否
		ResultCode	结果代码。2：工号不存在	INT	否
	 * @return
	 */
	public static String sendUpdatePackaging(String parem) {
		if(CustomUtil.isBlank(parem)) {
			LOGGER.error("东宝接口对接失败================================");
			return "接口异常";
		}
		JSONObject parseObject = JSONObject.parseObject(parem);
		Boolean success = parseObject.getBoolean("Success");
		if(success) {
			return "";
		}else {
			LOGGER.error("东宝接口对接失败================================"+parseObject);
			return parseObject.getString("Message");
		}
	}
	
	
	/**
	 *	更新接口入参封装
	 * @param funid 模块编号，参照表1
	 * @param language 语言。1：简体中文，2：繁体中文，3：英文
	 * @param data 数数据信息。多个字段条件是以AND串接
	 * @param sqlwhere Sql条件表达式
	 * @return
	 */
	public static JSONObject sendUpdatePackaging(String funid,JSONObject json,String requestId) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("FunID", funid);
		jsonObject.put("Language", ConstantUtil.dongbaoLanguage);
		jsonObject.put("Data", json);
		jsonObject.put("IsCommitConfirmAtOnce", true);
	//	jsonObject.put("FlowCode", requestId);
		return jsonObject;
	}
	
	/**
	 *	查询接口入参封装
	 * @param funid 模块编号，参照表1
	 * @param language 语言。1：简体中文，2：繁体中文，3：英文
	 * @param data 数数据信息。多个字段条件是以AND串接
	 * @param sqlwhere Sql条件表达式
	 * @return
	 */
	public static JSONObject sendPackaging(String funid,Integer language,JSONObject data,String sqlwhere) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("FunID", funid);
		jsonObject.put("Language", language);
		jsonObject.put("Data", data);
		jsonObject.put("SqlWhere", sqlwhere);
		return jsonObject;
	}
	
	
	/**
	 * 内部接口不需要带cookie url 路径 param ?后面的参数
	 */
	public static String sendGet(String url, String param) {
		LOGGER.error("请求地址========================"+url+"=====请求参数==============="+param);
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// connection.setRequestProperty("X-Xencio-Client-Id",
			// PropUtil.getString("ecustom", "jz.token.Access"));
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			LOGGER.error("工作日接口请求异常=========="+e);
			result = "";
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	
	/**
	 * 	东宝HR系统get请求
	 */
	public static String postUrlDataResult(String strArry) throws MalformedURLException, IOException {
		URL realurl = new URL(strArry);
		BufferedReader reader = new BufferedReader(new InputStreamReader(realurl.openStream()));
		StringBuilder inputLine = new StringBuilder();
		String str = "";
		while ((str = reader.readLine()) != null) {
			inputLine.append(str);
		}
		str = inputLine.toString();
		if (str.startsWith("\"") && str.endsWith("\"")) {
			str = str.substring(1, str.length() - 1);
		}
		if (str.contains("</")) {
			str = str.substring(str.indexOf(">") + 1, str.lastIndexOf("<"));
		}
		return str;
	}
	
	/**
	 * 	东宝HR系统post请求
	 */
	public static String postUrlDataResultByPost(String requestUrl, String param)  {
		URL url=null;
		HttpURLConnection connection = null;
		DataOutputStream out = null;
		BufferedReader reader = null;
		try {
			url = new URL(requestUrl);
		    connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);// 需要输出
			connection.setDoInput(true);// 需要输入
			connection.setUseCaches(false);// 不允许缓存
			connection.setConnectTimeout(9999999);
			connection.setReadTimeout(99999999);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST");// 设置请求方式
			connection.setRequestProperty("Accept", "application/json");// 设置接收数据的格式
			connection.setRequestProperty("Content-Type", "multipart/form-data;charset=UTF-8"); // form表单
			out = new DataOutputStream(connection.getOutputStream());
			out.write(param.getBytes("UTF-8"));
			out.flush();
			out.close();
			InputStream input = null;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				input = connection.getInputStream();
				StringBuilder inputLine = new StringBuilder();
				String str = "";
				reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				while ((str = reader.readLine()) != null) {
					inputLine.append(str);
				}
				str = inputLine.toString();
				if (str.startsWith("\"") && str.endsWith("\"")) {
					str = str.substring(1, str.length() - 1);
				}
				return str;
			} else {
				input = connection.getErrorStream();
				StringBuilder inputLine = new StringBuilder();
				String str = "";
				reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				while ((str = reader.readLine()) != null) {
					inputLine.append(str);
				}
				str = inputLine.toString();
				LOGGER.error("接口请求异常========================================== " + str);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			LOGGER.error("接口地址异常" + e);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("接口IO流异常 " + e);
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	
	
}
