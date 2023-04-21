package com.e6.custom.action;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.e6.common.utils.DateUtil;
import com.e6.common.utils.HttpUtils;
import com.e6.constant.ConstantUtil;
import com.e6.e9.flowaction.AbstractAction;

/**
 * 人力资源节点提交后将工资结算截止日期更新HR离职日期
 * HR05-员工离职办理流程--制造中心工厂
 * @author 阳浩明
 *
 */
public class HR05LizhiZZZAction extends AbstractAction{
	
	private static Logger LOGGER = Logger.getLogger(HR05LizhiZZZAction.class);
	
	@Override
	public void execute() throws Exception {
		Integer requestId = getRequestId();
		LOGGER.error("HR05-员工离职办理流程--制造中心工厂职能人力资源节点---=="+requestId);
		JSONObject jsonObject = new JSONObject();
		setData(jsonObject);
		JSONObject sendPackaging = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoLZ021001,jsonObject,requestId+"");
		LOGGER.error("HR05-员工离职办理流程--制造中心工厂职能流程人力资源节点---甘露人员信息============="+sendPackaging.toString());
		String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging.toString());
		String value = HttpUtils.sendUpdatePackaging(postUrlDataResultByPost);
		if(!value.equals("")) {
			setRequest("异常信息如下：","HR05-员工离职办理流程--制造中心工厂 人力资源节点异常"+value,getRequestInfo());
			throw new Exception("HR05LizhiZZZAction");
		}
		LOGGER.error("HR05-员工离职办理流程---制造中心职能人力资源节点---结束========================");
	}
	
	
	/**
	 * 人员信息封装
	 * @param jsonObject
	 */
	private void setData(JSONObject jsonObject) {
		jsonObject.put("LeaveDate", getMainStringValue("gzjsjzrq",""));
		jsonObject.put("DID", getMainStringValue("lcbh",""));
		jsonObject.put("OAID", getMainStringValue("gh1",""));
		jsonObject.put("EmpNo", getMainStringValue("gh1",""));
		jsonObject.put("ReqLeaveDate", getMainStringValue("sqrq", ""));
		jsonObject.put("ReqDays", DateUtil.diffDate(getMainStringValue("sqrq", ""),getMainStringValue("pzlzrq", "")));//批准日期-申请日期的天数
		
		jsonObject.put("LeaveTypeNo", getLzlx(getMainIntegerValue("lzlx",0)));
		jsonObject.put("WhyLeave", getLzyy(getMainIntegerValue("lzyy")));
		jsonObject.put("pzlzrq",getMainStringValue("pzlzrq", ""));
		
	}

	
	/**
	 * 离职原因
	 * 	辞职
		辞退
		自离
		
		01 个人原因 
		02 公司原因 
		03 其它原因 
	 * @param mainIntegerValue
	 * @return
	 */
	private String getLzyy(Integer lzyy) {
		String lzlxVal="";
		if(lzyy==0) {
			lzlxVal="02";
		}
		if(lzyy==1) {
			lzlxVal="01";
		}
		if(lzyy==2) {
			lzlxVal="03";
		}
		return lzlxVal;
	}

	/**
	 * 离职类型    辞职  辞退  自离
	 * 办理离职手续　跨公司调岗办理入离职
	 * 01 辞职 
		02 辞退 
		03 退休 
		04 其它 
		05 自离 

	 * @param mainIntegerValue
	 * @return
	 */
	private String getLzlx(Integer lzlx) {
		String lzlxVal="";
		if(lzlx==0) {
			lzlxVal="01";
		}
		if(lzlx==1) {
			lzlxVal="02";
		}
		if(lzlx==2) {
			lzlxVal="05";
		}
		return lzlxVal;
	}

}
