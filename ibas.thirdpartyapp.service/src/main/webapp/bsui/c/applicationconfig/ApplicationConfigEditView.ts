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
            /** 编辑视图-应用配置 */
            export class ApplicationConfigEditView extends ibas.BOEditView implements app.IApplicationConfigEditView {
                /** 删除数据事件 */
                deleteDataEvent: Function;
                /** 新建数据事件，参数1：是否克隆 */
                createDataEvent: Function;
                /** 添加应用配置-项目事件 */
                addApplicationConfigItemEvent: Function;
                /** 删除应用配置-项目事件 */
                removeApplicationConfigItemEvent: Function;

                /** 绘制视图 */
                draw(): any {
                    let that: this = this;
                    let formTop: sap.ui.layout.form.SimpleForm = new sap.ui.layout.form.SimpleForm("", {
                        editable: true,
                        content: [
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_applicationconfig_code") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "code",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 15
                                }),
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_applicationconfig_name") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "name",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 100
                                }),
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_applicationconfig_group") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "group",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 10
                                }),
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_applicationconfig_remarks") }),
                            new sap.extension.m.TextArea("", {
                                rows: 4,
                            }).bindProperty("bindingValue", {
                                path: "remarks",
                                type: new sap.extension.data.Alphanumeric()
                            }),
                        ]
                    });
                    this.tableApplicationConfigItem = new sap.extension.table.DataTable("", {
                        rowActionCount: 1,
                        chooseType: ibas.emChooseType.NONE,
                        visibleRowCountMode: sap.ui.table.VisibleRowCountMode.Auto,
                        dataInfo: {
                            code: bo.ApplicationConfig.BUSINESS_OBJECT_CODE,
                            name: bo.ApplicationConfigItem.name
                        },
                        rowActionTemplate: new sap.ui.table.RowAction("", {
                            items: [
                                new sap.ui.table.RowActionItem("", {
                                    icon: "sap-icon://decline",
                                    type: sap.ui.table.RowActionType.Delete,
                                    press: function (oEvent: sap.ui.base.Event): void {
                                        let source: any = oEvent.getSource();
                                        if (source instanceof sap.ui.core.Element) {
                                            that.fireViewEvents(that.removeApplicationConfigItemEvent, source.getBindingContext().getObject());
                                        }
                                    },
                                })
                            ]
                        }),
                        rows: "{/rows}",
                        columns: [
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationconfigitem_name"),
                                template: new sap.extension.m.Input("", {
                                }).bindProperty("bindingValue", {
                                    path: "name",
                                    type: new sap.extension.data.Alphanumeric({
                                        maxLength: 30
                                    }),
                                }),
                                width: "15%",
                            }),
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationconfigitem_description"),
                                template: new sap.extension.m.Input("", {
                                }).bindProperty("bindingValue", {
                                    path: "description",
                                    type: new sap.extension.data.Alphanumeric({
                                        maxLength: 100
                                    }),
                                }),
                                width: "25%",
                            }),
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationconfigitem_category"),
                                template: new sap.extension.m.EnumSelect("", {
                                    enumType: bo.emConfigItemCategory,
                                }).bindProperty("bindingValue", {
                                    path: "category",
                                    type: new sap.extension.data.Enum({
                                        enumType: bo.emConfigItemCategory,
                                    }),
                                }),
                                width: "8rem",
                            }),
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationconfigitem_foruser"),
                                template: new sap.extension.m.CheckBox("", {
                                }).bindProperty("bindingValue", {
                                    path: "forUser",
                                    type: new sap.extension.data.YesNo(),
                                }),
                                width: "5rem",
                            }),
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationconfigitem_value"),
                                template: new sap.extension.m.Input("", {
                                }).bindProperty("bindingValue", {
                                    path: "value",
                                    type: new sap.extension.data.Alphanumeric({
                                        maxLength: 200
                                    }),
                                }),
                                width: "auto",
                            }),
                        ]
                    });
                    return new sap.extension.m.DataPage("", {
                        showHeader: false,
                        dataInfo: {
                            code: bo.ApplicationConfig.BUSINESS_OBJECT_CODE,
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
                                new sap.m.ToolbarSpacer(""),
                                new sap.m.Button("", {
                                    text: ibas.i18n.prop("shell_data_add_line"),
                                    type: sap.m.ButtonType.Transparent,
                                    icon: "sap-icon://add",
                                    press: function (): void {
                                        that.fireViewEvents(that.addApplicationConfigItemEvent);
                                    },
                                })
                            ]
                        }),
                        content: [
                            new sap.m.SplitContainer("", {
                                masterPages: [
                                    this.page = new sap.extension.m.Page("", {
                                        showHeader: false,
                                        floatingFooter: true,
                                        content: [
                                            formTop
                                        ]
                                    })
                                ],
                                detailPages: [
                                    new sap.m.Page("", {
                                        showHeader: false,
                                        content: [
                                            this.tableApplicationConfigItem
                                        ],
                                    })
                                ],
                            })
                        ]
                    });
                }

                private page: sap.extension.m.Page;
                private tableApplicationConfigItem: sap.extension.table.Table;

                /** 显示数据 */
                showApplicationConfig(data: bo.ApplicationConfig): void {
                    this.page.setModel(new sap.extension.model.JSONModel(data));
                    // 改变页面状态
                    sap.extension.pages.changeStatus(this.page);
                }
                /** 显示数据-应用配置-项目 */
                showApplicationConfigItems(datas: bo.ApplicationConfigItem[]): void {
                    this.tableApplicationConfigItem.setModel(new sap.extension.model.JSONModel({ rows: datas }));
                }
            }
        }
    }
}
