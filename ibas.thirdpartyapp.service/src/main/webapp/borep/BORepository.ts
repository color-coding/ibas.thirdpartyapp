/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace bo {
        /** 业务对象仓库 */
        export class BORepositoryThirdPartyApp extends ibas.BORepositoryApplication implements IBORepositoryThirdPartyApp {

            /** 创建此模块的后端与前端数据的转换者 */
            protected createConverter(): ibas.IDataConverter {
                return new DataConverter;
            }
            /**
             * 获取地址
             */
            toUrl(filename: string): string {
                if (!this.address.endsWith("/")) { this.address += "/"; }
                let url: string = this.address.replace("/services/rest/data/", "/services/rest/file/");
                url += ibas.strings.format("{0}?token={1}", filename, ibas.tokens.content(this.token));
                return encodeURI(url);
            }
            /**
             * 上传文件
             * @param caller 调用者
             */
            upload(caller: ibas.IUploadFileCaller<ibas.FileData>): void {
                if (!this.address.endsWith("/")) { this.address += "/"; }
                let fileRepository: ibas.FileRepositoryUploadAjax = new ibas.FileRepositoryUploadAjax();
                fileRepository.address = this.address.replace("/services/rest/data/", "/services/rest/file/");
                fileRepository.token = this.token;
                fileRepository.converter = this.createConverter();
                fileRepository.upload("upload", caller);
            }
            /**
             * 下载文件
             * @param caller 调用者
             */
            download(caller: ibas.IDownloadFileCaller<Blob>): void {
                if (!this.address.endsWith("/")) { this.address += "/"; }
                let fileRepository: ibas.FileRepositoryDownloadAjax = new ibas.FileRepositoryDownloadAjax();
                fileRepository.address = this.address.replace("/services/rest/data/", "/services/rest/file/");
                fileRepository.token = this.token;
                fileRepository.converter = this.createConverter();
                fileRepository.download("download", caller);
            }
            /**
             * 查询 应用
             * @param fetcher 查询者
             */
            fetchApplication(fetcher: ibas.IFetchCaller<bo.Application>): void {
                super.fetch(bo.Application.name, fetcher);
            }
            /**
             * 保存 应用
             * @param saver 保存者
             */
            saveApplication(saver: ibas.ISaveCaller<bo.Application>): void {
                super.save(bo.Application.name, saver);
            }
            /**
             * 查询 应用配置
             * @param fetcher 查询者
             */
            fetchApplicationConfig(fetcher: ibas.IFetchCaller<bo.ApplicationConfig>): void {
                super.fetch(bo.ApplicationConfig.name, fetcher);
            }
            /**
             * 保存 应用配置
             * @param saver 保存者
             */
            saveApplicationConfig(saver: ibas.ISaveCaller<bo.ApplicationConfig>): void {
                super.save(bo.ApplicationConfig.name, saver);
            }
            /**
             * 查询 用户应用
             * @param caller 查询者
             */
            fetchUserApplications(caller: IUserMethodsCaller<bo.UserApplication>): void {
                let boRepository: ibas.BORepositoryAjax = new ibas.BORepositoryAjax();
                boRepository.address = this.address;
                boRepository.token = this.token;
                boRepository.converter = this.createConverter();
                if (ibas.objects.isNull(boRepository)) {
                    throw new Error(ibas.i18n.prop("sys_invalid_parameter", "remoteRepository"));
                }
                let method: string =
                    ibas.strings.format("fetchUserApplications?user={0}&token={1}",
                        caller.user, ibas.tokens.content(this.token));
                boRepository.callRemoteMethod(method, undefined, (opRslt) => {
                    caller.onCompleted.call(ibas.objects.isNull(caller.caller) ? caller : caller.caller, opRslt);
                });
            }
            /**
             * 保存 应用配置
             * @param caller 查询者
             */
            saveApplicationSetting(caller: IApplicationSettingSaver): void {
                if (!this.address.endsWith("/")) { this.address += "/"; }
                let fileRepository: ibas.FileRepositoryUploadAjax = new ibas.FileRepositoryUploadAjax();
                fileRepository.address = this.address.replace("/services/rest/data/", "/services/rest/file/");
                fileRepository.token = this.token;
                fileRepository.converter = this.createConverter();
                let builder: ibas.StringBuilder = new ibas.StringBuilder();
                builder.append("saveApplicationSetting");
                builder.append("?");
                builder.append("application");
                builder.append("=");
                builder.append(encodeURIComponent(caller.application));
                if (!ibas.strings.isEmpty(caller.user)) {
                    builder.append("&");
                    builder.append("user");
                    builder.append("=");
                    builder.append(caller.user);
                }
                fileRepository.upload(builder.toString(), {
                    fileData: caller.formData,
                    onCompleted: caller.onCompleted,
                });
            }
            /**
             * 查询 应用配置
             * @param caller 查询者
             */
            fetchApplicationSetting(caller: IApplicationSettingFetcher): void {
                if (!this.address.endsWith("/")) { this.address += "/"; }
                let boRepository: ibas.BORepositoryAjax = new ibas.BORepositoryAjax();
                boRepository.address = this.address.replace("/services/rest/data/", "/services/rest/file/");
                boRepository.token = this.token;
                boRepository.converter = this.createConverter();
                if (ibas.objects.isNull(boRepository)) {
                    throw new Error(ibas.i18n.prop("sys_invalid_parameter", "remoteRepository"));
                }
                let builder: ibas.StringBuilder = new ibas.StringBuilder();
                builder.append("fetchApplicationSetting");
                builder.append("?");
                builder.append("application");
                builder.append("=");
                builder.append(encodeURIComponent(caller.application));
                if (!ibas.strings.isEmpty(caller.user)) {
                    builder.append("&");
                    builder.append("user");
                    builder.append("=");
                    builder.append(caller.user);
                }
                builder.append("&");
                builder.append("token");
                builder.append("=");
                builder.append(ibas.tokens.content(this.token));
                boRepository.callRemoteMethod(builder.toString(), undefined, (opRslt) => {
                    caller.onCompleted.call(ibas.objects.isNull(caller.caller) ? caller : caller.caller, opRslt);
                });
            }
            /**
             * 移除 用户应用配置
             * @param caller 查询者
             */
            removeApplicationSetting(caller: IApplicationSettingRemover): void {
                if (!this.address.endsWith("/")) { this.address += "/"; }
                let boRepository: ibas.BORepositoryAjax = new ibas.BORepositoryAjax();
                boRepository.address = this.address.replace("/services/rest/data/", "/services/rest/file/");
                boRepository.token = this.token;
                boRepository.converter = this.createConverter();
                if (ibas.objects.isNull(boRepository)) {
                    throw new Error(ibas.i18n.prop("sys_invalid_parameter", "remoteRepository"));
                }
                let builder: ibas.StringBuilder = new ibas.StringBuilder();
                builder.append("removeApplicationSetting");
                builder.append("?");
                builder.append("application");
                builder.append("=");
                builder.append(encodeURIComponent(caller.application));
                if (!ibas.strings.isEmpty(caller.user)) {
                    builder.append("&");
                    builder.append("user");
                    builder.append("=");
                    builder.append(caller.user);
                }
                builder.append("&");
                builder.append("token");
                builder.append("=");
                builder.append(ibas.tokens.content(this.token));
                boRepository.callRemoteMethod(builder.toString(), undefined, (opRslt) => {
                    caller.onCompleted.call(ibas.objects.isNull(caller.caller) ? caller : caller.caller, opRslt);
                });
            }
            /**
             * 查询 用户映射
             * @param fetcher 查询者
             */
            fetchUserMapping(fetcher: ibas.IFetchCaller<bo.UserMapping>): void {
                super.fetch(bo.UserMapping.name, fetcher);
            }
        }
        /**
         * 用户相关调用者
         */
        export interface IUserMethodsCaller<P> extends ibas.IMethodCaller<P> {
            /** 用户 */
            user: string;
            /** 平台 */
            platform?: string;
        }
        /**
         * 应用配置保存者
         */
        export interface IApplicationSettingSaver extends ibas.IMethodCaller<ApplicationSetting> {
            /** 应用 */
            application: string;
            /** 用户 */
            user?: string;
            /** 提交数据 */
            formData: FormData;
        }
        /**
         * 应用配置查询者
         */
        export interface IApplicationSettingFetcher extends ibas.IMethodCaller<ApplicationSetting> {
            /** 应用 */
            application: string;
            /** 用户 */
            user?: string;
        }
        /**
         * 应用配置查询者
         */
        export interface IApplicationSettingRemover extends ibas.IMethodCaller<void> {
            /** 应用 */
            application: string;
            /** 用户 */
            user: string;
        }
    }
}
