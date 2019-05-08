/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace ui {
        export namespace c {
            /** 编辑视图-用户 */
            export class UserEditView extends ibas.BOEditView implements app.IUserEditView {
                /** 删除数据事件 */
                deleteDataEvent: Function;
                /** 新建数据事件，参数1：是否克隆 */
                createDataEvent: Function;
                /** 选择用户事件 */
                chooseUserEvent: Function;
                /** 选择应用事件 */
                chooseApplicationEvent: Function;

                /** 绘制视图 */
                draw(): any {
                    let that: this = this;
                    let formTop: sap.ui.layout.form.SimpleForm = new sap.ui.layout.form.SimpleForm("", {
                        editable: true,
                        content: [
                            new sap.ui.core.Title("", { text: ibas.i18n.prop("thirdpartyapp_title_general") }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_user_user") }),
                            new sap.extension.m.RepositoryInput("", {
                                showValueHelp: true,
                                repository: initialfantasy.bo.BO_REPOSITORY_INITIALFANTASY,
                                dataInfo: {
                                    type: ibas.boFactory.classOf(initialfantasy.bo.User.BUSINESS_OBJECT_CODE),
                                    key: "Code",
                                    text: "Name"
                                },
                                valueHelpRequest: function (): void {
                                    that.fireViewEvents(that.chooseUserEvent);
                                }
                            }).bindProperty("bindingValue", {
                                path: "user",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 8
                                })
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_user_application") }),
                            new sap.extension.m.RepositoryInput("", {
                                showValueHelp: true,
                                repository: bo.BORepositoryThirdPartyApp,
                                dataInfo: {
                                    type: bo.Application,
                                    key: "Code",
                                    text: "Name"
                                },
                                valueHelpRequest: function (): void {
                                    that.fireViewEvents(that.chooseApplicationEvent);
                                }
                            }).bindProperty("bindingValue", {
                                path: "application",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 30
                                })
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_user_activated") }),
                            new sap.extension.m.EnumSelect("", {
                                enumType: ibas.emYesNo
                            }).bindProperty("bindingValue", {
                                path: "activated",
                                type: new sap.extension.data.YesNo()
                            }),
                            new sap.ui.core.Title("", { text: ibas.i18n.prop("thirdpartyapp_title_others") }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_user_mappedid") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "mappedId",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 120
                                })
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_user_mappeduser") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "mappedUser",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 60
                                })
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_user_mappedpassword") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "mappedPassword",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 30
                                })
                            }),
                        ]
                    });
                    return this.page = new sap.extension.m.DataPage("", {
                        showHeader: false,
                        dataInfo: {
                            code: bo.User.BUSINESS_OBJECT_CODE,
                        },
                        subHeader: new sap.m.Toolbar("", {
                            content: [
                                new sap.m.Button("", {
                                    text: ibas.i18n.prop("shell_data_save"),
                                    type: sap.m.ButtonType.Transparent,
                                    icon: "sap-icon://save",
                                    press: function (): void {
                                        that.fireViewEvents(that.saveDataEvent);
                                    }
                                }),
                                new sap.m.Button("", {
                                    text: ibas.i18n.prop("shell_data_delete"),
                                    type: sap.m.ButtonType.Transparent,
                                    icon: "sap-icon://delete",
                                    press: function (): void {
                                        that.fireViewEvents(that.deleteDataEvent);
                                    }
                                }),
                                new sap.m.ToolbarSeparator(""),
                                new sap.m.MenuButton("", {
                                    text: ibas.strings.format("{0}/{1}",
                                        ibas.i18n.prop("shell_data_new"), ibas.i18n.prop("shell_data_clone")),
                                    icon: "sap-icon://create",
                                    type: sap.m.ButtonType.Transparent,
                                    menu: new sap.m.Menu("", {
                                        items: [
                                            new sap.m.MenuItem("", {
                                                text: ibas.i18n.prop("shell_data_new"),
                                                icon: "sap-icon://create",
                                                press: function (): void {
                                                    // 创建新的对象
                                                    that.fireViewEvents(that.createDataEvent, false);
                                                }
                                            }),
                                            new sap.m.MenuItem("", {
                                                text: ibas.i18n.prop("shell_data_clone"),
                                                icon: "sap-icon://copy",
                                                press: function (): void {
                                                    // 复制当前对象
                                                    that.fireViewEvents(that.createDataEvent, true);
                                                }
                                            }),
                                        ],
                                    })
                                }),
                            ]
                        }),
                        content: [
                            formTop,
                        ]
                    });
                }
                private page: sap.extension.m.Page;

                /** 显示数据 */
                showUser(data: bo.User): void {
                    this.page.setModel(new sap.extension.model.JSONModel(data));
                    // 改变页面状态
                    sap.extension.pages.changeStatus(this.page);
                }
            }
        }
    }
}
