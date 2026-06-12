<div align="center">

# IBAS ThirdPartyApp

**第三方应用集成模块**

IBAS 系统的第三方应用集成层，提供 SSO/OIDC 身份认证（微信、SAP IAS、阿里云 IDaaS 等）、邮件服务与 OpenAI API 集成。

Third-party application integration for the IBAS system — SSO/OIDC authentication (WeChat, SAP IAS, Alibaba IDaaS), email services, and OpenAI API integration.

[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-1.8+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.x-red.svg)](https://maven.apache.org/)
[![Version](https://img.shields.io/badge/version-0.2.0-green.svg)](pom.xml)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](#-贡献--contributing)

</div>

---

## 📖 目录 | Table of Contents

- [✨ 特性 | Features](#-特性--features)
- [📦 模块结构 | Modules](#-模块结构--modules)
- [🚀 快速开始 | Quick Start](#-快速开始--quick-start)
- [🏗️ 架构 | Architecture](#️-架构--architecture)
- [📚 相关项目 | Related Projects](#-相关项目--related-projects)
- [🤝 贡献 | Contributing](#-贡献--contributing)
- [📄 许可证 | License](#-许可证--license)

---

## ✨ 特性 | Features

- **🔐 SSO / OIDC 认证** — 统一身份认证客户端，支持多种第三方平台
- **💬 微信集成** — 微信公众号、微信小程序、轻应用认证
- **🏢 企业认证** — SAP IAS、阿里云 IDaaS 企业级身份认证
- **🤖 OpenAI API** — 集成 OpenAI 接口，支持 AI 对话与补全
- **📧 邮件服务** — 邮件发送客户端
- **👤 用户映射** — 第三方用户与 IBAS 系统用户的映射管理
- **⚙️ 应用配置** — 可配置的第三方应用参数与密钥管理

### 支持的认证平台

| 平台 | 说明 |
|------|------|
| WeChat | 微信公众号 |
| WeChatApplet | 微信小程序 |
| LightApp | 轻应用 |
| SSO | 单点登录 |
| OIDC | OpenID Connect |
| SAP_IAS | SAP Identity Authentication Service |
| ALY_IDaaS | 阿里云 IDaaS |
| OpenAI_API | OpenAI API 集成 |

---

## 📦 模块结构 | Modules

| 模块 | 类型 | 说明 |
|------|------|------|
| `ibas.thirdpartyapp` | JAR | **核心模块** — BO 模型（Application / ApplicationConfig / UserMapping）、仓储层、客户端管理器 |
| `ibas.thirdpartyapp.webapi` | JAR | **Web API 客户端** — 微信、SSO、OIDC、SAP IAS、阿里云 IDaaS、OpenAI 等客户端实现 |
| `ibas.thirdpartyapp.email` | JAR | **邮件客户端** — 邮件发送服务 |
| `ibas.thirdpartyapp.service` | WAR | **REST 服务** — DataService、FileService、JointService、JSON 配置 |

### 依赖关系

```
core → webapi / email → service（依赖 webapi 和 email）
```

---

## 🚀 快速开始 | Quick Start

### 环境要求 | Prerequisites

- **JDK** 1.8+
- **Maven** 3.x
- [ibas-framework](https://github.com/color-coding/ibas-framework)（BOBAS 框架）
- [ibas.initialfantasy](https://github.com/color-coding/ibas.initialfantasy)（认证/身份模块）

### 构建 | Build

```bash
# 克隆仓库
git clone https://github.com/color-coding/ibas.thirdpartyapp.git
cd ibas.thirdpartyapp

# 编译全部模块（按 compile_order.txt 顺序）
./compile_packages.sh            # Linux / macOS
compile_packages.bat             # Windows

# 编译单个模块
mvn clean package install -Dmaven.test.skip=true -f ibas.thirdpartyapp/pom.xml

# 运行测试
mvn test -f ibas.thirdpartyapp/pom.xml

# 部署
./deploy_packages.sh
```

### Maven 依赖

```xml
<dependency>
    <groupId>org.colorcoding.apps</groupId>
    <artifactId>ibas.thirdpartyapp</artifactId>
    <version>0.2.0</version>
</dependency>
```

---

## 🏗️ 架构 | Architecture

```
┌─────────────────────────────────────────┐
│    ibas.thirdpartyapp.service (WAR)     │  REST 服务层
├──────────────────┬──────────────────────┤
│  webapi (JAR)    │  email (JAR)         │  Web API 客户端 / 邮件
├──────────────────┴──────────────────────┤
│    ibas.thirdpartyapp (JAR)             │  核心：BO / 仓储 / 客户端管理
├─────────────────────────────────────────┤
│    ibas-framework (BOBAS)               │  业务对象框架
└─────────────────────────────────────────┘
```

### 核心组件

| 组件 | 说明 |
|------|------|
| `Application` / `ApplicationConfig` | 第三方应用配置 BO |
| `UserMapping` | 第三方用户与系统用户映射 |
| `ApplicationClient` | 应用客户端基类 |
| `ApplicationClientManager` | 客户端管理器，统一管理各平台认证 |

---

## 📚 相关项目 | Related Projects

| 项目 | 说明 |
|------|------|
| [ibas-framework](https://github.com/color-coding/ibas-framework) | BOBAS 业务对象框架 |
| [ibas.initialfantasy](https://github.com/color-coding/ibas.initialfantasy) | 身份认证与用户管理 |

---

## 🤝 贡献 | Contributing

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支（`git checkout -b feature/amazing-feature`）
3. 提交更改（`git commit -m 'Add amazing feature'`）
4. 推送到分支（`git push origin feature/amazing-feature`）
5. 发起 Pull Request

---

## 📄 许可证 | License

本项目基于 [Apache License 2.0](LICENSE) 开源。
---

## 🙏 鸣谢 | Thanks

<div align="center">

**[Color-Coding Studio](http://colorcoding.org/)** · 咔啦工作室

</div>
