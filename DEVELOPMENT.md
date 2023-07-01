# Developing Gitnuro

## Step-by-step installation (Linux, macOS and Windows)

1. [Download and install "vcpkg"](https://vcpkg.io/en/getting-started.html) (recommended to install "libssh" on Windows).
2. [Download and install "libssh"](https://www.libssh.org/get-it/).
3. [Download and install "Java(TM) SE Development Kit 17"](https://www.oracle.com/java/technologies/downloads/#java17).
4. [Download and install "IntelliJ IDEA Community Edition" using "JetBrains Toolbox App"](https://www.jetbrains.com/help/idea/installation-guide.html#toolbox).
5. Open "IntelliJ IDEA Community Edition" from "JetBrains Toolbox App".
6. Go into "'File' -> 'Settings...' -> Plugins".
7. Search and install "Compose Multiplatform IDE Support".
8. Go into "'File' -> 'Settings...' -> 'Build, Execution, Deployment' -> 'Build Tools' -> 'Gradle'".
9. Select the project ("Gitnuro") under "Gradle Projects".
10. Select "17 Oracle OpenJDK version 17.0.7" in the "Gradle JVM" dropdown field.

### Troubleshoot

#### Building with Gradle fails (java.io.StreamCorruptedException: invalid type code: 00)

Make sure the "Gradle JVM" version is identical to the project Java version.
To check the project Java version, go into "build.gradle.kts" at
"val javaLanguageVersion = JavaLanguageVersion.of".

## Alternative: Setting up JDK for use on CLI

You don't need this if you only use the JDK installed by IntelliJ IDEA.

Check which Java version this project currently uses (`cat build.gradle.kts | grep JavaLanguageVersion`) and install it.
For instance, on Debian-based systems, you'd run:

```bash
sudo apt-get install openjdk-17-jre openjdk-17-jdk libssh-dev
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

Once it works (e.g. `./gradlew build`), you may want to add that latter line to your `/etc/environment`.

## Running the app / unit tests

From the "Gradle" window in the IDE, under "Tasks" select "compose desktop > run" or "verification > test"
for the main app or unit tests, respectively, and run it.
Next time, it will already be in the "Run Configurations" at the top right of the IDE, so you
won't have to open the "Gradle" window again.
You can also run these in debug mode and set break points in the code.

Alternatively, on CLI: `./gradlew run` or `./gradlew test`.

## IDE and dependencies details
> Why I need to install "libssh"?

Gitnuro depends on libssh being present as an external, native library
(using [JNA](https://github.com/java-native-access/jna)).
While the release GitHub workflow packages it automatically, you'll need to install it manually when developing locally,
such that it's available on the `$PATH`. See [here](https://www.libssh.org/get-it/) for one-liner installation
instructions with your OS's package manager, or manually download a binary or compile it from source and place it in the
main project directory (next to `LICENSE`) or elsewhere on your `$PATH`.

> Can I use another IDE?

If you have other preferences, go with it, but the recommendation is to use
"IntelliJ IDEA Community Edition" with the plugin "Compose Multiplatform IDE Support".