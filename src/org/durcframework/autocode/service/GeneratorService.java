package org.durcframework.autocode.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.durcframework.autocode.common.AutoCodeContext;
import org.durcframework.autocode.entity.CodeFile;
import org.durcframework.autocode.entity.DataSourceConfig;
import org.durcframework.autocode.entity.GeneratorParam;
import org.durcframework.autocode.entity.TemplateConfig;
import org.durcframework.autocode.generator.SQLContext;
import org.durcframework.autocode.generator.SQLService;
import org.durcframework.autocode.generator.SQLServiceFactory;
import org.durcframework.autocode.generator.TableDefinition;
import org.durcframework.autocode.generator.TableSelector;
import org.durcframework.autocode.util.FileUtil;
import org.durcframework.autocode.util.VelocityUtil;
import org.durcframework.autocode.util.XmlFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GeneratorService {
	@Autowired
	private TemplateConfigService templateConfigService;

	private static final String DOWNLOAD_FOLDER_NAME = "download";

	/**
	 * 生成代码内容,map的
	 * 
	 * @param tableNames
	 * @param tcIds
	 * @param dataSourceConfig
	 * @return 一张表对应多个模板
	 */
	public List<CodeFile> generate(GeneratorParam generatorParam,
			DataSourceConfig dataSourceConfig) {

		List<SQLContext> contextList = this.buildSQLContextList(generatorParam,
				dataSourceConfig);

		List<CodeFile> codeFileList = new ArrayList<CodeFile>();

		for (SQLContext sqlContext : contextList) {
			setPackageName(sqlContext, generatorParam.getPackageName());
			sqlContext.setService(generatorParam.getService());
			String packageName = sqlContext.getPackageName();
			for (int tcId : generatorParam.getTcIds()) {

				TemplateConfig template = templateConfigService.get(tcId);

				String fileName = doGenerator(sqlContext,
						template.getFileName());
				String content = doGenerator(sqlContext, template.getContent());
				if (fileName.contains("jsp")) {// jsp模板包名
					packageName = "jsp."
							+ sqlContext.getJavaBeanName().toLowerCase();
				}
				CodeFile codeFile = new CodeFile(packageName, fileName, content);

				this.formatFile(codeFile);

				this.saveFile(codeFile, generatorParam, sqlContext);
				codeFileList.add(codeFile);

			}
		}

		return codeFileList;
	}

	private void saveFile(CodeFile codeFile, GeneratorParam generatorParam,
			SQLContext sqlContext) {
		String packageName = codeFile.getTableName();
		String templateName = codeFile.getTemplateName();
		String content = codeFile.getContent();
		String apiPath = generatorParam.getApiPath();
		String webPath = generatorParam.getWebPath();
		String serverPath = generatorParam.getServerPath();
		boolean cover = generatorParam.getCover() == 1 ? true : false;
		String savePath = packageName.replaceAll("\\.", "/");
		boolean apiFlg = apiPath != null && !"".equals(apiPath)
				&& templateName.contains("java")
				&& FileUtil.checkExists(apiPath);
		boolean webFlg = webPath != null && !"".equals(webPath)
				&& templateName.contains("jsp")
				&& FileUtil.checkExists(webPath);
		boolean serverFlg = serverPath != null && !"".equals(serverPath)
				&& FileUtil.checkExists(serverPath);
		if (apiFlg) {
			String api = "src/";
			String service = sqlContext.getService();

			String filePathName = apiPath + File.separator + api + savePath
					+ File.separator + templateName;
			boolean flg =FileUtil.isExists(filePathName);
			if(!flg){
				FileUtil.createFolder(apiPath + File.separator + api + savePath);
			}
			if (cover) {// 覆盖
				FileUtil.write(content, filePathName,
						generatorParam.getCharset());
			} else {// 不覆盖
				if (!flg) {
					FileUtil.write(content, filePathName,
							generatorParam.getCharset());
				}
			}
			if (serverFlg && !flg) {
				String server="src/";
				FileUtil.createFolder(serverPath + File.separator + server);
				String serviceFileName = serverPath + File.separator + server
						+ "service.properties";
				String apiName=templateName.substring(0,
						templateName.indexOf(".java"));
				String txt = service
						+ "^_^"
						+ apiName
						+ "="
						+ packageName
						+ "."
						+ apiName;
				String hh = "\r\n";
				if (FileUtil.isExists(serviceFileName)) {
					FileUtil.write(FileUtil.read(serviceFileName) + txt + hh,
							serviceFileName, generatorParam.getCharset());
				} else {
					FileUtil.write(
							"###############################" + service
									+ "###############################" + hh
									+ txt + hh, serviceFileName,
							generatorParam.getCharset());
				}
			}
		}
		if (webFlg) {
			String web = "WebRoot/WEB-INF/";
			String filePathName = webPath + File.separator + web + savePath
					+ File.separator + templateName;
			boolean flg =FileUtil.isExists(filePathName);
			if(!flg){
				FileUtil.createFolder(webPath + File.separator + web + savePath);
			}
			if (cover) {// 覆盖
				FileUtil.write(content, filePathName,
						generatorParam.getCharset());
			} else {// 不覆盖
				if (!flg) {
					FileUtil.write(content, filePathName,
							generatorParam.getCharset());
				}
			}
		}
	}

	private void formatFile(CodeFile file) {
		String formated = this.doFormat(file.getTemplateName(),
				file.getContent());
		file.setContent(formated);
	}

	private String doFormat(String fileName, String content) {
		if (fileName.endsWith(".xml")) {
			return XmlFormat.format(content);
		}
		return content;
	}

	/**
	 * 生成zip
	 * 
	 * @param generatorParam
	 * @param dataSourceConfig
	 * @param webRootPath
	 * @return
	 */
	public String generateZip(GeneratorParam generatorParam,
			DataSourceConfig dataSourceConfig, String webRootPath) {
		List<SQLContext> contextList = this.buildSQLContextList(generatorParam,
				dataSourceConfig);
		String projectFolder = this.buildProjectFolder(webRootPath);
		for (SQLContext sqlContext : contextList) {
			setPackageName(sqlContext, generatorParam.getPackageName());
			sqlContext.setService(generatorParam.getService());
			String packageName = sqlContext.getPackageName();
			for (int tcId : generatorParam.getTcIds()) {
				TemplateConfig template = templateConfigService.get(tcId);
				String content = doGenerator(sqlContext, template.getContent());
				String fileName = doGenerator(sqlContext,
						template.getFileName());

				if (fileName.contains("jsp")) {// jsp模板包名
					packageName = "jsp."
							+ sqlContext.getJavaBeanName().toLowerCase();
				}
				String savePath = packageName.replaceAll("\\.",
						"/");
				content = this.doFormat(fileName, content);
				
				if (StringUtils.isEmpty(fileName)) {
					fileName = template.getName();
				}

				FileUtil.createFolder(projectFolder + File.separator + savePath);

				String filePathName = projectFolder + File.separator + savePath
						+ File.separator + fileName;
				FileUtil.write(content, filePathName,
						generatorParam.getCharset());
			}
		}

		try {
			FileUtil.zip(projectFolder, projectFolder + ".zip");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtil.deleteDir(new File(projectFolder));
		}

		return projectFolder + ".zip";
	}

	/**
	 * 返回SQL上下文列表
	 * 
	 * @param tableNames
	 * @return
	 */
	private List<SQLContext> buildSQLContextList(GeneratorParam generatorParam,
			DataSourceConfig dataSourceConfig) {

		List<String> tableNames = generatorParam.getTableNames();
		List<SQLContext> contextList = new ArrayList<SQLContext>();
		SQLService service = SQLServiceFactory.build(dataSourceConfig);

		TableSelector tableSelector = service
				.getTableSelector(dataSourceConfig);
		tableSelector.setSchTableNames(tableNames);

		List<TableDefinition> tableDefinitions = tableSelector
				.getTableDefinitions();

		for (TableDefinition tableDefinition : tableDefinitions) {
			contextList.add(new SQLContext(tableDefinition));
		}

		return contextList;
	}

	private String buildProjectFolder(String webRootPath) {
		return webRootPath + File.separator + DOWNLOAD_FOLDER_NAME
				+ File.separator
				+ AutoCodeContext.getInstance().getUser().getUsername()
				+ System.currentTimeMillis();
	}

	private void setPackageName(SQLContext sqlContext, String packageName) {
		if (StringUtils.hasText(packageName)) {
			sqlContext.setPackageName(packageName + "."
					+ sqlContext.getJavaBeanName().toLowerCase());
		}
	}

	private String doGenerator(SQLContext sqlContext, String template) {
		if (template == null) {
			return "";
		}
		VelocityContext context = new VelocityContext();
		// 将数据库字段转换为模板字段
		context.put("context", sqlContext);
		context.put("table", sqlContext.getTableDefinition());
		context.put("pkColumn", sqlContext.getTableDefinition().getPkColumn());
		context.put("columns", sqlContext.getTableDefinition()
				.getColumnDefinitions());
		context.put("username", AutoCodeContext.getInstance().getUser()
				.getUsername());
		return VelocityUtil.generate(context, template);
	}
}
