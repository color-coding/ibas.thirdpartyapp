package org.colorcoding.ibas.thirdpartyapp.service.rest;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import org.colorcoding.ibas.bobas.common.OperationMessage;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.FileRepository;
import org.colorcoding.ibas.bobas.repository.jersey.FileRepositoryService;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.bo.application.Application;
import org.colorcoding.ibas.thirdpartyapp.bo.application.IApplication;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSetting;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItem;
import org.colorcoding.ibas.thirdpartyapp.bo.other.ApplicationSettingItems;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.IUserMapping;
import org.colorcoding.ibas.thirdpartyapp.bo.usermapping.UserMapping;
import org.colorcoding.ibas.thirdpartyapp.data.DataConvert;
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
	public OperationResult<FileData> upload(FormDataMultiPart formData,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		return super.save(formData.getField("file"), MyConfiguration.optToken(authorization, token));
	}

	@POST
	@Path("download")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void download(Criteria criteria, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token, @Context HttpServletResponse response) {
		try {
			// 获取文件
			IOperationResult<FileData> operationResult = this.fetch(criteria,
					MyConfiguration.optToken(authorization, token));
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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("fetchApplicationSetting")
	public OperationResult<ApplicationSetting> fetchApplicationSetting(@QueryParam("application") String appCode,
			@QueryParam("user") String user, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		try {
			token = MyConfiguration.optToken(authorization, token);
			BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
			boRepository.setUserToken(token);
			OperationResult<ApplicationSetting> operationResult = new OperationResult<>();
			if (DataConvert.isNullOrEmpty(user)) {
				Criteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(Application.PROPERTY_CODE.getName());
				condition.setValue(appCode);
				IOperationResult<IApplication> opRslt = boRepository.fetchApplication(criteria);
				if (opRslt.getError() != null) {
					throw opRslt.getError();
				}
				IApplication application = opRslt.getResultObjects().firstOrDefault();
				if (application == null) {
					throw new Exception(I18N.prop("msg_tpa_invaild_application", appCode));
				}
				operationResult.addResultObjects(boRepository.createApplicationSetting(application));
			} else {
				Criteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
				condition.setValue(appCode);
				condition = criteria.getConditions().create();
				condition.setAlias(UserMapping.PROPERTY_USER.getName());
				condition.setValue(user);
				IOperationResult<IUserMapping> opRslt = boRepository.fetchUserMapping(criteria);
				if (opRslt.getError() != null) {
					throw opRslt.getError();
				}
				IUserMapping userMapping = opRslt.getResultObjects().firstOrDefault();
				if (userMapping == null) {
					throw new Exception(I18N.prop("msg_tpa_invaild_application_user_mapping", appCode, user));
				}
				operationResult.addResultObjects(boRepository.createApplicationSetting(userMapping));
			}
			return operationResult;
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	@POST
	@Path("saveApplicationSetting")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationResult<ApplicationSetting> saveApplicationSetting(FormDataMultiPart formData,
			@QueryParam("application") String appCode, @QueryParam("user") String user,
			@HeaderParam("authorization") String authorization, @QueryParam("token") String token) {
		try {
			token = MyConfiguration.optToken(authorization, token);
			Criteria criteria = new Criteria();
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
			ApplicationSettingItems savingItems = new ApplicationSettingItems();
			ApplicationSetting appSetting = boRepository.createApplicationSetting(application, false);
			for (ApplicationSettingItem settingItem : appSetting.getSettingItems()) {
				if (DataConvert.isNullOrEmpty(settingItem.getName())) {
					continue;
				}
				FormDataBodyPart bodyPart = formData.getField(settingItem.getName());
				if (bodyPart != null) {
					if (settingItem.getCategory() == emConfigItemCategory.FILE
							&& bodyPart.getMediaType() != MediaType.TEXT_PLAIN_TYPE) {
						OperationResult<FileData> opRsltFile = super.save(bodyPart, token);
						if (opRsltFile.getError() != null) {
							throw opRsltFile.getError();
						}
						FileData fileData = opRsltFile.getResultObjects().firstOrDefault();
						if (fileData == null) {
							throw new Exception(
									I18N.prop("msg_tpa_invaild_application_setting_item", settingItem.getName()));
						}
						settingItem.setValue(fileData.getFileName());
					} else {
						settingItem.setValue(bodyPart.getValue());
					}
					savingItems.add(settingItem);
				}
			}
			if (DataConvert.isNullOrEmpty(user)) {
				application.setSettings(savingItems.encode());
				opRsltApp = boRepository.saveApplication(application);
				if (opRsltApp.getError() != null) {
					throw opRsltApp.getError();
				}
			} else {
				criteria = new Criteria();
				condition = criteria.getConditions().create();
				condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
				condition.setValue(appCode);
				condition = criteria.getConditions().create();
				condition.setAlias(UserMapping.PROPERTY_USER.getName());
				condition.setValue(user);
				boRepository = new BORepositoryThirdPartyApp();
				boRepository.setUserToken(token);
				IOperationResult<IUserMapping> opRsltMapping = boRepository.fetchUserMapping(criteria);
				if (opRsltMapping.getError() != null) {
					throw opRsltMapping.getError();
				}
				IUserMapping userMapping = opRsltMapping.getResultObjects().firstOrDefault();
				if (userMapping == null) {
					userMapping = new UserMapping();
					userMapping.setApplication(appCode);
					userMapping.setUser(user);
				}
				userMapping.setSettings(savingItems.encode());
				opRsltMapping = boRepository.saveUserMapping(userMapping);
				if (opRsltMapping.getError() != null) {
					throw opRsltMapping.getError();
				}
			}
			return this.fetchApplicationSetting(application.getCode(), user, authorization, token);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	@POST
	@Path("removeApplicationSetting")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OperationMessage removeApplicationSetting(@QueryParam("application") String appCode,
			@QueryParam("user") String user, @HeaderParam("authorization") String authorization,
			@QueryParam("token") String token) {
		try {
			token = MyConfiguration.optToken(authorization, token);
			Criteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_APPLICATION.getName());
			condition.setValue(appCode);
			condition = criteria.getConditions().create();
			condition.setAlias(UserMapping.PROPERTY_USER.getName());
			condition.setValue(user);
			BORepositoryThirdPartyApp boRepository = new BORepositoryThirdPartyApp();
			boRepository.setUserToken(token);
			IOperationResult<IUserMapping> opRsltMapping = boRepository.fetchUserMapping(criteria);
			if (opRsltMapping.getError() != null) {
				throw opRsltMapping.getError();
			}
			for (IUserMapping item : opRsltMapping.getResultObjects()) {
				item.delete();
				boRepository.saveUserMapping(item);
			}
			return new OperationMessage();
		} catch (Exception e) {
			return new OperationMessage(e);
		}
	}
}
