# Item-Manager 项目

`item-manager` 是一个基于 Docker 容器运行的项目，提供了一套用于管理项目条目的简便工具。

## 环境要求

项目运行于 Docker 容器中，容器的基础环境为：

- 操作系统：Ubuntu
- Apache Maven 3.9.9
- Java version: 17.0.12

## 拉取镜像并运行容器(开发环境)

1. 拉取 Docker 镜像：
   bash
   docker pull riverhou/item-manager:v2

2. 运行 Docker 镜像：
    docker run -d -p 8080:8080 riverhou/item-manager

