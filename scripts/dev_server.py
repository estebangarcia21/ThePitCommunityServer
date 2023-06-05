import subprocess
import os

MAX_SERVER_MEMORY_GB = 4


def start_minecraft_server(max_memory):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    local_server_dir = os.path.join(current_dir, '..', '.local-server')

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


exit_code = start_minecraft_server(MAX_SERVER_MEMORY_GB)

if exit_code == 0:
    print("Server stopped successfully.")
else:
    print("Server stopped with an error. Exit code:", exit_code)
