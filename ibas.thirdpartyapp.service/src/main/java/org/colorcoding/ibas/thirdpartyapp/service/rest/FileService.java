package org.colorcoding.ibas.thirdpartyapp.service.rest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.FileRepository;
import org.colorcoding.ibas.bobas.repository.jersey.FileRepositoryService;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.ApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.IApplicationConfig;
import org.colorcoding.ibas.thirdpartyapp.bo.applicationconfig.IApplicationConfigItem;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItem;
import org.colorcoding.ibas.thirdpartyapp.data.emConfigItemCategory;
import org.colorcoding.ibas.thirdpartyapp.repository.BORepositoryThirdPartyApp;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path("file")
public class FileService extends FileRepositoryService {

	public final static String WORK_FOLDER = MyConfiguration.getFileFolder();

	public FileService() {
		// 设置工作目录
		this.getRepository().setRepositoryFolder(FileService.WORK_FOLDER);
	}

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<FileData> upload(FormDataMultiPart formData, @QueryParam("token") String token) {
		return super.save(formData.getField("file"), token);
	}

	@POST
	@Path("download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void download(Criteria criteria, @QueryParam("token") String token, @Context HttpServletResponse response) {
		try {
			// 获取文件
			IOperationResult<FileData> operationResult = this.fetch(criteria, token);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			FileData fileData = operationResult.getResultObjects().firstOrDefault();
			if (fileData != null) {
				// 设置文件名
				response.setHeader("Content-Disposition",
						String.format("attachment;filename=%s", fileData.getFileName()));
				// 设置内容类型
				response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				// 写入响应输出流
				OutputStream os = response.getOutputStream();
				os.write(fileData.getFileBytes());
				os.flush();
			} else {
				// 文件不存在
				throw new WebApplicationException(404);
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@GET
	@Path("{resource}")
	public void resource(@PathParam("resource") String resource, @QueryParam("token") String token,
			@Context HttpServletResponse response) {
		try {
			Criteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(FileRepository.CRITERIA_CONDITION_ALIAS_FILE_NAME);
			condition.setValue(resource);
			// 获取文件
			IOperationResult<FileData> operationResult = this.fetch(criteria, token);
			if (operationResult.getError() != null) {
				throw operationResult.getError();
			}
			FileData fileData = operationResult.getResultObjects().firstOrDefault();
			if (fileData != null) {
				// 设置内容类型
				response.setContentType(this.getContentType(fileData));
				// 写入响应输出流
				OutputStream os = response.getOutputStream();
				os.write(fileData.getFileBytes());
				os.flush();
			} else {
				// 文件不存在
				throw new WebApplicationException(404);
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	@POST
	@Path("saveApplicationSetting")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<Application> saveApplicationSetting(FormDataMultiPart formData,
			@QueryParam("application") String appCode, @QueryParam("token") String token) {
		try {
			OperationResult<Application> operationResult = new OperationResult<>();
			Criteria criteria = new Criteria();
			criteria.setResultCount(1);
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(Application.PROPERTY_CODE.getName());
			condition.setValue(appCode);
			BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
			boRepository.setUserToken(token);
			IOperationResult<IApplication> opRsltApp = boRepository.fetchApplication(criteria);
			if (opRsltApp.getError() != null) {
				throw opRsltApp.getError();
			}
			IApplication application = opRsltApp.getResultObjects().firstOrDefault();
			if (application == null) {
				throw new Exception(I18N.prop("msg_tpa_invaild_application", appCode));
			}
			criteria = new Criteria();
			condition = criteria.getConditions().create();
			condition.setAlias(ApplicationConfig.PROPERTY_CODE.getName());
			condition.setValue(application.getConfig());
			IOperationResult<IApplicationConfig> opRsltConf = boRepository.fetchApplicationConfig(criteria);
			IApplicationConfig applicationConfig = opRsltConf.getResultObjects().firstOrDefault();
			if (applicationConfig == null) {
				throw new Exception(I18N.prop("msg_tpa_invaild_application_config", application.getConfig()));
			}
			OperationResult<FileData> opRsltFile;
			ApplicationSetting appSetting = new ApplicationSetting();
			appSetting.setName(application.getCode());
			appSetting.setGroup(application.getConfig());
			appSetting.setDescription(application.getName());
			appSetting.setSecretKey(MyConfiguration.getConfigValue(
					org.colorcoding.ibas.initialfantasy.MyConfiguration.CONFIG_ITEM_USER_TOKEN_KEY,
					applicationConfig.getCreateActionId()));
			for (IApplicationConfigItem configItem : applicationConfig.getApplicationConfigItems()) {
				ApplicationSettingItem appSettingItem = appSetting.getSettingItems().create();
				appSettingItem.setName(configItem.getName());
				appSettingItem.setDescription(configItem.getDescription());
				appSettingItem.setCategory(configItem.getCategory());
				FormDataBodyPart bodyPart = formData.getField(appSettingItem.getName());
				if (bodyPart != null) {
					if (appSettingItem.getCategory() == emConfigItemCategory.FILE
							&& bodyPart.getMediaType() != MediaType.TEXT_PLAIN_TYPE) {
						opRsltFile = super.save(bodyPart, token);
						if (opRsltFile.getError() != null) {
							throw opRsltFile.getError();
						}
						FileData fileData = opRsltFile.getResultObjects().firstOrDefault();
						if (fileData == null) {
							throw new Exception(
									I18N.prop("msg_tpa_invaild_application_setting_item", appSettingItem.getName()));
						}
						appSettingItem.setValue(fileData.getFileName());
					} else {
						appSettingItem.setValue(bodyPart.getValue());
					}
				}
			}
			try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
				ISerializer<?> serializer = SerializerFactory.create().createManager().create("json");
				serializer.serialize(appSetting.getSettingItems(), writer, ApplicationSettingItem.class);
				application.setSettings(Base64.getEncoder().encodeToString(writer.toByteArray()));
			} catch (Exception e) {
				throw e;
			}
			opRsltApp = boRepository.saveApplication(application);
			if (opRsltApp.getError() != null) {
				throw opRsltApp.getError();
			}
			operationResult.addResultObjects(opRsltApp.getResultObjects().firstOrDefault());
			return operationResult;
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}
}
