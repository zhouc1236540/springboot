package com.e6.e9.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 人力资源表。
 * 
 * @author 明
 */
@Table(name = "HRMRESOURCE", catalog = "ecology")
public class HrmResource {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;//
	private String loginid;//
	private String password;//
	private String lastname;//
	private String sex;//
	private String birthday;//
	private Integer nationality;//
	private Integer systemlanguage;//
	private String maritalstatus;//
	private String telephone;//
	private String mobile;//
	private String mobilecall;//
	private String email;//
	private Integer locationid;//
	private String workroom;//
	private String homeaddress;//
	private String resourcetype;//
	private String startdate;//
	private String enddate;//
	private Integer jobtitle;//
	private String jobactivitydesc;//
	private Integer joblevel;//
	private Integer seclevel;//
	private Integer departmentid;//
	private Integer subcompanyid1;//
	private Integer costcenterid;//
	private Integer managerid;//
	private Integer assistantid;//
	private Integer bankid1;//
	private String accountid1;//
	private Integer resourceimageid;//
	private Integer createrid;//
	private String createdate;//
	private Integer lastmodid;//
	private String lastmoddate;//
	private String lastlogindate;//
	private String certificatenum;//
	private String nativeplace;//
	private String educationlevel;//
	private String bememberdate;//
	private String bepartydate;//
	private String workcode;//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public Integer getNationality() {
		return nationality;
	}

	public void setNationality(Integer nationality) {
		this.nationality = nationality;
	}

	public Integer getSystemlanguage() {
		return systemlanguage;
	}

	public void setSystemlanguage(Integer systemlanguage) {
		this.systemlanguage = systemlanguage;
	}

	public String getMaritalstatus() {
		return maritalstatus;
	}

	public void setMaritalstatus(String maritalstatus) {
		this.maritalstatus = maritalstatus;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobilecall() {
		return mobilecall;
	}

	public void setMobilecall(String mobilecall) {
		this.mobilecall = mobilecall;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getLocationid() {
		return locationid;
	}

	public void setLocationid(Integer locationid) {
		this.locationid = locationid;
	}

	public String getWorkroom() {
		return workroom;
	}

	public void setWorkroom(String workroom) {
		this.workroom = workroom;
	}

	public String getHomeaddress() {
		return homeaddress;
	}

	public void setHomeaddress(String homeaddress) {
		this.homeaddress = homeaddress;
	}

	public String getResourcetype() {
		return resourcetype;
	}

	public void setResourcetype(String resourcetype) {
		this.resourcetype = resourcetype;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public Integer getJobtitle() {
		return jobtitle;
	}

	public void setJobtitle(Integer jobtitle) {
		this.jobtitle = jobtitle;
	}

	public String getJobactivitydesc() {
		return jobactivitydesc;
	}

	public void setJobactivitydesc(String jobactivitydesc) {
		this.jobactivitydesc = jobactivitydesc;
	}

	public Integer getJoblevel() {
		return joblevel;
	}

	public void setJoblevel(Integer joblevel) {
		this.joblevel = joblevel;
	}

	public Integer getSeclevel() {
		return seclevel;
	}

	public void setSeclevel(Integer seclevel) {
		this.seclevel = seclevel;
	}

	public Integer getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(Integer departmentid) {
		this.departmentid = departmentid;
	}

	public Integer getSubcompanyid1() {
		return subcompanyid1;
	}

	public void setSubcompanyid1(Integer subcompanyid1) {
		this.subcompanyid1 = subcompanyid1;
	}

	public Integer getCostcenterid() {
		return costcenterid;
	}

	public void setCostcenterid(Integer costcenterid) {
		this.costcenterid = costcenterid;
	}

	public Integer getManagerid() {
		return managerid;
	}

	public void setManagerid(Integer managerid) {
		this.managerid = managerid;
	}

	public Integer getAssistantid() {
		return assistantid;
	}

	public void setAssistantid(Integer assistantid) {
		this.assistantid = assistantid;
	}

	public Integer getBankid1() {
		return bankid1;
	}

	public void setBankid1(Integer bankid1) {
		this.bankid1 = bankid1;
	}

	public String getAccountid1() {
		return accountid1;
	}

	public void setAccountid1(String accountid1) {
		this.accountid1 = accountid1;
	}

	public Integer getResourceimageid() {
		return resourceimageid;
	}

	public void setResourceimageid(Integer resourceimageid) {
		this.resourceimageid = resourceimageid;
	}

	public Integer getCreaterid() {
		return createrid;
	}

	public void setCreaterid(Integer createrid) {
		this.createrid = createrid;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public Integer getLastmodid() {
		return lastmodid;
	}

	public void setLastmodid(Integer lastmodid) {
		this.lastmodid = lastmodid;
	}

	public String getLastmoddate() {
		return lastmoddate;
	}

	public void setLastmoddate(String lastmoddate) {
		this.lastmoddate = lastmoddate;
	}

	public String getLastlogindate() {
		return lastlogindate;
	}

	public void setLastlogindate(String lastlogindate) {
		this.lastlogindate = lastlogindate;
	}

	public String getCertificatenum() {
		return certificatenum;
	}

	public void setCertificatenum(String certificatenum) {
		this.certificatenum = certificatenum;
	}

	public String getNativeplace() {
		return nativeplace;
	}

	public void setNativeplace(String nativeplace) {
		this.nativeplace = nativeplace;
	}

	public String getEducationlevel() {
		return educationlevel;
	}

	public void setEducationlevel(String educationlevel) {
		this.educationlevel = educationlevel;
	}

	public String getBememberdate() {
		return bememberdate;
	}

	public void setBememberdate(String bememberdate) {
		this.bememberdate = bememberdate;
	}

	public String getBepartydate() {
		return bepartydate;
	}

	public void setBepartydate(String bepartydate) {
		this.bepartydate = bepartydate;
	}

	public String getWorkcode() {
		return workcode;
	}

	public void setWorkcode(String workcode) {
		this.workcode = workcode;
	}

	@Override
	public String toString() {
		return "HrmResource [id=" + id + ", loginid=" + loginid + ", password=" + password + ", lastname=" + lastname
				+ ", sex=" + sex + ", birthday=" + birthday + ", nationality=" + nationality + ", systemlanguage="
				+ systemlanguage + ", maritalstatus=" + maritalstatus + ", telephone=" + telephone + ", mobile="
				+ mobile + ", mobilecall=" + mobilecall + ", email=" + email + ", locationid=" + locationid
				+ ", workroom=" + workroom + ", homeaddress=" + homeaddress + ", resourcetype=" + resourcetype
				+ ", startdate=" + startdate + ", enddate=" + enddate + ", jobtitle=" + jobtitle + ", jobactivitydesc="
				+ jobactivitydesc + ", joblevel=" + joblevel + ", seclevel=" + seclevel + ", departmentid="
				+ departmentid + ", subcompanyid1=" + subcompanyid1 + ", costcenterid=" + costcenterid + ", managerid="
				+ managerid + ", assistantid=" + assistantid + ", bankid1=" + bankid1 + ", accountid1=" + accountid1
				+ ", resourceimageid=" + resourceimageid + ", createrid=" + createrid + ", createdate=" + createdate
				+ ", lastmodid=" + lastmodid + ", lastmoddate=" + lastmoddate + ", lastlogindate=" + lastlogindate
				+ ", certificatenum=" + certificatenum + ", nativeplace=" + nativeplace + ", educationlevel="
				+ educationlevel + ", bememberdate=" + bememberdate + ", bepartydate=" + bepartydate + ", workcode="
				+ workcode + "]";
	}

}
