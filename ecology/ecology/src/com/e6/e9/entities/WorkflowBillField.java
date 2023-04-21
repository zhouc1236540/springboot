package com.e6.e9.entities;

import javax.persistence.Table;

@Table(name = "WORKFLOW_BILLFIELD", catalog = "ecology")
public class WorkflowBillField {

	private Integer id;
	private Integer billId;
	private String fieldName;
	private String fieldDbType;
	private String detailTable;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBillId() {
		return billId;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDbType() {
		return fieldDbType;
	}

	public void setFieldDbType(String fieldDbType) {
		this.fieldDbType = fieldDbType;
	}

	public String getDetailTable() {
		return detailTable;
	}

	public void setDetailTable(String detailTable) {
		this.detailTable = detailTable;
	}
}