### About this project.. 

This will be translated in a moment..

### 5.3 基于高德API的驾车、公交、骑行、步行路径规划

#### 5.3.1 高德开放平台

高德开放平台提供包含地图、定位、导航、搜索、路径规划、室内地图等服务供合作伙伴和开发者使用。路线规划，即根据目的地、出发地以及路径策略设置，为用户量身设计出行方案。同时可结合实时交通，帮助用户绕开拥堵路段，提供更贴心、更人性化的出行体验。高德路线规划基于全面的路网信息，结合实时路况，在多端为用户提供准确的路线规划能力。高德开放平台提供了Android、iOS、Web Service等平台的API。权衡开发成本以及数据获取效率，我们选择Web Service API。

#### 5.3.2 数据获取

HTTP是一个基于TCP/IP通信协议来传递数据的标准，包括html文件、图像等，即是一个客户端和服务器端请求和应答的标准。在HTTP请求方法中，GET方法通常用于获取服务端数据。使用GET请求主要有两个优点：传递的数据量小，4kb左右；速度快，通常用于对安全性要求不高的请求。
高德Web Service API向开发者提供HTTP接口，开发者可通过这些接口使用各类型的地理数据服务，返回结果支持JSON和XML格式。通过使用GET请求，我们将接收到API的返回的JSON数据。我们将数据获取的核心功能封装在getdata中，对外提供get函数接口。解析JSON数据后，由get函数返回所需的路径距离与时间信息。

```python
def get(origin, destination, mode):  
  …  
  responses = requests.get(url,data)  
  result = json.loads(responses.text)  
  …  
```
