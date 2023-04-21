package com.e6.custom.action;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.e6.common.utils.HttpUtils;
import com.e6.constant.ConstantUtil;
import com.e6.e9.flowaction.AbstractAction;
import com.e6.e9.service.HrmresourceService;

import weaver.conn.RecordSet;

/**
 * 流程归档时将转正日期，转正后岗位，转正后职级更新HR系统
 * @author 阳浩明1
 *
 */
public class HR02ConversionSalaryAction extends AbstractAction{
	
	private static Logger LOGGER = Logger.getLogger(HR02ConversionSalaryAction.class);
	
	@Override
	public void execute() throws Exception {
		Integer requestId = getRequestId();
		LOGGER.error("HR02 员工转正薪资审批流程  流程归档时将转正日期，转正后岗位，转正后职级更新HR系统=="+requestId);
		JSONObject jsonObject = new JSONObject();
		setData(jsonObject);
		JSONObject sendPackaging = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoRs020101,jsonObject,requestId+"");
		LOGGER.error("HR02 员工转正薪资审批人员信息============="+sendPackaging.toString());
		String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging.toString());
		String value=HttpUtils.sendUpdatePackaging(postUrlDataResultByPost);
		if(!value.equals("")) {
			setRequest("异常信息如下：","HR02 员工岗位信息归档异常:"+value,getRequestInfo());
			throw new Exception("HR02ConversionSalaryAction");
		}
		JSONObject jsonObject2 = new JSONObject();
		setData2(jsonObject2);
		JSONObject sendPackaging2 = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoXZ021001,jsonObject2,requestId+"");
		LOGGER.error("HR02 员工转正薪资审批薪资信息============="+sendPackaging2.toString());
		String postUrlDataResultByPost2 = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging2.toString());
		String value2= HttpUtils.sendUpdatePackaging(postUrlDataResultByPost2);
		if(!value2.equals("")) {
			setRequest("异常信息如下：","HR02 员工转正薪资审批归档异常:"+value2,getRequestInfo());
			throw new Exception("HR02ConversionSalaryAction");
		}
		LOGGER.error("HR02 员工转正薪资审批流程  流程归档时将转正日期，转正后岗位，转正后职级更新HR系统结束========================");
	}

	/**
	 * 	转正薪资信息封装
	 * @param jsonObject2
	 */
	private void setData2(JSONObject jsonObject2) {
		jsonObject2.put("BillNo", getMainStringValue("lcbh", ""));
		jsonObject2.put("ChangeTypeNo", "01");
		jsonObject2.put("OAID", getMainStringValue("yggh",""));
		jsonObject2.put("EmpNo", getMainStringValue("yggh", ""));
		jsonObject2.put("PayMode", getXzlb(getMainIntegerValue("gzlbh")));
		jsonObject2.put("ReqYYMMDD", getMainStringValue("sqrq", ""));
		jsonObject2.put("YYMMDD", getMainStringValue("zzrq", ""));
		jsonObject2.put("PayNo12", getMainDoubleValue("zcgzgzh", 0.0d));
		jsonObject2.put("PayNo14", getMainDoubleValue("gwgzh", 0.0d));
		jsonObject2.put("PayNo13", getMainDoubleValue("zwgzh", 0.0d));
		jsonObject2.put("PayNo15", getMainDoubleValue("xygzh", 0.0d));
		jsonObject2.put("PayNo22", getMainDoubleValue("cbh2", 0.0d));
		jsonObject2.put("PayNo16", getMainDoubleValue("gdjbfh", 0.0d));
		jsonObject2.put("PayNo20", getMainDoubleValue("gwbth", 0.0d));
		jsonObject2.put("PayNo17", getMainDoubleValue("fwbth", 0.0d));
		jsonObject2.put("PayNo18", getMainDoubleValue("dhjth", 0.0d));
		jsonObject2.put("PayNo53", getMainDoubleValue("jgfbh", 0.0d));
		jsonObject2.put("PayNo54", getMainDoubleValue("flfbh", 0.0d));
		jsonObject2.put("PayNo19", getMainDoubleValue("hzjth", 0.0d));
		jsonObject2.put("PayNo70", getMainDoubleValue("bdjjgzh", 0.0d));
		//jsonObject2.put("PayNo25", getMainDoubleValue("cbh1", 0.0d));
		jsonObject2.put("PayTotal", getMainDoubleValue("gzze", 0.0d));
		//jsonObject2.put("", getMainStringValue("jxtcjj",""));
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
	 *  	人员信息封装
	 * @param jsonObject
	 */
	private void setData(JSONObject jsonObject) {
		jsonObject.put("OAID", getMainStringValue("yggh",""));
		jsonObject.put("TryuseDate", getMainStringValue("zzrq",""));
		jsonObject.put("QtName", getGwmc(getMainIntegerValue("zzhgw"),jsonObject));
		jsonObject.put("LevelLevel", HrmresourceService.findRankName(getMainIntegerValue("zzhzj")+""));
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

	

	private String getGwmc(Integer gwId, JSONObject jsonObject) {
		RecordSet recordSet = new RecordSet();
		recordSet.execute("select jobtitlename,outkey from hrmjobtitles where id="+gwId);
		String jobtitlename="";
		String outkey="";
		if(recordSet.next()) {
			jobtitlename=recordSet.getString("jobtitlename");
			outkey=recordSet.getString("outkey");
		}
		jsonObject.put("QuartersNo", outkey);
		return jobtitlename;
	}

}
