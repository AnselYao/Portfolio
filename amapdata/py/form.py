import xlrd, xlwt
import getdata

data = xlrd.open_workbook('position.xlsx') # 打开输入文件
table = data.sheets()[0] # 打开第一张表
nrows = table.nrows # 获取表的行数
ncols = table.ncols 

wbk = xlwt.Workbook() # 新建输出表格
sheet = wbk.add_sheet('sheet 1')
sheet.write(0,0,'From') # 构造表头
sheet.write(0,1,'To')
sheet.write(0,2,'步行距离/km')
sheet.write(0,3,'步行时间/min')
sheet.write(0,4,'骑行距离/km')
sheet.write(0,5,'骑行时间/min')
sheet.write(0,6,'公交距离/km')
sheet.write(0,7,'公交时间/min')
sheet.write(0,8,'驾车距离/km')
sheet.write(0,9,'驾车时间/min')

lists = [[10,14],[10,15],[11,14],[11,15],[12,8],[12,9],[12,10],[12,11],[12,12],[12,13]]
index = 0
line=1 # 当前待写入待行号

while (index < 10):
    for i in range(nrows): 
        for j in range(ncols):
            x = lists[index][0]-1
            y = lists[index][1]-1
            if ((i<=7) and (i>=3) and (j<=3) and (j>=0))or((i==x)and(j==y)):
                continue
            else:
                sheet.write(line,0,'%d,%d'%(i+1,j+1)) # 出发地
                sheet.write(line,1,'%d,%d'%(x+1,y+1)) # 目的地
                for k in range(4): # 获取并打印四种出行方式的距离、时间
                    distance,duration=getdata.get(table.cell_value(i,j),table.cell_value(x,y),k)
                    sheet.write(line,2+k*2,round(int(distance)/1000,1))
                    sheet.write(line,2+k*2+1,round(int(duration)/60))
                line=line+1
                print(line)
    index=index+1

wbk.save('data_final.xls')