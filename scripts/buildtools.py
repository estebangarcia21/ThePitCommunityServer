import os
import platform
import shutil
import subprocess
import urllib.request

# Set the desired Spigot version
spigot_version = "1.8.8"

# Define the URLs for Spigot Build Tools and the Java version
build_tools_url = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"

# Define the file names for the downloaded files
build_tools_file = "buildtools.jar"

# Determine the absolute path of the script's directory
script_dir = os.path.dirname(os.path.abspath(__file__))
build_tools_dir = os.path.join(script_dir, "buildtools")

try:
    shutil.rmtree(build_tools_dir)
    print("Previous buildtools directory removed.")
except FileNotFoundError:
    pass

# Create the buildtools/ directory if it doesn't exist
os.makedirs(os.path.join(script_dir, "buildtools"), exist_ok=True)

# Change to the buildtools/ directory
os.chdir(os.path.join(script_dir, "buildtools"))

# Download Spigot Build Tools
print("Downloading Spigot Build Tools...")
urllib.request.urlretrieve(build_tools_url, build_tools_file)

# Build Spigot with the desired version
print(f"Building Spigot {spigot_version}...")

# Change working directory to buildtools/ for the build command
os.chdir(script_dir)

# Determine the Java command based on the platform
java_command = "java"

subprocess.run(f"{java_command} -jar {build_tools_file} --rev {spigot_version}", cwd=build_tools_dir, shell=True)

shutil.rmtree(build_tools_dir)

print("Spigot build completed.")
