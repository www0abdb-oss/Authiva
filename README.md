# Authiva

Lightweight server-side authentication for Minecraft.

Authiva is a lightweight authentication plugin designed to provide simple player registration and login protection for Minecraft servers. It focuses on keeping authentication straightforward, secure, and easy to configure without requiring players to install a client-side mod.

## Features

* Player registration
* Player login
* Authentication protection
* Secure password hashing
* Configurable authentication timeout
* Configurable login attempt limits
* SQLite account storage
* Server-side only
* No client-side mod required
* Lightweight and simple configuration

## How It Works

When a player joins the server, Authiva checks whether the player already has an account.

New players can register:

```text
/register <password>
```

Returning players can authenticate:

```text
/login <password>
```

Until authentication is completed, restricted player actions can be blocked to prevent unauthorized access to the account.

## Commands

| Command                       | Description                      |
| ----------------------------- | -------------------------------- |
| `/register <password>`        | Create a new account             |
| `/login <password>`           | Authenticate an existing account |
| `/changepassword <old> <new>` | Change the account password      |

Additional commands may be added in future releases.

## Security

Authiva is designed with account security in mind.

Passwords should never be stored as plain text. Authiva uses password hashing so that stored credentials cannot simply be read from the account database.

Authentication attempts can also be limited to reduce repeated password-guessing attempts.

## Configuration

Authiva uses a simple configuration file:

```yaml
login-timeout: 60
max-login-attempts: 5
password-min-length: 8
storage: sqlite
```

Configuration options may change as the project develops.

## Installation

1. Download the latest Authiva release.
2. Place the Authiva `.jar` file into the server's `plugins` directory.
3. Start the server.
4. Authiva will create its configuration and data files.
5. Configure Authiva if necessary.
6. Restart the server.

Players do not need to install Authiva on their Minecraft clients.

## Requirements

* Minecraft server
* Paper or a compatible Paper-based server
* Java version required by the supported Minecraft version

Supported Minecraft versions and platform versions will be listed for each release.

## Development

Clone the repository:

```bash
git clone https://github.com/www0abdb-oss/Authiva.git
cd Authiva
```

Build the project:

```bash
./gradlew build
```

The compiled plugin will be located in:

```text
build/libs/
```

## Project Structure

```text
Authiva/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── README.md
├── LICENSE
└── .gitignore
```

## Project Status

Authiva is currently in early development.

The initial development focus is:

* Registration
* Login
* Authentication protection
* Secure password storage
* SQLite support
* Basic configuration

Features will be introduced gradually as the project develops.

## License

Authiva is open source.

See the `LICENSE` file for the applicable license terms.

## Links

* [GitHub](https://github.com/www0abdb-oss/Authiva)
* Hangar: Coming soon

## Contributing

Suggestions, bug reports, and improvements are welcome.

Before submitting changes, make sure the project builds successfully:

```bash
./gradlew build
```

## Disclaimer

Authiva is provided as an authentication solution for Minecraft servers. Server administrators are responsible for configuring and operating their servers securely.

<div align="center">

# Authiva

Lightweight and configurable authentication plugin for Paper servers.

<br>

<a href="https://hangar.papermc.io/www0abdb-oss/Authiva">
  <img src="https://modfolio.creeperkatze.dev/hangar/project/Authiva/downloads" alt="Authiva Downloads">
</a>
&nbsp;&nbsp;
<a href="https://github.com/www0abdb-oss/Authiva">
  <img src="https://img.shields.io/github/stars/www0abdb-oss/Authiva?style=flat&label=GitHub%20Stars" alt="GitHub Stars">
</a>

<br><br>

<a href="https://hangar.papermc.io/www0abdb-oss/Authiva">Hangar</a>
 •  <a href="https://github.com/www0abdb-oss/Authiva">Source Code</a>
 •  <a href="https://github.com/www0abdb-oss/Authiva/issues">Issues</a>

</div>

