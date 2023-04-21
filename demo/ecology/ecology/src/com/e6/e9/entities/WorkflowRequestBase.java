/**
* @Title: WorkflowRequestBase
* @Description: 
* @author: HM
* @date 2022年4月19日 下午4:26:56
*/
package com.e6.e9.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述:流程请求相关字段信息
 * 
 * @author HM
 * @date 2022年4月19日
 *
 */
@Table(name = "WORKFLOW_REQUESTBASE", catalog = "ecology")
public class WorkflowRequestBase {
	@Id
	private Integer requestId;
	private Integer workflowId;
	private Integer lastnodeId;
	private String lastNodeType;
	private Integer currentNodeId;
	private String currentNodeType;
	private String status;
	private Integer passedGroups;
	private Integer totalGroups;
	private String requestName;
	private Integer creater;
	private String createDate;
	private String createTime;
	private Integer lastOperator;
	private String lastOperateDate;
	private String lastOperateTime;
	private Integer deleted;
	private Integer createrType;
	private Integer lastOperatorType;
	private Double nodePassTime;
	private Double nodeLeftTime;
	private String docIds;
	private String crmIds;
	private String hrmIds;
	private String prjIds;
	private String cptIds;
	private Integer requestLevel;
	private String requestMark;
	private Integer messageType;
	private Integer mainRequestId;
	private Integer currentStatus;
	private String lastStatus;
	private Integer isMultiPrint;
	private Integer chatsType;

