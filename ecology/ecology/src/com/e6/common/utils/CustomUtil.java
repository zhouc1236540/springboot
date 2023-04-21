package com.e6.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 自定义工具类。
 * 
 * @author HM
 */
public class CustomUtil {

	private static Logger LOGGER = Logger.getLogger(CustomUtil.class);

	/**
	 * 判空处理
	 * 
	 * @param val
	 * @return
	 */
	public static String getIsNullVal(String val) {
		String valStr = null;
		if (CustomUtil.isEmpty(val)) {
			valStr = "";
		} else {
			valStr = val;
		}
		return valStr;
	}
	
	public static boolean isEmpty(String val) {
		if (null == val || val.equals("") || val.equals("null")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获得一个util包下的UUID
	 * 
	 * @return String UUID
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}

	public static String getStringDate(String format) {
		return getStringDate(new Date(), format);
	}

	public static String getStringDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 将对象转换成整型引用。
	 * 
	 * @param value
	 * @return
	 */
	public static Integer getInteger(Object value) {
		return getInteger(value, null);
	}

	/**
	 * 将字符串转换为整型，如果转化失败返回-1。
	 * 
	 * @param v
	 * @return
	 */
	public static int getInt(String v) {
		return getInt(v, -1);
	}

	/**
	 * 将字符串转换成整数，如果转换失败返回默认值。
	 * 
	 * @param v        字符串
	 * @param defValue 默认值
	 * @return 转换后的整数
	 */
	public static int getInt(String v, Integer defValue) {
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			return defValue;
		}
	}

	/**
	 * 将对象转换成整型。
	 * 
	 * @param value
	 * @return
	 */
	public static void isInt(String value, String errMsg) {
		if (value == null || value.isEmpty() || getInteger(value, null) == null) {
			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * 将对象转换成字符串。
	 * 
	 * @param value
	 * @return
	 */
	public static Integer getInteger(Object value, Integer defValue) {
		try {
			return Integer.parseInt(getString(value));
		} catch (Exception e) {
		}
		return defValue;
	}

	public static String getString(Object value) {
		return value == null ? "" : value.toString();
	}

	public static String getString(Object value, String devValue) {
		return value == null ? devValue : value.toString();
	}

	public static String getStringMaxDate(String prefixFormat) {
		Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat(prefixFormat).format(calendar.getTime()) + calendar.getMaximum(Calendar.DATE);
	}

	public static String getExceptionMessage(Throwable throwable) {
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		return throwable.toString();
	}

	public static Double getDouble(String v, Double defValue) {
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
			return defValue;
		}
	}

	public static Double getDouble(Double v, Double defValue) {
		return v == null ? defValue : v;
	}

	public static boolean isBlank(String v) {
		return v == null || v.trim().length() == 0;
	}

	public static boolean isNotBlank(String v) {
		return !isBlank(v);
	}

	public static void checkKeys(Map<?, ?> map, String checkKeys) {
		String checkParamArr[] = checkKeys.split(",");
		for (String key : checkParamArr) {
			if (!map.containsKey(key) || map.get(key) == null || StringUtils.isBlank(map.get(key) + "")) {
				throw new RuntimeException("key=" + key + ", value is blank");
			}
		}
	}

	public static double fixed(double d, int f) {
		BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
		return bg.doubleValue();
	}

	public static String leftByBit(String str, int length, String charSet) {
		if (str == null) {
			return "";
		}
		int subLen = 0, chiLen = 0;
		if ("UTF-8".equalsIgnoreCase(charSet)) {
			chiLen = 3;
		}
		if ("GBK".equalsIgnoreCase(charSet)) {
			chiLen = 2;
		}
		if ("GB2312".equalsIgnoreCase(charSet)) {
			chiLen = 2;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char charAt = str.charAt(i);
			if (subLen + (charAt > 128 ? chiLen : 1) >= length) {
				break;
			}
			sb.append(charAt);
			subLen += charAt > 128 ? chiLen : 1;
		}
		return sb.toString();
	}

	public static boolean matches(String input, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	public static String toPlainString(double num) {
		BigDecimal bd1 = new BigDecimal(num);
		return bd1.toPlainString();
	}

	public static Double getDouble(String v) {
		return isBlank(v) ? null : getDouble(v, -1.0);
	}

	public static String stringToUnicode(String string) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i); // 取出每一个字符
			unicode.append("\\u" + Integer.toHexString(c));// 转换为unicode
		}
		return unicode.toString();
	}

	/**
	 * 判断字符串是否是符合指定格式的时间
	 * 
	 * @param date   时间字符串
	 * @param format 时间格式
	 * @return 是否符合
	 */
	public static boolean isDate(String date, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.parse(date);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断字符串有效性
	 */
	public static boolean valid(String src) {
		return !(src == null || "".equals(src.trim()));
	}

	/**
	 * 判断一组字符串是否有效
	 * 
	 * @param src
	 * @return
	 */
	public static boolean valid(String... src) {
		for (String s : src) {
			if (!valid(s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断一个对象是否为空
	 */
	public static boolean valid(Object obj) {
		return !(null == obj);
	}

	/**
	 * 判断一组对象是否有效
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean valid(Object... objs) {
		if (objs != null && objs.length != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断集合的有效性
	 */
	public static boolean valid(Collection col) {
		return !(col == null || col.isEmpty());
	}

	/**
	 * 判断一组集合是否有效
	 * 
	 * @param cols
	 * @return
	 */
	public static boolean valid(Collection... cols) {
		for (Collection c : cols) {
			if (!valid(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断map是否有效
	 * 
	 * @param map
	 * @return
	 */
	public static boolean valid(Map map) {
		return !(map == null || map.isEmpty());
	}

	/**
	 * 判断一组map是否有效
	 * 
	 * @param maps 需要判断map
	 * @return 是否全部有效
	 */
	public static boolean valid(Map... maps) {
		for (Map m : maps) {
			if (!valid(m)) {
				return false;
			}
		}
		return true;
	}
}