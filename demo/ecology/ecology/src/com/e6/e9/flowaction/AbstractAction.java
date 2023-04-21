/**
* @Title: AbstractAction
* @Description: 
* @author: HM
* @date 2022年4月19日 下午4:22:33
*/
package com.e6.e9.flowaction;

import org.apache.log4j.Logger;

import com.e6.common.utils.CustomUtil;
import com.e6.e9.commons.RecordUtil;
import com.e6.e9.entities.WorkflowRequestBase;

import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;

/**
 * 描述:流程工具抽象接口封装
 * 
 * @author HM
 * @date 2022年4月19日
 *
 */
public abstract class AbstractAction implements Action {
	private Logger log = Logger.getLogger(this.getClass());

	private Integer requestId;
	private RequestInfo requestInfo;
	private String requestCode; // 流程编号
	private String requestName; // 请求标题
	private String mainTableName; // 主表表名
	private WorkflowRequestBase requestBase; // 请求基本信息

	public String execute(RequestInfo requestInfo) {
		this.requestId = requestInfo.getRequestManager().getRequestid();
		log.info(this.getClass().getName() + " Start, requestId = " + getRequestId());
		try {
			WorkflowRequestBase requestBase = (WorkflowRequestBase) RecordUtil.findById(WorkflowRequestBase.class,
					this.requestId);
			this.requestBase = requestBase;
			this.requestCode = requestBase.getRequestMark();
			this.requestName = requestBase.getRequestName();
			this.requestInfo = requestInfo;
			this.mainTableName = "FORMTABLE_MAIN_" + Math.abs(requestInfo.getRequestManager().getFormid());
			execute();
			return Action.SUCCESS;
		} catch (Exception e) {
			log.info("流程提交异常======================" +e+"=="+e.getMessage()+"===="+e.getLocalizedMessage());
		} finally {
			log.info(this.getClass().getName() + " End, requestId = " + getRequestId());
		}
		return Action.FAILURE_AND_CONTINUE;
	}

	public abstract void execute() throws Exception;

	
	/**
	 * 响应异常信息
	 * <p>
	 * @Title:setRequest
	 * </p>
	 * <p>
	 * @Description:
	 * </p>
	 * 
	 * @param msg
	 * @param ep
	 * @param requestInfo
	 */
	public static void setRequest(String msg, Object ep, RequestInfo requestInfo) {
		requestInfo.getRequestManager().setMessageid("111100");
		requestInfo.getRequestManager().setMessage(msg);
		requestInfo.getRequestManager().setMessagecontent(
				"<textarea readonly=readonly style='margin: 0px; width: 1259px; height: 100px;'>" + ep + "</textarea>");
	}
	
	/*********************************
	 * 主表信息
	 *************************************************/
	/**
	 * 获取主表数据，并转换成整型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected Integer getMainIntegerValue(String fieldName, Integer defValue) {
		Property[] fields = getRequestInfo().getMainTableInfo().getProperty();
		Integer value = null;
		for (Property field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = CustomUtil.getInteger(field.getValue());
				break;
			}
		}
		return value == null ? defValue : value;
	}

	protected Integer getMainIntegerValue(String fieldName) {
		return getMainIntegerValue(fieldName, null);
	}

	/**
	 * 获取主表数据，并转换成整型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected String getMainStringValue(String fieldName, String defValue) {
		Property[] fields = getRequestInfo().getMainTableInfo().getProperty();
		String value = null;
		for (Property field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = field.getValue();
				break;
			}
		}
		return value == null ? defValue : value;
	}

	/**
	 * 获取主表格数据，并转换成Double类型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected Double getMainDoubleValue(String fieldName, Double defValue) {
		Property[] fields = getRequestInfo().getMainTableInfo().getProperty();
		Double value = null;
		for (Property field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = CustomUtil.getDouble(field.getValue());
				break;
			}
		}
		return value == null ? defValue : value;
	}

	/*********************************
	 * 明细信息
	 *************************************************/
	/**
	 * 获取明细表所有行。
	 * 
	 * @param tableIndex
	 * @return
	 */
	protected Row[] getRows(int tableIndex) {
		return getDetailTable(tableIndex).getRow();
	}

	/**
	 * 获取明细表。
	 * 
	 * @param tableIndex
	 * @return
	 */
	protected DetailTable getDetailTable(int tableIndex) {
		return getRequestInfo().getDetailTableInfo().getDetailTable(tableIndex);
	}

	/**
	 * 获取单元格数据，并转换成Double类型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected Double getCellDoubleValue(Row row, String fieldName) {
		return getCellDoubleValue(row, fieldName, null);
	}

	/**
	 * 获取单元格数据，并转换成Double类型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected String getCellStringValue(Row row, String fieldName, String defValue) {
		Cell[] fields = row.getCell();
		String value = null;
		for (Cell field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = field.getValue();
				break;
			}
		}
		return value == null ? defValue : value;
	}

	/**
	 * 获取单元格数据，并转换成Double类型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected Double getCellDoubleValue(Row row, String fieldName, Double defValue) {
		Cell[] fields = row.getCell();
		Double value = null;
		for (Cell field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = CustomUtil.getDouble(field.getValue());
				break;
			}
		}
		return value == null ? defValue : value;
	}

	/**
	 * 获取单元格数据，并转换成整型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected Integer getCellIntegerValue(Row row, String fieldName) {
		return getCellIntegerValue(row, fieldName, null);
	}

	/**
	 * 获取单元格数据，并转换成整型值返回。
	 * 
	 * @param row
	 * @param fieldName
	 * @param defValue
	 * @return
	 */
	protected Integer getCellIntegerValue(Row row, String fieldName, Integer defValue) {
		Cell[] fields = row.getCell();
		Integer value = null;
		for (Cell field : fields) {
			if (fieldName.equalsIgnoreCase(field.getName())) {
				value = CustomUtil.getInteger(field.getValue());
				break;
			}
		}
		return value == null ? defValue : value;
	}

	/**********************************
	 * get/set
	 **************************************************/
	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public String getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public String getMainTableName() {
		return mainTableName;
	}

	public void setMainTableName(String mainTableName) {
		this.mainTableName = mainTableName;
	}

	public WorkflowRequestBase getRequestBase() {
		return requestBase;
	}

	public void setRequestBase(WorkflowRequestBase requestBase) {
		this.requestBase = requestBase;
	}



}
