/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        export class ApplicationConfigFunc extends ibas.ModuleFunction {
            /** 功能标识 */
            static FUNCTION_ID = "38b832e2-d5d1-4e7b-a63a-b0aae1c6ad70";
            /** 功能名称 */
            static FUNCTION_NAME = "thirdpartyapp_func_applicationconfig";
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationConfigFunc.FUNCTION_ID;
                this.name = ApplicationConfigFunc.FUNCTION_NAME;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 默认功能 */
            default(): ibas.IApplication<ibas.IView> {
                let app: ApplicationConfigListApp = new ApplicationConfigListApp();
                app.navigation = this.navigation;
                return app;
            }
        }
    }
}
