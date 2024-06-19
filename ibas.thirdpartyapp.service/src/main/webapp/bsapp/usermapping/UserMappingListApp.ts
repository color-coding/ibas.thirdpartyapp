/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        export class MappingData {
            constructor(mapping: bo.UserMapping, app: bo.Application) {
                this.mapping = mapping;
                this.app = app;
            }
            app: bo.Application;
            mapping: bo.UserMapping;
        }
        /** 列表应用-用户映射 */
        export class UserMappingListApp extends ibas.BOListApplication<IUserMappingListView, initialfantasy.bo.User> {
            /** 应用标识 */
            static APPLICATION_ID: string = "ac4e6017-50f7-40b1-b4c6-832363fa92c6";
            /** 应用名称 */
            static APPLICATION_NAME: string = "thirdpartyapp_app_usermapping_list";
            /** 业务对象编码 */
            static BUSINESS_OBJECT_CODE: string = bo.UserMapping.BUSINESS_OBJECT_CODE;
            /** 构造函数 */
            constructor() {
                super();
                this.id = UserMappingListApp.APPLICATION_ID;
                this.name = UserMappingListApp.APPLICATION_NAME;
                this.boCode = UserMappingListApp.BUSINESS_OBJECT_CODE;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 注册视图 */
            protected registerView(): void {
                super.registerView();
                // 其他事件
                this.view.viewDataEvent = this.viewData;
                this.view.connectApplicationEvent = this.connectApplication;
                this.view.disconnectApplicationEvent = this.disconnectApplication;
            }
            /** 视图显示后 */
            protected viewShowed(): void {
                // 视图加载完成
                super.viewShowed();
                if (this.appMaps === undefined) {
                    this.appMaps = null;
                    let criteria: ibas.Criteria = new ibas.Criteria();
                    criteria.noChilds = true;
                    let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                    boRepository.fetchApplication({
                        criteria: criteria,
                        onCompleted: (opRslt) => {
                            try {
                                if (opRslt.resultCode !== 0) {
                                    throw new Error(opRslt.message);
                                }
                                this.appMaps = new Map<string, bo.Application>();
                                for (let item of opRslt.resultObjects) {
                                    this.appMaps.set(item.code.toUpperCase(), item);
                                }
                            } catch (error) {
                                this.proceeding(error);
                            }
                        }
                    });
                }
            }
            /** 查询数据 */
            protected fetchData(criteria: ibas.ICriteria): void {
                this.busy(true);
                if (!ibas.objects.isNull(criteria)) {
                    if (criteria.conditions.firstOrDefault(
                        c => c.alias === initialfantasy.bo.User.PROPERTY_DOCENTRY_NAME
                            && c.operation === ibas.emConditionOperation.GRATER_THAN
                            && c.value === "0"
                    ) === null) {
                        if (criteria.conditions.length > 2) {
                            criteria.conditions.firstOrDefault().bracketOpen++;
                            criteria.conditions.lastOrDefault().bracketClose++;
                        }
                        let condition: ibas.ICondition = criteria.conditions.create();
                        condition.alias = initialfantasy.bo.User.PROPERTY_DOCENTRY_NAME;
                        condition.operation = ibas.emConditionOperation.GRATER_THAN;
                        condition.value = "0";
                    }
                }
                let that: this = this;
                let boRepository: initialfantasy.bo.BORepositoryInitialFantasy = new initialfantasy.bo.BORepositoryInitialFantasy();
                boRepository.fetchUser({
                    criteria: criteria,
                    onCompleted(opRslt: ibas.IOperationResult<initialfantasy.bo.User>): void {
                        try {
                            that.busy(false);
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            if (!that.isViewShowed()) {
                                // 没显示视图，先显示
                                that.show();
                            }
                            if (opRslt.resultObjects.length === 0) {
                                that.proceeding(ibas.emMessageType.INFORMATION, ibas.i18n.prop("shell_data_fetched_none"));
                            }
                            that.view.showData(opRslt.resultObjects);
                        } catch (error) {
                            that.messages(error);
                        }
                    }
                });
                this.proceeding(ibas.emMessageType.INFORMATION, ibas.i18n.prop("shell_fetching_data"));
            }
            /** 新建数据 */
            protected newData(): void {
            }
            private appMaps: Map<string, bo.Application>;
            /** 查看数据，参数：目标数据 */
            protected viewData(data: initialfantasy.bo.User | string): void {
                if (ibas.objects.isNull(data)) {
                    this.messages(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_please_chooose_data",
                        ibas.i18n.prop("shell_data_view")
                    ));
                    return;
                }
                if (ibas.objects.isNull(this.appMaps)) {
                    this.messages(ibas.emMessageType.WARNING, ibas.i18n.prop("thirdpartyapp_initializing_please_wait"));
                    return;
                }
                let criteria: ibas.Criteria = new ibas.Criteria();
                let condition: ibas.ICondition = criteria.conditions.create();
                condition.alias = bo.UserMapping.PROPERTY_USER_NAME;
                condition.value = data instanceof initialfantasy.bo.User ? data.code : data;
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.fetchUserMapping({
                    criteria: criteria,
                    onCompleted: (opRslt) => {
                        try {
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            let mappingDatas: ibas.IList<MappingData> = new ibas.ArrayList<MappingData>();
                            for (let item of opRslt.resultObjects) {
                                let application: any = this.appMaps.get(item.application.toUpperCase());
                                if (!ibas.objects.isNull(application)) {
                                    mappingDatas.add(new MappingData(item, application));
                                }
                            }
                            this.view.showUserMappings(mappingDatas);
                        } catch (error) {
                            this.messages(error);
                        }
                    }
                });
            }
            private connectApplication(user: initialfantasy.bo.User): void {
                if (ibas.objects.isNull(user)) {
                    this.messages(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_please_chooose_data",
                        ibas.i18n.prop("bo_user")
                    ));
                    return;
                }
                let that: this = this;
                ibas.servicesManager.runChooseService<bo.Application>({
                    boCode: bo.Application.BUSINESS_OBJECT_CODE,
                    chooseType: ibas.emChooseType.SINGLE,
                    criteria: [
                        new ibas.Condition(bo.Application.PROPERTY_ACTIVATED_NAME, ibas.emConditionOperation.EQUAL, ibas.emYesNo.YES)
                    ],
                    onCompleted(selecteds: ibas.IList<bo.Application>): void {
                        let selected: bo.Application = selecteds.firstOrDefault();
                        ibas.servicesManager.runApplicationService<IApplicationSettingContract, bo.ApplicationSetting>({
                            proxy: new ApplicationSettingServiceProxy({
                                application: selected.code,
                                user: user.code,
                            }),
                            onCompleted: (result) => {
                                if (!ibas.objects.isNull(result)) {
                                    that.viewData(user);
                                }
                            }
                        });
                    }
                });
            }
            private disconnectApplication(data: MappingData): void {
                if (ibas.objects.isNull(data)) {
                    this.messages(ibas.emMessageType.WARNING, ibas.i18n.prop("shell_please_chooose_data",
                        ibas.i18n.prop("bo_usermapping")
                    ));
                    return;
                }
                this.busy(true);
                let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                boRepository.removeApplicationSetting({
                    application: data.mapping.application,
                    user: data.mapping.user,
                    onCompleted: (opRslt) => {
                        try {
                            this.busy(false);
                            if (opRslt.resultCode !== 0) {
                                throw new Error(opRslt.message);
                            }
                            this.viewData(data.mapping.user);
                            this.messages(ibas.emMessageType.SUCCESS,
                                ibas.i18n.prop("shell_data_remove") + ibas.i18n.prop("shell_sucessful"));
                        } catch (error) {
                            this.messages(error);
                        }
                    }
                });

            }
        }
        /** 视图-用户映射 */
        export interface IUserMappingListView extends ibas.IBOListView {
            /** 显示数据 */
            showData(datas: initialfantasy.bo.User[]): void;
            /** 显示数据 */
            showUserMappings(datas: MappingData[]): void;
            /** 连接应用事件 */
            connectApplicationEvent: Function;
            /** 断开应用事件 */
            disconnectApplicationEvent: Function;
        }
    }
}
