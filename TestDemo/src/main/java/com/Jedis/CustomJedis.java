package com.Jedis;

import redis.clients.jedis.Jedis;

public class CustomJedis {

    SocketClient socket;
    CustomJedis(String IP,int port){
        socket=new SocketClient(IP,port);
    }
    public String set(String key, String value){
        socket.send(commandUtil(Resp.command.SET,key.getBytes(),value.getBytes()));
        return socket.read();
    }


    public String get(String key){
        socket.send(commandUtil(Resp.command.GET,key.getBytes()));
        return socket.read();
    }


    public String incr(String key){
        socket.send(commandUtil(Resp.command.INCR,key.getBytes()));
        return socket.read();
    }



    public static String commandUtil(Resp.command command,byte[]... bytes){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(Resp.star).append(1+bytes.length).append(Resp.line);
        stringBuilder.append(Resp.StringLength).append(command.toString().length()).append(Resp.line);
        stringBuilder.append(command.toString()).append(Resp.line);
        for (byte[] aByte : bytes) {
            stringBuilder.append(Resp.StringLength).append(aByte.length).append(Resp.line);
            stringBuilder.append(new String(aByte)).append(Resp.line);
        }
        return stringBuilder.toString();
    }


    public static void main(String[] args) {

        /**
         * 127.0.0.1:9999 这个地址不是真正的redisServer，只是起了一个socketServer服务，目的是为了看jedis发送给
         * redisServer的语法格式
         */
        Jedis redisClient=new Jedis("127.0.0.1",9999);
        /**  这是执行  redisClient.set("test", "123456")，jedis发送给redisServer的数据格式
         *     *3   代表几组命令，一共三组
               $3   发送的命令的长度
               SET  发送的命令
               $4   发送的key的长度
              test  发送给key
              $6    发送的value的长度
              123456   发送的value
         */
        System.out.println(redisClient.set("test", "123456"));
        System.out.println(redisClient.get("test"));
        System.out.println(redisClient.incr("lock"));
        redisClient.close();


        // 自定义 jedis
        CustomJedis jedis=new CustomJedis("127.0.0.1",9999);
        System.out.println(jedis.set("taibai2", "123456"));
        System.out.println(jedis.set("test", "123456"));
        System.out.println(jedis.get("test"));
        System.out.println(jedis.incr("lock"));

    }

}
