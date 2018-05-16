/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        /** 应用-用户应用 */
        export class UserApplicationApp extends ibas.Application<IUserApplicationView> {
            /** 应用标识 */
            static APPLICATION_ID: string = "227b5f43-30c9-4f01-b71c-566f0ee6b40f";
            /** 应用名称 */
            static APPLICATION_NAME: string = "thirdpartyapp_app_userapplication";
            /** 构造函数 */
            constructor() {
                super();
                this.id = UserApplicationApp.APPLICATION_ID;
                this.name = UserApplicationApp.APPLICATION_NAME;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 注册视图 */
            protected registerView(): void {
                super.registerView();
                // 其他事件
            }
            /** 视图显示后 */
            protected viewShowed(): void {
                // 视图加载完成
            }
            application: bo.UserApplication;
            /** 运行 */
            run(): void {
                this.view.isInside = true;
                this.view.url = this.application.url;
                this.view.title = ibas.strings.format(this.description, this.application.name);
                super.run.apply(this, arguments);
            }
        }
        /** 视图-用户应用 */
        export interface IUserApplicationView extends ibas.IUrlView {
        }
    }
}