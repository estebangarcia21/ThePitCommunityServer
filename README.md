## Setup

1. Install Amazon Corretto 17 https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html
2. Run `python3 scripts/download_dev_server.py`
3. Run `python3 scripts/dev_server.py` to compile the plugin and run the development server

## Running Spigot buildtools

You must run the `buildtools.py` script to build the Spigot and CraftBukkit Maven repositories. Ensure you are using 
Java 8 or Java 7 to run the script.

```shell
python scripts/buildtools.py
```

### For Windows Developers
By default, the script will use java from the `JAVA_HOME` environment variable to locate the Java executable. If you
want to override the Java executable, set the `BUILD_TOOLS_JAVA_PATH` environment variable to the full path of the Java
executable. For example:
```shell
# Powershell
$env:BUILD_TOOLS_JAVA_PATH = "C:\Program Files\Java\jdk1.8.0_202\bin\java.exe"; python scripts/buildtools.py

# CMD
set "BUILD_TOOLS_JAVA_PATH=C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" && python scripts\buildtools.py
```
