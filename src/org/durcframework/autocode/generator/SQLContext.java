package org.durcframework.autocode.generator;

import java.util.Date;
import java.util.List;

import org.durcframework.autocode.util.DateUtil;
import org.durcframework.autocode.util.FieldUtil;

/**
 * SQL上下文,这里可以取到表,字段信息<br>
 * 最终会把SQL上下文信息放到velocity中
 */
public class SQLContext {
	private TableDefinition tableDefinition; // 表结构定义
	private String packageName; // 包名
	private String createTime;
	private String service;
	public SQLContext(TableDefinition tableDefinition){
		this.tableDefinition = tableDefinition;
		// 默认为全字母小写的类名
		this.packageName = getJavaBeanName().toLowerCase();
		this.createTime=DateUtil.ymdFormat(new Date());
	}
	
	/**
	 * 返回Java类名
	 * @return
	 */
	public String getJavaBeanName(){
		String tableName = getJavaBeanNameLF();
		return FieldUtil.upperFirstLetter(tableName);
	}
	/**
	 * 返回Java类名全小写
	 * @return
	 */
	public String getJavaBeanNameLC(){
		String tableName = tableDefinition.getTableName();
		tableName = FieldUtil.underlineFilter(tableName);
		tableName = FieldUtil.dotFilter(tableName);
		return tableName.toLowerCase();
	}
	/**
	 * 返回Java类名全大写
	 * @return
	 */
	public String getJavaBeanNameUC(){
		String tableName = tableDefinition.getTableName();
		tableName = FieldUtil.underlineFilter(tableName);
		tableName = FieldUtil.dotFilter(tableName);
		return tableName.toUpperCase();
	}
	/**
	 * 返回Java类名且首字母小写
	 * @return
	 */
	public String getJavaBeanNameLF(){
		String tableName = tableDefinition.getTableName();
		tableName = FieldUtil.underlineFilter(tableName);
		tableName = FieldUtil.dotFilter(tableName);
		return FieldUtil.lowerFirstLetter(tableName);
	}
	/**
	 * 返回Java类名且首字母大写
	 * @return
	 */
	public String getJavaBeanNameUF(){
		String tableName = getJavaBeanNameLC();
		return FieldUtil.upperFirstLetter(tableName);
	}
	
	public String getPkName(){
		if(this.tableDefinition.getPkColumn() != null){
			return this.tableDefinition.getPkColumn().getColumnName();
		}
		return "";
	}
	
	public String getJavaPkName(){
		if(this.tableDefinition.getPkColumn() != null){
			return this.tableDefinition.getPkColumn().getJavaFieldName();
		}
		return "";
	}
	
	public String getJavaPkType(){
		if(this.tableDefinition.getPkColumn() != null){
			return this.tableDefinition.getPkColumn().getJavaType();
		}
		return "";
	}
	
	public String getMybatisPkType(){
		if(this.tableDefinition.getPkColumn() != null){
			return this.tableDefinition.getPkColumn().getMybatisJdbcType();
		}
		return "";
	}

	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}

	public void setTableDefinition(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
	}
	
	public List<ColumnDefinition> getColumnDefinitionList(){
		return tableDefinition.getColumnDefinitions();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public static void main(String[] args) {
		
		SQLContext[] tt = {
			new SQLContext(new TableDefinition("r_table_b"))
			,new SQLContext(new TableDefinition("MyTable"))
			,new SQLContext(new TableDefinition("user.frontUser"))
			,new SQLContext(new TableDefinition("user.back_user"))
		};
		
		for (SQLContext ctx : tt) {
			System.out.println(ctx.getJavaBeanName());
		}
		/*
		输出:
		RTableB
		MyTable
		UserFrontUser
		UserBackUser
		*/
	}
	
	
}
