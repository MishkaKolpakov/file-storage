FROM java:8-jdk-alpine

ENV POSTGRES_HOST=postgres

WORKDIR /app

COPY target/auth-service-0.0.1-SNAPSHOT.jar .
COPY rootfs/ .

ENTRYPOINT ["/bin/sh"]
CMD ["/app/app-entrypoint.sh"]