#FROM openjdk:8-jdk-alpine
FROM adoptopenjdk/openjdk14

COPY target/audit-manager-0.0.1.jar audit-manager.jar
COPY GCP-MyFirstProject-1c31159db52c.json GCP-MyFirstProject-1c31159db52c.json
COPY api_key.p12 /etc/certs/api_key.p12
COPY gmail_credentials.json /etc/creds/gmail_credentials.json
EXPOSE 443
EXPOSE 80
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-ea","-jar", "audit-manager.jar"]
