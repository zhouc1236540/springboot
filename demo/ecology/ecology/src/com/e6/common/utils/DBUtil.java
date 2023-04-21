/**
* @Title: DBUtil
* @Description: 
* @author: HM
* @date 2022年8月13日 上午7:33:54
*/
package com.e6.common.utils;

import org.apache.log4j.Logger;

import weaver.conn.RecordSet;
import weaver.general.Util;

/**
 * 描述:
 * 
 * @author HM
 * @date 2022年8月13日
 *
 */
public class DBUtil {
	private static Logger LOGGER = Logger.getLogger(DBUtil.class);

	
	/**
	 * 	根据下拉值查找内容
	 * 
	 */
	public static String  getSlectValue(String tableIndex,String field,Integer val) {
		String value="";
		RecordSet recordSet = new RecordSet();
		if(!CustomUtil.getIsNullVal(val+"").equals("")) {
			String sql="select fieldid,selectvalue,selectname   " + 
					"from workflow_selectitem where  fieldid=(SELECT  id FROM workflow_billfield  " + 
					"WHERE billid="+tableIndex+" AND fieldname='"+field+"') and selectvalue="+val;
			LOGGER.error("下拉框脚本============================"+sql);
			recordSet.execute(sql);
			String selectname="";
			if(recordSet.next()) {
				selectname=recordSet.getString("selectname");
			}
			value=selectname;
		}
		return value;
	}
	
	/**
	 * 获取数据的最大id数
	 * <p>
	 * 
	 * @Title:getTablesIndex
	 *                       </p>
	 *                       <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param table
	 * @return
	 */
	public static int getTablesIndex(String table) {
		Integer index = 0;
		RecordSet rs = new RecordSet();
		try {
			rs.execute("select max(id)+1 maxId from " + table + " ");
			if (rs.next()) {
				index = Integer.getInteger(rs.getString("maxId"));
			}
			if (index == null) {
				index = 1;
			}
		} catch (Exception e) {
			LOGGER.error("自增脚本异常================="+e);
		}
		return index;
	}


	
	/**
	 * 通过流程id查找表名称
	 * <p>
	 * 
	 * @Title:getTableNameByRequestid
	 *                                </p>
	 *                                <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param requestid
	 * @return
	 */
	public static String getTableNameByWorkFlowId(String requestCode) {
		RecordSet rs = new RecordSet();
		String sql = "select formid from workflow_base where id=(SELECT workflowid FROM workflow_requestbase "
				+ " where requestid="+requestCode+")";
		rs.execute(sql);
		if (rs.next()) {
			String formid = Util.null2String(rs.getString("formid"));
			return formid;
		}
		return "";
	}
	
	/**
	 * 通过flowid查找表名称
	 * <p>
	 * 
	 * @Title:getTableNameByRequestid
	 *                                </p>
	 *                                <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param requestid
	 * @return
	 */
	public static String getTableNameByWorkFlow(String workflowid) {
		RecordSet rs = new RecordSet();
		String sql = "select formid from workflow_base where id="+workflowid;
		rs.execute(sql);
		if (rs.next()) {
			String formid = Util.null2String(rs.getString("formid")).replaceAll("-", "");
			return "formtable_main_" + formid;
		}
		return "";
	}

	/**
	 * 通过流程编码取流程表名
	 * <p>
	 * 
	 * @Title:getTableNameByRequestid
	 *                                </p>
	 *                                <p>
	 * @Description:
	 *               </p>
	 * 
	 * @param requestid
	 * @return
	 */
	public static String getTableNameByRequestCode(String requestCode) {
		RecordSet rs = new RecordSet();
		String sql = "select formid from workflow_base where id=(SELECT workflowid FROM workflow_requestbase "
				+ " where requestid=(select requestid from workflow_codeseqrecord where workflowcode='" + requestCode
				+ "'))";
		rs.execute(sql);
		if (rs.next()) {
			String formid = Util.null2String(rs.getString("formid")).replaceAll("-", "");
			return "formtable_main_" + formid;
		}
		return "";
	}

	/**
	 * 通过requestId取流程表名
	 * <p>
	 * 
	 * @Title:getTableNameByRequestid
	 *                                </p>
	 *                                <p>
	 * @Description:
	 *               </p>
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
	 * 
	 * <p>
	 * 
	 * @Title:getSubcompanyCode
	 *                          </p>
	 *                          <p>
	 * @Description:获取工号
	 *                   </p>
	 * 
	 * @param subId
	 * @return
	 */
	public static String getHrmresourceWorkCode(String hrmId) {
		String workcode = "";
		if (null == hrmId || hrmId.equals("")) {
			return workcode;
		}
		RecordSet recordSet = new RecordSet();
		String sql = "select workcode from hrmresource where id=" + hrmId;
		recordSet.execute(sql);
		if (recordSet.next()) {
			workcode = recordSet.getString("workcode");
		}
		if (null == workcode || workcode.equals("")) {
			LOGGER.error("人员查询异常==========" + sql + "============" + hrmId);
		}
		return workcode;
	}

	/**
	 * 
	 * <p>
	 * 
	 * @Title:getSubcompanyCode
	 *                          </p>
	 *                          <p>
	 * @Description:获取姓名
	 *                   </p>
	 * 
	 * @param subId
	 * @return
	 */
	public static String getHrmresourceLastName(String hrmId) {
		String lastName = "";
		if (null == hrmId || hrmId.equals("")) {
			return lastName;
		}
		RecordSet recordSet = new RecordSet();
		String sql = "select lastname from hrmresource where id=" + hrmId;
		recordSet.execute(sql);
		if (recordSet.next()) {
			lastName = recordSet.getString("lastname");
		}
		if (null == lastName || lastName.equals("")) {
			LOGGER.error("人员查询异常==========" + sql + "============" + hrmId);
		}
		return lastName;
	}

}
