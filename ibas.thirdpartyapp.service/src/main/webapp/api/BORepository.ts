/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    export namespace bo {
        /** 业务仓库 */
        export interface IBORepositoryThirdPartyApp extends ibas.IBORepositoryApplication {
            /**
             * 上传文件
             * @param caller 调用者
             */
            upload(caller: ibas.IUploadFileCaller<ibas.FileData>): void;
            /**
             * 下载文件
             * @param caller 调用者
             */
            download(caller: ibas.IDownloadFileCaller<Blob>): void;
            /**
             * 查询 应用
             * @param fetcher 查询者
             */
            fetchApplication(fetcher: ibas.IFetchCaller<bo.IApplication>): void;
            /**
             * 保存 应用
             * @param saver 保存者
             */
            saveApplication(saver: ibas.ISaveCaller<bo.IApplication>): void;
            /**
             * 查询 用户
             * @param fetcher 查询者
             */
            fetchUser(fetcher: ibas.IFetchCaller<bo.IUser>): void;
            /**
             * 保存 用户
             * @param saver 保存者
             */
            saveUser(saver: ibas.ISaveCaller<bo.IUser>): void;

        }
    }
}
