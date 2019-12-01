FROM gradle:jdk11
WORKDIR /app
ADD ./  /app/
RUN ["./gradlew", "clean", "build"]

FROM adoptopenjdk/openjdk11-openj9:alpine-slim
WORKDIR /app/
COPY --from=0 /app/build/libs/banking-*-all.jar /app/banking.jar
ENV JAVA_OPS=${JAVA_TOOL_OPTIONS}" -XX:+PrintFlagsFinal -XX:+CMSClassUnloadingEnabled -XX:SharedCacheHardLimit=200m -Xscmx200m -Xshareclasses:cacheDir=/opt/shareclasses -Xtune:virtualized -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler"
EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPS} -jar /app/banking.jar