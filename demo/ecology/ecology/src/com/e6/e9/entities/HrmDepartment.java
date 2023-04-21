package com.e6.e9.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * 描述:部门表
 * 
 * @author HM
 * @date 2022年5月3日
 *
 */
@Table(name = "HRMDEPARTMENT", catalog = "ecology")
public class HrmDepartment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String departmentName;
	private String departmentMark;
	private String departmentCode;
	@Column(name = "SUBCOMPANYID1")
	private Integer subcompanyId;
	private Integer supDepId;
	private String allSupDepId;
	private String showOrder;
	private String canceled;
	private String outKey;
	@Column(name = "TLEVEL")
	private Integer level;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentMark() {
		return departmentMark;
	}

	public void setDepartmentMark(String departmentMark) {
		this.departmentMark = departmentMark;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public Integer getSubcompanyId() {
		return subcompanyId;
	}

	public void setSubcompanyId(Integer subcompanyId) {
		this.subcompanyId = subcompanyId;
	}

	public Integer getSupDepId() {
		return supDepId;
	}

	public void setSupDepId(Integer supDepId) {
		this.supDepId = supDepId;
	}

	public String getAllSupDepId() {
		return allSupDepId;
	}

	public void setAllSupDepId(String allSupDepId) {
		this.allSupDepId = allSupDepId;
	}

	public String getShowOrder() {
		return showOrder;
	}

	public void setShowOrder(String showOrder) {
		this.showOrder = showOrder;
	}

	public String getCanceled() {
		return canceled;
	}

	public void setCanceled(String canceled) {
		this.canceled = canceled;
	}

	public String getOutKey() {
		return outKey;
	}

	public void setOutKey(String outKey) {
		this.outKey = outKey;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}