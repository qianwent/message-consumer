## Overview

''steps to run kafka and zookeeper in local'':
1. D:\IT\kafka_2.11-2.4.0\bin\windows>zookeeper-server-start.bat ..\..\config\zookeeper.properties
2. D:\IT\kafka_2.11-2.4.0\bin\windows>kafka-server-start.bat ..\..\config\server.properties
3. D:\IT\kafka_2.11-2.4.0\bin\windows>kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic test
4. D:\IT\kafka_2.11-2.4.0\bin\windows>kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test
5. check all topics
D:\IT\kafka_2.11-2.4.0\bin\windows>kafka-topics --zookeeper localhost:2181 --list
6. check info of a topic
D:\IT\kafka_2.11-2.4.0\bin\windows>kafka-topics --describe --zookeeper localhost:2181 --topic test
7. use producer to produce messages using existing topic
D:\IT\kafka_2.11-2.4.0\bin\windows>kafka-console-producer.bat --broker-list localhost:9092 --topic test
