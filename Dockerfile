FROM clojure:openjdk-11-lein as builder
WORKDIR /confeus
ADD project.clj project.clj
RUN lein deps
ADD . .
RUN lein uberjar

FROM openjdk:11-jre-slim
EXPOSE 3299
CMD ["java", "-jar", "/bin/confeus.jar"]
COPY --from=builder /confeus/target/confeus.jar /bin/confeus.jar
