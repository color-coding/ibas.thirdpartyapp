/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        export class UserMappingFunc extends ibas.ModuleFunction {
            /** 功能标识 */
            static FUNCTION_ID = "1ba73bce-f4ae-44d1-9bcc-7322d16ec6f2";
            /** 功能名称 */
            static FUNCTION_NAME = "thirdpartyapp_func_usermapping";
            /** 构造函数 */
            constructor() {
                super();
                this.id = UserMappingFunc.FUNCTION_ID;
                this.name = UserMappingFunc.FUNCTION_NAME;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 默认功能 */
            default(): ibas.IApplication<ibas.IView> {
                let app: UserMappingListApp = new UserMappingListApp();
                app.navigation = this.navigation;
                return app;
            }
        }
    }
}
