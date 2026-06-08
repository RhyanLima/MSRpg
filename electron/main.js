const { app, BrowserWindow, ipcMain } = require('electron');
const { spawn } = require('child_process');
const path = require('path');

let mainWindow = null;
let javaProcess = null;
let serverInfo = null;

const isDev = !app.isPackaged;

async function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    minWidth: 1024,
    minHeight: 720,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    }
  });

  await mainWindow.loadFile(path.join(__dirname, 'splash.html'));

  if (isDev) {
    serverInfo = {
      port: 8080,
      token: ''
    };

    await mainWindow.loadURL('http://localhost:4200');
    mainWindow.webContents.openDevTools();
    return;
  }

  javaProcess = startJavaBackend();
  serverInfo = await readServerInfo(javaProcess.stdout);

  const indexPath = path.join(process.resourcesPath, 'frontend', 'browser', 'index.html');
  await mainWindow.loadFile(indexPath);
}

function startJavaBackend() {
  const javaExecutable = resolveJavaExecutable();
  const jarPath = path.join(process.resourcesPath, 'backend', 'msrpg-backend.jar');

  const child = spawn(javaExecutable, ['-jar', jarPath], {
    stdio: ['ignore', 'pipe', 'pipe']
  });

  child.stderr.on('data', data => {
    console.error(`[java:stderr] ${data.toString()}`);
  });

  child.on('exit', code => {
    console.log(`Java backend exited with code ${code}`);
  });

  return child;
}

function resolveJavaExecutable() {
  if (process.platform === 'win32') {
    return path.join(process.resourcesPath, 'jre', 'bin', 'java.exe');
  }

  return path.join(process.resourcesPath, 'jre', 'bin', 'java');
}

function readServerInfo(stdout) {
  return new Promise((resolve, reject) => {
    let port = null;
    let token = null;

    const timeout = setTimeout(() => {
      reject(new Error('Timeout waiting for Java backend startup'));
    }, 30000);

    stdout.on('data', data => {
      const text = data.toString();

      const portMatch = text.match(/SERVER_PORT=(\d+)/);
      const tokenMatch = text.match(/SESSION_TOKEN=([a-zA-Z0-9-]+)/);

      if (portMatch) {
        port = Number(portMatch[1]);
      }

      if (tokenMatch) {
        token = tokenMatch[1];
      }

      if (port && token) {
        clearTimeout(timeout);
        resolve({ port, token });
      }
    });
  });
}

ipcMain.handle('get-server-info', () => serverInfo);

app.whenReady().then(createWindow);

app.on('before-quit', () => {
  if (javaProcess) {
    javaProcess.kill();
  }
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});