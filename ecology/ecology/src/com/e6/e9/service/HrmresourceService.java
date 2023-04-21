/**
* @Title: HrmresourceService
* @Description: 
* @author: HM
* @date 2022年5月4日 下午7:58:38
*/
package com.e6.e9.service;

import org.apache.log4j.Logger;

import com.e6.e9.commons.RecordUtil;
import com.e6.e9.entities.HrmResource;

import weaver.conn.RecordSet;

/**
 * 描述:人力资源服务
 * 
 * @author HM
 * @date 2022年5月4日
 *
 */
public class HrmresourceService {

	private static Logger LOGGER = Logger.getLogger(HrmresourceService.class);

	public static HrmResource findByLoginId(Integer ID) {
		HrmResource res = null;
		try {
			res = RecordUtil.find(HrmResource.class, "id=?", ID);
		} catch (Exception e) {
			LOGGER.error("获取人员信息异常findByLoginId()", e);
		}
		return res;
	}
	
	/**
	 *	 入职日期查询
	 * @param ID
	 * @return
	 */
	public static String findByWorkCodeCompanystartdate(String workCode) {
		RecordSet recordSet = new RecordSet();
		String companystartdate="";
		try {
			recordSet.execute("select  companystartdate from hrmresource where workcode='"+workCode+"'");
			if(recordSet.next()) {
				companystartdate=recordSet.getString("companystartdate");
			}
		} catch (Exception e) {
			LOGGER.error("获取人员入职日期异常", e);
		}
		LOGGER.error("入职日期查询====="+companystartdate);
		return companystartdate;
	}
	
	/**
	 *	 部门编号查询
	 * @param ID
	 * @return
	 */
	public static String findByDeptCode(String deptId) {
		RecordSet recordSet = new RecordSet();
		String departmentcode="";
		try {
			recordSet.execute("select  departmentcode from hrmdepartment where id="+deptId+"");
			if(recordSet.next()) {
				departmentcode=recordSet.getString("departmentcode");
			}
		} catch (Exception e) {
			LOGGER.error("获取人员入职日期异常", e);
		}
		LOGGER.error("部门编号查询====="+departmentcode);
		return departmentcode;
	}
	
	/**
	 *	 员工姓名查询
	 * @param ID
	 * @return
	 */
	public static String findByWorkCodeLastName(String workCode) {
		RecordSet recordSet = new RecordSet();
		String lastname="";
		try {
			recordSet.execute("select  lastname from hrmresource where workcode='"+workCode+"'");
			if(recordSet.next()) {
				lastname=recordSet.getString("lastname");
			}
		} catch (Exception e) {
			LOGGER.error("获取人员入职日期异常", e);
		}
		LOGGER.error("员工姓名查询====="+lastname);
		return lastname;
	}

	/**
	 *	部门名称查询
	 * @param ID
	 * @return
	 */
	public static String findDeptName(String denptId) {
		RecordSet recordSet = new RecordSet();
		String deptName="";
		try {
			recordSet.execute("select  departmentname from hrmdepartment where id="+denptId+"");
			if(recordSet.next()) {
				deptName=recordSet.getString("departmentname");
			}
		} catch (Exception e) {
			LOGGER.error("获取人员入职日期异常", e);
		}
		LOGGER.error("部门名称查询====="+deptName);
		return deptName;
	}
	
	
	
	
	/**
	 *	岗位名称查询
	 * @param ID
	 * @return
	 */
	public static String findJobtitleName(String postId) {
		RecordSet recordSet = new RecordSet();
		String jobtitlename="";
		try {
			recordSet.execute("select  jobtitlename from hrmjobtitles where id="+postId+"");
			if(recordSet.next()) {
				jobtitlename=recordSet.getString("jobtitlename");
			}
		} catch (Exception e) {
			LOGGER.error("获取人员入职日期异常", e);
		}
		LOGGER.error("岗位名称查询====="+jobtitlename);
		return jobtitlename;
	}
	
	/**
	 *	办公地点查询
	 * @param ID
	 * @return
	 */
	public static String findLocaName(String locaId) {
		RecordSet recordSet = new RecordSet();
		String locationname="";
		try {
			recordSet.execute("SELECT locationname FROM hrmlocations where id="+locaId+"");
			if(recordSet.next()) {
				locationname=recordSet.getString("locationname");
			}
		} catch (Exception e) {
			LOGGER.error("办公地点查询异常", e);
		}
		LOGGER.error("办公地点查询====="+locationname);
		return locationname;
	}
	
	/**
	 *	调整后职级名称查询
	 * @param ID
	 * @return
	 */
	public static String findRankName(String jobcallId) {
		RecordSet recordSet = new RecordSet();
		String name="";
		try {
			recordSet.execute("SELECT name FROM hrmjobcall where id="+jobcallId+"");
			if(recordSet.next()) {
				name=recordSet.getString("name");
			}
		} catch (Exception e) {
			LOGGER.error("调整后职级名称异常", e);
		}
		LOGGER.error("调整后职级名称查询====="+name);
		return name;
	}
}
