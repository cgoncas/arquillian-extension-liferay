arquillian-extension-liferay
============================

Arquillian Extension for Liferay Portal Server. OSGi incontainer deployment.

[![Build Status](https://arquillian.ci.cloudbees.com/buildStatus/icon?job=Arquillian-Extension-Liferay)](https://arquillian.ci.cloudbees.com/job/Arquillian-Extension-Liferay/)

## Testing Pull Requests
If you want any pull request you receive to be automatically tested by Travis CI, please set up your job directly in Travis.

- Go to [http://travis-ci.org/profile](http://travis-ci.org/profile)
- Enable Travis for arquillian-extension-liferay Github repository
- Click on the Settings icon.
- Enable 'Build pull requests' option element.

With those simple steps pulls qill be tested agains one of the most popular Open Source CI systems nowdays.

## Keeping Travis CI up-to-date
Anytime you add a dependency on the build system, verify that it is properly configured in the Travis CI descriptor, the [.travis.yml](.travis.yml) file, so that pulls there don't get broken.