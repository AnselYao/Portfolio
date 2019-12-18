import msg,getIp
import time,datetime
from tkinter import *

class client:
    ip = ""
    name = ""
    sendlist=[]
    recvlist=[]
    IPlist=[] # TODO 发出join消息后，由收发模块处理网络上的节点信息，加入到IPlist中
    src=0
    in_num=0
    out_num=0
    def __init__(self,name):
        self.name = name
        self.ip = getIp.getIp()
        self.IPlist.append(self.ip)
        #self.sysJoin()

    # 1 开启该出入口：加入系统
    def sysJoin(self):
        mJoin = msg.msg(self.ip)
        mJoin.to_Join()
        self.sendlist.append(mJoin) # 发送加入系统消息
        time.sleep(1)
        if len(self.IPlist) == 1:
            self.src = 60 # 如果我是第一个节点，就给我所有资源
        else:
            self.inviteTender()# 初始src分配：招标
        refresh()

    # 2 关闭该出入口：退出系统
    def sysLeave(self):
        mLeave = msg.msg(self.ip)
        #mLeave.to_Leave(self.src,self.IPlist[1]) # 本地测试时需要注释此条，因为不允许所有节点都关闭
        self.src = 0
        self.sendlist.append(mLeave)
        refresh()

    # 3 处理车辆入库
    def carIn(self):
        while 1:
            if self.src > 0:
                self.in_num += 1
                self.src -= 1
                refresh()
                return 0
            else:
                # 招标
                self.inviteTender()

    # 4 处理车辆出库
    def carOut(self):
        self.out_num += 1
        self.src += 1
        refresh()

    # 5 投标
    def tender(self, mInv):
        if self.src > mInv.least_src:
            id = mInv.tender_ID
            mTndr = msg.msg(self.ip)
            mTndr.to_Tender(mInv.tender_ID,mInv.fromIP)
            self.sendlist.append(mTndr) # 发送投标消息
            time.sleep(1) # 等待1秒

            for i in range(len(self.recvlist)):
                if (self.recvlist[i].tender_ID == id) and (self.recvlist[i].msg_type == "win"):
                    srcSend = self.src/2 # 发送一定数量的资源给招标节点 TODO 确定一个函数    
                    if (self.src > srcSend) and (self.src > mInv.least_src): # 防止同时中标两次，发完一次后自己资源不足
                        mSrc = msg.msg(self.ip)
                        mSrc.to_send_src(srcSend, mInv.fromIP)
                        self.sendlist.append(mSrc)
                        self.src = self.src - srcSend
                    else:
                        mSrc = msg.msg(self.ip)
                        mSrc.to_send_src(0, mInv.fromIP)
                        self.sendlist.append(mSrc)
                        # 不足就发0
                    self.recvlist.pop(i)
                    break
                # 发现自己中标就发送资源，没中就算了
        self.recvlist.remove(mInv)
        refresh()

    # 6 招标
    def inviteTender(self):
        mInv = msg.msg(self.ip)
        id = datetime.datetime.now()
        mInv.to_IFB(id) # tender_ID为当前时间
        self.sendlist.append(mInv)

        time.sleep(1) # 等待1秒
        tenderlist = []
        for i in range(len(self.recvlist)):
            if (self.recvlist[i].tender_ID == id) and (self.recvlist[i].msg_type == "tender"):
                tenderlist.append(self.recvlist[i])
                self.recvlist.pop(i) # 从消息接收列表中把对应本次招标的投标消息都拿走
        
        win = 0 # 挑选一个幸运儿
        n = len(tenderlist)
        if n != 0:
            for i in range(n):
                win = win + tenderlist[i].tender_num
            win = win % n # 选择一个中标节点
        else:
            return 0
            # 没人投标，gg
        mWin = msg.msg(self.ip)
        mWin.to_tell_win(ip,tenderlist[win].fromIP)
        self.sendlist.append(mWin) # 发送中标消息；mRecv接收资源消息

        time.sleep(1) # 等待1秒
        for i in range(len(self.recvlist)):
            if (self.recvlist[i].tender_ID == id) and (self.recvlist[i].msg_type == "src"):
                self.src = self.src + self.recvlist[i].src # 拿到车位
                self.recvlist.pop(i)
                refresh()
                break

    # 0 本地资源信息
    def getSrc(self):
        return self.src

    # -1 消息处理 "join""leave""IFB""tender""win""src"
    def recv(self, mRecv):
        while 1:
            if len(self.recvlist) != 0:
                if self.recvlist[0].msg_type == "join":
                    self.IPlist.append(self.recvlist[0].fromIP)
                    self.recvlist.pop(0)

                if self.recvlist[0].msg_type == "leave":
                    self.src += self.recvlist[0].src
                    self.IPlist.remove(self.recvlist[0].fromIP)
                    self.recvlist.pop(0)

                if self.recvlist[0].msg_type == "IFB": # 收到招标消息
                    if datetime.datetime.now() - self.recvlist[0].tender_ID < datetime.timedelta(seconds=1):
                        self.tender(self.recvlist[0]) # TODO 开一条线程
                    self.recvlist.pop(0)
                    
                if self.recvlist[0].msg_type == "tender":
                    if datetime.datetime.now() - self.recvlist[0].tender_ID < datetime.timedelta(seconds=1.5):
                        if len(self.recvlist) != 1:
                            self.recvlist.append(self.recvlist[0])
                            self.recvlist.pop(0)
                    else:
                        self.recvlist.pop(0)

                if (self.recvlist[0].msg_type == "win"): 
                    # 收到中标消息：未超时移到list尾部等待处理，超时了就删除
                    if datetime.datetime.now() - self.recvlist[0].tender_ID < datetime.timedelta(seconds=2):
                        if len(self.recvlist) != 1:
                            self.recvlist.append(self.recvlist[0])
                            self.recvlist.pop(0)
                    else:
                        self.recvlist.pop(0)

                if self.recvlist[0].msg_type == "src":    
                    if datetime.datetime.now() - self.recvlist[0].tender_ID < datetime.timedelta(seconds=2.5):
                        if len(self.recvlist) != 1:
                            self.recvlist.append(self.recvlist[0])
                            self.recvlist.pop(0)
                    else:
                        self.recvlist.pop(0)
            # 如果接收消息列表不为空，则读取第一条消息，处理完成后删除该条消息的操作由消息处理函数处理
        

m = client("yaoyuan") # 本节点上线了

# GUI
root= Tk()
root.title("Parking System")
root.geometry('240x240')

label_0 = Label(root, text="src")
label_0.pack(side = LEFT)
label = Label(root, text=m.getSrc())
label.pack(side = LEFT)
def refresh():
    label["text"] = m.getSrc()

btnJoin = Button(root, text ="开启出入口", command = m.sysJoin)
btnQuit = Button(root, text ="关闭出入口", command = m.sysLeave)
btnCarIn = Button(root, text ="车辆入库", command = m.carIn)
btnCarOut = Button(root, text ="车辆出库", command = m.carOut)

btnJoin.pack()
btnQuit.pack()
btnCarIn.pack()
btnCarOut.pack()
root.mainloop()