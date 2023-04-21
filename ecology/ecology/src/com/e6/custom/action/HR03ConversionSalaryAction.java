package com.e6.custom.action;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.e6.common.utils.DBUtil;
import com.e6.common.utils.HttpUtils;
import com.e6.constant.ConstantUtil;
import com.e6.e9.flowaction.AbstractAction;
import com.e6.e9.service.HrmresourceService;

import weaver.conn.RecordSet;

/**
 * HR03 员工岗位薪资变动流程 
 * 	流程归档更新HR系统
 * @author 阳浩明1
 *
 */
public class HR03ConversionSalaryAction extends AbstractAction{
	
	private static Logger LOGGER = Logger.getLogger(HR03ConversionSalaryAction.class);
	
	@Override
	public void execute() throws Exception {
		Integer requestId = getRequestId();
		LOGGER.error("HR03 员工岗位薪资变动流程 流程归档时将转正日期，转正后岗位，转正后职级更新HR系统=="+requestId);
		JSONObject jsonObject = new JSONObject();
		setData(jsonObject);
		JSONObject sendPackaging = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoYD021001,jsonObject,requestId+"");
		LOGGER.error("HR03 员工岗位薪资变动流程人员信息============="+sendPackaging.toString());
		String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging.toString());
		String value= HttpUtils.sendUpdatePackaging(postUrlDataResultByPost);
		if(!value.equals("")) {
			setRequest("异常信息如下：","HR03 员工岗位变动流程归档异常："+value,getRequestInfo());
			throw new Exception("HR03ConversionSalaryAction");
		}
		JSONObject jsonObject2 = new JSONObject();
		setData2(jsonObject2);
		JSONObject sendPackaging2 = HttpUtils.sendUpdatePackaging(ConstantUtil.dongbaoXZ021001,jsonObject2,requestId+"");
		LOGGER.error("HR03 员工岗位薪资变动流程薪资信息============="+sendPackaging2.toString());
		String postUrlDataResultByPost2 = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsaveorupdatebaoUrl, sendPackaging2.toString());
		String value2= HttpUtils.sendUpdatePackaging(postUrlDataResultByPost2);
		if(!value2.equals("")) {
			setRequest("异常信息如下：","HR03 员工岗位薪资变动归档异常："+value2,getRequestInfo());
			throw new Exception("HR03ConversionSalaryAction");
		}
		LOGGER.error("HR03 员工岗位薪资变动流程  流程归档时将转正日期，转正后岗位，转正后职级更新HR系统结束========================");
	}

	/**
	 * 薪资信息封装
	 * @param jsonObject2
	 */
	private void setData2(JSONObject jsonObject2) {
		jsonObject2.put("BillNo", getMainStringValue("lcbh", ""));
		jsonObject2.put("ChangeTypeNo", "04");
		jsonObject2.put("OAID", getMainStringValue("yggh",""));
		jsonObject2.put("EmpNo", getMainStringValue("yggh", ""));
		jsonObject2.put("PayMode", getXzlb(getMainIntegerValue("gzlbh")));
		jsonObject2.put("ReqYYMMDD", getMainStringValue("sqrq", ""));
		jsonObject2.put("YYMMDD", getMainStringValue("bdzxrq", ""));
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
	 * 人员异动信息封装
	 * @param jsonObject
	 */
	private void setData(JSONObject jsonObject) {
		String tableNameByWorkFlowId = DBUtil.getTableNameByWorkFlowId(getRequestId() + "");
		jsonObject.put("BillNo", getMainStringValue("lcbh",""));
		jsonObject.put("AppForDate", getMainStringValue("sqrq", ""));
		jsonObject.put("YYMMDD", getMainStringValue("bdzxrq", ""));
		jsonObject.put("EmpNo", getMainStringValue("yggh",""));
		jsonObject.put("ChangeTypeNo", "04");
		//部门名称
		jsonObject.put("OAID", getMainStringValue("yggh",""));
		jsonObject.put("PartName", HrmresourceService.findDeptName(getMainIntegerValue("dzhbm")+""));
		jsonObject.put("PartNo", HrmresourceService.findByDeptCode(getMainIntegerValue("dzhbm")+""));
		jsonObject.put("QtName", getGwmc(getMainIntegerValue("dzhzw"),jsonObject));//岗位
		jsonObject.put("PerField55", ConstantUtil.getTypeName(tableNameByWorkFlowId, "dzhhsdw_new",getMainIntegerValue("dzhhsdw_new")));
		jsonObject.put("PerField69", HrmresourceService.findLocaName(getMainIntegerValue("dzhbgdd")+""));
		jsonObject.put("PerField68", DBUtil.getSlectValue(DBUtil.getTableNameByWorkFlowId(getRequestId()+""),"bdqcbdw",getMainIntegerValue("bdqcbdw")));
		if(getMainIntegerValue("gjjgmyqsfbd")==0) {
			jsonObject.put("PerField73", DBUtil.getSlectValue(DBUtil.getTableNameByWorkFlowId(getRequestId()+""),"dzhdzfgjj",getMainIntegerValue("dzhdzfgjj")));
		}
		if(getMainIntegerValue("gjjgmyqsfbd")==1) {
			jsonObject.put("PerField73", "");
		}
		jsonObject.put("LevelLevel", HrmresourceService.findRankName(getMainIntegerValue("dzhzj")+""));
		if(getMainIntegerValue("sbgmyqsfbd")==0) {
			jsonObject.put("PerField67", DBUtil.getSlectValue(DBUtil.getTableNameByWorkFlowId(getRequestId()+""),"dzhdcbqk",getMainIntegerValue("dzhdcbqk")));
		}
		if(getMainIntegerValue("sbgmyqsfbd")==1) {
			jsonObject.put("PerField67", "");
		}
		LOGGER.error("数据处理========================="+jsonObject.toString());
	}
	
	/**
	 * 	岗位名称和编号
	 * @param gwId
	 * @param jsonObject
	 * @return
	 */
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
		LOGGER.error("岗位名称和编号====="+outkey+"==="+jobtitlename);
		return jobtitlename;
	}

	

	
	
	/**
	 * 	调整后的住房公积金情况
	 * @param mainIntegerValue
	 * @return
	 */
	private String getDzhdzfgjj(Integer val) {
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

	/**
	 * 	整后参保单位
	 * @param integer
	 * @return
	 */
	private String getDzhcbdw(Integer dzhcbdw) {
		String strVal="";
		if(dzhcbdw==0) {
			strVal="甘露";
		}
		if(dzhcbdw==1) {
			strVal="中盛";
		}
		if(dzhcbdw==2) {
			strVal="中宝盈";
		}
		if(dzhcbdw==3) {
			strVal="瑞金盈";
		}
		if(dzhcbdw==4) {
			strVal="中钊";
		}
		if(dzhcbdw==5) {
			strVal="新恒盛";
		}
		if(dzhcbdw==6) {
			strVal="金苹果";
		}
		if(dzhcbdw==7) {
			strVal="金兰";
		}
		if(dzhcbdw==8) {
			strVal="检测中心";
		}
		return strVal;
	}

	/**
	 * 	调整后核算单位
	 * @param mainIntegerValue
	 * @return
	 */
//	private String getDzhhsdw(Integer val) {
//		String strVal="";
//		if(val==0) {
//			strVal="甘露";
//		}
//		if(val==1) {
//			strVal="中盛";
//		}
//		if(val==2) {
//			strVal="中宝盈";
//		}
//		if(val==3) {
//			strVal="瑞金盈";
//		}
//		if(val==4) {
//			strVal="中钊";
//		}
//		if(val==5) {
//			strVal="新恒盛";
//		}
//		if(val==6) {
//			strVal="金苹果";
//		}
//		if(val==7) {
//			strVal="金兰";
//		}
//		if(val==8) {
//			strVal="检测中心";
//		}
//		return strVal;
//	}
//
//	/**
//	 * 岗位编号
//	 * @param mainIntegerValue
//	 * @return
//	 */
//	private String getGwmc(Integer gwId) {
//		RecordSet recordSet = new RecordSet();
//		recordSet.execute("select outkey from hrmjobtitles where id="+gwId);
//		String outkey="";
//		if(recordSet.next()) {
//			outkey=recordSet.getString("outkey");
//		}
//		return outkey;
//	}

}
