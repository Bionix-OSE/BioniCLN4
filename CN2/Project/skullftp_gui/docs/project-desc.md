# Project: Build an FTP Client in Java
### 0. Goal:
Implement a FTP Client using Java that can interact with an FTP server .
### 1. Allowed Libraries (Strict Restriction)
Only the following Java packages are allowed:
- `java.io.*`
- `java.net.*`
- `java.util.*`
- No third-party libraries or additional Java packages are permitted.
### 2. Required Features
- Connect to FTP server (default port 21)
- Anonymous login and custom username/password login
- `pwd` (PWD command)
- `cd` (CWD command)
- `ls` using PASV + LIST
- `get` using PASV + RETR (binary mode)
- `put` using PASV + STOR (binary mode)
- `delete` (DELE command)
- `mkdir` (MKD command)
- `rmdir` (RMD command)
- `quit` (QUIT command)
### 3. Technical Requirements
- Must correctly handle control connection and separate data connection.
- Must correctly parse FTP reply codes, including multiline responses.
- Must properly close sockets and streams.
- Must handle errors gracefully.
