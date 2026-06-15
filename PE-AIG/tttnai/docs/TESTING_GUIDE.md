# TTTNet Quick Start & Testing Guide

## Quick Start

### Compilation
```bash
cd /workspaces/BioniCLN4/PE/tttnet
mvn clean compile
```

### Running in Separate Terminals

**Terminal 1 - Start Server:**
```bash
cd /workspaces/BioniCLN4/PE/tttnet
java -cp target/classes ose.bionix.pe.tttnet.Main server
```

Expected output:
```
[GameServer] Started on port 5555
[GameServer] Waiting for client connections...
```

**Terminal 2 - Start Client:**
```bash
cd /workspaces/BioniCLN4/PE/tttnet
java -cp target/classes ose.bionix.pe.tttnet.Main client
```

Expected output:
```
[Client] Connected to server at localhost:5555
[Server] You are connected to the game server
[Server] You are Player 1 (X), Computer is Player 2 (O)
```

## Game Play

### Sample Game Session

Client will see:
```
+---+---+---+
|   |   |   |
+---+---+---+
|   |   |   |
+---+---+---+
|   |   |   |
+---+---+---+

>>> YOUR TURN - Enter position [1-9]

> Enter position [1-9], [s]tatus, or [q]uit: 5
```

After you enter 5 (center), server processes and computer responds:
```
+---+---+---+
|   |   |   |
+---+---+---+
|   | X |   |
+---+---+---+
|   |   |   |
+---+---+---+

... COMPUTER IS THINKING ...

+---+---+---+
| O |   |   |
+---+---+---+
|   | X |   |
+---+---+---+
|   |   |   |
+---+---+---+

>>> YOUR TURN - Enter position [1-9]

> Enter position [1-9], [s]tatus, or [q]uit: 9
```

Continue playing until game ends:
```
=== GAME OVER ===
You won!
==================
```

### Client Commands

| Command | Action |
|---------|--------|
| `1-9` | Play move at that position |
| `s` | Show current board state |
| `q` | Quit game |

## Testing Multiple Clients

You can run multiple client terminals connecting to the same server. Each gets its own independent game:

```bash
# Terminal 1: Server
java -cp target/classes ose.bionix.pe.tttnet.Main server

# Terminal 2: Client 1
java -cp target/classes ose.bionix.pe.tttnet.Main client

# Terminal 3: Client 2
java -cp target/classes ose.bionix.pe.tttnet.Main client

# Terminal 4: Client 3
java -cp target/classes ose.bionix.pe.tttnet.Main client
```

Each client runs independently with its own game instance.

## Debugging

### Enable Verbose Output
Server output shows:
- Connection accepted
- Message received
- Game state changes
- Thread management

Example server output:
```
[GameServer] Client 1 connected from 127.0.0.1:54321
[ClientHandler-1] I/O streams initialized
[ClientHandler-1] GameInstance created
[ClientHandler-1] Game thread started
[ClientHandler-1] Received: MOVE:4
[GameInstance] Human played at position 4
[ClientHandler-1] Received: MOVE:8
```

### Connection Issues
- **"Connection refused"**: Server not running on port 5555
- **"Socket timeout"**: Network connectivity issue
- **"Game timeout"**: Player took more than 60 seconds to move

## Architecture Verification

The merged architecture ensures:

1. ✓ **Server Management**: GameServer handles multiple clients independently
2. ✓ **Client-Server Protocol**: Standardized message format
3. ✓ **Threading**: Proper synchronization between listener and game threads
4. ✓ **Game Logic**: GameInstance manages all game state and rules
5. ✓ **Resource Cleanup**: Proper socket and thread shutdown
6. ✓ **Error Handling**: Timeouts, I/O errors, invalid moves

## Common Issues & Solutions

### Issue: Game hangs after initial connection
- **Cause**: Thread synchronization issue
- **Solution**: Verify both listener and game threads are running

### Issue: Board doesn't update
- **Cause**: Message protocol mismatch
- **Solution**: Check console output for protocol errors

### Issue: Connection drops after move
- **Cause**: I/O stream flush issue
- **Solution**: Verify PrintWriter.flush() is called

### Issue: Multiple clients interfere
- **Cause**: Shared game instance
- **Solution**: Each client gets its own GameInstance (by design)

## Code Quality Checklist

- [x] Client-server architecture merged and functional
- [x] Threading model properly synchronized
- [x] Communication protocol standardized
- [x] Error handling for timeouts and disconnects
- [x] Resource cleanup on shutdown
- [x] Comprehensive logging for debugging
- [x] Code documentation with JavaDoc
- [x] Compilation without errors
