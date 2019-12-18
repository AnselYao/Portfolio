#coding:utf-8
 
import socket,sys
#目标port 8899
dest = ('<broadcast>', 8899)

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.setsockopt(socket.SOL_SOCKET,socket.SO_BROADCAST,1)
#joinmsg的json
s.sendto("joinMsg".encode(),dest)
 
print ("looking for replies: press Ctrl + c to stop ")
while 1:
    (buf ,address) = s.recvfrom(2048)
    if not len(buf):
        break
    print("Revived from %s:%s" %(address ,buf))