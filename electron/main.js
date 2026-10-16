const { app, BrowserWindow, ipcMain, protocol, net } = require('electron');
const { spawn } = require('child_process');
const { pathToFileURL } = require('url');
const fs = require('fs');
const path = require('path');

// O Angular é servido por um protocolo próprio (app://msrpg) em produção. Com file://
// o <base href="/"> resolve os assets na raiz do disco e o roteador não casa nenhuma rota
// (tela em branco), além de o Origin ser "null", bloqueado pelo CORS do backend.
const APP_SCHEME = 'app';
const APP_HOST = 'msrpg';

protocol.registerSchemesAsPrivileged([
  { scheme: APP_SCHEME, privileges: { standard: true, secure: true, supportFetchAPI: true } }
]);

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

  registerAppProtocol();
  javaProcess = startJavaBackend();
  javaProcess.on('error', showStartupError);
  serverInfo = await readServerInfo(javaProcess.stdout);

  await mainWindow.loadURL(`${APP_SCHEME}://${APP_HOST}/`);
}

function registerAppProtocol() {
  const root = path.join(process.resourcesPath, 'frontend', 'browser');
  const indexPath = path.join(root, 'index.html');

  protocol.handle(APP_SCHEME, request => {
    const { pathname } = new URL(request.url);
    const requested = path.normalize(path.join(root, decodeURIComponent(pathname)));

    const insideRoot = requested === root || requested.startsWith(root + path.sep);
    const isFile = insideRoot && fs.existsSync(requested) && fs.statSync(requested).isFile();

    // Rotas do Angular (ex.: /auth/login) não são arquivos: devolve o index.html.
    return net.fetch(pathToFileURL(isFile ? requested : indexPath).toString());
  });
}

function showStartupError(error) {
  console.error('[startup]', error);
  if (!mainWindow || mainWindow.isDestroyed()) {
    return;
  }
  const message = String(error && error.message ? error.message : error)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;');
  const html = `<body style="background:#0a0806;color:#f5ead6;font-family:serif;padding:2rem">
    <h2>MSRpg não pôde ser iniciado</h2><p>${message}</p></body>`;
  mainWindow.loadURL(`data:text/html;charset=utf-8,${encodeURIComponent(html)}`);
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
    let buffer = '';

    const timeout = setTimeout(() => {
      reject(new Error('Timeout waiting for Java backend startup'));
    }, 30000);

    stdout.once('close', () => {
      clearTimeout(timeout);
      reject(new Error('O backend Java encerrou antes de ficar pronto.'));
    });

    stdout.on('data', data => {
      buffer += data.toString();

      const portMatch = buffer.match(/SERVER_PORT=(\d+)/);
      const tokenMatch = buffer.match(/SESSION_TOKEN=([a-zA-Z0-9-]+)/);

      if (portMatch && !port) {
        port = Number(portMatch[1]);
      }

      if (tokenMatch && !token) {
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

app.whenReady().then(() => createWindow().catch(showStartupError));

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