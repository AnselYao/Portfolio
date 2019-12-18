#!/usr/bin/python3
# -*- coding: UTF-8 -*-
import json
import random

class msg():
    msg_type=""
    fromIP=""
    disIP=""
    port=8899
    src=0
    least_src=0
    tender_num=0
    tender_ID=0
    __tendernum = [1,2,3,4,5,6,7,8,9,10]
    def __init__(self,fromIP,port=8899):
        self.fromIP=fromIP
        self.port=port
    #广播加入消息
    def to_Join(self):
        self.msg_type="join"
    #单播离开消息 附赠自己剩余的资源
    def to_Leave(self,src,disIP):
        self.msg_type="leave"
        self.disIP=disIP
        self.src=src
    #广播招标消息 附上招标最低资源数 要求自身资源大于least_src才参与招标 默认为0 
    def to_IFB(self,tender_ID,least_src=0):
        self.msg_type="IFB"
        self.least_src=least_src
        self.tender_ID=tender_ID
    #发送投标消息 附上标号 标号取自一个随机数组 
    def to_Tender(self,tender_ID,disIP):
        self.msg_type="tender"
        self.tender_num=random.sample(self.__tendernum, 1)[0]
        self.tender_ID=tender_ID
        self.disIP=disIP
    #告知中标人
    def to_tell_win(self,tender_ID,disIP):
        self.msg_type="win"
        self.tender_ID=tender_ID
        self.disIP=disIP        
    #中标后发送资源 也可用于自身资源过多 分发资源
    def to_send_src(self,src,disIP):
        self.msg_type="src"
        self.src=src
        self.disIP=disIP
    def to_json(self):
        temp_json=json.dumps(self, default=lambda self: self.__dict__, sort_keys=True, indent=4)
        return temp_json
# m = msg("127.0.0.1")
# m.to_Tender()
# #转json 
# myjson=m.to_json()
# print(myjson)

