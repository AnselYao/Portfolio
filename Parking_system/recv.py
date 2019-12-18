#coding:utf-8
 
import socket,traceback
 
host = ''
port = 8899
 
s =socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
s.setsockopt(socket.SOL_SOCKET,socket.SO_BROADCAST,1)
s.bind((host,port))
 
while 1:
    try:
        message,addr = s.recvfrom(8192)
        print ("Got data from " ,addr)
        #addr里的port不是接受者要求的port  我们的port统一就好了
        s.sendto("I am here".encode(),addr)
    except (KeyboardInterrupt,SystemExit):
        raise
    except:
        traceback.print_exc()