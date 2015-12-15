# Arquillian Liferay
Arquillian Extension for Liferay Portal Server. OSGi incontainer deployment.

##What is this?

*Arquillian extension for Liferay* is a set of tools to help developers test their Liferay plugins.

It consists of:

* Arquillian Remote Container for Liferay (OSGi based)
* Arquillian Deployment Scenario Generator for OSGi SDK plugins based on BND
* Arquillian Deployment Scenario Generator for legacy Maven plugins

##How to use it?

We have defined a complete example of how to use it in [arquillian-extension-liferay-example](arquillian-extension-liferay-example/README.md)

##Build Status

[![Build Status](https://arquillian.ci.cloudbees.com/buildStatus/icon?job=Arquillian-Extension-Liferay)](https://arquillian.ci.cloudbees.com/job/Arquillian-Extension-Liferay/)

![codecov.io](https://codecov.io/github/arquillian/arquillian-extension-liferay/coverage.svg?branch=master)

## Testing Pull Requests
If you want any pull request you receive to be automatically tested by Travis CI, please set up your job directly in Travis.

- Go to [http://travis-ci.org/profile](http://travis-ci.org/profile)
- Enable Travis for arquillian-extension-liferay Github repository
- Click on the Settings icon.
- Enable 'Build pull requests' option element.

With those simple steps pulls will be tested against one of the most popular Open Source CI systems nowadays.

## Keeping Travis CI up-to-date
Anytime you add a dependency on the build system, verify that it is properly configured in the Travis CI descriptor, the [.travis.yml](.travis.yml) file, so that pulls there don't get broken.