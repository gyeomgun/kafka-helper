# kafka-helper

## feature
- KMS(Key Management Service). Managed your Consumer group id (Do not allow duplicate)
- Switching Consumer turn on/off
- Deadletter. Manage exceptions that occur in business logic
- Logging Message. Entire message logging

## project
```
kafka-helper
+-kafka-helper-adapter      //kafka consuer, publisher wrapper
+-kafka-helper-web          //management api server
+-frontend                  //frontend(Vue.js)
+-kafka-helper-domain       //infrastructure (database configuration..)
+-kafka-helper-batch        //TBD
+-kafka-helper-interface    //TBD
+-consumer-example          //kafka-helper-adapter consumer example
+-publisher-example         //kafka-helper-adapter publisher example
```

## screen shot 
<img width="1508" alt="1" src="https://user-images.githubusercontent.com/11720532/95019339-b9b2c680-069f-11eb-83ed-f70f8bc5c1c2.png">
<img width="1488" alt="2" src="https://user-images.githubusercontent.com/11720532/95019342-be777a80-069f-11eb-85fc-82c98106657a.png">
<img width="1488" alt="3" src="https://user-images.githubusercontent.com/11720532/95019343-bfa8a780-069f-11eb-8e5b-3cb5970b0841.png">
<img width="1488" alt="4" src="https://user-images.githubusercontent.com/11720532/95019344-c0d9d480-069f-11eb-838e-8eeb9c068372.png">

## how to run
- Docker, Docker-compose, npm..
1. `start_up.sh`
2. start kafka-helper-web 
3. connect `http://localhost:8081`
4. shut down `docker-compose down`
5. start consumer example
  <img width="850" alt="스크린샷 2020-10-05 오전 12 27 48" src="https://user-images.githubusercontent.com/11720532/95019664-a3a60580-06a1-11eb-838b-43fdf816f312.png">
   
6. start publisher example
  <img width="707" alt="스크린샷 2020-10-05 오전 12 31 05" src="https://user-images.githubusercontent.com/11720532/95019740-17e0a900-06a2-11eb-8436-670c075810a6.png">
   
7. publish `curl http://localhost:8093/pub`
8. You can check message log and deadletter

 

