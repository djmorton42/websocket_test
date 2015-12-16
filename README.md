# websocket_test
This is a small prototype project for experimenting with Websocket and JEE 7.

- This project may be built using Maven
- The resulting war is designed to be deployed on Wildfly 8.2 application server
  - Other app servers which support JEE 7 will work, except that the default context will be used rather than the one specified in the jboss-web.xml
- The mpstat Linux utility is expected to be available on the system running Wildfly
- The UI may be accessed from http://localhost:8080/websocket_test (or whatever server/port your app server is bound to)
