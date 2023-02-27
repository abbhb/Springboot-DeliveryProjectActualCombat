#  基础镜像
FROM openjdk:17
# 维护者信息
MAINTAINER qc2003020403@163.com
#Default to UTF-8 file.encoding

#添加应用
ADD reggit_take_out-1.0-SNAPSHOT.jar app.jar
#参数
# 执行启动命令
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]