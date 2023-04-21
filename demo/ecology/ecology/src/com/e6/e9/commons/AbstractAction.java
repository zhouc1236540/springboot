package com.e6.e9.commons;

import org.apache.log4j.Logger;

import com.e6.common.utils.CustomUtil;

import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

/**
 * 
 * 描述:流程接口封装
 * 
 * @author HM
 * @date 2022年5月4日
 *
 */
public abstract class AbstractAction implements Action {

	private Logger log = Logger.getLogger(this.getClass());

	public String execute(RequestInfo requestInfo) {
		int requestId = requestInfo.getRequestManager().getRequestid();
		log.info(this.getClass().getName() + " Start, requestId = " + requestId);
		try {
			process(requestInfo);
			return Action.SUCCESS;
		} catch (Exception e) {
			String msg = "<div style=\"padding-bottom: 10px;\">报错时间：" + CustomUtil.getStringDate("yyyy-MM-dd HH:mm:ss")
					+ "， 请求ID：" + requestId + "，文件：" + getClass().getName() + "</div>" + "<span>提示消息：" + e.getMessage()
					+ "</span>";
			log.error(msg, e);
			requestInfo.getRequestManager().setMessageid("20088");
			requestInfo.getRequestManager().setMessagecontent(msg);
		} finally {
			log.info(this.getClass().getName() + " End, requestId = " + requestId);
		}
		return Action.FAILURE_AND_CONTINUE;
	}

	public abstract void process(RequestInfo requestInfo) throws Exception;

	protected String getMainString(Property[] mainFields, String fieldName) {
		if (CustomUtil.isBlank(fieldName))
			return "";
		return ActionUtil.getMainStringValue(mainFields, fieldName);
	}

	protected Integer getMainInteger(Property[] mainFields, String fieldName) {
		return ActionUtil.getMainIntegerValue(mainFields, fieldName);
	}

	protected Integer getMainIntegerCK(Property[] mainFields, String fieldName) {
		return getMainIntegerCK(mainFields, fieldName, fieldName);
	}

	protected Integer getMainIntegerCK(Property[] mainFields, String fieldName, String showName) {
		Integer id = ActionUtil.getMainIntegerValue(mainFields, fieldName);
		if (id == null)
			throw new RuntimeException("“" + showName + "”转换成整型失败！");
		return id;
	}

	protected Double getMainDouble(Property[] mainFields, String fieldName) {
		return ActionUtil.getMainDoubleValue(mainFields, fieldName);
	}

}
