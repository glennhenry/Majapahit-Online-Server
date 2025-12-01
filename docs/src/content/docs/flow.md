---
title: Flow
slug: flow
description: Flow
---

This page should contain the game flow from preloader to in-game.

As of now, we are still stuck in the authentication part.

### Loading

1. The website uses `swfobject.js` to load the `Preloader.swf`.
2. `Preloader.swf` do necessary security and URLs setup, then download the `MOKV.swf`.
3. Once preloader finishes, `MOKV.swf` gets into the frame and replaces the preloader.

### Beginning Assets Download

4. `MOKV.swf` download essential assets in the beginning, this includes `effectjson.txt`, `tutorialjson.txt`, and many sounds as well as item effects images.

### Authentication

5. User may register/login to the game. Upon such operation, the game will download the `flashbridge/1` from the `api.playerio.com`. This `flashbridge` is assumed to be the PlayerIO's utility to bridges between the SWF game client with the PlayerIO backend. As for MOKV, they use this utility to register and login.
6. Register attempt will request to API 403 (`SimpleRegister`) in the server, and login will request to API 400 (`SimpleConnect`). Our local server needs to respond to both API request (i.e., the route `127.0.0.1:8080/api/403`).
