package com.e6.custom.action;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.e6.common.utils.HttpUtils;
import com.e6.constant.ConstantUtil;
import com.e6.e9.flowaction.AbstractAction;
import com.e6.e9.service.HrmresourceService;

/**
 * 归档节点时更新HR状态为离职，HR系统按离职日期过滤离职人员和在职人员--
 * HR05-员工离职办理流程---制造中心职能
 * @author 阳浩明1
 *
 */
public class HR05LizhiZZEndAction extends AbstractAction{
	
	private static Logger LOGGER = Logger.getLogger(HR05LizhiZZEndAction.class);
	
	@Override
	public void execute() throws Exception {
		Integer requestId = getRequestId();
		LOGGER.error("HR05-员工离职办理流程---制造中心职能 =="+requestId);
		JSONObject jsonObject = new JSONObject();
		setData(jsonObject);
		JSONObject sendPackaging = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoRs020101,jsonObject,requestId+"");
		LOGGER.error("HR05-员工离职办理流程---制造中心职能人员信息============="+sendPackaging.toString());
		String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging.toString());
		String value = HttpUtils.sendUpdatePackaging(postUrlDataResultByPost);
		if(!value.equals("")) {
			setRequest("异常信息如下：","HR05-员工离职办理流程---制造中心职能 归档异常:"+value,getRequestInfo());
			throw new Exception("HR05LizhiZZEndAction");
		}
		LOGGER.error("HR05-员工离职办理流程---制造中心职能  归档结束========================");
	}

	/**
	 * 
	 * @param jsonObject
	 */
	private void setData(JSONObject jsonObject) {
		jsonObject.put("InCumbency", "0");
		jsonObject.put("OAID", getMainStringValue("gh1",""));
		jsonObject.put("EmpNo", getMainStringValue("gh1",""));
		jsonObject.put("EmpName", HrmresourceService.findByWorkCodeLastName(getMainStringValue("gh1","")));
		jsonObject.put("ComeDate", HrmresourceService.findByWorkCodeCompanystartdate(getMainStringValue("gh1","")));
		jsonObject.put("PerField9", getXzlb(getMainIntegerValue("xzlb", 0)));
		jsonObject.put("CompanyNo", "1");
		jsonObject.put("PartNo", HrmresourceService.findByDeptCode(getMainIntegerValue("ygbm")+""));
	}

	/**
	 * 	薪资类别
	 * 	OA:计时 计件 保底计件
	 *  HR:"04:保底计件
			03:包干
			02:计件
			01:计时"
	 * @param mainIntegerValue
	 * @return
	 */
	private String getXzlb(Integer val) {
		String strVal="";
		if(val==0) {
			strVal="01";
		}
		if(val==1) {
			strVal="02";
		}
		if(val==2) {
			strVal="04";
		}
		return strVal;
	}
	
}
