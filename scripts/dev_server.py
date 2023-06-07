import platform
import subprocess
import os
import sys

MAX_SERVER_MEMORY_GB = 4
PROJECT_ROOT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")


def run_gradle_task(task):
    gradle_command = "gradlew.bat" if platform.system() == "Windows" else "./gradlew"
    command = [gradle_command, task]

    process = subprocess.Popen(
        command,
        cwd=PROJECT_ROOT_DIR,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        universal_newlines=True,
        shell=True if platform.system() == "Windows" else False,
    )

    while process.poll() is None:
        output = process.stdout.readline().rstrip()
        if output:
            print(output)

    exit_code = process.wait()

    if exit_code != 0:
        print("Gradle task failed with exit code:", exit_code)
        sys.exit(1)

    return exit_code


def start_minecraft_server(max_memory):
    local_server_dir = os.path.join(PROJECT_ROOT_DIR, ".local-server")

    if platform.system() == "Windows":
        command = [
            "cmd.exe",
            "/C",
            "java",
            f"-Xmx{max_memory}G",
            f"-Xms{max_memory}G",
            "-jar",
            "server.jar",
            "nogui",
        ]
    else:
        command = [
            "java",
            f"-Xmx{max_memory}G",
            f"-Xms{max_memory}G",
            "-jar",
            "server.jar",
            "nogui",
        ]

    process = subprocess.Popen(
        command,
        cwd=local_server_dir,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        universal_newlines=True,
        shell=True if platform.system() == "Windows" else False,
    )

    while True:
        output = process.stdout.readline().rstrip()
        if output == "" and process.poll() is not None:
            break
        if output:
            print(output)

    return process.poll()


print("Building plugin...")
run_gradle_task("localBuild")

mc_server_exit_code = start_minecraft_server(MAX_SERVER_MEMORY_GB)

if mc_server_exit_code == 0:
    print("Server stopped successfully.")
else:
    print("Server stopped with an error. Exit code:", mc_server_exit_code)
