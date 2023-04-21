package com.e6.e9.commons;

import com.e6.common.utils.CustomUtil;

import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.Row;

public final class ActionUtil {

	/**
	 * 获取主表数据，并转换成Integer类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	public static Integer getMainIntegerValue(Property[] fields, String fieldName) {
		return getMainIntegerValue(fields, fieldName, null);
	}

	/**
	 * 获取主表数据，并转换成Integer类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	public static Integer getMainIntegerValue(Property[] fields, String fieldName, Integer defValue) {
		String value = null;
		for (Property field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = field.getValue();
				break;
			}
		}
		return CustomUtil.getInteger(value, defValue);
	}

	public static Double getMainDouble(Property[] mainFields, String fieldName) {
		return ActionUtil.getMainDoubleValue(mainFields, fieldName);
	}

	/**
	 * 获取主表数据，并转换成Integer类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	public static Double getMainDoubleValue(Property[] fields, String fieldName) {
		return getMainDoubleValue(fields, fieldName, null);
	}

	/**
	 * 获取主表数据，并转换成Integer类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	public static Double getMainDoubleValue(Property[] fields, String fieldName, Double defValue) {
		String value = null;
		for (Property field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = field.getValue();
				break;
			}
		}
		return CustomUtil.getDouble(value, defValue);
	}

	/**
	 * 获取主表数据，并转换成String类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	public static String getMainString(Property[] fields, String fieldName) {
		return getMainStringValue(fields, fieldName, null);
	}

	/**
	 * 获取主表数据，并转换成String类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @return
	 */
	public static String getMainStringValue(Property[] fields, String fieldName) {
		return getMainStringValue(fields, fieldName, null);
	}

	/**
	 * 获取主表数据，并转换成String类型值返回。
	 * 
	 * @param fields
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	public static String getMainStringValue(Property[] fields, String fieldName, String defValue) {
		String value = null;
		for (Property field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = field.getValue();
				break;
			}
		}
		return CustomUtil.isBlank(value) ? defValue : value;
	}

	/**
	 * 获取明细表数据，并转换成String类型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	public static String getCellStringValue(Row row, String fieldName) {
		return getCellStringValue(row, fieldName, null);
	}

	/**
	 * 获取明细表数据，并转换成String类型值返回。
	 * 
	 * @param rows
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	public static String getCellStringValue(Row row, String fieldName, String defValue) {
		if (CustomUtil.isBlank(fieldName))
			return "";
		String value = null;
		Cell[] cells = row.getCell();
		for (Cell field : cells) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = field.getValue();
				break;
			}
		}
		return CustomUtil.isBlank(value) ? defValue : value;
	}

	/**
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @Title: getMainInteger
	 * @date 2019-09-23 17:12
	 * @param @param  mainFields
	 * @param @param  string
	 * @param @return 参数
	 * @return Integer 返回类型
	 * @throws @return Integer
	 * @param mainFields
	 * @param string
	 * @return
	 */
	public static Integer getMainInteger(Property[] fields, String fieldName) {
		return getMainIntegerValue(fields, fieldName, null);
	}

	/**
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @Title: getMainIntegerCK
	 * @date 2019-09-23 17:14
	 * @param @param  mainFields
	 * @param @param  fieldName
	 * @param @param  showName
	 * @param @return 参数
	 * @return Integer 返回类型
	 * @throws @return Integer
	 * @param mainFields
	 * @param fieldName
	 * @param showName
	 * @return
	 */
	private static Integer getMainIntegerCK(Property[] mainFields, String fieldName, String showName) {
		Integer id = ActionUtil.getMainIntegerValue(mainFields, fieldName);
		if (id == null) {
			return null;
		}
		return id;
	}

	public static Integer getCellIntegerValue(Row row, String fieldName) {
		if (CustomUtil.isBlank(fieldName))
			return null;
		Integer value = null;
		Cell[] cells = row.getCell();
		for (Cell field : cells) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = CustomUtil.getInteger(field.getValue());
				break;
			}
		}
		return value;
	}
}