	@Column(name = "ecology_pinyin_search")
	private String ecologyPinyinSearch;
	private String requestNameNew;
	private String formSignatureMd5;
	private String dataAggregated;

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}

	public Integer getLastnodeId() {
		return lastnodeId;
	}

	public void setLastnodeId(Integer lastnodeId) {
		this.lastnodeId = lastnodeId;
	}

	public String getLastNodeType() {
		return lastNodeType;
	}

	public void setLastNodeType(String lastNodeType) {
		this.lastNodeType = lastNodeType;
	}

	public Integer getCurrentNodeId() {
		return currentNodeId;
	}

	public void setCurrentNodeId(Integer currentNodeId) {
		this.currentNodeId = currentNodeId;
	}

	public String getCurrentNodeType() {
		return currentNodeType;
	}

	public void setCurrentNodeType(String currentNodeType) {
		this.currentNodeType = currentNodeType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPassedGroups() {
		return passedGroups;
	}

	public void setPassedGroups(Integer passedGroups) {
		this.passedGroups = passedGroups;
	}

	public Integer getTotalGroups() {
		return totalGroups;
	}

	public void setTotalGroups(Integer totalGroups) {
		this.totalGroups = totalGroups;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public Integer getCreater() {
		return creater;
	}

	public void setCreater(Integer creater) {
		this.creater = creater;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getLastOperator() {
		return lastOperator;
	}

	public void setLastOperator(Integer lastOperator) {
		this.lastOperator = lastOperator;
	}

	public String getLastOperateDate() {
		return lastOperateDate;
	}

	public void setLastOperateDate(String lastOperateDate) {
		this.lastOperateDate = lastOperateDate;
	}

	public String getLastOperateTime() {
		return lastOperateTime;
	}

	public void setLastOperateTime(String lastOperateTime) {
		this.lastOperateTime = lastOperateTime;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public Integer getCreaterType() {
		return createrType;
	}

	public void setCreaterType(Integer createrType) {
		this.createrType = createrType;
	}

	public Integer getLastOperatorType() {
		return lastOperatorType;
	}

	public void setLastOperatorType(Integer lastOperatorType) {
		this.lastOperatorType = lastOperatorType;
	}

	public Double getNodePassTime() {
		return nodePassTime;
	}

	public void setNodePassTime(Double nodePassTime) {
		this.nodePassTime = nodePassTime;
	}

	public Double getNodeLeftTime() {
		return nodeLeftTime;
	}

	public void setNodeLeftTime(Double nodeLeftTime) {
		this.nodeLeftTime = nodeLeftTime;
	}

	public String getDocIds() {
		return docIds;
	}

	public void setDocIds(String docIds) {
		this.docIds = docIds;
	}

	public String getCrmIds() {
		return crmIds;
	}

	public void setCrmIds(String crmIds) {
		this.crmIds = crmIds;
	}

	public String getHrmIds() {
		return hrmIds;
	}

	public void setHrmIds(String hrmIds) {
		this.hrmIds = hrmIds;
	}

	public String getPrjIds() {
		return prjIds;
	}

	public void setPrjIds(String prjIds) {
		this.prjIds = prjIds;
	}

	public String getCptIds() {
		return cptIds;
	}

	public void setCptIds(String cptIds) {
		this.cptIds = cptIds;
	}

	public Integer getRequestLevel() {
		return requestLevel;
	}

	public void setRequestLevel(Integer requestLevel) {
		this.requestLevel = requestLevel;
	}

	public String getRequestMark() {
		return requestMark;
	}

	public void setRequestMark(String requestMark) {
		this.requestMark = requestMark;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Integer getMainRequestId() {
		return mainRequestId;
	}

	public void setMainRequestId(Integer mainRequestId) {
		this.mainRequestId = mainRequestId;
	}

	public Integer getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(Integer currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public Integer getIsMultiPrint() {
		return isMultiPrint;
	}

	public void setIsMultiPrint(Integer isMultiPrint) {
		this.isMultiPrint = isMultiPrint;
	}

	public Integer getChatsType() {
		return chatsType;
	}

	public void setChatsType(Integer chatsType) {
		this.chatsType = chatsType;
	}

	public String getEcologyPinyinSearch() {
		return ecologyPinyinSearch;
	}

	public void setEcologyPinyinSearch(String ecologyPinyinSearch) {
		this.ecologyPinyinSearch = ecologyPinyinSearch;
	}

	public String getRequestNameNew() {
		return requestNameNew;
	}

	public void setRequestNameNew(String requestNameNew) {
		this.requestNameNew = requestNameNew;
	}

	public String getFormSignatureMd5() {
		return formSignatureMd5;
	}

	public void setFormSignatureMd5(String formSignatureMd5) {
		this.formSignatureMd5 = formSignatureMd5;
	}

	public String getDataAggregated() {
		return dataAggregated;
	}

	public void setDataAggregated(String dataAggregated) {
		this.dataAggregated = dataAggregated;
	}

	@Override
	public String toString() {
		return "WorkflowRequestBase [requestId=" + requestId + ", workflowId=" + workflowId + ", lastnodeId="
				+ lastnodeId + ", lastNodeType=" + lastNodeType + ", currentNodeId=" + currentNodeId
				+ ", currentNodeType=" + currentNodeType + ", status=" + status + ", passedGroups=" + passedGroups
				+ ", totalGroups=" + totalGroups + ", requestName=" + requestName + ", creater=" + creater
				+ ", createDate=" + createDate + ", createTime=" + createTime + ", lastOperator=" + lastOperator
				+ ", lastOperateDate=" + lastOperateDate + ", lastOperateTime=" + lastOperateTime + ", deleted="
				+ deleted + ", createrType=" + createrType + ", lastOperatorType=" + lastOperatorType
				+ ", nodePassTime=" + nodePassTime + ", nodeLeftTime=" + nodeLeftTime + ", docIds=" + docIds
				+ ", crmIds=" + crmIds + ", hrmIds=" + hrmIds + ", prjIds=" + prjIds + ", cptIds=" + cptIds
				+ ", requestLevel=" + requestLevel + ", requestMark=" + requestMark + ", messageType=" + messageType
				+ ", mainRequestId=" + mainRequestId + ", currentStatus=" + currentStatus + ", lastStatus=" + lastStatus
				+ ", isMultiPrint=" + isMultiPrint + ", chatsType=" + chatsType + ", ecologyPinyinSearch="
				+ ecologyPinyinSearch + ", requestNameNew=" + requestNameNew + ", formSignatureMd5=" + formSignatureMd5
				+ ", dataAggregated=" + dataAggregated + "]";
	}

}
