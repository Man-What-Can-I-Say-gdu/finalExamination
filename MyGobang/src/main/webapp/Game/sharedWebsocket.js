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
//将数据发送给后端
function sendToBack(data){
    backWs.send(data)
}
//将数据发送给中继服务器
function sendToMiddle(data){
    middleWs.send(data)
}
//处理前端发送的信息
function handleMsgFromFrontToMiddle(event) {
    if(typeof event.data === 'string') {
        //此处的data是包含两种格式，一种是简单指令，一种是指令加数据
        infoParts = event.data.data.split('\\.')
        if (infoParts.length === 1) {
            //前端发送的简单指令，只需要将指令内容直接转发给服务器
            middleWs.send(event.data.data)
        } else if (infoParts.length === 2) {
            //指令加数据,直接将数据发送后端服务器
            sendToMiddle(event.data.data);
        }
    }else{
        //二进制数据，不进行解析直接发送
        sendToMiddle(event.data.data)
    }
}
//处理中继服务器发送到前端的消息
function handleMsgFromMiddleToFront(event){
    if(typeof event.data.data === 'string'){
        //中继服务器发送消息，数据由region和data两部分组成，data包含两部分，可能存在数据（用'.'分隔），需要先进行解读
        //此处的data是包含两种格式，一种是简单指令，一种是指令加数据
        infoParts = event.data.data.split('\\.')
        if (infoParts.length === 1) {
            switch (event.data.data){
                case "connectSuccess":
                    broadcast("middleServerConnectSuccess")
                    break;
                case ("opponentSurrender" || "opponentLost" || "opponentVictory" || "joinInSuccess" || "OwnerExit" || "GuestOutOfRoom"):
                    //这些指令只需要传递给前端和后端
                    sendToBack(event.data.data)
                    broadcast({type:'middle-message' , region:event.data.region , data:even.data.data})
                    break;
                case "updateRoomFalse" || "joinInNotice" || "opponentWithdraw":
                    //收到的这些指令用于后端的数据更新
                    sendToBack(event.data.data)
                    break;
            }
        } else if (infoParts.length === 2) {
            switch (infoParts[0]) {
                case "startGame" || "userData" || "roomInfo":
                    //更新数据并传递给前端进行展示
                    broadcast({type:'middle-message' , region:event.data.region , data:event.data.data})
                    sendToBack(event.data.data)
                    break;
            }
        }
    }else{
        //二进制数据直接发送给也买你和后端
        sendToBack(event.data.data);
        broadcast(event.data.data)
    }
}
//处理前端发送给后端的消息
function handleMsgFromFrontToBack(event) {
    if(typeof event.data.data === 'string'){
        infoParts = event.data.split('\\.')
        if (infoParts.length === 1) {
            switch (event.data){
                case "connectSuccess":
                    broadcast("connectSuccess")
                    break;
                case  "opponentSurrender" || "opponentLost" || "opponentVictory" || "ownerExitRoom" || "guestExitRoom" || "lost" || "victory" || "withdraw":
                    broadcast(event.data.data)
                    sendToBack(event.data.data)
                    break;
                case "updateRoomFalse":
                    broadcast(event.data.data)
                    break;
            }
        } else if (infoParts.length === 2) {
            switch (infoParts[0]) {
                case "startGame" || "roomInfo" || "joinInNotice" ||"joinInSuccess" || "opponentWithdraw" || "buildRoom" || "joinRoom" || "updateRoom" || "chessing":
                    sendToBack(event.data.data)
                    break;
            }
        }
    }else{
        sendToBack(event.data.data)
    }
}
//处理后端发送给前端的消息
function handleMsgFromBackToFront(event) {
    if(typeof event.data.data === 'string'){
        infoParts = event.data.split('\\.')
        if (infoParts.length === 1) {
            switch (event.data.data){
                case "RoomInfoError" || "GuestIdError" || "ChessingError":
                    broadcast({type:'back-message' , region:event.data.region , data:even.data.data})

                    break;
            }
        }
    }else{
        sendToMiddle(event.data.data);
    }
}

//页面链接时触发
self.onconnect = (e) => {
    const port = e.ports[0];
    //监听页面发送的消息
    port.onmessage = (event) => {
        if (event.data.region === "front") {
            //处理前端页面发送的数据
            if (event.data.type === "connect-middle") {
                // 如果 middleWs和backWs 未建立，则创建
                if (!middleWs) {
                    middleWs = new WebSocket(event.data.URL);
                    middleWs.onopen = () => {
                        broadcast({type: 'status', data: 'connected'})
                    }
                    // WebSocket 收到消息时，广播给所有页面
                    middleWs.onmessage = (msg) => {
                        handleMsgFromMiddleToFront(event);
                    };
                    middleWs.onclose = () => {
                        broadcast({type: 'status', data: 'disconnected'});
                        middleWs = null; // 清理
                    };
                    middleWs.onerror = (error) => {
                        broadcast({type: 'error', data: error.message});
                    };
                }
            } else if (event.data.type === "middle-message") {
                // 页面发送消息到 WebSocket
                if (middleWs && middleWs.readyState === WebSocket.OPEN) {
                    handleMsgFromFrontToMiddle(event);
                }
            } else if (event.data.type === "connect-back") {
                // 如果backWs 未建立，则创建
                if (!backWs) {
                    backWs = new WebSocket(event.data.URL);
                    backWs.onopen = () => {
                        broadcast({type: 'status', data: 'connected'})
                    }
                    // WebSocket 收到消息时,判断消息的类型并处理转发
                    backWs.onmessage = (msg) => {
                        handleMsgFromBackToFront(event);
                    };
                    backWs.onclose = () => {
                        broadcast({type: 'status', data: 'disconnected'});
                        backWs = null; // 清理
                    };
                    backWs.onerror = (error) => {
                        broadcast({type: 'error', data: error.message});
                    };
                }
            } else if (event.data.type === "back-message") {
                // 页面发送消息到 WebSocket
                if (backWs && backWs.readyState === backWs.OPEN) {
                    handleMsgFromFrontToBack(event);
                }
            }
        } else {
            handleMsgFromBackToFront(event);
        }
    };
//页面关闭时，移除端口
    port.onclose = () => {
        ports.delete(port);
        // 如果没有页面连接了，关闭 WebSocket
        if (ports.size === 0 && ws) {
            middleWs.close();
            backWs.close();
        }
    };
}