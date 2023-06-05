import subprocess
import os
import sys

MAX_SERVER_MEMORY_GB = 4

root_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..')


def run_gradle_task(task):
    command = ["./gradlew", task]

    process = subprocess.Popen(
        command,
        cwd=root_dir,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        universal_newlines=True,
    )

    while process.poll() is None:
        output = process.stdout.readline().rstrip()
        if output:
            print(output)

    exit_code = process.poll()

    if exit_code != 0:
        print("Gradle task failed with exit code:", exit_code)
        sys.exit(1)

    return exit_code


def start_minecraft_server(max_memory):
    local_server_dir = os.path.join(root_dir, '.local-server')

    command = [
        'java',
        f'-Xmx{max_memory}G',
        f'-Xms{max_memory}G',
        '-jar',
        'server.jar',
        'nogui'
    ]

    process = subprocess.Popen(command, cwd=local_server_dir, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

    while True:
        output = process.stdout.readline().decode().rstrip()
        if output == '' and process.poll() is not None:
            break
        if output:
            print(output)

    return process.poll()


print('Building plugin...')
run_gradle_task('localBuild')

exit_code = start_minecraft_server(MAX_SERVER_MEMORY_GB)

if exit_code == 0:
    print("Server stopped successfully.")
else:
    print("Server stopped with an error. Exit code:", exit_code)
