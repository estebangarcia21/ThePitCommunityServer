## Setup

You must have Java 17 and Java 8 installed on your system to run various scripts and compilation steps.


1. [Install Java 17 (Amazon Corretto)](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
2. Install Java 8. I
   recommend [downloading the LTS JDK 8 from AdoptOpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot).
3. Run `python3 scripts/download_dev_server.py` to download the development server.
4. Run `python3 scripts/dev_server.py` to compile the plugin and run the development server.
5. You are ready to start developing! Simply rerun the development server script to refresh any changes.

## Running Spigot buildtools

You must run the `buildtools.py` script to build the Spigot and CraftBukkit Maven repositories. Ensure you are using
Java 8 or Java 7 to run the script.

```shell
python scripts/buildtools.py
```

After running the  buildtools script, you must include the generated `spigot` jar in your classpath.

You can do this by going to File > Project Structure > Libraries in IntelliJ and adding the `spigot` jar file generated in the buildtools directory inside of the scripts directory

## For Windows Developers

### Note: JDK directory names maybe different on your system, so adjust the paths accordingly.

Setup two environment variables to make running the buildtools script easier:
`BUILD_TOOLS_JAVA_PATH` and
`DEV_SERVER_JAVA_PATH` 

By default, the script will use java from the `JAVA_HOME` environment variable to locate the Java executable. If you
want to override the Java executable, set the `BUILD_TOOLS_JAVA_PATH` environment variable to the full path of the Java
executable. For example:

```shell
# Powershell
$env:BUILD_TOOLS_JAVA_PATH = "C:\Program Files\Java\jdk1.8.0_202\bin\java.exe"; python scripts/buildtools.py

# CMD
set "BUILD_TOOLS_JAVA_PATH=C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" && python scripts\buildtools.py
```

For the development server, you can set the `DEV_SERVER_JAVA_PATH` environment variable in a similar way. This will allow you to use Java 17
for the development server while still using Java 8 for the buildtools script. If you want to avoid changing your `JAVA_HOME`.
```shell
# Powershell
$env:DEV_SERVER_JAVA_PATH = "C:\Program Files\Java\jdk-17.0.1\bin\java.exe"; python scripts/dev_server.py

# CMD
set "DEV_SERVER_JAVA_PATH=C:\Program Files\Java\jdk-17.0.1\bin\java.exe" && python scripts\dev_server.py
```


# DO NOT FORGET TO RUN THIS!
If you do not setup the DynamoDB database, the plugin will not work correctly, and your player joining will crash the server.
## Local Database (DynamoDB)

1. Ensure you have `docker` and `docker-compose` installed on your system.
2. Run `docker-compose up` in the `dynamodb` directory to start the local DynamoDB instance.
3. Run the `reset_schema.py` script in `scripts/db/reset_schema.py`. This will delete any pre-existing data and recreate
   a fresh environment.
