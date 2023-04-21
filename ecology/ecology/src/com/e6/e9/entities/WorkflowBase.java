package com.e6.e9.entities;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "WORKFLOW_BASE", catalog = "ecology")
public class WorkflowBase {

	@Id
	private Integer id;
	private String workflowName;
	private Integer workflowType;
	private Integer formId;
	private Integer activeVersionId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Integer getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(Integer workflowType) {
		this.workflowType = workflowType;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Integer getActiveVersionId() {
		return activeVersionId;
	}

	public void setActiveVersionId(Integer activeVersionId) {
		this.activeVersionId = activeVersionId;
	}
}
