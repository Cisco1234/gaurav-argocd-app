FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/abhiroopghatak/frontend-emart.git

FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY --from=0 /app/frontend-emart /app
RUN mvn install

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=1 /app/target/emart-frontend-0.0.1-SNAPSHOT.jar /app


#ADD my-ldap-ca-bundle.crt /usr/local/share/ca-certificates/dynacert.cert
#RUN chmod 644 /usr/local/share/ca-certificates/dynacert.cert 
#RUN update-ca-certificates

#ARG DT_API_URL=<<apipath>>
#ARG DT_API_TOKEN=<<token>>
#ARG DT_ONEAGENT_OPTIONS="flavor=default&include=java"
#ENV DT_HOME="/opt/dynatrace/oneagent"
#RUN mkdir -p "$DT_HOME" && \
#    wget -O --no-check-certificate "$DT_HOME/oneagent.zip" "$DT_API_URL/v1/deployment/installer/agent/unix/paas/latest?Api-Token=$DT_API_TOKEN&$DT_ONEAGENT_OPTIONS" && \
#    unzip -d "$DT_HOME" "$DT_HOME/oneagent.zip" && \
#    rm "$DT_HOME/oneagent.zip"
#ENTRYPOINT [ "/opt/dynatrace/oneagent/dynatrace-agent64.sh" ]

CMD java -jar emart-frontend-0.0.1-SNAPSHOT.jar

USER 1005
