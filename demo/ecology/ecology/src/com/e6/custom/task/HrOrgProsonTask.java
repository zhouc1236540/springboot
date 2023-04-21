package com.e6.custom.task;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.e6.common.utils.DateUtil;

import weaver.conn.RecordSet;
import weaver.interfaces.schedule.BaseCronJob;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * 	人员关系值缓存维护
 * @author 阳浩明1
 *
 */
public class HrOrgProsonTask extends BaseCronJob{
	private static Logger logger = LoggerFactory.getLogger(HrOrgProsonTask.class);
	public static ConcurrentHashMap<String, Integer> concurrentCompany = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentBranch = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentDepartment = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentStation = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentSuperior = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentSuperiorLastName = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentSite = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentEducation = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentBank = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentTechnical = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentGroups = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Integer> concurrentJobactivities = new ConcurrentHashMap<String, Integer>();
//	public static ConcurrentHashMap<String, Integer> concurrentPersonnel = new ConcurrentHashMap<String, Integer>();
	@Override
	public void execute() {
		logger.error("组织相关信息缓存开始============================================="+DateUtil.date2String(new Date()));
		//总公司
		syncCompany();
		//分部
		syncBranch();
		//部门
		syncDepartment();
		
		//岗位
		syncStation();
		
		//上级|人员
		syncSuperior();
		
		//名称缓存
		syncSuperiorLastName();
		
		//职称
		syncTechnical();
		
		//地址
		syncSite();
		
		//学历
		syncEducation();
		//职务类型
		syncgroups();
		//职务
		syncJobactivities();
		//银行
		syncBank();
		logger.error("组织相关信息缓存结束============================================="+ DateUtil.date2String(new Date()));
		
	}

	/**
	 * 	职务
	 */
	@SuppressWarnings("static-access")
	private void syncJobactivities() {
		this.concurrentJobactivities.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,jobactivityname from hrmjobactivities ");
		while(recordSet.next()) {
			this.concurrentJobactivities.put(recordSet.getString("jobactivityname"), recordSet.getInt("id"));
		}
	}

	/**
	 * 职务类型
	 */
	@SuppressWarnings("static-access")
	private void syncgroups() {
		this.concurrentGroups.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,jobgroupname from hrmjobgroups ");
		while(recordSet.next()) {
			this.concurrentGroups.put(recordSet.getString("jobgroupname"), recordSet.getInt("id"));
		}
	}

//	private void syncPersonnel() {
//		this.concurrentPersonnel.clear();
//		RecordSet recordSet = new RecordSet();
//		recordSet.execute("select id,outkey from HrmResource ");
//		while(recordSet.next()) {
//			this.concurrentPersonnel.put(recordSet.getString("outkey"), recordSet.getInt("id"));
//		}
//	}

	@SuppressWarnings("static-access")
	private void syncTechnical() {
		this.concurrentTechnical.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,name from hrmjobcall ");
		while(recordSet.next()) {
			this.concurrentTechnical.put(recordSet.getString("name"), recordSet.getInt("id"));
		}
	}

	@SuppressWarnings("static-access")
	private void syncBank() {
		this.concurrentBank.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,bankname from hrmbank");
		while(recordSet.next()) {
			this.concurrentBank.put(recordSet.getString("bankname"), recordSet.getInt("id"));
		}
	}

	@SuppressWarnings("static-access")
	private void syncEducation() {
		this.concurrentEducation.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,name from HrmEducationLevel");
		while(recordSet.next()) {
			this.concurrentEducation.put(recordSet.getString("name"), recordSet.getInt("id"));
		}
	}

	@SuppressWarnings("static-access")
	private void syncSite() {
		this.concurrentSite.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,locationname from hrmlocations ");
		while(recordSet.next()) {
			this.concurrentSite.put(recordSet.getString("locationname"), recordSet.getInt("id"));
		}
	}

	//人员outkey作为缓存条件
	@SuppressWarnings("static-access")
	private void syncSuperior() {
		this.concurrentSuperior.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,workcode from HrmResource  ");
		while(recordSet.next()) {
			this.concurrentSuperior.put(recordSet.getString("workcode"), recordSet.getInt("id"));
		}
	}
	
	//人员名称作为缓存条件
		@SuppressWarnings("static-access")
		private void syncSuperiorLastName() {
			this.concurrentSuperiorLastName.clear();
			RecordSet recordSet = new RecordSet();
			recordSet.execute("select id,lastname from HrmResource  ");
			while(recordSet.next()) {
				this.concurrentSuperiorLastName.put(recordSet.getString("lastname"), recordSet.getInt("id"));
			}
		}

	@SuppressWarnings("static-access")
	private void syncStation() {
		this.concurrentStation.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,jobtitlecode from hrmjobtitles ");
		while(recordSet.next()) {
			this.concurrentStation.put(recordSet.getString("jobtitlecode"), recordSet.getInt("id"));
		}
	}

	@SuppressWarnings("static-access")
	private void syncDepartment() {
		this.concurrentDepartment.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,departmentcode from hrmdepartment ");
		while(recordSet.next()) {
			this.concurrentDepartment.put(recordSet.getString("departmentcode"), recordSet.getInt("id"));
		}
	}

	@SuppressWarnings("static-access")
	private void syncBranch() {
		this.concurrentBranch.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,subcompanyname from hrmsubcompany ");
		while(recordSet.next()) {
			this.concurrentBranch.put(recordSet.getString("subcompanyname"), recordSet.getInt("id"));
		}
	}
	
	@SuppressWarnings("static-access")
	private void syncCompany() {
		this.concurrentCompany.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("SELECT  id,companyname FROM hrmcompany");
		while(recordSet.next()) {
			this.concurrentCompany.put(recordSet.getString("companyname"), recordSet.getInt("id"));
		}
	}

	@SuppressWarnings("static-access")
	private void syncLeaveDate() {
		this.concurrentCompany.clear();
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select id,leaveDate from HrmResource  ");
		while(recordSet.next()) {
			this.concurrentCompany.put(recordSet.getString("leaveDate"), recordSet.getInt("id"));
		}
	}


	
}
