/**
 * @license
 * Copyright Color-Coding Studio. All Rights Reserved.
 *
 * Use of this source code is governed by an Apache License, Version 2.0
 * that can be found in the LICENSE file at http://www.apache.org/licenses/LICENSE-2.0
 */
namespace thirdpartyapp {
    /** 模块-标识 */
    export const CONSOLE_ID: string = "e25dde1f-2d1f-48de-a519-a0f9e88090ae";
    /** 模块-名称 */
    export const CONSOLE_NAME: string = "ThirdPartyApp";
    /** 模块-版本 */
    export const CONSOLE_VERSION: string = "0.1.0";

    export namespace bo {
        /** 业务仓库名称 */
        export const BO_REPOSITORY_THIRDPARTYAPP: string = ibas.strings.format(ibas.MODULE_REPOSITORY_NAME_TEMPLATE, CONSOLE_NAME);
        /** 业务对象编码-应用 */
        export const BO_CODE_APPLICATION: string = "${Company}_TPA_APP";
        /** 业务对象编码-用户 */
        export const BO_CODE_USER: string = "${Company}_TPA_USER";
    }

    export namespace app {

    }
}
