package com.e6.constant;

import com.e6.e9.commons.PropUtil;

import weaver.conn.RecordSet;

/**
 * 	常量维护
 * @author 阳浩明1
 *
 */
public class ConstantUtil {
	private static String environment="ecustom-proc";
	//系统接口地址
	@SuppressWarnings("unused")
	public static String dongsearchbaoUrl=PropUtil.getString(environment, "dongbao.system.url.search");
	public static String dongsaveorupdatebaoUrl=PropUtil.getString(environment, "dongbao.system.url.saveorupdate");
	//接口语言
	@SuppressWarnings("unused")
	public static Integer dongbaoLanguage=PropUtil.getIntCK(environment, "dongbao.system.language");
	//模块数据标识|部门
	@SuppressWarnings("unused")
	public static String dongbaoRs010501=PropUtil.getString(environment, "dongbao.rs010501.FunID");
	//模块数据标识|岗位
	@SuppressWarnings("unused")
	public static String dongbaoRs015001=PropUtil.getString(environment, "dongbao.rs015001.FunID");
	//模块数据标识|部门岗位关系表
	@SuppressWarnings("unused")
	public static String dongbaoRs015002=PropUtil.getString(environment, "dongbao.rs015002.FunID");
	//模块数据标识|职务和类型
	@SuppressWarnings("unused")
	public static String dongbaoRs014001=PropUtil.getString(environment, "dongbao.rs014001.FunID");
	//模块数据标识|人员信息
	@SuppressWarnings("unused")
	public static String dongbaoRs020101=PropUtil.getString(environment, "dongbao.rs020101.FunID");
	//模块数据标识|薪资
	@SuppressWarnings("unused")
	public static String dongbaoXZ021001=PropUtil.getString(environment, "dongbao.xz021001.FunID");
	//模块数据标识|异动
	@SuppressWarnings("unused")
	public static String dongbaoYD021001=PropUtil.getString(environment, "dongbao.yd021001.FunID");
	//模块数据标识|离职
	@SuppressWarnings("unused")
	public static String dongbaoLZ021001=PropUtil.getString(environment, "dongbao.lz021001.FunID");
	
	
	
	
	/**
	 * 下拉框类型
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static String getTypeName(String tableIndex, String fileds, Integer filedsVal) {
		String filedVal = "";
		String sql = "select fieldid,selectvalue,selectname  from workflow_selectitem where  fieldid=(SELECT  max(id) FROM workflow_billfield "
				+ "WHERE billid=" + tableIndex + " AND fieldname='" + fileds + "') and selectvalue=" + filedsVal;
		RecordSet recordSet = new RecordSet();
		recordSet.execute(sql);
		if (recordSet.next()) {
			filedVal = recordSet.getString("selectname");
		}
		return filedVal;
	}
}
