npm run build --prefix ./frontend/
mv ./frontend/target/dist/* ./kafka-helper-web/src/main/resources/
./gradlew -p ./kafka-helper-web clean build
docker rmi kafka-helper-web
docker build -t kafka-helper-web ./kafka-helper-web
docker-compose up -d
