const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("msrpg", {
    getServerInfo: () => ipcRenderer.invoke("get-server-info")
});