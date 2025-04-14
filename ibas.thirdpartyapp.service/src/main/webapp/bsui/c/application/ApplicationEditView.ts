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
            /** 编辑视图-应用 */
            export class ApplicationEditView extends ibas.BOEditView implements app.IApplicationEditView {
                /** 删除数据事件 */
                deleteDataEvent: Function;
                /** 新建数据事件，参数1：是否克隆 */
                createDataEvent: Function;
                /** 选择应用配置 */
                chooseApplicationConfigEvent: Function;
                /** 上传图片事件 */
                uploadPictureEvent: Function;
                /** 绘制视图 */
                draw(): any {
                    let that: this = this;
                    let formTop: sap.ui.layout.form.SimpleForm = new sap.ui.layout.form.SimpleForm("", {
                        editable: true,
                        content: [
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_code") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "code",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 20
                                })
                            }).bindProperty("editable", {
                                path: "series",
                                formatter(data: any): any {
                                    return data > 0 ? false : true;
                                }
                            }),
                            new sap.extension.m.SeriesSelect("", {
                                objectCode: bo.BO_CODE_APPLICATION,
                            }).bindProperty("bindingValue", {
                                path: "series",
                                type: new sap.extension.data.Numeric()
                            }).bindProperty("editable", {
                                path: "isNew",
                                formatter(data: any): any {
                                    return data === false ? false : true;
                                }
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_name") }),
                            new sap.extension.m.Input("", {
                            }).bindProperty("bindingValue", {
                                path: "name",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 100
                                })
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_activated") }),
                            new sap.extension.m.EnumSelect("", {
                                enumType: ibas.emYesNo
                            }).bindProperty("bindingValue", {
                                path: "activated",
                                type: new sap.extension.data.YesNo()
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_category") }),
                            new sap.extension.m.PropertySelect("", {
                                dataInfo: {
                                    code: bo.Application.BUSINESS_OBJECT_CODE,
                                },
                                propertyName: "category",
                            }).bindProperty("bindingValue", {
                                path: "category",
                                type: new sap.extension.data.Alphanumeric({
                                    maxLength: 8
                                })
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_picture"), }),
                            new sap.m.FlexBox("", {
                                width: "100%",
                                justifyContent: sap.m.FlexJustifyContent.Start,
                                renderType: sap.m.FlexRendertype.Bare,
                                items: [
                                    new sap.extension.m.Input("", {
                                        showValueHelp: true,
                                        valueHelpRequest: function (): void {
                                            ibas.files.open((files) => {
                                                if (files.length > 0) {
                                                    let fileData: FormData = new FormData();
                                                    fileData.append("file", files[0], encodeURI(files[0].name));
                                                    that.fireViewEvents(that.uploadPictureEvent, fileData);
                                                }
                                            }, { accept: "image/gif,image/jpeg,image/jpg,image/png" });
                                        }
                                    }).bindProperty("bindingValue", {
                                        path: "picture",
                                        type: new sap.extension.data.Alphanumeric({
                                            maxLength: 250
                                        })
                                    }),
                                    new sap.m.Button("", {
                                        width: "auto",
                                        icon: "sap-icon://show",
                                        tooltip: {
                                            path: "picture",
                                            type: new sap.extension.data.Alphanumeric({
                                                maxLength: 250
                                            })
                                        },
                                        press: function (this: sap.m.Button): void {
                                            new sap.m.LightBox("", {
                                                imageContent: [
                                                    new sap.m.LightBoxItem("", {
                                                        subtitle: undefined,
                                                        title: undefined,
                                                        imageSrc: new bo.BORepositoryThirdPartyApp().toUrl(String(this.getTooltip())),
                                                    })
                                                ]
                                            }).open();
                                        }
                                    }).addStyleClass("sapUiTinyMarginBegin"),
                                ]
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_remarks") }),
                            new sap.extension.m.TextArea("", {
                                rows: 4,
                            }).bindProperty("bindingValue", {
                                path: "remarks",
                                type: new sap.extension.data.Alphanumeric()
                            }),
                        ]
                    });
                    this.table = new sap.extension.table.Table("", {
                        rowActionCount: 1,
                        chooseType: ibas.emChooseType.NONE,
                        visibleRowCountMode: sap.ui.table.VisibleRowCountMode.Auto,
                        rowActionTemplate: new sap.ui.table.RowAction("", {
                            items: [
                                new sap.ui.table.RowActionItem("", {
                                    icon: "sap-icon://browse-folder",
                                    press: function (oEvent: sap.ui.base.Event): void {
                                        let source: any = oEvent.getSource();
                                        if (source instanceof sap.ui.core.Element) {
                                            let data: any = source.getBindingContext().getObject();
                                            if (data instanceof bo.ApplicationSettingItem) {
                                                ibas.files.open((files) => {
                                                    if (files.length > 0) {
                                                        data.value = files[0];
                                                    }
                                                });
                                            }
                                        }
                                    },
                                }).bindProperty("visible", {
                                    path: "category",
                                    formatter(data: bo.emConfigItemCategory): boolean {
                                        return data === bo.emConfigItemCategory.FILE ? true : false;
                                    }
                                }),
                            ]
                        }),
                        rows: "{/rows}",
                        columns: [
                            new sap.extension.table.Column("", {
                                label: ibas.i18n.prop("bo_applicationsettingitem_name"),
                                template: new sap.extension.m.Text("", {
                                }).bindProperty("bindingValue", {
                                    path: "name",
                                    type: new sap.extension.data.Alphanumeric(),
                                }),
                                width: "20%",
                            }),
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationsettingitem_description"),
                                template: new sap.extension.m.Text("", {
                                }).bindProperty("bindingValue", {
                                    path: "description",
                                    type: new sap.extension.data.Alphanumeric(),
                                }),
                                width: "25%",
                            }),
                            new sap.extension.table.DataColumn("", {
                                label: ibas.i18n.prop("bo_applicationsettingitem_value"),
                                template: new sap.extension.m.Input("", {
                                    autocomplete: false,
                                }).bindProperty("bindingValue", {
                                    path: "value",
                                    type: new sap.extension.data.Unknown({
                                        formatValue(oValue: any, sInternalType: string): any {
                                            if (oValue instanceof File && sInternalType === "string") {
                                                return oValue.name;
                                            }
                                            return oValue;
                                        },
                                        parseValue(oValue: any, sInternalType: string): any {
                                            return oValue;
                                        }
                                    }),
                                }).bindProperty("editable", {
                                    path: "category",
                                    formatter(data: bo.emConfigItemCategory): boolean {
                                        return data !== bo.emConfigItemCategory.FILE ? true : false;
                                    }
                                }).bindProperty("type", {
                                    path: "category",
                                    formatter(data: bo.emConfigItemCategory): sap.m.InputType {
                                        if (ibas.config.get(ibas.CONFIG_ITEM_DEBUG_MODE, false)) {
                                            return sap.m.InputType.Text;
                                        }
                                        return data === bo.emConfigItemCategory.PASSWORD ? sap.m.InputType.Password : sap.m.InputType.Text;
                                    }
                                }),
                                width: "auto",
                            }),
                        ],
                    });
                    return new sap.extension.m.DataPage("", {
                        showHeader: false,
                        dataInfo: {
                            code: bo.Application.BUSINESS_OBJECT_CODE,
                        },
                        subHeader: new sap.m.Toolbar("", {
                            content: [
                                new sap.m.Button("", {
                                    text: ibas.i18n.prop("shell_data_save"),
                                    type: sap.m.ButtonType.Transparent,
                                    icon: "sap-icon://save",
                                    press: function (): void {
                                        let model: any = that.table.getModel();
                                        if (model instanceof sap.extension.model.JSONModel) {
                                            let data: any = model.getData();
                                            if (data && data.rows instanceof Array) {
                                                let formData: FormData = null;
                                                for (let item of data.rows) {
                                                    if (item instanceof bo.ApplicationSettingItem) {
                                                        if (ibas.objects.isNull(item.value)) {
                                                            continue;
                                                        }
                                                        if (formData === null) {
                                                            formData = new FormData();
                                                        }
                                                        formData.append(item.name, item.value);
                                                    }
                                                }
                                                that.fireViewEvents(that.saveDataEvent, formData);
                                            }
                                        }

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
                                    text: ibas.i18n.prop("shell_data_new") + ibas.i18n.prop("bo_application_settings"),
                                    type: sap.m.ButtonType.Transparent,
                                    icon: "sap-icon://add-equipment",
                                    press: function (): void {
                                        that.fireViewEvents(that.chooseApplicationConfigEvent);
                                    }
                                }),
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
                                            this.table
                                        ],
                                    })
                                ],
                            })
                        ]
                    });
                }
                private page: sap.extension.m.Page;
                private table: sap.extension.table.Table;
                /** 显示数据 */
                showApplication(data: bo.Application): void {
                    this.page.setModel(new sap.extension.model.JSONModel(data));
                    // 改变页面状态
                    sap.extension.pages.changeStatus(this.page);
                }
                /** 显示可用配置 */
                showApplicationSettingItems(datas: bo.ApplicationSettingItem[]): void {
                    this.table.setModel(new sap.extension.model.JSONModel({ rows: datas }));
                }
            }
        }
    }
}
