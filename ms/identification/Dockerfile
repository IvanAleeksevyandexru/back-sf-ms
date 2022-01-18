FROM registry.gosuslugi.local/base_images/bellsoft/liberica-openjdk-centos:11
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN groupadd spring && adduser spring -g spring
USER spring:spring
ARG JAR_FILE=target/*.jar
ARG SMART_ENGINE_LIB_DIR=/jni-lib
ARG BUILD_VERSION=Not_Set
ENV VERSION=$BUILD_VERSION
ENV LD_LIBRARY_PATH=$SMART_ENGINE_LIB_DIR
ADD ${SMART_ENGINE_LIB_DIR} ${SMART_ENGINE_LIB_DIR}
COPY ${JAR_FILE} app.jar
CMD ["java","-jar","/app.jar","--spring.config.location=file:/deploy.yml,file:/passwords.yml"]
EXPOSE 8080/tcp
