I use gradle 5.4.1 and JDK 1.8 

to compile 

`gradle build && gradle shadowJar`

to run
`java -jar build/libs/messaging-0.0.1-SNAPSHOT-all.jar`

to run tests

`gradle test`

Steps

* So the first step find a way to use core Java to create a HTTP, I decided to with com.sun.net.httpserver.HttpServer after researching posibilities
* So important stuff missing from a framework was the URL handling and error handling so added a few methods to take care of that
* Second step I decided to model the data the rest endpoints would recieve and also decided to do some JSON schema validation, since the schemas were provided
* After having basic HTTP server functionality and models, models validation, I decided to save messages in memory, which helped test the first two endpoints, but then I thought it would be a good idea to persist data in a database, I decided to go with a NoSQL option, in this case I used Cassandra.
* After having I starting testing the flows using a docker container of Cassandra, and decided to start writing integration tests using an embedded cassandra version
* Using the DB I think I could solve the message out of order and the sorting of the messages by timestamp

