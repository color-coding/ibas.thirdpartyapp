/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        export class UserFunc extends ibas.ModuleFunction {

            /** 功能标识 */
            static FUNCTION_ID = "5396d9a5-eeaa-46f6-8f9a-380fcb01a8c7";
            /** 功能名称 */
            static FUNCTION_NAME = "thirdpartyapp_func_user";
            /** 构造函数 */
            constructor() {
                super();
                this.id = UserFunc.FUNCTION_ID;
                this.name = UserFunc.FUNCTION_NAME;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 默认功能 */
            default(): ibas.IApplication<ibas.IView> {
                let app: UserListApp = new UserListApp();
                app.navigation = this.navigation;
                return app;
            }
        }
    }
}
