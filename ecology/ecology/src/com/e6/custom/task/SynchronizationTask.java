package com.e6.custom.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.e6.common.utils.CacheUtils;
import com.e6.common.utils.CustomUtil;
import com.e6.common.utils.DBUtil;
import com.e6.common.utils.DateUtil;
import com.e6.common.utils.HttpUtils;
import com.e6.common.utils.MD5Util;
import com.e6.constant.ConstantUtil;
import com.e6.e9.service.RequestInfoService;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetTrans;
import weaver.interfaces.schedule.BaseCronJob;
import weaver.soa.workflow.request.DetailTableInfo;
import weaver.soa.workflow.request.MainTableInfo;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.RequestService;
import weaver.system.SysRemindWorkflow;

/**
 * 组织架构同步
 * SQLSERVER
 *
 * @author 阳浩明1
 */
public class SynchronizationTask extends BaseCronJob {
    private static Logger LOGGER = Logger.getLogger(SynchronizationTask.class);

    //人员自定义字段初始化
    private static String zcdj = "";
    private static String zwdj = "";
    private static String cbzx = "";
    private static String gzxz = "";
    private static String xzlb = "";
    private static String syb = "";
    private static String yjbm = "";
    private static String ejbm = "";
    private static String fjlx = "";
    private RequestInfo requestInfo;
    //部门自定义字段初始化

    /**
     *  查找自定义字段赋值给这些成员
     */
    static {
        String sql = "select  cus.hrm_fieldlable,dic.fieldlabel from cus_formfield cus"
                + "		 left join cus_formdict dic  on cus.fieldid=dic.id "
                + "		 where cus.scope='HrmCustomFieldByInfoType' "
                + "		 AND cus.hrm_fieldlable in('社保电脑号','户籍地址','参保单位','住房公积金账号',"
                + "'习惯手','是否购买住房公积金','参保档次','核算单位','户籍类型')";
        RecordSet hrre = new RecordSet();
        hrre.execute(sql);
        while (hrre.next()) {
            String hrm_fieldlable = hrre.getString("hrm_fieldlable");
            String fieldlabel = hrre.getString("fieldlabel");
            if ("社保电脑号".equals(hrm_fieldlable)) {
                zcdj = fieldlabel;
            }
            if ("户籍地址".equals(hrm_fieldlable)) {
                zwdj = fieldlabel;
            }
            if ("参保单位".equals(hrm_fieldlable)) {
                cbzx = fieldlabel;
            }
            if ("住房公积金账号".equals(hrm_fieldlable)) {
                gzxz = fieldlabel;
            }
            if ("习惯手".equals(hrm_fieldlable)) {
                xzlb = fieldlabel;
            }
            if ("是否购买住房公积金".equals(hrm_fieldlable)) {
                syb = fieldlabel;
            }
            if ("参保档次".equals(hrm_fieldlable)) {
                yjbm = fieldlabel;
            }
            if ("核算单位".equals(hrm_fieldlable)) {
                ejbm = fieldlabel;
            }
            if ("户籍类型".equals(hrm_fieldlable)) {
                fjlx = fieldlabel;
            }
        }
        LOGGER.error("人员自定义字段初始化结束===============");
    }

    //总部、分部同为甘露集团
    private static String companyName = "甘露集团";
    //缓存标识
    public static int flag = 1;
    //总部|分部关系值
    public static Integer companynameId = null;
    public static Integer branchId = null;

    //监控值
    private static String numSum = "";//合计
    private static int isSyncNum = 0;//已同步
    private static int isExceNum = 0;//异常
    private static int isNewNum = 0;//新增
    private static String strCentent = "";//发送内容

    @Override
    public void execute() {
        LOGGER.error("组织架构同步开始===========================================" + DateUtil.date2String(new Date()));
        long startTime = System.currentTimeMillis();
        try {
            /**
             *  从数据库中查询组织架构信息缓存到hashmap
             */
            syncCache();
        } catch (Exception e) {
            LOGGER.error("组织架构缓存同步失败===========================================" + e);
            return;
        }
        try {
            numSum = "";//合计
            isSyncNum = 0;//已同步
            isExceNum = 0;//异常
            strCentent = "";//发送内容
            isNewNum = 0;
            //总公司|分部同步
            syncHrmcompany();
            LOGGER.error("分部同步完成===========================================");
            //部门
            JSONObject jsonObject = new JSONObject();
            JSONObject sendPackaging = HttpUtils.sendPackaging(ConstantUtil.dongbaoRs010501, ConstantUtil.dongbaoLanguage, jsonObject, "1=1");
            String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsearchbaoUrl, sendPackaging.toString());
            syncDept(postUrlDataResultByPost);
            LOGGER.error("部门同步完成===========================================");
            //岗位
            //syncJob();
            //职务类别与职务
            syncJobCategory();
            LOGGER.error("职务类别与职务完成===========================================");
            //人员同步
            syncpersonnel("1");
            LOGGER.error("人员同步完成===========================================");
            //矩阵信息同步
            syncMatrixSync(postUrlDataResultByPost);
            LOGGER.error("部门矩阵完成===========================================");
            //系统缓存清理
            CacheUtils.cache();
            strCentent += "日志访问路径：<a href=\"ecology/log/ecology\" target=\"_blank\"></a>";
            statusRemind();
            LOGGER.error("系统缓存清理完成===========================================");

        } catch (Exception e) {
            LOGGER.error("组织架构同步失败===========================================" + e);
            return;
        }

