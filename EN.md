## FetchJS

**FetchJS** provides simple methods that allow you to make HTTP requests, send data, or download files directly from **KubeJS** scripts.

---

### Basic Usage

The `Fetch` method works similarly to the web-based `fetch` API.
It can be used to call APIs or send data to servers.

A common use case is for modpacks to retrieve official announcements,
or dynamically update configuration files from a GitHub repository.

```javascript
FetchJS.fetch("https://zenquotes.io/api/random", data => {
    event.player.tell(data);
});
```

The URL above returns a random quote — useful for testing.

---

The `Download` method is used for downloading files such as images or mods.

```javascript
FetchJS.download(
    "https://raw.githubusercontent.com/Tower-of-Sighs/SmartKeyPrompts/refs/heads/master/libs/SlashBladeResharped-1.20.1-1.3.40.jar",
    "mods/SlashBladeResharped-1.20.1-1.3.40.jar",
    progress => {
        event.player.displayClientMessage(Component.literal("Downloading " + Math.round(progress * 100) + "%"), true);
    }
);
```

In this example, the method downloads the *SlashBlade* mod directly into the `mods` folder
and displays real-time download progress to the player.

Downloading images is usually faster and more practical—
please, use it responsibly. 😄

---

### Advanced Usage

The examples above demonstrate the simplified usage.
For more complete control, you can use the extended versions of these methods:

```javascript
boolean fetch(String url,
              String method,
              Map<String, String> headers,
              String jsonBody,
              Map<String, String> formData,
              int timeoutMillis,
              Consumer<String> callback);

boolean download(String url, 
                 String path, 
                 Map<String, String> headers, 
                 int timeoutMillis, 
                 Consumer<Double> progressCallback);
```

You can safely pass `null` for unused parameters.
`timeoutMillis` defines the maximum time (in milliseconds) to wait before a connection fails —
for example, GitHub’s default limit is around **20,000 ms**.

In most cases, the simplified methods should meet your needs.
The extended versions have not been deeply tested, so use them carefully.

If you’re unsure how to use these parameters,
refer to the [**official Fetch API documentation**](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch).

---

### Notes

This mod is a bit experimental — who knows how long it’ll last.
Use it while it lives, and enjoy it while you can.