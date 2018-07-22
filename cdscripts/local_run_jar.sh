mvn clean package

VERSION=`mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q`
ARTIFACT=`mvn -Dexec.executable='echo' -Dexec.args='${project.artifactId}' --non-recursive exec:exec -q`

# for debugging purposes
#java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar ./target/$ARTIFACT-$VERSION-jar-with-dependencies.jar

java -jar ./target/$ARTIFACT-$VERSION-jar-with-dependencies.jar
