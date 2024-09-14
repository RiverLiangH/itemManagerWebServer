# 使用官方 Tomcat 镜像作为基础镜像
FROM tomcat:10.1-jdk17

# 将构建好的 WAR 文件复制到 Tomcat 的 webapps 目录中
COPY target/item_manager_backend.war /usr/local/tomcat/webapps/

# 暴露 Tomcat 默认端口
EXPOSE 8080

# 启动 Tomcat
CMD ["catalina.sh", "run"]
