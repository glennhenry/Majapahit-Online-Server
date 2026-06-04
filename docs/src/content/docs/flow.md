---
title: Flow
slug: flow
description: Flow
---

This page should contain the game flow from preloader to in-game.

As of now, we are still stuck in the game initialization part.

### Loading

1. The website uses `swfobject.js` to load the `Preloader.swf`.
2. `Preloader.swf` do necessary security and URLs setup, then download the `MOKV.swf`.
3. Once preloader finishes, `MOKV.swf` gets into the frame and replaces the preloader.

### Beginning Assets Download

4. `MOKV.swf` download essential assets in the beginning, this includes `effectjson.txt`, `tutorialjson.txt`, and many sounds as well as item effects images.

### Authentication

5. User may register/login to the game. Upon such operation, the game will download the `flashbridge/1` from the `api.playerio.com`. This `flashbridge` is PlayerIO's utility to bridges between the SWF game client with the PlayerIO backend. It contains PlayerIO client core library, such as the register and login functionality.
6. Register attempt will request to API 403 (`SimpleRegister`) in the server, and login will request to API 400 (`SimpleConnect`). Our local server needs to respond to both API request (i.e., the route `127.0.0.1:8080/api/403`).
7. Input of registration includes (most importantly): username, email, and password.
8. Output is (most importantly): `token` and `userId`. `token` is not anything complex, it's solely secure `String` used for auth. `userId` is a unique user DB identifier. Last but not least, ensure that `gameFSRedirectMap` is a non-empty String, because Protobuf serialization may encode empty string into nothing, hence the encounter of null value in the client-side.
9. The game makes an extra GET request to `/register`, providing email and password. This request is safe to ignore.

### Room Joining

10. Afterward, the game make request to API 21 of `CreateRoom`. We assumed that this tells the server to create a room for the requesting client. Expected output of this API is a String of `roomId`.
11. Rightly after, API 24 of `JoinRoom` is requested. Failing to respond to this will disconnect the game with the server. Expected response is server's socket endpoint and the room join key.
12. In this point, the game also make request to API 30 of `ListRooms`. This request is **repeatedly** invoked by `MOKV.as` for every second. Expected output is the list of rooms available on server (with the `RoomInfo` definition). This repeated API 30 requests are intentionally triggered to update the list of online players (rooms).

:::note
PlayerIO's room work as an instance of server dedicated to serve players. A player connecting to the game, playing alone, will be connected to a room of their own, where the `roomId` is their own username. In a possible multiplayer scenario (maybe a real-time PVP), server may maintain separate room for the two players to join.
:::

### Socket Connection Start

13. A successful `JoinRoom` response on API server leads to the creation of `Connection` class, which is done in the PlayerIO bridge. The client will establish a socket connection to the given endpoint of the API response.
14. The connection to socket begins with an implicit cross policy file request (automatic by flash's socket) that the server must respond to allow the access. The client will disconnect after getting a response.

### PlayerIO Handshake

The client reconnects after the policy response. From now on, message exchange will follow the PlayerIO's binary serialization (see `BinarySerializer.as` in client-side).

This part is specifically done by the `doConnect` callback of `Multiplayer.as` and the constructor of `Connection` class. It consists of PlayerIO handshake which still happen in the PlayerIO bridge.

15. The client reconnect and send a PlayerIO `join` message, including a join key (from API 24 response), and username. Server should validate this message before allowing them to join.
16. Server should respond with `playerio.joinresult` and a boolean true to indicate successful join.
17. At this point, the handshake is basically done, and the focus exits from the PIO bridge and back to `MOKV.as`.

### Game Initialization

18. After receiving `playerio.joinresult`, the client registers message handlers for the following message types:

- `finishLoad`
- `getAnnouncement`
- `buddyInvite`
- `getWhisper`
- `dailyReward`
- `loadData`

At this point, no further messages are sent by the client. Communication appears to pause, so we assumed that the client is waiting for the server to initiate the next step.

We interpret this as the client waiting for the server to indicate readiness before proceeding with data loading and any subsequent requests.

### Data Loading

We thought that `loadData` is the most essential message and a good one to start. When the server sends `loadData` message, the client will reply by requesting to API 103 `LoadMyPlayerObject`. This is supposed to load the tutorial's data and a subsequent request to API 85 `LoadObjects` will be made after, which should load the player's core data.

19. Server sends `loadData`.
20. Client reply by requesting to API 103, providing the hex `0x08 0x00` (don't appear to be meaningful).

:::caution
This part is somewhat weird, the client doesn't send any information like `playerId` or `username`. This is pretty unclear, and we don't know what how to reliably progress. For now, we send new tutorial data (with all states set to 0) every time to anyone.

It's possible to have a temporary storage to store the identified `playerId` on `join` message, then use it to load tutorial's data, but this is a very fragile and risky solution.
:::

21. Client assigns the `tutorialData` response of API 103 to `playerData`, then request to API 85.
22. Various `playerData` fields are sent by server. Once load is success, the client assigns them all locally.
23. Other loading parts are: `payVault.refresh`, `initMergeData`, `revertConnectionEvent`, `checkCharacterData`, `advanceToMainMenu`, `mainGame.initScreen`, `loadAvatar`, `loadAction`.
