/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        /** 查看应用-应用 */
        export class ApplicationViewApp extends ibas.BOViewService<IApplicationViewView, bo.Application> {
            /** 应用标识 */
            static APPLICATION_ID: string = "6ef40bc6-09d0-4c3f-9bd1-f301e5249543";
            /** 应用名称 */
            static APPLICATION_NAME: string = "thirdpartyapp_app_application_view";
            /** 业务对象编码 */
            static BUSINESS_OBJECT_CODE: string = bo.Application.BUSINESS_OBJECT_CODE;
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationViewApp.APPLICATION_ID;
                this.name = ApplicationViewApp.APPLICATION_NAME;
                this.boCode = ApplicationViewApp.BUSINESS_OBJECT_CODE;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 注册视图 */
            protected registerView(): void {
                super.registerView();
                // 其他事件
                this.view.editDataEvent = this.editData;
            }
            /** 视图显示后 */
            protected viewShowed(): void {
                // 视图加载完成，基类方法更新地址
                super.viewShowed();
                if (ibas.objects.isNull(this.viewData)) {
                    // 创建编辑对象实例
                    this.viewData = new bo.Application();
                    this.proceeding(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_data_created_new"));
                } else {
                    this.view.showApplication(this.viewData);
                    if (this.viewData.isNew) {
                        this.view.showApplicationSettingItems([]);
                    } else {
                        let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                        boRepository.fetchApplicationSetting({
                            application: this.viewData.code,
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
                this.view.showApplication(this.viewData);
            }
            /** 编辑数据，参数：目标数据 */
            protected editData(): void {
                let app: ApplicationEditApp = new ApplicationEditApp();
                app.navigation = this.navigation;
                app.viewShower = this.viewShower;
                app.run(this.viewData);
            }
            run(): void;
            run(data: bo.Application): void;
            run(): void {
                if (arguments[0] instanceof bo.Application) {
                    let data: bo.Application = arguments[0];
                    if (data.isNew) {
                        this.viewData = data;
                        this.show();
                    } else {
                        let criteria: ibas.ICriteria = data.criteria();
                        if (criteria?.conditions.length > 0) {
                            // 有效的查询对象查询
                            let that: this = this;
                            let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                            boRepository.fetchApplication({
                                criteria: criteria,
                                onCompleted(opRslt: ibas.IOperationResult<bo.Application>): void {
                                    try {
                                        if (opRslt.resultCode !== 0) {
                                            throw new Error(opRslt.message);
                                        }
                                        if (opRslt.resultObjects.length > 0) {
                                            that.viewData = opRslt.resultObjects.firstOrDefault();
                                            that.show();
                                        } else {
                                            that.messages({
                                                type: ibas.emMessageType.WARNING,
                                                message: ibas.i18n.prop("shell_data_deleted_and_created"),
                                                onCompleted(): void {
                                                    that.show();
                                                }
                                            });
                                        }
                                    } catch (error) {
                                        that.messages(error);
                                    }
                                }
                            });
                        } else {
                            super.run.apply(this, arguments);
                        }
                    }
                } else {
                    super.run.apply(this, arguments);
                }
            }
            /** 查询数据 */
            protected fetchData(criteria: ibas.ICriteria | string): void {
                this.busy(true);
                let that: this = this;
                if (typeof criteria === "string") {
                    let condition: ibas.ICondition;
                    let value: string = criteria;
                    criteria = new ibas.Criteria();
                    criteria.result = 1;
                    condition = criteria.conditions.create();
                    condition.alias = bo.Application.PROPERTY_CODE_NAME;
                    condition.value = value;
                }
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.fetchApplication({
                    criteria: criteria,
                    onCompleted(opRslt: ibas.IOperationResult<bo.Application>): void {
                        try {
                            that.busy(false);
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            that.viewData = opRslt.resultObjects.firstOrDefault();
                            if (!that.isViewShowed()) {
                                // 没显示视图，先显示
                                that.show();
                            } else {
                                that.viewShowed();
                            }
                        } catch (error) {
                            that.messages(error);
                        }
                    }
                });
                this.proceeding(ibas.emMessageType.INFORMATION, ibas.i18n.prop("shell_fetching_data"));
            }
        }
        /** 视图-应用 */
        export interface IApplicationViewView extends ibas.IBOViewView {
            /** 显示数据 */
            showApplication(data: bo.Application): void;
            /** 显示可用配置 */
            showApplicationSettingItems(datas: bo.ApplicationSettingItem[]): void;

        }
        /** 应用连接服务映射 */
        export class ApplicationLinkServiceMapping extends ibas.BOLinkServiceMapping {
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationViewApp.APPLICATION_ID;
                this.name = ApplicationViewApp.APPLICATION_NAME;
                this.boCode = ApplicationViewApp.BUSINESS_OBJECT_CODE;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 创建服务实例 */
            create(): ibas.IBOLinkService {
                return new ApplicationViewApp();
            }
        }
    }
}
