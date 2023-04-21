package com.e6.e9.commons;

import org.apache.commons.lang3.StringUtils;

import com.e6.common.utils.CustomUtil;

import weaver.file.Prop;

/**
 * 
 * 描述:配置文件内容获取
 * 
 * @author HM
 * @date 2022年5月3日
 *
 */
public class PropUtil {
	public static String getString(String propFileName, String propName) {
		String value = Prop.getPropValue(propFileName, propName);
		return value;
	}

	public static String getStringCK(String propFileName, String propName) {
		String value = getString(propFileName, propName);
		if (StringUtils.isBlank(value)) {
			throw new RuntimeException(propFileName + ".properties -> " + propName + " is blank");
		}
		return value;
	}

	public static int getIntCK(String propFileName, String propName) {
		Integer value = CustomUtil.getInteger(Prop.getPropValue(propFileName, propName), null);
		if (value == null) {
			throw new RuntimeException(propFileName + ".properties -> " + propName + " is blank");
		}
		return value;
	}
}
