/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        /**
         * 用户应用功能
         */
        export class UserApplicationFunc extends ibas.ModuleFunction {
            /** 功能标识 */
            static FUNCTION_ID_PREFIX = "cb80bc03-4f03-449c-9033-";
            /** 构造函数 */
            constructor(application: bo.UserApplication) {
                super();
                this.application = application;
                this.id = UserApplicationFunc.FUNCTION_ID_PREFIX + ibas.strings.fill(application.code, 12, "0");
                this.name = application.code;
                this.description = application.name;
            }
            private application: bo.UserApplication;
            /** 默认功能 */
            default(): ibas.IApplication<ibas.IView> {
                let app: UserApplicationApp = new UserApplicationApp();
                app.navigation = this.navigation;
                app.application = this.application;
                return app;
            }
        }
    }
}