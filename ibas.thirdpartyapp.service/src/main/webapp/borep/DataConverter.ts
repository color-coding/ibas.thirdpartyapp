/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace bo {
        /** 数据转换者 */
        export class DataConverter extends ibas.DataConverter4j {
            /** 创建业务对象转换者 */
            protected createConverter(): ibas.BOConverter {
                return new BOConverter;
            }
            /**
             * 解析业务对象数据
             * @param data 目标类型
             * @param sign 特殊标记
             * @returns 本地类型
             */
            parsing(data: any, sign: string): any {
                if (data.type === bo.UserApplication.name) {
                    let remote: ibas4j.IUserApplication = data;
                    let newData: bo.UserApplication = new bo.UserApplication();
                    newData.code = remote.Code;
                    newData.name = remote.Name;
                    newData.url = remote.Url;
                    newData.user = remote.User;
                    return newData;
                }
                return super.parsing(data, sign);
            }
        }

        /** 模块业务对象工厂 */
        export const boFactory: ibas.BOFactory = new ibas.BOFactory();
        /** 业务对象转换者 */
        class BOConverter extends ibas.BOConverter {
            /** 模块对象工厂 */
            protected factory(): ibas.BOFactory {
                return boFactory;
            }

            /**
             * 自定义解析
             * @param data 远程数据
             * @returns 本地数据
             */
            protected customParsing(data: any): ibas.IBusinessObject {
                return data;
            }

            /**
             * 转换数据
             * @param boName 对象名称
             * @param property 属性名称
             * @param value 值
             * @returns 转换的值
             */
            protected convertData(boName: string, property: string, value: any): any {
                if (boName === bo.ApplicationConfigItem.name) {
                    if (property === bo.ApplicationConfigItem.PROPERTY_CATEGORY_NAME) {
                        return ibas.enums.toString(bo.emConfigItemCategory, value);
                    }
                }
                return super.convertData(boName, property, value);
            }

            /**
             * 解析数据
             * @param boName 对象名称
             * @param property 属性名称
             * @param value 值
             * @returns 解析的值
             */
            protected parsingData(boName: string, property: string, value: any): any {
                if (boName === bo.ApplicationConfigItem.name) {
                    if (property === bo.ApplicationConfigItem.PROPERTY_CATEGORY_NAME) {
                        return ibas.enums.valueOf(bo.emConfigItemCategory, value);
                    }
                }
                return super.parsingData(boName, property, value);
            }
        }
        export namespace ibas4j {
            /** ibas的java端数据声明 */

            /** 操作消息 */
            export interface IDataDeclaration {
                /** 数据类型 */
                type: string;
            }
            /** 用户应用 */
            export interface IUserApplication extends IDataDeclaration {
                /** 编码 */
                Code: string;
                /** 名称 */
                Name: string;
                /** 地址 */
                Url: string;
                /** 用户 */
                User: string;
            }
            /** 应用设置 */
            export interface IApplicationSetting {
                /** 名称 */
                Name: string;
                /** 组 */
                Group: string;
                /** 描述 */
                Description: string;
                /** 设置项目 */
                SettingItems: ibas.IList<IApplicationSettingItem>;
            }
            /** 应用设置项目 */
            export interface IApplicationSettingItem {
                /** 名称 */
                Name: string;
                /** 类型 */
                Category: string;
                /** 描述 */
                Description: string;
                /** 值 */
                Value: string;
            }
        }
    }
}
