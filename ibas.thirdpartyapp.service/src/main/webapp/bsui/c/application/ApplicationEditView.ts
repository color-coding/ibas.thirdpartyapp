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
                /** 上传证书事件 */
                uploadCertificateEvent: Function;
                /** 上传公钥事件 */
                uploadAppKeyEvent: Function;
                /** 上传私钥事件 */
                uploadAppSecretEvent: Function;

                /** 绘制视图 */
                draw(): any {
                    let that: this = this;
                    let formTop: sap.ui.layout.form.SimpleForm = new sap.ui.layout.form.SimpleForm("", {
                        editable: true,
                        content: [
                            new sap.ui.core.Title("", { text: ibas.i18n.prop("thirdpartyapp_title_general") }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_code") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "code",
                            }).bindProperty("editable", {
                                path: "series",
                                formatter(data: any): any {
                                    return data > 0 ? false : true;
                                }
                            }),
                            new sap.m.ex.SeriesSelect("", {
                                objectCode: ibas.config.applyVariables(bo.BO_CODE_APPLICATION),
                                bindingValue: {
                                    path: "series",
                                    type: "sap.ui.model.type.Integer",
                                }
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_name") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "name",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_activated") }),
                            new sap.m.Select("", {
                                items: openui5.utils.createComboBoxItems(ibas.emYesNo),
                            }).bindProperty("selectedKey", {
                                path: "activated",
                                type: "sap.ui.model.type.Integer",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_appid") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "appId",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_appurl") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "appUrl",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_apiurl") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "apiUrl",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_account") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "account",
                            }),
                            new sap.ui.core.Title("", { text: ibas.i18n.prop("thirdpartyapp_title_others") }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_appkey") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "appKey",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_appsecret") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "appSecret",
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_certificate") }),
                            new sap.ui.unified.FileUploader("", {
                                name: "file",
                                width: "100%",
                                placeholder: ibas.i18n.prop("shell_browse"),
                                change(event: sap.ui.base.Event): void {
                                    if (ibas.objects.isNull(event.getParameters())
                                        || ibas.objects.isNull(event.getParameters().files)
                                        || event.getParameters().files.length === 0) {
                                        return;
                                    }
                                    let fileData: FormData = new FormData();
                                    fileData.append("file", event.getParameters().files[0], encodeURI(event.getParameters().newValue));
                                    that.application.viewShower.messages({
                                        type: ibas.emMessageType.QUESTION,
                                        title: that.application.description,
                                        actions: [
                                            ibas.emMessageAction.YES,
                                            ibas.emMessageAction.NO
                                        ],
                                        message: ibas.i18n.prop("thirdpartyapp_continue_upload_certificate"),
                                        onCompleted(action: ibas.emMessageAction): void {
                                            if (action === ibas.emMessageAction.YES) {
                                                that.fireViewEvents(that.uploadCertificateEvent, fileData);
                                            }
                                        }
                                    });
                                }
                            }).bindProperty("value", {
                                path: "certificate"
                            }),
                            new sap.m.Label("", { text: ibas.i18n.prop("bo_application_receivingurl") }),
                            new sap.m.Input("", {
                                type: sap.m.InputType.Text,
                            }).bindProperty("value", {
                                path: "receivingUrl",
                            }),
                        ]
                    });
                    this.layoutMain = new sap.ui.layout.VerticalLayout("", {
                        width: "100%",
                        content: [
                            formTop,
                        ]
                    });
                    this.page = new sap.m.Page("", {
                        showHeader: false,
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
                                    text: ibas.strings.format("{0}",
                                        ibas.i18n.prop("thirdpartyapp_upload_key_file")),
                                    icon: "sap-icon://two-keys",
                                    press: function (event: any): void {
                                        let popover: sap.m.Popover = new sap.m.Popover("", {
                                            showHeader: false,
                                            placement: sap.m.PlacementType.Bottom,
                                            content: [
                                                new sap.ui.unified.FileUploader("", {
                                                    buttonOnly: true,
                                                    multiple: false,
                                                    uploadOnChange: false,
                                                    width: "100%",
                                                    style: "Transparent",
                                                    icon: "sap-icon://key",
                                                    buttonText: ibas.i18n.prop("bo_application_appkey"),
                                                    change: function (oEvent: sap.ui.base.Event): void {
                                                        popover.close();
                                                        let files: File[] = oEvent.getParameter("files");
                                                        if (ibas.objects.isNull(files) || files.length === 0) {
                                                            return;
                                                        }
                                                        let fileData: FormData = new FormData();
                                                        fileData.append("file", files[0]);
                                                        that.fireViewEvents(that.uploadAppKeyEvent, fileData);
                                                    },
                                                }),
                                                new sap.ui.unified.FileUploader("", {
                                                    buttonOnly: true,
                                                    multiple: false,
                                                    uploadOnChange: false,
                                                    width: "100%",
                                                    style: "Transparent",
                                                    icon: "sap-icon://primary-key",
                                                    buttonText: ibas.i18n.prop("bo_application_appsecret"),
                                                    change: function (oEvent: sap.ui.base.Event): void {
                                                        popover.close();
                                                        let files: File[] = oEvent.getParameter("files");
                                                        if (ibas.objects.isNull(files) || files.length === 0) {
                                                            return;
                                                        }
                                                        let fileData: FormData = new FormData();
                                                        fileData.append("file", files[0]);
                                                        that.fireViewEvents(that.uploadAppSecretEvent, fileData);
                                                    },
                                                }),
                                            ]
                                        });
                                        (<any>popover).addStyleClass("sapMOTAPopover sapTntToolHeaderPopover");
                                        popover.openBy(event.getSource(), true);
                                    }
                                }),
                            ]
                        }),
                        content: [this.layoutMain]
                    });
                    return this.page;
                }

                private page: sap.m.Page;
                private layoutMain: sap.ui.layout.VerticalLayout;

                /** 改变视图状态 */
                private changeViewStatus(data: bo.Application): void {
                    if (ibas.objects.isNull(data)) {
                        return;
                    }
                    // 新建时：禁用删除，
                    if (data.isNew) {
                        if (this.page.getSubHeader() instanceof sap.m.Toolbar) {
                            openui5.utils.changeToolbarSavable(<sap.m.Toolbar>this.page.getSubHeader(), true);
                            openui5.utils.changeToolbarDeletable(<sap.m.Toolbar>this.page.getSubHeader(), false);
                        }
                    }
                }

                /** 显示数据 */
                showApplication(data: bo.Application): void {
                    this.layoutMain.setModel(new sap.ui.model.json.JSONModel(data));
                    this.layoutMain.bindObject("/");
                    // 监听属性改变，并更新控件
                    openui5.utils.refreshModelChanged(this.layoutMain, data);
                    // 改变视图状态
                    this.changeViewStatus(data);
                }
            }
        }
    }
}
