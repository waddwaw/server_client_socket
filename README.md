# server_client_socket
#适合手机客户度使用的 客户端方式连接服务器 和 作为消息服务器对设备提供服务 的socket 封装，采用byte[] 方式进行数据传输 ，支持数据回调，数据超时，
#支持异步发送任务，心跳机制

此版本为第一版可能有一些问题，还请把问题反馈到issues or 加入群570278493 寻找 万里无云

此外欢迎大家修改源码 我将第一时间审核代码并合进分支

使用方法

创建客户端链接
```java
                IConnectPolicy.ServerHost host = new IConnectPolicy.ServerHost();
                host.serverHost = "172.16.45.196";
                host.serverPort = 13911;
                List<IConnectPolicy.ServerHost> hosts = new ArrayList<>();
                hosts.add(host);
                ClientConnectPolicy policy = new ClientConnectPolicy(110, hosts, 1000 * 10, 10);
                TestRecvHander recvHander = new TestRecvHander();
                Transaction transaction = new Transaction();
                ConnectManager.getInstance().addClientConnect(policy, recvHander, transaction, new INetConnectListener() {
                    @Override
                    public void connectStatusChange(int serverID, boolean connected) {
                        Log.d("socket" ,"connectStatusChange:" + serverID  + "=====" + connected);
                    }

                    @Override
                    public void failedToConnect(int serverID, Exception e) {
                        Log.d("socket" ,"failedToConnect:" + serverID  + "=====" + e.toString());
                    }
                });

                
```
作为服务器启动
```java

               TestRecvHander recvHander = new TestRecvHander();
                Transaction transaction = new Transaction();
                ConnectManager.getInstance().addServerConnect(111, 14411, recvHander, transaction, new INetConnectListener(){

                    @Override
                    public void connectStatusChange(int serverID, boolean connected) {
                        Log.d("socket" ,"connectStatusChange:" + serverID  + "=====" + connected);
                    }

                    @Override
                    public void failedToConnect(int serverID, Exception e) {
                        Log.d("socket" ,"failedToConnect:" + serverID  + "=====" + e.toString());
                    }
                });

```
