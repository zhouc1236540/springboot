package com.e6.e9.entities;

import javax.persistence.Table;

/**
 * 文档附件视图。
 * 
 * @author William
 */
@Table(name = "CV_DOCIMAGEFILE", catalog = "ecology")
public class CVDocImageFile {

	private Integer id;
	private String imageFileName;
	private String fileRealPath;
	private String extFile;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public String getFileRealPath() {
		return fileRealPath;
	}

	public void setFileRealPath(String fileRealPath) {
		this.fileRealPath = fileRealPath;
	}

	public String getExtFile() {
		return extFile;
	}

	public void setExtFile(String extFile) {
		this.extFile = extFile;
	}
}
