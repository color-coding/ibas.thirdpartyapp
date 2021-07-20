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
            export class ApplicationSettingServiceView extends ibas.DialogView implements app.IApplicationSettingServiceView {
                saveDataEvent: Function;
                /** 绘制视图 */
                draw(): any {
                    let that: this = this;
                    return new sap.m.Dialog("", {
                        title: this.title,
                        type: sap.m.DialogType.Standard,
                        state: sap.ui.core.ValueState.None,
                        horizontalScrolling: true,
                        verticalScrolling: true,
                        content: [
                            this.list = new sap.m.List("", {
                                mode: sap.m.ListMode.None,
                                items: {
                                    path: "/",
                                    templateShareable: false,
                                    template: new sap.m.CustomListItem("", {
                                        content: [
                                            new sap.ui.layout.form.SimpleForm("", {
                                                editable: true,
                                                layout: sap.ui.layout.form.SimpleFormLayout.ResponsiveGridLayout,
                                                labelSpanS: 4,
                                                labelSpanM: 4,
                                                labelSpanL: 4,
                                                content: [
                                                    new sap.m.Label("", { text: ibas.i18n.prop("bo_applicationsettingitem_description") }),
                                                    new sap.extension.m.Input("", {
                                                        editable: false,
                                                    }).bindProperty("bindingValue", {
                                                        path: "description",
                                                        type: new sap.extension.data.Alphanumeric()
                                                    }),
                                                    new sap.m.Label("", { text: ibas.i18n.prop("bo_applicationsettingitem_value") }),
                                                    new sap.extension.m.Input("", {
                                                        valueHelpRequest(oEvent: sap.ui.base.Event): void {
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
                                                        }
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
                                                    }).bindProperty("type", {
                                                        path: "category",
                                                        formatter(data: bo.emConfigItemCategory): sap.m.InputType {
                                                            if (ibas.config.get(ibas.CONFIG_ITEM_DEBUG_MODE, false)) {
                                                                return sap.m.InputType.Text;
                                                            }
                                                            return data === bo.emConfigItemCategory.PASSWORD ? sap.m.InputType.Password : sap.m.InputType.Text;
                                                        }
                                                    }).bindProperty("showValueHelp", {
                                                        path: "category",
                                                        formatter(data: bo.emConfigItemCategory): boolean {
                                                            return data === bo.emConfigItemCategory.FILE ? true : false;
                                                        }
                                                    }),
                                                ]
                                            }).addStyleClass("sapUiNoContentPadding"),
                                        ]
                                    })
                                },
                            })
                        ],
                        buttons: [
                            new sap.m.Button("", {
                                text: ibas.i18n.prop("shell_data_save"),
                                type: sap.m.ButtonType.Transparent,
                                press: function (): void {
                                    that.fireViewEvents(that.saveDataEvent);
                                }
                            }),
                            new sap.m.Button("", {
                                text: ibas.i18n.prop("shell_exit"),
                                type: sap.m.ButtonType.Transparent,
                                press: function (): void {
                                    that.fireViewEvents(that.closeEvent);
                                }
                            }),
                        ],
                    }).addStyleClass("sapUiNoContentPadding");
                }
                private list: sap.m.List;
                showSetting(data: bo.ApplicationSetting): void {
                    if (ibas.objects.isNull(data)) {
                        this.list.setHeaderText(undefined);
                    } else {
                        this.list.setHeaderText(data.description);
                    }
                }
                showSettingItems(datas: bo.ApplicationSettingItem[]): void {
                    this.list.setModel(new sap.extension.model.JSONModel(datas));
                }
            }
        }
    }
}