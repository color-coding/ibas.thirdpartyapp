/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace bo {
        /** 应用配置 */
        export interface IApplicationConfig extends ibas.IBOMasterData {
            /** 编码 */
            code: string;
            /** 名称 */
            name: string;
            /** 分组 */
            group: string;
            /** 备注 */
            remarks: string;
            /** 对象编号 */
            docEntry: number;
            /** 对象类型 */
            objectCode: string;
            /** 创建日期 */
            createDate: Date;
            /** 创建时间 */
            createTime: number;
            /** 修改日期 */
            updateDate: Date;
            /** 修改时间 */
            updateTime: number;
            /** 数据源 */
            dataSource: string;
            /** 实例号（版本） */
            logInst: number;
            /** 服务系列 */
            series: number;
            /** 创建用户 */
            createUserSign: number;
            /** 修改用户 */
            updateUserSign: number;
            /** 创建动作标识 */
            createActionId: string;
            /** 更新动作标识 */
            updateActionId: string;

            /** 应用配置-项目集合 */
            applicationConfigItems: IApplicationConfigItems;

        }

        /** 应用配置-项目 集合 */
        export interface IApplicationConfigItems extends ibas.IBusinessObjects<IApplicationConfigItem> {
            /** 创建并添加子项 */
            create(): IApplicationConfigItem;
        }

        /** 应用配置-项目 */
        export interface IApplicationConfigItem extends ibas.IBOMasterDataLine {
            /** 编码 */
            code: string;
            /** 行号 */
            lineId: number;
            /** 类型 */
            objectCode: string;
            /** 数据源 */
            dataSource: string;
            /** 创建日期 */
            createDate: Date;
            /** 创建时间 */
            createTime: number;
            /** 修改日期 */
            updateDate: Date;
            /** 修改时间 */
            updateTime: number;
            /** 版本 */
            logInst: number;
            /** 创建用户 */
            createUserSign: number;
            /** 修改用户 */
            updateUserSign: number;
            /** 创建动作标识 */
            createActionId: string;
            /** 更新动作标识 */
            updateActionId: string;
            /** 名称 */
            name: string;
            /** 描述 */
            description: string;
            /** 类别 */
            category: emConfigItemCategory;
            /** 值 */
            value: string;

        }


    }
}
