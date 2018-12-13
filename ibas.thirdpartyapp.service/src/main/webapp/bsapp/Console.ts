/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
/// <reference path="../borep/index.ts" />
/// <reference path="./application/index.ts" />
/// <reference path="./user/index.ts" />
/// <reference path="./users/index.ts" />

namespace thirdpartyapp {
    export namespace app {
        /** 模块控制台 */
        export class Console extends ibas.ModuleConsole {
            /** 构造函数 */
            constructor() {
                super();
                this.id = CONSOLE_ID;
                this.name = CONSOLE_NAME;
                this.version = CONSOLE_VERSION;
                this.copyright = ibas.i18n.prop("shell_license");
            }
            private _navigation: ibas.IViewNavigation;
            /** 创建视图导航 */
            navigation(): ibas.IViewNavigation {
                return this._navigation;
            }
            /** 初始化 */
            protected registers(): void {
                // 注册功能
                this.register(new ApplicationFunc());
                this.register(new UserFunc());
                // 注册服务应用
                this.register(new ApplicationChooseServiceMapping());
                this.register(new UserChooseServiceMapping());
                // 注册常驻应用

            }
            /** 运行 */
            run(): void {
                // 加载语言-框架默认
                ibas.i18n.load(this.rootUrl + "resources/languages/thirdpartyapp.json");
                ibas.i18n.load(this.rootUrl + "resources/languages/bos.json");
                // 设置资源属性
                this.description = ibas.i18n.prop(this.name.toLowerCase());
                this.icon = ibas.i18n.prop(this.name.toLowerCase() + "_icon");
                // 先加载ui导航
                let uiModules: string[] = [];
                if (!ibas.config.get(ibas.CONFIG_ITEM_DISABLE_PLATFORM_VIEW, false)) {
                    if (this.plantform === ibas.emPlantform.PHONE) {
                        // 使用m类型视图
                        uiModules.push("index.ui.m");
                    }
                }
                // 默认使用视图
                if (uiModules.length === 0) {
                    // 使用c类型视图
                    uiModules.push("index.ui.c");
                }
                let that: this = this;
                this.loadUI(uiModules, function (ui: any): void {
                    // 设置导航
                    that._navigation = new ui.Navigation();
                    // 调用初始化
                    that.initialize();
                });
                // 保留基类方法
                super.run();
            }
        }

        /** 模块控制台 */
        export class ConsoleUsers extends ibas.ModuleConsole {
            /** 模块-标识 */
            static CONSOLE_ID: string = "e25dde1f-2d1f-48de-a519-a0f9e88090af";
            /** 模块-名称 */
            static CONSOLE_NAME: string = "ThirdPartyAppUsers";
            /** 模块-版本 */
            static CONSOLE_VERSION: string = "0.1.0";
            /** 构造函数 */
            constructor() {
                super();
                this.id = ConsoleUsers.CONSOLE_ID;
                this.name = ConsoleUsers.CONSOLE_NAME;
                this.version = ConsoleUsers.CONSOLE_VERSION;
                this.copyright = ibas.i18n.prop("shell_license");
            }
            private _navigation: ibas.IViewNavigation;
            /** 创建视图导航 */
            navigation(): ibas.IViewNavigation {
                return this._navigation;
            }
            /** 初始化 */
            protected registers(): void {
                // 不在使用此处注册
            }
            /** 运行 */
            run(): void {
                // 加载语言-框架默认
                ibas.i18n.load(this.rootUrl + "resources/languages/thirdpartyapp.json");
                ibas.i18n.load(this.rootUrl + "resources/languages/bos.json");
                // 设置资源属性
                this.description = ibas.i18n.prop(this.name.toLowerCase());
                this.icon = ibas.i18n.prop(this.name.toLowerCase() + "_icon");
                // 先加载ui导航
                let uiModules: string[] = [];
                if (!ibas.config.get(ibas.CONFIG_ITEM_DISABLE_PLATFORM_VIEW, false)) {
                    if (this.plantform === ibas.emPlantform.PHONE) {
                        // 使用m类型视图
                        uiModules.push("index.ui.m");
                    }
                }
                // 默认使用视图
                if (uiModules.length === 0) {
                    // 使用c类型视图
                    uiModules.push("index.ui.c");
                }
                let that: this = this;
                this.loadUI(uiModules, function (ui: any): void {
                    // 设置导航
                    that._navigation = new ui.Navigation();
                    // 加载用户报表
                    let boRepository: bo.BORepositoryThirdPartyApp = new bo.BORepositoryThirdPartyApp();
                    boRepository.fetchUserApplications({
                        user: ibas.variablesManager.getValue(ibas.VARIABLE_NAME_USER_CODE),
                        onCompleted(opRslt: ibas.IOperationResult<bo.UserApplication>): void {
                            if (opRslt.resultCode !== 0) {
                                ibas.logger.log(ibas.emMessageLevel.ERROR, opRslt.message);
                            }
                            for (let item of opRslt.resultObjects) {
                                that.register(new UserApplicationFunc(item));
                            }
                            // 通知初始化完成
                            that.fireInitialized();
                        }
                    });
                });
                // 保留基类方法
                super.run();
            }
            /** 设置仓库地址 */
            setRepository(address: string): boolean {
                address = ibas.urls.normalize(address);
                let repositoryName: string = ibas.strings.format(ibas.MODULE_REPOSITORY_NAME_TEMPLATE, CONSOLE_NAME);
                let configName: string = ibas.strings.format(ibas.CONFIG_ITEM_TEMPLATE_REMOTE_REPOSITORY_ADDRESS, repositoryName);
                ibas.config.set(configName, address);
                ibas.logger.log(ibas.emMessageLevel.DEBUG, "repository: register [{0}]'s default address [{1}].", repositoryName, address);
                return super.setRepository(address);
            }
        }
    }
}
