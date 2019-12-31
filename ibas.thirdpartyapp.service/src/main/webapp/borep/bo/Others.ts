/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace bo {
        /** 用户应用 */
        export class UserApplication {
            /** 编码 */
            code: string;
            /** 名称 */
            name: string;
            /** 地址 */
            url: string;
            /** 用户 */
            user: string;
        }
        const PROPERTY_NAME: symbol = Symbol("name");
        const PROPERTY_GROUP: symbol = Symbol("group");
        const PROPERTY_DESCRIPTION: symbol = Symbol("description");
        const PROPERTY_SETTINGITEMS: symbol = Symbol("settingItems");
        const PROPERTY_CATEGORY: symbol = Symbol("category");
        const PROPERTY_VALUE: symbol = Symbol("value");
        /** 应用设置 */
        export class ApplicationSetting extends ibas.Bindable {
            /** 名称 */
            get name(): string {
                return this[PROPERTY_NAME];
            }
            set name(value: string) {
                this[PROPERTY_NAME] = value;
                this.firePropertyChanged("name");
            }
            /** 组 */
            get group(): string {
                return this[PROPERTY_GROUP];
            }
            set group(value: string) {
                this[PROPERTY_GROUP] = value;
                this.firePropertyChanged("group");
            }
            /** 描述 */
            get description(): string {
                return this[PROPERTY_DESCRIPTION];
            }
            set description(value: string) {
                this[PROPERTY_DESCRIPTION] = value;
                this.firePropertyChanged("description");
            }
            /** 子项 */
            get settingItems(): string {
                return this[PROPERTY_SETTINGITEMS];
            }
            set settingItems(value: string) {
                this[PROPERTY_SETTINGITEMS] = value;
                this.firePropertyChanged("settingItems");
            }
        }
        /** 应用设置项目 */
        export class ApplicationSettingItem extends ibas.Bindable {
            /** 名称 */
            get name(): string {
                return this[PROPERTY_NAME];
            }
            set name(value: string) {
                this[PROPERTY_NAME] = value;
                this.firePropertyChanged("name");
            }
            /** 类型 */
            get category(): emConfigItemCategory {
                return this[PROPERTY_CATEGORY];
            }
            set category(value: emConfigItemCategory) {
                this[PROPERTY_CATEGORY] = value;
                this.firePropertyChanged("category");
            }
            /** 描述 */
            get description(): string {
                return this[PROPERTY_DESCRIPTION];
            }
            set description(value: string) {
                this[PROPERTY_DESCRIPTION] = value;
                this.firePropertyChanged("description");
            }
            /** 值 */
            get value(): string | Blob {
                return this[PROPERTY_VALUE];
            }
            set value(value: string | Blob) {
                this[PROPERTY_VALUE] = value;
                this.firePropertyChanged("value");
            }
        }
    }
}
