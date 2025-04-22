//存储websocket连接
let middleWs = null;
let backWs = null;
const ports = new Set();

// 广播消息给所有连接的页面
function broadcast(message) {
    ports.forEach(port => {
        try {
            port.postMessage(message);
        } catch (e) {
            // 如果端口已关闭，从集合中移除
            ports.delete(port);
        }
    });
}

//页面链接时触发
onconnect = (e) =>{
    const middlePort = e.ports[0];
    ports.add(middlePort);
    const backPort = e.ports[1];
    ports.add(backPort);
    //监听页面发送的消息
    middlePort.onmessage = (event)=>{
        if (event.data.type === "connect") {
            // 如果 middleWs和backWs 未建立，则创建
            if (!middleWs) {
                middleWs = new WebSocket(event.data.middleServerURL);

                middleWs.onopen = () => {
                    broadcast({type:'status', data: 'connected'})
                }
                // WebSocket 收到消息时，广播给所有页面
                middleWs.onmessage = (msg) => {
                    broadcast({type: 'message', data:event.data})
                };
                middleWs.onclose = () => {
                    broadcast({ type: 'status', data: 'disconnected' });
                    middleWs = null; // 清理
                };
                ws.onerror = (error) => {
                    broadcast({ type: 'error', data: error.message });
                };
            }
        } else if (event.data.type === "sendToMiddle") {
            // 页面发送消息到 WebSocket
            if (middleWs && middleWs.readyState === WebSocket.OPEN) {
                middleWs.send(event.data.payload);
            }
        }
    };
//页面关闭时，移除端口
    middlePort.onclose = () => {
        ports.delete(middlePort);
        // 如果没有页面连接了，关闭 WebSocket
        if (ports.size === 0 && ws) {
            middleWs.close();
            backWs.close();
        }
    };
    middlePort.start();
    //监听页面发送的消息
    backPort.onmessage = (event)=>{
        if (event.data.type === "connect") {
            // 如果 middleWs和backWs 未建立，则创建
            if (!backWs) {
                backWs = new WebSocket(event.data.backServerURL);
                // WebSocket 收到消息时，广播给所有页面
                backWs.onmessage = (msg) => {
                    ports.forEach((p) => p.postMessage({ type: "message", data: msg.data }));
                };
                backWs.onclose = () => {
                    ports.forEach((p) => p.postMessage({ type: "close" }));
                    backWs = null; // 清理
                };
            }
        } else if (event.data.type === "sendToMiddle") {
            // 页面发送消息到 WebSocket
            if (backWs && backWs.readyState === WebSocket.OPEN) {
                backWs.send(event.data.payload);
            }
        }
    };
// 页面关闭时，移除端口
    backPort.onclose = () => {
        ports.delete(backPort);
        middleWs.close();
        backWs.close();
    };
    // 启动端口通信
    backPort.start();
};