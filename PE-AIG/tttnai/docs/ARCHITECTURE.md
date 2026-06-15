# Tic-Tac-Toe Network (TTTNet) Architecture

## Overview
TTTNet is a client-server implementation of Tic-Tac-Toe where human players connect remotely and play against a computer opponent.

## Architecture Components

### Client-Server Model (Primary)
The system uses a network-based architecture where:
- **GameServer**: Central server that accepts multiple client connections
- **GameClient**: Remote client that connects to the server
- **ClientHandler**: Server-side handler for each individual client connection
- **GameInstance**: Manages a single game session between one client and the CPU

### Core Game Logic
- **Board**: Represents the 3x3 game board (cells 0-8)
- **CPUPlayer**: Implements computer strategy (greedy first-available-cell algorithm)
- **Player**: Abstract base class for game players
- **Game**: Basic local game controller (kept for testing, not used in network mode)

## How It Works

### Server Side
1. **GameServer** starts and listens on port 5555
2. When a **GameClient** connects:
   - **ClientHandler** is created for that client
   - **GameInstance** is created and runs in a dedicated thread
   - The handler manages message routing between client and game instance

### Game Flow
```
Client connects
    ↓
ClientHandler sends WELCOME message
    ↓
GameInstance.run() starts in separate thread
    ↓
Server sends GAME_START and initial board state
    ↓
[Game Loop]
  → Client sends MOVE:position
  → ClientHandler receives and calls game.submitMove()
  → GameInstance processes move
  → Server sends updated BOARD state
  → Computer plays if human move valid
  → Loop continues until win/draw/timeout
    ↓
Game ends with END message
    ↓
Threads clean up and client disconnects
```

## Communication Protocol

### Server → Client Messages
- `WELCOME:message` - Connection established
- `GAME_START:message` - Game beginning, role assignment
- `BOARD:cells|TURN:playerNum` - Board state and whose turn
- `ERROR:message` - Invalid move or action
- `END:message` - Game over with outcome

### Client → Server Messages
- `MOVE:position` - Submit move (position 0-8)
- `QUIT` - Disconnect and end game
- `DEBUG:message` - Debug information

## Building and Running

### Compile
```bash
mvn clean compile
```

### Package
```bash
mvn package
```

### Run Server
```bash
java -cp target/classes ose.bionix.pe.tttnet.GameServer
```

Or use Main class:
```bash
java -cp target/classes ose.bionix.pe.tttnet.Main server
```

### Run Client
```bash
java -cp target/classes ose.bionix.pe.tttnet.GameClient [host] [port]
```

Or use Main class:
```bash
java -cp target/classes ose.bionix.pe.tttnet.Main client
```

### Example Session
Terminal 1 (Server):
```bash
$ java -cp target/classes ose.bionix.pe.tttnet.GameServer
[GameServer] Started on port 5555
[GameServer] Waiting for client connections...
```

Terminal 2 (Client):
```bash
$ java -cp target/classes ose.bionix.pe.tttnet.GameClient
[Client] Connected to server at localhost:5555
[Server] You are connected to the game server
```

## Threading Model

- **Main Server Thread**: Accepts connections and spawns client handlers
- **Client Handler Thread** (per client): Listens for incoming moves
- **Game Instance Thread** (per game): Runs game loop and broadcasts state

Each client connection spawns two threads:
1. Message listener (in ClientHandler.run())
2. Game loop (GameInstance.run())

Both threads synchronize through a **BlockingQueue** for moves and through synchronized output stream access.

## Game Rules

- Human player is X (always goes first)
- Computer is O
- Board positions are numbered 1-9 (user interface) but 0-8 internally
- Computer uses simple greedy strategy: takes first available cell
- Game ends on:
  - Win (3 in a row)
  - Draw (board full)
  - Timeout (60 seconds without move)
  - Disconnect

## Key Improvements from Original Code

1. **Fixed Threading**: Proper synchronization between listener and game threads
2. **Better Error Handling**: Timeouts, I/O error recovery
3. **Clear Protocol**: Standardized message format for all communications
4. **Improved Logging**: Debug output for troubleshooting
5. **Resource Cleanup**: Proper shutdown of threads and sockets
6. **Code Documentation**: Comprehensive JavaDoc and inline comments

## Known Limitations

- CPU uses basic greedy algorithm (no AI strategy)
- Single server instance (no clustering)
- No persistent game history
- No authentication
- No game lobby/matchmaking
