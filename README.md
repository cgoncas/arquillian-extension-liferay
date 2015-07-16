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

##Build Status

[![Build Status](https://arquillian.ci.cloudbees.com/buildStatus/icon?job=Arquillian-Extension-Liferay)](https://arquillian.ci.cloudbees.com/job/Arquillian-Extension-Liferay/)
