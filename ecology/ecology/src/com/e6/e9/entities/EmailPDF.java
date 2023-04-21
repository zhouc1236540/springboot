package com.e6.e9.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description: 邮件文档附件
 * @ClassName: uf_EmailPDF
 * @author lusheliang
 * @date 2019年11月1日
 *
 */
@Table(name = "uf_EmailPDF", catalog = "ecology")
public class EmailPDF {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String requestid;
	private String imagefilename;// 附件名称
	private String filerealpath; // 资源地址
	private String createDate;
	private String updateDate;
	private String fileuniqueidentifier;

	public String getFileuniqueidentifier() {
		return fileuniqueidentifier;
	}

	public void setFileuniqueidentifier(String fileuniqueidentifier) {
		this.fileuniqueidentifier = fileuniqueidentifier;
	}

	public String getRequestid() {
		return requestid;
	}

	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImagefilename() {
		return imagefilename;
	}

	public void setImagefilename(String imagefilename) {
		this.imagefilename = imagefilename;
	}

	public String getFilerealpath() {
		return filerealpath;
	}

	public void setFilerealpath(String filerealpath) {
		this.filerealpath = filerealpath;
	}

	@Override
	public String toString() {
		return "EmailPDF [id=" + id + ", requestid=" + requestid + ", imagefilename=" + imagefilename
				+ ", filerealpath=" + filerealpath + ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", fileuniqueidentifier=" + fileuniqueidentifier + "]";
	}

}
