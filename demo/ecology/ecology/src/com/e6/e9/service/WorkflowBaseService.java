package com.e6.e9.service;

import com.e6.e9.commons.RecordUtil;
import com.e6.e9.entities.WorkflowBase;

/**
 * 路径基本信息服务类。
 * 
 * @author William
 */
public class WorkflowBaseService {

	/**
	 * 根据流程ID获取流程当前可用版本。
	 * 
	 * @param workflowId
	 * @return
	 */
	public static Integer getActiveVersionId(int workflowId) {
		WorkflowBase e = RecordUtil.findById(WorkflowBase.class, workflowId);
		return e.getActiveVersionId();
	}

	/**
	 * 根据路径ID获取路径的基本信息。
	 * 
	 * @param workflowId
	 * @return
	 */
	public static WorkflowBase getById(int workflowId) {
		WorkflowBase e = RecordUtil.findById(WorkflowBase.class, workflowId);
		return e;
	}
}
