# Authiva

Lightweight, secure, and configurable server-side authentication for Minecraft.

Authiva is a server-side authentication plugin for Paper-based Minecraft servers. It provides player registration, login protection, password management, session handling, and configurable authentication security without requiring any client-side mod.

Authiva is designed to keep authentication simple for players while giving server administrators control over security, storage, timeouts, and authentication behavior.

## Features

* Player registration and login
* Password change and account management
* Login and authentication protection
* Configurable authentication timeout
* Configurable login attempt limits
* Secure password hashing with PBKDF2-HMAC-SHA256
* SQLite account storage
* Authentication session management
* Authentication bypass permissions
* Asynchronous authentication processing
* Automatic update checking
* Server-side only
* No client-side mod required
* Lightweight configuration
* bStats integration
* FastStats integration
* Paper-focused development

## Commands

### Player Commands

| Command                                           | Description                      |
| ------------------------------------------------- | -------------------------------- |
| `/register <password> <confirmPassword>`          | Create a new account             |
| `/login <password>`                               | Authenticate an existing account |
| `/logout`                                         | Log out of the current account   |
| `/changepassword <currentPassword> <newPassword>` | Change the account password      |
| `/unregister <password>`                          | Remove the current account       |

### Admin Commands

| Command                | Description                           |
| ---------------------- | ------------------------------------- |
| `/authiva help`        | Display Authiva help                  |
| `/authiva info`        | Display Authiva information           |
| `/authiva list`        | List registered accounts              |
| `/authiva logout`      | Manage player authentication sessions |
| `/authiva setpassword` | Set a player's password               |
| `/authiva unregister`  | Unregister a player's account         |
| `/authiva delete`      | Delete an account                     |
| `/authiva reload`      | Reload Authiva configuration          |

Administrative commands may require the appropriate Authiva permission.

## Authentication

When a player joins the server, Authiva checks whether an account exists for them.

Players without an account can register using:

```text
/register <password> <confirmPassword>
```

Players with an existing account must authenticate using:

```text
/login <password>
```

Until authentication is completed, Authiva can restrict protected player actions according to the server configuration.

Authentication sessions are managed by Authiva and can expire according to the configured authentication timeout.

## Security

Authiva is designed with password and authentication security in mind.

Passwords are not stored as plain text. Authiva uses PBKDF2-HMAC-SHA256 password hashing for stored credentials.

Authiva also provides configurable authentication controls such as:

* Authentication timeouts
* Maximum login attempts
* Minimum password length
* Authentication session handling
* Account management
* Optional authentication bypass permissions

Server administrators should still use strong passwords and secure server configurations.

## Storage

Authiva currently uses SQLite for account storage.

The authentication database is maintained by the plugin and does not require a separate database server for basic operation.

## Configuration

Authiva provides a configurable YAML configuration file for authentication behavior.

Configuration includes options related to:

* Login timeout
* Maximum login attempts
* Password requirements
* Authentication behavior
* Storage
* Authentication bypass
* Other security and plugin settings

The configuration may evolve between beta releases.

## Permissions

Authiva supports permissions for administrative and authentication-related functionality.

For example, trusted players or services can be granted an authentication bypass permission where appropriate.

Administrators should only grant Authiva permissions to trusted users or services.

## Metrics & Privacy

Authiva integrates two anonymous metrics services:

### bStats

Authiva uses bStats to collect anonymous and aggregated server/plugin statistics.

bStats may collect information such as:

* Minecraft/server software and version
* Java version
* Operating system
* Player counts
* Approximate country
* Plugin metrics

Server administrators can disable bStats globally through its configuration.

### FastStats

Authiva also uses FastStats for anonymous plugin metrics.

FastStats collects standard anonymous information such as:

* Server/player counts
* Minecraft and platform versions
* Plugin versions
* Operating system
* CPU architecture
* Java information
* Approximate country

FastStats does not require Authiva to send authentication credentials.

