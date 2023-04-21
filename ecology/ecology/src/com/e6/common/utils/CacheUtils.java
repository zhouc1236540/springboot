package com.e6.common.utils;

import weaver.hrm.company.CompanyComInfo;
import weaver.hrm.company.DepartmentComInfo;
import weaver.hrm.company.SubCompanyComInfo;
import weaver.hrm.resource.ResourceComInfo;
import weaver.matrix.MatrixUtil;

/**
 * 系统缓存工具
 * 
 * @author
 *
 */
public class CacheUtils {

	public static void cache() {

		try {
			new CompanyComInfo().removeCache();
			new SubCompanyComInfo().removeCache();
			MatrixUtil.sysSubcompayData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new DepartmentComInfo().removeCache();
			MatrixUtil.sysDepartmentData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			new weaver.hrm.job.JobTitlesComInfo().removeCache();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ResourceComInfo rinfo;
		try {
			rinfo = new ResourceComInfo();
			rinfo.removeResourceCache();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			new weaver.hrm.roles.RolesComInfo().removeCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
