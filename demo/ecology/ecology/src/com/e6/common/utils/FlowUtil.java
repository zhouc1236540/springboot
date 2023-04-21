/**
* @Title: FlowUtil
* @Description: 
* @author: HM
* @date 2022年6月26日 上午8:54:40
*/
package com.e6.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weaver.conn.RecordSet;
import weaver.general.Util;

/**
 * 描述:流程相关工具类
 * 
 * @author HM
 * @date 2022年6月26日
 *
 */
public class FlowUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(FlowUtil.class);
	/**
	 * requestid获取表名
	 * <p>
	 * @Title:getTableNameByRequestid
	 * </p>
	 * <p>
	 * @Description:
	 * </p>
	 * 
	 * @param requestid
	 * @return
	 */
	public static String getTableNameByRequestid(String requestid) {
		RecordSet rs = new RecordSet();
		rs.execute(
				"select formid from workflow_base where id=(SELECT workflowid FROM workflow_requestbase where requestid='"
						+ requestid + "')");
		if (rs.next()) {
			String formid = Util.null2String(rs.getString("formid")).replaceAll("-", "");
			return "formtable_main_" + formid;
		}
		return "";
	}

	/**
	 * 通过流程flowid获取表名
	 * <p>
	 * @Title:getTableName
	 * </p>
	 * <p>
	 * @Description:
	 * </p>
	 * 
	 * @param workflowid
	 * @return
	 */
	public static String getTableName(String workflowid) {
		RecordSet rs = new RecordSet();
		rs.execute("select formid from workflow_base where id='" + workflowid + "'");
		if (rs.next()) {
			String formid = Util.null2String(rs.getString("formid")).replaceAll("-", "");
			return "formtable_main_" + formid;
		}
		return "";
	}

	/**
	 * 流程id找jdbm 金蝶的code
	 * 
	 * @param qingjlc
	 */
	public static String getJDcode(Integer requestId) {
		String jdcodeId = "";
		RecordSet recordSet = new RecordSet();
		String sql = "select  jdbm from " + FlowUtil.getTableNameByRequestid(requestId + "") + " where requestId="
				+ requestId;
		LOGGER.error("sql======jdbm"+sql);
		recordSet.execute(sql);
		if (recordSet.next()) {
			jdcodeId = recordSet.getString("jdbm");
		}
		return jdcodeId;
	}

	/**
	 * 流程id修改单据接口信息字段
	 * 
	 * @param qingjlc
	 */
	public static String updateJdbm(Integer requestId, String jdbm) {
		String jdcodeId = "";
		RecordSet recordSet = new RecordSet();
		String sql = "update " + FlowUtil.getTableNameByRequestid(requestId + "") + " set jdbm='" + jdbm
				+ "' where requestId=" + requestId;
		recordSet.execute(sql);
		return jdcodeId;
	}

	/**
	 * 流程id修改单据接口信息字段
	 * 
	 * @param qingjlc
	 */
	public static String updateIntefaceCode(Integer requestId, String zt, String msg) {
		String jdcodeId = "";
		RecordSet recordSet = new RecordSet();
		String sql = "update " + FlowUtil.getTableNameByRequestid(requestId + "") + " set jkzt=" + zt + ",jkxx='" + msg
				+ "' where requestId=" + requestId;
		LOGGER.error("sql修改单据======jdbm"+sql);
		recordSet.execute(sql);
		return jdcodeId;
	}
	
	
}
