/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        /** 编辑应用-应用 */
        export class ApplicationEditApp extends ibas.BOEditApplication<IApplicationEditView, bo.Application> {

            /** 应用标识 */
            static APPLICATION_ID: string = "5ebb185a-4b41-4f3b-abdd-d0d0b4ad3bd0";
            /** 应用名称 */
            static APPLICATION_NAME: string = "thirdpartyapp_app_application_edit";
            /** 业务对象编码 */
            static BUSINESS_OBJECT_CODE: string = bo.Application.BUSINESS_OBJECT_CODE;
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationEditApp.APPLICATION_ID;
                this.name = ApplicationEditApp.APPLICATION_NAME;
                this.boCode = ApplicationEditApp.BUSINESS_OBJECT_CODE;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 注册视图 */
            protected registerView(): void {
                super.registerView();
                // 其他事件
                this.view.deleteDataEvent = this.deleteData;
                this.view.createDataEvent = this.createData;
                this.view.chooseApplicationConfigEvent = this.chooseApplicationConfig;
                this.view.uploadPictureEvent = this.uploadPicture;
            }
            /** 视图显示后 */
            protected viewShowed(): void {
                // 视图加载完成
                super.viewShowed();
                if (ibas.objects.isNull(this.editData)) {
                    // 创建编辑对象实例
                    this.editData = new bo.Application();
                    this.proceeding(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_data_created_new"));
                    this.view.showApplication(this.editData);
                    this.view.showApplicationSettingItems([]);
                } else {
                    this.view.showApplication(this.editData);
                    if (this.editData.isNew) {
                        this.view.showApplicationSettingItems([]);
                    } else {
                        let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                        boRepository.fetchApplicationSetting({
                            application: this.editData.code,
                            onCompleted: (opRslt) => {
                                try {
                                    if (opRslt.resultCode !== 0) {
                                        throw new Error(opRslt.message);
                                    }
                                    if (opRslt.resultObjects.length <= 0) {
                                        throw new Error(ibas.i18n.prop("thirdpartyapp_not_found_application_setting", this.editData.name));
                                    }
                                    this.view.showApplicationSettingItems(opRslt.resultObjects.firstOrDefault().settingItems);
                                } catch (error) {
                                    this.view.showApplicationSettingItems([]);
                                    this.messages(error);
                                }
                            }
                        });
                    }
                }
            }
            run(): void;
            run(data: bo.Application): void;
            run(): void {
                let that: this = this;
                if (ibas.objects.instanceOf(arguments[0], bo.Application)) {
                    let data: bo.Application = arguments[0];
                    // 新对象直接编辑
                    if (data.isNew) {
                        that.editData = data;
                        that.show();
                        return;
                    }
                    // 尝试重新查询编辑对象
                    let criteria: ibas.ICriteria = data.criteria();
                    if (!ibas.objects.isNull(criteria) && criteria.conditions.length > 0) {
                        // 有效的查询对象查询
                        let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                        boRepository.fetchApplication({
                            criteria: criteria,
                            onCompleted(opRslt: ibas.IOperationResult<bo.Application>): void {
                                let data: bo.Application;
                                if (opRslt.resultCode === 0) {
                                    data = opRslt.resultObjects.firstOrDefault();
                                }
                                if (ibas.objects.instanceOf(data, bo.Application)) {
                                    // 查询到了有效数据
                                    that.editData = data;
                                    that.show();
                                } else {
                                    // 数据重新检索无效
                                    that.messages({
                                        type: ibas.emMessageType.WARNING,
                                        message: ibas.i18n.prop("shell_data_deleted_and_created"),
                                        onCompleted(): void {
                                            that.show();
                                        }
                                    });
                                }
                            }
                        });
                        return; // 退出
                    }
                }
                super.run.apply(this, arguments);
            }
            /** 保存数据 */
            protected saveData(formData?: FormData): void {
                this.busy(true);
                let that: this = this;
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.saveApplication({
                    beSaved: this.editData,
                    onCompleted(opRslt: ibas.IOperationResult<bo.Application>): void {
                        try {
                            that.busy(false);
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            if (opRslt.resultObjects.length === 0) {
                                // 删除成功，释放当前对象
                                that.messages(ibas.emMessageType.SUCCESS,
                                    ibas.i18n.prop("shell_data_delete") + ibas.i18n.prop("shell_sucessful"));
                                that.editData = undefined;
                                // 刷新当前视图
                                that.viewShowed();
                            } else {
                                // 替换编辑对象
                                that.editData = opRslt.resultObjects.firstOrDefault();
                                if (formData instanceof FormData) {
                                    boRepository.saveApplicationSetting({
                                        application: that.editData.code,
                                        formData: formData,
                                        onCompleted(opRslt: ibas.IOperationResult<bo.ApplicationSetting>): void {
                                            try {
                                                if (opRslt.resultCode !== 0) {
                                                    throw new Error(opRslt.message);
                                                }
                                                // 增加版本号
                                                that.editData.logInst++;
                                                that.editData.markOld();
                                                that.view.showApplication(that.editData);
                                                if (opRslt.resultObjects.length <= 0) {
                                                    throw new Error(ibas.i18n.prop("thirdpartyapp_not_found_application_setting", that.editData.name));
                                                }
                                                that.view.showApplicationSettingItems(opRslt.resultObjects.firstOrDefault().settingItems);
                                                that.messages(ibas.emMessageType.SUCCESS,
                                                    ibas.i18n.prop("shell_data_save") + ibas.i18n.prop("shell_sucessful"));
                                            } catch (error) {
                                                that.view.showApplicationSettingItems([]);
                                                that.messages(error);
                                            }
                                        }
                                    });
                                } else {
                                    // 刷新当前视图
                                    that.viewShowed();
                                    that.messages(ibas.emMessageType.SUCCESS,
                                        ibas.i18n.prop("shell_data_save") + ibas.i18n.prop("shell_sucessful"));
                                }
                            }
                        } catch (error) {
                            that.messages(error);
                        }
                    }
                });
                this.proceeding(ibas.emMessageType.INFORMATION, ibas.i18n.prop("shell_saving_data"));
            }
            /** 删除数据 */
            protected deleteData(): void {
                let that: this = this;
                this.messages({
                    type: ibas.emMessageType.QUESTION,
                    title: ibas.i18n.prop(this.name),
                    message: ibas.i18n.prop("shell_delete_continue"),
                    actions: [ibas.emMessageAction.YES, ibas.emMessageAction.NO],
                    onCompleted(action: ibas.emMessageAction): void {
                        if (action === ibas.emMessageAction.YES) {
                            that.editData.delete();
                            that.saveData();
                        }
                    }
                });
            }
            /** 新建数据，参数1：是否克隆 */
            protected createData(clone: boolean): void {
                let that: this = this;
                let createData: Function = function (): void {
                    if (clone) {
                        // 克隆对象
                        that.editData = that.editData.clone();
                        that.proceeding(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_data_cloned_new"));
                        that.viewShowed();
                    } else {
                        // 新建对象
                        that.editData = new bo.Application();
                        that.proceeding(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_data_created_new"));
                        that.viewShowed();
                    }
                };
                if (that.editData.isDirty) {
                    this.messages({
                        type: ibas.emMessageType.QUESTION,
                        title: ibas.i18n.prop(this.name),
                        message: ibas.i18n.prop("shell_data_not_saved_continue"),
                        actions: [ibas.emMessageAction.YES, ibas.emMessageAction.NO],
                        onCompleted(action: ibas.emMessageAction): void {
                            if (action === ibas.emMessageAction.YES) {
                                createData();
                            }
                        }
                    });
                } else {
                    createData();
                }
            }
            private chooseApplicationConfig(): void {
                ibas.servicesManager.runChooseService<bo.IApplicationConfig>({
                    boCode: bo.BO_CODE_APPLICATIONCONFIG,
                    chooseType: ibas.emChooseType.SINGLE,
                    criteria: [
                        new ibas.Condition(bo.ApplicationConfig.PROPERTY_CODE_NAME, ibas.emConditionOperation.NOT_NULL)
                    ],
                    onCompleted: (selecteds) => {
                        let selected: bo.IApplicationConfig = selecteds.firstOrDefault();
                        this.editData.config = selected.code;
                        this.editData.settings = undefined;
                        if (!(this.editData.series > 0)) {
                            if (ibas.strings.isEmpty(this.editData.code)) {
                                this.editData.code = selected.code;
                            }
                            if (ibas.strings.isEmpty(this.editData.name)) {
                                this.editData.name = selected.name;
                            }
                        }
                        let settings: Array<bo.ApplicationSettingItem> = new Array<bo.ApplicationSettingItem>();
                        for (let item of selected.applicationConfigItems) {
                            if (item.forUser === ibas.emYesNo.YES) {
                                continue;
                            }
                            let setting: bo.ApplicationSettingItem = new bo.ApplicationSettingItem();
                            setting.name = item.name;
                            setting.description = item.description;
                            setting.category = item.category;
                            setting.value = item.value;
                            settings.push(setting);
                        }
                        this.view.showApplicationSettingItems(settings);
                    }
                });
            }
            /** 上传图片事件 */
            private uploadPicture(formData: FormData): void {
                let that: this = this;
                this.proceeding(ibas.emMessageType.INFORMATION, ibas.i18n.prop("shell_uploading_file"));
                this.busy(true);
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.upload({
                    fileData: formData,
                    onCompleted(opRslt: ibas.IOperationResult<ibas.FileData>): void {
                        try {
                            that.busy(false);
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            that.proceeding(ibas.emMessageType.INFORMATION,
                                ibas.i18n.prop("shell_upload") + ibas.i18n.prop("shell_sucessful"));
                            let fileData: ibas.FileData = opRslt.resultObjects.firstOrDefault();
                            that.editData.picture = fileData.fileName;
                        } catch (error) {
                            that.messages(error);
                        }
                    }
                });
            }
        }
        /** 视图-应用 */
        export interface IApplicationEditView extends ibas.IBOEditView {
            /** 显示数据 */
            showApplication(data: bo.Application): void;
            /** 删除数据事件 */
            deleteDataEvent: Function;
            /** 新建数据事件，参数1：是否克隆 */
            createDataEvent: Function;
            /** 选择应用配置 */
            chooseApplicationConfigEvent: Function;
            /** 显示可用配置 */
            showApplicationSettingItems(datas: bo.ApplicationSettingItem[]): void;
            /** 上传图片事件 */
            uploadPictureEvent: Function;
        }
    }
}
