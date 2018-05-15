/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace app {
        export class ApplicationFunc extends ibas.ModuleFunction {

            /** 功能标识 */
            static FUNCTION_ID = "31de58eb-f32f-49bb-84ce-576b1c895d4c";
            /** 功能名称 */
            static FUNCTION_NAME = "thirdpartyapp_func_application";
            /** 构造函数 */
            constructor() {
                super();
                this.id = ApplicationFunc.FUNCTION_ID;
                this.name = ApplicationFunc.FUNCTION_NAME;
                this.description = ibas.i18n.prop(this.name);
            }
            /** 默认功能 */
            default(): ibas.IApplication<ibas.IView> {
                let app: ApplicationListApp = new ApplicationListApp();
                app.navigation = this.navigation;
                return app;
            }
        }
    }
}