Server administrators can disable FastStats through its configuration.

Authiva does not use either metrics service to send player passwords or authentication credentials.

## Updates

Authiva includes an automatic update checker that can notify server administrators when a newer Authiva release is available.

Update notifications do not automatically install or replace the plugin.

## Installation

1. Download the latest Authiva release.
2. Place the Authiva `.jar` file into the server's `plugins` directory.
3. Start the server.
4. Authiva will create its required configuration and data files.
5. Review the configuration.
6. Restart the server if required.

Players do not need to install anything on their Minecraft clients.

## Requirements

* Paper or a compatible Paper-based server
* Minecraft 26.2 for the current beta release
* Java version required by the supported Minecraft/Paper version

Supported Minecraft and server versions are listed with each release.

## Development

Clone the repository:

```bash
git clone https://github.com/www0abdb-oss/Authiva.git
cd Authiva
```

Build Authiva:

```bash
./gradlew build
```

The compiled JAR files are generated in:

```text
build/libs/
```

For the release build, the shaded plugin JAR is generated as:

```text
build/libs/Authiva-<version>-all.jar
```

## Project Structure

```text
Authiva/
├── src/
│   └── main/
│       ├── java/
│       │   └── www0abdb/
│       │       └── oss/
│       │           └── authiva/
│       └── resources/
│           ├── config.yml
│           └── plugin.yml
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── README.md
├── LICENSE
└── .gitignore
```

## Project Status

Authiva is currently in beta development.

The `0.1.0-beta2` release focuses on expanding the authentication system beyond basic registration and login.

Current development includes:

* Registration and login
* Authentication protection
* Password security
* Password management
* Account management
* SQLite storage
* Authentication sessions
* Configurable security controls
* Administrative commands
* Authentication bypass support
* Asynchronous authentication
* Update checking
* Anonymous metrics
* Improved configuration
* Improved stability

Authiva is still under active development, and configuration, commands, and internal behavior may change in future releases.

## License

Authiva is open source.

See the `LICENSE` file for the applicable license terms.

## Links

* [GitHub](https://github.com/www0abdb-oss/Authiva)
* [Hangar](https://hangar.papermc.io/www0abdb-oss/Authiva)
* [Issues](https://github.com/www0abdb-oss/Authiva/issues)

## Contributing

Suggestions, bug reports, and improvements are welcome.

Before submitting changes, make sure the project builds successfully:

```bash
./gradlew clean build
```

For bug reports, include the Authiva version, Minecraft/Paper version, relevant configuration, and the server log information necessary to reproduce the issue. Never include player passwords or other authentication credentials.

## Disclaimer

Authiva is provided as an authentication solution for Minecraft servers.

Server administrators are responsible for configuring and operating their servers securely. Authiva does not replace proper server security, access control, backups, or other server administration practices.

<div align="center">

# Authiva

Lightweight and configurable authentication plugin for Paper servers.

<br>

<a href="https://hangar.papermc.io/www0abdb-oss/Authiva">
  <img src="https://img.shields.io/hangar/dt/Authiva?link=https%3A%2F%2Fhangar.papermc.io%2FGipot%2FAuthiva&style=flat-square" alt="Authiva Downloads">
</a>
&nbsp;&nbsp;
<a href="https://github.com/www0abdb-oss/Authiva">
  <img src="https://img.shields.io/github/stars/www0abdb-oss/Authiva?style=flat&label=GitHub%20Stars" alt="GitHub Stars">
</a>

<br><br>

---

![Authiva Statistics](https://bstats.org/signatures/bukkit/Authiva.svg)

<br>

<a href="https://hangar.papermc.io/www0abdb-oss/Authiva">Hangar</a>
 •  <a href="https://github.com/www0abdb-oss/Authiva">Source Code</a>
 •  <a href="https://github.com/www0abdb-oss/Authiva/issues">Issues</a>

</div>
