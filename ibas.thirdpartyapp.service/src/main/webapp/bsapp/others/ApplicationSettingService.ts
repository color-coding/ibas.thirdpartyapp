/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        /** 应用设置-应用 */
        export class ApplicationSettingService extends ibas.ServiceWithResultApplication<IApplicationSettingServiceView, IApplicationSettingContract, bo.ApplicationSetting> {
            /** 应用标识 */
            static APPLICATION_ID: string = "523cb99d-7667-4b82-af0e-bd1e6b27d669";
            /** 应用名称 */
            static APPLICATION_NAME: string = "thirdpartyapp_app_application_setting";
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationSettingService.APPLICATION_ID;
                this.name = ApplicationSettingService.APPLICATION_NAME;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 注册视图 */
            protected registerView(): void {
                super.registerView();
                // 其他事件
                this.view.saveDataEvent = this.saveData;
            }
            /** 视图显示后 */
            protected viewShowed(): void {
                // 视图加载完成
                this.view.showSetting(this.applicationSetting);
                if (this.user instanceof initialfantasy.bo.User) {
                    let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                    boRepository.fetchApplicationSetting({
                        application: this.applicationSetting.name,
                        user: this.user?.code,
                        onCompleted: (opRslt) => {
                            try {
                                for (let item of opRslt.resultObjects) {
                                    for (let sItem of item.settingItems) {
                                        for (let cItem of this.applicationSetting.settingItems) {
                                            if (cItem.name === sItem.name) {
                                                cItem.value = sItem.value;
                                                break;
                                            }
                                        }
                                    }
                                }
                                this.view.showSettingItems(this.applicationSetting.settingItems);
                            } catch (error) {
                                this.messages(error);
                            }
                        }
                    });
                } else {
                    this.view.showSettingItems(this.applicationSetting.settingItems);
                }
            }
            protected applicationSetting: bo.ApplicationSetting;
            protected user: initialfantasy.bo.User;

            protected runService(contract: IApplicationSettingContract): void {
                if (ibas.strings.isEmpty(contract.application)) {
                    throw new Error(ibas.i18n.prop("sys_invalid_parameter", "application"));
                }
                let criteria: ibas.ICriteria = new ibas.Criteria();
                let condition: ibas.ICondition = criteria.conditions.create();
                condition.alias = bo.Application.PROPERTY_CODE_NAME;
                condition.value = contract.application;
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.fetchApplication({
                    criteria: criteria,
                    onCompleted: (opRslt) => {
                        try {
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            if (opRslt.resultObjects.length === 0) {
                                throw new Error(ibas.i18n.prop("thirdpartyapp_not_found_application"));
                            }
                            let application: bo.Application = opRslt.resultObjects.firstOrDefault();
                            this.applicationSetting = new bo.ApplicationSetting();
                            this.applicationSetting.name = application.code;
                            this.applicationSetting.description = application.name;
                            this.applicationSetting.group = application.config;
                            let criteria: ibas.ICriteria = new ibas.Criteria();
                            let condition: ibas.ICondition = criteria.conditions.create();
                            condition.alias = bo.ApplicationConfig.PROPERTY_CODE_NAME;
                            condition.value = this.applicationSetting.group;
                            boRepository.fetchApplicationConfig({
                                criteria: criteria,
                                onCompleted: (opRslt) => {
                                    try {
                                        if (opRslt.resultCode !== 0) {
                                            throw new Error(opRslt.message);
                                        }
                                        if (opRslt.resultObjects.length === 0) {
                                            throw new Error(ibas.i18n.prop("thirdpartyapp_not_found_application_config"));
                                        }
                                        let applicationConfig: bo.ApplicationConfig = opRslt.resultObjects.firstOrDefault();
                                        // 增加用户映射配置
                                        if (!ibas.objects.isNull(contract.user)) {
                                            let sItem: bo.ApplicationSettingItem = new bo.ApplicationSettingItem();
                                            sItem.name = bo.UserMapping.PROPERTY_ACCOUNT_NAME;
                                            sItem.description = ibas.i18n.prop("bo_usermapping_account");
                                            sItem.category = bo.emConfigItemCategory.TEXT;
                                            this.applicationSetting.settingItems.add(sItem);
                                            sItem = new bo.ApplicationSettingItem();
                                            sItem.name = bo.UserMapping.PROPERTY_ACCESSDATA_NAME;
                                            sItem.description = ibas.i18n.prop("bo_usermapping_accessdata");
                                            sItem.category = bo.emConfigItemCategory.TEXT;
                                            this.applicationSetting.settingItems.add(sItem);
                                        }
                                        for (let item of applicationConfig.applicationConfigItems) {
                                            if (!ibas.objects.isNull(contract.user)) {
                                                if (item.forUser !== ibas.emYesNo.YES) {
                                                    continue;
                                                }
                                            } else {
                                                if (item.forUser === ibas.emYesNo.YES) {
                                                    continue;
                                                }
                                            }
                                            let sItem: bo.ApplicationSettingItem = new bo.ApplicationSettingItem();
                                            sItem.name = item.name;
                                            sItem.description = item.description;
                                            sItem.category = item.category;
                                            sItem.value = item.value;
                                            this.applicationSetting.settingItems.add(sItem);
                                        }
                                        if (!ibas.objects.isNull(contract.user)) {
                                            let criteria: ibas.ICriteria = new ibas.Criteria();
                                            if (typeof contract.user === "number") {
                                                let condition: ibas.ICondition = criteria.conditions.create();
                                                condition.alias = initialfantasy.bo.User.PROPERTY_DOCENTRY_NAME;
                                                condition.value = String(contract.user);
                                            } else {
                                                let condition: ibas.ICondition = criteria.conditions.create();
                                                condition.alias = initialfantasy.bo.User.PROPERTY_CODE_NAME;
                                                condition.value = String(contract.user);
                                            }
                                            let boRepository: initialfantasy.bo.BORepositoryInitialFantasy = new initialfantasy.bo.BORepositoryInitialFantasy();
                                            boRepository.fetchUser({
                                                criteria: criteria,
                                                onCompleted: (opRslt) => {
                                                    try {
                                                        if (opRslt.resultCode !== 0) {
                                                            throw new Error(opRslt.message);
                                                        }
                                                        if (opRslt.resultObjects.length === 0) {
                                                            throw new Error(ibas.i18n.prop("thirdpartyapp_not_found_user"));
                                                        }
                                                        this.user = opRslt.resultObjects.firstOrDefault();
                                                        this.applicationSetting.description = ibas.strings.format("{0} - {1}", this.applicationSetting.description, this.user.name);
                                                        this.show();
                                                    } catch (error) {
                                                        this.messages(error);
                                                    }
                                                }
                                            });
                                        } else {
                                            this.show();
                                        }
                                    } catch (error) {
                                        this.messages(error);
                                    }
                                }
                            });
                        } catch (error) {
                            this.messages(error);
                        }
                    }
                });
            }

            private saveData(): void {
                if (ibas.strings.isEmpty(this.user?.code)) {
                    throw new Error(ibas.i18n.prop("sys_invalid_parameter", "user"));
                }
                let formData: FormData;
                if (this.applicationSetting.settingItems.length > 0) {
                    formData = new FormData();
                    for (let item of this.applicationSetting.settingItems) {
                        formData.append(item.name, ibas.objects.isNull(item.value) ? "" : item.value);
                    }
                }
                if (ibas.objects.isNull(formData)) {
                    throw new Error(ibas.i18n.prop("sys_invalid_parameter", "settings"));
                }
                this.busy(true);
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.saveApplicationSetting({
                    application: this.applicationSetting.name,
                    user: this.user?.code,
                    formData: formData,
                    onCompleted: (opRslt) => {
                        try {
                            this.busy(false);
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            if (opRslt.resultObjects.length <= 0) {
                                throw new Error(ibas.i18n.prop("thirdpartyapp_not_found_application_setting", this.applicationSetting.name));
                            }
                            this.view.showSettingItems(this.applicationSetting.settingItems);
                            this.messages({
                                type: ibas.emMessageType.SUCCESS,
                                message: ibas.i18n.prop("shell_data_save") + ibas.i18n.prop("shell_sucessful"),
                                onCompleted: () => {
                                    this.fireCompleted(this.applicationSetting);
                                }
                            });
                        } catch (error) {
                            this.messages(error);
                        }
                    }
                });
            }
        }
        /** 视图-应用 */
        export interface IApplicationSettingServiceView extends ibas.IView {
            /** 保存事件 */
            saveDataEvent: Function;
            /** 显示配置 */
            showSetting(data: bo.ApplicationSetting): void;
            /** 显示配置内容 */
            showSettingItems(datas: bo.ApplicationSettingItem[]): void;
        }
        /** 应用配置选择服务映射 */
        export class ApplicationSettingServiceMapping extends ibas.ServiceMapping {
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationSettingService.APPLICATION_ID;
                this.name = ApplicationSettingService.APPLICATION_NAME;
                this.description = ibas.i18n.prop(this.name);
                this.proxy = ApplicationSettingServiceProxy;
            }
            /** 创建服务实例 */
            create(): ibas.IService<ibas.IServiceContract> {
                return new ApplicationSettingService();
            }
        }
    }
}
