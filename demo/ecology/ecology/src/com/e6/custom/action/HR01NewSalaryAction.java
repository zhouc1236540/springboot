package com.e6.custom.action;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.e6.common.utils.DBUtil;
import com.e6.common.utils.DateUtil;
import com.e6.common.utils.HttpUtils;
import com.e6.constant.ConstantUtil;
import com.e6.e9.flowaction.AbstractAction;
import com.e6.e9.service.HrmresourceService;

/**
 * 	流程审批归档时，将参保档次，是否购买住房公积金更新HR系统
 * 1、人员信息更新
 * 2、人员薪资更新
 * @author 阳浩明1
 *
 */
public class HR01NewSalaryAction extends AbstractAction{
	
	private static Logger LOGGER = Logger.getLogger(HR01NewSalaryAction.class);
	
	@Override
	public void execute() throws Exception {
		Integer requestId = getRequestId();
		LOGGER.error("HR01 员工新进薪资审批流程 ---流程审批归档时，将参保档次，是否购买住房公积金,薪资信息更新HR系统=="+requestId);
		JSONObject jsonObject = new JSONObject();
		//必填校验
		//validateData();
		LOGGER.error("==============="+getMainStringValue("sqrq", "")+"================测试"+DateUtil.getDateFromString(getMainStringValue("sqrq", ""))+"");
		setData(jsonObject);
		JSONObject sendPackaging = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoRs020101,jsonObject,requestId+"");
		LOGGER.error("HR01 员工新进薪资审批流程---甘露人员信息============="+sendPackaging.toString());
		JSONObject jsonObject2 = new JSONObject();
		setData2(jsonObject2);
		JSONObject sendPackaging2 = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoXZ021001,jsonObject2,requestId+"");
		LOGGER.error("HR01 员工新进薪资审批流程---甘露人员薪资信息============="+sendPackaging2.toString());
		String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging.toString());
		String value= HttpUtils.sendUpdatePackaging(postUrlDataResultByPost);
		if(!value.equals("")) {
			setRequest("异常信息如下：","HR01 员工新进薪资审批人员信息异常："+value,getRequestInfo());
			throw new Exception("HR01NewSalaryAction");
		}
		String postUrlDataResultByPost2 = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging2.toString());
		String value2= HttpUtils.sendUpdatePackaging(postUrlDataResultByPost2);
		if(!value2.equals("")) {
			setRequest("异常信息如下：","HR01 员工新进薪资审批薪资异常："+value2,getRequestInfo());
			throw new Exception("HR01NewSalaryAction");
		}
		LOGGER.error("HR01 员工新进薪资审批流程 ---流程审批归档时，将参保档次，是否购买住房公积金更新HR系统结束========================");
	}


	/**
	 * 	薪资信息封装
	 * 01  过试用期
		02  定期调整
		03  升迁调整
		04  岗位异动
		05 年度调整
		06  其他
		07  最低工资调整
		08  新人入职
	 * @param jsonObject2
	 */
	private void setData2(JSONObject jsonObject2) {
		jsonObject2.put("BillNo", getMainStringValue("lcbh", ""));
		jsonObject2.put("ChangeTypeNo", "08");
		jsonObject2.put("OAID", getMainStringValue("yggh",""));
		jsonObject2.put("EmpNo", getMainStringValue("yggh", ""));
		jsonObject2.put("PayMode", getXzlb(getMainIntegerValue("gzlb")));
		jsonObject2.put("PayMode", getXzlb(getMainIntegerValue("gzlb")));
		jsonObject2.put("ReqYYMMDD", getMainStringValue("gzsfrq", ""));
		jsonObject2.put("YYMMDD", getMainStringValue("gzsfrq", ""));
		jsonObject2.put("PayNo12", getMainDoubleValue("zcgzgz", 0.0d));
		jsonObject2.put("PayNo14", getMainDoubleValue("gwgz", 0.0d));
		jsonObject2.put("PayNo13", getMainDoubleValue("zwgz", 0.0d));
		jsonObject2.put("PayNo15", getMainDoubleValue("xygz", 0.0d));
		jsonObject2.put("PayNo22", getMainDoubleValue("cb2", 0.0d));
		jsonObject2.put("PayNo16", getMainDoubleValue("gdjbf", 0.0d));
		jsonObject2.put("PayNo20", getMainDoubleValue("gwbt", 0.0d));
		jsonObject2.put("PayNo17", getMainDoubleValue("fwbt", 0.0d));
		jsonObject2.put("PayNo18", getMainDoubleValue("dhjt", 0.0d));
		jsonObject2.put("PayNo53", getMainDoubleValue("jgfb", 0.0d));
		jsonObject2.put("PayNo54", getMainDoubleValue("flfb", 0.0d));
		jsonObject2.put("PayNo19", getMainDoubleValue("hzjt", 0.0d));
		jsonObject2.put("PayNo70", getMainDoubleValue("bdjjgzh", 0.0d));
		jsonObject2.put("PayTotal", getMainDoubleValue("gzze", 0.0d));
	}

	/**
	 * 	薪资类别
	 * 	OA:计时 计件 保底计件
	 *  HR:01  计时
		02  计件
		03 经理级
		04  包干
		05  月薪
		06 保底计件
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
			strVal="06";
		}
		return strVal;
	}

	/**
	 * 	人员数据信息封装
	 * @param jsonObject
	 */
	private void setData(JSONObject jsonObject) {
		jsonObject.put("OAID", getMainStringValue("yggh",""));
		jsonObject.put("PerField67", DBUtil.getSlectValue(DBUtil.getTableNameByWorkFlowId(getRequestId()+""),"cbdc",getMainIntegerValue("cbdc")));
		jsonObject.put("PerField73", getSfgmzfgjj(getMainIntegerValue("sfgmzfgjj")));
		jsonObject.put("EmpNo", getMainStringValue("yggh",""));
		jsonObject.put("EmpName", HrmresourceService.findByWorkCodeLastName(getMainStringValue("yggh","")));
		jsonObject.put("ComeDate", HrmresourceService.findByWorkCodeCompanystartdate(getMainStringValue("yggh","")));
		jsonObject.put("PerField9", getGzlb(getMainIntegerValue("gzlb")));
		jsonObject.put("CompanyNo", "1");
		jsonObject.put("PartNo", HrmresourceService.findByDeptCode(getMainIntegerValue("ygbm")+""));
		LOGGER.error("数据处理========================="+jsonObject.toString());
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
	private String getGzlb(Integer val) {
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

	

	/**
	 * 	是否购买住房公积金
	 * @param mainStringValue
	 * @return
	 */
	private String getSfgmzfgjj(Integer val) {
		String strVal="";
		if(val==0) {
			strVal="标准购买";
		}
		if(val==1) {
			strVal="特殊申请";
		}
		if(val==2) {
			strVal="不购买";
		}
		return strVal;
	}
	
}
