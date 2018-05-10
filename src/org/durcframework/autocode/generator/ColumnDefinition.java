package org.durcframework.autocode.generator;

import org.durcframework.autocode.util.FieldUtil;
import org.durcframework.autocode.util.SqlTypeUtil;

/**
 * 表字段信息
 */
public class ColumnDefinition  {

	private String columnName; // 数据库字段名
	private String type; // 数据库类型
	private boolean isIdentity; // 是否自增
	private boolean isPk; // 是否主键
	private String comment; // 字段注释
	private String length;//字段长度
	private String checkType;//验证类型
	private String isNull;//是否可以为空
	private String precision;//字段精度
	private String scale;//小数点长度
	/**
	 * 是否是自增主键
	 * 
	 * @return
	 */
	public boolean getIsIdentityPk() {
		return isPk && isIdentity;
	}
	
	/**
	 * 返回java字段名,并且第一个字母大写
	 * 
	 * @return
	 */
	public String getJavaFieldNameUF() {
		return FieldUtil.upperFirstLetter(getJavaFieldName());
	}
	
	public String getJavaFieldName() {
		return FieldUtil.underlineFilter(columnName.toLowerCase());
	}
	
	/**
	 * 获得基本类型,int,float
	 * @return
	 */
	
	public String getJavaType() {
		String typeLower = type.toLowerCase();
		return SqlTypeUtil.convertToJavaType(typeLower);
	}
	
	/**
	 * 获得装箱类型,Integer,Float
	 * @return
	 */
	
	public String getJavaTypeBox(){
		String typeLower = type.toLowerCase();
		return SqlTypeUtil.convertToJavaBoxType(typeLower);
	}
	
	public String getMybatisJdbcType() {
		String typeLower = type.toLowerCase();
		return SqlTypeUtil.convertToMyBatisJdbcType(typeLower);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getIsIdentity() {
		return isIdentity;
	}

	public void setIsIdentity(boolean isIdentity) {
		this.isIdentity = isIdentity;
	}

	public boolean getIsPk() {
		return isPk;
	}

	public void setIsPk(boolean isPk) {
		this.isPk = isPk;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		if(comment == null) {
			comment = "";
		}
		this.comment = comment;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getIsNull() {
		return isNull;
	}

	public void setIsNull(String isNull) {
		this.isNull = isNull;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
}
