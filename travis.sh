#!/bin/bash

set -euo pipefail

echo SALVAME $TRAVIS_PULL_REQUEST

# Run the tests
if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then

    docker run -d --name sonarqube -p 9000:9000 -p 9092:9092 sonarqube:5.1

    # For security reasons environment variables are not available on the pull requests
    # coming from outside repositories
    # http://docs.travis-ci.com/user/pull-requests/#Security-Restrictions-when-testing-Pull-Requests
    if [ -n "$SONAR_GITHUB_OAUTH" ]; then

        # Switch to java 8 as the Dory HTTPS certificate is not supported by Java 7
        export JAVA_HOME=/usr/lib/jvm/java-8-oracle
        export PATH=$JAVA_HOME/bin:$PATH

        cd ${TRAVIS_BUILD_DIR}

        echo "Analyze pull request"

        mvn clean install sonar:sonar -B -e -V\
         -Dmaven.test.failure.ignore=true \
         -Dclirr=true \
         -Dsonar.analysis.mode=issues \
         -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
         -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
         -Dsonar.github.login=$SONAR_GITHUB_LOGIN \
         -Dsonar.github.oauth=$SONAR_GITHUB_OAUTH \
        exit $?
    fi
fi