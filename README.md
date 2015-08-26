# Arquillian Liferay
Arquillian Extension for Liferay Portal Server. OSGi incontainer deployment.

##What is this?

*Arquillian Liferay* is an Arquillian extension that can be used to execute tests in a Liferay Container.

With this extension you can deploy a OSGi Bundle within Liferay and execute Arquillian tests.

##Basic Example

First of all we need to configure our Liferay to execute the Arquillian tests. This is example will be executed in the next environment:

* Tomcat Server 7.0.62
  * JMX enabled and configured.
  * Tomcat Manager installed and configured.
* Liferay 7.0.0

### Configure Liferay Tomcat Server

#### Enable and Configure JMX in tomcat

You can follow this [guide](https://tomcat.apache.org/tomcat-7.0-doc/monitoring.html#Enabling_JMX_Remote) to enable your JMX congifuration in tomcat 

In the next example you can see a example of a **setenv** file that enable JMX in a Tomcat in the port 8099 whithout authentication:

```sh
CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=UTF8 -Djava.net.preferIPv4Stack=true -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false -Duser.timezone=GMT -Xmx1024m -XX:MaxPermSize=256m"

JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=8099 -Dcom.sun.management.jmxremote.ssl=false"

CATALINA_OPTS="${CATALINA_OPTS} ${JMX_OPTS}"
```

##Build Status

[![Build Status](https://arquillian.ci.cloudbees.com/buildStatus/icon?job=Arquillian-Extension-Liferay)](https://arquillian.ci.cloudbees.com/job/Arquillian-Extension-Liferay/)
