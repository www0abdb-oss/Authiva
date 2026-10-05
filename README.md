<div align="center">

# Authiva

Lightweight authentication plugin for Minecraft servers.

[![Downloads](https://img.shields.io/hangar/dt/Authiva?style=flat-square)](https://hangar.papermc.io/www0abdb-oss/Authiva)
[![GitHub Stars](https://img.shields.io/github/stars/www0abdb-oss/Authiva?style=flat-square)](https://github.com/www0abdb-oss/Authiva)

![Authiva Statistics](https://bstats.org/signatures/bukkit/Authiva.svg)

[![Hangar](https://img.shields.io/badge/Hangar-Authiva-03a9f4?logo=hangar&logoColor=white)](https://hangar.papermc.io/www0abdb-oss/Authiva)
[![Modrinth](https://img.shields.io/badge/Modrinth-Authiva-00AF5C?logo=modrinth&logoColor=white)](https://modrinth.com/plugin/authiva)
[![GitHub](https://img.shields.io/badge/GitHub-Authiva-181717?logo=github&logoColor=white)](https://github.com/www0abdb-oss/Authiva)
[![Issues](https://img.shields.io/badge/Issues-Report-F97316?logo=github&logoColor=white)](https://github.com/www0abdb-oss/Authiva/issues)


</div>

## About

Authiva is a lightweight, server-side authentication plugin for Minecraft servers.

No client-side mod is required.

## Features

* Player registration and login
* Password management
* Login protection
* Authentication sessions
* SQLite storage
* Configurable security
* Lightweight server-side implementation

## Commands

```text
/register <password> <confirmPassword>
/login <password>
/logout
/changepassword <currentPassword> <newPassword>
/unregister <password>
```

## Admin Commands

```text
/authiva help
/authiva info
/authiva list
/authiva logout
/authiva setpassword
/authiva unregister
/authiva delete
/authiva reload
```

## Compatibility

<div align="center">

| Platform       | Support   |
| -------------- | --------- |
| Purpur         | Supported |
| Spigot         | Supported |
| Paper          | Supported |
| Bukkit         | Supported |
| Minecraft 26.2 | Supported |
| Minecraft 26.3 | Partial   |

</div>

## Installation

1. Download the latest Authiva `.jar`.
2. Place the file into your server's `plugins` folder.
3. Start or restart your Minecraft server.
4. Configure Authiva to match your server's requirements.

## License

Authiva is licensed under the Apache License 2.0.
