/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace bo {
        /** 应用 */
        export class Application extends ibas.BOMasterData<Application> implements IApplication {
            /** 业务对象编码 */
            static BUSINESS_OBJECT_CODE: string = BO_CODE_APPLICATION;
            /** 构造函数 */
            constructor() {
                super();
            }
            /** 映射的属性名称-编码 */
            static PROPERTY_CODE_NAME: string = "Code";
            /** 获取-编码 */
            get code(): string {
                return this.getProperty<string>(Application.PROPERTY_CODE_NAME);
            }
            /** 设置-编码 */
            set code(value: string) {
                this.setProperty(Application.PROPERTY_CODE_NAME, value);
            }

            /** 映射的属性名称-名称 */
            static PROPERTY_NAME_NAME: string = "Name";
            /** 获取-名称 */
            get name(): string {
                return this.getProperty<string>(Application.PROPERTY_NAME_NAME);
            }
            /** 设置-名称 */
            set name(value: string) {
                this.setProperty(Application.PROPERTY_NAME_NAME, value);
            }

            /** 映射的属性名称-激活 */
            static PROPERTY_ACTIVATED_NAME: string = "Activated";
            /** 获取-激活 */
            get activated(): ibas.emYesNo {
                return this.getProperty<ibas.emYesNo>(Application.PROPERTY_ACTIVATED_NAME);
            }
            /** 设置-激活 */
            set activated(value: ibas.emYesNo) {
                this.setProperty(Application.PROPERTY_ACTIVATED_NAME, value);
            }

            /** 映射的属性名称-应用标记 */
            static PROPERTY_APPID_NAME: string = "AppId";
            /** 获取-应用标记 */
            get appId(): string {
                return this.getProperty<string>(Application.PROPERTY_APPID_NAME);
            }
            /** 设置-应用标记 */
            set appId(value: string) {
                this.setProperty(Application.PROPERTY_APPID_NAME, value);
            }

            /** 映射的属性名称-应用公钥 */
            static PROPERTY_APPKEY_NAME: string = "AppKey";
            /** 获取-应用公钥 */
            get appKey(): string {
                return this.getProperty<string>(Application.PROPERTY_APPKEY_NAME);
            }
            /** 设置-应用公钥 */
            set appKey(value: string) {
                this.setProperty(Application.PROPERTY_APPKEY_NAME, value);
            }

            /** 映射的属性名称-应用私钥 */
            static PROPERTY_APPSECRET_NAME: string = "AppSecret";
            /** 获取-应用私钥 */
            get appSecret(): string {
                return this.getProperty<string>(Application.PROPERTY_APPSECRET_NAME);
            }
            /** 设置-应用私钥 */
            set appSecret(value: string) {
                this.setProperty(Application.PROPERTY_APPSECRET_NAME, value);
            }

            /** 映射的属性名称-应用地址 */
            static PROPERTY_APPURL_NAME: string = "AppUrl";
            /** 获取-应用地址 */
            get appUrl(): string {
                return this.getProperty<string>(Application.PROPERTY_APPURL_NAME);
            }
            /** 设置-应用地址 */
            set appUrl(value: string) {
                this.setProperty(Application.PROPERTY_APPURL_NAME, value);
            }

            /** 映射的属性名称-认证地址 */
            static PROPERTY_OAUTHURL_NAME: string = "OAuthUrl";
            /** 获取-认证地址 */
            get oAuthUrl(): string {
                return this.getProperty<string>(Application.PROPERTY_OAUTHURL_NAME);
            }
            /** 设置-认证地址 */
            set oAuthUrl(value: string) {
                this.setProperty(Application.PROPERTY_OAUTHURL_NAME, value);
            }

            /** 映射的属性名称-证书地址 */
            static PROPERTY_CERTIFICATE_NAME: string = "Certificate";
            /** 获取-证书地址 */
            get certificate(): string {
                return this.getProperty<string>(Application.PROPERTY_CERTIFICATE_NAME);
            }
            /** 设置-证书地址 */
            set certificate(value: string) {
                this.setProperty(Application.PROPERTY_CERTIFICATE_NAME, value);
            }

            /** 映射的属性名称-账号 */
            static PROPERTY_ACCOUNT_NAME: string = "Account";
            /** 获取-账号 */
            get account(): string {
                return this.getProperty<string>(Application.PROPERTY_ACCOUNT_NAME);
            }
            /** 设置-账号 */
            set account(value: string) {
                this.setProperty(Application.PROPERTY_ACCOUNT_NAME, value);
            }

            /** 映射的属性名称-接收地址 */
            static PROPERTY_RECEIVINGURL_NAME: string = "ReceivingUrl";
            /** 获取-接收地址 */
            get receivingUrl(): string {
                return this.getProperty<string>(Application.PROPERTY_RECEIVINGURL_NAME);
            }
            /** 设置-接收地址 */
            set receivingUrl(value: string) {
                this.setProperty(Application.PROPERTY_RECEIVINGURL_NAME, value);
            }

            /** 映射的属性名称-对象编号 */
            static PROPERTY_DOCENTRY_NAME: string = "DocEntry";
            /** 获取-对象编号 */
            get docEntry(): number {
                return this.getProperty<number>(Application.PROPERTY_DOCENTRY_NAME);
            }
            /** 设置-对象编号 */
            set docEntry(value: number) {
                this.setProperty(Application.PROPERTY_DOCENTRY_NAME, value);
            }

            /** 映射的属性名称-对象类型 */
            static PROPERTY_OBJECTCODE_NAME: string = "ObjectCode";
            /** 获取-对象类型 */
            get objectCode(): string {
                return this.getProperty<string>(Application.PROPERTY_OBJECTCODE_NAME);
            }
            /** 设置-对象类型 */
            set objectCode(value: string) {
                this.setProperty(Application.PROPERTY_OBJECTCODE_NAME, value);
            }

            /** 映射的属性名称-创建日期 */
            static PROPERTY_CREATEDATE_NAME: string = "CreateDate";
            /** 获取-创建日期 */
            get createDate(): Date {
                return this.getProperty<Date>(Application.PROPERTY_CREATEDATE_NAME);
            }
            /** 设置-创建日期 */
            set createDate(value: Date) {
                this.setProperty(Application.PROPERTY_CREATEDATE_NAME, value);
            }

            /** 映射的属性名称-创建时间 */
            static PROPERTY_CREATETIME_NAME: string = "CreateTime";
            /** 获取-创建时间 */
            get createTime(): number {
                return this.getProperty<number>(Application.PROPERTY_CREATETIME_NAME);
            }
            /** 设置-创建时间 */
            set createTime(value: number) {
                this.setProperty(Application.PROPERTY_CREATETIME_NAME, value);
            }

            /** 映射的属性名称-修改日期 */
            static PROPERTY_UPDATEDATE_NAME: string = "UpdateDate";
            /** 获取-修改日期 */
            get updateDate(): Date {
                return this.getProperty<Date>(Application.PROPERTY_UPDATEDATE_NAME);
            }
            /** 设置-修改日期 */
            set updateDate(value: Date) {
                this.setProperty(Application.PROPERTY_UPDATEDATE_NAME, value);
            }

            /** 映射的属性名称-修改时间 */
            static PROPERTY_UPDATETIME_NAME: string = "UpdateTime";
            /** 获取-修改时间 */
            get updateTime(): number {
                return this.getProperty<number>(Application.PROPERTY_UPDATETIME_NAME);
            }
            /** 设置-修改时间 */
            set updateTime(value: number) {
                this.setProperty(Application.PROPERTY_UPDATETIME_NAME, value);
            }

            /** 映射的属性名称-数据源 */
            static PROPERTY_DATASOURCE_NAME: string = "DataSource";
            /** 获取-数据源 */
            get dataSource(): string {
                return this.getProperty<string>(Application.PROPERTY_DATASOURCE_NAME);
            }
            /** 设置-数据源 */
            set dataSource(value: string) {
                this.setProperty(Application.PROPERTY_DATASOURCE_NAME, value);
            }

            /** 映射的属性名称-实例号（版本） */
            static PROPERTY_LOGINST_NAME: string = "LogInst";
            /** 获取-实例号（版本） */
            get logInst(): number {
                return this.getProperty<number>(Application.PROPERTY_LOGINST_NAME);
            }
            /** 设置-实例号（版本） */
            set logInst(value: number) {
                this.setProperty(Application.PROPERTY_LOGINST_NAME, value);
            }

            /** 映射的属性名称-服务系列 */
            static PROPERTY_SERIES_NAME: string = "Series";
            /** 获取-服务系列 */
            get series(): number {
                return this.getProperty<number>(Application.PROPERTY_SERIES_NAME);
            }
            /** 设置-服务系列 */
            set series(value: number) {
                this.setProperty(Application.PROPERTY_SERIES_NAME, value);
            }

            /** 映射的属性名称-创建用户 */
            static PROPERTY_CREATEUSERSIGN_NAME: string = "CreateUserSign";
            /** 获取-创建用户 */
            get createUserSign(): number {
                return this.getProperty<number>(Application.PROPERTY_CREATEUSERSIGN_NAME);
            }
            /** 设置-创建用户 */
            set createUserSign(value: number) {
                this.setProperty(Application.PROPERTY_CREATEUSERSIGN_NAME, value);
            }

            /** 映射的属性名称-修改用户 */
            static PROPERTY_UPDATEUSERSIGN_NAME: string = "UpdateUserSign";
            /** 获取-修改用户 */
            get updateUserSign(): number {
                return this.getProperty<number>(Application.PROPERTY_UPDATEUSERSIGN_NAME);
            }
            /** 设置-修改用户 */
            set updateUserSign(value: number) {
                this.setProperty(Application.PROPERTY_UPDATEUSERSIGN_NAME, value);
            }

            /** 映射的属性名称-创建动作标识 */
            static PROPERTY_CREATEACTIONID_NAME: string = "CreateActionId";
            /** 获取-创建动作标识 */
            get createActionId(): string {
                return this.getProperty<string>(Application.PROPERTY_CREATEACTIONID_NAME);
            }
            /** 设置-创建动作标识 */
            set createActionId(value: string) {
                this.setProperty(Application.PROPERTY_CREATEACTIONID_NAME, value);
            }

            /** 映射的属性名称-更新动作标识 */
            static PROPERTY_UPDATEACTIONID_NAME: string = "UpdateActionId";
            /** 获取-更新动作标识 */
            get updateActionId(): string {
                return this.getProperty<string>(Application.PROPERTY_UPDATEACTIONID_NAME);
            }
            /** 设置-更新动作标识 */
            set updateActionId(value: string) {
                this.setProperty(Application.PROPERTY_UPDATEACTIONID_NAME, value);
            }

            /** 初始化数据 */
            protected init(): void {
                this.objectCode = ibas.config.applyVariables(Application.BUSINESS_OBJECT_CODE);
                this.activated = ibas.emYesNo.YES;
            }
        }


    }
}