        long endTime = System.currentTimeMillis();
        LOGGER.error("组织架构同步结束===========================================" + DateUtil.date2String(new Date()));
        LOGGER.error("组织架构同步结束===========================================用时：" + (endTime - startTime) / 1000 + "秒");
    }

    /**
     * 同步时间更新
     */
    private void updaPersonnel() {
        RecordSet recordSet = new RecordSet();
        String formatDateTime = DateUtil.formatDate(new Date());
        recordSet.execute("update infoFlag SET lastTime='" + formatDateTime + "' WHERE id='fe6caf52b4824f7d9af5d179f2504480'");
    }

    /**
     * 更新部门矩阵信息
     */
    private void syncMatrixSync(String postUrlDataResultByPost) {
        numSum = "";//合计
        isSyncNum = 0;//已同步
        isExceNum = 0;//异常
        isNewNum = 0;
        if (postUrlDataResultByPost == null || postUrlDataResultByPost.equals("")) {
            LOGGER.error("部门矩阵数据同步异常=============================" + postUrlDataResultByPost);
        }
        JSONObject parseObject = JSONObject.parseObject(postUrlDataResultByPost);
        Boolean boolean1 = parseObject.getBoolean("Success");
        if (boolean1) {
            numSum = parseObject.getString("Message");
            LOGGER.error("部门矩阵数据同步3=============================数量" + parseObject.getString("Message"));
            JSONArray jsonArray = parseObject.getJSONArray("Data");
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (jsonObject2.getString("PartNo") == null) {
                    isExceNum++;
                    LOGGER.error("部门矩阵数据同步关系字段异常key=============================" + jsonObject2.toString());
                    continue;
                }
                if (HrOrgProsonTask.concurrentDepartment.containsKey(jsonObject2.getString("PartNo"))) {
                    isSyncNum++;
                    updateDepartment2(jsonObject2, HrOrgProsonTask.concurrentDepartment.get(jsonObject2.getString("PartNo")), arrayList);
                }
            }
            if (arrayList.size() > 0) {
                RecordSetTrans recordSetTrans = new RecordSetTrans();
                SubmitLeaveData(arrayList, recordSetTrans);
            }
        } else {
            LOGGER.error("部门矩阵数据同步异常2=============================" + parseObject.getString("Message"));
        }
        strCentent += "部门矩阵：Ecology同步部门矩阵共(" + numSum + "条数据;已同步" + isSyncNum + "条,部门矩阵新增" + isNewNum + "条,异常" + isExceNum + "条;</br>";
    }

    /**
     * 修改部门自定义表数据和主表数据
     *
     * @param jsonObject2
     * @param id
     * @return
     */
    private void updateDepartment2(JSONObject jsonObject2, Integer id, ArrayList<String> arrayList) {
        String FieldValue2 = "";
        String FieldValue = "";
        String MyField18 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField18"));
        String MyField20 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField20"));
        String MyField19 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField19"));
        String MyField21 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField21"));
        String MyField24 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField24"));
        String MyField23 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField23"));
        String MyField26 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField26"));
        String MyField25 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField25"));
        //PerField75|PerField76
        String MyField30 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField30"));
        String MyField31 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField31"));
        String MyField28 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField28"));
        String MyField29 = CustomUtil.getIsNullVal(jsonObject2.getString("MyField29"));
        if (!MyField18.equals("")) {
            FieldValue += "DT2.bmzg='" + HrOrgProsonTask.concurrentSuperior.get(MyField18) + "',";
        } else {
            FieldValue += "DT2.bmzg=null,";
        }
        if (!MyField20.equals("")) {
            FieldValue += "DT2.tdfzr='" + HrOrgProsonTask.concurrentSuperior.get(MyField20) + "',";
        } else {
            FieldValue += "DT2.tdfzr=null,";
        }
        if (!MyField19.equals("")) {
            FieldValue += "DT2.bmfzr2='" + HrOrgProsonTask.concurrentSuperior.get(MyField19) + "',";
        } else {
            FieldValue += "DT2.bmfzr2=null,";
        }

        if (!MyField23.equals("")) {
            FieldValue += "DT2.cwjl='" + HrOrgProsonTask.concurrentSuperior.get(MyField23) + "',";
        } else {
            FieldValue += "DT2.cwjl=null,";
        }
        if (!MyField26.equals("")) {
            FieldValue += "DT2.zpfzr='" + HrOrgProsonTask.concurrentSuperior.get(MyField26) + "',";
        } else {
            FieldValue += "DT2.zpfzr=null,";
        }
        if (!MyField25.equals("")) {
            FieldValue += "DT2.zpzy='" + HrOrgProsonTask.concurrentSuperior.get(MyField25) + "',";
        } else {
            FieldValue += "DT2.zpzy=null,";
        }
        if (!MyField30.equals("")) {
            FieldValue += "DT2.rszy='" + HrOrgProsonTask.concurrentSuperior.get(MyField30) + "',";
        } else {
            FieldValue += "DT2.rszy=null,";
        }
        if (!MyField31.equals("")) {
            FieldValue += "DT2.cwzy='" + HrOrgProsonTask.concurrentSuperior.get(MyField31) + "',";
        } else {
            FieldValue += "DT2.cwzy=null,";
        }
        //主表数据
        if (!MyField21.equals("")) {
            FieldValue += "DT2.bmfgld2='" + HrOrgProsonTask.concurrentSuperior.get(MyField21) + "',";
        } else {
            FieldValue += "DT2.bmfgld2=null,";
        }
        if (!MyField24.equals("")) {
            FieldValue += "DT2.rljl='" + HrOrgProsonTask.concurrentSuperior.get(MyField24) + "',";
        } else {
            FieldValue += "DT2.rljl=null,";
        }
        if (!MyField28.equals("")) {
            FieldValue += "DT2.zcgly='" + HrOrgProsonTask.concurrentSuperior.get(MyField28) + "',";
        } else {
            FieldValue += "DT2.zcgly=null,";
        }
        if (!MyField29.equals("")) {
            FieldValue += "DT2.cwzcgly='" + HrOrgProsonTask.concurrentSuperior.get(MyField29) + "',";
        } else {
            FieldValue += "DT2.cwzcgly=null,";
        }
        FieldValue = FieldValue.substring(0, FieldValue.length() - 1);
        //FieldValue2=FieldValue2.substring(0, FieldValue2.length()-1);
        String sql = " UPDATE  " +
                " DT2 " +
                " SET  " + FieldValue +
                " FROM HrmDepartment DT1,HrmDepartmentDefined DT2" +
                " WHERE DT1.ID=DT2.deptid and DT1.ID=" + id;

        arrayList.add(sql);

        //主表
//		String upsql="UPDATE " + 
//				" DT1" + 
//				" SET " + FieldValue2+ 
//				" FROM HrmDepartment DT1,HrmDepartmentDefined DT2" + 
//				" WHERE DT1.ID=DT2.deptid and DT1.ID="+id;
//		arrayList.add(upsql);
    }

    public static String str = "";

    /**
     * 人员同步
     *
     * @param status 0离职 1：在职
     */
    private void syncpersonnel(String status) {
        numSum = "";//合计
        isSyncNum = 0;//已同步
        isExceNum = 0;//异常
        isNewNum = 0;
        JSONObject jsonObject = new JSONObject();
        JSONObject sendPackaging = null;
        //最新同步时间
        String lastTimeDate = "";
        RecordSet recordSet = new RecordSet();
        recordSet.execute("select  lastTime from infoFlag where id='fe6caf52b4824f7d9af5d179f2504480'");
        if (recordSet.next()) {
            lastTimeDate = recordSet.getString("lastTime");
        }
        if (!CustomUtil.isBlank(lastTimeDate)) {
            sendPackaging = HttpUtils.sendPackaging(ConstantUtil.dongbaoRs020101, ConstantUtil.dongbaoLanguage, jsonObject, " Incumbency=" + status + " ");
        } else {
            sendPackaging = HttpUtils.sendPackaging(ConstantUtil.dongbaoRs020101, ConstantUtil.dongbaoLanguage, jsonObject, " Incumbency=" + status + " ");
        }
        LOGGER.error("人员同步请求报文===========================================" + sendPackaging.toString());
        String postUrlDataResultByPost = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsearchbaoUrl, sendPackaging.toString());
        JSONObject parseObject = JSONObject.parseObject(postUrlDataResultByPost);
        Boolean Success = parseObject.getBoolean("Success");
        if (Success) {
            JSONArray jsonData = parseObject.getJSONArray("Data");
            numSum = jsonData.size() + "";
            LOGGER.error("人员同步响应成功===========================================" + jsonData.size());
            syncPerson(jsonData);
        }
        if (status.equals("1")) {
            str = "在线";
        }
        strCentent += "人员：Ecology" + str + "人员共(" + numSum + ")条数据;已同步" + isSyncNum + "条,新增" + isNewNum + "条,异常" + isExceNum + "条;</br>";
        if (!CustomUtil.isBlank(lastTimeDate) && Success && status.equals("1")) {
            syncpersonnel("0");
            str = "离线";
        }
    }

    /**
     * 人员同步识别
     *
     * @param jsonData
     */
    private void syncPerson(JSONArray jsonData) {
        List<String> jsonArray = new ArrayList<String>();
        for (int i = 0; i < jsonData.size(); i++) {
            JSONObject jsonObject = jsonData.getJSONObject(i);
            String empno = jsonObject.getString("EmpNo");
            if (CustomUtil.getIsNullVal(empno).equals("")) {
                isExceNum++;
                LOGGER.error("必填字段为空========================" + jsonObject);
                continue;
            }
            if (HrOrgProsonTask.concurrentSuperior.containsKey(empno)) {
                isSyncNum++;

                syncCustomData(jsonObject, HrOrgProsonTask.concurrentSuperior.get(empno));
                updateOrgProson(jsonObject, HrOrgProsonTask.concurrentSuperior.get(empno), jsonArray);
            } else {
                isNewNum++;
                isSyncNum++;
                insertOrgProson(jsonObject);
            }
        }
        if (jsonArray.size() > 0) {
            RecordSetTrans recordSetTrans = new RecordSetTrans();
            SubmitLeaveData(jsonArray, recordSetTrans);
        }
        //时间更新
        updaPersonnel();
    }

    /**
     * 人员新增
     *
     * @param jsonObject
     * @param integer
     */
    private void insertOrgProson(JSONObject jsonObject) {
        RecordSet rs = new RecordSet();
        String creater = "1";
        rs.execute("update SequenceIndex set currentid=currentid+1 where indexdesc= 'resourceid' ");
        rs.execute("select currentid from SequenceIndex where indexdesc= 'resourceid' ");
        Integer max = null;
        if (rs.next()) {
            max = rs.getInt("currentid");
        }
        if (max == null || max.intValue() <= 0) {
            max = 1;
        }
        StringBuilder filed = new StringBuilder();//字段
        StringBuilder filedVal = new StringBuilder();//字段值
        String sql = "";
        sql = getSqlInterserStr(sql, filed, filedVal, max, creater, jsonObject);

        LOGGER.error("人员插入脚本=========" + sql);
        boolean res = false;
        try {
            res = rs.executeUpdate(sql, new Object[0]);
        } catch (Exception e) {
            LOGGER.error("人员插入异常=========" + e);
        }
        //缓存维护
        HrOrgProsonTask.concurrentSuperior.put(jsonObject.getString("EmpNo"), max);
        HrOrgProsonTask.concurrentSuperiorLastName.put(jsonObject.getString("EmpName"), max);
        syncCustomData(jsonObject, max);
        LOGGER.error("人员插入状态=========" + res);
    }

    /**
     * 自定义字段同步 ：多表关联修改：不通数据库的写法不一样，需要注意
     *
     * @param jsonObject
     * @param string
     */
    private static void syncCustomData(JSONObject jsonObject, Integer personId) {
        RecordSet recordSet = new RecordSet();
        Integer idVal = null;
        recordSet.execute("select id from cus_fielddata where id=" + personId);
        if (recordSet.next()) {
            idVal = recordSet.getInt("id");
        }
        String sql = "";
        try {
            boolean execute = false;
            if (idVal != null && idVal > 0) {
                sql = " update   DT2  set  " + zcdj + "='" + jsonObject.getString("PerField6")
                        + "' ," + zwdj + "='" + jsonObject.getString("IDCardAddr") + "' ," + cbzx + "="
                        + getPerField68(jsonObject.getString("PerField68")) + " ," + gzxz + "='" + jsonObject.getString("PerField7") + "' ,"
                        + xzlb + "=" + getPerField71(jsonObject.getString("PerField71")) + " ," + syb + "="
                        + getPerField73(jsonObject.getString("PerField73")) + " ,"
                        + yjbm + "=" + getPerField67(jsonObject.getString("PerField67")) + " ,"
                        + ejbm + "=" + getHsdw(jsonObject.getString("PerField55")) + ", "
                        + fjlx + "='" + jsonObject.getString("AddrTypeName") + "' "
                        + "  from Hrmresource DT1,cus_fielddata DT2 "
                        + " where DT1.id=DT2.id AND DT2.scopeid=1 AND DT2.scope='HrmCustomFieldByInfoType' and DT1.id=" + personId + "";
                LOGGER.error("自定义更新脚本=======" + sql);
                execute = recordSet.execute(sql);
            }
            if (idVal == null || idVal < 0) {
                sql = " insert into   cus_fielddata(scopeid,scope"
                        + "," + zcdj
                        + "," + zwdj
                        + "," + cbzx
                        + "," + gzxz
                        + "," + xzlb
                        + "," + syb
                        + "," + yjbm
                        + "," + ejbm
                        + "," + fjlx
                        + ",id"
                        + ") values("
                        + "1,'HrmCustomFieldByInfoType'"
                        + ",'" + jsonObject.getString("PerField6") + "'"
                        + ",'" + jsonObject.getString("IDCardAddr") + "'"
                        + "," + getPerField68(jsonObject.getString("PerField68")) + ""
                        + ",'" + jsonObject.getString("PerField7") + "'"
                        + "," + getPerField71(jsonObject.getString("PerField71"))
                        + "," + getPerField73(jsonObject.getString("PerField73"))
                        + "," + getPerField67(jsonObject.getString("PerField67"))
                        + "," + getHsdw(jsonObject.getString("PerField55")) + ""
                        + ",'" + jsonObject.getString("AddrTypeName") + "'"
                        + "," + personId
                        + ")";
                LOGGER.error("自定义插入脚本=======" + sql);
                execute = recordSet.execute(sql);
            }
            LOGGER.error("自定义字段修改状态=" + idVal + "=======" + execute);
        } catch (Exception e) {
            LOGGER.error("自定义字段异常" + e);

        }
    }


    private static Integer getHsdw(String val) {
        Integer strVal = null;
        if (val.equals("甘露")) {
            strVal = 0;
        }
        if (val.equals("金苹果")) {
            strVal = 1;
        }
        if (val.equals("新恒盛")) {
            strVal = 2;
        }
        if (val.equals("中盛安保")) {
            strVal = 3;
        }
        if (val.equals("水贝安保")) {
            strVal = 4;
        }
        if (val.equals("金苹果安保")) {
            strVal = 5;
        }
        if (val.equals("中钊")) {
            strVal = 6;
        }
        if (val.equals("中宝盈")) {
            strVal = 7;
        }
        if (val.equals("中冠安保")) {
            strVal = 8;
        }
        if (val.equals("上海代销")) {
            strVal = 9;
        }
        if (val.equals("制造中心职能部门")) {
            strVal = 10;
        }
        if (val.equals("K金厂")) {
            strVal = 11;
        }
        if (val.equals("机织链厂")) {
            strVal = 12;
        }
        if (val.equals("镶嵌厂")) {
            strVal = 13;
        }
        if (val.equals("铸造厂")) {
            strVal = 14;
        }
        if (val.equals("素金厂")) {
            strVal = 15;
        }
        if (val.equals("宝石事业部")) {
            strVal = 16;
        }
        if (val.equals("德胜行")) {
            strVal = 17;
        }
        if (val.equals("金兰")) {
            strVal = 18;
        }
        if (val.equals("无")) {
            strVal = 19;
        }
        return strVal;
    }

    private static Integer getPerField68(String val) {
        Integer strVal = null;
        if (val.equals("甘露")) {
            strVal = 0;
        }
        if (val.equals("中盛")) {
            strVal = 1;
        }
        if (val.equals("中宝盈")) {
            strVal = 2;
        }
        if (val.equals("瑞金盈")) {
            strVal = 3;
        }
        if (val.equals("中钊")) {
            strVal = 4;
        }
        if (val.equals("新恒盛")) {
            strVal = 5;
        }
        if (val.equals("金苹果")) {
            strVal = 6;
        }
        if (val.equals("金兰")) {
            strVal = 7;
        }
        if (val.equals("检测中心")) {
            strVal = 8;
        }
        return strVal;
    }


    /**
     * 参保档次
     *
     * @param string
     * @return
     */
    private static Integer getPerField67(String val) {
        Integer strVal = null;
        if (val.equals("深户一档")) {
            strVal = 9;
        }
        if (val.equals("一档标准")) {
            strVal = 1;
        }
        if (val.equals("一档")) {
            strVal = 2;
        }
        if (val.equals("自费一档（二档标准）")) {
            strVal = 10;
        }
        if (val.equals("自费一档（三档标准）")) {
            strVal = 11;
        }
        if (val.equals("自费二档")) {
            strVal = 0;
        }
        if (val.equals("二档自费")) {
            strVal = 5;
        }
        if (val.equals("自费二档（三档标准）")) {
            strVal = 15;
        }
        if (val.equals("二档标准")) {
            strVal = 12;
        }
        if (val.equals("标准二档")) {
            strVal = 7;
        }
        if (val.equals("二档")) {
            strVal = 4;
        }
        if (val.equals("标准三档")) {
            strVal = 13;
        }
        if (val.equals("三档标准")) {
            strVal = 8;
        }
        if (val.equals("三档")) {
            strVal = 14;
        }
        if (val.equals("商业险")) {
            strVal = 3;
        }
        if (val.equals("其他")) {
            strVal = 6;
        }
        return strVal;
    }


    /**
     * 是否购买住房公积金
     *
     * @param string
     * @return
     */
    private static Integer getPerField73(String val) {
        Integer strVal = null;
        if (val.equals("标准购买")) {
            strVal = 0;
        }
        if (val.equals("特殊申请")) {
            strVal = 1;
        }
        if (val.equals("不购买")) {
            strVal = 2;
        }
        return strVal;
    }


    /**
     * 习惯手
     *
     * @param string
     * @return
     */
    private static Integer getPerField71(String val) {
        Integer strVal = null;
        if (val.equals("左手")) {
            strVal = 0;
        }
        if (val.equals("右手")) {
            strVal = 1;
        }
        return strVal;
    }


    /**
     * sql拼接:人员新增
     *
     * @param sql
     * @param filed      字段名称
     * @param filedVal   字段内容
     * @param sj         时间
     * @param max        最新id
     * @param creater    创建人
     * @param jsonObject 数据
     * @return
     */
    @SuppressWarnings("static-access")
    private String getSqlInterserStr(String sql, StringBuilder filed, StringBuilder filedVal, Integer manId,
                                     String creater, JSONObject jsonObject) {
        String IDCardNo = CustomUtil.getIsNullVal(jsonObject.getString("IDCardNo"));
        if (!IDCardNo.equals("")) {
            filed.append("certificatenum,");
            filedVal.append("'" + IDCardNo + "',");
        } else {
            filed.append("certificatenum,");
            filedVal.append("null,");
        }
        String LevelLevel = CustomUtil.getIsNullVal(jsonObject.getString("LevelLevel"));
        if (!LevelLevel.equals("")) {
            filed.append("jobcall,");
            filedVal.append("" + getJobcall(LevelLevel) + ",");
        } else {
            filed.append("jobcall,");
            filedVal.append("null,");
        }
        String Sex = CustomUtil.getIsNullVal(jsonObject.getString("Sex"));
        if (!Sex.equals("")) {
            filed.append("sex,");
            filedVal.append("'" + getSex(Sex) + "',");
        } else {
            filed.append("sex,");
            filedVal.append("null,");
        }
        String Mobile = CustomUtil.getIsNullVal(jsonObject.getString("Mobile"));
        if (!Mobile.equals("")) {
            filed.append("mobile,");
            filedVal.append("'" + Mobile + "',");
        } else {
            filed.append("mobile,");
            filedVal.append("null,");
        }
        String EducationName = CustomUtil.getIsNullVal(jsonObject.getString("EducationName"));
        if (!EducationName.equals("")) {
            filed.append("educationlevel,");
            filedVal.append("" + getDeucationLevel(EducationName) + ",");
        } else {
            filed.append("educationlevel,");
            filedVal.append("null,");
        }
        String ComeDate = CustomUtil.getIsNullVal(jsonObject.getString("ComeDate"));
        if (!ComeDate.equals("")) {
            filed.append("companystartdate,");
            filedVal.append("'" + ComeDate.split("T")[0] + "',");
        } else {
            filed.append("companystartdate,");
            filedVal.append("null,");
        }
        String TryuseDate = CustomUtil.getIsNullVal(jsonObject.getString("TryuseDate"));
        if (!TryuseDate.equals("")) {
            filed.append("probationenddate,");
            filedVal.append("'" + TryuseDate.split("T")[0] + "',");
        } else {
            filed.append("probationenddate,");
            filedVal.append("null,");
        }
        String startdate = getStartDate(jsonObject, 1);
        String enddate = getStartDate(jsonObject, 2);
        //String IDCardNo = CustomUtil.getIsNullVal(jsonObject.getString("IDCardNo"));
        if (!enddate.equals("")) {
            filed.append("enddate,");
            filedVal.append("'" + enddate + "',");
        } else {
            filed.append("enddate,");
            filedVal.append("null,");
        }
        if (!startdate.equals("")) {
            filed.append("startdate,");
            filedVal.append("'" + startdate + "',");
        } else {
            filed.append("startdate,");
            filedVal.append("null,");
        }

        String PerField69 = CustomUtil.getIsNullVal(jsonObject.getString("PerField69"));
        if (!PerField69.equals("")) {
            filed.append("locationid,");
            filedVal.append("" + getLocationId(PerField69) + ",");
        } else {
            filed.append("locationid,");
            filedVal.append("null,");
        }
        String PartNo = CustomUtil.getIsNullVal(jsonObject.getString("PartNo"));
        if (!PartNo.equals("")) {
            filed.append("departmentid,");
            if (HrOrgProsonTask.concurrentDepartment.containsKey(PartNo)) {
                filedVal.append("" + HrOrgProsonTask.concurrentDepartment.get(PartNo) + ",");
            } else {//没有
                filedVal.append("null,");
            }
        } else {
            filed.append("departmentid,");
            filedVal.append("null,");
        }
        String QtName = CustomUtil.getIsNullVal(jsonObject.getString("QtName"));
        String DtName = CustomUtil.getIsNullVal(jsonObject.getString("DtName"));
        String quartersNo = CustomUtil.getIsNullVal(jsonObject.getString("QuartersNo"));
        if (!QtName.equals("") && !PartNo.equals("")) {
            filed.append("jobtitle,");
            filedVal.append("" + getJobtitle(QtName, PartNo, DtName, quartersNo) + ",");
        } else {
            filed.append("jobtitle,");
            filedVal.append("null,");
        }
        String EmpNo = CustomUtil.getIsNullVal(jsonObject.getString("EmpNo"));
        if (!EmpNo.equals("")) {
            filed.append("workcode,");
            filedVal.append("'" + jsonObject.getString("EmpNo") + "',");
            filed.append("loginid,");
            filedVal.append("'" + jsonObject.getString("EmpNo") + "',");
        } else {
            filed.append("workcode,");
            filedVal.append("null,");
            filed.append("loginid,");
            filedVal.append("null,");
        }
        filed.append("password,");
        filedVal.append("'" + getMd5Password("GL123") + "',");
        filed.append("subcompanyid1,");
        filedVal.append("" + this.branchId + ",");
        String EmpName = CustomUtil.getIsNullVal(jsonObject.getString("EmpName"));
        if (!EmpName.equals("")) {
            filed.append("lastname,");
            filedVal.append("'" + jsonObject.getString("EmpName") + "',");
        } else {
            filed.append("lastname,");
            filedVal.append("null,");
        }
        String Incumbency = CustomUtil.getIsNullVal(jsonObject.getString("InCumbency"));
        if (!Incumbency.equals("")) {
            filed.append("status,");
            filedVal.append("" + getIncumbency(Incumbency) + ",");
        } else {
            filed.append("status,");
            filedVal.append("null,");
        }
        String ParentEmpNo = CustomUtil.getIsNullVal(jsonObject.getString("ParentEmpNo"));
        LOGGER.error("Hr系统直接上级========================================" + ParentEmpNo);
        if (!ParentEmpNo.equals("")) {
            filed.append("managerid,");
            filedVal.append("" + HrOrgProsonTask.concurrentSuperior.get(ParentEmpNo) + ",");
        } else {
            filed.append("managerid,");
            filedVal.append("null,");
        }
        String PerField74 = CustomUtil.getIsNullVal(jsonObject.getString("PerField74"));
        if (!PerField74.equals("")) {
            filed.append("seclevel,");
            filedVal.append("" + PerField74 + ",");
        } else {
            filed.append("seclevel,");
            filedVal.append("0,");
        }
        String FolkName = CustomUtil.getIsNullVal(jsonObject.getString("FolkName"));
        if (!FolkName.equals("")) {
            filed.append("folk,");
            filedVal.append("'" + FolkName + "',");
        } else {
            filed.append("folk,");
            filedVal.append("null,");
        }
        sql = "insert into hrmresource( id,"
                + " " + filed + " "
                + " systemlanguage,createdate,createrid,lastmodid,lastmoddate,created,creater"
                + "  ) values("
                + "" + manId + ","
                + " " + filedVal + " "
                + " 7,FORMAT(GETDATE(),'yyyy-MM-dd'),1,1,FORMAT(GETDATE(),'yyyy-MM-dd'),GETDATE(),1)";
        return sql;
    }

    /**
     * 明文密码加密
     *
     * @param pw
     * @return
     */
    public static String getMd5Password(String pw) {
        return MD5Util.getMD5(pw);
    }

    /**
     * 人员修改
     *
     * @param jsonObject
     * @param integer
     * @throws Exception
     */
    private void updateOrgProson(JSONObject jsonObject, Integer empId, List<String> jsonArray) {
        String sql = "";
        StringBuilder filedVal = new StringBuilder();
        jsonArray.add(getUpDate(sql, filedVal, jsonObject, empId));
    }


    @SuppressWarnings("static-access")
    private String getUpDate(String sql, StringBuilder filedVal, JSONObject jsonObject, Integer empId) {
        String IDCardNo = CustomUtil.getIsNullVal(jsonObject.getString("IDCardNo"));
        String LevelLevel = CustomUtil.getIsNullVal(jsonObject.getString("LevelLevel"));
        String Sex = CustomUtil.getIsNullVal(jsonObject.getString("Sex"));
        String Mobile = CustomUtil.getIsNullVal(jsonObject.getString("Mobile"));
        String EducationName = CustomUtil.getIsNullVal(jsonObject.getString("EducationName"));
        String ComeDate = CustomUtil.getIsNullVal(jsonObject.getString("ComeDate"));
        String TryuseDate = CustomUtil.getIsNullVal(jsonObject.getString("TryuseDate"));
        String startdate = getStartDate(jsonObject, 1);
        String enddate = getStartDate(jsonObject, 2);
        String PerField69 = CustomUtil.getIsNullVal(jsonObject.getString("PerField69"));
        String PartNo = CustomUtil.getIsNullVal(jsonObject.getString("PartNo"));
        String DtName = CustomUtil.getIsNullVal(jsonObject.getString("DtName"));
        String QtName = CustomUtil.getIsNullVal(jsonObject.getString("QtName"));
        String EmpNo = CustomUtil.getIsNullVal(jsonObject.getString("EmpNo"));
        String EmpName = CustomUtil.getIsNullVal(jsonObject.getString("EmpName"));
        String ParentEmpNo = CustomUtil.getIsNullVal(jsonObject.getString("ParentEmpNo"));
        String Incumbency = CustomUtil.getIsNullVal(jsonObject.getString("InCumbency"));
        String PerField74 = CustomUtil.getIsNullVal(jsonObject.getString("PerField74"));
        String FolkName = CustomUtil.getIsNullVal(jsonObject.getString("FolkName"));
        String quartersNo = CustomUtil.getIsNullVal(jsonObject.getString("QuartersNo"));
        if (!IDCardNo.equals("")) {
            filedVal.append("certificatenum='" + IDCardNo + "',");
        } else {
            filedVal.append("certificatenum=null,");
        }
        if (!LevelLevel.equals("")) {//职称
            filedVal.append("jobcall=" + getJobcall(LevelLevel) + ",");
        } else {
            filedVal.append("jobcall=null,");
        }
        if (!Sex.equals("")) {
            filedVal.append("sex='" + getSex(Sex) + "',");
        } else {
            filedVal.append("sex=null,");
        }
        if (!Mobile.equals("")) {
            filedVal.append("mobile='" + Mobile + "',");
        } else {
            filedVal.append("mobile=null,");
        }
        if (!EducationName.equals("")) {//学历
            filedVal.append("educationlevel=" + getDeucationLevel(EducationName) + ",");
        } else {
            filedVal.append("educationlevel=null,");
        }
        if (!ComeDate.equals("")) {
            filedVal.append("companystartdate='" + ComeDate.split("T")[0] + "',");
        } else {
            filedVal.append("companystartdate=null,");
        }
        if (!TryuseDate.equals("")) {
            filedVal.append("probationenddate='" + TryuseDate.split("T")[0] + "',");
        } else {
            filedVal.append("probationenddate=null,");
        }
        //----------------少两个
        if (!startdate.equals("")) {
            filedVal.append("startdate='" + startdate + "',");
        } else {
            filedVal.append("startdate=null,");
        }
        if (!enddate.equals("")) {
            filedVal.append("enddate='" + enddate + "',");
        } else {
            filedVal.append("enddate=null,");
        }
        if (!PerField69.equals("")) {//办公地址
            filedVal.append("locationid=" + getLocationId(PerField69) + ",");
        } else {
            filedVal.append("locationid=null,");
        }
        if (!PartNo.equals("")) {
            if (HrOrgProsonTask.concurrentDepartment.containsKey(PartNo)) {
                filedVal.append("departmentid=" + HrOrgProsonTask.concurrentDepartment.get(PartNo) + ",");
            } else {//没有
                filedVal.append("departmentid=null,");
            }
        } else {
            filedVal.append("departmentid=null,");
        }

        if (!QtName.equals("")) {
            filedVal.append("jobtitle=" + getJobtitle(QtName, PartNo, DtName, quartersNo) + ",");
        } else {
            filedVal.append("jobtitle=null,");
        }
        if (!EmpNo.equals("")) {
            filedVal.append("workcode='" + EmpNo + "',");
        } else {
            filedVal.append("workcode=null,");
        }
        if (!EmpName.equals("")) {
            filedVal.append("lastname='" + EmpName + "',");
        } else {
            filedVal.append("lastname=null,");
        }
        if (!EmpNo.equals("")) {
            filedVal.append("loginid='" + EmpNo + "',");
        } else {
            filedVal.append("loginid=null,");
        }

        if (!Incumbency.equals("")) {
            filedVal.append("status=" + getIncumbency(Incumbency) + ",");
        } else {
            filedVal.append("status=null,");
        }

        if (!PerField74.equals("")) {
            filedVal.append("seclevel=" + PerField74 + ",");
        } else {
            filedVal.append("seclevel=0,");
        }
        if (!FolkName.equals("")) {
            filedVal.append("folk='" + FolkName + "',");
        } else {
            filedVal.append("folk=null,");
        }
        LOGGER.error("Hr系统直接上级更新========================================" + ParentEmpNo);
        if (!ParentEmpNo.equals("")) {
            LOGGER.error("=========直接上级更新===" + ParentEmpNo);
            filedVal.append("managerid=" + HrOrgProsonTask.concurrentSuperior.get(ParentEmpNo) + ",");
        } else {
            filedVal.append("managerid=null,");
        }

        filedVal.append("subcompanyid1=" + this.branchId + ",");
        sql = "update hrmresource set " + filedVal + ""
                + " systemlanguage=7,createdate=FORMAT(GETDATE(),'yyyy-MM-dd'),createrid=1,lastmodid=1,lastmoddate=FORMAT(GETDATE(),'yyyy-MM-dd'),modifier=1,modified=GETDATE()"
                + " where id=" + empId;
        return sql;
    }


    /**
     * 日期取值
     * 合同日期有三对日期：取最新的日期，有内容的一对
     *
     * @param jsonObject
     * @param i
     * @return
     */
    private String getStartDate(JSONObject jsonObject, int index) {
        String PerField35 = jsonObject.getString("PerField35");
        String PerField37 = jsonObject.getString("PerField37");
        String PerField39 = jsonObject.getString("PerField39");
        String PerField36 = jsonObject.getString("PerField36");
        String PerField38 = jsonObject.getString("PerField38");
        String PerField40 = jsonObject.getString("PerField40");
        LOGGER.error("===" + PerField35 + "===" + PerField37 + "===" + PerField39 + "===" + PerField36 + "===" + PerField38 + "===" + PerField40 + "===");
        if (!CustomUtil.getIsNullVal(PerField35).equals("")) {
            if (index == 1) {
                return PerField35.split("T")[0];
            }
            if (index == 2) {
                return !CustomUtil.getIsNullVal(PerField36).equals("") ? PerField36.split("T")[0] : "";
            }
        }
        if (!CustomUtil.getIsNullVal(PerField37).equals("")) {
            if (index == 1) {
                return PerField37.split("T")[0];
            }
            if (index == 2) {
                return !CustomUtil.getIsNullVal(PerField38).equals("") ? PerField38.split("T")[0] : "";
            }
        }
        if (!CustomUtil.getIsNullVal(PerField39).equals("")) {
            if (index == 1) {
                return PerField39.split("T")[0];
            }
            if (index == 2) {
                return !CustomUtil.getIsNullVal(PerField40).equals("") ? PerField40.split("T")[0] : "";
            }
        }
        return "";
    }


    /**
     * @param incumbency
     * @return
     */
    private int getIncumbency(String incumbency) {
        LOGGER.error("hr人员状态=====" + incumbency);
        int status = 0;
        if (incumbency.equals("1")) {
            status = 1;
        }
        if (incumbency.equals("0")) {
            status = 5;
        }
        ;
        return status;
    }


    /**
     * 岗位编码：岗位名称_部门id
     * 岗位名称
     * 部门编码
     * 职务名称
     *
     * @param qtName
     * @return
     */
    private int getJobtitle(String qtName, String PartNo, String DtName, String quartersNo) {
        int qtNameId = 0;
        RecordSet recordSet = new RecordSet();
        if (HrOrgProsonTask.concurrentStation.containsKey((qtName + "_" + HrOrgProsonTask.concurrentDepartment.get(PartNo)))) {
            qtNameId = HrOrgProsonTask.concurrentStation.get(qtName + "_" + HrOrgProsonTask.concurrentDepartment.get(PartNo));
            //更新岗位编码
            if (!quartersNo.equals("")) {
                String sql = "update hrmjobtitles set outkey='" + quartersNo + "' WHERE id=" + qtNameId;
                recordSet.execute(sql);
            }
        } else {
            //int tablesIndex = DBUtil.getTablesIndex("hrmlocations");
            String sql2 = "insert into hrmjobtitles(jobtitlemark,jobtitlename,jobtitleremark,jobtitlecode,"
                    + "jobdepartmentid,jobactivityid,created,creater,outkey) values("

                    + "'" + qtName + "','"
                    + qtName + "','"
                    + qtName + "','"
                    + qtName + "_" + HrOrgProsonTask.concurrentDepartment.get(PartNo) + "',"
                    + (!CustomUtil.getIsNullVal(HrOrgProsonTask.concurrentJobactivities.containsKey(DtName) + "").equals("")
                    ? HrOrgProsonTask.concurrentJobactivities.get(DtName) : null) + ","
                    + HrOrgProsonTask.concurrentDepartment.get(PartNo) + ",GETDATE(),1,'" + quartersNo + "')";

            int tablesIndex = 0;
            try {
                recordSet.execute(sql2);
                recordSet.execute("SELECT  ID FROM hrmjobtitles WHERE jobtitlename='" + qtName + "'");
                if (recordSet.next()) {
                    tablesIndex = recordSet.getInt("ID");
                }
            } catch (Exception e) {
                LOGGER.error("岗位同步异常====" + e + "====|" + sql2);
            }
            HrOrgProsonTask.concurrentStation.put(qtName + "_" + HrOrgProsonTask.concurrentDepartment.get(PartNo), tablesIndex);
            qtNameId = tablesIndex;
        }
        return qtNameId;
    }


    private int getLocationId(String perField69) {
        int locationid = 0;
        if (HrOrgProsonTask.concurrentSite.containsKey(perField69)) {
            locationid = HrOrgProsonTask.concurrentSite.get(perField69);
        } else {//没有
            //int tablesIndex = DBUtil.getTablesIndex("hrmlocations");
            String sql2 = "insert into hrmlocations(locationname,locationdesc,countryid) values("
                    + "'" + perField69 + "','" + perField69 + "',1)";
            RecordSet recordSet = new RecordSet();
            int tablesIndex = 0;
            try {
                recordSet.execute(sql2);
                recordSet.execute("SELECT  ID FROM hrmlocations WHERE locationname='" + perField69 + "'");
                if (recordSet.next()) {
                    tablesIndex = recordSet.getInt("ID");
                }
            } catch (Exception e) {
                LOGGER.error("办公地址异常====" + e + "====|" + sql2);
            }
            HrOrgProsonTask.concurrentSite.put(perField69, tablesIndex);
            locationid = tablesIndex;
        }
        return locationid;
    }


    /**
     * 职称新增
     *
     * @param levelLevel
     * @return
     */
    private int getJobcall(String levelLevel) {
        int levelLevelId = 0;
        if (HrOrgProsonTask.concurrentTechnical.containsKey(levelLevel)) {
            levelLevelId = HrOrgProsonTask.concurrentTechnical.get(levelLevel);
        } else {
            //int tablesIndex = DBUtil.getTablesIndex("hrmjobcall");
            String sql2 = "insert into hrmjobcall(name,description) values("
                    + "'" + levelLevel + "','" + levelLevel + "')";
            LOGGER.error("职称sql" + sql2);
            int tablesIndex = 0;
            try {
                RecordSet recordSet = new RecordSet();
                recordSet.execute(sql2);
                recordSet.execute("SELECT  ID FROM hrmjobcall WHERE name='" + levelLevel + "'");
                if (recordSet.next()) {
                    tablesIndex = recordSet.getInt("ID");
                }
            } catch (Exception e) {
                LOGGER.error("职称同步异常====" + e + "====|" + sql2);
            }
            HrOrgProsonTask.concurrentTechnical.put(levelLevel, tablesIndex);
            levelLevelId = tablesIndex;
        }
        return levelLevelId;
    }


    /**
     * 学历新增
     *
     * @param educationName
     * @return
     */
    private int getDeucationLevel(String educationName) {
        int educationNameId = 0;
        if (HrOrgProsonTask.concurrentEducation.containsKey(educationName)) {
            educationNameId = HrOrgProsonTask.concurrentEducation.get(educationName);
        } else {
            //int tablesIndex = DBUtil.getTablesIndex("hrmeducationlevel");
            String sql2 = "insert into hrmeducationlevel(name,description) values("
                    + "'" + educationName
                    + "','" + educationName + "')";
            LOGGER.error("学历级别sql2" + sql2);
            RecordSet recordSet = new RecordSet();
            recordSet.execute(sql2);
            int tablesIndex = 0;
            recordSet.execute("SELECT  ID FROM hrmeducationlevel WHERE name='" + educationName + "'");
            if (recordSet.next()) {
                tablesIndex = recordSet.getInt("ID");
            }
            HrOrgProsonTask.concurrentEducation.put(educationName, tablesIndex);
            educationNameId = tablesIndex;
        }
        return educationNameId;
    }


    /**
     * 男，女
     *
     * @param sex
     * @return
     */
    private String getSex(String sex) {
        if (sex.equals("01")) {
            return "0";
        }
        if (sex.equals("02")) {
            return "1";
        }
        return "0";
    }


    /**
     * 职务类别---职务同步
     */
    private void syncJobCategory() {
        numSum = "";//合计
        isSyncNum = 0;//已同步
        isExceNum = 0;//异常
        isNewNum = 0;
        JSONObject jsonObject = new JSONObject();
        JSONObject sendPackaging = HttpUtils.sendPackaging(ConstantUtil.dongbaoRs014001, ConstantUtil.dongbaoLanguage, jsonObject, "1=1");
        String jobCategoryData = HttpUtils.postUrlDataResultByPost(ConstantUtil.dongsearchbaoUrl, sendPackaging.toString());
        LOGGER.error("职务类别---响应成功===========================================" + jobCategoryData);
        if (null == jobCategoryData) {
            LOGGER.error("职务类别---职务同步异常=============================" + jobCategoryData.toString());
            return;
        }
        JSONObject jobCategoryDataJson = JSONObject.parseObject(jobCategoryData);
        if (!jobCategoryDataJson.getBoolean("Success")) {
            LOGGER.error("职务类别---职务同步异常=============================" + jobCategoryDataJson.toString());
            return;
        }
        JSONArray jobCategoryDataJsonArray = jobCategoryDataJson.getJSONArray("Data");
        numSum = jobCategoryDataJsonArray.size() + "";
        LOGGER.error("职务类别数量" + jobCategoryDataJsonArray.size());
        for (int i = 0; i < jobCategoryDataJsonArray.size(); i++) {
            JSONObject jobCategoryDataJsonArrayObj = jobCategoryDataJsonArray.getJSONObject(i);
            if (CustomUtil.getIsNullVal(jobCategoryDataJsonArrayObj.getString("DutyTypeName")).equals("")) {
                isExceNum++;
                LOGGER.error("职务类别---职务同步异常key=============================" + jobCategoryDataJsonArrayObj.toString());
                continue;
            }
            if (HrOrgProsonTask.concurrentGroups.containsKey(jobCategoryDataJsonArrayObj.getString("DutyTypeName"))) {
                isSyncNum++;
                if (HrOrgProsonTask.concurrentJobactivities.containsKey(jobCategoryDataJsonArrayObj.getString("DtName"))) {

                    continue;
                } else {
                    isNewNum++;
                    insertJobactivities(jobCategoryDataJsonArrayObj.getString("DtName"), HrOrgProsonTask.concurrentGroups.get(jobCategoryDataJsonArrayObj.getString("DutyTypeName")));
                }
            } else {
                insertGroupsType(jobCategoryDataJsonArrayObj.getString("DutyTypeName"));
                isNewNum++;
                isSyncNum++;
                if (HrOrgProsonTask.concurrentJobactivities.containsKey(jobCategoryDataJsonArrayObj.getString("DtName"))) {

                    continue;
                } else {
                    isNewNum++;
                    insertJobactivities(jobCategoryDataJsonArrayObj.getString("DtName"), HrOrgProsonTask.concurrentGroups.get(jobCategoryDataJsonArrayObj.getString("DutyTypeName")));
                }
            }
        }
        strCentent += "职务和职务类别：Ecology同步职务和职务类别共(" + numSum + "条数据;已同步" + isSyncNum + "条,职务和职务类别新增" + isNewNum + "条,异常" + isExceNum + "条;</br>";
    }


    /**
     * 职务类型插入
     *
     * @param string
     */
    private void insertGroupsType(String typeName) {
        RecordSet recordSet = new RecordSet();
        //int tablesIndex = DBUtil.getTablesIndex("hrmjobgroups");
        String sql = "insert into hrmjobgroups(jobgroupname,jobgroupremark,created,creater) values('" + typeName + "','" + typeName + "', GETDATE(),1)";
        boolean execute = recordSet.execute(sql);
        if (!execute) {
            LOGGER.error("职务类型新增异常=================================" + sql);
        }
        int tablesIndex = 0;
        recordSet.execute("SELECT  ID FROM hrmjobgroups WHERE jobgroupname='" + typeName + "'");
        if (recordSet.next()) {
            tablesIndex = recordSet.getInt("ID");
        }
        HrOrgProsonTask.concurrentGroups.put(typeName, tablesIndex);
    }

    /**
     * 职务名称新增
     *
     * @param jobCategoryDataJsonArrayObj
     * @param integer
     */
    private void insertJobactivities(String DutyName, Integer typeid) {
        RecordSet recordSet = new RecordSet();
        //int tablesIndex = DBUtil.getTablesIndex("hrmjobactivities");
        String sql = "insert into hrmjobactivities(jobactivityname,jobactivitymark,jobgroupid,created,creater) values('" + DutyName + "','" + DutyName + "'," + typeid + ", GETDATE(),1)";
        boolean execute = recordSet.execute(sql);
        if (!execute) {
            LOGGER.error("职务类型新增异常=================================" + sql);
        }
        int tablesIndex = 0;
        recordSet.execute("SELECT  ID FROM hrmjobactivities WHERE jobactivityname='" + DutyName + "'");
        if (recordSet.next()) {
            tablesIndex = recordSet.getInt("ID");
        }
        HrOrgProsonTask.concurrentJobactivities.put(DutyName, tablesIndex);
    }


    /**
     * 缓存同步
     */
    @SuppressWarnings("static-access")
    private void syncCache() {
        HrOrgProsonTask hrOrgProsonTask = new HrOrgProsonTask();
        hrOrgProsonTask.execute();
    }

    /**
     * 总公司|分部同步
     */
    @SuppressWarnings({"static-access", "unused"})
    private void syncHrmcompany() {
        if (HrOrgProsonTask.concurrentCompany.containsKey(this.companyName)) {
            //获取到了公司名称为甘露集团 id=1
            companynameId = HrOrgProsonTask.concurrentCompany.get(this.companyName);
        } else {
            //没有就新增
            Integer maxid = insertCompany("hrmcompany", SynchronizationTask.companyName);
            HrOrgProsonTask.concurrentCompany.put(this.companyName, maxid);
        }
        if (HrOrgProsonTask.concurrentBranch.containsKey(this.companyName)) {
            branchId = HrOrgProsonTask.concurrentBranch.get(this.companyName);
        } else {
            Integer maxid = insertBranch("hrmsubcompany", SynchronizationTask.companyName, HrOrgProsonTask.concurrentCompany.get(this.companyName));
            HrOrgProsonTask.concurrentBranch.put(this.companyName, maxid);
        }
    }

    //分部插入
    private Integer insertBranch(String table, String companyName2, Integer companyId) {
        RecordSet recordSet = new RecordSet();
        int maxId = 0;
        String sql = "INSERT INTO " + table + "(subcompanyname,companyid) VALUES('" + companyName2 + "'," + companyId + ")";
        recordSet.execute(sql);
        recordSet.execute("select  ID from hrmsubcompany where subcompanyname='" + companyName2 + "'");
        if (recordSet.next()) {
            maxId = recordSet.getInt("ID");
        }
        return maxId;
    }

    //公司
    private Integer insertCompany(String table, String companyName2) {
        RecordSet recordSet = new RecordSet();
        int maxId = DBUtil.getTablesIndex(table);
        String sql = "INSERT INTO " + table + "(ID,companyname) VALUES(" + maxId + ",'" + companyName2 + "')";
        recordSet.execute(sql);
//		recordSet.execute("select  ID from hrmcompany where companyname='"+companyName2+"'");
//		if(recordSet.next()) {
//			maxId=recordSet.getInt("ID");
//		}
        return maxId;
    }

    /**
     * 部门信息同步
     */
    @SuppressWarnings({"unlikely-arg-type", "static-access"})
    private void syncDept(String postUrlDataResultByPost) {
        if (postUrlDataResultByPost == null || postUrlDataResultByPost.equals("")) {
            LOGGER.error("部门数据同步异常=============================" + postUrlDataResultByPost);
        }
        JSONObject parseObject = JSONObject.parseObject(postUrlDataResultByPost);
        Boolean boolean1 = parseObject.getBoolean("Success");
        if (boolean1) {
            numSum = parseObject.getString("Message");
            LOGGER.error("部门数据同步3=============================数量" + parseObject.getString("Message"));
            JSONArray jsonArray = parseObject.getJSONArray("Data");
            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                if (jsonObject2.getString("PartNo") == null) {
                    LOGGER.error("部门数据同步关系字段异常key=============================" + jsonObject2.toString());
                    continue;
                }
                if (HrOrgProsonTask.concurrentDepartment.containsKey(jsonObject2.getString("PartNo"))) {
                    isSyncNum++;
                    arrayList.add(updateDepartment(jsonObject2, HrOrgProsonTask.concurrentDepartment.get(jsonObject2.getString("PartNo"))));
                } else {
                    isNewNum++;
                    insertDepartment(jsonObject2);
                }
            }
            if (arrayList.size() > 0) {
                RecordSetTrans recordSetTrans = new RecordSetTrans();
                SubmitLeaveData(arrayList, recordSetTrans);
            }

        } else {
            LOGGER.error("部门数据同步异常2=============================" + parseObject.getString("Message"));
        }
        strCentent = "将组织架构数据进行有效监控：<br/>";
        strCentent += "部门：Ecology同步部门共(" + numSum + "条数据;已同步" + isSyncNum + "条,新增" + isNewNum + "条,异常" + isExceNum + "条;</br>";
    }

    /**
     * 批量提交sql
     *
     * @param arrayList
     * @param recordSetTrans
     */
    private static void SubmitLeaveData(List<String> arrayList, RecordSetTrans recordSetTrans) {
        LOGGER.error("需要执行的sql脚本======================================" + arrayList.toString());
        try {
            for (String strSql : arrayList) {
                recordSetTrans.execute(strSql);
            }
            boolean commit = recordSetTrans.commit();
            LOGGER.error("sql执行完成======================================执行状态==" + commit);
        } catch (Exception e) {
            recordSetTrans.rollback();
            LOGGER.error("sql执行异常回滚======================================" + e);
            isExceNum = arrayList.size();
            isSyncNum = isSyncNum - arrayList.size();
            e.printStackTrace();
        }
    }

    /**
     * 部门新增 |初次新增不做矩阵字段，人员新增后在同步
     *
     * @param jsonObject2
     * @param integer
     */
    @SuppressWarnings("unlikely-arg-type")
    private void insertDepartment(JSONObject jsonObject2) {
        //上级部门id查询
//		if(!HrOrgProsonTask.concurrentDepartment.containsKey(jsonObject2.getIntValue("FatherID"))) {
//			HrOrgProsonTask.concurrentDepartment
//		}
        //int idIndex = DBUtil.getTablesIndex("HrmDepartment");
        String insql = "INSERT INTO HrmDepartment  " +
                "        (departmentmark , " +
                "          departmentname , " +
                "          subcompanyid1 , " +
                "          supdepid , " +
                "          canceled , " +
                "          departmentcode , " +
                "          created , " +
                "          creater  " +
                "        ) " +
                "VALUES  ( '" +
                CustomUtil.getIsNullVal(jsonObject2.getString("PartName")) + "' , " +
                "          '" + CustomUtil.getIsNullVal(jsonObject2.getString("PartName")) + "', " +
                "          " + this.branchId + " , " +
                "           " + (HrOrgProsonTask.concurrentDepartment.containsKey(jsonObject2.getString("FatherID"))
                ? HrOrgProsonTask.concurrentDepartment.get(jsonObject2.getString("FatherID")) : null) + ", " +
                "          '" + CustomUtil.getIsNullVal(jsonObject2.getInteger("IsCancel") + "") + "' ," +
                "          '" + CustomUtil.getIsNullVal(jsonObject2.getString("PartNo")) + "' , " +
                "		  GETDATE() ," +
                "          1  " +
                "        )";
        RecordSet recordSet = new RecordSet();
        boolean execute = recordSet.execute(insql);
        if (!execute) {
            isExceNum++;
            LOGGER.error("部门插入异常" + jsonObject2 + "===sql" + insql);
            return;
        }
        recordSet.execute("SELECT ID FROM HrmDepartment WHERE departmentcode='" + CustomUtil.getIsNullVal(jsonObject2.getString("PartNo")) + "'");
        int idIndex = 0;
        if (recordSet.next()) {
            idIndex = recordSet.getInt("ID");
        }
        //部门自定义字段表维护
        recordSet.execute("insert into HrmDepartmentDefined(deptid) values(" + idIndex + ")");
        //维护缓存
        HrOrgProsonTask.concurrentDepartment.put(CustomUtil.getIsNullVal(jsonObject2.getString("PartNo")), idIndex);
    }

    /**
     * 部门修改
     *
     * @param jsonObject2
     */
    private String updateDepartment(JSONObject jsonObject2, Integer id) {
        String FieldValue = "";
        String PartName = CustomUtil.getIsNullVal(jsonObject2.getString("PartName"));
        String Remark = CustomUtil.getIsNullVal(jsonObject2.getString("PartName"));
        Integer FatherID = HrOrgProsonTask.concurrentDepartment.containsKey(jsonObject2.getString("FatherID"))
                ? HrOrgProsonTask.concurrentDepartment.get(jsonObject2.getString("FatherID")) : null;

        String IsCancel = CustomUtil.getIsNullVal(jsonObject2.getInteger("IsCancel") + "");
        if (!PartName.equals("")) {
            FieldValue += "departmentname='" + PartName + "',";
        } else {
            FieldValue += "departmentname=null,";
        }
        if (!Remark.equals("")) {
            FieldValue += "departmentmark='" + Remark + "',";
        } else {
            FieldValue += "departmentmark=null,";
        }
        FieldValue += "subcompanyid1=" + this.branchId + ",";
        if (null != FatherID) {
            FieldValue += "supdepid=" + FatherID + ",";
        } else {
            FieldValue += "supdepid=null,";
        }
        if (!IsCancel.equals("")) {
            FieldValue += "canceled='" + IsCancel + "',";
        } else {
            FieldValue += "canceled=null,";
        }


        String upsql = "update hrmdepartment set " + FieldValue
                + "departmentcode='" + jsonObject2.getString("PartNo") + "',"
                + "modified=GETDATE(),modifier=1 where id=" + id;
        return upsql;
    }

    private void statusRemind() throws Exception {
        String title = "Ecology组织架构集成提醒";
        int submiter = 1; // 发起人（暂时设为管理员）
        // SAP员工编号----Ecology员工工号
//			SysRemindWorkflow srw = new SysRemindWorkflow();
//			String mString =  strCentent;
//			srw.setPrjSysRemind(title, 0, submiter, "1", mString);
        sendFlowEmail(submiter, "1", strCentent);
        return;
    }

    private void sendFlowEmail(int submiter, String userId, String strCentent2) {
        String reiquetId = null;
        RequestService requestService = new RequestService();
        RequestInfo createTableFlow = createTableFlow(submiter, userId, strCentent2);
        try {
            reiquetId = requestService.createRequest(createTableFlow);
            String message = getMessage(Integer.parseInt(reiquetId));
            if (Integer.parseInt(reiquetId) > 0) {// 0:已推送
                LOGGER.error("监控提醒流程===============" + reiquetId);
            } else {
                LOGGER.error("流程创建失败===" + message + "====" + reiquetId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("流程创建异常===" + e + "====" + reiquetId);
        }
    }

    private RequestInfo getRequestInfo() {
        if (null == requestInfo) {
            requestInfo = new RequestInfo();
        }
        return requestInfo;
    }

    private RequestInfo createTableFlow(int submiter, String userId, String strCentent2) {
        getRequestInfo().setRequestid("");
        getRequestInfo().setCreatorid(submiter + "");
        getRequestInfo().setWorkflowid(getActiveVersionId());
        getRequestInfo().setDescription(getDescription());
        getRequestInfo().setRequestlevel(getRequestLevel());
        getRequestInfo().setRemindtype(getRemindType());
        getRequestInfo().setIsNextFlow(getIsNextFlow());
        getRequestInfo().setMainTableInfo(getMainTableInfo(userId, strCentent2));
        getRequestInfo().setDetailTableInfo(getDetailTableInfo());
        return getRequestInfo();
    }


    private String getActiveVersionId() {
        return "166";
    }

    private String getDescription() {
        return "Ecology与东宝HR集成监控" + DateUtil.date2String(new Date());
    }

    private String getRequestLevel() {
        return "0";
    }

    private String getRemindType() {
        return "系统监控发起";
    }

    private String getIsNextFlow() {
        return "0";
    }

    private MainTableInfo getMainTableInfo(String userId, String strCentent2) {
        MainTableInfo mainTable = new MainTableInfo();
        Property[] mainFields = new Property[]{
                RequestInfoService.generateProperty("jsr", userId, ""),
                RequestInfoService.generateProperty("tx", strCentent2, "")
        };
        mainTable.setProperty(mainFields);
        return mainTable;
    }

    private DetailTableInfo getDetailTableInfo() {
        DetailTableInfo detailTableInfo = new DetailTableInfo();
        return detailTableInfo;
    }

    private String getMessage(Integer requestid) {
        String message = "";
        switch (requestid) {
            case 0:
                message = "流程创建失败";
                break;
            case -1:
                message = "创建流程失败";
                break;
            case -2:
                message = "用户没有流程创建权限";
                break;
            case -3:
                message = "创建流程基本信息失败";
                break;
            case -4:
                message = "保存表单主表信息失败";
                break;
            case -5:
                message = "更新紧急程度失败";
                break;
            case -6:
                message = "流程操作者失败";
                break;
            case -7:
                message = "流转至下一节点失败";
                break;
            case -8:
                message = "节点附加操作失败";
                break;
            default:
                if (requestid > 0) {

                    message = "流程创建成功";
                } else {
                    message = "流程创建失败";
                }
                break;
        }
        return message;
    }

}
