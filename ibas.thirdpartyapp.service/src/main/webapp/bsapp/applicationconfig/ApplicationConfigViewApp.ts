/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        /** 查看应用-应用配置 */
        export class ApplicationConfigViewApp extends ibas.BOViewService<IApplicationConfigViewView, bo.ApplicationConfig> {
            /** 应用标识 */
            static APPLICATION_ID: string = "b6581e3b-5360-41a5-a7ba-29dbc51f7491";
            /** 应用名称 */
            static APPLICATION_NAME: string = "thirdpartyapp_app_applicationconfig_view";
            /** 业务对象编码 */
            static BUSINESS_OBJECT_CODE: string = bo.ApplicationConfig.BUSINESS_OBJECT_CODE;
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationConfigViewApp.APPLICATION_ID;
                this.name = ApplicationConfigViewApp.APPLICATION_NAME;
                this.boCode = ApplicationConfigViewApp.BUSINESS_OBJECT_CODE;
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
                    this.viewData = new bo.ApplicationConfig();
                    this.proceeding(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_data_created_new"));
                }
                this.view.showApplicationConfig(this.viewData);
                this.view.showApplicationConfigItems(this.viewData.applicationConfigItems.filterDeleted());
            }
            /** 编辑数据，参数：目标数据 */
            protected editData(): void {
                let app: ApplicationConfigEditApp = new ApplicationConfigEditApp();
                app.navigation = this.navigation;
                app.viewShower = this.viewShower;
                app.run(this.viewData);
            }
            run(): void;
            run(data: bo.ApplicationConfig): void;
            run(): void {
                if (arguments[0] instanceof bo.ApplicationConfig) {
                    let data: bo.ApplicationConfig = arguments[0];
                    if (data.isNew) {
                        this.viewData = data;
                        this.show();
                    } else {
                        let criteria: ibas.ICriteria = data.criteria();
                        if (criteria?.conditions.length > 0) {
                            // 有效的查询对象查询
                            let that: this = this;
                            let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                            boRepository.fetchApplicationConfig({
                                criteria: criteria,
                                onCompleted(opRslt: ibas.IOperationResult<bo.ApplicationConfig>): void {
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
                    condition.alias = bo.ApplicationConfig.PROPERTY_CODE_NAME;
                    condition.value = value;
                }
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.fetchApplicationConfig({
                    criteria: criteria,
                    onCompleted(opRslt: ibas.IOperationResult<bo.ApplicationConfig>): void {
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
        /** 视图-应用配置 */
        export interface IApplicationConfigViewView extends ibas.IBOViewView {
            /** 显示数据 */
            showApplicationConfig(data: bo.ApplicationConfig): void;
            /** 显示数据-应用配置-项目 */
            showApplicationConfigItems(datas: bo.ApplicationConfigItem[]): void;

        }
        /** 应用配置连接服务映射 */
        export class ApplicationConfigLinkServiceMapping extends ibas.BOLinkServiceMapping {
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationConfigViewApp.APPLICATION_ID;
                this.name = ApplicationConfigViewApp.APPLICATION_NAME;
                this.boCode = ApplicationConfigViewApp.BUSINESS_OBJECT_CODE;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 创建服务实例 */
            create(): ibas.IBOLinkService {
                return new ApplicationConfigViewApp();
            }
        }
    }
}
