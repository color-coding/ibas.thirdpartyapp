/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
/// <reference path="../../index.d.ts" />
/// <reference path="./application/index.ts" />
/// <reference path="./applicationconfig/index.ts" />
/// <reference path="./usermapping/index.ts" />
/// <reference path="./others/index.ts" />

namespace thirdpartyapp {
    export namespace ui {
        /** 视图导航 */
        export class Navigation extends ibas.ViewNavigation {
            /**
             * 创建实例
             * @param id 应用id
             */
            protected newView(id: string): ibas.IView {
                let view: ibas.IView = null;
                switch (id) {
                    case app.ApplicationListApp.APPLICATION_ID:
                        view = new c.ApplicationListView();
                        break;
                    case app.ApplicationChooseApp.APPLICATION_ID:
                        view = new c.ApplicationChooseView();
                        break;
                    case app.ApplicationEditApp.APPLICATION_ID:
                        view = new c.ApplicationEditView();
                        break;
                    case app.ApplicationViewApp.APPLICATION_ID:
                        view = new c.ApplicationViewView();
                        break;
                    case app.ApplicationConfigListApp.APPLICATION_ID:
                        view = new c.ApplicationConfigListView();
                        break;
                    case app.ApplicationConfigChooseApp.APPLICATION_ID:
                        view = new c.ApplicationConfigChooseView();
                        break;
                    case app.ApplicationConfigEditApp.APPLICATION_ID:
                        view = new c.ApplicationConfigEditView();
                        break;
                    case app.ApplicationConfigViewApp.APPLICATION_ID:
                        view = new c.ApplicationConfigViewView();
                        break;
                    case app.UserApplicationApp.APPLICATION_ID:
                        view = new c.UserApplicationView();
                        break;
                    case app.UserMappingListApp.APPLICATION_ID:
                        view = new c.UserMappingListView();
                        break;
                    case app.ApplicationSettingService.APPLICATION_ID:
                        view = new c.ApplicationSettingServiceView();
                        break;
                    default:
                        break;
                }
                return view;
            }
        }
    }
}
