@echo off

REM - sbt-launcher.jar script - will download sbt-launcher.jar, if required

REM Author - Richard Joseph [sixman9-at-gmail-dot-com]

SET SBT_LAUNCH_VER=0.13.5

SET SCRIPT_DIR=%~dp0

REM SBT Launcher JAR naming components
SET SBT_LAUNCH_JAR_NAME_PREFIX=sbt-launch
SET JAR_EXT=jar

REM sbt-launch-<version>.jar
SET SBT_LAUNCH_JAR_REPO_NAME=%SBT_LAUNCH_JAR_NAME_PREFIX%.%JAR_EXT%

SET SBT_LAUNCH_JAR_LOCAL_NAME=%SBT_LAUNCH_JAR_NAME_PREFIX%-%SBT_LAUNCH_VER%.%JAR_EXT%
SET SBT_LAUNCH_JAR_LOCAL_PATH=%SCRIPT_DIR%\%SBT_LAUNCH_JAR_LOCAL_NAME%

SET SBT_LAUNCH_JAR_URL=http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/%SBT_LAUNCH_VER%/%SBT_LAUNCH_JAR_REPO_NAME%

REM Download the sbt-launch.jar, if not already present
if NOT EXIST "%SBT_LAUNCH_JAR_LOCAL_PATH%" (
	REM Go - download the sbt-launch.jar!
	@powershell -NoProfile -ExecutionPolicy unrestricted -Command "(new-object System.Net.WebClient).DownloadFile('%SBT_LAUNCH_JAR_URL%', '%SBT_LAUNCH_JAR_LOCAL_PATH%')"
)

REM Run SBT Launcher jar
java -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256m -Xmx1024M -Xss2M -jar "%SBT_LAUNCH_JAR_LOCAL_PATH%" %*
