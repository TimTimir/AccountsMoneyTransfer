call mvn clean package

for /f %%i in ('call mvn -q --non-recursive "-Dexec.executable=cmd" "-Dexec.args=/C echo ${project.version}" "org.codehaus.mojo:exec-maven-plugin:exec"') do set VERSION=%%i
set ARTIFACT="accounts-money-transfer"

# for debugging purposes
#java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar ./target/$ARTIFACT-$VERSION-jar-with-dependencies.jar

java -jar ./target/$ARTIFACT-$VERSION-jar-with-dependencies.jar
