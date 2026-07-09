# 1. Aşama: Uygulamayı derleme (Build) - Java 17 tabanı korunuyor
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# [ÖNEMLİ EKLEME]: Önce sadece pom.xml kopyalanır ve kütüphaneler Docker önbelleğine (cache) alınır.
# Bu sayede src klasöründe kod değiştirsen bile Maven kütüphaneleri tekrar tekrar indirmez.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Senin orijinal kaynak kod kopyalama ve paketleme adımların aynen devam ediyor
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Aşama: Sadece JAR dosyasını çalıştıracak hafif imaj (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# İlk aşamada (build) üretilen JAR dosyası bu hafif Alpine katmanına aktarılıyor
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]