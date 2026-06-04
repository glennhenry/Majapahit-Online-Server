---
title: Intro
slug: index
description: Intro
---

![Majapahit online gameplay](../../assets/mokv.jpg)

MOKV is made with [PlayerIO](https://playerio.com) backend service.

## Assets

To provide your own assets:

1. Obtain client files
2. Create static folder, and follow the structure at server code: `src/main/kotlin/game/api/routes/FileRoutes.kt`
3. Redirect as needed, see [Redirection](#redirection)

## Summary

See [flow](flow) for the game sequential process.

### GameFS

PlayerIO has virtual file system called [GameFS](https://playerio.com/features/gamefs/) that games can access to from network. Every game-related files and assets lives here.

The access to GameFS can be done via PlayerIO client (provided from PlayerIO SDK) from the game's SWF. You would need a `gameId`, a unique identifier given to developers upon registering an account in PlayerIO. We can also download files directly with HTTP requests, that is by knowing the PlayerIO CDN URL, or the back-facing cloud URL, the `gameId` to include in the path, and the path that leads to the file itself.

There are two known `gameId` of MOKV (usually each game only have one):

- `mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw`
- `mo-kshatriya-varna-pghdt52hrue7uvjdoioika`

If the PlayerIO CDN is

- `http://cdn.playerio.com` and `http://r.playerio.com/r/`, then file directory would be in
- `http://cdn.playerio.com/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/`, and
- `http://r.playerio.com/r/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/`.

For example, the URL to the core file `MOKV.swf`:

- `http://r.playerio.com/r/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/MOKV.swf`

We can also use the back-facing cloud URL (the `r.playerio.com` typically limits request):

- `d1ro1du4c73r1c.cloudfront.net/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/MOKV.swf`

### MOKV Website

MOKV used to be hosted in its own website and Kongregate. Our private server decided to use the old MOKV website by Anantarupa.

### Redirection

MOKV uses `Preloader.swf` to setup security before loading the `MOKV.swf` game:

- reads `crossdomain.xml`.
- setup CDN URLs.
- allow CDN domain and hosts.

We should edit URLs in `Preloader.swf` and `MOKV.swf` to redirect it to our local server.

`Preloader.swf`:

1. `http://cdn.playerio.com/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/` -> `http://127.0.0.1:8080/game/`
2. `http://r.playerio.com/r/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/` -> `http://127.0.0.1:8080/game/`
3. `http://cdn.playerio.com/mo-kshatriya-varna-pghdt52hrue7uvjdoioika/` -> `http://127.0.0.1:8080/game/`
4. `http://r.playerio.com/r/mo-kshatriya-varna-pghdt52hrue7uvjdoioika/` -> `http://127.0.0.1:8080/game/`
5. `http://cdn.playerio.com/crossdomain.xml` -> `http://127.0.0.1:8080/crossdomain.xml`
6. `http://www.kongregate.com/flash/API_AS3_Local.swf` -> `http://127.0.0.1:8080/kong/API_AS3_Local.swf` (kong only)
7. `http://r.playerio.com/r/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/MOKV-Kongregate.swf` -> `http://127.0.0.1:8080/game/MOKV-Kongregate.swf` (kong only)
8. `http://r.playerio.com/r/mo-kshatriya-varna-gk7jceiwuk5cgydq4frqw/MOKV.swf` -> `http://127.0.0.1:8080/game/MOKV.swf`

The redirections for `MOKV.swf` follow the same pattern: any route to PlayerIO CDN or Anantarupa studio site is redirected to local host. Any files or assets related to the game itself are directed to the `/game` endpoint. Other assets, such as miscellaneous Flash files and cross-domain policy files, are located outside of the `/game` directory.

9. `http://mokv.anantarupa.com/sendEmail.php?email=some@email.com&username=player` -> `http://127.0.0.1:8080/register?email=some@email.com&username=player`.
10. `http://api.playerio.com/api` -> `http://127.0.0.1:8080/api`.

:::note
`MOKV-Kongregate.swf` and `API_AS3_Local.swf` aren't edited yet, this is because we don't use the Kong version. There is presumably little difference with the original version as their size don't differ by much. The loading screen
:::

Additionally for PlayerIO bridge `flashbridge/1.swf`:

11. `http://fb.playerio.com/fb/ + gameId + /_fb_quickconnect_oauth` -> `"http://127.0.0.1:8080/fb/_fb_quickconnect_oauth";` (Facebook auth which likely won't be used but redirected regardless).

### Debugging

Recommend to use native Flash browser like Basilisk instead of emulator like Ruffle.

Few ways to debug:

1. Use `console.log`: Import `flash.ExternalInterface` (optionally check if it's available), then call the console log function of browser to log client-side into browser.
2. Use AS3 trace: Require running the game from ADL (with fake `application.xml`) to enable redirecting the `trace` function of AS3 into terminal.
3. Use developer tools: In browser (e.g., Basilisk) to see the console or network tools.
4. Force error: Explicitly throw error (without catching it) or use PlayerIO `WriteError`. This forces the game to log the error to server.
5. Ctrl+`: The game offers console whose how-to-use hasn't been found out yet (TODO).

### PlayerIO Protocol

PlayerIO uses custom binary serializer/deserializer to exchange message inside socket connection. The relevant files are:

- `BinarySerializer.as`
- `Connection.as`
- `Message.as`

For API request/response, they are exchanged with ProtoBuf.
