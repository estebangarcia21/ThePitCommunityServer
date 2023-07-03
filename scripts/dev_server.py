import platform
import subprocess
import os
import sys
import urllib.request

MAX_SERVER_MEMORY_GB = 4
PROJECT_ROOT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
LOCAL_SERVER_DIR = os.path.join(PROJECT_ROOT_DIR, ".local-server")
PLUGIN_DIR = os.path.join(LOCAL_SERVER_DIR, "plugins")
DEPENDENCIES = {
    "Citizens.jar": "https://ci.citizensnpcs.co/job/Citizens2/lastSuccessfulBuild/artifact/dist/target/Citizens-2.0.32-b3148.jar"
}


def download_dependency(dep_url, filename):
    print(f"Downloading {filename}...")
    try:
        opener = urllib.request.build_opener()
        opener.addheaders = [('User-agent', 'Mozilla/5.0')]
        urllib.request.install_opener(opener)
        urllib.request.urlretrieve(dep_url, os.path.join(PLUGIN_DIR, filename))
        print(f"Downloaded {filename} successfully.")
    except Exception as e:
        print(f"Failed to download {filename}: {e}")
        sys.exit(1)


def ensure_directory_exists(directory):
    if not os.path.exists(directory):
        os.makedirs(directory)


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


def start_minecraft_server(max_memory, env_options):
    if platform.system() == "Windows":
        command = [
            "cmd.exe",
            "/C",
            "java",
            f"-Xmx{max_memory}G",
            f"-Xms{max_memory}G",
        ]
    else:
        command = [
            "java",
            f"-Xmx{max_memory}G",
            f"-Xms{max_memory}G",
        ]

    command += [
        "-jar",
        "server.jar",
        "nogui",
    ]

    env = os.environ.copy()
    env.update(env_options)

    process = subprocess.Popen(
        command,
        cwd=LOCAL_SERVER_DIR,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        universal_newlines=True,
        shell=True if platform.system() == "Windows" else False,
        env=env,
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

ensure_directory_exists(PLUGIN_DIR)

for dependency, url in DEPENDENCIES.items():
    dependency_path = os.path.join(PLUGIN_DIR, dependency)
    if not os.path.exists(dependency_path):
        download_dependency(url, dependency)

env_options = {}
if "--" in sys.argv:
    env_index = sys.argv.index("--") + 1
    if env_index < len(sys.argv):
        env_options = dict(option.split("=") for option in sys.argv[env_index:])

mc_server_exit_code = start_minecraft_server(MAX_SERVER_MEMORY_GB, env_options)

if mc_server_exit_code == 0:
    print("Server stopped successfully.")
else:
    print("Server stopped with an error. Exit code:", mc_server_exit_code)
