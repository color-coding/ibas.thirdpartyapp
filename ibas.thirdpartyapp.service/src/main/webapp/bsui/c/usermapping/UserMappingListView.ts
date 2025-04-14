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
            /** 列表视图-用户映射 */
            export class UserMappingListView extends ibas.BOQueryViewWithPanel implements app.IUserMappingListView {
                /** 返回查询的对象 */
                get queryTarget(): any {
                    return initialfantasy.bo.User;
                }
                newDataEvent: Function;
                viewDataEvent: Function;
                editDataEvent: Function;
                deleteDataEvent: Function;
                connectApplicationEvent: Function;
                /** 断开应用事件 */
                disconnectApplicationEvent: Function;
                /** 绘制视图 */
                draw(): any {
                    let that: this = this;
                    this.userList = new sap.extension.m.List("", {
                        inset: false,
                        growing: true,
                        growingThreshold: ibas.config.get(openui5.utils.CONFIG_ITEM_LIST_TABLE_VISIBLE_ROW_COUNT, 15),
                        growingScrollToLoad: true,
                        mode: sap.m.ListMode.SingleSelectMaster,
                        items: {
                            path: "/rows",
                            template: new sap.m.ObjectListItem("", {
                                title: {
                                    path: "name",
                                    type: new sap.extension.data.Alphanumeric(),
                                },
                                firstStatus: new sap.extension.m.ObjectYesNoStatus("", {
                                    negative: false,
                                }).bindProperty("enumValue", {
                                    path: "activated",
                                    type: new sap.extension.data.YesNo(),
                                }),
                                attributes: [
                                    new sap.extension.m.ObjectAttribute("", {
                                        title: ibas.i18n.prop("bo_user_code"),
                                        active: true,
                                        press(): void {
                                            let data: any = this.getBindingContext().getObject();
                                            if (data instanceof initialfantasy.bo.User) {
                                                ibas.servicesManager.runLinkService({
                                                    boCode: initialfantasy.bo.User.BUSINESS_OBJECT_CODE,
                                                    linkValue: data.code,
                                                });
                                            }
                                        }
                                    }).bindProperty("bindingValue", {
                                        path: "code",
                                        type: new sap.extension.data.Alphanumeric(),
                                    }),
                                    new sap.extension.m.RepositoryObjectAttribute("", {
                                        title: ibas.i18n.prop("bo_user_organization"),
                                        repository: initialfantasy.bo.BORepositoryInitialFantasy,
                                        dataInfo: {
                                            type: initialfantasy.bo.Organization,
                                            key: initialfantasy.bo.Organization.PROPERTY_CODE_NAME,
                                            text: initialfantasy.bo.Organization.PROPERTY_NAME_NAME
                                        }
                                    }).bindProperty("bindingValue", {
                                        path: "organization",
                                        type: new sap.extension.data.Alphanumeric(),
                                    }),
                                ],
                                type: sap.m.ListType.Active
                            })
                        },
                        itemPress(oEvent: any): void {
                            let oItem: any = oEvent.getParameter("listItem");
                            that.fireViewEvents(that.viewDataEvent, oItem.getBindingContext().getObject());
                        },
                        nextDataSet(event: sap.ui.base.Event): void {
                            // 查询下一个数据集
                            let data: any = event.getParameter("data");
                            if (ibas.objects.isNull(data)) {
                                return;
                            }
                            if (ibas.objects.isNull(that.lastCriteria)) {
                                return;
                            }
                            let criteria: ibas.ICriteria = that.lastCriteria.next(data);
                            if (ibas.objects.isNull(criteria)) {
                                return;
                            }
                            ibas.logger.log(ibas.emMessageLevel.DEBUG, "result: {0}", criteria.toString());
                            that.fireViewEvents(that.fetchDataEvent, criteria);
                        }
                    });
                    this.appPage = new sap.extension.m.Page("", {
                        showHeader: false,
                        subHeader: new sap.m.Toolbar("", {
                            content: [
                                new sap.m.SearchField("", {
                                    search(this: sap.m.SearchField): void {
                                        let binding: any = that.mappingList.getBinding("rows");
                                        if (binding instanceof sap.ui.model.json.JSONListBinding) {
                                            if (ibas.strings.isEmpty(this.getValue())) {
                                                binding.filter([], sap.ui.model.FilterType.Control);
                                            } else {
                                                binding.filter([
                                                    new sap.ui.model.Filter("app/name", sap.ui.model.FilterOperator.Contains, this.getValue())
                                                ], sap.ui.model.FilterType.Control);
                                            }
                                        }
                                    }
                                }),
                                new sap.m.ToolbarSpacer(),
                                new sap.m.Button("", {
                                    icon: "sap-icon://it-system",
                                    text: ibas.i18n.prop("thirdpartyapp_connect_application"),
                                    press(this: sap.m.Button): void {
                                        that.fireViewEvents(that.connectApplicationEvent, that.userList.getSelecteds().firstOrDefault());
                                    }
                                }),
                            ]
                        }),
                        content: [
                            this.mappingList = new sap.extension.m.List("", {
                                inset: false,
                                growing: true,
                                growingThreshold: ibas.config.get(openui5.utils.CONFIG_ITEM_LIST_TABLE_VISIBLE_ROW_COUNT, 15),
                                growingScrollToLoad: true,
                                items: {
                                    path: "/rows",
                                    template: new sap.m.FeedListItem("", {
                                        icon: {
                                            path: "app/picture",
                                            formatter(data: string): string {
                                                if (!ibas.strings.isEmpty(data)) {
                                                    return new bo.BORepositoryThirdPartyApp().toUrl(data);
                                                }
                                                return "";
                                            }
                                        },
                                        text: {
                                            parts: [
                                                {
                                                    path: "app/name",
                                                    data: new sap.extension.data.Alphanumeric()
                                                },
                                                {
                                                    path: "app/remarks",
                                                    data: new sap.extension.data.Alphanumeric()
                                                },
                                            ]
                                        },
                                        info: {
                                            path: "mapping/account",
                                            formatter(data: string): string {
                                                if (ibas.strings.isEmpty(data)) {
                                                    return "";
                                                }
                                                return data;
                                            }
                                        },
                                        actions: [
                                            new sap.m.FeedListItemAction("", {
                                                icon: "sap-icon://disconnected",
                                                text: ibas.i18n.prop("thirdpartyapp_disconnect_application"),
                                                press(this: sap.m.FeedListItemAction): void {
                                                    that.fireViewEvents(that.disconnectApplicationEvent, this.getBindingContext().getObject());
                                                }
                                            })
                                        ]
                                    })
                                },
                            })
                        ]
                    });
                    return this.split = new sap.m.SplitContainer("", {
                        masterPages: [
                            this.userListPage = new sap.extension.m.Page("", {
                                showHeader: false,
                                content: [
                                    this.userList,
                                ]
                            })
                        ],
                        detailPages: [
                            new sap.extension.m.Page("", {
                                showHeader: false,
                                content: [
                                    new sap.m.IllustratedMessage("", {
                                        illustrationType: sap.m.IllustratedMessageType.NoData,
                                        description: ibas.i18n.prop("thirdpartyapp_user_mapping_page_initial"),
                                    })
                                ]
                            }),
                            this.appPage
                        ],
                    });
                }
                private split: sap.m.SplitContainer;
                private userList: sap.extension.m.List;
                private appPage: sap.extension.m.Page;
                private userListPage: sap.extension.m.Page;
                private mappingList: sap.extension.m.List;
                /** 嵌入查询面板 */
                embedded(view: any): void {
                    if (view instanceof sap.m.Toolbar) {
                        view.setDesign(sap.m.ToolbarDesign.Transparent);
                        view.setStyle(sap.m.ToolbarStyle.Clear);
                        view.setHeight("100%");
                    }
                    this.userListPage.addHeaderContent(view);
                    this.userListPage.setShowHeader(true);
                }
                /** 记录上次查询条件，表格滚动时自动触发 */
                query(criteria: ibas.ICriteria): void {
                    super.query(criteria);
                    // 清除历史数据
                    if (this.isDisplayed) {
                        this.userList.setBusy(true);
                        this.userList.setModel(null);
                    }
                }
                /** 显示数据 */
                showData(datas: initialfantasy.bo.User[]): void {
                    let model: sap.ui.model.Model = this.userList.getModel();
                    if (model instanceof sap.extension.model.JSONModel) {
                        // 已绑定过数据
                        model.addData(datas);
                    } else {
                        // 未绑定过数据
                        this.userList.setModel(new sap.extension.model.JSONModel({ rows: datas }));
                    }
                    this.userList.setBusy(false);
                }
                showUserMappings(datas: app.MappingData[]): void {
                    this.split.to(this.appPage.getId(), null, null, null);
                    this.mappingList.setModel(new sap.extension.model.JSONModel({ rows: datas }));
                }
            }
        }
    }
}
