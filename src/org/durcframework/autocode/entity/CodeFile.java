package org.durcframework.autocode.entity;

public class CodeFile {
	public CodeFile(String tableName, String templateName, String content) {
		this.tableName = tableName;
		this.templateName = templateName;
		this.content = content;
	}

	private String tableName;//包名
	private String templateName;//模板名
	private String content;//内容

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
